package swervelib.commands2;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.studica.frc.Navx;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.wpilib.command2.SubsystemBase;
import swervelib.core.parser.SwerveParser.SwerveDriveDevices;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;

/**
 * Builds a whole swerve drive with a Studica navX3 plugged into a Systemcore USB port (navx3_usb), through the same
 * reflection path a robot uses. A class of its own, as each test class runs in a JVM of its own: the drive's motors
 * have the same CAN IDs as {@link SwerveParserNavxTest}'s.
 */
class SwerveParserNavxUsbTest {

  @TempDir static Path configDir;

  @BeforeAll
  static void writeConfig() throws IOException {
    SwerveParserNavxTest.writeConfig(
        configDir, "{\"type\":\"navx3_usb\",\"id\":0,\"canbus\":\"\",\"channel\":1}");
  }

  @Test
  void navx3UsbGyroBuilds() {
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
