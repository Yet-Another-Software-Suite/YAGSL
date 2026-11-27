# 4. Write the swerve code

Wire YAGSL into your robot code. `SwerveParser` reads your `swerve/base` config directory and builds a YAMS `SwerveDrive` for you, so you only write the subsystem (Commands v2) or mechanism (Commands v3) and the controller bindings around it.

## Create a swerve subsystem

{% tabs %}
{% tab title="Commands v2" %}
```java
package first.robot.subsystems.swervedrive;

import java.io.File;
import java.util.function.DoubleSupplier;
import org.wpilib.command2.Command;
import org.wpilib.command2.Commands;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.math.controller.PIDController;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.system.Filesystem;
import swervelib.commands2.SwerveParser;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;
import yams.commands2.swerve.SwerveInputStream;
import yams.core.telemetry.SwerveDriveTelemetryConfig;
import yams.core.telemetry.enums.TelemetryVerbosity;

public class SwerveDriveSubsystem extends SubsystemBase
{

  private final SwerveDrive drive;

  public SwerveDriveSubsystem()
  {
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, Rotation2d.ZERO))
        .withSubsystem(this)
        // Live tuning (steps 5 and 7) needs both drive-to-pose controllers.
        .withTranslationController(new PIDController(4, 0, 0))
        .withRotationController(new PIDController(3, 0, 0))
        .withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    drive = SwerveParser.createSwerveDrive(cfg);
  }

  public SwerveInputStream getAngularVelocityStream(DoubleSupplier x, DoubleSupplier y,
                                                    DoubleSupplier rot)
  {
    return SwerveInputStream.of(drive, x, y, rot);
  }

  /** Drive with a field relative SwerveInputStream. */
  public Command driveFieldRelative(SwerveInputStream stream)
  {
    return run(() -> drive.setFieldRelativeChassisSpeeds(stream.get())).withName("Drive Field Relative");
  }

  /** Zero the gyro heading. Bind this to a button combo for field recovery. */
  public Command zeroGyro()
  {
    return Commands.runOnce(drive::zeroGyro, this).withName("Zero Gyro");
  }

  @Override
  public void periodic()
  {
    drive.updateTelemetry();
  }

  @Override
  public void simulationPeriodic()
  {
    drive.simIterate();
  }
}
```
{% endtab %}

{% tab title="Commands v3" %}
```java
package first.robot.mechanisms;

import java.io.File;
import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.math.controller.PIDController;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.system.Filesystem;
import swervelib.commands3.SwerveParser;
import yams.commands3.config.SwerveDriveConfig;
import yams.commands3.swerve.SwerveDrive;
import yams.core.telemetry.SwerveDriveTelemetryConfig;
import yams.core.telemetry.enums.TelemetryVerbosity;

public class SwerveDriveMechanism implements Mechanism
{

  private final SwerveDrive drive;

  public SwerveDriveMechanism()
  {
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, Rotation2d.ZERO))
        .withMechanism(this)
        // Live tuning (steps 5 and 7) needs both drive-to-pose controllers.
        .withTranslationController(new PIDController(4, 0, 0))
        .withRotationController(new PIDController(3, 0, 0))
        .withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    drive = SwerveParser.createSwerveDrive(cfg);
  }

  /** The underlying YAMS SwerveDrive, used to build a SwerveInputStream. */
  public SwerveDrive getDrive()
  {
    return drive;
  }

  /** Drive with field relative ChassisVelocities. */
  public void drive(ChassisVelocities velocity)
  {
    drive.setFieldRelativeChassisSpeeds(velocity);
  }

  /** Zero the gyro heading. Bind this to a button combo for field recovery. */
  public Command zeroGyro()
  {
    return run(coroutine -> drive.zeroGyro()).named("Zero Gyro");
  }

  /** Call every loop. Commands v3 mechanisms have no periodic() hook of their own. */
  public void periodic()
  {
    drive.updateTelemetry();
  }

  /** Call every loop in simulation. */
  public void simulationPeriodic()
  {
    drive.simIterate();
  }
}
```

Call `periodic()` and `simulationPeriodic()` from your robot class, before the scheduler runs:

