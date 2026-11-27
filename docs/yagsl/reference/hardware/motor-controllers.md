---
description: >-
  Supported motor controller/motor combinations for module drive and angle
  motors
---

# Motor Controllers

{% hint style="danger" %}
**TalonSRX and brushed SparkMAX are no longer supported.** The pre-2026.8.05 `talonsrx` and `sparkmax_brushed` types have been removed. YAGSL now only drives brushless motors through YAMS's `SmartMotorController` abstraction. See [Schema Changes](../../upgrading/schema-changes.md).
{% endhint %}

## Motor Checklist

* [ ] All motors spin counterclockwise positive (CCW+); document any inversions required to achieve that.
* [ ] Motor is updated to the latest firmware.
* [ ] Motor has a unique CAN ID that matches its module JSON.
* [ ] Wheels are aligned forwards with all bevels facing the same way before first enable.
* [ ] PID is tuned. See [Tune PIDF Gains](../../tune-and-extend/tune-pidf-gains.md).

## Configuring a motor

Each module JSON (`frontleft.json`, etc.) has a `drive` and an `angle` motor, both `DeviceJson` objects of the form `"<controller>_<motor>"`:

```json
{
  "drive": { "type": "sparkflex_vortex", "id": 5, "canbus": "" },
  "angle":  { "type": "sparkmax_neo550", "id": 6, "canbus": "" }
}
```

The `<motor>` suffix tells YAGSL which `DCMotor` model to use for feedforward and simulation characterization (free speed, stall torque, etc.). It must match the physical motor attached to that controller.

