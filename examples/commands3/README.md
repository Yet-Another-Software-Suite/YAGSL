# YAGSL Example: Commands v3

This is the reference robot project for [YAGSL](https://docs.yagsl.com) on WPILib's **Commands v3** framework:
an `OpModeRobot` whose swerve drive is a YAMS `SwerveDrive` [Mechanism](https://github.com/Yet-Another-Software-Suite/YAMS)
built from YAGSL's JSON configuration, with three ways to drive in teleop and three autonomous routines. Using
**Commands v2** instead? See [`../commands2`](../commands2).

> **Start at [config.yagsl.com](https://config.yagsl.com).** Fill in your robot's hardware there, download
> your configuration, and follow the **Quick Start / Tuning Guide** it walks you through. Copy this project out as
> your own robot project, drop your generated `swerve` folder into `src/main/deploy`, and tune from there. This
> README covers everything else in the project.
>
> **Tell us how it went.** Something confusing, broken, or missing in the generator, the guide, or this
> example? [Open an issue](https://github.com/Yet-Another-Software-Suite/YAGSL/issues) or tell us on
> [Discord](https://discord.gg/yass). Commands v3 is new for everyone this season, so we especially want to hear
> what tripped you up. (Found an easter egg along the way? [Rivet](#meet-rivet) wants to hear about that too.)

---

## Quick setup

1. Copy this `examples/commands3` folder out of the YAGSL repository into your own robot project directory.
2. Open `build.gradle` and delete the block between `// YAGSL_TESTING` and `// END_YAGSL_TESTING`. It compiles
   YAGSL from this repository's source so the example can be tested here; your project uses the vendordep instead.
3. Install YAGSL for Commands v3: **WPILib: Manage Vendor Libraries → Install new libraries (online)** with
   `https://yet-another-software-suite.github.io/YAGSL/yagsl_commands3.json`. VS Code offers to install what it
   requires (YAMS, REVLib, Phoenix6, ReduxLib).
4. Set your team number (**WPILib: Set Team Number**, or edit `.wpilib/wpilib_preferences.json`).
5. Replace `src/main/deploy/swerve` with the configuration you generated at
   [config.yagsl.com](https://config.yagsl.com). The example config uses a CTRE Pigeon2 gyro.
6. Follow the rest of the **Tuning Guide**, in simulation first and then on the real robot.

---

## Opmodes

Commands v3 robots pick what to run with **opmodes**, chosen on the Driver Station like an autonomous routine.
`Robot` extends `OpModeRobot`, which finds every `@Teleop` and `@Autonomous` class under
`frc.robot.opmodes` and constructs the selected one; its bindings only exist while it's selected.

### Teleop

All three use the left stick to translate. Pick the one that matches how your driver likes to steer:

| Opmode                       | Translation (left stick)                                                        | Rotation (right stick)                                                                                     |
|------------------------------|----------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| **Heading Control**          | Field relative: away from you is away from your driver station, on either alliance | **Points the way the robot should face.** Up faces away from you, left faces left. Let go and the robot holds its heading. |
| **Angular Velocity Control** | Field relative, same as above                                                    | **X spins the robot.** Right spins clockwise, faster the further you push.                                  |
| **Demo Mode**                | **Robot relative**: forward drives the way the robot is facing                   | X spins the robot, like Angular Velocity Control                                                            |

**Demo Mode** caps every speed at 30%, and its controls follow the robot rather than the field. That makes it the
one to hand to a visitor at an outreach event, or to use when driving around people: no field orientation to
explain, and nothing happens fast.

Every teleop opmode also has these buttons (`opmodes/teleop/DriverButtons.java`):

| Input           | Action                                                                                   |
|-----------------|------------------------------------------------------------------------------------------|
| **Y** (hold)    | Drive to a demo point (3m, 3m, 180°) with YAMS' `driveToPose`                             |
| **Menu + View** | Zero the gyro (the robot is now facing away from you)                                     |

### Autonomous

Commands v3 has no PathPlanner support, so the autonomous routines are built from YAMS' `driveToPose`: the
`SwerveDriveConfig` translation and rotation PID controllers drive straight at each target pose.
`SwerveDriveMechanism.driveRelativeWaypoints(...)` drives to each waypoint in turn (each step ends within 2in and
5° of its target) and holds the last one until autonomous ends. The waypoints are relative to wherever the robot
is when autonomous starts (x forward, y left), so every routine works from any starting position and on either
alliance.

| Opmode                   | What it does                                                                          |
|--------------------------|---------------------------------------------------------------------------------------|
| **Leave Starting Area**  | Drives 2m straight forward                                                             |
| **Out And Back**         | Drives 3m forward, turns around, and drives back to where it started                    |
| **Drive To Pose Square** | Drives a 1.5m square, turning a quarter turn at each corner, and ends where it started |

To write your own, add a class to `opmodes/auto` with a list of `Transform2d` waypoints, following the existing
ones. For targets at fixed field positions (a scoring location, an AprilTag), call
`SwerveDriveMechanism.driveToPose(Pose2d)` with field relative poses instead, flipping them for the red alliance.

---

## Things to customize before you drive

- **Drive-to-pose controllers.** The `SwerveDriveConfig` translation (`kP = 4`) and rotation (`kP = 3`)
  controllers drive Heading Control, the Y button, and every autonomous routine. YAMS stops any module asked for
  less than 0.1 m/s (its module velocity deadband), so a rotation gain that's too low leaves the robot a few
  degrees short of its heading, and an autonomous step can wait forever on a heading it can't reach. At `kP = 3`
  the robot gets within 4.5°, inside the 5° tolerance. Tune these with [live tuning](#live-tuning).
- **Autonomous waypoints and tolerances.** The routines drive in the open; make sure there's room around the robot
  (3m ahead for Out And Back). The 2in / 5° step tolerances are in `SwerveDriveMechanism`.

---

## Live tuning

Live tuning is built directly into **YAMS**. YAMS publishes the drive's gains, setpoints, and live tuning commands
to NetworkTables and the WPILib Tunables registry so you can tune your mechanism and swerve parameters in real-time
from a dashboard (Elastic, Shuffleboard, AdvantageScope) while the robot runs, without redeploying code.

Because live tuning is handled directly by starting the built-in YAMS tuning commands and modifying tuning parameters
in NetworkTables, **no dedicated Live Tuning OpMode is needed**.

### Tuning Commands in YAMS

- **Swerve Drive-to-Pose / Module Tuning Command**:
  Published under `/Mechanisms/<driveName>/tuning/driveToPose` (named `"<driveName> DriveToPoseTuning"`, e.g.,
  `"swerve DriveToPoseTuning"` in Commands v3). When active, it continuously applies tuning values from `/Tuning/<driveName>/`
  to the drive and module controllers.
- **Smart Motor Controller Live Tuning Command**:
  For custom mechanisms using `SmartMotorControllerConfig.setupLiveTuning()`, YAMS registers a shared `"Live Tuning"`
  command on the mechanism published under `/Tuning/<MechanismName>/Live Tuning`.

### How to Live Tune

Tuning is available because `SwerveDriveMechanism` names the drive `"swerve"` and configures
`.withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH))` with both drive-to-pose
controllers set. With a different name, replace `swerve` in the keys below.

1. **Start in simulation**, or with the robot elevated on blocks. Tuning moves the robot.
2. **Start the tuning command** at `/Mechanisms/swerve/tuning/driveToPose` (set its `running` entry to `true`, or
   click it in your dashboard). Values under `/Tuning/swerve/` only take effect while it runs.
3. **Turn on one tuning mode** below and adjust its parameters. Only one mode drives at a time; if you enable more than
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
> the gyro facing 0° before enabling it. The `driveToPose` commands and autonomous routines are not affected.

**Tuned values are not saved.** When you're happy, copy them into your configuration: the module drive/azimuth
gains into `src/main/deploy/swerve/base/modules/pidfproperties.json` (`drive` and `angle`), and the translation and
rotation gains into the `PIDController`s in `SwerveDriveMechanism`.

---

## Project layout

```text
build.gradle                                     # YAGSL_TESTING block compiles YAGSL from ../../yagsl/java
vendordeps/                                      # CommandsV3, YAMS, Phoenix6, REVLib, ReduxLib
src/main/java/frc/robot/
├── Robot.java                                   # OpModeRobot: the swerve mechanism, controller, battery sim
├── mechanisms/
│   └── SwerveDriveMechanism.java                 # YAGSL config → YAMS SwerveDrive; drive styles, driveToPose
└── opmodes/
    ├── teleop/                                   # Heading Control, Angular Velocity Control, Demo Mode
    └── auto/                                     # Leave Starting Area, Out And Back, Drive To Pose Square
src/main/deploy/swerve/base/                      # YAGSL swerve config: replace with your own
src/test/java/frc/robot/RobotTest.java            # simulation smoke test: builds every opmode and runs the robot
```

---

## Meet Rivet

Rivet is the frog mascot of the Yet Another Software Suite. At events you'll usually find Rivet working as a
Control System Advisor, helping teams get their robots talking, and every so often borrowing a clipboard to
cosplay as the Lead Robot Inspector (Rivet's bumper-height checks are *extremely* thorough). Rivet loves teaching,
and loves a harmless prank almost as much, so a few easter eggs have a way of turning up in the YASS tools. Rivet
is also the reason Demo Mode exists: there is no better way to teach someone swerve than handing them the
controller. If you find an easter egg, Rivet would love to know: tell us on [Discord](https://discord.gg/yass).

---

## Links

| Resource                                | Link                                                                                                   |
|-----------------------------------------|--------------------------------------------------------------------------------------------------------|
| Swerve config generator + Tuning Guide  | [config.yagsl.com](https://config.yagsl.com)                                                           |
| YAGSL docs                              | [docs.yagsl.com](https://docs.yagsl.com)                                                               |
| YAGSL javadocs                          | [yet-another-software-suite.github.io/YAGSL/javadocs](https://yet-another-software-suite.github.io/YAGSL/javadocs/) |
| YAMS (mechanisms)                       | [github.com/Yet-Another-Software-Suite/YAMS](https://github.com/Yet-Another-Software-Suite/YAMS)       |
| Discord                                 | [discord.gg/yass](https://discord.gg/yass)                                                             |

Found a bug? [Open an issue](https://github.com/Yet-Another-Software-Suite/YAGSL/issues).
