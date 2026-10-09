package swervelib.commands2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.thrifty.nova.Nova;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.hardware.hal.HAL;
import org.wpilib.math.system.DCMotor;
import swervelib.core.parser.SwerveParser.SwerveDriveDevices;
import swervelib.core.parser.SwerveParser.SwerveModuleDevices;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;
import yams.core.mechanisms.swerve.SwerveModule;

/**
 * Builds a whole swerve drive from a JSON config whose drive motors are NEOs and azimuth motors are Minions on Thrifty
 * Novas, with absolute encoders attached to the azimuth Novas, through the same reflection path a robot uses.
 */
class SwerveParserNovaTest {

  @TempDir static Path configDir;

  private static final String[] MODULES = {"fl", "fr", "bl", "br"};

  @BeforeAll
  static void writeConfig() throws IOException {
    HAL.initialize();
    Path modules = Files.createDirectories(configDir.resolve("modules"));
    Files.writeString(
        configDir.resolve("swervedrive.json"),
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":41,\"canbus\":\"\"},"
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
          "{\"drive\":{\"type\":\"nova_neo\",\"id\":" + (50 + i * 2) + ",\"canbus\":\"\"},"
              + "\"angle\":{\"type\":\"nova_minion\",\"id\":" + (51 + i * 2) + ",\"canbus\":\"\"},"
              + "\"absoluteEncoder\":{\"type\":\"revthroughbore_attached\",\"id\":0,\"canbus\":\"\"},"
              + "\"inverted\":{\"drive\":false,\"angle\":false},"
              + "\"absoluteEncoderOffset\":0,\"absoluteEncoderInverted\":false,"
              + "\"location\":{\"front\":" + front + ",\"left\":" + left + "}}");
    }
  }

  @Test
  void novaModulesBuildOnNova() {
    File dir = configDir.toFile();
    SwerveParser.parse(dir);
    SubsystemBase subsystem = new SubsystemBase() {};
    SwerveDriveDevices<SwerveDrive> devices =
        SwerveParser.createSwerveDriveDevices(new SwerveDriveConfig().withSubsystem(subsystem));

    assertNotNull(devices.swerveDrive());
    DCMotor neo = DCMotor.getNEO(1);
    DCMotor minion = DCMotor.getMinion(1);
    for (SwerveModuleDevices module : devices.modules()) {
      assertInstanceOf(Nova.class, module.drive());
      assertInstanceOf(Nova.class, module.azimuth());
    }
    for (String name : MODULES) {
      SwerveModule module = devices.swerveDrive().getModule(name).orElseThrow();
      assertEquals(neo.freeSpeed, module.getDriveMotorController().getDCMotor().freeSpeed, 1e-9);
      assertEquals(minion.freeSpeed, module.getAzimuthMotorController().getDCMotor().freeSpeed, 1e-9);
    }
  }
}