`canbus` is the CAN bus the controller is on: `""` for the SystemCore bus `can_s0`, or the bus number, such as `"1"` for `can_s1`. See [Control system and CAN buses](../json-schema/#control-system-and-can-buses).

## Supported controller × motor combinations

| Controller                                                    | `type` values                                                                                                     |
| ------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| [TalonFX](motor-controllers.md#talonfx-kraken-x60-kraken-x44) | `talonfx_krakenx44`, `talonfx_krakenx60`, `talonfx_falcon`, `talonfx_falcon500`                                   |
| [TalonFXS](motor-controllers.md#talonfxs)                     | `talonfxs_neo`, `talonfxs_neo2`, `talonfxs_neo550`, `talonfxs_vortex`, `talonfxs_pulsar`, `talonfxs_minion`       |
| [SparkMAX](motor-controllers.md#sparkmax)                     | `sparkmax_neo`, `sparkmax_neo2`, `sparkmax_neo550`, `sparkmax_vortex`, `sparkmax_pulsar`, `sparkmax_minion`       |
| [SparkFlex](motor-controllers.md#sparkflex)                   | `sparkflex_neo`, `sparkflex_neo2`, `sparkflex_neo550`, `sparkflex_vortex`, `sparkflex_pulsar`, `sparkflex_minion` |
| [Nova (ThriftyBot)](motor-controllers.md#thrifty-nova)        | `nova_neo`, `nova_neo2`, `nova_neo550`, `nova_vortex`, `nova_pulsar`, `nova_minion`                               |

{% hint style="warning" %}
This is the full list the JSON schema accepts for feedforward/simulation purposes. It is **not** a claim that every combination is physically wireable. Respect real hardware constraints: a TalonFX is built into Kraken X44/X60 and Falcon 500 motors, and the other controllers only drive motors their connectors and firmware support. Pick the row for your actual controller, and the `<motor>` suffix that matches what's physically bolted to it.
{% endhint %}

Each controller family needs its vendor library installed in your robot project (Phoenix 6 for `talonfx_*`/`talonfxs_*`, REVLib for `sparkmax_*`/`sparkflex_*`, ThriftyLib for `nova_*`). YAGSL loads it by reflection only when your config uses that controller. See [Vendordep Installation](../vendordep-installation.md).

Every controller takes a bus number in `canbus`. Only CTRE controllers (`talonfx_*`, `talonfxs_*`) also take a name, for a CANivore. REV and Thrifty controllers reject anything other than `""` or a bus number.

{% hint style="warning" %}
**CTRE default bus changed in 2027.** A TalonFX or TalonFXS with `"canbus": ""` is now on `can_s0`, like every other vendor's devices. It used to end up on `can_s2`; if yours are wired to `can_s2`, set `"canbus": "2"`.
{% endhint %}

## TalonFX (Kraken X60 / Kraken X44)

TalonFX is the controller for Kraken X44/X60 and (legacy) Falcon 500 motors. Commonly used as a drive motor for its power and efficiency versus REV NEOs.

* [Kraken product page](https://wcproducts.com/products/kraken)
* Upgradeable/configurable via [Tuner X](https://v6.docs.ctr-electronics.com/en/latest/docs/tuner/index.html). Watch the LED status code when debugging. Settings changed in Tuner X are overwritten on startup by YAGSL.

{% hint style="warning" %}
If running Falcon 500 v2 (effectively discontinued), apply the [Loctite fix](https://content.vexrobotics.com/vexpro/Falcon/217-6515-753-Falcon500-V2-Upgrade.pdf).
{% endhint %}

## TalonFXS

TalonFXS is CTRE's controller for third-party/non-Kraken brushless motors (NEO, NEO Vortex, Minion, Pulsar) wired through CTRE's ecosystem, with the same Tuner X tooling and CAN/CANivore support as TalonFX.

## SparkMAX

REV SparkMAX, brushless mode (NEO / NEO 550 / NEO Vortex / Minion / Pulsar via the appropriate `<motor>` suffix).

* [Product page](https://www.revrobotics.com/rev-11-2158/)
* Requires the [REV Hardware Client](https://docs.revrobotics.com/rev-hardware-client/gs/install) to test PID, run at a set percentage, and update firmware.

{% hint style="warning" %}
The SparkMAX must be disconnected from the CAN bus **on startup** to be recognized by the REV Hardware Client.
{% endhint %}

To tune PID directly on the controller: open REV Hardware Client → select the SparkMAX → Telemetry tab → set to position control while adjusting gains in the left pane.

**Checklist**

* [ ] Unique CAN ID.
* [ ] Latest firmware.
* [ ] Rotates CCW+ (invert programmatically if not).
* [ ] PID tuned for one drive motor and one angle motor before wiring the rest.

## SparkFlex

REV SparkFlex, brushless mode (same motor suffixes as SparkMAX, primarily used with NEO Vortex).

* [Product page](https://www.revrobotics.com/next-generation-spark-neo/)
* Same [REV Hardware Client](https://docs.revrobotics.com/rev-hardware-client-2) workflow and startup-disconnect caveat as SparkMAX.

Both Spark controllers can read an absolute encoder plugged into their data port (the `_attached` encoder types): see [Absolute Encoders](absolute-encoders.md#attached-encoders). `canbus` must be `""` or a bus number; port names such as `"can_s1"` are no longer accepted.

## Thrifty Nova

ThriftyBot Nova, supported again in 2027 through YAMS's `NovaWrapper`. Requires the **ThriftyLib** vendordep.

* `type` is `nova_<motor>`. YAGSL configures the Nova's own motor type from the suffix: `MINION` for `nova_minion`, `PULSAR` for `nova_pulsar`, and `NEO` for every other suffix.
* `canbus` must be `""` (for `can_s0`) or a bus number. Anything else is rejected with an error that lists the bus numbers.
* A Nova angle motor can read an absolute encoder plugged into its data port, including the Thrifty 10 pin encoder (`thrifty_attached`). It does not support `analog5v_attached`. See [Absolute Encoders](absolute-encoders.md#attached-encoders).

**Checklist**

* [ ] Unique CAN ID.
* [ ] Latest firmware.
* [ ] Rotates CCW+ (invert programmatically if not).
* [ ] ThriftyLib vendordep installed.

***

### General motor tips (apply regardless of controller)

* Confirm CCW+ rotation on the bench before mounting. See [When to Invert](../../fix-a-problem/determine-inversion.md).
* Tune one drive motor and one angle motor fully before copying gains to the other three modules; see [Tune PIDF Gains](../../tune-and-extend/tune-pidf-gains.md).
* Current limits and gearing live in `physicalproperties.json` (or a per-module `gearing` override). See [physicalproperties.json Reference](../json-schema/physicalproperties-json.md).
