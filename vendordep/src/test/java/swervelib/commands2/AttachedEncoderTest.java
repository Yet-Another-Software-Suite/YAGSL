package swervelib.commands2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.wpilib.command2.CommandScheduler;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.hardware.hal.HAL;
import org.wpilib.hardware.hal.RobotMode;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.simulation.DriverStationSim;
import org.wpilib.simulation.SimHooks;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;
import yams.core.mechanisms.swerve.SwerveModule;
import yams.core.motorcontrollers.SmartMotorController;

/**
 * Builds a simulated swerve drive with every {@code _attached} absolute encoder type on one angle motor controller
 * type, and checks that each type either steers both modules to 90 degrees, one of them with an inverted encoder and
 * an offset, or fails at startup with a message saying what to use instead.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AttachedEncoderTest {

  /** Every attached absolute encoder type. */
  static final String[] TYPES = {
    "revthroughbore",
    "dutycycle",
    "analog",
    "analog5v",
    "canandmag",
    "srxmag",
    "thrifty",
    "andymarkhexbore"
  };

  @TempDir static Path tempDir;

  /** Angle motor type, such as {@code sparkmax_neo}. */
  abstract String angleMotor();

  /** Attached types that steer, and whether each becomes the angle motor controller's feedback sensor. */
  abstract Map<String, Boolean> working();

  /** Attached types that fail at startup, and a piece of the message each fails with. */
  abstract Map<String, String> failing();

  Stream<String> types() {
    return Stream.of(TYPES);
  }

  @BeforeAll
  void setUp() {
    HAL.initialize();
    DriverStationSim.setDsAttached(true);
    DriverStationSim.setRobotMode(RobotMode.TELEOPERATED);
    DriverStationSim.setEnabled(true);
    DriverStationSim.notifyNewData();
  }

  @AfterAll
  void tearDown() {
    SimHooks.resumeTiming();
  }

  /** A config with two modules: front left with an inverted encoder and a 45 degree offset, back right without. */
  Path writeConfig(String type) throws IOException {
    int index = java.util.Arrays.asList(TYPES).indexOf(type);
    Path dir = Files.createTempDirectory(tempDir, type);
    Path modules = Files.createDirectories(dir.resolve("modules"));
    Files.writeString(
        dir.resolve("swervedrive.json"),
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":"
            + (40 + index)
            + ",\"canbus\":\"\"},\"gyroAxis\":\"yaw\","
            + "\"gyroInvert\":false,\"modules\":[\"fl.json\",\"br.json\"]}");
    Files.writeString(
        modules.resolve("pidfproperties.json"),
        "{\"drive\":{\"p\":0.5,\"i\":0,\"d\":0},\"angle\":{\"p\":20,\"i\":0,\"d\":0}}");
    Files.writeString(
        modules.resolve("physicalproperties.json"),
        "{\"gearing\":{\"drive\":{\"gearRatio\":6.75,\"diameter\":4},\"angle\":{\"gearRatio\":21.43}},"
            + "\"statorCurrentLimit\":{\"drive\":40,\"angle\":20}}");
    String[] names = {"fl", "br"};
    for (int i = 0; i < names.length; i++) {
      int driveId = 1 + index * 4 + i * 2;
      boolean frontLeft = i == 0;
      Files.writeString(
          modules.resolve(names[i] + ".json"),
          "{\"drive\":{\"type\":\""
              + angleMotor()
              + "\",\"id\":"
              + driveId
              + ",\"canbus\":\"\"},"
              + "\"angle\":{\"type\":\""
              + angleMotor()
              + "\",\"id\":"
              + (driveId + 1)
              + ",\"canbus\":\"\"},"
              + "\"absoluteEncoder\":{\"type\":\""
              + type
              + "_attached\",\"id\":0,\"channel\":0,\"canbus\":\"\"},"
              + "\"inverted\":{\"drive\":false,\"angle\":false},"
              + "\"absoluteEncoderOffset\":"
              + (frontLeft ? 45 : 0)
              + ","
              + "\"absoluteEncoderInverted\":"
              + frontLeft
              + ","
              + "\"location\":{\"front\":"
              + (frontLeft ? 12 : -12)
              + ",\"left\":"
              + (frontLeft ? 12 : -12)
              + "}}");
    }
    return dir;
  }

  /** The SPARK's closed loop feedback sensor, or null for another motor controller. */
  static FeedbackSensor sparkFeedbackSensor(SmartMotorController motorController) {
    Object spark = motorController.getMotorController();
    if (spark instanceof SparkMax sparkMax) {
      return sparkMax.configAccessor.closedLoop.getFeedbackSensor();
    }
    if (spark instanceof SparkFlex sparkFlex) {
      return sparkFlex.configAccessor.closedLoop.getFeedbackSensor();
    }
    return null;
  }

  /** Angle error, folded so that a module pointing the opposite way counts as on target. */
  static double foldedError(double degrees, double target) {
    double error = Math.IEEEremainder(degrees - target, 180);
    return Math.abs(error);
  }

  @ParameterizedTest
  @MethodSource("types")
  void attachedEncoder(String type) throws IOException {
    assertTrue(
        working().containsKey(type) ^ failing().containsKey(type),
        type + " has to be working or failing");
    Path dir = writeConfig(type);
    SwerveParser.parse(dir.toFile());
    SubsystemBase subsystem = new SubsystemBase() {};
    try {
      if (failing().containsKey(type)) {
        Throwable thrown =
            assertThrows(
                Throwable.class,
                () ->
                    SwerveParser.createSwerveDrive(
                        new SwerveDriveConfig().withSubsystem(subsystem)));
        Throwable cause = thrown;
        while (cause.getCause() != null && !(cause instanceof IllegalArgumentException)) {
          cause = cause.getCause();
        }
        assertTrue(
            cause.getMessage() != null && cause.getMessage().contains(failing().get(type)),
            type + " failed with " + cause + ", not a message with: " + failing().get(type));
        return;
      }

      SwerveDrive drive =
          SwerveParser.createSwerveDrive(new SwerveDriveConfig().withSubsystem(subsystem));
      boolean feedback = working().get(type);
      for (SwerveModule module : drive.getModules()) {
        SmartMotorController azimuth = module.getAzimuthMotorController();
        assertEquals(
            feedback,
            azimuth.getConfig().getExternalEncoder().isPresent(),
            type
                + ": whether "
                + module.getName()
                + "'s encoder is its angle motor controller's feedback sensor");
        FeedbackSensor sensor = sparkFeedbackSensor(azimuth);
        if (sensor != null) {
          assertNotEquals(
              FeedbackSensor.kAnalogSensor,
              sensor,
              type + ": the SPARK's closed loop uses its analog input");
        }
      }

      SimHooks.pauseTiming();
      for (int i = 0; i < 25; i++) {
        drive.setAzimuthAngles(Rotation2d.ZERO);
        SimHooks.stepTiming(0.02);
        drive.simIterate();
        drive.updateTelemetry();
      }
      for (int i = 0; i < 75; i++) {
        drive.setAzimuthAngles(Rotation2d.fromDegrees(90));
        SimHooks.stepTiming(0.02);
        drive.simIterate();
        drive.updateTelemetry();
      }
      for (SwerveModule module : drive.getModules()) {
        double angle = module.getState().angle.getDegrees();
        assertTrue(
            foldedError(angle, 90) < 2,
            type + ": " + module.getName() + " reached " + angle + " degrees, not 90");
      }
    } finally {
      SimHooks.resumeTiming();
      CommandScheduler.getInstance().unregisterSubsystem(subsystem);
    }
  }

  /** Attached types that steer as the angle motor controller's feedback sensor, and the analog ones the module reads. */
  static Map<String, Boolean> sparkWorking() {
    return Map.of(
        "revthroughbore",
        true,
        "dutycycle",
        true,
        "canandmag",
        true,
        "srxmag",
        true,
        "andymarkhexbore",
        true,
        "analog",
        false,
        "analog5v",
        false);
  }

  static final Set<String> NOVA_FEEDBACK =
      Set.of(
          "revthroughbore",
          "dutycycle",
          "analog",
          "canandmag",
          "srxmag",
          "thrifty",
          "andymarkhexbore");
}
