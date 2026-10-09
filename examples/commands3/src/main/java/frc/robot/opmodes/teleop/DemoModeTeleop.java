package frc.robot.opmodes.teleop;

import frc.robot.Robot;
import org.wpilib.command3.Command;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.driverstation.XboxController;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;
import yams.commands3.swerve.SwerveInputStream;

/**
 * Slow (30% speed), robot relative drive where the right stick X spins the robot. For demos and driving around people: the left stick moves the robot the way it is facing, not relative to the field.
 *
 * <p>The left stick translates in every teleop opmode. See {@link DriverButtons} for the buttons they share.
 */
@Teleop(name = "Demo Mode")
public class DemoModeTeleop implements OpMode
{

  private static final double         DEMO_SPEED_SCALE = 0.3;

  private final CommandXboxController driverXbox       = new CommandXboxController(0);
  private final SwerveInputStream     driveStream;

  /**
   * Creates the teleop opmode. The OpModeRobot framework calls this when the opmode is selected on the driver station,
   * so the bindings below only exist while it is selected.
   *
   * @param robot The robot instance to control.
   */
  public DemoModeTeleop(Robot robot)
  {
    XboxController hid = driverXbox.getController();
    driveStream = SwerveInputStream.of(robot.swerve.getDrive(), () -> -hid.getLeftY(), () -> -hid.getLeftX())
        .withControllerRotationAxis(() -> -hid.getRightX())
        .withDeadband(0.05)
        .withRobotRelative()
        .withScaleTranslation(DEMO_SPEED_SCALE)
        .withScaleRotation(DEMO_SPEED_SCALE);

    Command driveCommand = robot.swerve.run(coroutine -> {
      while (true)
      {
        robot.swerve.drive(driveStream.get());
        coroutine.yield();
      }
    }).named("Drive Demo Mode");

    RobotModeTriggers.teleop().whileTrue(driveCommand);
    DriverButtons.bind(robot, driverXbox);
  }
}
