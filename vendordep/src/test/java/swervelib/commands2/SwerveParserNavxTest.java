package swervelib.commands2;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.studica.frc.Navx;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.hardware.hal.HAL;
import swervelib.core.parser.SwerveParser.SwerveDriveDevices;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;

/**
 * Builds a whole swerve drive with a Studica navX3 gyro from a JSON config, through the same reflection path a robot
 * uses.
 */
class SwerveParserNavxTest {

  @TempDir static Path configDir;

  private static final String[] MODULES = {"fl", "fr", "bl", "br"};

  @BeforeAll
  static void writeConfig() throws IOException {
    HAL.initialize();
    Path modules = Files.createDirectories(configDir.resolve("modules"));
    Files.writeString(
        configDir.resolve("swervedrive.json"),
        "{\"gyro\":{\"type\":\"navx3_can\",\"id\":42,\"canbus\":\"1\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":false,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}");
    Files.writeString(
        modules.resolve("pidfproperties.json"),
        "{\"drive\":{\"p\":0.5,\"i\":0.0,\"d\":0.0},\"angle\":{\"p\":20.0,\"i\":0.0,\"d\":0.0}}");
    Files.writeString(
        modules.resolve("physicalproperties.json"),
        "{\"gearing\":{\"drive\":{\"gearRatio\":6.75,\"diameter\":4.0},\"angle\":{\"gearRatio\":21.43}},"
            + "\"statorCurrentLimit\":{\"drive\":40,\"angle\":20}}");
    for (int i = 0; i < MODULES.length; i++) {
      double front = i < 2 ? 12 : -12;
      double left = i % 2 == 0 ? 12 : -12;
      Files.writeString(
          modules.resolve(MODULES[i] + ".json"),
          "{\"drive\":{\"type\":\"talonfx_krakenx60\",\"id\":" + (30 + i * 3) + ",\"canbus\":\"\"},"
              + "\"angle\":{\"type\":\"talonfx_krakenx60\",\"id\":" + (31 + i * 3) + ",\"canbus\":\"\"},"
              + "\"absoluteEncoder\":{\"type\":\"cancoder_can\",\"id\":" + (32 + i * 3) + ",\"canbus\":\"\"},"
              + "\"inverted\":{\"drive\":false,\"angle\":false},"
              + "\"absoluteEncoderOffset\":0,\"absoluteEncoderInverted\":false,"
              + "\"location\":{\"front\":" + front + ",\"left\":" + left + "}}");
    }
  }

  @Test
  void navx3GyroBuilds() {
    SwerveParser.parse(configDir.toFile());
    SubsystemBase subsystem = new SubsystemBase() {};
    SwerveDriveDevices<SwerveDrive> devices =
        SwerveParser.createSwerveDriveDevices(new SwerveDriveConfig().withSubsystem(subsystem));

    assertNotNull(devices.swerveDrive());
    assertInstanceOf(Navx.class, devices.gyro());
    // A navX with no reading yet still gives the drive a heading.
    assertNotNull(devices.swerveDrive().getGyroRotation3d());
  }
}
