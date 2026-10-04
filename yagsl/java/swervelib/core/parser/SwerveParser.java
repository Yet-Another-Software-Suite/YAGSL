package swervelib.core.parser;

import static org.wpilib.units.Units.Amps;
import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Inches;
import static org.wpilib.units.Units.MetersPerSecond;
import static org.wpilib.units.Units.Millisecond;
import static org.wpilib.units.Units.Radians;
import static org.wpilib.units.Units.RadiansPerSecond;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.RotationsPerSecond;

import org.wpilib.util.Pair;
import org.wpilib.math.controller.SimpleMotorFeedforward;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.LinearVelocity;
import org.wpilib.framework.RobotBase;
import io.avaje.jsonb.Jsonb;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.function.Supplier;
import swervelib.core.parser.json.DeviceJson.VENDOR;
import swervelib.core.parser.json.ModuleJson;
import swervelib.core.parser.json.PIDFPropertiesJson;
import swervelib.core.parser.json.PhysicalPropertiesJson;
import swervelib.core.parser.json.SwerveDriveJson;
import swervelib.core.parser.json.SwerveDriveJson.GyroAxis;
import swervelib.core.parser.json.modules.AngleGearingJson;
import swervelib.core.parser.json.modules.DriveGearingJson;
import yams.core.gearing.GearBox;
import yams.core.mechanisms.config.SwerveDriveConfig;
import yams.core.mechanisms.config.SwerveModuleConfig;
import yams.core.mechanisms.swerve.SwerveDrive;
import yams.core.mechanisms.swerve.SwerveModule;
import yams.core.motorcontrollers.SmartMotorController;
import yams.core.motorcontrollers.SmartMotorControllerConfig;
import yams.core.motorcontrollers.enums.ControlMode;
import yams.core.motorcontrollers.enums.MotorMode;
import yams.core.telemetry.enums.TelemetryVerbosity;
import yams.core.telemetry.SmartMotorControllerTelemetry;
import yams.core.telemetry.SmartMotorControllerTelemetryConfig;
import yams.core.telemetry.SwerveDriveTelemetryConfig;
import yams.core.telemetry.SwerveModuleTelemetryConfig;

/**
 * Helper class used to parse the JSON directory with specified configuration options.
 *
 * <p>This class is command-framework agnostic. Robot code should use {@code swervelib.commands2.SwerveParser} or
 * {@code swervelib.commands3.SwerveParser} to create a {@code SwerveDrive} bound to its command framework.
 */
public class SwerveParser {

  /**
   * Module number mapped to the JSON name.
   */
  private static final HashMap<String, Integer> moduleConfigs = new HashMap<>();
  /**
   * JSON reader. Uses the adapters avaje-jsonb generates for the {@code @Json} classes at compile time.
   */
  private static final Jsonb JSONB = Jsonb.builder().build();
  /**
   * Parsed swervedrive.json
   */
  public static SwerveDriveJson swerveDriveJson;
  /**
   * Parsed modules/pidfproperties.json
   */
  public static PIDFPropertiesJson pidfPropertiesJson;
  /**
   * Parsed modules/physicalproperties.json
   */
  public static PhysicalPropertiesJson physicalPropertiesJson;
  /**
   * Array holding the module jsons given in {@link SwerveDriveJson}.
   */
  public static ModuleJson[] moduleJsons;

  /**
   * Construct a swerve parser.
   */
  public SwerveParser() {
  }

  /**
   * Parses a swerve configuration directory and creates a {@link SwerveParser}
   * containing the parsed configuration.
   *
   * @param directory the directory containing the swerve configuration files
   * @return a {@link SwerveParser} containing the parsed configuration
   * @throws UncheckedIOException if the directory or any of its configuration
   *                              files
   *                              cannot be read
   */
  public static SwerveParser parse(File directory) {
    SwerveParser inst = new SwerveParser();

    try {
      parseDirectory(directory);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to parse swerve directory: " + directory, e);
    }

    return inst;
  }

