package frc.robot.subsystems.swervedrive;


import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.DegreesPerSecondPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathConstraints;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import java.io.File;
import java.io.IOException;
import java.util.function.DoubleSupplier;
import limelight.networktables.AngularVelocity3d;
import org.json.simple.parser.ParseException;
import swervelib.parser.SwerveParser;
import swervelib.parser.SwerveParser.SwerveDriveDevices;
import yams.mechanisms.config.SwerveDriveConfig;
import yams.mechanisms.swerve.SwerveDrive;
import yams.mechanisms.swerve.SwerveModule;
import yams.mechanisms.swerve.utility.SwerveInputStream;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;
import yams.telemetry.SwerveDriveTelemetryConfig;

public class SwerveDriveSubsystem extends SubsystemBase
{

  /**
   * Constraints used when pathfinding to a point with PathPlanner, matching the defaults configured in the
   * PathPlanner GUI settings.
   */
  private static final PathConstraints PATHFINDING_CONSTRAINTS = new PathConstraints(
      MetersPerSecond.of(3.0), MetersPerSecondPerSecond.of(3.0),
      DegreesPerSecond.of(540), DegreesPerSecondPerSecond.of(720));

  private SwerveDrive drive;
  private Pigeon2 gyro;
  private StatusSignal<AngularVelocity> gyroX;
  private StatusSignal<AngularVelocity> gyroY;
  private StatusSignal<AngularVelocity> gyroZ;


