---
description: One wrong number in a JSON file can make a swerve drive do almost anything...
---

# Common configuration issues

A wrong configuration value usually doesn't cause an error; the robot just drives wrong. This is a lookup of common configuration issues. For what each field means, see the [JSON Configuration Schema](../reference/json-schema/).

It's important to keep in mind that SEVERAL of these issues could be present on your robot at once.

## Project setup

* [ ] Team number is wrong.
* [ ] `swerve/` config directory isn't in `src/main/deploy/`.
* [ ] A vendor library is missing. See [Vendordeps and offline install](../reference/vendordep-installation.md#optional-vendor-libraries).

## Devices

* [ ] `id` doesn't match the device's CAN ID.
* [ ] Two devices share a CAN ID on the same bus, including the REV Power Distribution Hub.
* [ ] `canbus` doesn't match the bus the device is wired to. See [Put devices on a different CAN bus](../tune-and-extend/choose-a-can-bus.md).
* [ ] Drive and angle IDs are swapped.
* [ ] `type` is wrong for a motor, encoder, or gyro.
* [ ] `channel` is wrong for a SmartIO encoder.
* [ ] `gyroAxis` doesn't match how the gyro is mounted.
* [ ] Module files are listed in the wrong order in `swervedrive.json`.

## Module locations

* [ ] Module locations aren't defined correctly. Remember these are measured from the center of the robot to the center of the module, in inches. See [how to verify module locations](verify-module-locations.md).
* [ ] Front of the robot isn't the side the gyro reports as `0`.

## Inversions and offsets

* [ ] `inverted.angle` or `absoluteEncoderInverted` is wrong.
* [ ] `inverted.drive` is wrong.
* [ ] `gyroInvert` is wrong.
* [ ] `absoluteEncoderOffset` was recorded with the bevel gears facing right instead of left. See [Determine inversion](determine-inversion.md).
* [ ] `absoluteEncoderOffset` is out of date after a magnet or encoder was changed.
* [ ] [Translational axis changes with robot orientation](the-8-steps.md).

## Gearing

* [ ] The gear ratio/wheel diameter is not correct. See [Standard Conversion Factors](../reference/standard-conversion-factors.md).
* [ ] Gear ratio doesn't match the installed pinion.
* [ ] A module's `gearing` override in its module JSON disagrees with the default in `physicalproperties.json`.
  * A per-module `gearing` override is completely **OPTIONAL** and shouldn't be defined unless you have a specific reason.
* [ ] `absoluteEncoderGearRatio` is wrong.

## PID, feedforward and current limits

* [ ] Drive and steering/azimuth/angle motor PIDs aren't tuned well enough. See [how to tune PIDF gains](../tune-and-extend/tune-pidf-gains.md).
* [ ] Gains were copied from a different motor.
* [ ] The current limit (`statorCurrentLimit`) is too low.

## Code configuration

* [ ] Maximum physical velocity isn't set correctly. See `SwerveDriveConfig.withMaximumChassisSpeed()`/`withMaximumModuleSpeed()`.
* [ ] Controller input is scaled down, such as with `withScaleTranslation()`.
* [ ] Controller axes are mapped wrong.
* [ ] Gyro is never zeroed.
