package frc.robot.opmodes.teleop;

import frc.robot.Robot;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;

/**
 * Field relative drive where the right stick X spins the robot (right spins clockwise).
 *
 * <p>The left stick translates in every teleop opmode. See {@link DriverButtons} for the buttons they share.
 */
@Teleop(name = "Angular Velocity Control")
public class AngularVelocityTeleop implements OpMode
{

  /**
   * Creates the teleop opmode. The OpModeRobot framework calls this when the opmode is selected on the driver station,
   * so the bindings below only exist while it is selected.
   *
   * @param robot The robot instance to control.
   */
  public AngularVelocityTeleop(Robot robot)
  {
    RobotModeTriggers.teleop().whileTrue(robot.swerve.driveAngularVelocity(robot.driverXbox));
    DriverButtons.bind(robot);
  }
}
