# YAGSL Example Project

This is the reference robot project for [YAGSL](https://docs.yagsl.com) — a working,
buildable Command-based robot that wires YAGSL's swerve config parser into
[YAMS](https://github.com/Yet-Another-Software-Suite/YAMS)'s `SwerveDrive` mechanism, plus
PathPlanner autonomous and two independent vision pose sources.

> **Vision is optional.** The Limelight and PhotonVision subsystems are included purely as a
> reference for how to fuse vision poses into the drivetrain — nothing else in this project
> depends on them. If you don't have a coprocessor/camera set up yet (or don't want one), see
> [Removing vision entirely](#removing-vision-entirely) to delete it cleanly.

> **Configuring your own swerve drive?** Start at
> **[config.yagsl.com](https://config.yagsl.com)** and follow the **Quick Start / Tuning
> Guide** it walks you through after you download your configuration. That guide uses this
> exact project — it has you clone this repo, copy this `example/` folder out as your own
> robot project, drop in your generated `src/main/deploy/swerve` config, and tune from there.
> This README covers what the guide doesn't: everything in this project that isn't swerve
> config (PathPlanner, vision, driver controls) and how the pieces fit together.

---

## What's in this example

| Piece                                              | File(s)                                                     |
|-----------------------------------------------------|--------------------------------------------------------------|
| YAGSL config parsing → YAMS `SwerveDrive`            | `subsystems/swervedrive/SwerveDriveSubsystem.java`            |
| PathPlanner `AutoBuilder` + on-the-fly pathfinding   | `subsystems/swervedrive/SwerveDriveSubsystem.java`            |
| Autonomous chooser (built from `deploy/pathplanner`) | `RobotContainer.java`                                         |
| MegaTag2 pose fusion via a Limelight (YALL) *(optional, reference-only)*  | `subsystems/vision/LimelightVisionSubsystem.java`              |
| AprilTag pose fusion via PhotonVision, incl. sim *(optional, reference-only)* | `subsystems/vision/PhotonVisionSubsystem.java`                 |
| Driver controls, battery sim                         | `RobotContainer.java`                                          |

Both vision subsystems are independent and run side by side — each filters its own readings
(distance/tag-count/ambiguity) before fusing them into the same `SwerveDriveSubsystem` pose
estimator, and each publishes its accepted pose as a separate object on the swerve drive's
shared `Field2d` (`drivetrain.getField2d()`) rather than creating its own field widget. Run
one, both, or neither — they don't depend on each other, and the drivetrain works fine with
no vision at all. See [Removing vision entirely](#removing-vision-entirely).

---

## Quick setup

1. Clone [Yet-Another-Software-Suite/YAGSL](https://github.com/Yet-Another-Software-Suite/YAGSL)
   and copy this `example/` folder out into your own robot project directory — that's what
   you'll actually build and deploy.
2. Set your team number (WPILib extension: `Ctrl+Shift+P` → **WPILib: Set Team Number**, or edit
   `.wpilib/wpilib_preferences.json` directly).
3. Generate your own swerve configuration at **[config.yagsl.com](https://config.yagsl.com)**,
   delete the example's `src/main/deploy/swerve` folder, and unzip your download in its place.
4. Fill in the [things to customize](#things-to-customize-before-you-fly) below — the
   PathPlanner PID gains and (if you're keeping vision) the camera names/offsets are
   placeholders, not tuned values. Not using vision? See
   [Removing vision entirely](#removing-vision-entirely) instead.
5. Build and follow the rest of the **[Tuning Guide](https://config.yagsl.com)** to tune PID
   gains, align modules, and verify field orientation, in simulation first and then on the
   real robot.

---

## Driver controls

Defined in `RobotContainer.configureBindings()` (Xbox controller):

| Input                          | Action                                                                 |
|---------------------------------|-------------------------------------------------------------------------|
| Left stick                      | Translate (field/alliance-relative)                                     |
| Right stick X (default mode)    | Rotate — angular velocity control                                       |
| Right stick X/Y (after **A**)   | Rotate — heading (snap-to-angle) control                                |
| **A**                            | Toggle between angular-velocity and heading-based rotation control      |
| **X**                            | Drive to a fixed demo point using PathPlanner's on-the-fly pathfinding  |
| **Y**                            | Drive to the same demo point using YAMS' `SwerveDrive.driveToPose(...)` |
| **Start + Back**                 | Zero the gyro                                                           |
| Button 1                        | Run the front-left module's SysId characterization routine (hold)       |

> **Heads up:** on a standard Xbox controller, raw **Button 1 is the same physical button as
> A**. As written, both the SysId routine and the heading-control toggle are bound to it —
> that's a real conflict worth resolving (e.g. move SysId to a bumper or trigger) before you
> rely on either binding.

`X` and `Y` both drive to the same placeholder `Pose2d` (3m, 3m, 180°) — change that to
something meaningful for your field, or wire it to a real target (a scoring location, an
`AprilTag` pose, etc.) instead of a hardcoded point.

---

## Things to customize before you fly

None of the following are tuned for a real robot — they're placeholders so the project builds
and runs out of the box:

- **PathPlanner PID gains.** `SwerveDriveSubsystem.configurePathPlanner()` uses placeholder
  `PPHolonomicDriveController` gains (`5, 0, 0` for both translation and rotation) — tune
  these for your drivetrain.
- **`src/main/deploy/pathplanner/settings.json`.** Feeds `RobotConfig.fromGUISettings()` —
  edit it (or regenerate via the PathPlanner GUI) with your robot's real mass, MOI, wheel
  COF, and module locations.

If you're keeping the vision subsystems (see below), also set:

- **Camera names.** `LimelightVisionSubsystem` looks for a camera named `"limelight"`;
  `PhotonVisionSubsystem` looks for `"photonvision"`. Rename to match what's actually
  configured on your coprocessor(s).
- **Camera mount offsets.** Both vision subsystems hardcode a 5in-forward/8in-up
  `CAMERA_OFFSET` / `ROBOT_TO_CAMERA` transform — measure and set your own.
- **Vision filter thresholds.** Each vision subsystem rejects readings past a distance/tag
  count/ambiguity threshold before fusing them — the defaults are reasonable starting points,
  not universal constants; tune them for your field and cameras.

---

## Removing vision entirely

Nothing in this project requires vision — `LimelightVisionSubsystem` and
`PhotonVisionSubsystem` are two independent, optional examples of fusing a vision pose into
`SwerveDriveSubsystem`'s pose estimator. If you don't want either (no coprocessor yet, using a
different vision stack, or just want a leaner starting point), delete them:

1. Delete `src/main/java/frc/robot/subsystems/vision/LimelightVisionSubsystem.java` and/or
   `PhotonVisionSubsystem.java`.
2. In `RobotContainer.java`, remove the corresponding field(s) —
   `limelightVision`/`photonVision` — and their imports. Nothing else in `RobotContainer`
   references them.
3. Remove the now-unused vendor dependencies you're not using from `vendordeps/`:
   `yall.json` (Limelight/YALL) and/or `photonlib.json` (PhotonVision).
4. **`SwerveDriveSubsystem` needs no changes.** `addVisionMeasurement(...)`, `getField2d()`,
   `getGyroRotation3d()`, and `getGyroAngularVelocity()` are plain passthroughs that only exist
   for vision subsystems to call — the drivetrain, PathPlanner, and driver controls all work
   exactly the same with or without them being used.

---

## Project layout

```text
src/main/java/frc/robot/
├── Constants.java
├── Robot.java
├── RobotContainer.java                          # driver controls, autonomous chooser, battery sim
└── subsystems/
    ├── swervedrive/
    │   └── SwerveDriveSubsystem.java             # YAGSL config → YAMS SwerveDrive, PathPlanner hookup
    └── vision/                                   # optional, reference-only (see above)
        ├── LimelightVisionSubsystem.java         # YALL / Limelight MegaTag2 pose fusion
        └── PhotonVisionSubsystem.java            # PhotonVision pose fusion, incl. VisionSystemSim

src/main/deploy/
├── swerve/base/                                  # YAGSL swerve config (replace with your own)
└── pathplanner/                                  # paths, autos, and PathPlanner GUI settings
```

---

## Links

| Resource               | Link                                                                                              |
|--------------------------|------------------------------------------------------------------------------------------------------|
| Swerve config generator + Tuning Guide | [config.yagsl.com](https://config.yagsl.com)                                        |
| YAGSL docs               | [docs.yagsl.com](https://docs.yagsl.com)                                                             |
| YAGSL javadocs            | [yet-another-software-suite.github.io/YAGSL/javadocs](https://yet-another-software-suite.github.io/YAGSL/javadocs/) |
| YAMS (mechanisms)         | [github.com/Yet-Another-Software-Suite/YAMS](https://github.com/Yet-Another-Software-Suite/YAMS)     |
| YALL (Limelight)          | [yall.yassrobotics.com](https://yall.yassrobotics.com)                                               |
| PathPlanner               | [pathplanner.dev](https://pathplanner.dev)                                                           |
| PhotonVision              | [docs.photonvision.org](https://docs.photonvision.org)                                               |
| Discord                   | [discord.gg/yass](https://discord.gg/yass)                                                           |

Found a bug? [Open an issue](https://github.com/Yet-Another-Software-Suite/YAGSL/issues).
