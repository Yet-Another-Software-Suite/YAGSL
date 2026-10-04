package frc.robot.mechanisms;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Inches;
import static org.wpilib.units.Units.Radians;

import java.io.File;
import java.util.List;
import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchState;
import org.wpilib.driverstation.XboxController;
import org.wpilib.math.controller.PIDController;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Transform2d;
import org.wpilib.smartdashboard.Field2d;
import org.wpilib.system.Filesystem;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.Distance;
import swervelib.commands3.SwerveParser;
import yams.commands3.config.SwerveDriveConfig;
import yams.commands3.swerve.SwerveDrive;
import yams.commands3.swerve.SwerveInputStream;
import yams.core.telemetry.SwerveDriveTelemetryConfig;
import yams.core.telemetry.enums.TelemetryVerbosity;

/**
 * Swerve drive built by YAGSL from the JSON configuration in {@code deploy/swerve/base}, as a Commands v3
 * {@link Mechanism}.
 */
public class SwerveDriveMechanism implements Mechanism
{

  /**
   * How close {@link #driveToPose(Pose2d)} has to get to its target, in translation, before it ends.
   */
  private static final Distance POSE_TRANSLATION_TOLERANCE = Inches.of(2);
  /**
   * How close {@link #driveToPose(Pose2d)} has to get to its target, in heading, before it ends.
   */
  private static final Angle    POSE_ROTATION_TOLERANCE    = Degrees.of(5);
  /**
   * Controller stick deflection past which the right stick picks a new heading in heading control.
   */
  private static final double   HEADING_STICK_DEADBAND     = 0.5;
  /**
   * Fraction of the robot's maximum speeds used by {@link #driveDemo(CommandXboxController)}.
   */
  private static final double   DEMO_SPEED_SCALE           = 0.3;

  private final SwerveDrive drive;

