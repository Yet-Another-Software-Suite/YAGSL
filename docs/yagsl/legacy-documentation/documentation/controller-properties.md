# Controller Properties

## Swerve Controller (`controllerproperties.json`)

The Swerve Controller stores configuration options relating it how the swerve drive works during autonomous and drive modes which set the heading of the robot based off a joystick. The JSON files maps 1:1 with [`ControllerPropertiesJson.java`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/parser/json/ControllerPropertiesJson.java) which creates a [`SwerveControllerConfiguration`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/parser/SwerveControllerConfiguration.java). The values within here are **EXTREMELY** important to autonomous because the robot heading PID needs to be tuned correctly in order for Autonomous functions to work.

## Fields

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th>Required</th><th>Description</th></tr></thead><tbody><tr><td><code>angleJoystickRadiusDeadband</code></td><td>Double</td><td>Y</td><td>The minimum radius of the angle control joystick to allow for heading adjustment of the robot.</td></tr><tr><td><code>heading</code></td><td><a href="../../configuring-yagsl/configuration/pidf-properties-configuration/pidf.md">PID</a></td><td>Y</td><td>The PID used to control the robot heading.</td></tr></tbody></table>

