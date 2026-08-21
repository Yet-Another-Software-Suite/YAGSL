# Yet Another Generic Swerve Library (YAGSL)

YAGSL is a plug-and-play swerve drive library for FRC robots. It handles all of the kinematics,
odometry, and motor configuration so your team can focus on the rest of the robot. Full
documentation lives at [docs.yagsl.com](https://docs.yagsl.com).

---

## Quick Links

| Resource                | Link                                                                                                                |
|-------------------------|---------------------------------------------------------------------------------------------------------------------|
| Configuration Generator | **[config.yagsl.com](https://config.yagsl.com)**                                                                    |
| Javadocs                | [yet-another-software-suite.github.io/YAGSL/javadocs](https://yet-another-software-suite.github.io/YAGSL/javadocs/) |
| Library Source          | [github.com/Yet-Another-Software-Suite/YAGSL](https://github.com/Yet-Another-Software-Suite/YAGSL/)                 |
| Wiki / Docs             | [docs.yagsl.com](https://docs.yagsl.com)                                                                            |

---

## Project Setup

### 1. Create a new WPILib project

Open the **WPILib VS Code** extension and create a new **Command-Based Robot (Java)** project using
the template wizard. Teams may also use an existing project.

### 2. Add the vendor dependency

YAGSL is listed in the **WPILib Vendordep Tab** — the easiest way to install it is to search for
**YAGSL** there and click install.

Alternatively, open the **Manage Vendor Libraries** menu, select **Install new library (online)**,
and paste the URL manually:

```
https://yet-another-software-suite.github.io/YAGSL/yagsl/yagsl.json
```

Run a Gradle build to download the library before continuing.

### 3. Generate your configuration

Visit **[config.yagsl.com](https://config.yagsl.com)** and fill in your robot's hardware details:

- Gyro type and CAN ID
- Drive and angle motor types and CAN IDs for each module
- Absolute encoder types and CAN IDs
- Wheel diameter, gear ratios, and module locations

Download the generated ZIP and unzip it into your project's `src/main/deploy` directory so the
layout looks like this:

```text
src/main/deploy
└── swerve
    └── base
        ├── swervedrive.json
        └── modules
            ├── frontleft.json
            ├── frontright.json
            ├── backleft.json
            ├── backright.json
            ├── physicalproperties.json
            └── pidfproperties.json
```

---

## Using YAGSL in Your Robot

### SwerveDriveSubsystem

Create a subsystem that wraps the `SwerveDrive` object built by `SwerveParser`:

```java
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Filesystem;
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
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, Rotation2d.kZero))
        .withSubsystem(this)
        .withTelemetry(TelemetryVerbosity.HIGH);
    try
    {
      drive = SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"))
          .createSwerveDrive(cfg);
    } catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public SwerveInputStream getAngularVelocityStream(DoubleSupplier x, DoubleSupplier y,
                                                    DoubleSupplier rot)
  {
    return new SwerveInputStream(drive, x, y, rot);
  }

  public Command drive(SwerveInputStream stream)
  {
    return drive.drive(() -> ChassisSpeeds.fromFieldRelativeSpeeds(stream.get(),
                                                                   new Rotation2d(drive.getGyroAngle())));
  }

  /** Zero the gyro heading. Bind this to a button combo for field recovery. */
  public Command zeroGyro()
  {
    return runOnce(() -> drive.zeroGyro());
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

### RobotContainer — Binding Controls

Wire up your driver controller in `RobotContainer`. The example below uses an Xbox controller with *
*Start + Back** as the gyro-reset combo so drivers can recover field orientation if the gyro drifts:

```java
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.swervedrive.SwerveDriveSubsystem;
import yams.mechanisms.swerve.utility.SwerveInputStream;

public class RobotContainer
{

  final CommandXboxController driverXbox = new CommandXboxController(0);

  private final SwerveDriveSubsystem swerve = new SwerveDriveSubsystem();

  private final SwerveInputStream driveAngularVelocity =
      swerve.getAngularVelocityStream(
                driverXbox::getLeftY,
                driverXbox::getLeftX,
                () -> driverXbox.getRawAxis(2))
            .withAllianceRelativeControl();

  public RobotContainer()
  {
    configureBindings();
  }

  private void configureBindings()
  {
    // Default drive command
    swerve.setDefaultCommand(swerve.drive(driveAngularVelocity));

    // Zero the gyro with Start + Back — use this if the field-relative heading drifts
    driverXbox.start().and(driverXbox.back()).onTrue(swerve.zeroGyro());
  }

  public Command getAutonomousCommand()
  {
    return autoChooser.getSelected();
  }
}
```

> **Why bind `zeroGyro()` to a button combo?**  
> Gyros can drift or power on facing the wrong direction. A button combo (e.g., Start + Back, or
> both bumpers) lets the driver instantly re-align field-relative control without touching the
> Driver
> Station. Always bind this — it has saved matches.

---

## Configuration Tips

- Use **[config.yagsl.com](https://config.yagsl.com)** to walk through every field interactively —
  it validates your inputs and generates correct JSON.
- Set `absoluteEncoderOffset` for each module by rotating every wheel to face forward, reading the
  raw encoder value, and entering it as the offset.
- Start with conservative PIDF values from the generator and tune drive `kP` first, then angle `kP`.
- If modules spin out of control, invert the angle motor or encoder for that module.
- The `pidfproperties_sim.json` file lets you use different gains in simulation without touching
  your real robot config.

---

## Maintainers

- [@thenetworkgrinch](https://github.com/thenetworkgrinch)

## Special Thanks

- **Team 7900 Trial N' Terror** — essential debugging and stability work
- **Team 1466 Webb Robotics** — Falcon / TalonFX support

---

> Found a bug? [Open an issue](https://github.com/Yet-Another-Software-Suite/YAGSL/issues) — we
> actively monitor and fix them.
