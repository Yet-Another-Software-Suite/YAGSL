---
description: Register a YAGSL-built SwerveDrive with PathPlanner's AutoBuilder.
---

# Set up PathPlanner

[PathPlanner](https://pathplanner.dev) is a separate tool from YAGSL. It plans and follows multi-point paths (with obstacle-aware routing, event markers, and an on-field GUI editor) rather than the single point-to-point moves in [driveToPose](drive-to-a-pose.md). This page wires a YAGSL-built `SwerveDrive` into it. It assumes you already have a driving robot from the [Tutorial](../get-started/tutorial.md).

{% hint style="info" %}
This page covers the YAGSL/YAMS side of the wiring. For building paths/autos themselves, installing the standalone PathPlanner GUI app, and general PathPlanner concepts, see [PathPlanner's own documentation](https://pathplanner.dev/home.html).
{% endhint %}

{% hint style="warning" %}
**Commands v2 only.** PathplannerLib builds Commands v2 commands, so it needs the `yagsl_commands2.json` vendordep. There is no PathPlanner support for Commands v3 yet; on a Commands v3 robot, build autonomous routines from [driveToPose](drive-to-a-pose.md) instead, as the YAGSL `commands3` example does.
{% endhint %}

## 1. Install PathplannerLib

For WPILib 2027 use PathPlanner's SystemCore alpha vendordep. In VS Code run **WPILib: Manage Vendor Libraries → Install new libraries (online)** (see [installing from a URL](../get-started/02-install-yagsl.md#alternative-install-from-a-url)) with:

```
https://3015rangerrobotics.github.io/pathplannerlib/PathplannerLibSystemCoreAlpha.json
```

{% hint style="danger" %}
**Temporary workaround for PathplannerLib `2027.0.0-alpha-4`.** That release was built against an older WPILib `Alert` constructor, so on WPILib `2027_alpha7` its `RobotConfig` class throws "Alert already allocated" as it loads, which breaks every use of `RobotConfig`. Until PathPlanner ships a fix, copy [`src/main/java/com/pathplanner/lib/config/RobotConfig.java`](https://github.com/Yet-Another-Software-Suite/YAGSL/blob/main/examples/commands2/src/main/java/com/pathplanner/lib/config/RobotConfig.java) from the YAGSL `commands2` example into your project at the same path. It shadows the vendordep's class with a corrected copy. Delete it once you update to a fixed PathplannerLib.
{% endhint %}

## 2. Export your robot config from the PathPlanner GUI

In the [PathPlanner GUI app](https://github.com/mjansen4857/pathplanner/releases), fill in your robot's mass, MOI, module offsets, wheel radius, and drive motor specs under its Robot Config tab, then export. This writes `deploy/pathplanner/settings.json`, which `RobotConfig.fromGUISettings()` reads at runtime, so you don't hand-write this file or duplicate its values in Java.

{% hint style="warning" %}
This is a **second, separate** description of your robot's physical properties from YAGSL's own `modules/physicalproperties.json`. Gear ratio and wheel diameter in particular exist in both places. If you change one (a wheel swap, a gearing change), update the other too, or PathPlanner's paths and YAGSL's own odometry will disagree about how far the robot actually travels. This is a common, JSON-invisible cause of drift, see the [PathPlanner section of Swerve Drive Drift](../fix-a-problem/swerve-drift-causes.md#pathplanner).
{% endhint %}

## 3. Register `AutoBuilder` once, right after building the drive

Do this once, in your swerve subsystem's constructor, right after `SwerveParser.createSwerveDrive(...)` returns. After this call, `PathPlannerAuto` commands and GUI-defined paths just work, with no extra wiring per auto.

```java
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import java.io.File;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchState;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.system.Filesystem;
import swervelib.commands2.SwerveParser;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;
import yams.core.telemetry.SwerveDriveTelemetryConfig;
import yams.core.telemetry.enums.TelemetryVerbosity;

public class SwerveDriveSubsystem extends SubsystemBase {
  private final SwerveDrive drive;

  public SwerveDriveSubsystem() {
    var cfg = new SwerveDriveConfig()
        .withStartingPose(new Pose2d(3, 3, Rotation2d.ZERO))
        .withSubsystem(this)
        .withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    drive = SwerveParser.createSwerveDrive(cfg);

    configurePathPlanner();
  }

  private void configurePathPlanner() {
    RobotConfig config;
    try {
      config = RobotConfig.fromGUISettings(); // reads deploy/pathplanner/settings.json
    } catch (IOException | ParseException e) {
      throw new RuntimeException("Failed to load PathPlanner GUI settings", e);
    }

    AutoBuilder.configure(
        drive::getPose,                // robot pose supplier
        drive::resetOdometry,          // called if an auto defines a starting pose
        drive::getRobotRelativeSpeed,  // ChassisVelocities supplier, MUST be robot-relative
        (speeds, feedforwards) ->
            drive.setRobotRelativeChassisSpeeds(speeds, feedforwards.linearForces()),
        new PPHolonomicDriveController(
            new PIDConstants(5.0, 0.0, 0.0),  // translation PID
            new PIDConstants(5.0, 0.0, 0.0)   // rotation PID
        ),
        config,
        // Field origin is always the blue alliance wall, so flip paths when on red.
        () -> MatchState.getAlliance().orElse(Alliance.BLUE) == Alliance.RED,
        this                           // subsystem requirement for the generated commands
    );
  }
}
```

WPILib 2027 renamed `ChassisSpeeds` to `ChassisVelocities`, and that is the type `getRobotRelativeSpeed()` returns and `setRobotRelativeChassisSpeeds(...)` takes. The YAMS method names themselves still say `Speed`/`ChassisSpeeds`. The second argument passes PathPlanner's per-module feedforward forces through to the drive motors; use the one-argument `drive.setRobotRelativeChassisSpeeds(speeds)` if you'd rather ignore them.

{% hint style="warning" %}
The `PPHolonomicDriveController` PID constants above are **independent** of `SwerveDriveConfig.withTranslationController()`/`withRotationController()` from [driveToPose](drive-to-a-pose.md). PathPlanner's `AutoBuilder` uses its own controller and never touches YAGSL's. Tune them separately; there's no reason they need matching gains.
{% endhint %}

## 4. Run an auto

Build `"New Auto"` in the PathPlanner GUI and deploy. `AutoBuilder` can build a chooser of every auto in `deploy/pathplanner/autos` for you; publish it and return the selection as your autonomous command:

```java
import com.pathplanner.lib.auto.AutoBuilder;
import org.wpilib.command2.Command;
import org.wpilib.tunable.Selectable;
import org.wpilib.tunable.Tunables;

// Construct the swerve subsystem first: it is what configures AutoBuilder.
private final SwerveDriveSubsystem swerve = new SwerveDriveSubsystem();
private final Selectable<Command> autoChooser;

public RobotContainer() {
  autoChooser = AutoBuilder.buildAutoChooser();
  Tunables.publish("Auto Chooser", autoChooser);
}

public Command getAutonomousCommand() {
  return autoChooser.getSelected();
}
```

To run one specific auto instead, return `new PathPlannerAuto("New Auto")` (`com.pathplanner.lib.commands.PathPlannerAuto`). Event markers placed in the GUI fire `EventTrigger`s in code:

```java
new EventTrigger("EventMarker").whileTrue(Commands.print("Something"));
```

## Optional: pathfind to a pose

Once `AutoBuilder` is configured you can also drive to an arbitrary pose with PathPlanner's on-the-fly pathfinding, which plans around the obstacles in `deploy/pathplanner/navgrid.json` and ends at rest on the pose. This is what the `commands2` example's `driveToPose` does:

```java
import static org.wpilib.units.Units.DegreesPerSecond;
import static org.wpilib.units.Units.DegreesPerSecondPerSecond;
import static org.wpilib.units.Units.MetersPerSecond;
import static org.wpilib.units.Units.MetersPerSecondPerSecond;

import com.pathplanner.lib.path.PathConstraints;

private static final PathConstraints PATHFINDING_CONSTRAINTS = new PathConstraints(
    MetersPerSecond.of(3.0), MetersPerSecondPerSecond.of(3.0),
    DegreesPerSecond.of(540), DegreesPerSecondPerSecond.of(720));

public Command driveToPose(Pose2d pose) {
  return AutoBuilder.pathfindToPose(pose, PATHFINDING_CONSTRAINTS, MetersPerSecond.of(0));
}
```

Compare with YAMS' own [driveToPose](drive-to-a-pose.md), which drives straight at the pose with no path planning and runs until canceled.

## Troubleshooting

If a PathPlanner auto drives the wrong distance, curves when it should be straight, or ends up in the wrong spot, see the [PathPlanner checklist in Swerve Drive Drift](../fix-a-problem/swerve-drift-causes.md#pathplanner) before re-tuning anything. Most of these trace back to the config mismatch in step 2, or PID gains that were never tuned past their defaults.
