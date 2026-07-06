package frc.robot.subsystems.swervedrive;


import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.io.File;
import java.util.function.DoubleSupplier;
import swervelib.parser.SwerveParser;
import yams.mechanisms.config.SwerveDriveConfig;
import yams.mechanisms.swerve.SwerveDrive;
import yams.mechanisms.swerve.utility.SwerveInputStream;
import yams.motorcontrollers.SmartMotorControllerConfig.TelemetryVerbosity;

public class SwerveDriveSubsystem extends SubsystemBase
{

  private SwerveDrive drive;

  public SwerveDriveSubsystem()
  {
    SmartDashboard.putData(this);
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, Rotation2d.kZero))
        .withSubsystem(this)
        .withTelemetry(TelemetryVerbosity.HIGH);
    try
    {
      drive = new SwerveParser(new File(Filesystem.getDeployDirectory(), "swerve/base"))
          .createSwerveDrive(cfg);
    } catch (Exception e)
    {
      System.out.println("Error creating swerve drive");
      System.out.println(e);
      throw new RuntimeException(e);
    }
  }

  public SwerveInputStream getAngularVelocityStream(DoubleSupplier x, DoubleSupplier y, DoubleSupplier rot)
  {
    return new SwerveInputStream(drive, x, y, rot);
  }

  public Command drive(SwerveInputStream stream)
  {
    return drive.drive(()->ChassisSpeeds.fromFieldRelativeSpeeds(stream.get(), new Rotation2d(drive.getGyroAngle())));
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