```java
package first.robot;

import first.robot.mechanisms.SwerveDriveMechanism;
import org.wpilib.command3.Scheduler;
import org.wpilib.framework.OpModeRobot;

public class Robot extends OpModeRobot
{

  public final SwerveDriveMechanism swerve = new SwerveDriveMechanism();

  private final Scheduler scheduler = Scheduler.getDefault();

  @Override
  public void robotPeriodic()
  {
    swerve.periodic();
    scheduler.run();
  }

  @Override
  public void simulationPeriodic()
  {
    swerve.simulationPeriodic();
  }
}
```
{% endtab %}
{% endtabs %}

The drive publishes its telemetry under `Mechanisms/swerve`, which the next steps read. Turn `TelemetryVerbosity.HIGH` down once you've finished tuning.

## Bind your controller

{% tabs %}
{% tab title="Commands v2" %}
```java
package first.robot;

import first.robot.subsystems.swervedrive.SwerveDriveSubsystem;
import org.wpilib.command2.Commands;
import org.wpilib.command2.button.CommandXboxController;
import org.wpilib.command2.button.RobotModeTriggers;
import yams.commands2.swerve.SwerveInputStream;

public class RobotContainer
{

  final CommandXboxController driverXbox = new CommandXboxController(0);

  private final SwerveDriveSubsystem swerve = new SwerveDriveSubsystem();

  // Xbox sticks read negative when pushed forward/left, so every axis is negated:
  // left stick forward drives away from the driver station, right stick right spins clockwise.
  private final SwerveInputStream driveAngularVelocity =
      swerve.getAngularVelocityStream(
                () -> -driverXbox.getLeftY(),
                () -> -driverXbox.getLeftX(),
                () -> -driverXbox.getRightX())
            .withDeadband(0.05)
            .withAllianceRelativeControl();

  public RobotContainer()
  {
    configureBindings();
  }

  private void configureBindings()
  {
    // Default drive command
    swerve.setDefaultCommand(swerve.driveFieldRelative(driveAngularVelocity));

    // Utility mode is for live tuning: hold the drive idle so the default command doesn't fight it.
    RobotModeTriggers.utility().whileTrue(Commands.idle(swerve).withName("Live Tuning"));

    // Zero the gyro with Menu + View. Use this if the field-relative heading drifts.
    driverXbox.menu().and(driverXbox.view()).onTrue(swerve.zeroGyro());
  }
}
```
{% endtab %}

{% tab title="Commands v3" %}
In Commands v3 the bindings live in a teleop opmode:

```java
package first.robot.opmodes.teleop;

import first.robot.Robot;
import org.wpilib.command3.Command;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.command3.button.RobotModeTriggers;
import org.wpilib.driverstation.XboxController;
import org.wpilib.opmode.OpMode;
import org.wpilib.opmode.Teleop;
import yams.commands3.swerve.SwerveInputStream;

@Teleop(name = "Angular Velocity Control")
public class AngularVelocityTeleop implements OpMode
{

  private final CommandXboxController driverXbox = new CommandXboxController(0);
  private final SwerveInputStream     driveStream;

  public AngularVelocityTeleop(Robot robot)
  {
    // Xbox sticks read negative when pushed forward/left, so every axis is negated.
    XboxController hid = driverXbox.getController();
    driveStream = SwerveInputStream.of(robot.swerve.getDrive(), () -> -hid.getLeftY(), () -> -hid.getLeftX())
        .withControllerRotationAxis(() -> -hid.getRightX())
        .withDeadband(0.05)
        .withAllianceRelativeControl();

    Command driveCommand = robot.swerve.run(coroutine -> {
      while (true)
      {
        robot.swerve.drive(driveStream.get());
        coroutine.yield();
      }
    }).named("Drive Angular Velocity");

    RobotModeTriggers.teleop().whileTrue(driveCommand);

    // Zero the gyro with Menu + View. Use this if the field-relative heading drifts.
    driverXbox.menu().and(driverXbox.view()).onTrue(robot.swerve.zeroGyro());
  }
}
```
{% endtab %}
{% endtabs %}

{% hint style="info" %}
**Why bind `zeroGyro()` to a button combo?** Gyros can drift, or power on facing the wrong direction. A button combo (Menu + View, or both bumpers) lets the driver instantly re-align field-relative control without touching the Driver Station. Always bind this. It has saved matches. `zeroGyro()` is the same as YAMS's `zeroOdometryHeading()`: it turns the odometry's heading, which field-relative driving uses, to 0. The gyro itself keeps its reading.
{% endhint %}

Complete projects for both are in the [YAGSL examples](../../../examples).

Build the project, and make sure it compiles.
