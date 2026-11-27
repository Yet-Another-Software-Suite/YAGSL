# Controller Properties Configuration

## Swerve Controller (`controllerproperties.json`)

The Swerve Controller stores configuration options relating it how the swerve drive works during autonomous and drive modes which set the heading of the robot based off a joystick. The JSON files maps 1:1 with [`ControllerPropertiesJson`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/json/ControllerPropertiesJson.html) which creates a [`SwerveControllerConfiguration`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveControllerConfiguration.html). The values within here are **EXTREMELY** important to autonomous because the robot heading PID needs to be tuned correctly in order for Autonomous functions to work.

## Fields

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th>Required</th><th>Description</th></tr></thead><tbody><tr><td><code>angleJoystickRadiusDeadband</code></td><td>Double</td><td>Y</td><td>The minimum radius of the angle control joystick to allow for heading adjustment of the robot.</td></tr><tr><td><code>heading</code></td><td><a href="pidf-properties-configuration/pidf.md">PID</a></td><td>Y</td><td>The PID used to control the robot heading.</td></tr></tbody></table>

## Example Configuration

<pre class="language-json"><code class="lang-json">{
  <a data-footnote-ref href="#user-content-fn-1">"angleJoystickRadiusDeadband": 0.5</a>,
  "heading": {
    "p": 0.4,
    "i": 0,
    "d": 0.01
  }
}
</code></pre>

[^1]: Used with [`SwerveController.getTargetSpeeds`](https://broncbotz3481.github.io/YAGSL-Lib/docs/swervelib/SwerveController.html#getTargetSpeeds\(double,double,double,double,double,double\)) to set a deadband when finding the angle to rotate to based off of 2 axis.
