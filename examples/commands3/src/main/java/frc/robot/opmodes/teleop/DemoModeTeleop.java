package frc.robot.opmodes.teleop;

import frc.robot.Robot;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;

/**
 * Slow (30% speed), robot relative drive where the right stick X spins the robot. For demos and driving around people: the left stick moves the robot the way it is facing, not relative to the field.
 *
 * <p>The left stick translates in every teleop opmode. See {@link DriverButtons} for the buttons they share.
 */
@Teleop(name = "Demo Mode")
public class DemoModeTeleop implements OpMode
{

  /**
   * Creates the teleop opmode. The OpModeRobot framework calls this when the opmode is selected on the driver station,
   * so the bindings below only exist while it is selected.
   *
   * @param robot The robot instance to control.
   */
  public DemoModeTeleop(Robot robot)
  {
    RobotModeTriggers.teleop().whileTrue(robot.swerve.driveDemo(robot.driverXbox));
    DriverButtons.bind(robot);
  }
}
