package swervelib.parser;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Millisecond;
import static edu.wpi.first.units.Units.Rotations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Supplier;
import swervelib.parser.json.DeviceJson.VENDOR;
import swervelib.parser.json.ModuleJson;
import swervelib.parser.json.PIDFPropertiesJson;
import swervelib.parser.json.PhysicalPropertiesJson;
import swervelib.parser.json.SwerveDriveJson;
import swervelib.parser.json.SwerveDriveJson.GyroAxis;
import yams.gearing.GearBox;
import yams.mechanisms.config.SwerveDriveConfig;
import yams.mechanisms.config.SwerveModuleConfig;
import yams.mechanisms.swerve.SwerveDrive;
import yams.mechanisms.swerve.SwerveModule;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.SmartMotorControllerConfig.ControlMode;
import yams.motorcontrollers.SmartMotorControllerConfig.MotorMode;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;

/**
 * Helper class used to parse the JSON directory with specified configuration options.
 */
public class SwerveParser
{

  /**
   * Module number mapped to the JSON name.
   */
  private static final HashMap<String, Integer> moduleConfigs = new HashMap<>();
  /**
   * Parsed swervedrive.json
   */
  public static        SwerveDriveJson          swerveDriveJson;
  /**
   * Parsed modules/pidfproperties.json
   */
  public static        PIDFPropertiesJson       pidfPropertiesJson;
  /**
   * Parsed modules/physicalproperties.json
   */
  public static        PhysicalPropertiesJson   physicalPropertiesJson;
  /**
   * Array holding the module jsons given in {@link SwerveDriveJson}.
   */
  public static        ModuleJson[]             moduleJsons;

