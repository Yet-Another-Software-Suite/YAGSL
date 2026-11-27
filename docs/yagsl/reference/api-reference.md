# API Reference

YAGSL's own Java surface is intentionally small: `SwerveParser` reads your [JSON config](json-schema/) and hands you back a fully-built **YAMS** `SwerveDrive`. From that point on, you're driving and configuring a YAMS object, not a YAGSL-specific one, so the authoritative API reference lives in the YAMS docs, not here.

## YAGSL packages

YAGSL 2027 is split into a command framework agnostic core and one small package per WPILib command framework. Which packages you have depends on the [vendordep you installed](vendordep-installation.md).

| Package                                                   | Artifact          | What's in it                                                                                                                            |
| --------------------------------------------------------- | ----------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| `swervelib.commands2`                                     | `YAGSL-commands2` | `SwerveParser` for Commands v2. Returns a `yams.commands2.swerve.SwerveDrive` bound to your `Subsystem`.                                |
| `swervelib.commands3`                                     | `YAGSL-commands3` | `SwerveParser` for Commands v3. Returns a `yams.commands3.swerve.SwerveDrive` bound to your `Mechanism`.                                |
| `swervelib.core.parser`                                   | `YAGSL-core`      | The base `SwerveParser` (JSON reading, the `SwerveDriveDevices` / `SwerveModuleDevices` records), `CANBuses`, `PIDFConfig`.             |
| `swervelib.core.parser.json` (and `.json.modules`)        | `YAGSL-core`      | The classes each JSON file is read into: `SwerveDriveJson`, `ModuleJson`, `DeviceJson`, `PhysicalPropertiesJson`, `PIDFPropertiesJson`. |
| `swervelib.core.parser.deserializer` (and `.reflections`) | `YAGSL-core`      | `ReflectionsManager` and the per-vendor device classes, loaded by reflection.                                                           |
| `swervelib.core.telemetry`                                | `YAGSL-core`      | `SwerveDriveTelemetry`.                                                                                                                 |

The pre-2027 `swervelib.parser.*` and `swervelib.telemetry.*` packages are now `swervelib.core.parser.*` and `swervelib.core.telemetry.*`. Robot code should import `SwerveParser` from `swervelib.commands2` or `swervelib.commands3`, not from `swervelib.core.parser`.

## SwerveParser entry points

Every method is `static`. Parse the directory first, then create the drive:

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

| Method                                                            | Defined in                                                              | What it does                                                                                                                                                                                         |
| ----------------------------------------------------------------- | ----------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `SwerveParser.parse(File directory)`                              | `swervelib.core.parser.SwerveParser`                                    | Reads the JSON directory into the static `swerveDriveJson`, `pidfPropertiesJson`, `physicalPropertiesJson` and `moduleJsons` fields. Throws `UncheckedIOException` if a file can't be read.          |
| `SwerveParser.parseDirectory(File directory)`                     | `swervelib.core.parser.SwerveParser`                                    | Same, but throws the checked `IOException`.                                                                                                                                                          |
| `SwerveParser.readJson(File file, Class<T> type)`                 | `swervelib.core.parser.SwerveParser`                                    | Reads one JSON file into one of the `swervelib.core.parser.json` classes.                                                                                                                            |
| `SwerveParser.createSwerveDrive(SwerveDriveConfig config)`        | `swervelib.commands2.SwerveParser` / `swervelib.commands3.SwerveParser` | Builds the modules and gyro from the parsed JSON, applies them to your `SwerveDriveConfig`, and returns the framework's `SwerveDrive`.                                                               |
| `SwerveParser.createSwerveDriveDevices(SwerveDriveConfig config)` | `swervelib.commands2.SwerveParser` / `swervelib.commands3.SwerveParser` | Same, but returns a `SwerveDriveDevices<SwerveDrive>` record: `swerveDrive()`, `gyro()` and `modules()` (one `SwerveModuleDevices` per module, with `drive()`, `azimuth()` and `absoluteEncoder()`). |

{% hint style="warning" %}
`parse(...)` returns a core `swervelib.core.parser.SwerveParser`, which has no `createSwerveDrive`. The 2026 style chain `SwerveParser.parse(dir).createSwerveDrive(cfg)` and the older `new SwerveParser(dir).createSwerveDrive(cfg)` no longer compile. Call the two static methods one after the other, as shown above.
{% endhint %}

`SwerveParser.createSwerveDriveDevices(config)` is the one place YAGSL hands back something other than YAMS objects: alongside the `SwerveDrive`, it returns the raw vendor motor controller/encoder/ gyro objects it built, for the rare case where you need to call a vendor-specific method `SmartMotorController` doesn't expose. `gyro()` is `null` for a `custom` gyro. See [How to access raw hardware devices](../tune-and-extend/access-raw-hardware-devices.md).

## YAMS classes you get back

