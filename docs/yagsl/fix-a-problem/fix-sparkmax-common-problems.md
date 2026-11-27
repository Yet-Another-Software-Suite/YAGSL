---
description: These usually show up on MAXSwerve robots.
---

# Fix common SparkMAX/SparkFlex problems

## NEOs can damage SparkMAX controllers

When a NEO heats up enough from being stalled too long, it will short positive to the sensor cable, sending very high current through the low-current sensor channel and melting the SparkMax PCB. This WILL kill your SparkMAX, and every SparkMAX plugged into your killer NEO.

This can also kill your laptop if you plug into a powered-on SparkMAX, because the short can extend to the USB port.

{% embed url="https://docs.google.com/presentation/d/1uM_uPD5HjzGwYUOmzZOmmgb1-KANiTTKI3o9aIJlPcc/edit?slide=id.p#slide=id.p" %}

## SparkMAX absolute encoder boards

The [SparkMAX Absolute Encoder board](https://www.revrobotics.com/rev-11-3326/) should be zip-tied and hot-glued to the SparkMAX for redundancy and to prevent it from coming loose.

### Throughbore connection to the absolute encoder board

The connector between the [SparkMax Absolute Encoder board](https://www.revrobotics.com/rev-11-3326/) and the [REV Throughbore](https://www.revrobotics.com/rev-11-1271/) should be hot-glued down on **BOTH** connection points, because these can come loose.

The wire should also be tensioned just right. If it's over-tensioned, the wire might not be fully seated in the connector, and the data could be incomplete, corrupted, or unavailable.

## Absolute encoder attached via the SparkMax's duty-cycle port

When you use an absolute encoder wired to the SparkMax through its Absolute Encoder board, give it an `_attached` type in the module JSON, such as `revthroughbore_attached` (see the [absolute encoder reference](../reference/hardware/absolute-encoders.md) for the exact type string for your device). YAGSL then reads it through the angle motor's SparkMax instead of creating a separate device, so `id` and `channel` are not used.

```json
{
  "absoluteEncoder": { "type": "revthroughbore_attached", "id": 1, "channel": 0, "canbus": "" }
}
```

{% hint style="success" %}
When the absolute encoder is attached to the azimuth motor controller (e.g. a REV Throughbore wired into a SparkMax), YAGSL automatically wires it up as the SparkMax's closed-loop feedback sensor and applies `absoluteEncoderOffset` as its zero offset for you. There is no manual "set the `factor` to `360`" workaround to remember, and no `useExternalFeedbackSensor()`/`useInternalFeedbackSensor()` calls to make in your own code. Just set `absoluteEncoderOffset` in the module JSON (or via [**config.yagsl.com**](https://config.yagsl.com)) and the parser handles the rest.
{% endhint %}

{% hint style="info" %}
If you're migrating an old config that hand-set `physicalproperties.json`'s `factor` to `360` as a workaround, that field no longer exists. See the [schema changes reference](../upgrading/schema-changes.md).
{% endhint %}

## SPARK devices not found after moving to the SystemCore

Two things changed for REV hardware in YAGSL 2027:

* **REVLib is no longer pulled in by the YAGSL vendordep.** YAGSL only requires YAMS and loads REVLib by reflection when your JSON uses a `sparkmax_*`/`sparkflex_*` device, so install the REVLib vendordep yourself or the parser fails at startup when it tries to build the first SPARK.
* **`canbus` must be empty or a bus number.** `""` is SystemCore bus `can_s0`, `"1"` is `can_s1`, and so on. A name (such as a CANivore's) is rejected with an `IllegalArgumentException` saying REV devices only use the SystemCore and Motioncore CAN buses. See [How to put devices on a different CAN bus](../tune-and-extend/choose-a-can-bus.md).

## Status frame error

Usually, a status frame error means your connection to the REV Throughbore is incomplete somehow, or the sensor cable from your motor is loose or broken.

If you see this, check **ALL SENSOR CABLES GOING INTO THAT SPARKMAX**.