  /**
   * Construct a swerve parser. Will throw an error if there is a missing file.
   *
   * @param directory Directory with swerve configurations.
   * @throws IOException if a file doesn't exist.
   */
  public SwerveParser(File directory) throws IOException
  {
    checkDirectory(directory);
    swerveDriveJson =
        new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(new File(directory, "swervedrive.json"), SwerveDriveJson.class);
    pidfPropertiesJson =
        new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                new File(directory, "modules/pidfproperties.json"), PIDFPropertiesJson.class);
    physicalPropertiesJson =
        new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(
                new File(directory, "modules/physicalproperties.json"),
                PhysicalPropertiesJson.class);
    moduleJsons = new ModuleJson[swerveDriveJson.modules.length];
    for (int i = 0; i < moduleJsons.length; i++)
    {
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
  private JsonNode openJson(File file)
  {
    try
    {
      return new ObjectMapper().readTree(file);
    } catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Check directory structure.
   *
   * @param directory JSON Configuration Directory
   */
  private void checkDirectory(File directory)
  {
    assert new File(directory, "swervedrive.json").exists();
    assert new File(directory, "modules").exists() && new File(directory, "modules").isDirectory();
    assert new File(directory, "modules/pidfproperties.json").exists();
    assert new File(directory, "modules/physicalproperties.json").exists();
  }

  public SwerveDrive createSwerveDrive(Subsystem subsys, Pose2d initialPose, SwerveDriveConfig swerveDriveConfig)
  {
    SwerveModule[] modules = new SwerveModule[swerveDriveJson.modules.length];
    for (var i = 0; i < modules.length; i++)
    {
      var moduleJson = moduleJsons[i];
      var driveGearing = moduleJson.gearing.drive.equals(physicalPropertiesJson.gearing.drive)
                         ? physicalPropertiesJson.gearing.drive
                         : moduleJson.gearing.drive;
      var azimuthGearing = moduleJson.gearing.angle.equals(physicalPropertiesJson.gearing.drive)
                           ? physicalPropertiesJson.gearing.angle
                           : moduleJson.gearing.angle;
      SmartMotorControllerConfig driveMotorConfig = new SmartMotorControllerConfig(subsys)
          .withMotorInverted(moduleJson.inverted.drive)
          .withControlMode(ControlMode.CLOSED_LOOP)
          .withWheelDiameter(Inches.of(driveGearing.diameter))
          .withGearing(driveGearing.gearRatio)
          .withClosedLoopController(pidfPropertiesJson.drive.p, pidfPropertiesJson.drive.i, pidfPropertiesJson.drive.d)
          .withIdleMode(MotorMode.COAST)
          .withStatorCurrentLimit(Amps.of(physicalPropertiesJson.statorCurrentLimit.drive))
          .withTelemetry("drive_" + swerveDriveJson.modules[i], TelemetryVerbosity.LOW);

      SmartMotorControllerConfig azimuthMotorConfig = new SmartMotorControllerConfig(subsys)
          .withMotorInverted(moduleJson.inverted.angle)
          .withControlMode(ControlMode.CLOSED_LOOP)
          .withContinuousWrapping(Rotations.of(-0.5), Rotations.of(0.5))
          .withGearing(azimuthGearing.gearRatio)
          .withClosedLoopController(pidfPropertiesJson.angle.p, pidfPropertiesJson.angle.i, pidfPropertiesJson.angle.d)
          .withIdleMode(MotorMode.BRAKE)
          .withStatorCurrentLimit(Amps.of(physicalPropertiesJson.statorCurrentLimit.angle))
          .withTelemetry("azimuth_" + swerveDriveJson.modules[i], TelemetryVerbosity.LOW);
      var azimuthMotorVendor           = moduleJson.angle.getVendor(VENDOR.UNKNOWN);
      var absoluteEncoderVendor        = moduleJson.absoluteEncoder.getVendor(azimuthMotorVendor);
      var azimuthVendorMotorController = moduleJson.angle.getVendorMotorController();
      if (absoluteEncoderVendor == azimuthMotorVendor)
      {
        Object absoluteEncoder = null;
        switch (absoluteEncoderVendor)
        {
          case CTRE -> {absoluteEncoder = moduleJson.absoluteEncoder.getCTREEncoder();}
          case REV -> {absoluteEncoder = moduleJson.absoluteEncoder.getREVEncoder(azimuthVendorMotorController);}
          case THRIFTYBOT -> {absoluteEncoder = moduleJson.absoluteEncoder.getThriftyEncoder();}
          case ANDYMARK -> {absoluteEncoder = moduleJson.absoluteEncoder.getAndyMarkEncoder();}
//          case REDUX -> {absoluteEncoder = moduleJson.encoder.getReduxEncoder();}
          case SMARTIO -> {absoluteEncoder = moduleJson.absoluteEncoder.getSmartIOEncoder();}
        }
        if (absoluteEncoder != null)
        {
          azimuthMotorConfig.withExternalEncoder(absoluteEncoder).withUseExternalFeedbackEncoder(true);
        }
      }
      var azimuthMotorController = moduleJson.angle.getMotorController(azimuthMotorConfig,
                                                                       azimuthVendorMotorController);
      var driveMotorController = moduleJson.drive.getMotorController(driveMotorConfig, null);
      SwerveModuleConfig moduleConfig = new SwerveModuleConfig(driveMotorController, azimuthMotorController)
          .withOptimization(true)
          .withAbsoluteEncoderOffset(Degrees.of(moduleJson.absoluteEncoderOffset))
          .withAbsoluteEncoderGearing(GearBox.fromReductionStages(moduleJson.absoluteEncoderGearRatio))
          .withLocation(Inches.of(moduleJson.location.front), Inches.of(moduleJson.location.left))
          .withTelemetry(swerveDriveJson.modules[i], TelemetryVerbosity.HIGH);
      if (absoluteEncoderVendor != azimuthMotorVendor)
      {
        Supplier<Angle> absoluteEncoderSupplier = moduleJson.absoluteEncoder.getEncoderSupplier(azimuthVendorMotorController);
        moduleConfig.withAbsoluteEncoder(absoluteEncoderSupplier);
      }
      SwerveModule module = new SwerveModule(moduleConfig);
      modules[i] = module;
    }
    SwerveDriveConfig sdc = swerveDriveConfig
        .withStartingPose(initialPose)
        .withTelemetry(TelemetryVerbosity.HIGH)
//        .withSubsystem(subsys)
//        .withModules(modules)
        .withDiscretizationTime(Millisecond.of(20))
        .withSimDiscretizationTime(Millisecond.of(10))
        .withGyro(swerveDriveJson.gyro.getGyroSupplier(GyroAxis.valueOf(swerveDriveJson.gyroAxis.toUpperCase())))
        .withGyroInverted(swerveDriveJson.gyroInvert);
    return new SwerveDrive(sdc);
  }
}
