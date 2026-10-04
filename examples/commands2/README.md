# YAGSL Example: Commands v2

This is the reference robot project for [YAGSL](https://docs.yagsl.com) on WPILib's **Commands v2**
framework: a working, buildable Command-based robot that turns YAGSL's swerve JSON configuration into a
[YAMS](https://github.com/Yet-Another-Software-Suite/YAMS) `SwerveDrive`, with PathPlanner autonomous and
on-the-fly pathfinding. Using **Commands v3** instead? See [`../commands3`](../commands3).

> **Start at [config.yagsl.com](https://config.yagsl.com).** Fill in your robot's hardware there, download
> your configuration, and follow the **Quick Start / Tuning Guide** it walks you through. That guide uses this
> exact project: you copy it out as your own robot project, drop your generated `swerve` folder into
> `src/main/deploy`, and tune from there. This README covers everything else in the project.
>
> **Tell us how it went.** Something confusing, broken, or missing in the generator, the guide, or this
> example? [Open an issue](https://github.com/Yet-Another-Software-Suite/YAGSL/issues) or tell us on
> [Discord](https://discord.gg/yass). Feedback from teams setting up their first swerve drive is the most useful
> kind we get. (Found an easter egg along the way? [Rivet](#meet-rivet) wants to hear about that too.)

---

## Quick setup

1. Copy this `examples/commands2` folder out of the YAGSL repository into your own robot project directory.
2. Open `build.gradle` and delete the block between `// YAGSL_TESTING` and `// END_YAGSL_TESTING`. It compiles
   YAGSL from this repository's source so the example can be tested here; your project uses the vendordep instead.
3. Install YAGSL for Commands v2: **WPILib: Manage Vendor Libraries → Install new libraries (online)** with
   `https://yet-another-software-suite.github.io/YAGSL/yagsl_commands2.json`. VS Code offers to install what it
   requires (YAMS, REVLib, Phoenix6, ReduxLib).
4. Set your team number (**WPILib: Set Team Number**, or edit `.wpilib/wpilib_preferences.json`).
5. Replace `src/main/deploy/swerve` with the configuration you generated at
   [config.yagsl.com](https://config.yagsl.com).
6. Go through [things to customize](#things-to-customize-before-you-drive), then follow the rest of the
   **Tuning Guide**, in simulation first and then on the real robot.

---

## Driver controls

Defined in `RobotContainer.configureBindings()` (Xbox controller). All driving is **field relative** and
alliance relative: pushing the left stick away from you drives away from your driver station on either alliance.

| Input                         | Action                                                                                          |
|-------------------------------|-------------------------------------------------------------------------------------------------|
| Left stick                    | Translate (field relative)                                                                      |
| Right stick X                 | Spin the robot (angular velocity control, the default): right spins clockwise                   |
| Right stick (heading control) | Point the way the robot should face: up faces away from you. Centered: the robot stops turning |
| **A**                         | Toggle between angular velocity and heading control                                             |
| **X** (hold)                  | `driveToPose`: drive to a demo point with PathPlanner's on-the-fly pathfinding                  |
| **Y** (hold)                  | `driveToPoseYAMS`: drive straight to the same point with YAMS' `SwerveDrive.driveToPose`         |
| **B** (hold)                  | Run the front-left module's SysId characterization routine                                      |
| **Menu + View**               | Zero the gyro (the robot is now facing away from you)                                           |

`X` and `Y` drive to the same placeholder pose (3m, 3m, 180°). Swap in a real target for your field.

### `driveToPose`: PathPlanner vs YAMS

`SwerveDriveSubsystem` has two ways to drive to a pose:

- **`driveToPose(Pose2d)`** uses PathPlanner's on-the-fly pathfinding. It plans a path around the obstacles in
  `deploy/pathplanner/navgrid.json`, follows it with the `PPHolonomicDriveController` configured in
  `configurePathPlanner()`, and ends at rest on the pose.
- **`driveToPoseYAMS(Pose2d)`** uses YAMS' `SwerveDrive.driveToPose`: the translation and rotation PID
  controllers from the `SwerveDriveConfig` drive straight at the pose (nothing steers around obstacles) until the
  command is canceled. Commands v3 has no PathPlanner support, so this is the only option there.

---

## Things to customize before you drive

None of these are tuned for your robot. They're placeholders so the project builds and runs out of the box:

- **PathPlanner gains.** `configurePathPlanner()` uses `PIDConstants(5, 0, 0)` for translation and rotation.
- **`src/main/deploy/pathplanner/settings.json`.** Feeds `RobotConfig.fromGUISettings()`. It matches the example's
  swerve config (Kraken X60 drive motors, 5.36:1, 4in wheels, 40A, modules 10.875in from center). Set your robot's
  real mass, MOI, wheel COF, drive motor, gearing, and module locations, ideally from the PathPlanner GUI.
- **Drive-to-pose controllers.** The `SwerveDriveConfig` translation (`kP = 4`) and rotation (`kP = 3`)
  controllers are used by heading control and `driveToPoseYAMS`. YAMS stops any module asked for less than
  0.1 m/s (its module velocity deadband), so a rotation gain that's too low leaves the robot a few degrees short of
  the heading you asked for. Tune these with [live tuning](#live-tuning).
- **The gyro cast in `SwerveDriveSubsystem`.** It casts `devices.gyro()` to a Redux `Canandgyro`, the gyro in the
  example config, to read its full roll/pitch/yaw. If your `swervedrive.json` gyro is different, change the cast to
  match (a `Pigeon2` for `pigeon2_can`), or use `SwerveParser.createSwerveDrive(cfg)` and skip the raw device. See
  [How to access raw hardware devices](https://docs.yagsl.com/how-to/access-raw-hardware-devices).

> **Simulating on Windows or macOS?** ReduxLib 2027 only reaches its devices through Linux socketcan, so the
> example's Canandgyro can't be created in desktop simulation there, and the `RobotContainerTest` smoke test skips
> itself. Switch `swervedrive.json` (and the cast above) to `pigeon2_can` to simulate, or use the
> [Commands v3 example](../commands3), which uses a Pigeon2.

---

## Live tuning

YAGSL builds the drive on YAMS, which publishes the drive's gains and test setpoints to NetworkTables so you can
tune them from a dashboard (Elastic, Shuffleboard, AdvantageScope) while the robot runs, no redeploying. The
config generator's **Tuning Guide** walks through the full process; these are the keys it uses.

Tuning is available because `SwerveDriveSubsystem` names the drive `"swerve"` and configures
`.withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH))` with both drive-to-pose
controllers set. With a different name, replace `swerve` in the keys below.

1. **Start in simulation**, or with the robot on blocks. Tuning moves the robot.
2. **Enable Utility mode** (formerly Test). `RobotContainer` holds the drive with an idle command in Utility
   mode, so the driver controls can't fight the tuning command.
3. **Start the tuning command** at `/Mechanisms/swerve/tuning/driveToPose` (set its `running` entry to `true`, or
   click it in your dashboard). Values under `/Tuning/swerve/` only take effect while it runs.
4. **Turn on one tuning mode** below and adjust its values. Only one mode drives at a time; if you enable more than
   one, auto-align wins over drive tuning, and drive tuning wins over azimuth tuning (the losers switch themselves
   back off).

| Key (under `/Tuning/swerve/`)                                         | What it does                                                                                      |
|------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| `modules/drive/enabled`                                                | Drive tuning: drive every module at `modules/drive/velocity`                                       |
| `modules/drive/velocity`                                               | Drive velocity setpoint, m/s                                                                       |
| `modules/drive/inplace`                                                | Point the modules tangent to the robot so a positive velocity spins it in place (counter-clockwise) instead of driving straight |
| `modules/drive/feedback/p`, `/i`, `/d`                                  | Drive motor PID                                                                                    |
| `modules/drive/feedforward/s`, `/v`, `/a`                               | Drive motor feedforward                                                                            |
| `modules/azimuth/enabled`                                              | Azimuth tuning: point every module at `modules/azimuth/angle`                                      |
| `modules/azimuth/angle`                                                | Azimuth angle setpoint, degrees                                                                    |
| `modules/azimuth/feedback/p`, `/i`, `/d`                                | Azimuth motor PID                                                                                  |
| `modules/azimuth/feedforward/s`, `/v`, `/a`                             | Azimuth motor feedforward                                                                          |
| `autoalign/enabled`                                                    | Auto-align tuning: drive to `autoalign/setpoint/*` with the drive-to-pose controllers               |
| `autoalign/setpoint/x`, `/y`, `/rot`                                    | Auto-align target pose: meters, meters, degrees (field relative, blue origin)                       |
| `autoalign/translation/p`, `/i`, `/d`                                   | Translation controller (the `SwerveDriveConfig` translation PID)                                   |
| `autoalign/rotation/p`, `/i`, `/d`                                      | Rotation controller (the `SwerveDriveConfig` rotation PID)                                         |

> **Known issue: tune auto-align facing 0°.** In YAMS 2026.10.03, dashboard auto-align converts an
> already-robot-relative setpoint as if it were field relative, so it only drives correctly while the robot faces
> 0° (away from the blue driver station). At any other heading the robot drives off in the wrong direction. Zero
> the gyro facing 0° before enabling it. The `driveToPose` commands themselves are not affected.

**Tuned values are not saved.** When you're happy, copy them into your configuration: the module drive/azimuth
gains into `src/main/deploy/swerve/base/modules/pidfproperties.json` (`drive` and `angle`), and the translation and
rotation gains into the `PIDController`s in `SwerveDriveSubsystem`.

---

## Vision (on hold for 2027)

The previous example also fused Limelight (YALL) and PhotonVision poses into the drivetrain. Neither library has a
2027_alpha7 release yet, so those subsystems now live in [`disabled-vision/`](disabled-vision), outside the build.
Once they do, move them back under `src/main/java`, restore the vendordeps (`yall.json`, `photonlib.json`), and
uncomment the fields in `RobotContainer`.

---

## Project layout

```text
build.gradle                                     # YAGSL_TESTING block compiles YAGSL from ../../yagsl/java
vendordeps/                                      # CommandsV2, YAMS, Phoenix6, REVLib, ReduxLib, PathPlanner
src/main/java/frc/robot/
├── Robot.java
├── RobotContainer.java                          # driver controls, autonomous chooser, battery sim
└── subsystems/swervedrive/
    └── SwerveDriveSubsystem.java                 # YAGSL config → YAMS SwerveDrive, PathPlanner, driveToPose
src/main/java/com/pathplanner/lib/config/
└── RobotConfig.java                              # temporary PathPlanner fix for WPILib 2027 Alerts (see file)
src/main/deploy/
├── swerve/base/                                  # YAGSL swerve config: replace with your own
└── pathplanner/                                  # paths, autos, navgrid, and PathPlanner settings
src/test/java/frc/robot/RobotContainerTest.java   # simulation smoke test: builds and runs the robot
disabled-vision/                                  # Limelight/PhotonVision subsystems, waiting on 2027 releases
```

---

## Meet Rivet

Rivet is the frog mascot of the Yet Another Software Suite. At events you'll usually find Rivet working as a
Control System Advisor, helping teams get their robots talking, and every so often borrowing a clipboard to
cosplay as the Lead Robot Inspector (Rivet's bumper-height checks are *extremely* thorough). Rivet loves teaching,
and loves a harmless prank almost as much, so a few easter eggs have a way of turning up in the YASS tools. If you
find one, Rivet would love to know: tell us on [Discord](https://discord.gg/yass).

---

## Links

| Resource                                | Link                                                                                                   |
|-----------------------------------------|--------------------------------------------------------------------------------------------------------|
| Swerve config generator + Tuning Guide  | [config.yagsl.com](https://config.yagsl.com)                                                           |
| YAGSL docs                              | [docs.yagsl.com](https://docs.yagsl.com)                                                               |
| YAGSL javadocs                          | [yet-another-software-suite.github.io/YAGSL/javadocs](https://yet-another-software-suite.github.io/YAGSL/javadocs/) |
| YAMS (mechanisms)                       | [github.com/Yet-Another-Software-Suite/YAMS](https://github.com/Yet-Another-Software-Suite/YAMS)       |
| PathPlanner                             | [pathplanner.dev](https://pathplanner.dev)                                                             |
| Discord                                 | [discord.gg/yass](https://discord.gg/yass)                                                             |

Found a bug? [Open an issue](https://github.com/Yet-Another-Software-Suite/YAGSL/issues).
