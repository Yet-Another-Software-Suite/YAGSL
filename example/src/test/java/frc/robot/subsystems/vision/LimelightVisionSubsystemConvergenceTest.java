package frc.robot.subsystems.vision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.simulation.SimHooks;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveDriveSubsystem;
import limelight.Limelight;
import limelight.networktables.LimelightPoseEstimator;
import limelight.networktables.LimelightPoseEstimator.EstimationMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Reproduces two reported bugs where, in simulation, the drivetrain's odometry pose ({@link SwerveDriveSubsystem#getPose()}) failed to
 * converge back to the ground-truth simulated pose ({@link SwerveDriveSubsystem#getSimPose()}) while an AprilTag was supposedly in
 * view and {@link LimelightVisionSubsystem} was fusing vision measurements every loop.
 * <p>
 * Root cause 1 ({@link #odometryConvergesToGroundTruthWhileTagIsVisible()}): {@link LimelightVisionSubsystem}'s camera was mounted
 * only 8in off the ground while field tags sit around 35in up. Once the robot closed to within a couple meters of a tag, the look-up
 * angle exceeded the camera's vertical FOV and the tag silently dropped out of frame - "odometry not converging" was really "vision
 * stopped reporting a tag at all", not a fusion bug.
 * <p>
 * Root cause 2 ({@link #odometryConvergesToGroundTruthWithNonZeroPositionAndHeading()}): {@code Pigeon2#getRotation3d()} is built from
 * the device's quaternion status signals, not its yaw/pitch/roll ones. {@code Pigeon2SimState#setRawYaw()} (used in
 * {@link SwerveDriveSubsystem#simulationPeriodic()}) only sets the separate raw-yaw register, which this Java-only simulation path
 * never fuses back into the quaternion - so {@code getGyroRotation3d()} silently reported identity (0deg) forever whenever the
 * simulated robot's true heading wasn't already 0deg, and MegaTag2 never got fed a real heading to correct rotation with.
 */
class LimelightVisionSubsystemConvergenceTest
{

  @BeforeEach
  void init()
  {
    assertTrue(HAL.initialize(500, 0));
    SimHooks.pauseTiming();
  }

  @AfterEach
  void teardown()
  {
    SimHooks.resumeTiming();
  }

  /**
   * Compute a robot pose sitting {@code distanceMeters} in front of a tag, facing straight back at it.
   */
  private static Pose2d poseFacingTag(AprilTag tag, double distanceMeters)
  {
    Translation2d tagTranslation   = tag.pose.getTranslation().toTranslation2d();
    Rotation2d    tagYaw           = tag.pose.getRotation().toRotation2d();
    Translation2d normal           = new Translation2d(1, 0).rotateBy(tagYaw);
    Translation2d robotTranslation = tagTranslation.plus(normal.times(distanceMeters));
    Rotation2d    facing           = new Rotation2d(Math.atan2(-normal.getY(), -normal.getX()));
    return new Pose2d(robotTranslation, facing);
  }

  private static void runLoop(SwerveDriveSubsystem drivetrain, LimelightVisionSubsystem vision, int iterations)
  {
    for (int i = 0; i < iterations; i++)
    {
      drivetrain.periodic();
      vision.periodic();
      drivetrain.simulationPeriodic();
      vision.simulationPeriodic();
      SimHooks.stepTiming(0.02);
    }
  }

  @Test
  void odometryConvergesToGroundTruthWhileTagIsVisible()
  {
    SwerveDriveSubsystem     drivetrain = new SwerveDriveSubsystem();
    LimelightVisionSubsystem vision     = new LimelightVisionSubsystem(drivetrain);

    AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);
    AprilTag            tag         = fieldLayout.getTags().get(0);
    Pose2d              startPose   = poseFacingTag(tag, 4.0);

    drivetrain.resetOdometry(startPose);
    runLoop(drivetrain, vision, 10);

    // Phase 1: command a burst of translational motion with no vision fusion, to build up a real gap between
    // the drivetrain's ground-truth pose and its actual (motor/encoder based) odometry pose.
    Command driveForward = drivetrain.drive(drivetrain.getAngularVelocityStream(() -> 0.5, () -> 0, () -> 0));
    driveForward.initialize();
    for (int i = 0; i < 25; i++)
    {
      driveForward.execute();
      drivetrain.simulationPeriodic();
      SimHooks.stepTiming(0.02);
    }

    Command stop = drivetrain.drive(drivetrain.getAngularVelocityStream(() -> 0, () -> 0, () -> 0));
    stop.initialize();
    stop.execute();

    double driftBeforeVision = drivetrain.getPose().getTranslation().getDistance(drivetrain.getSimPose().getTranslation());
    assertTrue(driftBeforeVision > 0.02,
               "test setup should have produced measurable odometry drift to correct, got " + driftBeforeVision + "m");

    // Phase 2: sit still with the tag clearly in view and let LimelightVisionSubsystem fuse vision every loop.
    Limelight              reader          = new Limelight("limelight");
    LimelightPoseEstimator readerEstimator = reader.createPoseEstimator(EstimationMode.MEGATAG2);
    int                    visibleCount    = 0;

    for (int i = 0; i < 150; i++)
    {
      stop.execute();
      drivetrain.periodic();
      vision.periodic();
      drivetrain.simulationPeriodic();
      vision.simulationPeriodic();
      SimHooks.stepTiming(0.02);

      if (readerEstimator.getPoseEstimate().map(estimate -> estimate.hasData).orElse(false))
      {
        visibleCount++;
      }
    }

    assertEquals(150, visibleCount, "tag should stay in the camera's view for the entire test at this range");

    Pose2d truth    = drivetrain.getSimPose();
    Pose2d odometry = drivetrain.getPose();

    assertEquals(truth.getX(), odometry.getX(), 0.15,
                 "odometry X should converge back to ground truth while a tag is visible");
    assertEquals(truth.getY(), odometry.getY(), 0.15,
                 "odometry Y should converge back to ground truth while a tag is visible");
    assertEquals(truth.getRotation().getDegrees(), odometry.getRotation().getDegrees(), 5.0,
                 "odometry heading should converge back to ground truth while a tag is visible");
  }

  @Test
  void odometryConvergesToGroundTruthWithNonZeroPositionAndHeading()
  {
    SwerveDriveSubsystem     drivetrain = new SwerveDriveSubsystem();
    LimelightVisionSubsystem vision     = new LimelightVisionSubsystem(drivetrain);

    AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);
    AprilTag            tag         = fieldLayout.getTags().get(0);
    Pose2d              startPose   = poseFacingTag(tag, 4.0);

    drivetrain.resetOdometry(startPose);
    runLoop(drivetrain, vision, 10);

    // Phase 1: translate AND rotate (no vision fusion) so the robot ends up away from the origin with a
    // non-zero heading, not just a shifted position.
    Command driveAndSpin = drivetrain.drive(drivetrain.getAngularVelocityStream(() -> 0.4, () -> 0, () -> 0.05));
    driveAndSpin.initialize();
    for (int i = 0; i < 25; i++)
    {
      driveAndSpin.execute();
      drivetrain.simulationPeriodic();
      SimHooks.stepTiming(0.02);
    }

    Command stop = drivetrain.drive(drivetrain.getAngularVelocityStream(() -> 0, () -> 0, () -> 0));
    stop.initialize();
    stop.execute();

    assertTrue(Math.abs(drivetrain.getSimPose().getRotation().getDegrees()) > 1.0,
               "test setup should have produced a non-zero heading, got " + drivetrain.getSimPose().getRotation().getDegrees() + "deg");

    Limelight              reader          = new Limelight("limelight");
    LimelightPoseEstimator readerEstimator = reader.createPoseEstimator(EstimationMode.MEGATAG2);
    int                    visibleCount    = 0;

    for (int i = 0; i < 150; i++)
    {
      stop.execute();
      drivetrain.periodic();
      vision.periodic();
      drivetrain.simulationPeriodic();
      vision.simulationPeriodic();
      SimHooks.stepTiming(0.02);

      if (readerEstimator.getPoseEstimate().map(estimate -> estimate.hasData).orElse(false))
      {
        visibleCount++;
      }
    }

    assertEquals(150, visibleCount, "tag should stay in the camera's view for the entire test at this range");

    Pose2d truth    = drivetrain.getSimPose();
    Pose2d odometry = drivetrain.getPose();

    assertEquals(truth.getX(), odometry.getX(), 0.15,
                 "odometry X should converge back to ground truth while a tag is visible");
    assertEquals(truth.getY(), odometry.getY(), 0.15,
                 "odometry Y should converge back to ground truth while a tag is visible");
    assertEquals(truth.getRotation().getDegrees(), odometry.getRotation().getDegrees(), 5.0,
                 "odometry heading should converge back to ground truth while a tag is visible");
  }
}
