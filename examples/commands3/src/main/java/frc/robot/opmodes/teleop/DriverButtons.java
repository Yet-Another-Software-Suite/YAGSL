package frc.robot.opmodes.teleop;

import static org.wpilib.units.Units.Meters;

import frc.robot.Robot;
import org.wpilib.command3.Command;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import yams.commands3.swerve.SwerveInputStream;

/**
 * Button bindings shared by the teleop opmodes. Opmodes call {@link #bind} from their constructor, so the
 * bindings only exist while that opmode is selected.
 */
final class DriverButtons
{

  /**
   * Default speed scale when not boosting.
   */
  public static final double NORMAL_SPEED_SCALE = 0.5;
  /**
   * Boosted speed scale when the boost button is held.
   */
  public static final double BOOST_SPEED_SCALE  = 1.0;

  private DriverButtons()
  {
  }

  /**
   * Bind the shared driver buttons without speed boost (e.g. for demo mode).
   * <ul>
   *   <li>Y (hold): drive to a demo point with YAMS' driveToPose.</li>
   *   <li>X (hold): lock the wheels in an X pattern so the robot cannot easily be pushed.</li>
   *   <li>Menu + View: zero the gyro.</li>
   * </ul>
   *
   * @param robot      The robot instance to control.
   * @param driverXbox The driver controller created by the opmode.
   */
  static void bind(Robot robot, CommandXboxController driverXbox)
  {
    driverXbox.y().whileTrue(robot.swerve.driveToPose(new Pose2d(Meters.of(3), Meters.of(3),
                                                                 Rotation2d.fromDegrees(180))));
    driverXbox.x().whileTrue(robot.swerve.lockPose());
    driverXbox.menu().and(driverXbox.view()).onTrue(robot.swerve.zeroGyro());
  }

  /**
   * Bind the shared driver buttons including speed boost for standard teleop opmodes.
   * <ul>
   *   <li>Right Bumper (hold): increase drive speed scale from 50% to 100%.</li>
   *   <li>Y (hold): drive to a demo point with YAMS' driveToPose.</li>
   *   <li>X (hold): lock the wheels in an X pattern so the robot cannot easily be pushed.</li>
   *   <li>Menu + View: zero the gyro.</li>
   * </ul>
   *
   * @param robot       The robot instance to control.
   * @param driverXbox  The driver controller created by the opmode.
   * @param driveStream The swerve input stream of the opmode to boost.
   */
  static void bind(Robot robot, CommandXboxController driverXbox, SwerveInputStream driveStream)
  {
    bind(robot, driverXbox);
    driverXbox.rightBumper().whileTrue(
        Command.noRequirements(coroutine -> {
          driveStream.withScaleTranslation(BOOST_SPEED_SCALE)
                     .withScaleRotation(BOOST_SPEED_SCALE);
          while (true)
          {
            coroutine.yield();
          }
        }).whenCanceled(() -> driveStream.withScaleTranslation(NORMAL_SPEED_SCALE)
                                         .withScaleRotation(NORMAL_SPEED_SCALE))
               .named("Speed Boost")
    );
  }
}
