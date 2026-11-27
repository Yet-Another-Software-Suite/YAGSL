---
description: >-
  Configure a gyro YAGSL's parser doesn't build for you by handing YAMS a
  Rotation3d supplier yourself.
---

# Use a custom gyro

{% hint style="info" %}
If your gyro is one of the [supported types](../reference/hardware/gyroscopes.md#supported-gyroscope-types) (`pigeon2_can`, `canandgyro_can`, `navx3_can`, or the SystemCore's own IMU as `systemcore_internal`), you don't need this page. Set `gyro.type` normally in `swervedrive.json`.
{% endhint %}

Use a `custom` gyro when your heading source isn't one the parser builds for you: an IMU YAGSL has no `type` string for, a heading fused from several sensors, or a gyro object you want to construct and own yourself. Anything that can produce a WPILib `Rotation3d` works.

The examples below construct WPILib's `OnboardIMU` (the SystemCore's built-in IMU) by hand, because every SystemCore has one. Swap in your own device; the wiring into YAGSL is identical.

{% stepper %}
{% step %}
### Set `gyro.type` to `custom`

In `swervedrive.json`, set the gyro's `type` to `custom`. The `id`/`canbus` fields are unused for a custom gyro (leave them at `0`/`""`), and `gyroAxis` and `gyroInvert` are **also ignored**, which is the part that catches people out. The parser sees `custom` and skips gyro configuration entirely.

```json
{
  "gyro": { "type": "custom", "id": 0, "canbus": "" },
  "gyroAxis": "yaw",
  "gyroInvert": false,
  "modules": ["frontleft.json", "frontright.json", "backleft.json", "backright.json"]
}
```
{% endstep %}

{% step %}
### Construct the gyro and supply it yourself

Because the parser never calls `SwerveDriveConfig.withGyro()` for a `custom` gyro, you call it yourself on the `SwerveDriveConfig` you build **before** handing it to `SwerveParser.createSwerveDrive(...)`. Modules, motor controllers and encoders are still built normally from the rest of the JSON.

`withGyro` takes a `Supplier<Rotation3d>`: the gyro's full attitude (roll, pitch and yaw), not just a heading angle. YAMS reads the heading out of it, and uses the roll and pitch for anti-tipping if you enable it. `withRotation(...)` is the same method under another name.

{% tabs %}
{% tab title="Commands v2" %}
```java
import java.io.File;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.hardware.imu.OnboardIMU;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;
import org.wpilib.system.Filesystem;
import swervelib.commands2.SwerveParser;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;
import yams.core.telemetry.enums.TelemetryVerbosity;

public class SwerveDriveSubsystem extends SubsystemBase {
  private final OnboardIMU gyro = new OnboardIMU(MountOrientation.FLAT);
  private final SwerveDrive drive;

  public SwerveDriveSubsystem() {
    var cfg = new SwerveDriveConfig()
        .withSubsystem(this)
        .withTelemetry("swerve", TelemetryVerbosity.HIGH)
        .withGyro(gyro::getRotation3d);

    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    drive = SwerveParser.createSwerveDrive(cfg);
  }
}
```
{% endtab %}

{% tab title="Commands v3" %}
```java
import java.io.File;
import org.wpilib.command3.Mechanism;
import org.wpilib.hardware.imu.OnboardIMU;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;
import org.wpilib.system.Filesystem;
import swervelib.commands3.SwerveParser;
import yams.commands3.config.SwerveDriveConfig;
import yams.commands3.swerve.SwerveDrive;
import yams.core.telemetry.enums.TelemetryVerbosity;

public class SwerveDriveMechanism implements Mechanism {
  private final OnboardIMU gyro = new OnboardIMU(MountOrientation.FLAT);
  private final SwerveDrive drive;

  public SwerveDriveMechanism() {
    var cfg = new SwerveDriveConfig()
        .withMechanism(this)
        .withTelemetry("swerve", TelemetryVerbosity.HIGH)
        .withGyro(gyro::getRotation3d);

    SwerveParser.parse(new File(Filesystem.getDeployDirectory(), "swerve/base"));
    drive = SwerveParser.createSwerveDrive(cfg);
  }
}
```
{% endtab %}
{% endtabs %}