  public static void parseDirectory(File directory) throws IOException {
    checkDirectory(directory);
    swerveDriveJson = readJson(new File(directory, "swervedrive.json"), SwerveDriveJson.class);
    var pidfFile = new File(directory, "modules/pidfproperties.json");
    var simPidfFile = new File(directory, "modules/pidfproperties_sim.json");
    if (simPidfFile.exists() && RobotBase.isSimulation()) {
      pidfFile = simPidfFile;
    }
    pidfPropertiesJson = readJson(pidfFile, PIDFPropertiesJson.class);
    physicalPropertiesJson = readJson(new File(directory, "modules/physicalproperties.json"),
        PhysicalPropertiesJson.class);
    moduleJsons = new ModuleJson[swerveDriveJson.modules.length];
    for (int i = 0; i < moduleJsons.length; i++) {
      moduleConfigs.put(swerveDriveJson.modules[i], i);
      File moduleFile = new File(directory, "modules/" + swerveDriveJson.modules[i]);
      assert moduleFile.exists();
      moduleJsons[i] = readJson(moduleFile, ModuleJson.class);
    }
  }

  /**
   * Read a JSON file into the given type with WPILib's JSON library (avaje-jsonb). Properties in the file that the
   * type doesn't have (e.g. {@code $schema}) are ignored.
   *
   * @param file JSON file to read.
   * @param type Type to read the file into.
   * @param <T>  Type to read the file into.
   * @return Parsed file.
   * @throws IOException If the file can't be read.
   */
  public static <T> T readJson(File file, Class<T> type) throws IOException {
    return JSONB.type(type).fromJson(Files.readString(file.toPath()));
  }

  /**
   * Check directory structure.
   *
   * @param directory JSON Configuration Directory
   */
  private static void checkDirectory(File directory) {
    assert new File(directory, "swervedrive.json").exists();
    assert new File(directory, "modules").exists() && new File(directory, "modules").isDirectory();
    assert new File(directory, "modules/pidfproperties.json").exists();
    assert new File(directory, "modules/physicalproperties.json").exists();
  }

  /**
   * Build the {@link SwerveModule}s and gyro from the parsed JSON configuration and apply them to the given
   * {@link SwerveDriveConfig}, collecting the raw vendor hardware devices created along the way. The command-framework
   * specific parsers create the {@code SwerveDrive} from the configured {@link SwerveDriveConfig} afterwards.
   *
   * @param swerveDriveConfig   {@link SwerveDriveConfig} to apply the modules, gyro, etc. to.
   * @param motorConfigSupplier Creates a new, empty {@link SmartMotorControllerConfig} bound to the swerve drive's
   *                            command-framework owner (Subsystem or Mechanism).
   * @param <C>                 {@link SmartMotorControllerConfig} type of the command framework.
   * @return {@link SwerveDriveHardware} containing the raw devices created for the swerve drive.
   */
  protected static <C extends SmartMotorControllerConfig<C>> SwerveDriveHardware buildSwerveDrive(
      SwerveDriveConfig<?> swerveDriveConfig, Supplier<C> motorConfigSupplier) {
    SwerveModule[] modules = new SwerveModule[swerveDriveJson.modules.length];
    SwerveModuleDevices[] moduleDevices = new SwerveModuleDevices[swerveDriveJson.modules.length];
    LinearVelocity maxModuleSpeed = null;

    for (int i = 0; i < modules.length; i++) {
      ModuleJson moduleJson = moduleJsons[i];

      ModuleGearings gearings = resolveGearings(moduleJson);

      C driveConfig = createDriveMotorConfig(motorConfigSupplier.get(), swerveDriveConfig, moduleJson, gearings.drive, i);

      C azimuthConfig = createAzimuthMotorConfig(motorConfigSupplier.get(), swerveDriveConfig, moduleJson,
          gearings.azimuth, i);

      ModuleHardware hardware = createModuleHardware(moduleJson, azimuthConfig, driveConfig, swerveDriveConfig);

      // Desaturate towards the slowest module so every module can reach its commanded speed.
      LinearVelocity moduleMaxSpeed = calculateMaxModuleSpeed(driveConfig, hardware.driveMotorController);
      if (maxModuleSpeed == null || moduleMaxSpeed.lt(maxModuleSpeed)) {
        maxModuleSpeed = moduleMaxSpeed;
      }

      // Automatic theorhetical feedforward for drive motors.
      if ((pidfPropertiesJson.drive.v) == 0) {
        var sff = new SimpleMotorFeedforward(
            pidfPropertiesJson.drive.s,
            12.0 / driveConfig.convertToMechanism(moduleMaxSpeed)
                .in(RotationsPerSecond),
            pidfPropertiesJson.drive.a);
        driveConfig.withFeedforward(sff);
        hardware.driveMotorController.setFeedforward(sff.getKs(), sff.getKv(), sff.getKa(), 0);
      }

      modules[i] = createSwerveModule(
          moduleJson,
          hardware,
          i,
          swerveDriveConfig);

      moduleDevices[i] = new SwerveModuleDevices(
          hardware.driveMotorController.getMotorController(),
          hardware.azimuthMotorController.getMotorController(),
          hardware.absoluteEncoder.getSecond());
    }

    Object gyroDevice = configureSwerveDrive(
        swerveDriveConfig,
        modules,
        maxModuleSpeed);

    return new SwerveDriveHardware(gyroDevice, moduleDevices);
  }

