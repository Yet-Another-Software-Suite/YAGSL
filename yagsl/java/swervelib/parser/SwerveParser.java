package swervelib.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wpi.first.math.Pair;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.units.measure.LinearVelocity;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
   * Construct a swerve parser. Will throw an error if there is a missing file.
   *
   * @param directory Directory with swerve configurations.
   * @throws IOException if a file doesn't exist.
   */
  public SwerveParser(File directory) throws IOException {
    checkDirectory(directory);
    swerveDriveJson = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(new File(directory, "swervedrive.json"), SwerveDriveJson.class);
    pidfPropertiesJson = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue(
            new File(directory, "modules/pidfproperties.json"), PIDFPropertiesJson.class);
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
  private JsonNode openJson(File file) {
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
  private void checkDirectory(File directory) {
    assert new File(directory, "swervedrive.json").exists();
    assert new File(directory, "modules").exists() && new File(directory, "modules").isDirectory();
    assert new File(directory, "modules/pidfproperties.json").exists();
    assert new File(directory, "modules/physicalproperties.json").exists();
  }

  /**
   * Create a {@link SwerveDrive} from the parsed JSON configuration.
   *
   * @param swerveDriveConfig {@link SwerveDriveConfig} to apply to the created {@link SwerveDrive}.
   * @return Configured {@link SwerveDrive}.
   */
  public SwerveDrive createSwerveDrive(SwerveDriveConfig swerveDriveConfig) {
    SwerveModule[] modules = new SwerveModule[swerveDriveJson.modules.length];
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
      if((pidfPropertiesJson.drive.s+pidfPropertiesJson.drive.v+pidfPropertiesJson.drive.a) == 0) {
          var sff = new SimpleMotorFeedforward(
                  pidfPropertiesJson.drive.s,
                  12.0 / driveConfig.convertToMechanism(swerveDriveConfig.getMaximumModuleLinearVelocity().orElseThrow())
                          .in(RotationsPerSecond),
                  pidfPropertiesJson.drive.a);
          driveConfig.withFeedforward(sff);
          hardware.driveMotorController.setFeedforward(sff.getKs(),sff.getKv(),sff.getKa(),0);
      }

      modules[i] = createSwerveModule(
          moduleJson,
          hardware,
          i);
    }

    configureSwerveDrive(
        swerveDriveConfig,
        modules,
        totalMaxModuleSpeed.div(modules.length));

    return new SwerveDrive(swerveDriveConfig);
  }

  private ModuleGearings resolveGearings(ModuleJson moduleJson) {
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

  private SmartMotorControllerConfig createDriveMotorConfig(
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
        .withTelemetry(
            "drive_" + swerveDriveJson.modules[moduleIndex],
            TelemetryVerbosity.LOW);
  }

  private SmartMotorControllerConfig createAzimuthMotorConfig(
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
        .withTelemetry(
            "azimuth_" + swerveDriveJson.modules[moduleIndex],
            TelemetryVerbosity.LOW);
  }

  private ModuleHardware createModuleHardware(
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

  private LinearVelocity calculateMaxModuleSpeed(
      SmartMotorControllerConfig driveConfig,
      SmartMotorController driveMotorController) {
    return driveConfig.convertFromMechanism(
        RadiansPerSecond.of(
            driveMotorController.getDCMotor().freeSpeedRadPerSec));
  }

  private SwerveModule createSwerveModule(
      ModuleJson moduleJson,
      ModuleHardware hardware,
      int moduleIndex) {
    SwerveModuleConfig config = new SwerveModuleConfig(
        hardware.driveMotorController,
        hardware.azimuthMotorController)
        .withOptimization(true)
        .withAbsoluteEncoderOffset(
            Degrees.of(moduleJson.absoluteEncoderOffset))
        .withAbsoluteEncoderGearing(
            GearBox.fromReductionStages(
                moduleJson.absoluteEncoderGearRatio))
        .withLocation(
            Inches.of(moduleJson.location.front),
            Inches.of(moduleJson.location.left))
        .withTelemetry(
            swerveDriveJson.modules[moduleIndex].split("\\.json")[0],
            TelemetryVerbosity.HIGH);

    if (hardware.absoluteEncoderVendor != hardware.azimuthMotorVendor) {
      config.withAbsoluteEncoder(
          hardware.absoluteEncoder.getFirst());
    }

    return new SwerveModule(config);
  }

  private void configureSwerveDrive(
      SwerveDriveConfig config,
      SwerveModule[] modules,
      LinearVelocity maxModuleSpeed) {
    config
        .withModules(modules)
        .withMaximumModuleSpeed(maxModuleSpeed)
        .withDiscretizationTime(Millisecond.of(20))
        .withSimDiscretizationTime(Millisecond.of(20))
        .withGyro(
            swerveDriveJson.gyro.getGyro(
                GyroAxis.valueOf(
                    swerveDriveJson.gyroAxis.toUpperCase()),
                swerveDriveJson.gyroInvert).getFirst())
        .withGyroInverted(swerveDriveJson.gyroInvert);
  }

  private record ModuleGearings(
      DriveGearingJson drive,
      AngleGearingJson azimuth) {
  }

  private record ModuleHardware(
      SmartMotorController driveMotorController,
      SmartMotorController azimuthMotorController,
      Pair<?, ?> absoluteEncoder,
      VENDOR azimuthMotorVendor,
      VENDOR absoluteEncoderVendor) {
  }
}
