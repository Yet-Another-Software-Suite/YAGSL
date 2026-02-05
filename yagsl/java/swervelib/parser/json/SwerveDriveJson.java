package swervelib.parser.json;

/**
 * {@link yams.mechanisms.swerve.SwerveDrive} JSON parsed class. Used to access parsed data from the swervedrive.json file.
 */
public class SwerveDriveJson
{

  /**
   * Gyro Axis
   */
  public enum GyroAxis
  {
    /**
     * Yaw axis (Z)
     */
    YAW,
    /**
     * Pitch axis (X)
     */
    PITCH,
    /**
     * Roll axis (Y)
     */
    ROLL
  }

  /**
   * Robot Gyroscope used to determine the heading of the robot.
   */
  public DeviceJson gyro;
  /**
   * Gyro rotation axis used to determine what orientation the robots heading is.
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
