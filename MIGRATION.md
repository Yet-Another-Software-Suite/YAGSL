# Migrating from YAGSL 2026.4.1 to YAGSL 2027

This guide lists every difference between YAGSL `2026.4.1`, the last release of the original
library for WPILib `2026.2.1` and the roboRIO, and YAGSL for WPILib `2027_alpha7` and the
SystemCore.

Between the two, YAGSL was rewritten. It used to be a complete swerve library. It is now a JSON
parser that builds a [YAMS](https://github.com/Yet-Another-Software-Suite/YAMS) `SwerveDrive`, and
YAMS does the driving, odometry, simulation and telemetry. Your JSON files, your vendordeps, your
build files and most of your swerve code change.

- **Current documentation:** [docs.yagsl.com](https://docs.yagsl.com)
- **Current config generator:** [config.yagsl.com](https://config.yagsl.com)
- **Documentation for 2026.4.1:** the book as it was before the rewrite, in
  [YAGSL-gitbook #21](https://github.com/Yet-Another-Software-Suite/YAGSL-gitbook/pull/21)
- **Config generator for 2026.4.1:** [config.yagsl.com/old](https://config.yagsl.com/old)

> [!TIP]
> Regenerating your configuration with [config.yagsl.com](https://config.yagsl.com) is faster and
> safer than porting it by hand. Open your old configuration in
> [config.yagsl.com/old](https://config.yagsl.com/old) to read off your CAN IDs, offsets, gearing
> and locations, then enter them in the new generator.

> [!WARNING]
> The parser ignores fields it doesn't know. An old file still loads, but every renamed field is
> silently dropped, and a missing `gyro`, `absoluteEncoder`, `inverted` or `location` fails at
> startup with a `NullPointerException`. Don't assume an old file works because it parses.

## Contents

1. [Checklist](#checklist)
2. [WPILib, Java and the SystemCore](#wpilib-java-and-the-systemcore)
3. [Vendordeps](#vendordeps)
4. [JSON configuration](#json-configuration)
5. [Device types](#device-types)
6. [CAN buses](#can-buses)
7. [Robot code](#robot-code)
8. [Behavior changes](#behavior-changes)
9. [Not available on 2027_alpha7](#not-available-on-2027_alpha7)

## Checklist

1. Install WPILib `2027_alpha7` and move your project to it: Java 25, the SystemCore, and the
   2027 `build.gradle` and `settings.gradle`.
2. Delete `vendordeps/yagsl.json` and every Phoenix 5 vendordep. Install `yagsl_commands2.json`
   **or** `yagsl_commands3.json`, which brings in the matching YAMS vendordep, and the 2027
   vendordeps for the hardware in your configuration.
3. Regenerate your `swerve/` directory at [config.yagsl.com](https://config.yagsl.com), or port it
   by hand with [JSON configuration](#json-configuration), [Device types](#device-types) and
   [CAN buses](#can-buses).
4. Delete `controllerproperties.json`.
5. Replace hardware that has no 2027 support: a Pigeon (gen 1), any NavX or NavX2, an SPI or analog
   gyro, a Talon SRX, or a brushed SPARK MAX.
6. Port your code: imports, the parser calls, the `SwerveDrive` methods, `SwerveInputStream`, and
   `updateTelemetry()` every loop.
7. Set your maximum chassis speed with `SwerveDriveConfig.withMaximumChassisSpeed`.
8. Retune the PID and feedforward gains. The old gains don't carry over.
9. Deploy, confirm every device shows up, and check the gyro direction and every module's offset
   before driving.

## WPILib, Java and the SystemCore

| | 2026.4.1 | 2027 |
| --- | --- | --- |
| WPILib | `2026.2.1` | `2027_alpha7` (GradleRIO `2027.0.0-alpha-7`) |
| Robot controller | roboRIO | SystemCore |
| Java | 17 | 25 |
| Gradle | 8.10.2 | 9.4.1 |
| GradleRIO plugin | `edu.wpi.first.GradleRIO` | `org.wpilib.GradleRIO` |
| WPILib packages | `edu.wpi.first.*` | `org.wpilib.*` |
| Deploy directory | `/home/lvuser/deploy` | `/home/systemcore/deploy` |
| WPILib folder | `~/wpilib/2026`, `C:\Users\Public\wpilib\2026` | Windows `%PUBLIC%\wpilib\2027_alpha7`, Linux `~/.local/share/wpilib/2027_alpha7` (or `$XDG_DATA_HOME`), macOS `~/.wpilib/2027_alpha7` |

The easiest way to get a 2027 `build.gradle`, `settings.gradle` and
`.wpilib/wpilib_preferences.json` is to create a new project with WPILib `2027_alpha7`, or start
from `example-commands2.zip` or `example-commands3.zip` on the
[releases page](https://github.com/Yet-Another-Software-Suite/YAGSL/releases). The 2027
`build.gradle` deploys to the SystemCore, builds with the `application` plugin, and no longer needs
`org.dyn4j:dyn4j`.

## Vendordeps

| | 2026.4.1 | 2027 |
| --- | --- | --- |
| YAGSL vendordep | `yagsl.json` | `yagsl_commands2.json` **or** `yagsl_commands3.json`; they conflict, install one |
| URL | `https://yet-another-software-suite.github.io/YAGSL/yagsl.json` | `https://cdn.yassrobotics.com/yagsl_commands2.json`, `https://cdn.yassrobotics.com/yagsl_commands3.json` |
| Artifacts | `YAGSL-java`, plus `dyn4j` for MapleSim | `YAGSL-core`, plus `YAGSL-commands2` or `YAGSL-commands3` |
| Requires | ReduxLib, REVLib and ThriftyLib. Phoenix 5, Phoenix 6 and Studica were needed but not listed. | Only the matching YAMS vendordep, which requires WPILib's Commands v2 or v3 |

> [!WARNING]
> Don't update the old `yagsl.json` with **Check for updates**. In a 2026 project it updates to
> YAGSL `2026.10.03`, the rewrite on WPILib 2026, not to 2027. Delete it and install the 2027
> vendordep.

Hardware vendor libraries are optional now. YAGSL loads a vendor's classes only when your
configuration uses that vendor's devices, so install the 2027 vendordeps for the hardware you have.
A missing one fails when the device is created, with a message naming the library.

| Library | Version YAGSL builds against | Vendordep URL |
| --- | --- | --- |
| Phoenix 6 | `26.70.0-alpha-2` | Install from WPILib's vendor libraries. Its `jsonUrl` is not online. |
| REVLib | `2027.0.0-alpha-9` | `https://software-metadata.revrobotics.com/REVLib-2027.json` |
| ReduxLib | `2027.0.0-alpha-7p1` | `https://frcsdk.reduxrobotics.com/ReduxLib_2027.json` |
| StudicaLib | `2027.0.0-alpha-1` | `https://dev.studica.com/maven/release/2027/json/StudicaLib.json` |
| ThriftyLib | `2027.0.0-alpha-6` | `https://software.thethriftybot.com/frcvendor/ThriftyLib-2027.json` |

Phoenix 5 is gone: no 2027 device uses it. Delete its vendordep.

Each release also attaches `<version>-YAGSL-Offline.zip` for offline installs, which extracts into
the WPILib `2027_alpha7` folder.

## JSON configuration

The directory still has `swervedrive.json`, one file per module, and a `modules/` folder:

```
swerve/
├── swervedrive.json
├── frontleft.json, frontright.json, backleft.json, backright.json
└── modules/
    ├── physicalproperties.json
    ├── pidfproperties.json
    └── pidfproperties_sim.json   (new, optional)
```

- **`controllerproperties.json` is no longer read.** Delete it. Heading control moved to robot code;
  see [SwerveInputStream](#swerveinputstream).
- **`modules/pidfproperties_sim.json` is new and optional.** In simulation it replaces
  `pidfproperties.json`, so you can keep separate gains for the simulator.

### swervedrive.json

| 2026.4.1 | 2027 | Notes |
| --- | --- | --- |
| `imu: {type, id, canbus}` | `gyro: {type, id, canbus, channel}` | Renamed. The `type` strings changed; see [Gyros](#gyros). `channel` is the USB port for `navx3_usb`. |
| `invertedIMU` | `gyroInvert` | Renamed. Default `false`. |
| | `gyroAxis` | New. `"yaw"` (default), `"pitch"` or `"roll"`: the gyro axis used as the robot heading. |
| `modules` | `modules` | Unchanged. Each module's name is its file name without `.json`. |

```json
{
  "gyro": { "type": "pigeon2_can", "id": 13, "canbus": "" },
  "gyroAxis": "yaw",
  "gyroInvert": false,
  "modules": ["frontleft.json", "frontright.json", "backleft.json", "backright.json"]
}
```

### Module files

| 2026.4.1 | 2027 | Notes |
| --- | --- | --- |
| `drive`, `angle`: `{type, id, canbus}` | Same | The `type` strings changed; see [Motor controllers](#motor-controllers). |
| `encoder: {type, id, canbus}` | `absoluteEncoder: {type, id, channel, canbus}` | Renamed, and now required: `"none"` is gone. The `type` strings changed; see [Absolute encoders](#absolute-encoders). DIO and analog encoders take their port in `channel`, not `id`. |
| `absoluteEncoderOffset` | Same | Degrees, subtracted from the encoder reading, as before. |
| `absoluteEncoderInverted` | Same | Default `false`. |
| | `absoluteEncoderGearRatio` | New. Default `1`. A reduction between the absolute encoder and the module, used when the module reads the encoder itself rather than through the angle motor controller. |
| `conversionFactors: {drive: {gearRatio, diameter, factor}, angle: {gearRatio, factor}}` | `gearing: {drive: {gearRatio, diameter}, angle: {gearRatio}}` | Renamed, and `factor` is gone. Optional: it overrides `physicalproperties.json` for this module. The override rule changed; see the warning below. |
| `inverted: {drive, angle}` | Same | |
| `location: {front, left}` | Same | Inches from the robot's center. |
| `useCosineCompensator` | Removed | Cosine compensation is always on. See [Behavior changes](#behavior-changes). |

> [!WARNING]
> A module's `gearing.drive` replaces the shared drive gearing when its `gearRatio` isn't `0`. A
> module that sets only `gearRatio` keeps the shared wheel diameter, as the old `conversionFactors`
> override did.

```json
{
  "drive": { "type": "sparkmax_neo", "id": 4, "canbus": "" },
  "angle": { "type": "sparkmax_neo", "id": 3, "canbus": "" },
  "absoluteEncoder": { "type": "cancoder_can", "id": 9, "channel": 0, "canbus": "" },
  "inverted": { "drive": false, "angle": false },
  "absoluteEncoderOffset": -114.609,
  "absoluteEncoderInverted": false,
  "location": { "front": 12, "left": 12 }
}
```

### modules/physicalproperties.json

| 2026.4.1 | 2027 | Notes |
| --- | --- | --- |
| `conversionFactors: {drive: {gearRatio, diameter, factor}, angle: {gearRatio, factor}}` | `gearing: {drive: {gearRatio, diameter}, angle: {gearRatio}}` | Renamed. `factor` is gone: give the gear ratio (X:1) and the wheel diameter in inches. |
| `conversionFactor` (deprecated) | Removed | |
| `currentLimit: {drive, angle}` | `statorCurrentLimit: {drive, angle}` | Renamed. Default `40` and `20` A, as before, but now a stator limit on every motor controller. See [Behavior changes](#behavior-changes). |
| `rampRate: {drive, angle}` | Removed | No ramp is applied. The old default was 0.25 s. |
| `optimalVoltage` | Removed | No voltage compensation is applied. The old default was 12 V, also used for the default drive feedforward. |
| `friction`, `steerRotationalInertia`, `robotMass`, `wheelGripCoefficientOfFriction` | Removed | They configured the MapleSim simulation, which is gone. |

```json
{
  "gearing": {
    "drive": { "gearRatio": 8.14, "diameter": 4 },
    "angle": { "gearRatio": 12.8 }
  },
  "statorCurrentLimit": { "drive": 40, "angle": 20 }
}
```

### modules/pidfproperties.json

| 2026.4.1 | 2027 | Notes |
| --- | --- | --- |
| `p`, `i`, `d` | Same | Different units; see the note below. |
| `f` | Removed | It was not a general feedforward: SPARKs and the Nova ignored it, and TalonFX and TalonFXS used it as kS. |
| `iz` | Removed | |
| `output: {min, max}` | Removed | |
| | `s`, `v`, `a` | New. A `SimpleMotorFeedforward` for each motor, default `0`. When the drive's `v` is `0`, YAGSL works it out from the drive motor's free speed and gearing. |

> [!IMPORTANT]
> Retune your gains. The old ones were in each vendor's own units, such as duty cycle per degree on a
> SPARK. YAMS's closed loop outputs volts, from an error in rotations, or meters for the drive
> motor. The [current documentation](https://docs.yagsl.com) and the Tuning Guide at
> [config.yagsl.com](https://config.yagsl.com) walk through tuning.

The file's shape, with your own tuned values:

```json
{
  "drive": { "p": 0, "i": 0, "d": 0, "s": 0, "v": 0, "a": 0 },
  "angle": { "p": 0, "i": 0, "d": 0, "s": 0, "v": 0, "a": 0 }
}
```

## Device types

Every `type` is now `<device>_<connection>`, or `<controller>_<motor>` for motor controllers.

### Gyros

| 2026.4.1 | 2027 |
| --- | --- |
| `pigeon2` | `pigeon2_can` |
| `canandgyro` | `canandgyro_can` |
| `navx3` | `navx3_can`. Its `canbus` is now used. |
| `pigeon` (Pigeon gen 1), `pigeon_via_talonsrx` | **Removed.** Phoenix 5 has no 2027 release. |
| `navx`, `navx_spi`, `navx_i2c`, `navx_usb`, `navx_mxp_serial` (NavX and NavX2) | **Removed.** There is no NavX2 library for WPILib 2027 yet. |
| `adis16448`, `adis16470`, `adxrs450`, `analog` | **Removed.** |
| | `navx3_usb`: a NavX3 on a SystemCore USB port, with the port (0 to 3) in `channel` |
| | `systemcore_internal`: the IMU built into the SystemCore |
| | `custom`: YAGSL builds no gyro, and you supply it with `SwerveDriveConfig.withGyro(...)` |

With a removed gyro, change to a supported one, or keep yours with `custom` and your own code.

### Motor controllers

Controllers are `talonfx`, `talonfxs`, `sparkmax`, `sparkflex` and `nova`. Motors are `neo`,
`neo2`, `neo550`, `vortex`, `minion`, `pulsar`, `krakenx44`, `krakenx60`, `falcon` and
`falcon500`. A TalonFX only takes `krakenx44`, `krakenx60`, `falcon` and `falcon500`.

| 2026.4.1 | 2027 |
| --- | --- |
| `neo`, `sparkmax`, `sparkmax_neo` | `sparkmax_neo` |
| `neo550`, `sparkmax_neo550` | `sparkmax_neo550` |
| `sparkmax_vortex`, `sparkmax_minion` | Same |
| `sparkflex`, `sparkflex_vortex` | `sparkflex_vortex` |
| `sparkflex_neo`, `sparkflex_neo550`, `sparkflex_minion` | Same |
| `falcon`, `falcon500` | `talonfx_falcon` |
| `talonfx`, `krakenx60` | `talonfx_krakenx60` |
| `krakenx44` | `talonfx_krakenx44` |
| `falcon500foc`, `krakenx60foc` | **Removed.** Use `talonfx_falcon` or `talonfx_krakenx60`. |
| `talonfxs_neo`, `talonfxs_neo550`, `talonfxs_vortex`, `talonfxs_minion` | Same |
| `nova_neo`, `nova_neo550`, `nova_vortex`, `nova_minion` | Same |
| `*_pulsar` (not supported) | `sparkmax_pulsar`, `sparkflex_pulsar`, `talonfxs_pulsar`, `nova_pulsar` |
| `talonsrx` | **Removed.** Phoenix 5 has no 2027 release. |
| `sparkmax_brushed` | **Removed.** Only brushless motors are supported. |

### Absolute encoders

`_attached` encoders plug into the angle motor controller's data port, and work with a SPARK MAX,
SPARK Flex or Nova angle motor, not a TalonFX or TalonFXS. `_dio` and `_analog` encoders plug into
a SystemCore SmartIO port, given in `channel`.

| 2026.4.1 | 2027 |
| --- | --- |
| `cancoder` | `cancoder_can` |
| `canandmag_can`, `canandcoder_can` | `canandmag_can` |
| `integrated`, `attached`, `sparkflex_integrated`, `sparkflex_attached` | `revthroughbore_attached`, or the attached encoder's own type below |
| `canandmag`, `canandcoder`, `sparkflex_canandmag`, `sparkflex_canandcoder` | `canandmag_attached` |
| `sparkmax_analog` | `analog_attached` |
| `sparkmax_analog5v` | `analog5v_attached` |
| `thrifty_nova_rev` | `revthroughbore_attached` |
| `thrifty_nova_redux` | `canandmag_attached` |
| `thrifty_nova_srx_mag` | `srxmag_attached` |
| `throughbore`, `rev_hex`, `dutycycle` (DIO, port in `id`) | `revthroughbore_dio`, port in `channel` |
| `am_mag` (DIO) | `andymarkhexbore_dio` |
| `ctre_mag` (DIO) | `revthroughbore_dio`: every `_dio` type reads a duty cycle encoder |
| `thrifty`, `ma3`, `analog` (analog input, port in `id`) | `thrifty_analog`, `srxmag_analog` or `andymarkhexbore_analog`, port in `channel` |
| `talonfxs_analog` | **Removed.** |
| `srxmag_standalone`, `talonsrx_pwm`, `talonsrx_analog` | **Removed.** Phoenix 5 has no 2027 release. |
| `none` | **Removed.** Every module needs an absolute encoder. |
| | New: `splineencoder_can`, `thrifty_attached`, `srxmag_attached`, `dutycycle_attached`, `canandmag_dio` |

## CAN buses

In 2026.4.1 only CTRE devices read `canbus`: empty or `null` was the roboRIO's bus, and anything
else was a CANivore's name. Every other vendor ignored it. Now every device reads it the same way:

| `canbus` | Bus |
| --- | --- |
| `""`, `null` or missing | SystemCore `can_s0` |
| `"0"` to `"4"` | SystemCore `can_s0` to `can_s4` |
| `"5"` to `"24"` | Motioncore `can_d0` to `can_d19` |
| `"CAN_S0"` to `"CAN_S4"`, `"CAN_D0"` to `"CAN_D19"` | The same buses by name |
| Any other number | An error, for every vendor |
| Any other string | Passed to the vendor: a CANivore's name for CTRE devices, or a ReduxLib bus string |

- CTRE devices with an empty `canbus` are on `can_s0`, not Phoenix 6's SystemCore default
  `can_s2`. If yours are wired to `can_s2`, set `"canbus": "2"`.
- REV, ThriftyBot and Studica devices only take a bus number or name, and reject anything else.
- A CTRE device on a CANivore keeps the CANivore's name.

The SystemCore has five CAN buses, so set each device's `canbus` to the bus it is wired to.

## Robot code

### Packages

| 2026.4.1 | 2027 |
| --- | --- |
| `swervelib.parser.SwerveParser` | `swervelib.commands2.SwerveParser` or `swervelib.commands3.SwerveParser` |
| `swervelib.parser.*` | `swervelib.core.parser.*` |
| `swervelib.SwerveDrive` | `yams.commands2.swerve.SwerveDrive` or `yams.commands3.swerve.SwerveDrive` |
| `swervelib.SwerveInputStream` | `yams.commands2.swerve.SwerveInputStream` or `yams.commands3.swerve.SwerveInputStream` |
| `swervelib.SwerveModule` | `yams.core.mechanisms.swerve.SwerveModule` |
| `swervelib.telemetry.SwerveDriveTelemetry.verbosity` | `SwerveDriveConfig.withTelemetry(name, yams.core.telemetry.enums.TelemetryVerbosity)` |
| `swervelib.SwerveController`, `swervelib.SwerveDriveTest`, `swervelib.math.*`, `swervelib.imu.*`, `swervelib.encoders.*`, `swervelib.motors.*`, `swervelib.simulation.*`, `swervelib.telemetry.Alert` | Removed |

> [!NOTE]
> `swervelib.core.telemetry.SwerveDriveTelemetry` still compiles, but nothing reads it: setting its
> `verbosity` does nothing. Set the telemetry on the `SwerveDriveConfig`.

WPILib 2027 renamed the types swerve code uses most:

| WPILib 2026 | WPILib 2027 |
| --- | --- |
| `ChassisSpeeds` (`vxMetersPerSecond`, `vyMetersPerSecond`, `omegaRadiansPerSecond`) | `ChassisVelocities` (`vx`, `vy`, `omega`) |
| `ChassisSpeeds.fromFieldRelativeSpeeds(...)` | `chassisVelocities.toRobotRelative(rotation)` |
| `SwerveModuleState` (`speedMetersPerSecond`, `angle`) | `SwerveModuleVelocity` (`velocity`, `angle`) |
| `SwerveModulePosition.distanceMeters` | `SwerveModulePosition.distance` |
| `SwerveDriveKinematics.toSwerveModuleStates`, `toChassisSpeeds`, `desaturateWheelSpeeds` | `toSwerveModuleVelocities`, `toChassisVelocities`, `desaturateWheelVelocities` |
| `Pose2d.kZero`, `Rotation2d.kZero` | `Pose2d.ZERO`, `Rotation2d.ZERO` |
| `Translation2d.getAngle()` returns `Rotation2d` | Returns `Optional<Rotation2d>` |
| `DriverStation.getAlliance()`, `DriverStation.Alliance.Red` | `MatchState.getAlliance()`, `Alliance.RED` |
| `edu.wpi.first.wpilibj2.command.*` | `org.wpilib.command2.*` |

### Creating the drive

**2026.4.1:**

```java
SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
SwerveDrive drive = new SwerveParser(directory).createSwerveDrive(maximumSpeed, startingPose);
```

**2027, Commands v2:**

```java
import swervelib.commands2.SwerveParser;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;
import yams.core.telemetry.SwerveDriveTelemetryConfig;
import yams.core.telemetry.enums.TelemetryVerbosity;

SwerveDriveConfig cfg = new SwerveDriveConfig()
    .withSubsystem(this)
    .withStartingPose(startingPose)
    .withMaximumChassisSpeed(MetersPerSecond.of(4.5), RotationsPerSecond.of(1.5))
    .withTranslationController(new PIDController(5, 0, 0))
    .withRotationController(new PIDController(5, 0, 0))
    .withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

SwerveParser.parse(directory);
SwerveDrive drive = SwerveParser.createSwerveDrive(cfg);
```

For Commands v3, import the `commands3` classes and use `withMechanism(this)` instead of
`withSubsystem(this)`.

- `SwerveParser.parse(dir)` and `createSwerveDrive(cfg)` are static, and can't be chained.
- `SwerveParser.createSwerveDriveDevices(cfg)` also returns the vendor devices YAGSL created: the
  gyro, and each module's drive, angle and absolute encoder.
- The old `maximumSpeed` and conversion factor arguments are gone. See
  [Behavior changes](#behavior-changes) for the maximum speed.
- The parser sets the modules, the maximum module speed, a 20 ms discretization time, and the gyro
  and its axis and inversion on your `SwerveDriveConfig`. Don't set those yourself.

### Updating every loop

The old drive updated its odometry on its own thread. Now the pose updates only when you call
`drive.updateTelemetry()`, so call it every loop, and `drive.simIterate()` in simulation:

```java
// Commands v2, in the subsystem
@Override
public void periodic() {
  drive.updateTelemetry();
}

@Override
public void simulationPeriodic() {
  drive.simIterate();
}
```

A Commands v3 `Mechanism` has no `periodic()`. Call `updateTelemetry()` from
`Robot.robotPeriodic()`, and `simIterate()` from `Robot.simulationPeriodic()`.

### SwerveDrive

| 2026.4.1 | 2027 |
| --- | --- |
| `drive(ChassisSpeeds)`, `setChassisSpeeds(ChassisSpeeds)` | `setRobotRelativeChassisSpeeds(ChassisVelocities)` |
| `driveFieldOriented(ChassisSpeeds)` | `setFieldRelativeChassisSpeeds(ChassisVelocities)` |
| `drive(Translation2d, double, boolean fieldRelative, boolean isOpenLoop)` | Build a `ChassisVelocities` and call one of the two above. Open loop is gone. |
| `drive(ChassisSpeeds, Translation2d centerOfRotation)` | `SwerveDriveConfig.withCenterOfRotation(...)` |
| `driveFieldOrientedAndRobotOriented(...)` | Removed. Add the two with `ChassisVelocities.toRobotRelative(...)` and `plus(...)`. |
| `drive(ChassisSpeeds, SwerveModuleState[], Force[])` (PathPlanner) | `setRobotRelativeChassisSpeeds(ChassisVelocities, Force[])` |
| `setModuleStates(SwerveModuleState[], boolean)` | `setSwerveModuleStates(SwerveModuleVelocity[])` |
| `getPose()`, `resetOdometry(Pose2d)`, `zeroGyro()` | Same. `resetOdometry` also resets the gyro to the pose's heading. |
| `getOdometryHeading()` | `getPose().getRotation()` |
| `getYaw()`, `getPitch()`, `getRoll()`, `getGyroRotation3d()` | `getGyroRotation3d()` |
| `getGyro()` | `createSwerveDriveDevices(cfg).gyro()` |
| `setGyro(Rotation3d)` | `resetOdometry(Pose2d)` |
| `setGyroOffset(Rotation3d)` | `SwerveDriveConfig.withGyroOffset(Angle)` |
| `addVisionMeasurement(...)`, `setVisionMeasurementStdDevs(...)` | Same |
| `getRobotVelocity()`, `getFieldVelocity()` | `getRobotRelativeSpeed()`, `getFieldRelativeSpeed()` |
| `getStates()` | `getModuleStates()`. Also new: `getDesiredModuleStates()`, `getDesiredChassisSpeeds()`. |
| `getModulePositions()`, `getModules()`, `lockPose()` | Same |
| `getModuleMap()` | `getModule(String name)`, which returns an `Optional` |
| `kinematics` | `getKinematics()` |
| `field`, `postTrajectory(Trajectory)` | `getField2d()`, then `getObject(name).setTrajectory(...)` |
| `setMotorIdleMode(boolean)` | Per motor: `module.getDriveMotorController().setZeroPower(MotorMode.BRAKE)` |
| `setMaximumAttainableSpeeds(...)`, `setMaximumAllowableSpeeds(...)` | `SwerveDriveConfig.withMaximumChassisSpeed(...)`, `withMaximumModuleSpeed(...)` |
| `getMaximumChassisVelocity()`, `getMaximumChassisAngularVelocity()` | `getConfig().getMaximumChassisLinearVelocity()`, `getMaximumChassisAngularVelocity()`; both `Optional` |
| `setHeadingCorrection(...)`, `getSwerveController()` | Removed. Use `SwerveInputStream` heading control and `SwerveDriveConfig.withRotationController(...)`. |
| `setChassisDiscretization(...)` | Set by the parser to 20 ms |
| `setAngularVelocityCompensation(...)` | `SwerveDriveConfig.withGyroAngularVelocityScaleFactor(double)` |
| `setCosineCompensator(boolean)` | Per module: `module.getConfig().withCosineCompensation(boolean)` |
| `setModuleStateOptimization(boolean)` | `SwerveDriveConfig.withModuleStateOptimization(boolean)` |
| `replaceSwerveModuleFeedforward(...)` | Per module: `module.getDriveMotorController().setFeedforward(kS, kV, kA, kG)` |
| `pushOffsetsToEncoders()`, `restoreInternalOffset()`, `useExternalFeedbackSensor()`, `useInternalFeedbackSensor()` | Removed. The parser uses an absolute encoder attached to the angle motor controller as its feedback. |
| `setModuleEncoderAutoSynchronize(...)`, `synchronizeModuleEncoders()` | Removed. `module.seedAzimuthEncoder()` reseeds a module's angle from its absolute encoder. |
| `setAutoCenteringModules(boolean)` | Removed |
| `resetDriveEncoders()`, `setAngleMotorConversionFactor(...)`, `setDriveMotorConversionFactor(...)`, `updateCacheValidityPeriods(...)`, `setOdometryPeriod(...)`, `stopOdometryThread()` | Removed |
| `getMapleSimDrive()`, `getSimulationDriveTrainPose()` | `getSimPose()` |

New in YAMS: `driveToPose(Pose2d)` and `drive(Supplier<ChassisVelocities>)` commands,
`driveToPoseSetpoint(...)`, `isNear(...)`, `setAzimuthAngles(Rotation2d)`, `stop()`, and
`SwerveDriveConfig.withAntiTipping(...)`.

> [!WARNING]
> `SwerveDrive.drive(Supplier<ChassisVelocities>)` drives **robot-relative**, but
> `SwerveInputStream.get()` gives **field-relative** speeds. Drive a stream with
> `run(() -> drive.setFieldRelativeChassisSpeeds(stream.get()))`, as the examples do.

### SwerveModule

| 2026.4.1 | 2027 |
| --- | --- |
| `getState()`, `getPosition()` | Same |
| `setDesiredState(...)` | `setSwerveModuleState(SwerveModuleVelocity)` |
| `setAngle(double)` | `setAzimuthAngle(Rotation2d)` |
| `getDriveMotor()`, `getAngleMotor()` | `getDriveMotorController()`, `getAzimuthMotorController()` |
| `getRawAbsolutePosition()` | `getRawAbsoluteEncoderAngle()` |
| `getAbsolutePosition()` | `getConfig().getAbsoluteEncoderAngle()` |
| `queueSynchronizeEncoders()` | `seedAzimuthEncoder()` |
| `moduleNumber`, `configuration` | `getName()`, `getConfig()` |
| `setAntiJitter(...)` | `getConfig().withVelocityDeadband(LinearVelocity)` |

### SwerveInputStream

`SwerveInputStream` moved to YAMS. Most methods gained a `with` prefix:

| 2026.4.1 | 2027 |
| --- | --- |
| `of(drive, x, y)` | Same. Also new: `of(drive, x, y, rotation)`. |
| `copy()` | `clone()` |
| `robotRelative(...)` | `withRobotRelative(...)` |
| `allianceRelativeControl(...)` | `withAllianceRelativeControl(...)` |
| `translationHeadingOffset(...)` | `withTranslationHeadingOffset(Rotation2d[, BooleanSupplier])` |
| `cubeRotationControllerAxis(...)`, `cubeTranslationControllerAxis(...)` | `withCubeRotationControllerAxis(...)`, `withCubeTranslationControllerAxis(...)` |
| `withControllerRotationAxis(...)`, `withControllerHeadingAxis(...)` | Same |
| `deadband(double)` | Same, or `withDeadband(double)` |
| `scaleTranslation(...)`, `scaleRotation(...)` | `withScaleTranslation(...)`, `withScaleRotation(...)` |
| `headingWhile(...)` | `withHeadingControl(BooleanSupplier)` |
| `aim(...)` and `aimWhile(...)` | `withAim(Supplier<Pose2d>, BooleanSupplier)` |
| `aimLookahead(...)`, `aimFeedforward(kS, kV, kA)`, `aimHeadingOffset(...)` | `withAimLookahead(...)`, `withAimFeedforward(SimpleMotorFeedforward)`, `withAimHeadingOffset(...)` |
| `aimLock(Angle)` | Same |
| `translationOnlyWhile(...)` | `withTranslationOnly(BooleanSupplier)` |
| `translationOnlyHeading(...)` | Removed. The heading is captured when translation-only mode starts. |
| `driveToPose(...)`, `driveToPoseEnabled(...)`, `atTargetPose(...)` | Removed. Use the `SwerveDrive.driveToPose(Pose2d)` command and `isNear(...)`. |

Heading, aim and translation-only control use the rotation PID from
`SwerveDriveConfig.withRotationController(...)`, which replaces `controllerproperties.json`'s
`heading` PID. They throw if it isn't set. There's no replacement for the old `SwerveController`'s
slew-rate limiters.

### Commands v2 or v3

Pick the vendordep for the command framework your robot uses:

| | Commands v2 | Commands v3 |
| --- | --- | --- |
| Vendordep | `yagsl_commands2.json` | `yagsl_commands3.json` |
| Packages | `swervelib.commands2`, `yams.commands2` | `swervelib.commands3`, `yams.commands3` |
| Drive owner | `SwerveDriveConfig.withSubsystem(this)` | `SwerveDriveConfig.withMechanism(this)` |
| `updateTelemetry()` | The subsystem's `periodic()` | `Robot.robotPeriodic()` |
| PathPlanner | Supported | Not supported; use `driveToPose(...)` |

The [examples](examples) show both: `examples/commands2` and `examples/commands3`.

## Behavior changes

- **Closed loop only.** Open-loop driving is gone: both motors always run closed loop.
- **No odometry thread.** The pose only updates in `updateTelemetry()`; call it every loop.
- **Maximum speed.** YAGSL works out the maximum module speed from the drive motor's free speed and
  gearing. It doesn't set a maximum chassis speed. Without `withMaximumChassisSpeed`,
  `SwerveInputStream` scales the joysticks to 4 m/s and 1 rotation per second.
- **Current limits.** `statorCurrentLimit` is a stator limit on every motor controller. In 2026.4.1
  `currentLimit` was a supply limit on a TalonFX or TalonFXS and the smart current limit on a SPARK.
- **No ramp and no voltage compensation.** The old 0.25 s ramp and 12 V compensation are gone.
- **Drive feedforward.** When the drive's `v` is `0`, kV is 12 V divided by the wheel's speed at
  the motor's free speed. In 2026.4.1 it was 12 V divided by the maximum speed you passed in.
  The angle motor gets no automatic feedforward.
- **Cosine compensation** is always on. Robot code can turn it off per module with
  `module.getConfig().withCosineCompensation(false)`.
- **Idle modes.** Drive motors coast and angle motors brake.
- **Absolute encoder feedback.** An absolute encoder attached to the angle motor controller, or a
  CANcoder with a TalonFX or TalonFXS angle motor, becomes the motor controller's feedback sensor.
  The exception is `analog_attached` or `analog5v_attached` on a SPARK, which YAMS can't use as
  feedback. Other encoders are read by the module.
- **Gyro.** YAMS applies `gyroAxis` and `gyroInvert` to the gyro's full 3D attitude. Check that the
  heading increases counterclockwise before driving.
- **Telemetry.** The drive publishes to NetworkTables under `Mechanisms/<telemetry name>` and logs
  to a DataLog. The verbosity levels are `LOW`, `MID` and `HIGH`, replacing `NONE`, `LOW`, `INFO`,
  `POSE`, `HIGH` and `MACHINE`.
- **Simulation.** MapleSim is gone. YAMS simulates each motor with its vendor's simulation, and the
  examples add a simulated battery. `pidfproperties_sim.json` holds simulation gains.
- **Validation.** The old warnings for CAN IDs above 40 and for a module at the robot's center are
  gone.

## Not available on 2027_alpha7

These have no WPILib `2027_alpha7` release yet:

| Library or hardware | Effect |
| --- | --- |
| Phoenix 5 | No Pigeon (gen 1), Talon SRX, or SRX Mag Encoder through a Talon SRX |
| NavX2 library | No NavX or NavX2 gyro. A NavX3 works through StudicaLib. |
| AndyMark library | `andymarkhexbore_can` doesn't work. The DIO, analog and attached hex bore types do. |
| MapleSim | No physics simulation; YAMS simulates the motors instead |
| PhotonLib, Limelight (YALL) | No vision library. The Commands v2 example's vision subsystems are parked in `disabled-vision/`. |

> [!NOTE]
> ReduxLib 2027 only opens devices through Linux SocketCAN, so a Canandgyro crashes the robot
> program in desktop simulation on Windows and macOS. Use a different gyro in simulation there, as
> the examples do with a Pigeon 2.
