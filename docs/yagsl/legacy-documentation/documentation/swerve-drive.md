# Swerve Drive

## Swerve Drive (`swervedrive.json`)

The Swerve Drive JSON configuration file configures everything related to the overall Swerve Drive. It maps 1:1 to [`SwerveDriveJson.java`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/parser/json/SwerveDriveJson.java) which creates a [`SwerveDriveConfiguration`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/parser/SwerveDriveConfiguration.java) that is used to create the [`SwerveDrive`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/SwerveDrive.java) object.

## JSON Fields

| Name          | Units                                                                   | Required | Description                                                     |
| ------------- | ----------------------------------------------------------------------- | -------- | --------------------------------------------------------------- |
| `imu`         | [Device](../../configuring-yagsl/configuration/device-configuration.md) | Y        | Robot IMU used to determine heading of the robot.               |
| `invertedIMU` | Boolean                                                                 | Y        | Inversion state of the IMU.                                     |
| `modules`     | String array                                                            | Y        | Module JSONs in order clockwise order starting from front left. |

