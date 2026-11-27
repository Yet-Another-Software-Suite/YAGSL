---
description: Supported gyroscope/IMU types for swervedrive.json's gyro field
---

# Gyroscopes

{% hint style="danger" %}
**Hardware support narrowed in the 2026.8.05 rewrite.** YAGSL only builds CAN gyros and, since 2027, the IMU inside the SystemCore. The SPI/I2C/USB-serial NavX (original NavX2 `navx`/`navx_spi`/ `navx_i2c`/`navx_mxp_serial`/`navx_usb`), the original CTRE **Pigeon** (gen 1), and the SPI gyros (**ADXRS450**, **ADIS16448**, **ADIS16470**) are **not supported** by the parser. If your robot uses a gyro that isn't in the table below, see [Schema Changes](../../upgrading/schema-changes.md) and use the [`custom` gyro type](gyroscopes.md#custom-gyro) to wire it up yourself.
{% endhint %}

## Gyroscope Checklist

* [ ] Gyroscope readings increase when rotated counter-clockwise (CCW+).
* [ ] Yaw reading is the robot heading.
* [ ] Gyroscope `0°` is the desired robot "front".

## Configuring the gyro

In `swervedrive.json` the gyro is one object plus two drive-wide settings:

```json
{
  "gyro": {
    "type": "pigeon2_can",
    "id": 13,
    "canbus": "canivore"
  },
  "gyroAxis": "yaw",
  "gyroInvert": true,
  "modules": ["frontleft.json", "frontright.json", "backleft.json", "backright.json"]
}
```

