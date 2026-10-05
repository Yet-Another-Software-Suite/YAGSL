package frc.robot.opmodes.teleop;

import static org.wpilib.units.Units.Radians;

import frc.robot.Robot;
import org.wpilib.command3.Command;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchState;
import org.wpilib.driverstation.XboxController;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;
import yams.commands3.swerve.SwerveInputStream;

/**
 * Field relative drive where the right stick points the way the robot should face (up faces away from the driver station). With the stick centered the robot holds its heading.
 *
 * <p>The left stick translates in every teleop opmode. See {@link DriverButtons} for the buttons they share.
 */
@Teleop(name = "Heading Control")
public class HeadingTeleop implements OpMode
{

  private static final double         HEADING_STICK_DEADBAND = 0.5;

  private final CommandXboxController driverXbox             = new CommandXboxController(0);
  private final SwerveInputStream     driveStream;

  /**
   * Creates the teleop opmode. The OpModeRobot framework calls this when the opmode is selected on the driver station,
   * so the bindings below only exist while it is selected.
   *
   * @param robot The robot instance to control.
   */
  public HeadingTeleop(Robot robot)
  {
    XboxController hid = driverXbox.getController();
    driveStream = new SwerveInputStream(robot.swerve.getDrive(), () -> -hid.getLeftY(), () -> -hid.getLeftX())
        .withDeadband(0.05)
        .withScaleTranslation(DriverButtons.NORMAL_SPEED_SCALE)
        .withScaleRotation(DriverButtons.NORMAL_SPEED_SCALE)
        .withAllianceRelativeControl(true)
        .withHeading(() -> {
          double headingX = -hid.getRightX();
          double headingY = -hid.getRightY();
          // Hold the current heading unless the stick is pushed far enough to pick a new one.
          double heading = robot.swerve.getGyroRotation3d().toRotation2d().getRadians();
          if (Math.hypot(headingX, headingY) > HEADING_STICK_DEADBAND)
          {
            // The stick points at the heading to face. Translation is alliance relative, so flip the heading on the
            // red alliance to match.
            heading = Math.atan2(headingX, headingY) + (isRedAlliance() ? Math.PI : 0);
          }
          return Radians.of(heading);
        })
        .withHeadingControl(true);

    Command driveCommand = robot.swerve.run(coroutine -> {
      while (true)
      {
        robot.swerve.drive(driveStream.get());
        coroutine.yield();
      }
    }).named("Drive Heading");

    RobotModeTriggers.teleop().whileTrue(driveCommand);
    DriverButtons.bind(robot, driverXbox, driveStream);
  }

  private static boolean isRedAlliance()
  {
    return MatchState.getAlliance().orElse(Alliance.BLUE) == Alliance.RED;
  }
}
