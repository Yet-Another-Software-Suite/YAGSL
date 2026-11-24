# Yet Another Generic Swerve Library (YAGSL)

YAGSL is a plug-and-play swerve drive library for FRC robots. It reads a JSON description of your
swerve drive and builds a [YAMS](https://github.com/Yet-Another-Software-Suite/YAMS) `SwerveDrive`
from it, which handles the kinematics, odometry and motor configuration so your team can focus on
the rest of the robot. YAGSL targets WPILib `2027_alpha7` and the SystemCore. Full documentation
lives at [docs.yagsl.com](https://docs.yagsl.com).

---

## Quick Links

| Resource                | Link                                                                                                                |
|-------------------------|---------------------------------------------------------------------------------------------------------------------|
| Configuration Generator | **[config.yagsl.com](https://config.yagsl.com)**                                                                    |
| Javadocs                | [yet-another-software-suite.github.io/YAGSL/javadocs](https://yet-another-software-suite.github.io/YAGSL/javadocs/) |
| Library Source          | [github.com/Yet-Another-Software-Suite/YAGSL](https://github.com/Yet-Another-Software-Suite/YAGSL/)                 |
| Wiki / Docs             | [docs.yagsl.com](https://docs.yagsl.com)                                                                            |
| Example Projects        | [examples/](examples/)                                                                                              |
| Migrating from 2026.4.1 | [MIGRATION.md](MIGRATION.md)                                                                                        |

---

## Project Setup

### 1. Create a new WPILib project

Open **WPILib VS Code** `2027_alpha7` and create a new **Command-Based Robot (Java)** project using
the template wizard. Teams may also use an existing 2027 project. Upgrading a 2026 project? Follow
[MIGRATION.md](MIGRATION.md).

### 2. Add the vendor dependency

YAGSL has one vendordep for each WPILib command framework. Install the one your robot uses, and
only one: they conflict with each other. Open **WPILib: Manage Vendor Libraries**, select
**Install new libraries (online)**, and paste its URL:

| Command framework | Vendordep URL                                       |
|-------------------|-----------------------------------------------------|
| Commands v2       | `https://cdn.yassrobotics.com/yagsl_commands2.json` |
| Commands v3       | `https://cdn.yassrobotics.com/yagsl_commands3.json` |

VS Code offers to install the matching YAMS vendordep, the only library YAGSL requires. Then
install the 2027 vendordep for each hardware vendor in your configuration, such as Phoenix 6 for a
TalonFX or REVLib for a SPARK MAX.

Run a Gradle build to download the libraries before continuing.

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

Create a subsystem that wraps the `SwerveDrive` built by `SwerveParser`. This uses Commands v2; the
[examples](examples/) show both command frameworks.

```java
import java.io.File;
import java.util.function.DoubleSupplier;
import org.wpilib.command2.Command;
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
        .withTranslationController(new PIDController(4, 0, 0))
        .withRotationController(new PIDController(3, 0, 0))
        .withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

    // parse(...) and createSwerveDrive(...) are static, and are called one after the other.
    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    drive = SwerveParser.createSwerveDrive(cfg);
  }

  public SwerveInputStream getAngularVelocityStream(DoubleSupplier x, DoubleSupplier y,
                                                    DoubleSupplier rot)
  {
    return SwerveInputStream.of(drive, x, y, rot);
  }

  /** Drive with a field relative {@link SwerveInputStream}. */
  public Command driveFieldRelative(SwerveInputStream stream)
  {
    return run(() -> drive.setFieldRelativeChassisSpeeds(stream.get()));
  }

  /** Zero the gyro heading. Bind this to a button combo for field recovery. */
  public Command zeroGyro()
  {
    return runOnce(drive::zeroGyro);
  }

  @Override
  public void periodic()
  {
    // The pose only updates here, so this has to run every loop.
    drive.updateTelemetry();
  }

  @Override
  public void simulationPeriodic()
  {
    drive.simIterate();
  }
}
```

Without `withMaximumChassisSpeed(...)` on the `SwerveDriveConfig`, `SwerveInputStream` scales the
joysticks to 4 m/s and 1 rotation per second. Set it to your robot's speed.

### RobotContainer — Binding Controls

Wire up your driver controller in `RobotContainer`. The example below uses an Xbox controller with
**Menu + View** (Start + Back) as the gyro-reset combo so drivers can recover field orientation if
the gyro drifts:

```java
import frc.robot.subsystems.swervedrive.SwerveDriveSubsystem;
import org.wpilib.command2.button.CommandXboxController;
import yams.commands2.swerve.SwerveInputStream;

public class RobotContainer
{

  final CommandXboxController driverXbox = new CommandXboxController(0);

  private final SwerveDriveSubsystem swerve = new SwerveDriveSubsystem();

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

    // Zero the gyro with Menu + View — use this if the field-relative heading drifts
    driverXbox.menu().and(driverXbox.view()).onTrue(swerve.zeroGyro());
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
