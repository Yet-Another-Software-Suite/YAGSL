package swervelib.core.parser.json;

import io.avaje.jsonb.Json;

import java.util.function.Supplier;
import org.wpilib.math.geometry.Rotation3d;

/**
 * {@link yams.core.mechanisms.swerve.SwerveDrive} JSON parsed class. Used to access parsed data from the swervedrive.json file.
 */
@Json
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
    ROLL;

    /**
     * Convert the gyroscope's attitude into the robot's attitude, using this axis as the robot's heading.
     *
     * @param gyroAttitude {@link Supplier} of the gyroscope's attitude.
     * @return {@link Supplier} of the robot's attitude. For {@link #YAW} this is the gyroscope's full attitude, so
     * roll and pitch (and anything built on them, like anti-tipping) are available. For {@link #PITCH} and
     * {@link #ROLL} only the heading is reported, as the yaw of the returned {@link Rotation3d}.
     */
    // TODO: Delete once YAMS ships SwerveDriveConfig#withGyroHeadingAxis(yams.core.mechanisms.config.enums.GyroAxis)
    // (in YAMS main, not yet released). Pass the raw gyro attitude to withGyro() and the axis to
    // withGyroHeadingAxis(GyroAxis.valueOf(name())) instead; YAMS applies a full change of frame.
    public Supplier<Rotation3d> toRobotAttitude(Supplier<Rotation3d> gyroAttitude)
    {
      return switch (this)
      {
        case YAW -> gyroAttitude;
        case PITCH -> () -> new Rotation3d(0, 0, gyroAttitude.get().getY());
        case ROLL -> () -> new Rotation3d(0, 0, gyroAttitude.get().getX());
      };
    }
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