  private static ModuleGearings resolveGearings(ModuleJson moduleJson) {
    var driveGearing = physicalPropertiesJson.gearing.drive;
    var azimuthGearing = physicalPropertiesJson.gearing.angle;

    if (moduleJson.gearing.drive.gearRatio != 0) {
      driveGearing = moduleJson.gearing.drive;
    }

    if (moduleJson.gearing.angle.gearRatio != 0) {
      azimuthGearing = moduleJson.gearing.angle;
    }

    return new ModuleGearings(driveGearing, azimuthGearing);
  }

  private static <C extends SmartMotorControllerConfig<C>> C createDriveMotorConfig(
      C motorConfig,
      SwerveDriveConfig<?> swerveDriveConfig,
      ModuleJson moduleJson,
      DriveGearingJson driveGearing,
      int moduleIndex) {
    SmartMotorControllerTelemetryConfig driveTelemetryConfig = new SmartMotorControllerTelemetryConfig()
        .withCustom(SmartMotorControllerTelemetry.BooleanTelemetryField.SimpleMotorFeedForward, false)
        .withCustom(new SmartMotorControllerTelemetry.DoubleTelemetryField[]{
            SmartMotorControllerTelemetry.DoubleTelemetryField.StatorCurrent,
            SmartMotorControllerTelemetry.DoubleTelemetryField.SupplyCurrent,
            SmartMotorControllerTelemetry.DoubleTelemetryField.MeasurementPosition,
            SmartMotorControllerTelemetry.DoubleTelemetryField.MeasurementVelocity
        }, true);
    swerveDriveConfig.getSwerveDriveTelemetryConfig()
                     .flatMap(SwerveDriveTelemetryConfig::getDataLogName)
                     .ifPresent(driveDataLogName -> driveTelemetryConfig.withDataLogName(
                         driveDataLogName + "/modules/" + getModuleName(moduleIndex) + "/drive"));

    return motorConfig
        .withMotorInverted(moduleJson.inverted.drive)
        .withControlMode(ControlMode.CLOSED_LOOP)
        .withWheelDiameter(Inches.of(driveGearing.diameter))
        .withGearing(driveGearing.gearRatio)
        .withClosedLoopController(
            pidfPropertiesJson.drive.p,
            pidfPropertiesJson.drive.i,
            pidfPropertiesJson.drive.d)
        .withFeedforward(new SimpleMotorFeedforward(
            pidfPropertiesJson.drive.s,
            pidfPropertiesJson.drive.v,
            pidfPropertiesJson.drive.a))
        .withZeroPower(MotorMode.COAST)
        .withStatorCurrentLimit(
            Amps.of(physicalPropertiesJson.statorCurrentLimit.drive))
        .withTelemetry("drive", driveTelemetryConfig);
  }