If your device only reports a yaw angle, wrap it in a `Rotation3d` with zero roll and pitch:

```java
.withGyro(() -> new Rotation3d(0, 0, Math.toRadians(myGyro.getYawDegrees())))
```

{% hint style="danger" %}
If you forget to call `withGyro()` before `createSwerveDrive()`, YAMS has no attitude supplier to read. Expect an `IllegalStateException` ("Gyro supplier is not set!") the first time the drive reads its heading on a real robot, not a silent fallback.
{% endhint %}
{% endstep %}

{% step %}
### Handle the heading axis and inversion yourself

Since `gyroAxis` and `gyroInvert` are ignored for a `custom` gyro, set their Java equivalents on the same `SwerveDriveConfig`. Hand YAMS the gyro's attitude untouched and let it do both in one place; don't negate or re-map angles inside your supplier as well, or they get applied twice.

```java
import yams.core.mechanisms.config.enums.GyroAxis;

.withGyro(gyro::getRotation3d)
.withGyroHeadingAxis(GyroAxis.YAW) // YAW is the default; ROLL or PITCH for a gyro mounted on its side
.withGyroInverted(true)            // only if the heading decreases when the robot turns counter-clockwise
```

| JSON field (ignored for `custom`) | Java equivalent                                    |
| --------------------------------- | -------------------------------------------------- |
| `gyroAxis`                        | `withGyroHeadingAxis(GyroAxis.YAW / PITCH / ROLL)` |
| `gyroInvert`                      | `withGyroInverted(boolean)`                        |

`withGyroHeadingAxis` says which axis of the gyro points up through the robot. YAMS rotates the whole attitude into the robot's frame for that axis, then applies the inversion to the resulting heading.
{% endstep %}

{% step %}
### Verify it

Follow the normal [gyro CCW+ check](../fix-a-problem/determine-inversion.md#2-confirm-the-gyro-reads-ccw). `Mechanisms/swerve/gyro` in NetworkTables still populates and behaves exactly like a parser-built gyro, since YAMS only ever sees a `Supplier<Rotation3d>` regardless of where it came from. If it increases while the robot rotates counter-clockwise, you're done; if not, flip `withGyroInverted(...)` from step 3 rather than `gyroInvert` in the JSON (which, again, is ignored).

In your own code, read the robot's attitude back with `drive.getGyroRotation3d()`, which has the axis, inversion and offset applied. For just the heading, use `drive.getGyroRotation3d().toRotation2d()`. (`SwerveDrive.getGyroAngle()` no longer exists.)

{% hint style="info" %}
In simulation YAMS reports its simulated heading from `getGyroRotation3d()` and never calls your supplier, so a custom gyro needs no simulation support of its own.
{% endhint %}
{% endstep %}
{% endstepper %}

## Optional: angular velocity for skew correction

Gyro-based angular-velocity skew correction (see [Swerve Drive Drift](../fix-a-problem/swerve-drift-causes.md)) is off until you set a scale factor. The parser never sets one for any gyro, custom or not:

```java
import static org.wpilib.units.Units.RadiansPerSecond;

.withGyroAngularVelocityScaleFactor(1.0)
.withGyroVelocity(() -> RadiansPerSecond.of(gyro.getGyroRateZ()))
```

`withGyroVelocity` is optional. Without it YAMS differentiates the heading from `withGyro` instead (and raises a low-priority alert saying so), which is fine for most robots; supply the gyro's own yaw rate when it has a cleaner one.

{% hint style="warning" %}
`withGyroInverted` and `withGyroHeadingAxis` apply to the attitude only, not to the supplier you pass to `withGyroVelocity`. The rate must already be counter-clockwise positive about the robot's vertical axis: if you needed `withGyroInverted(true)`, negate the rate, and if you changed the heading axis, read the rate from that same axis, or the skew correction will fight the heading it's supposed to be correcting.
{% endhint %}
