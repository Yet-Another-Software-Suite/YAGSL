package frc.robot.mechanisms;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Inches;

import java.io.File;
import java.util.List;
import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.math.controller.PIDController;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.smartdashboard.Field2d;
import org.wpilib.system.Filesystem;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.Distance;
import swervelib.commands3.SwerveParser;
import yams.commands3.config.SwerveDriveConfig;
import yams.commands3.swerve.SwerveDrive;
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
   * Get the underlying YAMS {@link SwerveDrive}.
   *
   * @return The swerve drive.
   */
  public SwerveDrive getDrive()
  {
    return drive;
  }

  /**
   * Drive the robot with the given field-relative {@link ChassisVelocities}.
   *
   * @param velocity Field-relative {@link ChassisVelocities} to apply.
   */
  public void drive(ChassisVelocities velocity)
  {
    drive.setFieldRelativeChassisSpeeds(velocity);
  }

  /**
   * Lock the swerve drive wheels in an X pattern so the robot is difficult to push.
   *
   * @return {@link Command} that locks the wheels in place until canceled.
   */
  public Command lockPose()
  {
    return run(coroutine -> {
      while (true)
      {
        drive.lockPose();
        coroutine.yield();
      }
    }).named("Lock Pose");
  }

  /**
   * Lock the swerve drive wheels in an X pattern so the robot is difficult to push.
   *
   * @return {@link Command} that locks the wheels in place until canceled.
   */
  public Command lock()
  {
    return lockPose();
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
   * Get the current heading of the robot, as reported by the gyro.
   *
   * @return {@link Rotation2d} of the robot's heading.
   */
  public Rotation2d getHeading()
  {
    return drive.getGyroRotation3d().toRotation2d();
  }

  /**
   * Get the gyro's 3D orientation.
   *
   * @return {@link Rotation3d} of the gyro.
   */
  public Rotation3d getGyroRotation3d()
  {
    return drive.getGyroRotation3d();
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
}
