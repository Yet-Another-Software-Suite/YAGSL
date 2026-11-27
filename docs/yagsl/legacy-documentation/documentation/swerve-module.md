# Swerve Module

## Swerve Module Configuration (`module/x.json`)

The swerve module configuration configures unique properties of each swerve module. It maps 1:1 with [`ModuleJson.java`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/parser/json/ModuleJson.java) which is used to create [`SwerveModuleConfiguration`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/parser/SwerveModuleConfiguration.java). This configuration file interacts directly with swerve kinematics.

## Fields

| Name                      | Units                                                                   | Required | Description                                                                                                                                                                                       |
| ------------------------- | ----------------------------------------------------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `drive`                   | [Device](../../configuring-yagsl/configuration/device-configuration.md) | Y        | Drive motor device configuration.                                                                                                                                                                 |
| `angle`                   | [Device](../../configuring-yagsl/configuration/device-configuration.md) | Y        | Angle motor device configuration.                                                                                                                                                                 |
| `encoder`                 | [Device](../../configuring-yagsl/configuration/device-configuration.md) | Y        | Absolute encoder device configuration.                                                                                                                                                            |
| `inverted`                | [MotorConfig](swerve-module.md#motorconfig)                             | Y        | Inversion state of each motor as a boolean.                                                                                                                                                       |
| `absoluteEncoderOffset`   | Degrees                                                                 | Y        | Absolute encoder offset from 0 in degrees. May need to be a negative number.                                                                                                                      |
| `absoluteEncoderInverted` | Bool                                                                    | N        | Inversion state of the Absolute Encoder.                                                                                                                                                          |
| `location`                | [Location](swerve-module.md#location)                                   | Y        | The location of the swerve module from the center of the robot in inches. +x is torwards the robot front, and +y is torwards robot left.                                                          |
| `conversionFactor`        | [MotorConfig](swerve-module.md#motorconfig)                             | N        | _OVERRIDE_ Conversion factor applied to the motor controller for the onboard PID, used to override this setting in [`swervedrive.json`](https://github.com/BroncBotz3481/YAGSL/wiki/Swerve-Drive) |

#### MotorConfig

| Name    | Units | Required | Description        |
| ------- | ----- | -------- | ------------------ |
| `drive` | Value | Y        | Drive motor value. |
| `angle` | Value | Y        | Angle motor value. |

#### Location

| Name    | Units | Required | Description                                              |
| ------- | ----- | -------- | -------------------------------------------------------- |
| `front` | Value | Y        | Inches from robot center to module in forward direction. |
| `left`  | Value | Y        | Inches from robot center to module in left direction.    |

