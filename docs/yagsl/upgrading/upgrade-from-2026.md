---
description: >-
  Move a 2026 YAGSL robot project to YAGSL 2027 and the SystemCore, starting
  with the hardware that has no 2027 support
---

# Upgrade a 2026 project to YAGSL 2027

This page takes a robot project on YAGSL `2026.4.1`, the last release of the original library for WPILib `2026.2.1`, to YAGSL 2027 on WPILib `2027_alpha7` and the SystemCore.

YAGSL was rewritten for 2027. It used to be a complete swerve library. It is now a JSON parser that builds a [YAMS](https://yams.yassrobotics.com) `SwerveDrive`, and YAMS does the driving, odometry, simulation and telemetry. Your hardware may need replacing, and your JSON files, vendordeps, build files and most of your swerve code change. Work through it in this order:

1. [Hardware](upgrade-from-2026.md#hardware): check every device on your robot has 2027 support.
2. [Configuration](upgrade-from-2026.md#configuration): your build files, vendordeps and `swerve/` directory.
3. [Usage](upgrade-from-2026.md#usage): your robot code, and how the drive behaves.

{% hint style="success" %}
Regenerating your configuration with the [config generator](https://config.yagsl.com) is faster and safer than porting it by hand. Open your old configuration in the [2026 config generator](https://config.yagsl.com/old) to read off your CAN IDs, offsets, gearing and module locations, then enter them in the new one.
{% endhint %}

## Hardware

Start here: a device without 2027 support has to be replaced before anything else will work.

### The SystemCore

YAGSL 2027 runs on the SystemCore. It has five CAN buses, `can_s0` to `can_s4`, so note which bus each device is wired to. You will need it for every device's `canbus` in [CAN buses](upgrade-from-2026.md#can-buses).

### Gyros

| 2026 gyro                                     | 2027                                                                   |
| --------------------------------------------- | ---------------------------------------------------------------------- |
| Pigeon 2.0                                    | Supported (`pigeon2_can`)                                              |
| Redux Canandgyro                              | Supported (`canandgyro_can`)                                           |
| NavX3                                         | Supported, on CAN (`navx3_can`) or a SystemCore USB port (`navx3_usb`) |
| Pigeon (gen 1), on CAN or through a Talon SRX | **Not supported.** Phoenix 5 has no 2027 release.                      |
| NavX and NavX2, on any port                   | **Not supported.** There is no NavX2 library for WPILib 2027 yet.      |
| ADIS16448, ADIS16470, ADXRS450, analog gyros  | **Not supported.**                                                     |

New in 2027: the IMU built into the SystemCore (`systemcore_internal`). To keep a gyro YAGSL doesn't support, use the `custom` type and read it in your own code. See [Use a custom gyro](../tune-and-extend/use-a-custom-gyro.md).

{% hint style="warning" %}
ReduxLib 2027 only opens devices through Linux SocketCAN, so a Canandgyro crashes the robot program in desktop simulation on Windows and macOS. Use a different gyro in simulation there, as the examples do with a Pigeon 2.
{% endhint %}

### Motor controllers

Only brushless motors are supported, on a TalonFX, TalonFXS, SPARK MAX, SPARK Flex or Thrifty Nova.

| 2026 motor controller                                                | 2027                                              |
| -------------------------------------------------------------------- | ------------------------------------------------- |
| TalonFX (Kraken X44, Kraken X60, Falcon 500)                         | Supported                                         |
| TalonFXS, SPARK MAX, SPARK Flex, Thrifty Nova with a brushless motor | Supported                                         |
| Talon SRX                                                            | **Not supported.** Phoenix 5 has no 2027 release. |
| SPARK MAX with a brushed motor                                       | **Not supported.**                                |

### Absolute encoders

Every module now needs an absolute encoder. The 2026 `none` encoder is gone.

| 2026 absolute encoder                                     | 2027                                                                                                      |
| --------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| CANcoder, Canandmag on CAN                                | Supported                                                                                                 |
| An encoder in a SPARK MAX, SPARK Flex or Nova's data port | Supported, when that controller is the angle motor. A TalonFX or TalonFXS can't read an attached encoder. |
| A duty cycle or analog encoder in a robot controller port | Supported, in a SystemCore SmartIO port                                                                   |
| An SRX Mag Encoder through a Talon SRX                    | **Not supported.** Phoenix 5 has no 2027 release.                                                         |
| An analog encoder on a TalonFXS                           | **Not supported.**                                                                                        |
| An AndyMark hex bore encoder on CAN                       | **Not yet.** The AndyMark library has no 2027 release. Its DIO, analog and attached types work.           |

New in 2027: the Spline encoder on CAN, and Thrifty, SRX Mag and duty cycle encoders attached to an angle motor controller's data port.

### Libraries without a 2027 release

These have no WPILib `2027_alpha7` release yet, so some features go with them:

| Library                     | What it affects                                                                       |
| --------------------------- | ------------------------------------------------------------------------------------- |
| Phoenix 5                   | The Pigeon (gen 1), the Talon SRX, and an SRX Mag Encoder through a Talon SRX         |
| NavX2 library               | Every NavX and NavX2. A NavX3 works through StudicaLib.                               |
| AndyMark library            | `andymarkhexbore_can`                                                                 |
| MapleSim                    | Physics simulation of the whole drive. YAMS simulates each motor instead.             |
| PhotonLib, Limelight (YALL) | Vision. The Commands v2 example's vision subsystems are parked in `disabled-vision/`. |

## Configuration

### WPILib, Java and the build

|                  | 2026                      | 2027                                         |
| ---------------- | ------------------------- | -------------------------------------------- |
| WPILib           | `2026.2.1`                | `2027_alpha7` (GradleRIO `2027.0.0-alpha-7`) |
| Robot controller | roboRIO                   | SystemCore                                   |
| Java             | 17                        | 25                                           |
| Gradle           | 8.10.2                    | 9.4.1                                        |
| GradleRIO plugin | `edu.wpi.first.GradleRIO` | `org.wpilib.GradleRIO`                       |
| WPILib packages  | `edu.wpi.first.*`         | `org.wpilib.*`                               |
| Deploy directory | `/home/lvuser/deploy`     | `/home/systemcore/deploy`                    |

The easiest way to get a 2027 `build.gradle`, `settings.gradle` and `.wpilib/wpilib_preferences.json` is to create a new project with WPILib `2027_alpha7`, or start from `example-commands2.zip` or `example-commands3.zip` on the [releases page](https://github.com/Yet-Another-Software-Suite/YAGSL/releases). The 2027 `build.gradle` no longer needs `org.dyn4j:dyn4j`.

### Vendordeps

|                 | 2026                                                                           | 2027                                                                                                       |
| --------------- | ------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------- |
| YAGSL vendordep | `yagsl.json`                                                                   | `yagsl_commands2.json` **or** `yagsl_commands3.json`. They conflict, so install one.                       |
| URL             | `https://yet-another-software-suite.github.io/YAGSL/yagsl.json`                | `https://cdn.yassrobotics.com/yagsl_commands2.json` or `https://cdn.yassrobotics.com/yagsl_commands3.json` |
| Requires        | ReduxLib, REVLib and ThriftyLib, and Phoenix 5, Phoenix 6 and Studica unlisted | Only the matching YAMS vendordep                                                                           |

{% hint style="warning" %}
Don't update the old `yagsl.json` with **Check for updates**. It updates to a 2026 build, not to 2027. Delete it and install the 2027 vendordep.
{% endhint %}

Hardware vendor libraries are optional now. YAGSL loads a vendor's classes only when your configuration uses that vendor's devices, so install the 2027 vendordeps for the hardware you have, and delete Phoenix 5. A missing one fails when the device is created, with a message naming the library. [Vendordeps and offline install](../reference/vendordep-installation.md) lists each vendor's 2027 vendordep and the offline zip.

### The `swerve/` directory

The directory keeps its layout:

```
swerve/
├── swervedrive.json
├── frontleft.json, frontright.json, backleft.json, backright.json
└── modules/
    ├── physicalproperties.json
    ├── pidfproperties.json
    └── pidfproperties_sim.json   (new, optional)
```

* **Delete `controllerproperties.json`.** It is no longer read. Heading control moved to robot code; see [SwerveInputStream](upgrade-from-2026.md#swerveinputstream).
* **`modules/pidfproperties_sim.json` is new and optional.** In simulation it replaces `pidfproperties.json`, so you can keep separate gains for the simulator.

{% hint style="danger" %}
The parser ignores fields it doesn't know. An old file still loads, but every renamed field is silently dropped, and a missing `gyro`, `absoluteEncoder`, `inverted` or `location` fails at startup with a `NullPointerException`. Don't assume an old file works because it parses.
{% endhint %}

#### `swervedrive.json`

| 2026                      | 2027                                | Notes                                                                                                           |
| ------------------------- | ----------------------------------- | --------------------------------------------------------------------------------------------------------------- |
| `imu: {type, id, canbus}` | `gyro: {type, id, canbus, channel}` | Renamed, with new [type strings](upgrade-from-2026.md#device-types). `channel` is the USB port for `navx3_usb`. |
| `invertedIMU`             | `gyroInvert`                        | Renamed. Default `false`.                                                                                       |
|                           | `gyroAxis`                          | New. `"yaw"` (default), `"pitch"` or `"roll"`: the gyro axis used as the robot's heading.                       |
| `modules`                 | `modules`                           | Unchanged. Each module's name is its file name without `.json`.                                                 |

```json
{
  "gyro": { "type": "pigeon2_can", "id": 13, "canbus": "" },
  "gyroAxis": "yaw",
  "gyroInvert": false,
  "modules": ["frontleft.json", "frontright.json", "backleft.json", "backright.json"]
}
```

#### Module files

| 2026                                                                                    | 2027                                                          | Notes                                                                                                                                                                    |
| --------------------------------------------------------------------------------------- | ------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `drive`, `angle`: `{type, id, canbus}`                                                  | Same                                                          | New [type strings](upgrade-from-2026.md#device-types).                                                                                                                   |
| `encoder: {type, id, canbus}`                                                           | `absoluteEncoder: {type, id, channel, canbus}`                | Renamed, and required. New [type strings](upgrade-from-2026.md#device-types). DIO and analog encoders take their port in `channel`, not `id`.                            |
| `absoluteEncoderOffset`                                                                 | Same                                                          | Degrees, subtracted from the encoder reading, as before.                                                                                                                 |
| `absoluteEncoderInverted`                                                               | Same                                                          | Default `false`.                                                                                                                                                         |
|                                                                                         | `absoluteEncoderGearRatio`                                    | New. Default `1`. A reduction between the absolute encoder and the module, used when the module reads the encoder itself rather than through the angle motor controller. |
| `conversionFactors: {drive: {gearRatio, diameter, factor}, angle: {gearRatio, factor}}` | `gearing: {drive: {gearRatio, diameter}, angle: {gearRatio}}` | Renamed, and `factor` is gone. Optional: it overrides `physicalproperties.json` for this module.                                                                         |
| `inverted: {drive, angle}`                                                              | Same                                                          |                                                                                                                                                                          |
| `location: {front, left}`                                                               | Same                                                          | Inches from the robot's center.                                                                                                                                          |
| `useCosineCompensator`                                                                  | Removed                                                       | Cosine compensation is always on; see [Behavior changes](upgrade-from-2026.md#behavior-changes).                                                                         |

{% hint style="warning" %}
A module's `gearing.drive` replaces the shared drive gearing when its `gearRatio` isn't `0`. A module that sets only `gearRatio` keeps the shared wheel diameter, as the old `conversionFactors` override did.
{% endhint %}

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

#### `modules/physicalproperties.json`

| 2026                                                                                    | 2027                                                          | Notes                                                                                          |
| --------------------------------------------------------------------------------------- | ------------------------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| `conversionFactors: {drive: {gearRatio, diameter, factor}, angle: {gearRatio, factor}}` | `gearing: {drive: {gearRatio, diameter}, angle: {gearRatio}}` | Renamed. `factor` is gone: give the gear ratio (X:1) and the wheel diameter in inches.         |
| `conversionFactor` (deprecated)                                                         | Removed                                                       |                                                                                                |
| `currentLimit: {drive, angle}`                                                          | `statorCurrentLimit: {drive, angle}`                          | Renamed. Default `40` and `20` A, as before, but now a stator limit on every motor controller. |
| `rampRate: {drive, angle}`                                                              | Removed                                                       | No ramp is applied. The old default was 0.25 s.                                                |
| `optimalVoltage`                                                                        | Removed                                                       | No voltage compensation is applied. The old default was 12 V.                                  |
| `friction`, `steerRotationalInertia`, `robotMass`, `wheelGripCoefficientOfFriction`     | Removed                                                       | They configured the MapleSim simulation, which is gone.                                        |

```json
{
  "gearing": {
    "drive": { "gearRatio": 8.14, "diameter": 4 },
    "angle": { "gearRatio": 12.8 }
  },
  "statorCurrentLimit": { "drive": 40, "angle": 20 }
}
```

#### `modules/pidfproperties.json`

| 2026                 | 2027          | Notes                                                                                                                                                       |
| -------------------- | ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `p`, `i`, `d`        | Same          | Different units; see below.                                                                                                                                 |
| `f`                  | Removed       | It was not a general feedforward: SPARKs and the Nova ignored it, and the TalonFX and TalonFXS used it as kS.                                               |
| `iz`                 | Removed       |                                                                                                                                                             |
| `output: {min, max}` | Removed       |                                                                                                                                                             |
|                      | `s`, `v`, `a` | New. A `SimpleMotorFeedforward` for each motor, default `0`. When the drive's `v` is `0`, YAGSL works it out from the drive motor's free speed and gearing. |

```json
{
  "drive": { "p": 0, "i": 0, "d": 0, "s": 0, "v": 0, "a": 0 },
  "angle": { "p": 0, "i": 0, "d": 0, "s": 0, "v": 0, "a": 0 }
}
```

{% hint style="warning" %}
Retune your gains. The old ones were in each vendor's own units, such as duty cycle per degree on a SPARK. The closed loop now outputs volts, from an error in rotations, or meters for the drive motor. See [Tune PIDF gains](../tune-and-extend/tune-pidf-gains.md).
{% endhint %}

### Device types

Every `type` is now `<device>_<connection>`, or `<controller>_<motor>` for a motor controller. [Device Type Strings](../reference/json-schema/device-types.md) lists them all.

#### Gyros

| 2026         | 2027                                                                                     |
| ------------ | ---------------------------------------------------------------------------------------- |
| `pigeon2`    | `pigeon2_can`                                                                            |
| `canandgyro` | `canandgyro_can`                                                                         |
| `navx3`      | `navx3_can`. Its `canbus` is now used.                                                   |
|              | `navx3_usb`: a NavX3 on a SystemCore USB port, with the port (0 to 3) in `channel`       |
|              | `systemcore_internal`: the IMU built into the SystemCore                                 |
|              | `custom`: YAGSL builds no gyro, and you supply it with `SwerveDriveConfig.withGyro(...)` |

#### Motor controllers

Controllers are `talonfx`, `talonfxs`, `sparkmax`, `sparkflex` and `nova`. Motors are `neo`, `neo2`, `neo550`, `vortex`, `minion`, `pulsar`, `krakenx44`, `krakenx60`, `falcon` and `falcon500`. A TalonFX only takes `krakenx44`, `krakenx60`, `falcon` and `falcon500`.

| 2026                                                                    | 2027                                                                    |
| ----------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| `neo`, `sparkmax`, `sparkmax_neo`                                       | `sparkmax_neo`                                                          |
| `neo550`, `sparkmax_neo550`                                             | `sparkmax_neo550`                                                       |
| `sparkmax_vortex`, `sparkmax_minion`                                    | Same                                                                    |
| `sparkflex`, `sparkflex_vortex`                                         | `sparkflex_vortex`                                                      |
| `sparkflex_neo`, `sparkflex_neo550`, `sparkflex_minion`                 | Same                                                                    |
| `falcon`, `falcon500`                                                   | `talonfx_falcon`                                                        |
| `talonfx`, `krakenx60`                                                  | `talonfx_krakenx60`                                                     |
| `krakenx44`                                                             | `talonfx_krakenx44`                                                     |
| `falcon500foc`, `krakenx60foc`                                          | `talonfx_falcon` or `talonfx_krakenx60`                                 |
| `talonfxs_neo`, `talonfxs_neo550`, `talonfxs_vortex`, `talonfxs_minion` | Same                                                                    |
| `nova_neo`, `nova_neo550`, `nova_vortex`, `nova_minion`                 | Same                                                                    |
|                                                                         | `sparkmax_pulsar`, `sparkflex_pulsar`, `talonfxs_pulsar`, `nova_pulsar` |

#### Absolute encoders

`_attached` encoders plug into the angle motor controller's data port, and `_dio` and `_analog` encoders into a SystemCore SmartIO port, given in `channel`.

| 2026                                                                       | 2027                                                                                              |
| -------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| `cancoder`                                                                 | `cancoder_can`                                                                                    |
| `canandmag_can`, `canandcoder_can`                                         | `canandmag_can`                                                                                   |
| `integrated`, `attached`, `sparkflex_integrated`, `sparkflex_attached`     | `revthroughbore_attached`, or the attached encoder's own type below                               |
| `canandmag`, `canandcoder`, `sparkflex_canandmag`, `sparkflex_canandcoder` | `canandmag_attached`                                                                              |
| `sparkmax_analog`                                                          | `analog_attached`                                                                                 |
| `sparkmax_analog5v`                                                        | `analog5v_attached`                                                                               |
| `thrifty_nova_rev`                                                         | `revthroughbore_attached`                                                                         |
| `thrifty_nova_redux`                                                       | `canandmag_attached`                                                                              |
| `thrifty_nova_srx_mag`                                                     | `srxmag_attached`                                                                                 |
| `throughbore`, `rev_hex`, `dutycycle` (DIO, port in `id`)                  | `revthroughbore_dio`, port in `channel`                                                           |
| `am_mag` (DIO)                                                             | `andymarkhexbore_dio`                                                                             |
| `ctre_mag` (DIO)                                                           | `revthroughbore_dio`: every `_dio` type reads a duty cycle encoder                                |
| `thrifty`, `ma3`, `analog` (analog input, port in `id`)                    | `thrifty_analog`, `srxmag_analog` or `andymarkhexbore_analog`, port in `channel`                  |
|                                                                            | `splineencoder_can`, `thrifty_attached`, `srxmag_attached`, `dutycycle_attached`, `canandmag_dio` |

### CAN buses

In 2026 only CTRE devices read `canbus`: empty or `null` was the robot controller's bus, and anything else was a CANivore's name. Every other vendor ignored it. Now every device reads it the same way:

| `canbus`                                            | Bus                                                                                |
| --------------------------------------------------- | ---------------------------------------------------------------------------------- |
| `""`, `null` or missing                             | SystemCore `can_s0`                                                                |
| `"0"` to `"4"`                                      | SystemCore `can_s0` to `can_s4`                                                    |
| `"5"` to `"24"`                                     | Motioncore `can_d0` to `can_d19`                                                   |
| `"CAN_S0"` to `"CAN_S4"`, `"CAN_D0"` to `"CAN_D19"` | The same buses by name                                                             |
| Any other number                                    | An error, for every vendor                                                         |
| Any other string                                    | Passed to the vendor: a CANivore's name for CTRE devices, or a ReduxLib bus string |

* CTRE devices with an empty `canbus` are on `can_s0`, not Phoenix 6's SystemCore default `can_s2`. If yours are wired to `can_s2`, set `"canbus": "2"`.
* REV, ThriftyBot and Studica devices only take a bus number or name, and reject anything else.
* A CTRE device on a CANivore keeps the CANivore's name.

See [Put devices on a different CAN bus](../tune-and-extend/choose-a-can-bus.md).

## Usage

### Packages

| 2026                                                                                                                                                                                                  | 2027                                                                                   |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| `swervelib.parser.SwerveParser`                                                                                                                                                                       | `swervelib.commands2.SwerveParser` or `swervelib.commands3.SwerveParser`               |
| `swervelib.parser.*`                                                                                                                                                                                  | `swervelib.core.parser.*`                                                              |
| `swervelib.SwerveDrive`                                                                                                                                                                               | `yams.commands2.swerve.SwerveDrive` or `yams.commands3.swerve.SwerveDrive`             |
| `swervelib.SwerveInputStream`                                                                                                                                                                         | `yams.commands2.swerve.SwerveInputStream` or `yams.commands3.swerve.SwerveInputStream` |
| `swervelib.SwerveModule`                                                                                                                                                                              | `yams.core.mechanisms.swerve.SwerveModule`                                             |
| `swervelib.telemetry.SwerveDriveTelemetry.verbosity`                                                                                                                                                  | `SwerveDriveConfig.withTelemetry(name, TelemetryVerbosity)`                            |
| `swervelib.SwerveController`, `swervelib.SwerveDriveTest`, `swervelib.math.*`, `swervelib.imu.*`, `swervelib.encoders.*`, `swervelib.motors.*`, `swervelib.simulation.*`, `swervelib.telemetry.Alert` | Removed                                                                                |

{% hint style="info" %}
`swervelib.core.telemetry.SwerveDriveTelemetry` still compiles, but nothing reads it: setting its `verbosity` does nothing. Set the telemetry on the `SwerveDriveConfig`.
{% endhint %}

WPILib 2027 renamed the types swerve code uses most:

| WPILib 2026                                                                              | WPILib 2027                                                                    |
| ---------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------ |
| `ChassisSpeeds` (`vxMetersPerSecond`, `vyMetersPerSecond`, `omegaRadiansPerSecond`)      | `ChassisVelocities` (`vx`, `vy`, `omega`)                                      |
| `ChassisSpeeds.fromFieldRelativeSpeeds(...)`                                             | `chassisVelocities.toRobotRelative(rotation)`                                  |
| `SwerveModuleState` (`speedMetersPerSecond`, `angle`)                                    | `SwerveModuleVelocity` (`velocity`, `angle`)                                   |
| `SwerveModulePosition.distanceMeters`                                                    | `SwerveModulePosition.distance`                                                |
| `SwerveDriveKinematics.toSwerveModuleStates`, `toChassisSpeeds`, `desaturateWheelSpeeds` | `toSwerveModuleVelocities`, `toChassisVelocities`, `desaturateWheelVelocities` |
| `Pose2d.kZero`, `Rotation2d.kZero`                                                       | `Pose2d.ZERO`, `Rotation2d.ZERO`                                               |
| `Translation2d.getAngle()` returns `Rotation2d`                                          | Returns `Optional<Rotation2d>`                                                 |
| `DriverStation.getAlliance()`, `DriverStation.Alliance.Red`                              | `MatchState.getAlliance()`, `Alliance.RED`                                     |
| `edu.wpi.first.wpilibj2.command.*`                                                       | `org.wpilib.command2.*`                                                        |

### Creating the drive

The 2026 drive was made in one call:

```java
SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
SwerveDrive drive = new SwerveParser(directory).createSwerveDrive(maximumSpeed, startingPose);
```

Now you describe the drive in a `SwerveDriveConfig`, and the parser fills in the rest:

{% tabs %}
{% tab title="Commands v2" %}
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
{% endtab %}

{% tab title="Commands v3" %}
```java
import swervelib.commands3.SwerveParser;
import yams.commands3.config.SwerveDriveConfig;
import yams.commands3.swerve.SwerveDrive;
import yams.core.telemetry.SwerveDriveTelemetryConfig;
import yams.core.telemetry.enums.TelemetryVerbosity;

SwerveDriveConfig cfg = new SwerveDriveConfig()
    .withMechanism(this)
    .withStartingPose(startingPose)
    .withMaximumChassisSpeed(MetersPerSecond.of(4.5), RotationsPerSecond.of(1.5))
    .withTranslationController(new PIDController(5, 0, 0))
    .withRotationController(new PIDController(5, 0, 0))
    .withTelemetry("swerve", new SwerveDriveTelemetryConfig(TelemetryVerbosity.HIGH));

SwerveParser.parse(directory);
SwerveDrive drive = SwerveParser.createSwerveDrive(cfg);
```
{% endtab %}
{% endtabs %}

* `SwerveParser.parse(dir)` and `createSwerveDrive(cfg)` are static, and can't be chained.
* `SwerveParser.createSwerveDriveDevices(cfg)` also returns the vendor devices YAGSL created: the gyro, and each module's drive, angle and absolute encoder. See [Access raw hardware devices](../tune-and-extend/access-raw-hardware-devices.md).
* The old `maximumSpeed` and conversion factor arguments are gone. Set the maximum chassis speed with `withMaximumChassisSpeed`; see [Behavior changes](upgrade-from-2026.md#behavior-changes).
* The parser sets the modules, the maximum module speed, a 20 ms discretization time, and the gyro and its axis and inversion on your `SwerveDriveConfig`. Don't set those yourself.

### Updating every loop

The 2026 drive updated its odometry on its own thread. Now the pose only updates when you call `drive.updateTelemetry()`, so call it every loop, and `drive.simIterate()` in simulation:

{% tabs %}
{% tab title="Commands v2" %}
```java
// In the drive subsystem
@Override
public void periodic() {
  drive.updateTelemetry();
}

@Override
public void simulationPeriodic() {
  drive.simIterate();
}
```
{% endtab %}

{% tab title="Commands v3" %}
A Commands v3 `Mechanism` has no `periodic()`. Call `updateTelemetry()` from `Robot.robotPeriodic()`, and `simIterate()` from `Robot.simulationPeriodic()`.
{% endtab %}
{% endtabs %}

### SwerveDrive

| 2026                                                                                                                                                                                    | 2027                                                                                                   |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| `drive(ChassisSpeeds)`, `setChassisSpeeds(ChassisSpeeds)`                                                                                                                               | `setRobotRelativeChassisSpeeds(ChassisVelocities)`                                                     |
| `driveFieldOriented(ChassisSpeeds)`                                                                                                                                                     | `setFieldRelativeChassisSpeeds(ChassisVelocities)`                                                     |
| `drive(Translation2d, double, boolean fieldRelative, boolean isOpenLoop)`                                                                                                               | Build a `ChassisVelocities` and call one of the two above. Open loop is gone.                          |
| `drive(ChassisSpeeds, Translation2d centerOfRotation)`                                                                                                                                  | `SwerveDriveConfig.withCenterOfRotation(...)`                                                          |
| `driveFieldOrientedAndRobotOriented(...)`                                                                                                                                               | Removed. Add the two with `ChassisVelocities.toRobotRelative(...)` and `plus(...)`.                    |
| `drive(ChassisSpeeds, SwerveModuleState[], Force[])` (PathPlanner)                                                                                                                      | `setRobotRelativeChassisSpeeds(ChassisVelocities, Force[])`                                            |
| `setModuleStates(SwerveModuleState[], boolean)`                                                                                                                                         | `setSwerveModuleStates(SwerveModuleVelocity[])`                                                        |
| `getPose()`, `resetOdometry(Pose2d)`, `zeroGyro()`                                                                                                                                      | Same. `resetOdometry` also resets the gyro to the pose's heading.                                      |
| `getOdometryHeading()`                                                                                                                                                                  | `getPose().getRotation()`                                                                              |
| `getYaw()`, `getPitch()`, `getRoll()`, `getGyroRotation3d()`                                                                                                                            | `getGyroRotation3d()`                                                                                  |
| `getGyro()`                                                                                                                                                                             | `createSwerveDriveDevices(cfg).gyro()`                                                                 |
| `setGyro(Rotation3d)`                                                                                                                                                                   | `resetOdometry(Pose2d)`                                                                                |
| `setGyroOffset(Rotation3d)`                                                                                                                                                             | `resetOdometry(Pose2d)`: the pose estimator keeps the offset between the gyro and the pose             |
| `addVisionMeasurement(...)`, `setVisionMeasurementStdDevs(...)`                                                                                                                         | Same                                                                                                   |
| `getRobotVelocity()`, `getFieldVelocity()`                                                                                                                                              | `getRobotRelativeSpeed()`, `getFieldRelativeSpeed()`                                                   |
| `getStates()`                                                                                                                                                                           | `getModuleStates()`. Also new: `getDesiredModuleStates()`, `getDesiredChassisSpeeds()`.                |
| `getModulePositions()`, `getModules()`, `lockPose()`                                                                                                                                    | Same                                                                                                   |
| `getModuleMap()`                                                                                                                                                                        | `getModule(String name)`, which returns an `Optional`                                                  |
| `kinematics`                                                                                                                                                                            | `getKinematics()`                                                                                      |
| `field`, `postTrajectory(Trajectory)`                                                                                                                                                   | `getField2d()`, then `getObject(name).setTrajectory(...)`                                              |
| `setMotorIdleMode(boolean)`                                                                                                                                                             | Per motor: `module.getDriveMotorController().setZeroPower(MotorMode.BRAKE)`                            |
| `setMaximumAttainableSpeeds(...)`, `setMaximumAllowableSpeeds(...)`                                                                                                                     | `SwerveDriveConfig.withMaximumChassisSpeed(...)`, `withMaximumModuleSpeed(...)`                        |
| `getMaximumChassisVelocity()`, `getMaximumChassisAngularVelocity()`                                                                                                                     | `getConfig().getMaximumChassisLinearVelocity()`, `getMaximumChassisAngularVelocity()`, both `Optional` |
| `setHeadingCorrection(...)`, `getSwerveController()`                                                                                                                                    | Removed. Use `SwerveInputStream` heading control and `SwerveDriveConfig.withRotationController(...)`.  |
| `setChassisDiscretization(...)`                                                                                                                                                         | Set by the parser to 20 ms                                                                             |
| `setAngularVelocityCompensation(...)`                                                                                                                                                   | `SwerveDriveConfig.withGyroAngularVelocityScaleFactor(double)`                                         |
| `setCosineCompensator(boolean)`                                                                                                                                                         | Per module: `module.getConfig().withCosineCompensation(boolean)`                                       |
| `setModuleStateOptimization(boolean)`                                                                                                                                                   | `SwerveDriveConfig.withModuleStateOptimization(boolean)`                                               |
| `replaceSwerveModuleFeedforward(...)`                                                                                                                                                   | Per module: `module.getDriveMotorController().setFeedforward(kS, kV, kA, kG)`                          |
| `pushOffsetsToEncoders()`, `restoreInternalOffset()`, `useExternalFeedbackSensor()`, `useInternalFeedbackSensor()`                                                                      | Removed. The parser uses an absolute encoder attached to the angle motor controller as its feedback.   |
| `setModuleEncoderAutoSynchronize(...)`, `synchronizeModuleEncoders()`                                                                                                                   | Removed. `module.seedAzimuthEncoder()` reseeds a module's angle from its absolute encoder.             |
| `setAutoCenteringModules(boolean)`                                                                                                                                                      | Removed                                                                                                |
| `resetDriveEncoders()`, `setAngleMotorConversionFactor(...)`, `setDriveMotorConversionFactor(...)`, `updateCacheValidityPeriods(...)`, `setOdometryPeriod(...)`, `stopOdometryThread()` | Removed                                                                                                |
| `getMapleSimDrive()`, `getSimulationDriveTrainPose()`                                                                                                                                   | `getSimPose()`                                                                                         |

New: the `driveToPose(Pose2d)` and `drive(Supplier<ChassisVelocities>)` commands, `driveToPoseSetpoint(...)`, `isNear(...)`, `setAzimuthAngles(Rotation2d)`, `stop()`, and `SwerveDriveConfig.withAntiTipping(...)`. See [Drive to a pose](../tune-and-extend/drive-to-a-pose.md).

{% hint style="warning" %}
`SwerveDrive.drive(Supplier<ChassisVelocities>)` drives **robot-relative**, but `SwerveInputStream.get()` gives **field-relative** speeds. Drive a stream with `run(() -> drive.setFieldRelativeChassisSpeeds(stream.get()))`, as the examples do.
{% endhint %}

### SwerveModule

| 2026                                 | 2027                                                       |
| ------------------------------------ | ---------------------------------------------------------- |
| `getState()`, `getPosition()`        | Same                                                       |
| `setDesiredState(...)`               | `setSwerveModuleState(SwerveModuleVelocity)`               |
| `setAngle(double)`                   | `setAzimuthAngle(Rotation2d)`                              |
| `getDriveMotor()`, `getAngleMotor()` | `getDriveMotorController()`, `getAzimuthMotorController()` |
| `getRawAbsolutePosition()`           | `getRawAbsoluteEncoderAngle()`                             |
| `getAbsolutePosition()`              | `getConfig().getAbsoluteEncoderAngle()`                    |
| `queueSynchronizeEncoders()`         | `seedAzimuthEncoder()`                                     |
| `moduleNumber`, `configuration`      | `getName()`, `getConfig()`                                 |
| `setAntiJitter(...)`                 | `getConfig().withVelocityDeadband(LinearVelocity)`         |

### SwerveInputStream

`SwerveInputStream` moved to YAMS, and most of its methods gained a `with` prefix:

| 2026                                                                       | 2027                                                                                               |
| -------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| `of(drive, x, y)`                                                          | Same. Also new: `of(drive, x, y, rotation)`.                                                       |
| `copy()`                                                                   | `clone()`                                                                                          |
| `robotRelative(...)`                                                       | `withRobotRelative(...)`                                                                           |
| `allianceRelativeControl(...)`                                             | `withAllianceRelativeControl(...)`                                                                 |
| `translationHeadingOffset(...)`                                            | `withTranslationHeadingOffset(Rotation2d[, BooleanSupplier])`                                      |
| `cubeRotationControllerAxis(...)`, `cubeTranslationControllerAxis(...)`    | `withCubeRotationControllerAxis(...)`, `withCubeTranslationControllerAxis(...)`                    |
| `withControllerRotationAxis(...)`, `withControllerHeadingAxis(...)`        | Same                                                                                               |
| `deadband(double)`                                                         | Same, or `withDeadband(double)`                                                                    |
| `scaleTranslation(...)`, `scaleRotation(...)`                              | `withScaleTranslation(...)`, `withScaleRotation(...)`                                              |
| `headingWhile(...)`                                                        | `withHeadingControl(BooleanSupplier)`                                                              |
| `aim(...)` and `aimWhile(...)`                                             | `withAim(Supplier<Pose2d>, BooleanSupplier)`                                                       |
| `aimLookahead(...)`, `aimFeedforward(kS, kV, kA)`, `aimHeadingOffset(...)` | `withAimLookahead(...)`, `withAimFeedforward(SimpleMotorFeedforward)`, `withAimHeadingOffset(...)` |
| `aimLock(Angle)`                                                           | Same                                                                                               |
| `translationOnlyWhile(...)`                                                | `withTranslationOnly(BooleanSupplier)`                                                             |
| `translationOnlyHeading(...)`                                              | Removed. The heading is captured when translation-only mode starts.                                |
| `driveToPose(...)`, `driveToPoseEnabled(...)`, `atTargetPose(...)`         | Removed. Use the `SwerveDrive.driveToPose(Pose2d)` command and `isNear(...)`.                      |

Heading, aim and translation-only control use the rotation PID from `SwerveDriveConfig.withRotationController(...)`, which replaces `controllerproperties.json`'s `heading` PID. They throw if it isn't set. There's no replacement for the old `SwerveController`'s slew rate limiters.

### Commands v2 or v3

|                     | Commands v2                             | Commands v3                             |
| ------------------- | --------------------------------------- | --------------------------------------- |
| Vendordep           | `yagsl_commands2.json`                  | `yagsl_commands3.json`                  |
| Packages            | `swervelib.commands2`, `yams.commands2` | `swervelib.commands3`, `yams.commands3` |
| Drive owner         | `SwerveDriveConfig.withSubsystem(this)` | `SwerveDriveConfig.withMechanism(this)` |
| `updateTelemetry()` | The subsystem's `periodic()`            | `Robot.robotPeriodic()`                 |
| PathPlanner         | Supported                               | Not supported; use `driveToPose(...)`   |

The [examples](../../../examples) show both.

### Behavior changes

* **Closed loop only.** Open loop driving is gone: both motors always run closed loop.
* **No odometry thread.** The pose only updates in `updateTelemetry()`; call it every loop.
* **Maximum speed.** YAGSL works out the maximum module speed from the drive motor's free speed and gearing. It doesn't set a maximum chassis speed. Without `withMaximumChassisSpeed`, `SwerveInputStream` scales the joysticks to 4 m/s and 1 rotation per second.
* **Current limits.** `statorCurrentLimit` is a stator limit on every motor controller. In 2026 `currentLimit` was a supply limit on a TalonFX or TalonFXS and the smart current limit on a SPARK.
* **No ramp and no voltage compensation.** The old 0.25 s ramp and 12 V compensation are gone.
* **Drive feedforward.** When the drive's `v` is `0`, kV is 12 V divided by the wheel's speed at the motor's free speed. In 2026 it was 12 V divided by the maximum speed you passed in. The angle motor gets no automatic feedforward.
* **Cosine compensation** is always on. Robot code can turn it off per module with `module.getConfig().withCosineCompensation(false)`.
* **Idle modes.** Drive motors coast and angle motors brake.
* **Absolute encoder feedback.** An absolute encoder attached to the angle motor controller, or a CANcoder with a TalonFX or TalonFXS angle motor, becomes the motor controller's feedback sensor. The exception is `analog_attached` or `analog5v_attached` on a SPARK, which YAMS can't use as feedback. Other encoders are read by the module.
* **Gyro.** YAMS applies `gyroAxis` and `gyroInvert` to the gyro's full 3D attitude. Check that the heading increases counterclockwise before driving.
* **Telemetry.** The drive publishes to NetworkTables under `Mechanisms/<telemetry name>` and logs to a DataLog. The verbosity levels are `LOW`, `MID` and `HIGH`, replacing `NONE`, `LOW`, `INFO`, `POSE`, `HIGH` and `MACHINE`.
* **Simulation.** MapleSim is gone. YAMS simulates each motor with its vendor's simulation, and the examples add a simulated battery. `pidfproperties_sim.json` holds simulation gains.
* **Validation.** The old warnings for CAN IDs above 40 and for a module at the robot's center are gone.

## Checklist

1. Replace hardware without 2027 support: a Pigeon (gen 1), any NavX or NavX2, an SPI or analog gyro, a Talon SRX, a brushed SPARK MAX, or an absolute encoder read through a Talon SRX or a TalonFXS's analog input. Give every module an absolute encoder.
2. Install WPILib `2027_alpha7` and move your project to it: Java 25, the SystemCore, and the 2027 `build.gradle` and `settings.gradle`.
3. Delete `vendordeps/yagsl.json` and every Phoenix 5 vendordep. Install `yagsl_commands2.json` **or** `yagsl_commands3.json`, which brings in the matching YAMS vendordep, and the 2027 vendordeps for the hardware in your configuration.
4. Regenerate your `swerve/` directory with the [config generator](https://config.yagsl.com), or port it by hand with [The `swerve/` directory](upgrade-from-2026.md#the-swerve-directory), [Device types](upgrade-from-2026.md#device-types) and [CAN buses](upgrade-from-2026.md#can-buses).
5. Delete `controllerproperties.json`.
6. Port your code: imports, the parser calls, the `SwerveDrive` methods, `SwerveInputStream`, and `updateTelemetry()` every loop.
7. Set your maximum chassis speed with `SwerveDriveConfig.withMaximumChassisSpeed`.
8. Retune the PID and feedforward gains. The old gains don't carry over.
9. Deploy, confirm every device shows up, and check the gyro direction and every module's offset before driving. See [Check and calibrate your hardware](../get-started/04-verify-and-calibrate-hardware.md).
