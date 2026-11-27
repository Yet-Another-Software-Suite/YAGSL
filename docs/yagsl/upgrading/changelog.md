# Changelog

{% hint style="info" %}
YAGSL was rewritten as a JSON-config parser around YAMS's `SwerveDrive` between versions `2026.4.1` and `2026.8.05`, and ported to WPILib 2027 and the SystemCore in `2026.10.08`. Entries from `2026.8.05` onward describe the current architecture. Everything from `2026.4.1` down is the old, pre-rewrite library, kept under [Pre-rewrite history](changelog.md#pre-rewrite-history).
{% endhint %}

## Pull requests are reviewed

I highly encourage anyone who wants to help make YAGSL better to create pull requests with any modifications you have made that increases your quality of life.

{% embed url="https://github.com/Yet-Another-Software-Suite/YAGSL/pulls?q=is%3Apr+is%3Aclosed" %}

{% embed url="https://github.com/BroncBotz3481/YAGSL-Example/pulls?q=is%3Apr+is%3Aclosed" %}

## Contributing

YAGSL development is done on the `main` branch of the [YAGSL repository](https://github.com/Yet-Another-Software-Suite/YAGSL), with the library source at `yagsl/java/swervelib`.

{% embed url="https://github.com/Yet-Another-Software-Suite/YAGSL" %}
YAGSL repository
{% endembed %}

All PR's should be based off of and merged into `main`.

## 2026.11.06

Uses YAMS `2026.11.06`. See [Install YAGSL](../get-started/02-install-yagsl.md).

* **The NavX3 works again.** `navx3_can` builds the gyro with StudicaLib `2027.0.0-alpha-1`, the first StudicaLib for `2027_alpha7`. Install the StudicaLib vendordep from `https://dev.studica.com/maven/release/2027/json/StudicaLib.json`. Its `canbus` is a SystemCore CAN bus number, like every other vendor's. See [NavX3-CAN](../reference/hardware/gyroscopes.md#navx3-can).
* **A Thrifty Nova angle motor works with a CAN absolute encoder**, such as `cancoder_can`, like a SPARK MAX with one. The Nova runs the module's closed loop, wrapping and feedforward itself. ([#64](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/64), [#65](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/65))
* **`canbus` means the same thing for every vendor:** empty for `can_s0`, or the bus number, such as `"1"` for `can_s1`. CTRE devices with an empty `canbus` are now on `can_s0`, not Phoenix 6's default `can_s2`. See [Put devices on a different CAN bus](../tune-and-extend/choose-a-can-bus.md). ([#59](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/59))
* The module motors' closed loop period, velocity measurement delay and drive mechanism velocity are published to telemetry. ([#62](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/62))
* REVLib `2027.0.0-alpha-9` and ThriftyLib `2027.0.0-alpha-6`.

## 2026.10.09

Install from `https://cdn.yassrobotics.com/yagsl_commands2.json` or `https://cdn.yassrobotics.com/yagsl_commands3.json`. See [Install YAGSL](../get-started/02-install-yagsl.md).

* **Vendordeps and artifacts now come from `cdn.yassrobotics.com`**, so networks that block `github.io` can still install, update and download YAGSL. The vendordep JSON URLs are `https://cdn.yassrobotics.com/yagsl_commands2.json` and `https://cdn.yassrobotics.com/yagsl_commands3.json`, and `https://cdn.yassrobotics.com/` is the only maven URL. The old `yet-another-software-suite.github.io/YAGSL` vendordep and maven URLs are stale. ([#57](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/57))
* **The YAGSL vendordeps require only YAMS.** `yagsl_commands2.json` requires YAMS (Commands v2) and `yagsl_commands3.json` requires YAMS (Commands v3). REVLib, Phoenix 6, ReduxLib and ThriftyLib are no longer required: YAGSL loads a vendor's device classes through reflection only when a JSON configuration uses that vendor's devices, so you install vendor libraries for the hardware you have.
*   **`canbus` is read the same way for every vendor** through the new `swervelib.core.parser.CANBuses`. ([#59](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/59))

    | `canbus`          | Bus                                                              |
    | ----------------- | ---------------------------------------------------------------- |
    | `""` (or missing) | `can_s0` (bus 0)                                                 |
    | `"0"` to `"4"`    | `can_s0` to `can_s4`, so `"1"` is `can_s1`                       |
    | `"5"` to `"24"`   | Motioncore `can_d0` to `can_d19`                                 |
    | anything else     | passed to the vendor, such as a CANivore's name for CTRE devices |

    * **Behavior change:** CTRE devices with an empty `canbus` are now on `can_s0`, like every other vendor. Before, Phoenix 6 put them on its SystemCore default, `can_s2`. Configurations whose CTRE devices are on `can_s2` need `"canbus": "2"`.
    * **Behavior change:** REV devices no longer take port names such as `"CAN_S1"`. Use `"1"`.
    * REV and Thrifty devices reject anything other than a bus number, with an error that explains the bus numbers. A bus number that does not exist, such as `"25"`, also gives a clear error.
    * Redux devices take bus numbers too. ReduxLib bus strings such as `"socketcan:can_s1"` still work, since they are passed through.
* **Thrifty Nova is supported again**, through YAMS's `NovaWrapper` on ThriftyLib 2027. ([#58](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/58))
  * Motor types are `nova_<motor>`: `nova_minion` and `nova_pulsar` select those motors on the Nova, and anything else, such as `nova_neo`, is run as a NEO.
  * An absolute encoder attached to the Nova's data port (`revthroughbore_attached`, `canandmag_attached`, `srxmag_attached`, `thrifty_attached`, `andymarkhexbore_attached`, `dutycycle_attached`) sets the Nova's absolute encoder type and direction, and is used as the external feedback encoder.
  * Attached `analog_attached` encoders are now supported on a Nova.
  * The `canbus` of a Nova must be empty or a bus number, since ThriftyLib does not take bus names.
* **SystemCore IMU added as a gyro.** Set the gyro `type` to `systemcore_internal` to use the IMU built into the SystemCore. It needs no vendordep, ignores `id` and `canbus`, and is created with a flat mount orientation. ([#56](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/56))
* The Commands v2 and Commands v3 vendordeps are no longer listed as direct requirements of the YAGSL vendordeps. They come in through YAMS.
* Built against YAMS `2026.10.09`.

## 2026.10.08

**YAGSL 2027.** The library, vendordep build and examples were ported to WPILib 2027 (`2027_alpha7`), Java 25 and the SystemCore, on top of YAMS's 2027 core/commands split. ([#55](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/55))

Breaking changes for robot code:

* **WPILib 2027.** WPILib Java packages are `org.wpilib.*` (`edu.wpi.first.*` no longer exists), and several types were renamed. The ones you meet with YAGSL are `ChassisSpeeds`, now `ChassisVelocities`, and `SwerveModuleState`, now `SwerveModuleVelocity`.
*   **Two vendordeps, one per command framework.** Install exactly one, they conflict with each other.

    | Vendordep              | Artifacts                       | Command framework |
    | ---------------------- | ------------------------------- | ----------------- |
    | `yagsl_commands2.json` | `YAGSL-core`, `YAGSL-commands2` | Commands v2       |
    | `yagsl_commands3.json` | `YAGSL-core`, `YAGSL-commands3` | Commands v3       |

    `YAGSL-java` is replaced by `YAGSL-core` plus `YAGSL-commands2` or `YAGSL-commands3`. Remove the old `yagsl.json` from your `vendordeps` folder.
* **Packages.** Command framework agnostic code moved to `swervelib.core`: `swervelib.parser` is now `swervelib.core.parser`, and `swervelib.telemetry` is now `swervelib.core.telemetry`.
* **Entry point.** Create the drive with `swervelib.commands2.SwerveParser` or `swervelib.commands3.SwerveParser`. They return the matching YAMS `SwerveDrive` (`yams.commands2.swerve.SwerveDrive` or `yams.commands3.swerve.SwerveDrive`). `SwerveParser.parse(directory)` works as before.
* **YAMS packages.** YAMS classes are now under `yams.core.*`, `yams.commands2.*` and `yams.commands3.*`. For example `yams.mechanisms.config.SwerveDriveConfig` is now `yams.commands2.config.SwerveDriveConfig`, `yams.mechanisms.swerve.utility.SwerveInputStream` is now `yams.commands2.swerve.SwerveInputStream`, and `SmartMotorControllerConfig.TelemetryVerbosity` is now `yams.core.telemetry.enums.TelemetryVerbosity`.
* **Gyro API.** `SwerveDrive.getGyroAngle()` is removed, use `getGyroRotation3d()`. `SwerveDriveConfig.withGyro` takes a `Supplier<Rotation3d>`, for example `withGyro(gyro::getRotation3d)`.

Other changes:

* Gyros now hand YAMS their full `Rotation3d` attitude. The heading axis from `gyroAxis` is applied in one place (YAMS's `GyroAxis`, through `withGyroHeadingAxis`), and inversion is left to YAMS's `withGyroInverted`. This fixes the Canandgyro being inverted twice.
* JSON parsing moved from Jackson, which WPILib 2027 no longer ships, to WPILib's avaje-jsonb. The parsing code is generated at compile time and ships in `YAGSL-core`. Your JSON files do not change, and properties the schema does not know, such as `$schema`, are still ignored.
* The `canbus` field now selects the bus for REV devices as well (see `2026.10.09` for the final rules).
* The azimuth motor controller is created once per module. It was created twice before, which made YAMS register duplicate alerts and throw.
* Releases now attach `<version>-YAGSL-Offline.zip`, which extracts into the WPILib `2027_alpha7` home folder for offline installs, plus `example-commands2.zip` and `example-commands3.zip`.
* The example is now two projects, [`examples/commands2`](../../../examples/commands2) and [`examples/commands3`](../../../examples/commands3).

Not available yet on `2027_alpha7`:

* StudicaLib and the AndyMark library have no 2027 vendordep, so the `navx3_can` gyro and the `andymarkhexbore_can` encoder fail at startup.
* YALL (Limelight) and PhotonLib have no 2027 vendordep, so the example's vision subsystems are parked outside the build.
* Thrifty Nova was unavailable in this release. It returned in `2026.10.09`.

## 2026.10.03

* Fixed the maximum module speed calculation. The motor's free speed is now reduced through the drive gearing to get the wheel speed, and the drive desaturates towards the slowest module so every module can reach its commanded speed.
* Added simulation tests for the example's vision subsystem.

## 2026.9.27

* `SwerveParser` no longer forces module state optimization on. Control it with `SwerveDriveConfig.withModuleStateOptimization(boolean)`.

## 2026.9.17

* SmartIO encoders (`*_dio` and `*_analog` absolute encoders) now read their port from the `channel` field, not `id`. ([#54](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/54))

## 2026.9.16

* Fixed attached absolute encoders so `absoluteEncoderOffset` is applied as the external encoder's zero offset.
* Updated to YAMS `2026.9.16`.

## 2026.9.02.1

* Telemetry settings from your `SwerveDriveConfig` are passed down to the modules and their motor controllers: the modules follow the drive's telemetry verbosity when one is given, and the DataLog names follow the drive's `SwerveDriveTelemetryConfig.withDataLogName(...)`.
* Trimmed the per-motor telemetry. Drive motors publish stator and supply current plus `measurement/position` and `measurement/velocity`. Azimuth motors publish stator and supply current, `mechanism/position` and `mechanism/velocity`, and the external encoder position and velocity.

## 2026.8.31

* Telemetry is configured with YAMS's `SwerveDriveTelemetryConfig` and `SwerveModuleTelemetryConfig`. When you only give a telemetry verbosity, YAGSL still writes the DataLog under `Swerve/`.
* The example gained vision simulation, with telemetry fixes from YAMS.

## 2026.8.25

* `SwerveParser` methods are static: call `SwerveParser.parse(directory)` and then `SwerveParser.createSwerveDrive(config)`, with no instance to create. By [@DanPeled](https://github.com/DanPeled) in [#52](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/52)
* Added `SwerveParser.createSwerveDriveDevices(config)`, which returns the `SwerveDrive` together with the raw vendor devices YAGSL created (motor controllers, absolute encoders and the gyro). See [Access raw hardware devices](../tune-and-extend/access-raw-hardware-devices.md).
* The examples were merged into a single example project, which now includes vision and autonomous.

## 2026.8.18

* Added the `custom` gyro type, for supplying your own gyro in code. See [Use a custom gyro](../tune-and-extend/use-a-custom-gyro.md).

## 2026.8.17

* `SwerveParser` now controls which telemetry fields the swerve drive exposes.
* Fixed the DataLog location.

## 2026.8.05

First release of the rewritten YAGSL: a JSON configuration parser that builds a YAMS `SwerveDrive`. The JSON schema changed, see [Schema Changes](schema-changes.md).

* Cleaned up `createSwerveDrive` by [@DanPeled](https://github.com/DanPeled) in [#48](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/48)
* Fixed to use correct imports by [@DanPeled](https://github.com/DanPeled) in [#49](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/49)
* Added cosine compensation, drive and angle feedforward support, and an automatic theoretical drive feedforward when the drive `v` gain is `0`.
* Added `pidfproperties_sim.json`, which replaces `pidfproperties.json` in simulation.
* Module telemetry names no longer include the `.json` suffix.
* Added attached absolute encoder support for REV, CTRE and Thrifty Nova motor controllers, plus the `analog`, `analog5v`, `dutycycle` and `splineencoder` encoder types.
* All vendor devices are created through reflection.

## Pre-rewrite history

{% hint style="warning" %}
Every entry below this notice describes the **old**, pre-rewrite architecture (a self-contained `SwerveDrive`/`SwerveModule`/`SwerveController` implementation, `controllerproperties.json`, `imu`/`encoder`-named JSON fields, etc.). None of it reflects the current API. If you're trying to understand what changed in the rewrite itself, see [Schema Changes](schema-changes.md) instead. This history is kept for archaeology: understanding _why_ a given behavior exists, and crediting the contributors who built it.
{% endhint %}

## 2026.4.1

* `setActiveOverride` should be public, not protected. by [@twisterjafla](https://github.com/twisterjafla) in [#43](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/43)

## 2026.3.12

* Adds overrides to activity when `shouldClock` is set to false. by [@twisterjafla](https://github.com/twisterjafla) in [#34](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/34)
* Fix drive base to vision pose fusion by [@Chris7898](https://github.com/Chris7898) in [#36](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/36)
* EmptyOpponent by [@konnorreynolds](https://github.com/konnorreynolds) in [#38](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/38)
* Alert fix in [#41](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/41)
* Change `aimTarget` to use `Supplier<Pose2d>` by [@Hello2Krish19](https://github.com/Hello2Krish19) in [#40](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/40)

## 2026.3.5

* Fixed SparkFlex sim motor issue in [#29](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/29)
* Added offset to `calculateAngularVelocity` by [@Kfir-bot](https://github.com/Kfir-bot) in [#30](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/30)
* An offset can be added to the aiming functionality by [@J-Kistner](https://github.com/J-Kistner) in [#32](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/32)
* Studica update and REV fix in [#33](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/33)

## 2026.2.21

* Do not round location values parsed from JSON by [@Scaratech](https://github.com/Scaratech) in [#22](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/22)
* Update incorrect default command by [@XM2TBE](https://github.com/XM2TBE) in [#25](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/25)
* Refactor `blueAlliance` variable initialization by [@XM2TBE](https://github.com/XM2TBE) in [#24](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/24)
* Fix some simulation issues by [@clrozeboom](https://github.com/clrozeboom) in [#26](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/26)
* Fix broken link in README by [@nickmcski](https://github.com/nickmcski) in [#18](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/18)

## 2026.2.03

* Fix ThriftyLib crash in [#17](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/17)
* Made CTRE device creation reflective in [#20](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/20)

## 2026.1.24

* Removed an extra println by [@twisterjafla](https://github.com/twisterjafla) in [#11](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/11)
* Changes where MapleSim publishes goal poses by [@twisterjafla](https://github.com/twisterjafla) in [#13](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/13)
* Disable bump collision by [@konnorreynolds](https://github.com/konnorreynolds) in [#16](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/16)

## 2026.1.14

* Updated vendordep error messages for clarity by [@twisterjafla](https://github.com/twisterjafla) in [#10](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/10)
* Made Studica device creation reflective.

## 2026.1.13

* Additional example projects, reworked alliance based gyro zeroing by [@HolyArtist31656](https://github.com/HolyArtist31656) in [#1](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/1)
* Fixed unneeded alliance flipping by [@HolyArtist31656](https://github.com/HolyArtist31656) in [#2](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/2)
* Updates to Thrifty API for ThriftyNova code by [@clrozeboom](https://github.com/clrozeboom) in [#4](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/4)
* MapleSim update by [@konnorreynolds](https://github.com/konnorreynolds) in [#8](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/8)
* Adds rebuilt by [@twisterjafla](https://github.com/twisterjafla) in [#9](https://github.com/Yet-Another-Software-Suite/YAGSL/pull/9)

## 2025.8.0

* Fix attached absolute encoders with brushed SparkMAX's

## 2025.7.2

* fix setAbsoluteEncoderOffset() by [@MEisSCAMMER](https://github.com/MEisSCAMMER) in [#338](https://github.com/BroncBotz3481/YAGSL-Example/pull/338)
* Enable Continuous Input for omegaPIDController by [@Sate04](https://github.com/Sate04) in [#337](https://github.com/BroncBotz3481/YAGSL-Example/pull/337)
* Patched crash when starting while enabled.

## 2025.7.1

* Fixed `SwerveInputStream.allianceRelativeControl`
* Prevent crashing midmatch if robot code restarts.
* Added `SwerveInputStream.translationHeadingOffset`
* Removed gyro zeroing from YAGSL startup. You must now call gyro zero on `autonomousInit` or use the `RobotModeTriggers`
* `Added RobotModeTriggers.autonomous().onTrue(Commands.runOnce(this::zeroGyroWithAlliance));`to the example code.

## 2025.7.0

* [ ] Made the old method for conversion factors throw an exception instead of throwing a warning.
* [ ] Added TalonFXS Support.
* [ ] Renamed <kbd>SwerveMotor.isAttachedAbsoluteEncoder</kbd> to <kbd>SwerveMotor.usingExternalFeedbackSensor</kbd>.
* [ ] Deprecated <kbd>SwerveDrive.pushOffsetsToEncoders</kbd> in favor of <kbd>SwerveDrive.useExternalFeedbackSensor</kbd>.
* [ ] Deprecated <kbd>SwerveDrive.restoreInternalOffset</kbd> in favor of <kbd>SwerveDrive.useInternalFeedbackSensor</kbd>.
* [ ] Updated references from Shuffleboard to any dashboard by [@DanPeled](https://github.com/DanPeled)
* [ ] Fixed Absolute Encoders attached to SparkFlex's.
* [ ] Attached absolute encoders are no longer set as the feedback device, must be explicitly called.
* [ ] Added `SwerveInputStream.driveToPose`
* [ ] Add optional optimization of module states in drive function that uses feedforward by [@clrozeboom](https://github.com/clrozeboom) in [#314](https://github.com/BroncBotz3481/YAGSL-Example/pull/314)
* [ ] Make all closable items implement `AutoClosable` by [@kytpbs](https://github.com/kytpbs) in [#317](https://github.com/BroncBotz3481/YAGSL-Example/pull/317)
* [ ] remove useless imports and variables by [@kytpbs](https://github.com/kytpbs) in [#318](https://github.com/BroncBotz3481/YAGSL-Example/pull/318)
* [ ] Set IZone of PIDControllers created from PIDFConfigs by [@CoffeeCoder1](https://github.com/CoffeeCoder1) in [#319](https://github.com/BroncBotz3481/YAGSL-Example/pull/319)
* [ ] Fix typo by [@fletch3555](https://github.com/fletch3555) in [#323](https://github.com/BroncBotz3481/YAGSL-Example/pull/323)
* [ ] Rev Through bore via DIO by [@wackyvert](https://github.com/wackyvert) in [#325](https://github.com/BroncBotz3481/YAGSL-Example/pull/325)
* [ ] stop setMotorBrake from constantly executing by [@rakosi2](https://github.com/rakosi2) in [#327](https://github.com/BroncBotz3481/YAGSL-Example/pull/327)
* [ ] Prevent rotation if controller heading inputs are not past axisDeadband by [@MrFast-js](https://github.com/MrFast-js) in [#328](https://github.com/BroncBotz3481/YAGSL-Example/pull/328)

## 2025.3.0

* [ ] Added `SwerveDrive.setModuleStateOptimization`to allow for easier debugging.
* [ ] Fixed `sparkmax_analog`
* [ ] Null pointer crash when there have been no vision results by [@jwt388](https://github.com/jwt388) in [#307](https://github.com/BroncBotz3481/YAGSL-Example/pull/307)
* [ ] Adding PigeonViaTalonSRX and attached SparkFlex by [@konnorreynolds](https://github.com/konnorreynolds) in [#292](https://github.com/BroncBotz3481/YAGSL-Example/pull/292)
* [ ] Throw an error when module configs are incorrectly set to 0 by [@wackyvert](https://github.com/wackyvert) in [#308](https://github.com/BroncBotz3481/YAGSL-Example/pull/308)
* [ ] Fixed rawAbsoluteEncoder position. by [@konnorreynolds](https://github.com/konnorreynolds) in [#309](https://github.com/BroncBotz3481/YAGSL-Example/pull/309)

## 2025.2.2

* [ ] Fixed `swerve/measuredChassisSpeeds`to report both vx and vy.
* [ ] Fixed `SwerveInputStream.robotRelative` by Team 151
* [ ] Fixed `SwerveDrive.drive` `fieldOriented` parameter to be applied. Bug cuaght by WispySparks from team 2508!
* [ ] Added `SwerveInputStream.headingOffset`

## 2025.2.1

* [ ] Fixed optimization using the relative encoder
* [ ] Fix absolute encoders detection in `SparkMaxSwerve.isAbsoluteEncoderAttached()` by changing null check to `Optional.isPresent()` (Discovered by Team 217!)
* [ ] Changed `absoluteEncoder` to an `Optional` for SparkMax's (by Team 217)
* [ ] Added a way to sysId while spinning in place, changed parameters to `SwerveDriveTest.setDriveSysIdRoutine(new Config(),this, swerveDrive, 12, true)`

## 2025.2.0

* [ ] Disabled kA for the drive motor feedforward by default.
* [ ] Fix getMaxVelocity used in feedforward by [@jwt388](https://github.com/jwt388) in [#286](https://github.com/BroncBotz3481/YAGSL-Example/pull/286)
* [ ] Maximum speed settings do not allow limiting robot to less than full speed by [@clrozeboom](https://github.com/clrozeboom) in [#277](https://github.com/BroncBotz3481/YAGSL-Example/pull/277)
* [ ] Sim modules SysId routines & New maple-sim version by [@catr1xLiu](https://github.com/catr1xLiu) in [#288](https://github.com/BroncBotz3481/YAGSL-Example/pull/288)

## 2025.1.3

* [ ] Fixed `Adjusted IMU Yaw` and set it to publish under `SmartDashboard`
* [ ] Added simulation velocities and readings to module output.
* [ ] Added `srxmag_standalone`absolute encoder type for SRX Mag Encoders attached to a stand alone TalonSRX.

## 2025.1.2

* [ ] Updated to WPILib 2025.1.1
* [ ] Updated CTRE/REV/Studica/Redux vendordeps.
* [ ] Fixed some javadocs failures.
* [ ] Touched up comments by [@yapplejack](https://github.com/yapplejack) in [#278](https://github.com/BroncBotz3481/YAGSL-Example/pull/278)

## 2025.1.1

* [ ] Fixed telemetry publishing problem. Telemetry is now published under the `SmartDashboard` network table.
* [ ] Throw an error when SparkMAX or SparkFlex configuration changes are done while the robot is enabled.
* [ ] Changed example to not set the drive motor idle mode.
* [ ] Added ThirftyNova support.
* [ ] Added `SwerveInputStream.robotRelative`and `SwerveInputStream.allianceRelativeControl`

## 2025.1.0.1

* [ ] Added configuration redundancy for SparkMAX, and SparkFlex.
* [ ] Fix loop time and use field oriented control for driveWithSetpointGenerator (PR [#271 ](https://github.com/BroncBotz3481/YAGSL-Example/pull/271)by @[**jwt388**](https://github.com/jwt388)**)**
* [ ] Added cycle times to telemetry.
* [ ] Optimized telemetry to only post settings when `SwerveDriveTelemetry.updateSettings`is `true`.
* [ ] Reduced retry delay for SparkMAX and SparkFlex to 5ms from 10ms.
* [ ] Reduced on the fly configuration delay to 10ms from 100ms.
* [ ] Added warning for on the fly configuration delay past init.
* [ ] Fixed `SimpleMotorFeedForward.calculate`to use only the velocity. This ignores acceleration unfortunately.
* [ ] Changed `SmartDashboard.put`to NT4 Publishers
* [ ] Removed `IMUVelocity`in favor of using library velocity fetches.
* [ ] Renamed `SwerveIMU.getRate()`-> `SwerveIMU.getYawAngularVelocity()`and made it return an `AngularVelocity`object.
* [ ] Added `SwerveInputStream`object for easy controller conversions.

## 2025.1.0

* [ ] Fixed vision file null-ptr exception.
* [ ] Updated to Maple-Sim 0.2.4; added `SwerveDrive.getMapleSimDrive()` by [@catr1xLiu](https://github.com/catr1xLiu) in [#262](https://github.com/BroncBotz3481/YAGSL-Example/pull/262)
* [ ] Fixed spark max brushed motor controller encoder null-ptr exceptions
* [ ] Fixed `SwerveMath.scaleTranslation`issue
* [ ] Fixed `Vision`update with `resultLists`check by @jwt388 FRC Team 151
* [ ] Changed `SwerveDrive.getMaximumVelocity()`-> `SwerveDrive.getMaximumChassisVelocity()`
* [ ] Changed `SwerveDrive.getMaximumAngularVelocity()` -> `SwerveDrive.getMaximumChassisAngularVelocity()`
* [ ] Used known motor types to calculate the drive motor feedforward.
* [ ] Extended the parser to know the motor type attached to the motor controller. (Except brushed)
* [ ] Separated chassis maximum velocity from module maximum velocity.
* [ ] Added `navx_mxp_serial` back.
* [ ] Added motor specifiers `krakenx60foc`, `krakenx60`, `falcon500foc`, `falcon500`, `sparkmax_neo550`, `sparkmax_neo`, `sparkflex_neo`, `sparkflex_vortex`, `sparkflex_neo550`
* [ ] Create the drive motor feedforward in `SwerveModule` constructor.
* [ ] Added module max speeds to `SwerveModule.maxDriveVelocity` and `SwerveModule.maxAngularVelocity`
* [ ] Updated the web configuration

## 2025.0.0

* [ ] AHRS imu variable is lost after constructor scope ends by [@clrozeboom](https://github.com/clrozeboom) in [#254](https://github.com/BroncBotz3481/YAGSL-Example/pull/254)
* [ ] Add Maple-Sim by [@catr1xLiu](https://github.com/catr1xLiu) in [#251](https://github.com/BroncBotz3481/YAGSL-Example/pull/251)
* [ ] Implement support for kA by [@Etaash-mathamsetty](https://github.com/Etaash-mathamsetty) in [#258](https://github.com/BroncBotz3481/YAGSL-Example/pull/258)
* [ ] Update to WPILIb 2025 Beta-2 by [@thenetworkgrinch](https://github.com/thenetworkgrinch) in [#257](https://github.com/BroncBotz3481/YAGSL-Example/pull/257)
* [ ] Add support to Talon SRX for an integrated absolute encoder by [@WispySparks](https://github.com/WispySparks) in [#208](https://github.com/BroncBotz3481/YAGSL-Example/pull/208)
* [ ] Implemented Path Planner DriveFeedForward using DCMotor modeling by [@catr1xLiu](https://github.com/catr1xLiu) in [#260](https://github.com/BroncBotz3481/YAGSL-Example/pull/260)
* [ ] Fixed NavX inversion.
* [ ] Fixed Absolute Encoder issue on MAXSwerve with help from Team 457 Grease Monkeys

## 2024.7.0 - Last version for 2024 WPILib

* [ ] Added inversion redundancy check to SparkMAX and SparkFlex.
* [ ] Add Warmup for Pathfinding (PR [#241](https://github.com/BroncBotz3481/YAGSL-Example/pull/241) by [TechnologyMan00](https://github.com/Technologyman00))

## 2024.6.1.0

* [ ] Added `Canandgyro` support.
* [ ] A tiny bug fix in the aim-at-speaker command of the example swerve subsystem code by (PR [#239](https://github.com/BroncBotz3481/YAGSL-Example/pull/239) by [catr1xLiu](https://github.com/catr1xLiu))

## 2024.6.0.0

* [ ] Merge Swerve configuration test changes (PR [#228](https://github.com/BroncBotz3481/YAGSL-Example/pull/228) by [**clrozeboom**](https://github.com/clrozeboom)**)**
* [ ] Angular velocity correction (huge update that massively improves YAGSL!) (PR [#231](https://github.com/BroncBotz3481/YAGSL-Example/pull/231) by [yapplejack](https://github.com/yapplejack))
* [ ] Sparkmax optimizations, changes to avg filter etc (PR [#233](https://github.com/BroncBotz3481/YAGSL-Example/pull/233) by [**yapplejack**](https://github.com/yapplejack)**)**
* [ ] Addded `sparkmax_analog5v` as a valid absolute encoder type.
* [ ] Suggestion for desaturateWheelSpeeds() to use desiredChassisSpeeds (PR [#232](https://github.com/BroncBotz3481/YAGSL-Example/pull/232) by [**yapplejack**](https://github.com/yapplejack)**)**
* [ ] Made auto synchronization optional and configurable by `SwerveDrive.setModuleEncoderAutoSynchronize`
* [ ] Fixed configurator problem for `TalonFXSwerve`

## 2024.5.0.4

* [ ] Added PhotonVision `Vision` class to example and integrate it with the example code.
* [ ] Added `getAprilTagPose` method to `Vision` (PR [#226](https://github.com/BroncBotz3481/YAGSL-Example/pull/226) by [**kreidljj**](https://github.com/kreidljj))
* [ ] Update the vision simulation on `Vision.updatePoseEstimation` (PR [#224](https://github.com/BroncBotz3481/YAGSL-Example/pull/224) by [**brandonzx3**](https://github.com/brandonzx3)**)**
* [ ] Add Standard Deviations for YAGSL SwerveDrive Pose Estimator (PR [#222](https://github.com/BroncBotz3481/YAGSL-Example/pull/222) by [**maxikyuu**](https://github.com/maxikyuu))
* [ ] Updating Vendordeps and renaming CanandCoders to CanandMags (PR [#219](https://github.com/BroncBotz3481/YAGSL-Example/pull/219) by [**Turbojax07**](https://github.com/Turbojax07) and YAGSL devs)
* [ ] Fixed documentation issue with `SwerveDriveTelemetry` (Issue [#233 ](https://github.com/BroncBotz3481/YAGSL-Example/issues/223)by [**DanPeled**](https://github.com/DanPeled) )
* [ ] Fixed AbsoluteEncoders attached to the SparkMAX like Throughbores.

## 2024.5.0.3

* [ ] Fixed `SparkMaxAnalogEncoder` wrapper to work. The expected read values are in volts, 3.3v max. (Found and fixed by team Austin from team 2377)
* [ ] Fixed `CanandCoder` to `CanandMag` due to product rename. (Credit to [TurboJax07](https://github.com/Turbojax07))
* [ ] Changed default behavior of setting an attached absolute encoder up without calling `SwerevDrive.pushOffsetsToEncoders` which is now an optional optimization for MAX Swerve modules instead of a requirement. MAX Swerve no longer need to use `360` as the conversion factor IF they do not use `SwerveDrive.pushOffsetsToEncoders`

## 2024.5.0.1

* [ ] Added `SwerveDrive.setVisionMeasurementStdDevs(Matrix<N3, N1> visionMeasurementStdDevs)`
* [ ] Add ability to get the rotation rate from the IMU (PR [#216](https://github.com/BroncBotz3481/YAGSL-Example/pull/216) by [@clrozeboom](https://github.com/clrozeboom))

## 2024.5.0.0

* [ ] Changed input scaling to utilize Polar coordinate magnitude multiplication.
* [ ] Simplify placeInAppropriate0To360Scope (PR [#213](https://github.com/BroncBotz3481/YAGSL-Example/pull/213) by [GoldenStack](https://github.com/GoldenStack))
* [ ] Added auto-centering modules setting.
* [ ] Added back composite conversion factors.

## 2024.4.8.7

* [ ] Increased logging verbosity to introduce logging modes for the field and data. (PR [#185](https://github.com/BroncBotz3481/YAGSL-Example/pull/185) by [5010](https://github.com/5010TigerDynasty))
* [ ] Added `SwerveDrive.setChassisDiscretization` to allow for changing of the discrete value between cycle times which can be tuned to reduce drift. (PR [#194](https://github.com/BroncBotz3481/YAGSL-Example/pull/194) by [TechnologyMan00](https://github.com/Technologyman00))
* [ ] Renamed `SwerveDrive.pushOffsetsToControllers` to `SwerveDrive.pushOffsetsToEncoders`. (PR [#194](https://github.com/BroncBotz3481/YAGSL-Example/pull/194) by [TechnologyMan00](https://github.com/Technologyman00))
* [ ] Changed `Module[...]` to `swerve/modules` in Telemetry.

## 2024.4.8.6

* [ ] Added PIDF helper functions `SwerveModule.setDrivePIDF` and `SwerveModule.setAnglePIDF` alongside `SwerveModule.getDrivePIDF` and `SwerveModule.getAnglePIDF`.
* [ ] Changed feedforward around to use `SwerveModule.setFeedforward` instead of directly modifying `SwerveModule.feedforward`
* [ ] Renamed `SwerveModule.feedforward` to `SwerveModule.driveFeedforward`.
* [ ] Added anti-jitter disabling option via `SwerveModule.setAntiJitter` which also modifies the encoder offsets that are pushed to the motor controllers.
* [ ] Updated vendordeps

## 2024.4.8.5

* [ ] Fixed NavX inversion state not taking any affect on the robot.

## 2024.4.8.4

* [ ] Updated anti jitter to run before cosine compensation.

## 2024.4.8.3

* [ ] Updated `Pigeon2Swerve` to use `imu.getRotation3d()` instead of handling the CAN timeouts.
* [ ] Massively reduced memory footprint required. (with help from [@TheGamer1002](https://github.com/TheGamer1002))

## 2024.4.8.2

* [ ] Made CTRE wait for status times static for user modification. `CANCoderSwerve.STATUS_TIMEOUT_SECONDS`,`TalonFXSwerve.STATUS_TIMEOUT_SECONDS`, and `Pigeon2Swerve.STATUS_TIMEOUT_SECONDS` can be modified for desired use case.

## 2024.4.8.1

* [ ] Added `SwerveDrive.setCosineCompensation` function which can enable and disable cosine compensation as desired, disabled by default in sim because of discrepancies with real robots.

## 2024.4.8

* [ ] Added SysId support. (PR [#160](https://github.com/BroncBotz3481/YAGSL-Example/pull/160) and [#165](https://github.com/BroncBotz3481/YAGSL-Example/pull/165) by [Team 5010](https://github.com/5010TigerDynasty))
* [ ] `waitForStatusUpdate` is used instead of `refresh` with default timeouts of 20ms.
* [ ] Added `Cache` class to cache variables over a `validityPeriod` which will speed up processing and reduce CAN utilization.
* [ ] Added `SwerveDrive.updateCacheValidityPeriods` so the user can change the cache validity period to their hearts desire.
* [ ] Added `SwerveDrive.getModuleMap()` which will fetch all of the `SwerveModule`'s as a HashMap where the key is the module's filename without `.json` so `frontleft.json` would have the key of `frontleft`.

## 2024.4.7

* [ ] Modules will stay in the previous position if the desired velocity is 0.
* [ ] Cosine compensation is now reported through telemetry as expected.

## 2024.4.6.3

* [ ] Updated heading correction to use `getOdometryHeading()` instead of `getYaw()` (PR [#150](https://github.com/BroncBotz3481/YAGSL-Example/pull/150) by [@Blargleflakes](https://github.com/Blargleflakes)) This does impact heading correction PID values in `controllerproperties.json` and may NEED these value's increased by 50x.
* [ ] Added ability to disable the cosine compensator via config files. (PR [#148](https://github.com/BroncBotz3481/YAGSL-Example/pull/148) by [@fovea1959](https://github.com/fovea1959))
* [ ] Added `SwerveDrive.getGyro()` to return `SwerveIMU`.
* [ ] Added `SwerveMotor.setVoltage` and `SwerveMotor.getVoltage` and `SwerveMotor.getAppliledOutput` to the `SwerveMotor` wrapper for future use with SysId.
* [ ] Added functions to test setting voltage of all swerve module motor.
* [ ] Added function to find coupling ratio of all swerve modules.
* [ ] Added function to set steering/azimuth/angle using of all swerve modules.
* [ ] Added function to find kV for drive motors to move.
* [ ] Set the angle motor relative encoder position AFTER changing the conversion factor. (Issue [#155](https://github.com/BroncBotz3481/YAGSL-Example/issues/155))
* [ ] Fixed module open loop control by sending `maxSpeed` to the module. (by @nstrike and [@MarshallTappen](https://github.com/MarshallTappen))
* [ ] Fixed Pigeon2Swerve only using X acceleration (PR [#146](https://github.com/BroncBotz3481/YAGSL-Example/pull/146#event-11577147896) by [@dezash123](https://github.com/dezash123))
* [ ] Prevent drive motors from moving when `absoluteEncoderOffset` is not tuned. (Found by [@fovea1959](https://github.com/fovea1959))
* [ ] Updated javadocs for `SwerveDrive.addVisionMeasurement` so that they reflect the latest changes.

## 2024.4.6.1

* [ ] Fixed `SwerveDrive.resetOdometry` and utilize the pose estimation instead. (PR [#142](https://github.com/BroncBotz3481/YAGSL-Example/pull/142) by [@MarshallTappen](https://github.com/MarshallTappen) and @nstrike [commit](https://github.com/BroncBotz3481/YAGSL-Example/commit/039e5c2867690cfdd5ebd0c1e84eefc6b165adee))
* [ ] Functionalize IMU inversion (PR [#140](https://github.com/BroncBotz3481/YAGSL-Example/pull/140) by [@TechnologyMan00](https://github.com/Technologyman00) and @nstrike)
* [ ] Added `SwerveDrive.getOdometryHeading()`
* [ ] Fixed `SwerveDrive.addVisionMeasurement` with vision standard deviations.
* [ ] Added IMU readings to SmartDashboard via `Raw IMU Yaw` (gyro with invert applied) and `Adjusted IMU Yaw` (pose estimation rotation).
* [ ] Changed `navx_mxp` to `navx_mxp_serial` to notate that it's serial over MXP.
* [ ] Added warning when using `navx_mxp_serial` or `navx_usb`.
* [ ] Added back wheel speed desaturation.

## 2024.4.6

* [ ] Simplified `SwerveMath.calculateDegreesPerRotation` and `SwerveMath.calculateMetersPerRotation` to exclude encoder resolution and add a default.
* [ ] Changed `Module[...] Raw Motor Encoder` to `Module[...] Raw Angle Encoder`.
* [ ] Added `Module[...] Raw Drive Encoder`

## 2024.4.5

* [ ] Fix for TalonFX Angle motor control (by [@bhall-ctre](https://github.com/bhall-ctre), and @Wackyvert 2225 Mentor) .
  1. TalonFX's needed to use conversion factor as gear ratio rather than gear ratio + unit conversion.
  2. Conversion factor needed to be inverted.
  3. setPosition update to reflect current position.
* [ ] Changed `ma3` encoders to be read via analog input. (Discovered by [@CoZm0](https://github.com/CoZ-m0))
* [ ] Support 3 wheel swerve module setups with PathPlanner helper function. (PR #139 by [@TechnologyMan00](https://github.com/Technologyman00))
* [ ] Added `SwerveModule.getAbsoluteEncoder()` `SwerveDrive.getMaximumVelocity()` and `SwerveDrive.getMaximumAngularVelocity()`.
* [ ] Reccommend Tuner X when a compatible Tuner X config is used.
* [ ] Added ability to change heading correction deadband.

## 2024.4.2

* [ ] Added feedforward to SparkMAX's.
* [ ] Added Network Alerts (made by[ @TheGamer1002](https://github.com/TheGamer1002) PR [#136](https://github.com/BroncBotz3481/YAGSL-Example/pull/136))
* [ ] Added feedforward to TalonFX's
* [ ] Fixed TalonFX conversion factor (made by [@jkbo6](https://github.com/jbko6) PR [#128](https://github.com/BroncBotz3481/YAGSL-Example/pull/128))
* [ ] CanAndCoder tweaks (made by [@guineahawk](https://github.com/guineawheek) PR [#134](https://github.com/BroncBotz3481/YAGSL-Example/pull/134))
* [ ] Added missing SparkMAX status frame's (made by [@RoboPenguin7](https://github.com/RoboPenguin7) PR [#130](https://github.com/BroncBotz3481/YAGSL-Example/pull/130))
* [ ] Heading Correction update to make it less sensitive (made by [@balien-12](https://github.com/balien-12) PR [#132](https://github.com/BroncBotz3481/YAGSL-Example/pull/132))
* [ ] Fixed cosine compensator error (made by [@jkbo6](https://github.com/jbko6) PR [#129](https://github.com/BroncBotz3481/YAGSL-Example/pull/129))
* [ ] Updated PathPlanner path based off of alliance (made by [@MarshalTappen](https://github.com/MarshallTappen) PR [#122](https://github.com/BroncBotz3481/YAGSL-Example/pull/122))
* [ ] Fixed Rotation being off, update getDriveBaseMeters (made by [@TechnologyMan00](https://github.com/Technologyman00))