  private static <C extends SmartMotorControllerConfig<C>> C createAzimuthMotorConfig(
      C motorConfig,
      SwerveDriveConfig<?> swerveDriveConfig,
      ModuleJson moduleJson,
      AngleGearingJson azimuthGearing,
      int moduleIndex) {
    SmartMotorControllerTelemetryConfig azimuthTelemetryConfig = new SmartMotorControllerTelemetryConfig()
        .withCustom(SmartMotorControllerTelemetry.BooleanTelemetryField.SimpleMotorFeedForward, false)
        .withCustom(new SmartMotorControllerTelemetry.DoubleTelemetryField[]{
            SmartMotorControllerTelemetry.DoubleTelemetryField.StatorCurrent,
            SmartMotorControllerTelemetry.DoubleTelemetryField.SupplyCurrent,
            SmartMotorControllerTelemetry.DoubleTelemetryField.MechanismPosition,
            SmartMotorControllerTelemetry.DoubleTelemetryField.MechanismVelocity,
            SmartMotorControllerTelemetry.DoubleTelemetryField.ExternalEncoderPosition,
            SmartMotorControllerTelemetry.DoubleTelemetryField.ExternalEncoderVelocity
        }, true);
    swerveDriveConfig.getSwerveDriveTelemetryConfig()
                     .flatMap(SwerveDriveTelemetryConfig::getDataLogName)
                     .ifPresent(driveDataLogName -> azimuthTelemetryConfig.withDataLogName(
                         driveDataLogName + "/modules/" + getModuleName(moduleIndex) + "/azimuth"));

    return motorConfig
        .withMotorInverted(moduleJson.inverted.angle)
        .withControlMode(ControlMode.CLOSED_LOOP)
        .withGearing(azimuthGearing.gearRatio)
        .withClosedLoopController(
            pidfPropertiesJson.angle.p,
            pidfPropertiesJson.angle.i,
            pidfPropertiesJson.angle.d)
        .withFeedforward(new SimpleMotorFeedforward(
            pidfPropertiesJson.angle.s,
            pidfPropertiesJson.angle.v,
            pidfPropertiesJson.angle.a))
        .withContinuousWrapping(
            Rotations.of(-0.5),
            Rotations.of(0.5))
        .withZeroPower(MotorMode.BRAKE)
        .withStatorCurrentLimit(
            Amps.of(physicalPropertiesJson.statorCurrentLimit.angle))
        .withTelemetry("azimuth", azimuthTelemetryConfig);
  }

