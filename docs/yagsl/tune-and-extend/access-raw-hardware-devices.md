---
description: >-
  Get the actual vendor motor controller/encoder/gyro objects SwerveParser
  built, for configuration that isn't exposed through SmartMotorController.
---

# Access raw hardware devices

{% hint style="info" %}
Most robot code never needs this page. `SwerveDrive`, `SwerveModule`, and YAMS's `SmartMotorController` cover driving, telemetry, and the vast majority of per-motor configuration. Reach for `createSwerveDriveDevices` when you need to call a vendor-specific method (a CTRE `TalonFXConfigurator` option, a REV `SparkMax` signal, a `CANcoder`-specific setting) that `SmartMotorController` doesn't expose, or when you need real hardware, not a wrapper, for something like gyro angular rates (see the example below).
{% endhint %}

`SwerveParser.createSwerveDrive(config)` hands you back a fully-built `SwerveDrive`. Everything underneath it (drive/azimuth motor controllers, absolute encoders, the gyro) is wrapped and effectively private. `SwerveParser.createSwerveDriveDevices(config)` builds the exact same hardware, but also gives you the raw vendor objects it created along the way, alongside the `SwerveDrive` itself.

{% stepper %}
{% step %}
### Call `createSwerveDriveDevices` instead of `createSwerveDrive`

{% tabs %}
{% tab title="Commands v2" %}
```java
import swervelib.commands2.SwerveParser;
import swervelib.core.parser.SwerveParser.SwerveDriveDevices;
import yams.commands2.swerve.SwerveDrive;

SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
SwerveDriveDevices<SwerveDrive> devices = SwerveParser.createSwerveDriveDevices(cfg);

drive = devices.swerveDrive();
```
{% endtab %}

{% tab title="Commands v3" %}
```java
import swervelib.commands3.SwerveParser;
import swervelib.core.parser.SwerveParser.SwerveDriveDevices;
import yams.commands3.swerve.SwerveDrive;

SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
SwerveDriveDevices<SwerveDrive> devices = SwerveParser.createSwerveDriveDevices(cfg);

drive = devices.swerveDrive();
```
{% endtab %}
{% endtabs %}

`SwerveDriveDevices` and `SwerveModuleDevices` are nested in the command framework agnostic `swervelib.core.parser.SwerveParser`; the `SwerveParser` you call is still the one from `swervelib.commands2` or `swervelib.commands3`, matching your vendordep.

{% hint style="danger" %}
Call **either** `createSwerveDrive(...)` **or** `createSwerveDriveDevices(...)` for a given parsed directory, not both. Each call builds the hardware from scratch, so calling both would construct every motor controller, encoder, and gyro twice (two CAN objects fighting over the same ID).
{% endhint %}
{% endstep %}

{% step %}
### Read the devices back out

`SwerveDriveDevices<D>` is a record with three components:

| Component     | Type                     | Notes                                                                                                                                                               |
| ------------- | ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `swerveDrive` | `D` (your `SwerveDrive`) | The same object `createSwerveDrive(...)` would have returned: `yams.commands2.swerve.SwerveDrive` or `yams.commands3.swerve.SwerveDrive`. Use it exactly as normal. |
| `gyro`        | `Object`                 | The raw gyro device, or `null` if `gyro.type` is `"custom"`. See the table below.                                                                                   |
| `modules`     | `SwerveModuleDevices[]`  | One entry per module, in the same order as the `modules` array in `swervedrive.json`.                                                                               |

The gyro object depends on `gyro.type`:

| `gyro.type`           | Raw device                                      |
| --------------------- | ----------------------------------------------- |
| `pigeon2_can`         | CTRE `com.ctre.phoenix6.hardware.Pigeon2`       |
| `canandgyro_can`      | Redux `com.reduxrobotics.canandgyro.Canandgyro` |
| `navx3_can`           | Studica `com.studica.frc.Navx`                  |
| `systemcore_internal` | WPILib `org.wpilib.hardware.imu.OnboardIMU`     |
| `custom`              | `null`                                          |

Each `SwerveModuleDevices` is in turn:

| Component         | Type     | Notes                                                                                                                                                                                                                                                                                                                                                                                                    |
| ----------------- | -------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `drive`           | `Object` | Raw drive motor controller (a REV `SparkMax`/`SparkFlex`, a CTRE `TalonFX`/`TalonFXS`, or a Thrifty `Nova`).                                                                                                                                                                                                                                                                                             |
| `azimuth`         | `Object` | Raw azimuth/angle motor controller.                                                                                                                                                                                                                                                                                                                                                                      |
| `absoluteEncoder` | `Object` | Raw absolute encoder device. For a separate device, that device (`CANcoder`, `Canandmag`, WPILib `AnalogEncoder`/`DutyCycleEncoder`, ...). For an `_attached` encoder, the vendor's accessor for it rather than a second device: the SPARK's absolute encoder or analog sensor object on REV, and on a Nova just the `FeedbackSensorType.ABS` marker (read the encoder through the `Nova` in `azimuth`). |

