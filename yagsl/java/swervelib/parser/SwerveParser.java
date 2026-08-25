package swervelib.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.math.Pair;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.LinearVelocity;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.RobotBase;
import swervelib.parser.json.DeviceJson.VENDOR;
import swervelib.parser.json.ModuleJson;
import swervelib.parser.json.PIDFPropertiesJson;
import swervelib.parser.json.PhysicalPropertiesJson;
import swervelib.parser.json.SwerveDriveJson;
import swervelib.parser.json.SwerveDriveJson.GyroAxis;
import swervelib.parser.json.modules.AngleGearingJson;
import swervelib.parser.json.modules.DriveGearingJson;
import yams.gearing.GearBox;
import yams.mechanisms.config.SwerveDriveConfig;
import yams.mechanisms.config.SwerveModuleConfig;
import yams.mechanisms.swerve.SwerveDrive;
import yams.mechanisms.swerve.SwerveModule;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.SmartMotorControllerConfig.ControlMode;
import yams.motorcontrollers.SmartMotorControllerConfig.MotorMode;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;
import yams.telemetry.SmartMotorControllerTelemetry;
import yams.telemetry.SmartMotorControllerTelemetryConfig;

import static edu.wpi.first.units.Units.*;

/**
 * Helper class used to parse the JSON directory with specified configuration
 * options.
 */
public class SwerveParser {