  private static ModuleHardware createModuleHardware(
      ModuleJson moduleJson,
      SmartMotorControllerConfig<?> azimuthConfig, SmartMotorControllerConfig<?> driveConfig,
      SwerveDriveConfig<?> swerveDriveConfig) {
    var azimuthMotorVendor = moduleJson.angle.getVendor(VENDOR.UNKNOWN);

    var absoluteEncoderVendor = moduleJson.absoluteEncoder.getVendor(azimuthMotorVendor);

    // Create the azimuth motor controller exactly once: an attached absolute encoder is read through its vendor
    // device, and YAMS wrappers register Alerts keyed by CAN ID, so a second wrapper for the same device would throw
    // "Alert already allocated".
    var azimuthMotorController = moduleJson.angle.getSmartMotorController(azimuthConfig);

    var absoluteEncoder = moduleJson.absoluteEncoder.getAbsoluteEncoder(
        moduleJson.angle.getMotorController(),
        azimuthMotorController,
        moduleJson.absoluteEncoderInverted);

    if (absoluteEncoderVendor == azimuthMotorVendor
        && swerveDriveConfig.useExternalFeedbackSensor()) {
      azimuthConfig
          .withExternalEncoder(absoluteEncoder.getSecond())
          .withExternalEncoderZeroOffset(Degrees.of(moduleJson.absoluteEncoderOffset))
          .withUseExternalFeedbackEncoder(true);
      if (!azimuthMotorController.applyConfig(azimuthConfig)) {
        System.err.println("Failed to apply the external encoder configuration to the azimuth motor controller of "
                           + moduleJson.angle.type + " (CAN ID " + moduleJson.angle.id + ")");
      }
    }

    var driveMotorController = moduleJson.drive.getSmartMotorController(driveConfig);

    return new ModuleHardware(
        driveMotorController,
        azimuthMotorController,
        absoluteEncoder,
        azimuthMotorVendor,
        absoluteEncoderVendor);
  }

  private static LinearVelocity calculateMaxModuleSpeed(
      SmartMotorControllerConfig<?> driveConfig,
      SmartMotorController driveMotorController) {
    // DCMotor free speed is at the rotor, so reduce it through the gearing to get the wheel speed.
    return driveConfig.convertFromMechanism(
        RadiansPerSecond.of(
            driveMotorController.getDCMotor().freeSpeed
                * driveConfig.getGearing().getRotorToMechanismRatio()));
  }

  private static SwerveModule createSwerveModule(
      ModuleJson moduleJson,
      ModuleHardware hardware,
      int moduleIndex,
      SwerveDriveConfig<?> swerveDriveConfig)
  {
    TelemetryVerbosity telemetryVerbosity = swerveDriveConfig.getTelemetryVerbosity()
                                                             .orElse(TelemetryVerbosity.LOW);
    SwerveModuleTelemetryConfig moduleTelemetryConfig = new SwerveModuleTelemetryConfig(telemetryVerbosity);
    swerveDriveConfig.getSwerveDriveTelemetryConfig()
                     .flatMap(SwerveDriveTelemetryConfig::getDataLogName)
                     .ifPresent(driveDataLogName -> moduleTelemetryConfig.withDataLogName(
                         driveDataLogName + "/modules/" + getModuleName(moduleIndex)));
    SwerveModuleConfig config = new SwerveModuleConfig(
        hardware.driveMotorController,
        hardware.azimuthMotorController)
        .withCosineCompensation(true)
        //.withOptimization(true)
        .withAbsoluteEncoderOffset(
            Degrees.of(moduleJson.absoluteEncoderOffset))
        .withAbsoluteEncoderGearing(GearBox.fromReductionStages(moduleJson.absoluteEncoderGearRatio))
        .withLocation(
            Inches.of(moduleJson.location.front),
            Inches.of(moduleJson.location.left))
        .withTelemetry(getModuleName(moduleIndex), moduleTelemetryConfig);

    if (hardware.absoluteEncoderVendor != hardware.azimuthMotorVendor) {
      config.withAbsoluteEncoder(hardware.absoluteEncoder.getFirst());
    }

    return new SwerveModule(config);
  }

  private static String getModuleName(int moduleIndex) {
    return swerveDriveJson.modules[moduleIndex].split("\\.json")[0];
  }