Cast each `Object` to whatever vendor type you know it to be from your `swervedrive.json`/module JSON `type` strings (see [Device Type Strings](../reference/json-schema/device-types.md)):

```java
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import swervelib.core.parser.SwerveParser.SwerveModuleDevices;

SwerveModuleDevices frontLeft = devices.modules()[0];

TalonFX driveMotor = (TalonFX) frontLeft.drive();
CANcoder absoluteEncoder = (CANcoder) frontLeft.absoluteEncoder();

driveMotor.getConfigurator().apply(myCustomTalonConfig);
```

{% hint style="warning" %}
Nothing enforces that `modules[i]` is the type you expect. Mixed-vendor swerve drives (a CTRE drive motor with a REV angle motor, say) are supported by YAGSL, so a blind cast across every module in a loop can throw `ClassCastException` if the vendors aren't actually uniform. Cast per-module against what that module's JSON actually specifies.
{% endhint %}

{% hint style="info" %}
YAGSL only loads a vendor's classes when your JSON uses that vendor's devices, but your own cast is ordinary Java: the vendor library you cast to (Phoenix 6, REVLib, ReduxLib, ThriftyLib, ...) has to be installed as a vendordep in your project.
{% endhint %}
{% endstep %}

{% step %}
### Example: gyro angular rates for vision

`SwerveDrive.getGyroRotation3d()` already gives you the robot's full attitude (roll, pitch and yaw, with `gyroAxis` and `gyroInvert` applied), so you don't need the raw device for orientation. What YAMS doesn't expose is the gyro's angular _rates_. If you'd like to feed a vision coprocessor real roll, pitch and yaw rates (for example for MegaTag2), you need the raw device:

```java
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.Pigeon2;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.units.measure.AngularVelocity;

private Pigeon2 gyro;
private StatusSignal<AngularVelocity> gyroX;
private StatusSignal<AngularVelocity> gyroY;
private StatusSignal<AngularVelocity> gyroZ;

public SwerveDriveSubsystem() {
  // ... parse and build cfg as usual ...
  SwerveDriveDevices<SwerveDrive> devices = SwerveParser.createSwerveDriveDevices(cfg);
  drive = devices.swerveDrive();

  gyro = (Pigeon2) devices.gyro();
  gyroX = gyro.getAngularVelocityXDevice();
  gyroY = gyro.getAngularVelocityYDevice();
  gyroZ = gyro.getAngularVelocityZDevice();
}

public Rotation3d getGyroRotation3d() {
  return drive.getGyroRotation3d(); // robot attitude, already corrected for axis and inversion
}
```

Then hand the attitude and rates to your vision coprocessor's orientation input. Whether the extra roll/pitch data actually improves your pose estimate depends on your camera mount and how noisy that IMU data is; it isn't automatically better than yaw-only, just more complete.

{% hint style="info" %}
This pattern is vendor-specific: per-axis `StatusSignal`s are CTRE Pigeon2 methods. A different gyro's raw device exposes angular velocity differently, or not at all, in which case the attitude from `SwerveDrive.getGyroRotation3d()` is what you have, and that's fine: yaw is all MegaTag2 actually requires.
{% endhint %}

{% hint style="warning" %}
The raw device's own readings (`gyro.getRotation3d()`, `gyro.getYaw()`, ...) are **not** corrected for `gyroAxis`, `gyroInvert` or a zeroed heading, and they don't follow the simulated robot in simulation. Prefer `drive.getGyroRotation3d()` for orientation and keep the raw device for what only it can give you.
{% endhint %}
{% endstep %}

{% step %}
### Handle a `custom` gyro

If `swervedrive.json`'s `gyro.type` is `"custom"`, the parser never builds a gyro device at all, see [How to use a custom gyro](use-a-custom-gyro.md), so `devices.gyro()` is `null`. Your own gyro object (the one you constructed to call `SwerveDriveConfig.withGyro(...)`) is already in scope wherever you built it; there's nothing to retrieve here.
{% endstep %}
{% endstepper %}

## Why `Object`?

`SwerveModuleDevices`/`SwerveDriveDevices` are deliberately vendor-agnostic. YAGSL supports mixing CTRE, REV, ThriftyBot, AndyMark, and Redux hardware across modules (see [Supported Hardware](../reference/hardware/)), and loads each vendor's library by reflection only when your JSON uses it, so there's no single common supertype to return instead of `Object`. The cast is the price of that flexibility.
