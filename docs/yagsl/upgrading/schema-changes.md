# Schema changes

{% hint style="info" %}
Upgrading a robot project from the 2026 season? Follow [Upgrade a 2026 project to YAGSL 2027](upgrade-from-2026.md) instead. This page is for configurations made with the early builds of the rewritten YAGSL.
{% endhint %}

This page covers the two breaking changes to YAGSL's configuration and entry point:

* [Migrating a 2026 project to YAGSL 2027](schema-changes.md#migrating-to-yagsl-2027): WPILib 2027, the SystemCore, new packages, two vendordeps and new `canbus` values. The JSON **shape** did not change.
* [Migrating from pre-2026.8.05](schema-changes.md#migrating-from-pre-2026.8.05): the rewrite on top of YAMS, which changed nearly every JSON field.

If you are coming from a pre-2026.8.05 config, do both, starting with the older one.

## Migrating to YAGSL 2027

YAGSL `2026.10.08` and newer target WPILib `2027_alpha7` (Java 25) and run on the SystemCore. Your `swerve/` directory keeps the same files and fields, so most of the work is in the build setup and your Java imports.

### Vendordep

| 2026                                                                           | 2027                                                                                                                                                                        |
| ------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| One vendordep, `https://yet-another-software-suite.github.io/YAGSL/yagsl.json` | Two vendordeps, install exactly one: `https://cdn.yassrobotics.com/yagsl_commands2.json` (Commands v2) or `https://cdn.yassrobotics.com/yagsl_commands3.json` (Commands v3) |
| Artifact `YAGSL-java`                                                          | Artifacts `YAGSL-core` plus `YAGSL-commands2` or `YAGSL-commands3`                                                                                                          |
| Required REVLib, Phoenix 5, Phoenix 6, StudicaLib, ThriftyLib                  | Requires only the matching YAMS vendordep. Hardware vendor libraries are optional: install the ones for the devices in your config.                                         |

Delete the old `vendordeps/yagsl.json` and install the new vendordep for your command framework. See [Vendordep Installation](../reference/vendordep-installation.md).

### Packages and entry point

| 2026                                                            | 2027                                                                                   |
| --------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| `swervelib.parser.SwerveParser`                                 | `swervelib.commands2.SwerveParser` or `swervelib.commands3.SwerveParser`               |
| `swervelib.parser.*` (JSON classes, `PIDFConfig`, deserializer) | `swervelib.core.parser.*`                                                              |
| `swervelib.telemetry.*`                                         | `swervelib.core.telemetry.*`                                                           |
| `yams.mechanisms.swerve.SwerveDrive`                            | `yams.commands2.swerve.SwerveDrive` or `yams.commands3.swerve.SwerveDrive`             |
| `yams.mechanisms.config.SwerveDriveConfig`                      | `yams.commands2.config.SwerveDriveConfig` or `yams.commands3.config.SwerveDriveConfig` |
| `yams.mechanisms.swerve.utility.SwerveInputStream`              | `yams.commands2.swerve.SwerveInputStream` or `yams.commands3.swerve.SwerveInputStream` |
| `yams.mechanisms.swerve.SwerveModule`                           | `yams.core.mechanisms.swerve.SwerveModule`                                             |
| `SwerveParser.parse(dir).createSwerveDrive(cfg)`                | `SwerveParser.parse(dir);` then `SwerveParser.createSwerveDrive(cfg);`                 |
| WPILib `edu.wpi.first.*`                                        | WPILib `org.wpilib.*`                                                                  |

`parse(...)` now returns the command framework agnostic `swervelib.core.parser.SwerveParser`, which has no `createSwerveDrive`, so the two calls can no longer be chained:

{% tabs %}
{% tab title="Commands v2" %}
```java
import swervelib.commands2.SwerveParser;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;

var cfg = new SwerveDriveConfig().withSubsystem(this);

SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
SwerveDrive drive = SwerveParser.createSwerveDrive(cfg);
```
{% endtab %}

{% tab title="Commands v3" %}
```java
import swervelib.commands3.SwerveParser;
import yams.commands3.config.SwerveDriveConfig;
import yams.commands3.swerve.SwerveDrive;

var cfg = new SwerveDriveConfig().withMechanism(this);

SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
SwerveDrive drive = SwerveParser.createSwerveDrive(cfg);
```
{% endtab %}
{% endtabs %}

The rest of the YAMS API also moved (`yams.core.*`, `yams.commands2.*`, `yams.commands3.*`) and WPILib renamed some classes (for example `ChassisSpeeds` to `ChassisVelocities` and `SwerveModuleState` to `SwerveModuleVelocity`). See the [API Reference](../reference/api-reference.md) for where the swerve classes live now.

### `canbus` values

`canbus` is still a string on every device, but it is now read the same way for every vendor (`swervelib.core.parser.CANBuses`), and it names a SystemCore or Motioncore bus by **number**:

| `canbus`                | Bus                                                              |
| ----------------------- | ---------------------------------------------------------------- |
| `""` (empty or missing) | SystemCore `can_s0`                                              |
| `"0"` to `"4"`          | SystemCore `can_s0` to `can_s4`                                  |
| `"5"` to `"24"`         | Motioncore `can_d0` to `can_d19`                                 |
| anything else           | Passed to the vendor, such as a CANivore's name for CTRE devices |

What to change in an existing config:

| Device                                                                                       | Old value                                          | New value                                          |
| -------------------------------------------------------------------------------------------- | -------------------------------------------------- | -------------------------------------------------- |
| CTRE (`talonfx_*`, `talonfxs_*`, `pigeon2_can`, `cancoder_can`) with `""`, wired to `can_s0` | `""`                                               | `""` (unchanged, but see the warning below)        |
| CTRE with `""`, wired to `can_s2`                                                            | `""`                                               | `"2"`                                              |
| CTRE on a CANivore                                                                           | the CANivore's name                                | unchanged                                          |
| REV (`sparkmax_*`, `sparkflex_*`, `splineencoder_can`)                                       | a port name such as `"can_s1"`                     | the bus number, `"1"`                              |
| ThriftyBot (`nova_*`)                                                                        | anything non-numeric                               | `""` or the bus number                             |
| Redux (`canandgyro_can`, `canandmag_can`)                                                    | a ReduxLib bus string such as `"socketcan:can_s1"` | still works, but the bus number `"1"` is preferred |

{% hint style="danger" %}
**The CTRE default bus changed.** In the first 2027 release (`2026.10.08`), a CTRE device with an empty `canbus` was on Phoenix 6's SystemCore default bus, `can_s2`. Since `2026.10.09` it is on `can_s0`, like every other vendor's devices. If your CTRE devices are wired to `can_s2`, set `"canbus": "2"` or they will not be found.
{% endhint %}

{% hint style="warning" %}
**REV and Thrifty devices only accept `""`, a bus number or its `CANPort` name.** Port names such as `"can_s1"` that REV devices used to take now throw `REV devices only use the Systemcore and Motioncore CAN buses`; use `"1"` or `"CAN_S1"`. A bus number that doesn't exist (such as `"25"`) throws for every vendor.
{% endhint %}

See [Control system and CAN buses](../reference/json-schema/#control-system-and-can-buses) for the full rules.

### Devices

* **Thrifty Nova** (`nova_*`) is supported again, through YAMS's `NovaWrapper`, including absolute encoders plugged into the Nova (`thrifty_attached` and the other `_attached` types). New type strings: `thrifty_attached` and `thrifty_analog`. See [Motor Controllers](../reference/hardware/motor-controllers.md#thrifty-nova).
* **`systemcore_internal`** now works: it is the IMU built into the SystemCore. See [Gyroscopes](../reference/hardware/gyroscopes.md#systemcore-imu).
* **`navx3_usb`** is new: a NavX3 plugged into a SystemCore USB port. See [Gyroscopes](../reference/hardware/gyroscopes.md#navx3-over-usb).
* **`andymarkhexbore_can` is not available yet**, because AndyMark's library has no `2027_alpha7` release. Use a different encoder connection until it does.
* **`channel`** on `_dio` and `_analog` absolute encoders is a SystemCore SmartIO channel.

### Gyro API

* The parser now hands YAMS the gyro's full attitude and lets YAMS apply `gyroAxis` and `gyroInvert` in one place. If you had flipped `gyroInvert` to work around a **Canandgyro** being inverted twice, re-check your heading direction.
* `SwerveDrive.getGyroAngle()` is removed. Use `SwerveDrive.getGyroRotation3d()` (for example `drive.getGyroRotation3d().toRotation2d()` for the heading).
* `SwerveDriveConfig.withGyro(...)` takes a `Supplier<Rotation3d>` instead of a `Supplier<Angle>`. This only affects you if you use a [`custom` gyro](../reference/hardware/gyroscopes.md#custom-gyro).

### JSON parsing

The JSON files are now read with WPILib's JSON library (avaje-jsonb) instead of Jackson. Nothing changes in the files themselves: unknown fields are still ignored, and missing optional fields still get their defaults.

### 2027 migration checklist

1. Install WPILib `2027_alpha7` and move the project to it.
2. Remove `vendordeps/yagsl.json`. Install `yagsl_commands2.json` **or** `yagsl_commands3.json`, the matching YAMS vendordep, and the 2027 vendor libraries for the hardware you use.
3. Update imports: `swervelib.parser.SwerveParser` to `swervelib.commands2.SwerveParser` (or `commands3`), and the YAMS classes to their `yams.commands2.*` / `yams.commands3.*` / `yams.core.*` packages.
4. Split `SwerveParser.parse(dir).createSwerveDrive(cfg)` into two static calls.
5. Check every `canbus`: CTRE devices on `can_s2` need `"2"`, and REV/Thrifty devices need `""` or a number.
6. Replace `getGyroAngle()` with `getGyroRotation3d()`, and re-check the gyro direction.
7. Deploy and confirm every device shows up before enabling.

## Migrating from pre-2026.8.05

Between the `2026.4.1` and `2026.8.05` releases, YAGSL was rewritten from its own standalone swerve implementation into a thin JSON parser that builds a **YAMS** `SwerveDrive`. The JSON configuration schema changed to match what YAMS's config builders need. If your `src/main/deploy/swerve/` directory predates this, none of your old JSON files will parse correctly against the new schema. This section is a field-by-field diff to help you port them by hand. For a fresh config, it's easier to just regenerate everything from [config.yagsl.com](https://config.yagsl.com).

{% hint style="warning" %}
This is a breaking change, not an additive one. The parser silently ignores unknown fields, so an old file will "parse" without errors but silently drop every renamed/restructured field, leaving you with defaults you didn't intend. Don't assume an old config "still works" just because it loads without an exception. Diff it against this page.
{% endhint %}

### `swervedrive.json`

| Old (≤ 2026.4.1)          | New (≥ 2026.8.05)                  | Notes                                                                            |
| ------------------------- | ---------------------------------- | -------------------------------------------------------------------------------- |
| `imu: {type, id, canbus}` | `gyro: {type, id, canbus}`         | Renamed. The `type` strings changed too, and some gyros were removed. See below. |
| `invertedIMU: boolean`    | `gyroInvert: boolean`              | Renamed.                                                                         |
| _(none)_                  | `gyroAxis: "yaw"\|"pitch"\|"roll"` | New, optional, defaults to `"yaw"`.                                              |
| `modules: string[]`       | `modules: string[]`                | Unchanged.                                                                       |

**Old:**

```json
{
  "imu": { "type": "pigeon2", "id": 13, "canbus": "canivore" },
  "invertedIMU": true,
  "modules": ["frontleft.json", "frontright.json", "backleft.json", "backright.json"]
}
```

**New:**

```json
{
  "gyro": { "type": "pigeon2_can", "id": 13, "canbus": "canivore" },
  "gyroAxis": "yaw",
  "gyroInvert": true,
  "modules": ["frontleft.json", "frontright.json", "backleft.json", "backright.json"]
}
```

The `type` strings changed too. Old bare names became `<device>_<connection>` strings, such as `"pigeon2"` to `"pigeon2_can"`. The original Pigeon (`"pigeon"`) and every NavX or NavX2 type (`"navx"` and its variants) have no new type; see [Hardware support that was dropped](schema-changes.md#hardware-support-that-was-dropped-not-just-renamed). See [Device Type Strings](../reference/json-schema/device-types.md) for the current, complete list.

### Module JSON (e.g. `frontleft.json`)

| Old (≤ 2026.4.1)                                                                                                        | New (≥ 2026.8.05)                                                                                                                                                                                      | Notes                                                                                                                                                                                                         |
| ----------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `encoder: {type, id, canbus}`                                                                                           | `absoluteEncoder: {type, id, channel, canbus}`                                                                                                                                                         | Renamed, and gained a `channel` field for analog/DIO-connected encoders. `type` strings changed (`"cancoder"` → `"cancoder_can"`, etc.). See [Device Type Strings](../reference/json-schema/device-types.md). |
| `absoluteEncoderOffset: number`                                                                                         | `absoluteEncoderOffset: number`                                                                                                                                                                        | Unchanged (degrees).                                                                                                                                                                                          |
| `absoluteEncoderInverted: boolean`                                                                                      | `absoluteEncoderInverted: boolean`                                                                                                                                                                     | Unchanged, defaults to `false`.                                                                                                                                                                               |
| `drive` / `angle`: `{type, id, canbus}`                                                                                 | Same shape, but `type` strings changed to `<controller>_<motor>` (e.g. `"sparkmax_neo"` unchanged, but `"talonfx"` alone became `"talonfx_krakenx60"` etc.; the motor is now part of the type string). | See [Device Type Strings](../reference/json-schema/device-types.md).                                                                                                                                          |
| `conversionFactors: {drive: {gearRatio, diameter, factor}, angle: {gearRatio, factor}}` (optional, per-module override) | `gearing: {drive: {gearRatio, diameter}, angle: {gearRatio}}` (optional, per-module override)                                                                                                          | Renamed, and `factor` is gone. A module's `gearing.drive` replaces the shared drive gearing when its `gearRatio` isn't `0`; a module that sets only `gearRatio` keeps the shared wheel diameter, as before.   |
| `inverted: {drive, angle}`                                                                                              | `inverted: {drive, angle}`                                                                                                                                                                             | Unchanged.                                                                                                                                                                                                    |
| `location: {front, left}`                                                                                               | `location: {front, left}`                                                                                                                                                                              | Unchanged (inches).                                                                                                                                                                                           |

**Old:**

```json
{
  "drive": { "type": "sparkmax_neo", "id": 4, "canbus": null },
  "angle": { "type": "sparkmax_neo", "id": 3, "canbus": null },
  "encoder": { "type": "cancoder", "id": 9, "canbus": null },
  "inverted": { "drive": false, "angle": false },
  "absoluteEncoderOffset": -114.609,
  "location": { "front": 12, "left": 12 }
}
```

**New:**

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

### `modules/physicalproperties.json`

| Old (≤ 2026.4.1)                                                                        | New (≥ 2026.8.05)                                             | Notes                                                                                                                                                                                          |
| --------------------------------------------------------------------------------------- | ------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `conversionFactors: {drive: {gearRatio, diameter, factor}, angle: {gearRatio, factor}}` | `gearing: {drive: {gearRatio, diameter}, angle: {gearRatio}}` | Renamed and simplified. The precomputed `factor` override is gone; YAMS derives the conversion internally from `gearRatio`/`diameter`.                                                         |
| `currentLimit: {drive, angle}`                                                          | `statorCurrentLimit: {drive, angle}`                          | Renamed. Defaults are still `40`/`20` A, but it is now a stator limit on every motor controller. It used to be a supply limit on a TalonFX or TalonFXS and the smart current limit on a SPARK. |
| `friction: {drive, angle}`                                                              | _(removed)_                                                   | Configured the MapleSim simulation, which is gone.                                                                                                                                             |
| `steerRotationalInertia: number`                                                        | _(removed)_                                                   | Configured the MapleSim simulation, which is gone.                                                                                                                                             |
| `robotMass: number`                                                                     | _(removed)_                                                   | Configured the MapleSim simulation, which is gone.                                                                                                                                             |
| `rampRate: {drive, angle}`                                                              | _(removed)_                                                   | No ramp is applied. The old default was 0.25 s.                                                                                                                                                |
| `wheelGripCoefficientOfFriction: number`                                                | _(removed)_                                                   | Configured the MapleSim simulation, which is gone.                                                                                                                                             |
| `optimalVoltage: number`                                                                | _(removed)_                                                   | No voltage compensation is applied. The old default was 12 V, also used for the default drive feedforward.                                                                                     |

**Old:**

```json
{
  "conversionFactors": {
    "angle": { "gearRatio": 12.8, "factor": 0 },
    "drive": { "gearRatio": 8.14, "diameter": 4, "factor": 0 }
  },
  "currentLimit": { "drive": 40, "angle": 20 },
  "rampRate": { "drive": 0.25, "angle": 0.25 },
  "wheelGripCoefficientOfFriction": 1.19,
  "optimalVoltage": 12
}
```

**New:**

```json
{
  "gearing": {
    "drive": { "gearRatio": 8.14, "diameter": 4.0 },
    "angle": { "gearRatio": 12.8 }
  },
  "statorCurrentLimit": { "drive": 40, "angle": 20 }
}
```

`friction`, `robotMass`, `steerRotationalInertia` and `wheelGripCoefficientOfFriction` configured the MapleSim physics simulation. MapleSim is gone: YAMS simulates each motor with its vendor's simulation instead, and nothing in this file replaces them.

### `modules/pidfproperties.json`

| Old (≤ 2026.4.1)             | New (≥ 2026.8.05)                    | Notes                                                                                                                                                                            |
| ---------------------------- | ------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `p`, `i`, `d`                | `p`, `i`, `d`                        | Unchanged.                                                                                                                                                                       |
| `f: number`                  | _(removed, replaced by `s`/`v`/`a`)_ | Old `f` was not a general feedforward: SPARKs and the Nova ignored it, and TalonFX and TalonFXS used it as kS. The new fields are a `SimpleMotorFeedforward` (`kS`, `kV`, `kA`). |
| `iz: number` (integral zone) | _(removed)_                          | No longer configurable.                                                                                                                                                          |
| `output: {min, max}` (clamp) | _(removed)_                          | No longer configurable via JSON.                                                                                                                                                 |
| _(none)_                     | `s`, `v`, `a: number` (all optional) | New feedforward gains. If `drive.v` is left `0`/omitted, the parser auto-derives `kV` from the drive motor's free speed.                                                         |

**Old:**

```json
{
  "drive": { "p": 0.00023, "i": 0.0000002, "d": 1, "f": 0, "iz": 0 },
  "angle": { "p": 0.01, "i": 0, "d": 0, "f": 0, "iz": 0 }
}
```

**New:**

```json
{
  "drive": { "p": 0.5, "i": 0.0, "d": 0.01 },
  "angle": { "p": 2.0, "i": 0.0, "d": 0.1 }
}
```

You'll need to re-tune from scratch rather than porting old `p`/`i`/`d` values. The old gains were in each vendor's own units, such as duty cycle per degree on a SPARK. YAMS's closed loop outputs volts, from an error in rotations, or meters for the drive motor. See [How to Tune PIDF Gains](../tune-and-extend/tune-pidf-gains.md).

### `controllerproperties.json`: removed entirely

Pre-2026.8.05 YAGSL read a `controllerproperties.json` with an `angleJoystickRadiusDeadband` and a `heading: {p, i, d}` heading-hold PID, used internally by a `swervelib.SwerveController` class to build field-relative driving with heading lock. **Both the file and the class are gone.**

```json
// This file no longer does anything. Delete it.
{
  "angleJoystickRadiusDeadband": 0.5,
  "heading": { "p": 0.4, "i": 0, "d": 0.01 }
}
```

Field-relative/heading-based driving is now built in your own robot code using YAMS's `SwerveInputStream` (`yams.commands2.swerve.SwerveInputStream` or `yams.commands3.swerve.SwerveInputStream`), not a JSON-configured heading PID. See [SwerveInputStream reference](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-input-stream) and the [tutorial's driving step](../get-started/05-deploy-and-drive.md) for the current pattern.

### Hardware support that was dropped, not just renamed

This isn't just a field-rename. Some previously-supported gyro and motor-controller hardware has **no equivalent type string at all** in the current parser (`swervelib.core.parser.deserializer.reflections`). If your old config used one of these, there's no direct migration; you need different hardware or a custom fork:

* **Gyros:** every NavX and NavX2 type (`navx`, `navx_spi`, `navx_i2c`, `navx_mxp_serial`, `navx_usb`), the original Pigeon (gen 1: `pigeon`, `pigeon_via_talonsrx`), and the SPI and analog gyros (`adxrs450`, `adis16448`, `adis16470`, `analog`) are all gone. There is no NavX2 library for WPILib 2027 yet, and Phoenix 5, which the original Pigeon needs, has no 2027 release. The gyro types are `pigeon2_can`, `canandgyro_can`, `navx3_can`, `systemcore_internal` (2027) and `custom`. See [Gyroscopes](../reference/hardware/gyroscopes.md) for which of them are available in the current release.
* **Motor controllers:** `talonsrx` and brushed `sparkmax_brushed` are gone. Only the five brushless controller families (`talonfx`, `talonfxs`, `sparkmax`, `sparkflex`, `nova`) are supported now, each paired with a brushless motor. See [Device Type Strings](../reference/json-schema/device-types.md).

If you're on old gyro/motor hardware that's no longer supported, budget time to swap hardware, not just JSON. This is a real capability change, not a documentation gap. For a gyro, the [`custom` gyro type](../reference/hardware/gyroscopes.md#custom-gyro) lets you keep supplying it yourself.

### Behavior changes that aren't JSON fields

* **Cosine compensation** used to be toggleable per-module (`useCosineCompensator`, default `true`). It's now always applied by the parser. There's no way to disable it via config, but robot code can turn it off per module with `module.getConfig().withCosineCompensation(false)`.
* **Creating the drive** changed from `new SwerveParser(dir).createSwerveDrive(maximumSpeed)` to `SwerveParser.parse(dir)` followed by `SwerveParser.createSwerveDrive(new SwerveDriveConfig()...)`. See [Deploy and Drive](../get-started/05-deploy-and-drive.md).
* **No odometry thread.** The old drive updated its odometry on its own thread. Now the pose only updates when you call `drive.updateTelemetry()`, so call it every loop.
* **Maximum speed.** The old `maximumSpeed` argument is gone. YAGSL works out the maximum module speed from the drive motor's free speed and gearing, but doesn't set a maximum chassis speed. Without `SwerveDriveConfig.withMaximumChassisSpeed(...)`, `SwerveInputStream` scales the joysticks to 4 m/s and 1 rotation per second.
* **Heading correction** was a JSON-configured PID run inside `SwerveDrive.drive()`. It's now built in your code with `SwerveInputStream`, with gains from `SwerveDriveConfig.withRotationController(PIDController)`. See `controllerproperties.json` above.
* **Module encoder auto-synchronization**, `SwerveDrive.setModuleEncoderAutoSynchronize(boolean, double)`, is now set per motor controller with `SmartMotorControllerConfig.withFeedbackSynchronizationThreshold(Angle)`.
* **`SwerveDrive.pushOffsetsToEncoders()` and `SwerveDrive.restoreInternalOffset()`** are removed. The parser picks external or internal feedback for you. See [Module Behaviors](../how-it-works/module-behaviors.md#offset-offloading-external-vs-internal-feedback-sensor).
* **Auto-centering modules** (`SwerveDrive.setAutoCenteringModules`, `SwerveModule.setAntiJitter`), which snapped idle modules back to `0°`, was not carried over. Open an issue if you need it back.
* **Angular velocity compensation**, `SwerveDrive.setAngularVelocityCompensation(boolean, boolean, double)`, is now `SwerveDriveConfig.withGyroAngularVelocityScaleFactor(double)`. See [Module Behaviors](../how-it-works/module-behaviors.md#angular-velocity-compensation).
* **Telemetry verbosity** levels `NONE`/`LOW`/`INFO`/`POSE`/`HIGH`/`MACHINE` are now `LOW`, `MID` and `HIGH`. Pick the nearest one.

### Pre-2026.8.05 migration checklist

1. Regenerate from [config.yagsl.com](https://config.yagsl.com) if at all possible. It's faster and less error-prone than hand-porting every field above.
2. If hand-porting: rename `imu`→`gyro`, `invertedIMU`→`gyroInvert` in `swervedrive.json`; add `gyroAxis: "yaw"`.
3. Rename `encoder`→`absoluteEncoder` in every module file, add a `channel` (usually `0` if CAN), add `absoluteEncoderInverted: false`.
4. Update every `type` string to the new `<device>_<connection>` convention. See [Device Type Strings](../reference/json-schema/device-types.md).
5. Replace `conversionFactors`→`gearing` in `physicalproperties.json`, dropping any `factor` override. Replace `currentLimit`→`statorCurrentLimit`.
6. Delete `controllerproperties.json`; move any heading-lock behavior into robot code via `SwerveInputStream`.
7. Re-tune PIDF from scratch using the new `p`/`i`/`d`/`s`/`v`/`a` shape; old gains don't transfer.
8. Delete any `rampRate`/`friction`/`robotMass`/`steerRotationalInertia`/ `wheelGripCoefficientOfFriction`/`optimalVoltage` fields. They're ignored now.
9. Continue with the [2027 migration checklist](schema-changes.md#id-2027-migration-checklist) above.