  /**
   * Apply the swerve drive's non-module configuration (module array, max speed, discretization, gyro).
   *
   * @param config       {@link SwerveDriveConfig} to apply the gyro/modules/etc. to.
   * @param modules      {@link SwerveModule}s to apply.
   * @param maxModuleSpeed Maximum module speed to apply.
   * @return Raw gyro device (e.g. a {@code Pigeon2} instance), or {@code null} if the configured gyro type is
   * {@code "custom"} (the caller is expected to supply/own their own gyro in that case).
   */
  private static Object configureSwerveDrive(
      SwerveDriveConfig<?> config,
      SwerveModule[] modules,
      LinearVelocity maxModuleSpeed) {
    config
        .withModules(modules)
        .withMaximumModuleSpeed(maxModuleSpeed)
        .withDiscretizationTime(Millisecond.of(20))
        .withSimDiscretizationTime(Millisecond.of(20));
    if (config.getSwerveDriveTelemetryConfig().isEmpty() && config.getTelemetryVerbosity().isPresent())
    {
      // Force data-log, just incase.
      config.withTelemetry(config.getTelemetryName(), new SwerveDriveTelemetryConfig(config.getTelemetryVerbosity().orElseThrow())
          .withDataLogName("Swerve/"));
    }

    // "custom" gyro type: skip applying the gyro so the user can configure it
    // themselves.
    if ("custom".equalsIgnoreCase(swerveDriveJson.gyro.type)) {
      return null;
    }

    // YAMS inverts the heading itself (withGyroInverted), so the gyro attitude is passed through un-inverted.
    Pair<Supplier<Rotation3d>, Object> gyro = swerveDriveJson.gyro.getGyro(
        GyroAxis.valueOf(swerveDriveJson.gyroAxis.toUpperCase()));
    config
        .withGyro(gyro.getFirst())
        .withGyroInverted(swerveDriveJson.gyroInvert);
    return gyro.getSecond();
  }

  private static record ModuleGearings(
      DriveGearingJson drive,
      AngleGearingJson azimuth) {
  }

  private static record ModuleHardware(
      SmartMotorController driveMotorController,
      SmartMotorController azimuthMotorController,
      Pair<Supplier<Angle>, Object> absoluteEncoder,
      VENDOR azimuthMotorVendor,
      VENDOR absoluteEncoderVendor) {
  }

  /**
   * Raw hardware devices created for a single {@link SwerveModule} by {@link SwerveParser}.
   *
   * @param drive           Raw drive motor controller device (e.g. a vendor {@code SparkMax} or {@code TalonFX}
   *                        instance).
   * @param azimuth         Raw azimuth/angle motor controller device.
   * @param absoluteEncoder Raw absolute encoder device (e.g. a {@code CANcoder}, {@code AnalogEncoder}, or
   *                        {@code DutyCycleEncoder} instance, or the azimuth motor controller's own device when its
   *                        integrated absolute encoder feedback is used instead of a separate device).
   */
  public static record SwerveModuleDevices(
      Object drive,
      Object azimuth,
      Object absoluteEncoder) {
  }

  /**
   * Raw hardware devices created by {@link #buildSwerveDrive(SwerveDriveConfig, Supplier)}.
   *
   * @param gyro    Raw gyro device (e.g. a {@code Pigeon2} instance), or {@code null} if the configured gyro type is
   *                {@code "custom"} (the caller is expected to supply/own their own gyro in that case).
   * @param modules Raw devices for each module, in the same order as {@link SwerveDriveJson#modules}.
   */
  protected static record SwerveDriveHardware(
      Object gyro,
      SwerveModuleDevices[] modules) {
  }

  /**
   * Raw hardware devices created by a command-framework specific {@code SwerveParser#createSwerveDriveDevices},
   * alongside the {@code SwerveDrive} it built.
   *
   * @param swerveDrive {@code SwerveDrive} built from these devices.
   * @param gyro        Raw gyro device (e.g. a {@code Pigeon2} instance), or {@code null} if the configured gyro
   *                    type is {@code "custom"} (the caller is expected to supply/own their own gyro in that case).
   * @param modules     Raw devices for each module, in the same order as {@link SwerveDriveJson#modules}.
   * @param <D>         {@code SwerveDrive} type of the command framework.
   */
  public static record SwerveDriveDevices<D extends SwerveDrive>(
      D swerveDrive,
      Object gyro,
      SwerveModuleDevices[] modules) {
  }
}
