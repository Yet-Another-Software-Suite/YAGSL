package swervelib.core.parser.json;

import io.avaje.jsonb.Json;

/**
 * {@link yams.core.mechanisms.swerve.SwerveDrive} JSON parsed class. Used to access parsed data from the swervedrive.json file.
 */
@Json
public class SwerveDriveJson
{

  /**
   * Robot Gyroscope used to determine the heading of the robot.
   */
  public DeviceJson gyro;
  /**
   * Gyro axis that points up through the robot, one of {@code yaw}, {@code pitch}, or {@code roll}. Passed to
   * {@link yams.core.mechanisms.config.SwerveDriveConfig#withGyroHeadingAxis(yams.core.mechanisms.config.enums.GyroAxis)}.
   */
  public String   gyroAxis = "yaw";
  /**
   * Invert the Gyroscope heading of the robot.
   */
  public boolean  gyroInvert;
  /**
   * Module JSONs in order clockwise order starting from front left.
   */
  public String[] modules;
}