  public SwerveDriveSubsystem()
  {
    SmartDashboard.putData(this);
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, Rotation2d.kZero))
        .withSubsystem(this)
        .withTranslationController(new PIDController(4, 0, 0))
        .withRotationController(new PIDController(1, 0, 0))
        .withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    SwerveDriveDevices devices = SwerveParser.createSwerveDriveDevices(cfg);
    drive = devices.swerveDrive();
    gyro = (Pigeon2)devices.gyro();
    gyroX = gyro.getAngularVelocityXDevice();
    gyroY = gyro.getAngularVelocityYDevice();
    gyroZ = gyro.getAngularVelocityZDevice();
    // You can also create the SwerveDrive without the ability to retrieve the devices like this.
    // drive = SwerveParser.createSwerveDrive(cfg);

    configurePathPlanner();
  }

  /**
   * Configure {@link AutoBuilder} to {@link SwerveDrive} so that {@link Command}s built from PathPlanner paths and
   * autos can drive this subsystem.
   */
  private void configurePathPlanner()
  {
    RobotConfig config;
    try
    {
      config = RobotConfig.fromGUISettings();
    } catch (IOException | ParseException e)
    {
      throw new RuntimeException("Failed to load PathPlanner GUI settings", e);
    }

    AutoBuilder.configure(
        drive::getPose,
        drive::resetOdometry,
        drive::getRobotRelativeSpeed,
        (speeds, feedforwards) -> drive.setRobotRelativeChassisSpeeds(speeds),
        new PPHolonomicDriveController(new PIDConstants(5.0, 0.0, 0.0), new PIDConstants(5.0, 0.0, 0.0)),
        config,
        () -> DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red,
        this
    );
  }

  /**
   * Drive to the given point on the field using PathPlanner's on-the-fly pathfinding.
   *
   * @param point {@link Pose2d} to drive to. Field relative, blue-origin where 0deg is facing towards RED alliance.
   * @return {@link Command} that pathfinds to the given point.
   */
  public Command driveToPointPathPlanner(Pose2d point)
  {
    return AutoBuilder.pathfindToPose(point, PATHFINDING_CONSTRAINTS);
  }

  /**
   * Drive to the given point on the field using YAMS' {@link SwerveDrive#driveToPose(Pose2d)}.
   *
   * @param point {@link Pose2d} to drive to. Field relative, blue-origin where 0deg is facing towards RED alliance.
   * @return {@link Command} that drives to the given point.
   */
  public Command driveToPointYAMS(Pose2d point)
  {
    return drive.driveToPose(point);
  }

  /**
   * Zero the gyro, resetting the robot's heading to face away from the driver station (0deg, red alliance side).
   *
   * @return {@link Command} that zeroes the gyro.
   */
  public Command zeroGyro()
  {
    return Commands.runOnce(drive::zeroGyro, this).withName("Zero Gyro");
  }

  public SwerveInputStream getAngularVelocityStream(DoubleSupplier x, DoubleSupplier y, DoubleSupplier rot)
  {
    return new SwerveInputStream(drive, x, y, rot);
  }

  /**
   * Get the current heading of the robot, as reported by odometry.
   *
   * @return {@link Rotation2d} of the robot's heading.
   */
  public Rotation2d getHeading()
  {
    return new Rotation2d(drive.getGyroAngle());
  }

  /**
   * Get the gyro's full 3 axis orientation (roll, pitch, and yaw) as a {@link Rotation3d}.
   * <p>
   * {@link SwerveDrive} itself only tracks a single yaw {@link edu.wpi.first.units.measure.Angle} (see
   * {@link SwerveDrive#getGyroAngle()}), since yaw is the only axis MegaTag2 pose estimation actually requires. This
   * subsystem grabs the raw {@link Pigeon2} device instead (via {@link SwerveParser#createSwerveDriveDevices}, see
   * "How to access raw hardware devices" in the docs) so it can report the IMU's real roll and pitch too. That's a
   * nice to have here, not a requirement. Any gyro that only reports yaw (NavX, ADIS16470, ADXRS450, or otherwise)
   * still works fine for MegaTag2, and feeding it real roll and pitch could help or hurt the resulting pose
   * depending on your camera mount and how noisy that data is, so don't treat it as free accuracy.
   *
   * @return {@link Rotation3d} of the gyro.
   */
  public Rotation3d getGyroRotation3d()
  {
    // Pigeon2#getRotation3d() is built entirely from the device's quaternion status signals, not from the
    // yaw/pitch/roll ones. Pigeon2SimState#setRawYaw() (see simulationPeriodic()) only sets that separate raw-yaw
    // register - nothing in this Java-only simulation path fuses it back into the quaternion, so getRotation3d()
    // silently reports identity forever in simulation. Feed MegaTag2 the known-good simulated ground truth instead.
    if (RobotBase.isSimulation())
    {
      return new Rotation3d(0, 0, drive.getSimPose().getRotation().getRadians());
    }
    return gyro.getRotation3d();
  }

  /**
   * Get the robot's full 3 axis angular velocity (roll, pitch, and yaw rates), read directly off the raw
   * {@link Pigeon2} device obtained via {@link SwerveParser#createSwerveDriveDevices}. As with
   * {@link #getGyroRotation3d()}, only the yaw rate is required for MegaTag2. The roll and pitch rates are extra
   * accuracy this subsystem happens to have available because it grabbed the raw gyro, not something every robot
   * needs to supply.
   *
   * @return {@link AngularVelocity3d} of the gyro's roll, pitch, and yaw rates.
   */
  public AngularVelocity3d getGyroAngularVelocity()
  {
    return new AngularVelocity3d(gyroX.refresh().getValue(), gyroY.refresh().getValue(), gyroZ.refresh().getValue());
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
   * Reset the pose estimator (and, in simulation, the ground truth pose) to the given pose.
   *
   * @param pose Pose to reset to. Field relative, blue-origin.
   */
  public void resetOdometry(Pose2d pose)
  {
    drive.resetOdometry(pose);
  }

  /**
   * Get the {@link Field2d} used to display the robot's pose, so callers (e.g. vision subsystems) can publish
   * additional {@link edu.wpi.first.wpilibj.smartdashboard.FieldObject2d}s onto the same field widget instead of
   * creating their own.
   *
   * @return {@link Field2d} of the drive.
   */
  public Field2d getField2d()
  {
    return drive.getField2d();
  }

  /**
   * Fuse a vision-derived pose measurement, e.g. from {@link frc.robot.subsystems.vision.LimelightVisionSubsystem}, into the
   * drive's pose estimator.
   *
   * @param visionPose        Vision-measured {@link Pose2d}, field relative, blue-origin.
   * @param timestampSeconds  Timestamp the measurement was taken at, matching {@link edu.wpi.first.wpilibj.Timer#getFPGATimestamp()}.
   */
  public void addVisionMeasurement(Pose2d visionPose, double timestampSeconds)
  {
    drive.addVisionMeasurement(visionPose, timestampSeconds);
  }

  public Command drive(SwerveInputStream stream)
  {
    return drive.drive(()->ChassisSpeeds.fromFieldRelativeSpeeds(stream.get(), new Rotation2d(drive.getGyroAngle())));
  }

  /**
   * Create a {@link Command} that runs a full SysId characterization routine (quasistatic and dynamic, forward and
   * reverse) on a single swerve module's drive motor. The module's azimuth is held pointed straight ahead for the
   * duration of the test so only the drive motor is characterized.
   *
   * @param moduleName Name of the module to test, e.g. "frontleft", "frontright", "backleft", or "backright".
   * @return {@link Command} that runs the full SysId routine on the given module.
   */
  public Command sysIdModule(String moduleName)
  {

    SwerveModule         module       = drive.getModule(moduleName).orElseThrow();
    SmartMotorController driveMotor   = module.getDriveMotorController();
    SmartMotorController azimuthMotor = module.getAzimuthMotorController();

    SysIdRoutine routine = new SysIdRoutine(
        new SysIdRoutine.Config(Volts.of(1).per(Second), Volts.of(7), Seconds.of(10)),
        new SysIdRoutine.Mechanism(
            azimuthMotor::setVoltage,
            log -> log.motor(moduleName + "-azimuth")
                      .voltage(azimuthMotor.getVoltage())
                      .angularPosition(azimuthMotor.getMechanismPosition())
                      .angularVelocity(azimuthMotor.getMechanismVelocity()),
            this,
            moduleName + "-azimuth"
        )
    );

    return Commands.runOnce(() -> azimuthMotor.setPosition(Rotation2d.kZero.getMeasure()))
                   .andThen(routine.quasistatic(SysIdRoutine.Direction.kForward))
                   .andThen(Commands.waitSeconds(1))
                   .andThen(routine.quasistatic(SysIdRoutine.Direction.kReverse))
                   .andThen(Commands.waitSeconds(1))
                   .andThen(routine.dynamic(SysIdRoutine.Direction.kForward))
                   .andThen(Commands.waitSeconds(1))
                   .andThen(routine.dynamic(SysIdRoutine.Direction.kReverse))
                   .withName("SysId " + moduleName + " Azimuth");
  }

  public void periodic()
  {
    drive.updateTelemetry();
  }

  public void simulationPeriodic()
  {
    drive.simIterate();
    gyro.getSimState().setRawYaw(drive.getSimPose().getRotation().getDegrees());
  }

  public Pose2d getSimPose()
  {
    return drive.getSimPose();
  }

  public SwerveDrivePoseEstimator createPoseEstimator()
  {
    return new SwerveDrivePoseEstimator(drive.getKinematics(), gyro.getRotation2d(), drive.getModulePositions(), drive.getConfig().getInitialPose());
  }

  public void updatePoseEstimator(SwerveDrivePoseEstimator visionPoseEstimator)
  {
    visionPoseEstimator.update(gyro.getRotation2d(), drive.getModulePositions());
  }
}