* `gyro.type`: one of the supported types below, formatted `vendor_connection`, or `custom` to configure the gyro yourself (see [Custom Gyro](gyroscopes.md#custom-gyro)).
* `gyro.id`: CAN ID of the device (ignored where not applicable).
* `gyro.canbus`: the CAN bus the device is on. Use `""` for the SystemCore bus `can_s0`, or the bus number, such as `"1"` for `can_s1`. A Pigeon 2 also takes a CANivore's name. See [Control system and CAN buses](../json-schema/#control-system-and-can-buses).
* `gyroAxis`: which axis of the sensor points up through the robot and is read as the heading: `yaw` (default), `pitch`, or `roll`. Only change this if the sensor is mounted on an edge or angle.
* `gyroInvert`: invert the heading reading. If your robot spins out of control with no controller input, invert this.

The parser hands YAMS the gyro's full 3D attitude (a `Supplier<Rotation3d>`) untouched. YAMS then applies `gyroAxis` (`SwerveDriveConfig.withGyroHeadingAxis`) and `gyroInvert` (`SwerveDriveConfig.withGyroInverted`) in one place, so no gyro is inverted twice. Read the result in robot code with `SwerveDrive.getGyroRotation3d()`; the old `getGyroAngle()` is gone.

{% hint style="warning" %}
**CTRE default bus changed in 2027.** A Pigeon 2 with `"canbus": ""` is now on `can_s0`, like every other vendor's devices. It used to end up on `can_s2`; if yours is wired to `can_s2`, set `"canbus": "2"`.
{% endhint %}

## Supported gyroscope types

| Device                                         | `type`                | Communication                                                            | Vendor library           |
| ---------------------------------------------- | --------------------- | ------------------------------------------------------------------------ | ------------------------ |
| [Pigeon 2](gyroscopes.md#pigeon-2)             | `pigeon2_can`         | CAN; bus number or CANivore name                                         | Phoenix 6                |
| [Canandgyro](gyroscopes.md#canandgyro)         | `canandgyro_can`      | CAN; bus number or ReduxLib bus string                                   | ReduxLib                 |
| [SystemCore IMU](gyroscopes.md#systemcore-imu) | `systemcore_internal` | Built into the SystemCore; `id` and `canbus` are ignored                 | none (part of WPILib)    |
| [NavX3-CAN](gyroscopes.md#navx3-can)           | `navx3_can`           | CAN; bus number or `CANPort` name                                        | StudicaLib               |
| [NavX3 over USB](gyroscopes.md#navx3-over-usb) | `navx3_usb`           | A SystemCore USB port, given by `channel`; `id` and `canbus` are ignored | StudicaLib               |
| [Custom](gyroscopes.md#custom-gyro)            | `custom`              | _any_: you construct and supply the gyro yourself                        | whatever your gyro needs |

The vendor library is loaded by reflection only when your config uses that gyro, so install the one for the gyro you have. See [Vendordep Installation](../vendordep-installation.md).

If you need a gyro not on this list, use the `custom` type below.

## Custom Gyro

{% hint style="info" %}
This is the reference summary. For a full walkthrough see [How to use a custom gyro](../../tune-and-extend/use-a-custom-gyro.md).
{% endhint %}

Set `gyro.type` to `custom` and YAGSL's parser skips gyro configuration entirely: it never calls `SwerveDriveConfig.withGyro()`/`withGyroHeadingAxis()`/`withGyroInverted()`, and `gyroAxis`/`gyroInvert` in `swervedrive.json` are ignored. You're expected to call them yourself on the `SwerveDriveConfig` you pass into `SwerveParser.createSwerveDrive(...)`. Everything else in the config (modules, motor controllers, encoders) is still built normally from the JSON.

`withGyro()` takes a `Supplier<Rotation3d>`: the gyro's full attitude, not just a heading angle. Most vendor gyros and WPILib's `OnboardIMU` have a `getRotation3d()` you can pass directly. `withRotation(...)` is the same method under another name.

This is the escape hatch for any gyro the parser doesn't build for you. The example below uses it for a SystemCore that is mounted vertically, since `systemcore_internal` always assumes a SystemCore mounted flat.

```json
{
  "gyro": { "type": "custom", "id": 0, "canbus": "" },
  "gyroAxis": "yaw",
  "gyroInvert": false,
  "modules": ["frontleft.json", "frontright.json", "backleft.json", "backright.json"]
}
```

{% tabs %}
{% tab title="Commands v2" %}
```java
import org.wpilib.hardware.imu.OnboardIMU;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;
import swervelib.commands2.SwerveParser;
import yams.commands2.config.SwerveDriveConfig;
import yams.core.mechanisms.config.enums.GyroAxis;
import yams.core.telemetry.enums.TelemetryVerbosity;

// SystemCore mounted vertically, with its long edge parallel to the ground.
private final OnboardIMU gyro = new OnboardIMU(MountOrientation.LANDSCAPE);

public SwerveDriveSubsystem() {
  var cfg = new SwerveDriveConfig()
      .withSubsystem(this)
      .withTelemetry("swerve", TelemetryVerbosity.HIGH)
      .withGyro(gyro::getRotation3d)
      // gyroAxis and gyroInvert are ignored for a "custom" gyro, so set them here.
      .withGyroHeadingAxis(GyroAxis.YAW)
      .withGyroInverted(false);

  SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
  drive = SwerveParser.createSwerveDrive(cfg);
}
```
{% endtab %}

{% tab title="Commands v3" %}
```java
import org.wpilib.hardware.imu.OnboardIMU;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;
import swervelib.commands3.SwerveParser;
import yams.commands3.config.SwerveDriveConfig;
import yams.core.mechanisms.config.enums.GyroAxis;
import yams.core.telemetry.enums.TelemetryVerbosity;

// SystemCore mounted vertically, with its long edge parallel to the ground.
private final OnboardIMU gyro = new OnboardIMU(MountOrientation.LANDSCAPE);

public SwerveDriveMechanism() {
  var cfg = new SwerveDriveConfig()
      .withMechanism(this)
      .withTelemetry("swerve", TelemetryVerbosity.HIGH)
      .withGyro(gyro::getRotation3d)
      // gyroAxis and gyroInvert are ignored for a "custom" gyro, so set them here.
      .withGyroHeadingAxis(GyroAxis.YAW)
      .withGyroInverted(false);

  SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
  drive = SwerveParser.createSwerveDrive(cfg);
}
```
{% endtab %}
{% endtabs %}

{% hint style="warning" %}
Because the parser never calls `withGyroInverted()` for a `custom` gyro, a gyro that reads clockwise positive needs `.withGyroInverted(true)` on your own config. Don't also negate the attitude inside the supplier, or the heading is inverted twice.
{% endhint %}

## SystemCore IMU

`systemcore_internal` uses the IMU built into the SystemCore through WPILib's `org.wpilib.hardware.imu.OnboardIMU`. It needs no vendor library and no wiring, and `id` and `canbus` are ignored.

```json
"gyro": { "type": "systemcore_internal", "id": 0, "canbus": "" }
```

**Checklist**

* [ ] SystemCore is mounted **flat** (parallel to the ground). The parser always creates the IMU with `MountOrientation.FLAT`.
* [ ] SystemCore is mounted rigidly and as close to the robot's center as practical.

If your SystemCore is mounted vertically (`MountOrientation.LANDSCAPE` or `PORTRAIT`), create the `OnboardIMU` yourself with a [custom gyro](gyroscopes.md#custom-gyro), as shown above.

## NavX3-CAN

{% hint style="info" %}
NavX3-CAN supports CAN 2.0 and has CAN-FD capability. See Studica's notes for CAN-FD requirements.
{% endhint %}

* [Studica product page](https://www.studica.co/navx3-can-imu)
* [Studica NavX releases, firmware tools, and vendordeps](https://github.com/Studica-Robotics/NavX)

**Checklist**

* [ ] Install the **StudicaLib** vendordep from `https://dev.studica.com/maven/release/2027/json/StudicaLib.json` (do not install both `Studica` and `StudicaLib`). YAGSL 2026.11.06 and later work with StudicaLib 2027.0.0-alpha-1.
* [ ] Update NavX3-CAN firmware to **5.0.4+** using Studica Hardware Manager.
* [ ] Use Studica Hardware Manager to find the CAN ID (often ships as `0`) and enter it in your config.

Set `canbus` to the SystemCore CAN bus the NavX3 is on: empty for `can_s0`, or the bus number, such as `"1"` for `can_s1`. A CANivore name isn't accepted. Until its first reading arrives, or while it is disconnected, the NavX3 reports its last heading (zero at startup).

NavX3-CAN ships factory-calibrated. For higher accuracy, re-calibrate with Studica Hardware Manager, which auto-detects sensor orientation during calibration. Connect CAN-H/CAN-L to the robot bus and make sure the bus is properly terminated.

## NavX3 over USB

A NavX3 plugged into one of the SystemCore's USB ports instead of the CAN bus. YAGSL creates it with StudicaLib's `Navx.usb(UsbPort)`. Needs a YAGSL release newer than 2026.11.06.

**Checklist**

* [ ] Install the **StudicaLib** vendordep, as for [NavX3-CAN](gyroscopes.md#navx3-can).
* [ ] Plug the NavX3 into a SystemCore USB port and note which one.

Set `channel` to that port: `0` for USB0 to `3` for USB3. It defaults to `0`. A port that doesn't exist fails when the drive is created. `id` and `canbus` are ignored.

```json
"gyro": { "type": "navx3_usb", "id": 0, "canbus": "", "channel": 1 }
```

In the [config generator](https://config.yagsl.com), pick **USB** as the gyro's connection, then the USB port. Like the NavX3-CAN, it reports its last heading (zero at startup) until its first reading arrives or while it is disconnected.

## Pigeon 2

* [Product page](https://store.ctr-electronics.com/pigeon-2/)
* [Hardware reference](https://pro.docs.ctr-electronics.com/en/latest/docs/hardware-reference/pigeon2/index.html)
* Upgradeable via [Tuner X](https://v6.docs.ctr-electronics.com/en/latest/docs/tuner/index.html). Pay attention to the LED status code when debugging.

**Checklist**

* [ ] Yaw increments counter-clockwise positive.
* [ ] Pigeon 2 is mounted as close to the robot's center as possible.
* [ ] Updated to the latest firmware, on a unique CAN ID.
* [ ] Calibrated once installed on the robot.
* [ ] **Phoenix 6** vendordep installed.

Communicates over CAN. Set `canbus` to `""` for `can_s0` or to the bus number of another SystemCore bus. It can also be paired with a [CANivore](https://store.ctr-electronics.com/canivore/): [set a CANivore name](https://pro.docs.ctr-electronics.com/en/latest/docs/canivore/canivore-setup.html) and use it as `canbus`.

## Canandgyro

* [Getting started](https://docs.reduxrobotics.com/canandgyro/getting-started)
* CAN only; does not support CANivore.
* Requires the **ReduxLib** vendordep.
* `canbus` is `""` for `can_s0` or a bus number. Any other value is handed to ReduxLib as a bus string, such as `"socketcan:can_s1"`.
* If you are coming from an older YAGSL version where you had to flip `gyroInvert` to work around the Canandgyro being inverted twice, re-check it: the inversion is now only applied once, by YAMS.

***

### General gyro tips (still apply regardless of device)

* Mount the gyro as close to the robot's center of rotation as practical. Off-center mounting couples translational vibration into the heading reading.
* Always keep the robot still for a few seconds after power-on if your device does an on-the-fly calibration; moving during that window produces a bad heading for the rest of the match.
* If the robot's heading drifts over a match, see [Swerve Drive Drift Causes](../../fix-a-problem/swerve-drift-causes.md).
