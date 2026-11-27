# Device Type Strings

Every device `type` string YAGSL's parser recognizes, grouped by device kind. These are the exact enum values enforced by the JSON Schema files in the YAGSL repository (`vendordep/src/test/resources/schemas/`) and resolved in code by `swervelib.core.parser.json.DeviceJson`, `swervelib.core.parser.deserializer.ReflectionsManager` and the per-vendor classes under `swervelib.core.parser.deserializer.reflections`.

{% hint style="info" %}
[config.yagsl.com](https://config.yagsl.com) only lets you pick valid combinations, so you rarely need to type these strings by hand. This page is for reading/debugging existing config files.
{% endhint %}

Type strings are lowercase and are matched exactly. Each vendor's devices are loaded by reflection only when a config uses them, so the matching vendor library has to be installed in your robot project: see [Vendordep Installation](../vendordep-installation.md).

## Gyro

Used in [swervedrive.json](swervedrive-json.md)'s `gyro.type`.

| Type string           | Hardware                                                                                                                                                                        | Vendor library           |
| --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------ |
| `pigeon2_can`         | CTRE Pigeon 2.0, over CAN                                                                                                                                                       | Phoenix 6                |
| `canandgyro_can`      | Redux Canandgyro, over CAN                                                                                                                                                      | ReduxLib                 |
| `systemcore_internal` | The IMU built into the SystemCore (WPILib `OnboardIMU`), mounted flat. `id` and `canbus` are ignored.                                                                           | none (part of WPILib)    |
| `navx3_can`           | Studica NavX3, over CAN                                                                                                                                                         | StudicaLib               |
| `navx3_usb`           | Studica NavX3, plugged into a SystemCore USB port given by `channel`: `0` for USB0 to `3` for USB3. `id` and `canbus` are ignored. Needs a YAGSL release newer than 2026.11.06. | StudicaLib               |
| `custom`              | Any gyro not built by the parser. You supply it yourself.                                                                                                                       | whatever your gyro needs |

See [Gyroscopes](../hardware/gyroscopes.md) for wiring/calibration notes per device.

## Motor Controllers

Used in [module json](module-json.md)'s `drive.type` and `angle.type`. Format is `<controller>_<motor>`.

| Controller family | Motor suffixes in the schema                          | Vendor library |
| ----------------- | ----------------------------------------------------- | -------------- |
| `talonfx_`        | `krakenx44`, `krakenx60`, `falcon`, `falcon500`       | Phoenix 6      |
| `talonfxs_`       | `neo`, `neo2`, `neo550`, `vortex`, `pulsar`, `minion` | Phoenix 6      |
| `sparkmax_`       | `neo`, `neo2`, `neo550`, `vortex`, `pulsar`, `minion` | REVLib         |
| `sparkflex_`      | `neo`, `neo2`, `neo550`, `vortex`, `pulsar`, `minion` | REVLib         |
| `nova_`           | `neo`, `neo2`, `neo550`, `vortex`, `pulsar`, `minion` | ThriftyLib     |

e.g. `talonfx_krakenx60`, `sparkmax_neo`, `sparkflex_vortex`, `nova_minion`.

The motor suffix selects the `DCMotor` model used for feedforward and simulation:

| Suffix                | `DCMotor` model                                                           |
| --------------------- | ------------------------------------------------------------------------- |
| `neo`, `neo2`         | `DCMotor.getNEO(1)`                                                       |
| `neo550`              | `DCMotor.getNeo550(1)`                                                    |
| `vortex`              | `DCMotor.getNeoVortex(1)`                                                 |
| `minion`              | `DCMotor.getMinion(1)`                                                    |
| `pulsar`              | ThriftyBot Pulsar (12 V, 3.1 N·m stall, 189 A stall, 7500 RPM free speed) |
| `krakenx44`           | `DCMotor.getKrakenX44(1)`                                                 |
| `krakenx60`           | `DCMotor.getKrakenX60(1)`                                                 |
| `falcon`, `falcon500` | `DCMotor.getFalcon500(1)`                                                 |

Every motor suffix above is a **brushless** motor. SparkMAX itself is still fully supported, but only in brushless mode. There is no `type` value for a brushed motor on any controller family (the old `sparkmax_brushed` and `talonsrx` types are gone). See [Motor Controllers](../hardware/motor-controllers.md) and [Schema Changes](../../upgrading/schema-changes.md).

{% hint style="info" %}
A Thrifty Nova also has to be told which motor is plugged into it. YAGSL sets the Nova's motor type to `MINION` for `nova_minion`, `PULSAR` for `nova_pulsar`, and `NEO` for every other suffix.
{% endhint %}

{% hint style="warning" %}
The schema lists the combinations above, but the parser itself splits the string at the `_` and accepts any controller with any motor suffix (e.g. `talonfx_neo` is not rejected). Not every motor is actually usable on every controller family in practice. Stick to combinations [config.yagsl.com](https://config.yagsl.com) offers, or a combination you've confirmed via the controller's own vendor tooling.
{% endhint %}

See [Motor Controllers](../hardware/motor-controllers.md) for per-controller notes.

## Absolute Encoders

Used in [module json](module-json.md)'s `absoluteEncoder.type`. Format is `<encoder>_<connection>`, where the connection is one of:

* `_can`: its own CAN device, using `id` and `canbus`.
* `_dio`: a duty cycle signal wired to a SystemCore SmartIO port, using `channel`.
* `_analog`: an analog signal wired to a SystemCore SmartIO port, using `channel`.
* `_attached`: wired to the data port of the module's **angle** motor controller and read through that controller. Works with SparkMAX, SparkFlex and Thrifty Nova angle motors.

| Type string                | Hardware                                                                                  |
| -------------------------- | ----------------------------------------------------------------------------------------- |
| `cancoder_can`             | CTRE CANcoder, over CAN                                                                   |
| `canandmag_can`            | Redux Canandmag, over CAN                                                                 |
| `canandmag_dio`            | Redux Canandmag, wired to a SystemCore SmartIO port                                       |
| `canandmag_attached`       | Redux Canandmag, plugged into the angle motor controller                                  |
| `revthroughbore_dio`       | REV Through Bore Encoder, wired to a SystemCore SmartIO port                              |
| `revthroughbore_attached`  | REV Through Bore Encoder, plugged into the angle motor controller                         |
| `splineencoder_can`        | REV spline encoder, over CAN                                                              |
| `srxmag_analog`            | CTRE Mag Encoder (SRX Mag), wired to a SystemCore SmartIO port                            |
| `srxmag_attached`          | CTRE Mag Encoder (SRX Mag), plugged into the angle motor controller                       |
| `andymarkhexbore_can`      | AndyMark Hex Bore Encoder, over CAN. **Not available in the 2027 builds yet**, see below. |
| `andymarkhexbore_dio`      | AndyMark Hex Bore Encoder, wired to a SystemCore SmartIO port                             |
| `andymarkhexbore_analog`   | AndyMark Hex Bore Encoder, wired to a SystemCore SmartIO port as an analog input          |
| `andymarkhexbore_attached` | AndyMark Hex Bore Encoder, plugged into the angle motor controller                        |
| `thrifty_analog`           | Thrifty absolute encoder, wired to a SystemCore SmartIO port as an analog input           |
| `thrifty_attached`         | Thrifty 10 pin absolute encoder, plugged into a Thrifty Nova angle motor controller       |
| `analog_attached`          | Generic analog encoder (3.3 V on a Spark), plugged into the angle motor controller        |
| `analog5v_attached`        | Generic 5 V analog encoder, plugged into a SparkMAX/SparkFlex angle motor controller      |
| `dutycycle_attached`       | Generic duty-cycle encoder, plugged into the angle motor controller                       |

Which `_attached` types work depends on the angle motor controller:

| Angle motor controller | `_attached` types it accepts                                                                                                                                                                                                                          |
| ---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| SparkMAX / SparkFlex   | `revthroughbore`, `canandmag`, `srxmag`, `andymarkhexbore` and `dutycycle` (all read as a duty cycle encoder), `analog` (3.3 V per rotation) and `analog5v` (5 V per rotation), which the module reads rather than the SPARK. Not `thrifty_attached`. |
| Thrifty Nova           | `thrifty`, `revthroughbore`, `canandmag`, `srxmag`, `andymarkhexbore`, `dutycycle`, `analog`. Not `analog5v_attached`.                                                                                                                                |
| TalonFX / TalonFXS     | None. Use a `_can`, `_dio` or `_analog` encoder.                                                                                                                                                                                                      |

{% hint style="warning" %}
`andymarkhexbore_can` is still a valid type string, but AndyMark's library has no `2027_alpha7` release yet, so the YAGSL 2027 artifacts are built without its device class and selecting it fails when the drive is created. Wire the encoder to a SmartIO port (`andymarkhexbore_dio` / `andymarkhexbore_analog`) or to the angle motor controller (`andymarkhexbore_attached`) instead.
{% endhint %}

See [Absolute Encoders](../hardware/absolute-encoders.md) for per-device notes and calibration tips.
