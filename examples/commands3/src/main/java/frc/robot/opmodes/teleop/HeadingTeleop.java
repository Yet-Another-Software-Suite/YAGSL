package frc.robot.opmodes.teleop;

import frc.robot.Robot;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;

/**
 * Field relative drive where the right stick points the way the robot should face (up faces away from the driver station). With the stick centered the robot holds its heading.
 *
 * <p>The left stick translates in every teleop opmode. See {@link DriverButtons} for the buttons they share.
 */
@Teleop(name = "Heading Control")
public class HeadingTeleop implements OpMode
{

  /**
   * Creates the teleop opmode. The OpModeRobot framework calls this when the opmode is selected on the driver station,
   * so the bindings below only exist while it is selected.
   *
   * @param robot The robot instance to control.
   */
  public HeadingTeleop(Robot robot)
  {
    RobotModeTriggers.teleop().whileTrue(robot.swerve.driveHeading(robot.driverXbox));
    DriverButtons.bind(robot);
  }
}
