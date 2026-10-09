package frc.robot.subsystems.vision;

import static edu.wpi.first.units.Units.Inches;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.swervedrive.SwerveDriveSubsystem;
import java.util.List;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.simulation.PhotonCameraSim;
import org.photonvision.simulation.SimCameraProperties;
import org.photonvision.simulation.VisionSystemSim;
import org.photonvision.targeting.PhotonPipelineResult;

/**
 * Fuses a PhotonVision camera's AprilTag pose estimate into the {@link SwerveDriveSubsystem}'s pose estimator every
 * loop, filtering out single-tag, high-ambiguity reads before they're trusted. Runs a {@link VisionSystemSim} in
 * simulation so the same code path produces (simulated) detections on the desktop.
 */
public class PhotonVisionSubsystem extends SubsystemBase
{

  private static final String CAMERA_NAME = "photonvision";

  /**
   * Camera mount offset from robot center: 5in forward, 8in up, facing straight ahead.
   */
  private static final Transform3d ROBOT_TO_CAMERA = new Transform3d(
      new Translation3d(Inches.of(5), Inches.of(0), Inches.of(8)), new Rotation3d());

  /**
   * Single-tag estimates with an ambiguity above this are considered untrustworthy.
   */
  private static final double MAX_SINGLE_TAG_AMBIGUITY = 0.2;

  private final SwerveDriveSubsystem drivetrain;
  private final PhotonCamera         camera;
  private final PhotonPoseEstimator  poseEstimator;

  // Only populated in simulation; the real camera runs the pose solve on its own coprocessor.
  private final VisionSystemSim  visionSim;
  private final PhotonCameraSim  cameraSim;

  public PhotonVisionSubsystem(SwerveDriveSubsystem drivetrain)
  {
    this.drivetrain = drivetrain;

    AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);

    camera = new PhotonCamera(CAMERA_NAME);
    poseEstimator = new PhotonPoseEstimator(fieldLayout, ROBOT_TO_CAMERA);

    if (RobotBase.isSimulation())
    {
      visionSim = new VisionSystemSim("main");
      visionSim.addAprilTags(fieldLayout);

      SimCameraProperties cameraProps = new SimCameraProperties();
      cameraProps.setCalibration(960, 720, Rotation2d.fromDegrees(90));
      cameraProps.setCalibError(0.25, 0.10);
      cameraProps.setFPS(20);
      cameraProps.setAvgLatencyMs(35);
      cameraProps.setLatencyStdDevMs(5);

      cameraSim = new PhotonCameraSim(camera, cameraProps);
      cameraSim.enableDrawWireframe(true);
      visionSim.addCamera(cameraSim, ROBOT_TO_CAMERA);
    } else
    {
      visionSim = null;
      cameraSim = null;
    }
  }

  @Override
  public void periodic()
  {
    List<PhotonPipelineResult> results = camera.getAllUnreadResults();
    for (PhotonPipelineResult result : results)
    {
      estimatePose(result).ifPresent(this::processEstimate);
    }
  }

  /**
   * Estimate the robot's pose from a pipeline result, preferring a multi-tag solve done on the coprocessor and
   * falling back to the single lowest-ambiguity tag when fewer than two tags are visible.
   *
   * @param result Pipeline result to estimate a pose from.
   * @return {@link EstimatedRobotPose} if one could be produced.
   */
  private Optional<EstimatedRobotPose> estimatePose(PhotonPipelineResult result)
  {
    Optional<EstimatedRobotPose> multiTagEstimate = poseEstimator.estimateCoprocMultiTagPose(result);
    return multiTagEstimate.isPresent() ? multiTagEstimate : poseEstimator.estimateLowestAmbiguityPose(result);
  }

  /**
   * Filter and fuse a {@link EstimatedRobotPose} into the drivetrain's pose estimator.
   *
   * @param estimate Pose estimate produced by {@link #estimatePose(PhotonPipelineResult)}.
   */
  private void processEstimate(EstimatedRobotPose estimate)
  {
    // A single tag viewed at a bad angle can be ambiguous (multiple plausible poses); multi-tag solves don't have
    // this problem, so only filter on ambiguity when exactly one tag was used.
    if (estimate.targetsUsed.size() == 1 && estimate.targetsUsed.get(0).getPoseAmbiguity() > MAX_SINGLE_TAG_AMBIGUITY)
    {
      return;
    }
    Pose2d pose = estimate.estimatedPose.toPose2d();
    drivetrain.addVisionMeasurement(pose, estimate.timestampSeconds);
    drivetrain.getField2d().getObject("PhotonVision Pose").setPose(pose);
  }

  @Override
  public void simulationPeriodic()
  {
    visionSim.update(drivetrain.getPose());
  }
}
