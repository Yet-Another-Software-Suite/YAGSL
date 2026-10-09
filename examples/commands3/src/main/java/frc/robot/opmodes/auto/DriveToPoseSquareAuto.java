package frc.robot.opmodes.auto;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Meters;

import frc.robot.Robot;
import java.util.List;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Transform2d;
import org.wpilib.opmode.Autonomous;
import org.wpilib.opmode.OpMode;

/**
 * Drives a 1.5m square, turning a quarter turn at each corner, and finishes back where it started.
 *
 * <p>Built from YAMS' driveToPose, since Commands v3 has no PathPlanner support. Each waypoint is relative to wherever
 * the robot is when autonomous starts (x forward, y left), so the routine works from any starting position and on
 * either alliance.
 */
@Autonomous(name = "Drive To Pose Square")
public class DriveToPoseSquareAuto implements OpMode
{

  /**
   * Waypoints, relative to the robot's pose when autonomous starts.
   */
  static final List<Transform2d> WAYPOINTS = List.of(
      new Transform2d(Meters.of(1.5), Meters.of(0), new Rotation2d(Degrees.of(90))),
      new Transform2d(Meters.of(1.5), Meters.of(1.5), new Rotation2d(Degrees.of(180))),
      new Transform2d(Meters.of(0), Meters.of(1.5), new Rotation2d(Degrees.of(270))),
      new Transform2d(Meters.of(0), Meters.of(0), new Rotation2d(Degrees.of(0))));

  /**
   * Creates the autonomous opmode. The OpModeRobot framework calls this when the opmode is selected on the driver
   * station.
   *
   * @param robot The robot instance to control.
   */
  public DriveToPoseSquareAuto(Robot robot)
  {
    RobotModeTriggers.autonomous().whileTrue(robot.swerve.driveRelativeWaypoints("Drive To Pose Square", WAYPOINTS));
  }
}
