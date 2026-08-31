package frc.robot.subsystems.vision;

import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.swervedrive.SwerveDriveSubsystem;
import java.util.Optional;
import limelight.Limelight;
import limelight.networktables.LimelightPoseEstimator;
import limelight.networktables.LimelightPoseEstimator.EstimationMode;
import limelight.networktables.LimelightSettings.LEDMode;
import limelight.networktables.Orientation3d;
import limelight.networktables.PoseEstimate;
import limelight.sim.LimelightSim;

/**
 * Fuses a Limelight's AprilTag pose estimate (via YALL) into the {@link SwerveDriveSubsystem}'s pose estimator every loop, filtering out distant or ambiguous readings before they're trusted.
 */
public class LimelightVisionSubsystem extends SubsystemBase
{

  /**
   * Readings further than this from the robot are considered too noisy to fuse.
   */
  private static final double MAX_TAG_DISTANCE_METERS = 4.0;

  /**
   * Readings with a minimum tag ambiguity above this are considered untrustworthy.
   */
  private static final double MAX_TAG_AMBIGUITY = 0.3;

  /**
   * Camera mount offset from robot center.
   * <p>
   * The mount height matters more than it looks: field AprilTags sit around 35in (0.89m) up, and this camera's ~50deg vertical FOV only spans about +/-25deg. Mounted too low (this used to be 8in),
   * the tag's look-up angle blows past the top of the frame the moment the robot closes to within a couple meters, so vision silently stops correcting odometry right when it's needed most. 24in keeps
   * tags in frame down to a much closer range.
   */
  private static final Pose3d                 CAMERA_OFFSET        = new Pose3d(Inches.of(5).in(Meters),
                                                                                Inches.of(0).in(Meters),
                                                                                Inches.of(24).in(Meters),
                                                                                Rotation3d.kZero);
  private final        boolean                updateDrivetrainPose = true;
  private final        SwerveDriveSubsystem   drivetrain;
  private final        Limelight              limelight;
  private final        LimelightSim           limelightSim;
  private final        LimelightPoseEstimator poseEstimator;

  /**
   * Publishes the fused Limelight pose estimate to NetworkTables for dashboards and logging.
   */
  private final StructPublisher<Pose2d> visionReadingPosePublisher = NetworkTableInstance.getDefault()
                                                                                         .getStructTopic("Limelight/reading",
                                                                                                         Pose2d.struct)
                                                                                         .publish();
  /**
   * Publishes the fused Limelight pose estimate to NetworkTables for dashboards and logging.
   */
  private final StructPublisher<Pose2d> visionPosePublisher        = NetworkTableInstance.getDefault()
                                                                                         .getStructTopic("Limelight/poseEstimate",
                                                                                                         Pose2d.struct)
                                                                                         .publish();

  /**
   * Publishes the simulated robot pose to NetworkTables for dashboards and logging.
   */
  private final StructPublisher<Pose2d> simPosePublisher = NetworkTableInstance.getDefault()
                                                                               .getStructTopic("Limelight/simulation",
                                                                                               Pose2d.struct)
                                                                               .publish();

  /**
   * Odometry-only pose estimate that never gets vision fused into it, so it can be compared side by side against {@link #drivetrain}'s pose estimator (which does fuse vision) to see how much of a
   * difference vision makes.
   */
  private final SwerveDrivePoseEstimator visionPoseEstimator;

  public LimelightVisionSubsystem(SwerveDriveSubsystem drivetrain)
  {
    this.drivetrain = drivetrain;

    limelight = new Limelight("limelight");
    limelight.getSettings()
             .withLimelightLEDMode(LEDMode.PipelineControl)
             .withCameraOffset(CAMERA_OFFSET)
             .save();
    limelightSim = new LimelightSim(limelight);
    limelightSim.withRobotToCameraTransform(new Transform3d(CAMERA_OFFSET.getTranslation(), CAMERA_OFFSET.getRotation()));
//        .withField2d(drivetrain.getField2d());

    poseEstimator = limelight.createPoseEstimator(EstimationMode.MEGATAG2);

    visionPoseEstimator = drivetrain.createPoseEstimator();
  }

  @Override
  public void periodic()
  {
    // MegaTag2 fuses tag detections with the robot's current heading, so it must be submitted every loop before
    // reading a pose estimate.
    //
    // Only yaw is actually required for MegaTag2. SwerveDriveSubsystem.getGyroRotation3d() and
    // getGyroAngularVelocity() happen to report real pitch, roll, and per axis angular velocity here because they
    // read straight off the Pigeon2 obtained through SwerveParser.createSwerveDriveDevices(), see
    // "How to access raw hardware devices" in the docs. That's not a requirement though. Any gyro that only reports
    // yaw (NavX, ADIS16470, ADXRS450, or otherwise) is perfectly sufficient for MegaTag2, and feeding real pitch and
    // roll from a full IMU is optional. Depending on your camera mount and how noisy that extra data is, it could
    // help or hurt the resulting pose just as easily.
    limelight.getSettings()
             .withRobotOrientation(new Orientation3d(drivetrain.getGyroRotation3d(),
                                                     drivetrain.getGyroAngularVelocity()))
             .save();

    Optional<PoseEstimate> visionEstimate = poseEstimator.getPoseEstimate();
    visionEstimate.ifPresent(poseEstimate -> {
      // Reject long-range or ambiguous reads before fusing them into odometry.
      if (poseEstimate.avgTagDist < MAX_TAG_DISTANCE_METERS
          && poseEstimate.tagCount > 0
          && poseEstimate.getMinTagAmbiguity() < MAX_TAG_AMBIGUITY)
      {
        Pose2d pose = poseEstimate.pose.toPose2d();
        visionPoseEstimator.addVisionMeasurement(pose, poseEstimate.timestampSeconds);
        if (updateDrivetrainPose)
        {drivetrain.addVisionMeasurement(pose, poseEstimate.timestampSeconds);}
        drivetrain.getField2d().getObject("Limelight Pose").setPose(pose);
        visionReadingPosePublisher.set(pose);
      }
    });
  }

  public void simulationPeriodic()
  {
    drivetrain.updatePoseEstimator(visionPoseEstimator);
    Pose2d simPose = drivetrain.getSimPose();
    drivetrain.getField2d().getObject("SimPose").setPose(simPose);
    simPosePublisher.set(simPose);
    limelightSim.update(simPose);
    visionPosePublisher.set(visionPoseEstimator.getEstimatedPosition());
  }
}