| Class                                                         | Commands v2                                      | Commands v3                               | YAMS API reference                                                                                           |
| ------------------------------------------------------------- | ------------------------------------------------ | ----------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| `SwerveDrive`                                                 | `yams.commands2.swerve.SwerveDrive`              | `yams.commands3.swerve.SwerveDrive`       | [SwerveDrive](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-drive)                |
| `SwerveDriveConfig`                                           | `yams.commands2.config.SwerveDriveConfig`        | `yams.commands3.config.SwerveDriveConfig` | [SwerveDriveConfig](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-drive-config)   |
| `SwerveInputStream`                                           | `yams.commands2.swerve.SwerveInputStream`        | `yams.commands3.swerve.SwerveInputStream` | [SwerveInputStream](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-input-stream)   |
| `SwerveModule` (per-module)                                   | `yams.core.mechanisms.swerve.SwerveModule`       | same                                      | [SwerveModule](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-module)              |
| `SwerveModuleConfig` (built internally from your module JSON) | `yams.core.mechanisms.config.SwerveModuleConfig` | same                                      | [SwerveModuleConfig](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-module-config) |

The framework specific `SwerveDrive` and `SwerveDriveConfig` extend the shared `yams.core.mechanisms.swerve.SwerveDrive` and `yams.core.mechanisms.config.SwerveDriveConfig`, which is where most of their methods are documented.

The full YAMS API reference (all mechanisms, motor controllers, units, and math helpers, not just swerve) is at [yagsl.gitbook.io/yams](https://yagsl.gitbook.io/yams/api-reference/java-reference).

## Javadoc

| Library                              | Javadoc                                                                                                                                                                                                                                                                                                   |
| ------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| YAGSL                                | [javadocs.yassrobotics.com/yagsl](https://javadocs.yassrobotics.com/yagsl/)                                                                                                                                                                                                                               |
| `swervelib.commands2.SwerveParser`   | [SwerveParser (Commands v2)](https://javadocs.yassrobotics.com/yagsl/swervelib/commands2/SwerveParser.html)                                                                                                                                                                                               |
| `swervelib.commands3.SwerveParser`   | [SwerveParser (Commands v3)](https://javadocs.yassrobotics.com/yagsl/swervelib/commands3/SwerveParser.html)                                                                                                                                                                                               |
| `swervelib.core.parser.SwerveParser` | [SwerveParser (core)](https://javadocs.yassrobotics.com/yagsl/swervelib/core/parser/SwerveParser.html)                                                                                                                                                                                                    |
| `swervelib.core.parser.CANBuses`     | [CANBuses](https://javadocs.yassrobotics.com/yagsl/swervelib/core/parser/CANBuses.html)                                                                                                                                                                                                                   |
| `swervelib.core.parser.json`         | [JSON classes](https://javadocs.yassrobotics.com/yagsl/swervelib/core/parser/json/package-summary.html)                                                                                                                                                                                                   |
| YAMS                                 | [javadocs.yassrobotics.com/yams](https://javadocs.yassrobotics.com/yams/)                                                                                                                                                                                                                                 |
| YAMS `SwerveDrive`                   | [Commands v2](https://javadocs.yassrobotics.com/yams/yams/commands2/swerve/SwerveDrive.html), [Commands v3](https://javadocs.yassrobotics.com/yams/yams/commands3/swerve/SwerveDrive.html), [core](https://javadocs.yassrobotics.com/yams/yams/core/mechanisms/swerve/SwerveDrive.html)                   |
| YAMS `SwerveDriveConfig`             | [Commands v2](https://javadocs.yassrobotics.com/yams/yams/commands2/config/SwerveDriveConfig.html), [Commands v3](https://javadocs.yassrobotics.com/yams/yams/commands3/config/SwerveDriveConfig.html), [core](https://javadocs.yassrobotics.com/yams/yams/core/mechanisms/config/SwerveDriveConfig.html) |

{% hint style="info" %}
Only `SwerveParser` and the JSON-file classes under `swervelib.core.parser.json` are YAGSL-specific. If you find yourself calling a method on `SwerveDrive`, `SwerveModule`, `SwerveDriveConfig`, `SwerveModuleConfig`, or `SwerveInputStream`, you're looking at YAMS API. Go to the links above, not the YAGSL source, for the authoritative docs and Javadoc.
{% endhint %}

## Where to look for what

* **"What JSON fields exist?"** → [JSON Schema Reference](json-schema/) (this site).
* **"What does `SwerveDrive.drive(...)` do, what are all its methods?"** → [YAMS SwerveDrive reference](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-drive).
* **"How do I build a `SwerveDriveConfig` by hand instead of from JSON?"** → [YAMS SwerveDriveConfig reference](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-drive-config).
* **"How do I wire joystick input into field/robot-relative chassis velocities?"** → [YAMS SwerveInputStream reference](https://yagsl.gitbook.io/yams/api-reference/java-reference/swerve/swerve-input-stream).
* **"Which CAN bus does a `canbus` value mean?"** → [Control system and CAN buses](json-schema/#control-system-and-can-buses).
* **"What changed in the JSON schema or the API recently?"** → [Schema Changes](../upgrading/schema-changes.md).