  /**
   * Module number mapped to the JSON name.
   */
  private static final HashMap<String, Integer> moduleConfigs = new HashMap<>();
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
    swerveDriveJson = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(new File(directory, "swervedrive.json"), SwerveDriveJson.class);
    var pidfFile = new File(directory, "modules/pidfproperties.json");
    var simPidfFile = new File(directory, "modules/pidfproperties_sim.json");
    if (simPidfFile.exists() && RobotBase.isSimulation()) {
      pidfFile = simPidfFile;
    }
    pidfPropertiesJson = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(pidfFile, PIDFPropertiesJson.class);
    physicalPropertiesJson = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(
            new File(directory, "modules/physicalproperties.json"),
            PhysicalPropertiesJson.class);
    moduleJsons = new ModuleJson[swerveDriveJson.modules.length];
    for (int i = 0; i < moduleJsons.length; i++) {
      moduleConfigs.put(swerveDriveJson.modules[i], i);
      File moduleFile = new File(directory, "modules/" + swerveDriveJson.modules[i]);
      assert moduleFile.exists();
      moduleJsons[i] = new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(moduleFile, ModuleJson.class);
    }
  }

  /**
   * Open JSON file.
   *
   * @param file JSON File to open.
   * @return JsonNode of file.
   */
  private static JsonNode openJson(File file) {
    try {
      return new ObjectMapper().readTree(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
   * Create a {@link SwerveDrive} from the parsed JSON configuration.
   *
   * @param swerveDriveConfig {@link SwerveDriveConfig} to apply to the created
   *                          {@link SwerveDrive}.
   * @return Configured {@link SwerveDrive}.
   */
  public static SwerveDrive createSwerveDrive(SwerveDriveConfig swerveDriveConfig) {
    return buildSwerveDrive(swerveDriveConfig).swerveDrive();
  }

  /**
   * Create a {@link SwerveDrive} from the parsed JSON configuration, exposing the raw vendor hardware devices
   * created along the way (drive/azimuth motor controllers, absolute encoders, and the gyro) alongside it. Useful
   * when code needs direct access to a vendor device for configuration that isn't exposed through
   * {@link yams.motorcontrollers.SmartMotorController} -- everything else should prefer
   * {@link #createSwerveDrive(SwerveDriveConfig)}.
   *
   * @param swerveDriveConfig {@link SwerveDriveConfig} to apply to the created {@link SwerveDrive}.
   * @return {@link SwerveDriveDevices} containing the configured {@link SwerveDrive} and every raw device created
   * for it.
   */
  public static SwerveDriveDevices createSwerveDriveDevices(SwerveDriveConfig swerveDriveConfig) {
    return buildSwerveDrive(swerveDriveConfig);
  }

  /**
   * Build a {@link SwerveDrive} from the parsed JSON configuration, collecting the raw vendor hardware devices
   * created for each module and the gyro along the way.
   *
   * @param swerveDriveConfig {@link SwerveDriveConfig} to apply to the created {@link SwerveDrive}.
   * @return {@link SwerveDriveDevices} containing the configured {@link SwerveDrive} and every raw device created
   * for it.
   */
  private static SwerveDriveDevices buildSwerveDrive(SwerveDriveConfig swerveDriveConfig) {
    SwerveModule[] modules = new SwerveModule[swerveDriveJson.modules.length];
    SwerveModuleDevices[] moduleDevices = new SwerveModuleDevices[swerveDriveJson.modules.length];
    LinearVelocity totalMaxModuleSpeed = MetersPerSecond.zero();

    for (int i = 0; i < modules.length; i++) {
      ModuleJson moduleJson = moduleJsons[i];

      ModuleGearings gearings = resolveGearings(moduleJson);

      SmartMotorControllerConfig driveConfig = createDriveMotorConfig(swerveDriveConfig, moduleJson, gearings.drive, i);

      SmartMotorControllerConfig azimuthConfig = createAzimuthMotorConfig(swerveDriveConfig, moduleJson,
          gearings.azimuth, i);

      ModuleHardware hardware = createModuleHardware(moduleJson, azimuthConfig, driveConfig, swerveDriveConfig);

      totalMaxModuleSpeed = totalMaxModuleSpeed.plus(
          calculateMaxModuleSpeed(driveConfig, hardware.driveMotorController));

      // Automatic theorhetical feedforward for drive motors.
      if ((pidfPropertiesJson.drive.v) == 0) {
        var sff = new SimpleMotorFeedforward(
            pidfPropertiesJson.drive.s,
            12.0 / driveConfig.convertToMechanism(calculateMaxModuleSpeed(driveConfig, hardware.driveMotorController))
                .in(RotationsPerSecond),
            pidfPropertiesJson.drive.a);
        driveConfig.withFeedforward(sff);
        hardware.driveMotorController.setFeedforward(sff.getKs(), sff.getKv(), sff.getKa(), 0);
      }

      modules[i] = createSwerveModule(
          moduleJson,
          hardware,
          i);

      moduleDevices[i] = new SwerveModuleDevices(
          hardware.driveMotorController.getMotorController(),
          hardware.azimuthMotorController.getMotorController(),
          hardware.absoluteEncoder.getSecond());
    }

    Object gyroDevice = configureSwerveDrive(
        swerveDriveConfig,
        modules,
        totalMaxModuleSpeed.div(modules.length));

    return new SwerveDriveDevices(new SwerveDrive(swerveDriveConfig), gyroDevice, moduleDevices);
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

  private static SmartMotorControllerConfig createDriveMotorConfig(
      SwerveDriveConfig swerveDriveConfig,
      ModuleJson moduleJson,
      DriveGearingJson driveGearing,
      int moduleIndex) {
    return new SmartMotorControllerConfig(swerveDriveConfig.getSubsystem())
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
        .withIdleMode(MotorMode.COAST)
        .withStatorCurrentLimit(
            Amps.of(physicalPropertiesJson.statorCurrentLimit.drive))
        .withTelemetry("drive",
            new SmartMotorControllerTelemetryConfig()
                .withDataLogName("Swerve/modules/" + getModuleName(moduleIndex) + "/drive")
                .withCustom(SmartMotorControllerTelemetry.BooleanTelemetryField.SimpleMotorFeedForward, false)
                .withCustom(new SmartMotorControllerTelemetry.DoubleTelemetryField[] {
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kP,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kI,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kD,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kS,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kV,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kA,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.StatorCurrent,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.StatorCurrentLimit,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.SupplyCurrent,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.SupplyCurrentLimit,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.MeasurementPosition,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.MeasurementVelocity
                }, true));
  }

  private static SmartMotorControllerConfig createAzimuthMotorConfig(
      SwerveDriveConfig swerveDriveConfig,
      ModuleJson moduleJson,
      AngleGearingJson azimuthGearing,
      int moduleIndex) {
    return new SmartMotorControllerConfig(swerveDriveConfig.getSubsystem())
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
        .withIdleMode(MotorMode.BRAKE)
        .withStatorCurrentLimit(
            Amps.of(physicalPropertiesJson.statorCurrentLimit.angle))
        .withTelemetry("azimuth",
            new SmartMotorControllerTelemetryConfig()
                .withDataLogName("Swerve/modules/" + getModuleName(moduleIndex) + "/azimuth")
                .withCustom(SmartMotorControllerTelemetry.BooleanTelemetryField.SimpleMotorFeedForward, false)
                .withCustom(new SmartMotorControllerTelemetry.DoubleTelemetryField[] {
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kP,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kI,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kD,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kS,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kV,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.kA,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.StatorCurrent,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.StatorCurrentLimit,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.SupplyCurrent,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.SupplyCurrentLimit,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.MechanismPosition,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.MechanismVelocity,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.ExternalEncoderPosition,
                    SmartMotorControllerTelemetry.DoubleTelemetryField.ExternalEncoderVelocity
                }, true));
  }

  private static ModuleHardware createModuleHardware(
      ModuleJson moduleJson,
      SmartMotorControllerConfig azimuthConfig, SmartMotorControllerConfig driveConfig,
      SwerveDriveConfig swerveDriveConfig) {
    var azimuthMotorVendor = moduleJson.angle.getVendor(VENDOR.UNKNOWN);

    var absoluteEncoderVendor = moduleJson.absoluteEncoder.getVendor(azimuthMotorVendor);

    var vendorMotorController = moduleJson.angle.getSmartMotorController(azimuthConfig);

    var absoluteEncoder = moduleJson.absoluteEncoder.getAbsoluteEncoder(
        moduleJson.angle.getMotorController(),
        vendorMotorController,
        moduleJson.absoluteEncoderInverted);

    if (absoluteEncoderVendor == azimuthMotorVendor
        && swerveDriveConfig.useExternalFeedbackSensor()) {
      azimuthConfig
          .withExternalEncoder(absoluteEncoder.getSecond())
          .withUseExternalFeedbackEncoder(true);
    }

    var azimuthMotorController = moduleJson.angle.getSmartMotorController(azimuthConfig);

    var driveMotorController = moduleJson.drive.getSmartMotorController(driveConfig);

    return new ModuleHardware(
        driveMotorController,
        azimuthMotorController,
        absoluteEncoder,
        azimuthMotorVendor,
        absoluteEncoderVendor);
  }

  private static LinearVelocity calculateMaxModuleSpeed(
      SmartMotorControllerConfig driveConfig,
      SmartMotorController driveMotorController) {
    return driveConfig.convertFromMechanism(
        RadiansPerSecond.of(
            driveMotorController.getDCMotor().freeSpeedRadPerSec));
  }

  private static SwerveModule createSwerveModule(
      ModuleJson moduleJson,
      ModuleHardware hardware,
      int moduleIndex) {
    SwerveModuleConfig config = new SwerveModuleConfig(
        hardware.driveMotorController,
        hardware.azimuthMotorController)
        .withCosineCompensation(true)
        .withOptimization(true)
        .withAbsoluteEncoderOffset(
            Degrees.of(moduleJson.absoluteEncoderOffset))
        .withAbsoluteEncoderGearing(
            GearBox.fromReductionStages(
                moduleJson.absoluteEncoderGearRatio))
        .withLocation(
            Inches.of(moduleJson.location.front),
            Inches.of(moduleJson.location.left))
        .withDataLogName("Swerve/modules/" + getModuleName(moduleIndex))
        .withTelemetry(getModuleName(moduleIndex),
            TelemetryVerbosity.LOW);

    if (hardware.absoluteEncoderVendor != hardware.azimuthMotorVendor) {
      config.withAbsoluteEncoder(
          hardware.absoluteEncoder.getFirst());
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
      SwerveDriveConfig config,
      SwerveModule[] modules,
      LinearVelocity maxModuleSpeed) {
    config
        .withModules(modules)
        .withMaximumModuleSpeed(maxModuleSpeed)
        .withDiscretizationTime(Millisecond.of(20))
        .withSimDiscretizationTime(Millisecond.of(20))
        .withDataLogName("Swerve");

    // "custom" gyro type: skip applying the gyro so the user can configure it
    // themselves.
    if ("custom".equalsIgnoreCase(swerveDriveJson.gyro.type)) {
      return null;
    }

    Pair<Supplier<Angle>, Object> gyro = swerveDriveJson.gyro.getGyro(
        GyroAxis.valueOf(swerveDriveJson.gyroAxis.toUpperCase()),
        swerveDriveJson.gyroInvert);
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
   * Raw hardware devices created by {@link SwerveParser#createSwerveDriveDevices(SwerveDriveConfig)}, alongside the
   * {@link SwerveDrive} it built.
   *
   * @param swerveDrive {@link SwerveDrive} built from these devices.
   * @param gyro        Raw gyro device (e.g. a {@code Pigeon2} instance), or {@code null} if the configured gyro
   *                    type is {@code "custom"} (the caller is expected to supply/own their own gyro in that case).
   * @param modules     Raw devices for each module, in the same order as {@link SwerveDriveJson#modules}.
   */
  public static record SwerveDriveDevices(
      SwerveDrive swerveDrive,
      Object gyro,
      SwerveModuleDevices[] modules) {
  }
}