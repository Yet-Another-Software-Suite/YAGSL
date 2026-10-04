package frc.robot.opmodes.teleop;

import static org.wpilib.units.Units.Meters;

import frc.robot.Robot;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;

/**
 * Button bindings shared by the teleop opmodes. Opmodes call {@link #bind(Robot)} from their constructor, so the
 * bindings only exist while that opmode is selected.
 */
final class DriverButtons
{

  private DriverButtons()
  {
  }

  /**
   * Bind the shared driver buttons.
   * <ul>
   *   <li>Y (hold): drive to a demo point with YAMS' driveToPose.</li>
   *   <li>Menu + View: zero the gyro.</li>
   * </ul>
   *
   * @param robot The robot instance to control.
   */
  static void bind(Robot robot)
  {
    robot.driverXbox.y().whileTrue(robot.swerve.driveToPose(new Pose2d(Meters.of(3), Meters.of(3),
                                                                       Rotation2d.fromDegrees(180))));
    robot.driverXbox.menu().and(robot.driverXbox.view()).onTrue(robot.swerve.zeroGyro());
  }
}
