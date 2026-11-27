---
description: Supported absolute encoder types for a module's absoluteEncoder field
---

# Absolute Encoders

YAGSL supports most common FRC absolute encoders. An absolute encoder is required for every module. It's what lets the angle motor know its true position on power-up, without needing to "home" against a hard stop.

The live reading shows up on the dashboard under `Mechanisms/swerve/modules/<name>/encoder` (degrees). Use it to find the value for `absoluteEncoderOffset` in each module's JSON. See [Telemetry, Simulation & Vision](../../how-it-works/telemetry-and-vision.md) for the full NT tree.

## Absolute Encoder Checklist

* [ ] All absolute encoders read counterclockwise positive (CCW+).
* [ ] Magnetic absolute encoders get a good read on the magnet both at rest and while the robot is moving (check for wiggle/wire strain).
* [ ] Each encoder has a unique CAN ID or SmartIO channel.
* [ ] Encoder reads the full `0°`–`360°` range.

## Configuring an absolute encoder

`absoluteEncoder` is a `DeviceJson`-shaped object, `"<encoder>_<connection>"`:

```json
{
  "absoluteEncoder": { "type": "cancoder_can", "id": 11, "channel": 0, "canbus": "" },
  "absoluteEncoderOffset": -18.281,
  "absoluteEncoderInverted": false
}
```

* `absoluteEncoderOffset`: degrees to add so the wheel reads `0°` when facing forward with the bevel gear to the left. Read this off `Raw Absolute Encoder` on the dashboard with the wheel manually squared up, then enter the negative of that raw value (config.yagsl.com does this arithmetic for you).
* `absoluteEncoderInverted`: should rarely, if ever, need to be `true`.
* For `_can` encoders, set `id` and `canbus`. `canbus` is `""` for the SystemCore bus `can_s0`, or the bus number, such as `"1"` for `can_s1`; a CANcoder also takes a CANivore's name. See [Control system and CAN buses](../json-schema/#control-system-and-can-buses).
* For encoders wired to a SystemCore SmartIO port (`_dio` and `_analog`), set `channel` to the SmartIO channel instead of `id`/`canbus`.
* For `_attached` encoders, `id`, `channel` and `canbus` are all unused: the encoder is read through the module's angle motor controller.

{% hint style="warning" %}
If a module spins continuously without settling, try inverting its angle motor before suspecting the absolute encoder.
{% endhint %}

## Supported absolute encoder types

| Device                                                                                                      | `type`                     | Connection                                                                        |
| ----------------------------------------------------------------------------------------------------------- | -------------------------- | --------------------------------------------------------------------------------- |
| [REV Throughbore](https://www.revrobotics.com/rev-11-1271/)                                                 | `revthroughbore_attached`  | Plugged into the angle motor controller's duty-cycle port                         |
| REV Throughbore                                                                                             | `revthroughbore_dio`       | SystemCore SmartIO (duty cycle)                                                   |
| CTRE [CANcoder](https://pro.docs.ctr-electronics.com/en/latest/docs/hardware-reference/cancoder/index.html) | `cancoder_can`             | CAN; bus number or CANivore name. Requires Phoenix 6.                             |
| [Canandmag](https://docs.reduxrobotics.com/canandmag/getting-started)                                       | `canandmag_attached`       | Plugged into the angle motor controller's duty-cycle port                         |
| Canandmag                                                                                                   | `canandmag_dio`            | SystemCore SmartIO (duty cycle)                                                   |
| Canandmag                                                                                                   | `canandmag_can`            | CAN; bus number or ReduxLib bus string. Requires ReduxLib.                        |
| [SRX Mag Encoder](https://store.ctr-electronics.com/srx-mag-encoder/)                                       | `srxmag_attached`          | Plugged into the angle motor controller's duty-cycle port                         |
| SRX Mag Encoder                                                                                             | `srxmag_analog`            | SystemCore SmartIO (analog input)                                                 |
| [AndyMark Hex Bore](https://www.andymark.com/)                                                              | `andymarkhexbore_attached` | Plugged into the angle motor controller's duty-cycle port                         |
| AndyMark Hex Bore                                                                                           | `andymarkhexbore_dio`      | SystemCore SmartIO (duty cycle)                                                   |
| AndyMark Hex Bore                                                                                           | `andymarkhexbore_analog`   | SystemCore SmartIO (analog input)                                                 |
| AndyMark Hex Bore                                                                                           | `andymarkhexbore_can`      | CAN. _Not available in the 2027 builds yet, see below._                           |
| Spline Encoder                                                                                              | `splineencoder_can`        | CAN (REV); bus number or `CANPort` name only. Requires REVLib.                    |
| Thrifty absolute encoder                                                                                    | `thrifty_attached`         | Plugged into a Thrifty Nova angle motor controller's data port (10 pin encoder)   |
| Thrifty absolute encoder                                                                                    | `thrifty_analog`           | SystemCore SmartIO (analog input)                                                 |
| Generic analog (5V)                                                                                         | `analog5v_attached`        | Plugged into a SparkMAX/SparkFlex angle motor controller's analog port, 5 V range |
| Generic analog                                                                                              | `analog_attached`          | Plugged into the angle motor controller's analog port (3.3 V range on a Spark)    |
| Generic duty-cycle                                                                                          | `dutycycle_attached`       | Plugged into the angle motor controller's duty-cycle port                         |

{% hint style="warning" %}
`andymarkhexbore_can` is still accepted by the schema, but AndyMark's library has no `2027_alpha7` release yet, so the YAGSL 2027 artifacts are built without its device class and creating the drive with it fails. Use `andymarkhexbore_dio`, `andymarkhexbore_analog` or `andymarkhexbore_attached` for now.
{% endhint %}

### Attached encoders

The `_attached` connection means the encoder is wired directly into the module's **angle** motor controller rather than a SmartIO port or the CAN bus. YAGSL reads it back through that controller's vendor API. This replaces the old `SwerveDrive.pushOffsetsToEncoders()` mechanism from pre-2026.8.05 releases.

Which `_attached` types work depends on the angle motor controller:

| Angle motor controller | Accepted `_attached` encoders                                                                                                                                                                                          |
| ---------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| SparkMAX / SparkFlex   | `revthroughbore`, `canandmag`, `srxmag`, `andymarkhexbore` and `dutycycle` (all read from the duty-cycle absolute encoder input), `analog` (3.3 V) and `analog5v` (5 V) from the analog input. Not `thrifty_attached`. |
| Thrifty Nova           | `thrifty`, `revthroughbore`, `canandmag`, `srxmag`, `andymarkhexbore`, `dutycycle` and `analog`. The Nova is told which encoder is on its data port from the type string. Not `analog5v_attached`.                     |
| TalonFX / TalonFXS     | None. Use a `_can`, `_dio` or `_analog` encoder instead.                                                                                                                                                               |

***

### General absolute encoder tips (apply regardless of device)

* Set the offset with the wheel physically squared to the robot, not by eyeballing degrees on the dashboard from an arbitrary position.
* Re-check offsets any time a module is disassembled. Even a "put it back the same way" rebuild can shift the magnet by a few degrees, and swerve is unforgiving of small offset errors at low speed.
* If you swap a module between corners, its encoder wiring moves with it. The offset value does not automatically follow. See [Verify Module Locations](../../fix-a-problem/verify-module-locations.md).
