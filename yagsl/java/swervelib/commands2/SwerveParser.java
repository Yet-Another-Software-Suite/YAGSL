package swervelib.commands2;

import yams.commands2.config.SmartMotorControllerConfig;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;

/**
 * {@link swervelib.core.parser.SwerveParser} for WPILib Commands v2. Creates a {@link SwerveDrive} whose motor controllers are
 * bound to the {@link org.wpilib.command2.Subsystem} of the given {@link SwerveDriveConfig}.
 */
public class SwerveParser extends swervelib.core.parser.SwerveParser
{

  /**
   * Construct a swerve parser.
   */
  public SwerveParser()
  {
  }

  /**
   * Create a {@link SwerveDrive} from the parsed JSON configuration.
   *
   * @param swerveDriveConfig {@link SwerveDriveConfig} to apply to the created {@link SwerveDrive}.
   * @return Configured {@link SwerveDrive}.
   */
  public static SwerveDrive createSwerveDrive(SwerveDriveConfig swerveDriveConfig)
  {
    return createSwerveDriveDevices(swerveDriveConfig).swerveDrive();
  }

  /**
   * Create a {@link SwerveDrive} from the parsed JSON configuration, exposing the raw vendor hardware devices created
   * along the way (drive/azimuth motor controllers, absolute encoders, and the gyro) alongside it. Useful when code
   * needs direct access to a vendor device for configuration that isn't exposed through
   * {@link yams.core.motorcontrollers.SmartMotorController} -- everything else should prefer
   * {@link #createSwerveDrive(SwerveDriveConfig)}.
   *
   * @param swerveDriveConfig {@link SwerveDriveConfig} to apply to the created {@link SwerveDrive}.
   * @return {@link SwerveDriveDevices} containing the configured {@link SwerveDrive} and every raw device created for
   * it.
   */
  public static SwerveDriveDevices<SwerveDrive> createSwerveDriveDevices(SwerveDriveConfig swerveDriveConfig)
  {
    SwerveDriveHardware hardware = buildSwerveDrive(
        swerveDriveConfig,
        () -> new SmartMotorControllerConfig(swerveDriveConfig.getSubsystem()));
    return new SwerveDriveDevices<>(new SwerveDrive(swerveDriveConfig), hardware.gyro(), hardware.modules());
  }
}