  /**
   * Parse the YAGSL configuration and create the swerve drive.
   */
  public SwerveDriveMechanism()
  {
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, Rotation2d.ZERO))
        .withMechanism(this)
        .withTranslationController(new PIDController(4, 0, 0))
        // YAMS stops any module asked for less than 0.1 m/s (its default module velocity deadband), so a weak
        // rotation gain leaves a heading error it can never close. At kP = 3 that dead zone is under 4.5 degrees,
        // inside driveToPose's 5 degree tolerance.
        .withRotationController(new PIDController(3, 0, 0))
        .withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    drive = SwerveParser.createSwerveDrive(cfg);
    // Use SwerveParser.createSwerveDriveDevices(cfg) instead to also get the raw vendor devices (motor controllers,
    // encoders, and the gyro) YAGSL created.
  }

  /**
   * Field relative drive with angular velocity control. Every axis is negated because Xbox sticks read negative when
   * pushed forward/left:
   * <ul>
   *   <li>Left stick: translation. Forward drives away from the driver station, left drives left. Alliance relative,
   *   so it is flipped on the red alliance.</li>
   *   <li>Right stick X: spin. Right spins clockwise.</li>
   * </ul>
   *
   * @param controller Driver controller.
   * @return {@link Command} that drives until it is interrupted.
   */
  public Command driveAngularVelocity(CommandXboxController controller)
  {
    XboxController hid = controller.getController();
    return run(coroutine -> {
      SwerveInputStream input = new SwerveInputStream(drive)
          .withDeadband(0.05)
          .withAllianceRelativeControl(true);
      while (true)
      {
        input.withTranslation(-hid.getLeftY(), -hid.getLeftX())
             .withRotation(-hid.getRightX());
        drive.setFieldRelativeChassisSpeeds(input.get());
        coroutine.yield();
      }
    }).named("Drive Angular Velocity");
  }

  /**
   * Field relative drive with heading control. Every axis is negated because Xbox sticks read negative when pushed
   * forward/left:
   * <ul>
   *   <li>Left stick: translation. Forward drives away from the driver station, left drives left. Alliance relative,
   *   so it is flipped on the red alliance.</li>
   *   <li>Right stick: points the direction to face, so up faces away from the driver station and left faces left.
   *   With the stick centered the robot keeps its current heading.</li>
   * </ul>
   *
   * @param controller Driver controller.
   * @return {@link Command} that drives until it is interrupted.
   */
  public Command driveHeading(CommandXboxController controller)
  {
    XboxController hid = controller.getController();
    return run(coroutine -> {
      SwerveInputStream input = new SwerveInputStream(drive)
          .withDeadband(0.05)
          .withAllianceRelativeControl(true)
          .withHeadingControl(true);
      while (true)
      {
        double headingX = -hid.getRightX();
        double headingY = -hid.getRightY();
        // Hold the current heading unless the stick is pushed far enough to pick a new one.
        double heading = new Rotation2d(drive.getGyroAngle()).getRadians();
        if (Math.hypot(headingX, headingY) > HEADING_STICK_DEADBAND)
        {
          // The stick points at the heading to face. Translation is alliance relative, so flip the heading on the
          // red alliance to match.
          heading = Math.atan2(headingX, headingY) + (isRedAlliance() ? Math.PI : 0);
        }
        input.withTranslation(-hid.getLeftY(), -hid.getLeftX())
             .withHeading(Radians.of(heading));
        drive.setFieldRelativeChassisSpeeds(input.get());
        coroutine.yield();
      }
    }).named("Drive Heading");
  }

  /**
   * Slow, robot relative drive with angular velocity control, for demos and driving around people. Speeds are scaled
   * to {@code DEMO_SPEED_SCALE} of the robot's maximum.
   * <ul>
   *   <li>Left stick: translation relative to the robot. Forward drives the way the robot is facing, left drives to
   *   the robot's left.</li>
   *   <li>Right stick X: spin. Right spins clockwise.</li>
   * </ul>
   *
   * @param controller Driver controller.
   * @return {@link Command} that drives until it is interrupted.
   */
  public Command driveDemo(CommandXboxController controller)
  {
    XboxController hid = controller.getController();
    return run(coroutine -> {
      SwerveInputStream input = new SwerveInputStream(drive)
          .withDeadband(0.05)
          .withRobotRelative(true)
          .withScaleTranslation(DEMO_SPEED_SCALE)
          .withScaleRotation(DEMO_SPEED_SCALE);
      while (true)
      {
        input.withTranslation(-hid.getLeftY(), -hid.getLeftX())
             .withRotation(-hid.getRightX());
        // The stream converts robot relative input into field relative velocities.
        drive.setFieldRelativeChassisSpeeds(input.get());
        coroutine.yield();
      }
    }).named("Drive Demo Mode");
  }

  /**
   * Drive to a series of poses relative to wherever the robot is when the command starts, one after another, with
   * {@link #driveToPose(Pose2d)}, then hold the last one. Because the poses are relative, the routine works from any
   * starting position and on either alliance.
   *
   * @param name      Name of the command.
   * @param waypoints Poses to drive to, each relative to the robot's pose when the command starts.
   * @return {@link Command} that drives to each pose in turn and holds the last one until it is canceled.
   */
  public Command driveRelativeWaypoints(String name, List<Transform2d> waypoints)
  {
    return run(coroutine -> {
      Pose2d start = getPose();
      for (int i = 0; i < waypoints.size() - 1; i++)
      {
        coroutine.await(driveToPose(start.transformBy(waypoints.get(i))));
      }
      // driveToPose ends as soon as the robot is within tolerance, even if it is still moving fast, so the last
      // waypoint is held instead. Otherwise the robot would coast past it once the routine ended.
      coroutine.await(drive.driveToPose(start.transformBy(waypoints.get(waypoints.size() - 1))));
    }).named(name);
  }

  /**
   * Drive straight to the given pose with YAMS' {@link SwerveDrive#driveToPose(Pose2d, Distance, Angle)}, using the
   * translation and rotation PID controllers from the {@link SwerveDriveConfig}. There is no path planning (Commands
   * v3 has no PathPlanner support), so nothing steers it around obstacles.
   *
   * @param pose {@link Pose2d} to drive to. Field relative, blue-origin where 0deg is facing towards RED alliance.
   * @return {@link Command} that drives to the pose, ending once it is within 2in and 5deg of it.
   */
  public Command driveToPose(Pose2d pose)
  {
    return drive.driveToPose(pose, POSE_TRANSLATION_TOLERANCE, POSE_ROTATION_TOLERANCE);
  }

  /**
   * Zero the gyro, resetting the robot's heading to face away from the driver station (0deg, red alliance side).
   *
   * @return {@link Command} that zeroes the gyro.
   */
  public Command zeroGyro()
  {
    return run(coroutine -> drive.zeroGyro()).named("Zero Gyro");
  }

  /**
   * Gets the measured pose (position and rotation) of the robot, as reported by odometry.
   *
   * @return The robot's pose.
   */
  public Pose2d getPose()
  {
    return drive.getPose();
  }

  /**
   * Get the {@link Field2d} used to display the robot's pose.
   *
   * @return {@link Field2d} of the drive.
   */
  public Field2d getField2d()
  {
    return drive.getField2d();
  }

  /**
   * Update odometry and telemetry. Call every loop; Commands v3 mechanisms have no periodic() hook of their own.
   */
  public void periodic()
  {
    drive.updateTelemetry();
  }

  /**
   * Step the swerve drive simulation. Call every loop in simulation.
   */
  public void simulationPeriodic()
  {
    drive.simIterate();
  }

  private static boolean isRedAlliance()
  {
    return MatchState.getAlliance().orElse(Alliance.BLUE) == Alliance.RED;
  }
}
