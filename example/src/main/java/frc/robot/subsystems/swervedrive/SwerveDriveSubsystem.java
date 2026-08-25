package frc.robot.subsystems.swervedrive;


import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathConstraints;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import java.io.File;
import java.io.IOException;
import java.util.function.DoubleSupplier;
import org.json.simple.parser.ParseException;
import swervelib.parser.SwerveParser;
import yams.mechanisms.config.SwerveDriveConfig;
import yams.mechanisms.swerve.SwerveDrive;
import yams.mechanisms.swerve.SwerveModule;
import yams.mechanisms.swerve.utility.SwerveInputStream;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;

import static edu.wpi.first.units.Units.*;

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

  public SwerveDriveSubsystem()
  {
    SmartDashboard.putData(this);
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, Rotation2d.kZero))
        .withSubsystem(this)
        .withTelemetry(TelemetryVerbosity.HIGH);

    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    drive = SwerveParser
        .createSwerveDrive(cfg);

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
   * Get the gyro's orientation as a {@link Rotation3d}, with pitch and roll fixed at zero.
   * <p>
   * {@link SwerveDrive} only tracks a single yaw {@link edu.wpi.first.units.measure.Angle} (see
   * {@link SwerveDrive#getGyroAngle()}), so pitch and roll are always zero here. This is by design: yaw is the only
   * axis that MegaTag2 pose estimation actually requires, so any gyro that reports yaw -- NavX, Pigeon2, ADIS16470,
   * ADXRS450, or otherwise -- works fine for MegaTag2. Feeding real pitch/roll from a full IMU is optional, not a
   * prerequisite; depending on your camera mount and how noisy that extra data is, it could help or hurt the
   * resulting pose, so treat it as a tuning knob rather than a requirement.
   *
   * @return {@link Rotation3d} of the gyro.
   */
  public Rotation3d getGyroRotation3d()
  {
    return new Rotation3d(0, 0, drive.getGyroAngle().in(Radians));
  }

  /**
   * Get the robot's yaw angular velocity, derived from wheel odometry (kinematics of the current module states)
   * since {@link SwerveDrive} doesn't expose a raw gyro angular velocity reading directly.
   *
   * @return {@link AngularVelocity} of the robot's yaw rate.
   */
  public AngularVelocity getGyroAngularVelocity()
  {
    return RadiansPerSecond.of(drive.getRobotRelativeSpeed().omegaRadiansPerSecond);
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
  }
}

