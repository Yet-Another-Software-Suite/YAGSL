# Swerve Module Configuration

## Swerve Module Configuration (`module/x.json`)

The swerve module configuration configures unique properties of each swerve module. It maps 1:1 with [`ModuleJson`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/json/ModuleJson.html) which is used to create [`SwerveModuleConfiguration`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveModuleConfiguration.html). This configuration file interacts directly with swerve kinematics.

{% hint style="warning" %}
**IF** your angle motors spin out of control then you may need to invert them in `inverted`
{% endhint %}

{% hint style="warning" %}
**IF** your drive motors spin uncontrollably you may have the drive motor set to be the angle motor via the ID's. Be sure to check them!
{% endhint %}

## Fields

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th>Required</th><th>Description</th></tr></thead><tbody><tr><td><code>drive</code></td><td><a href="../../devices/motor-controllers/#motor-controller-configuration">Motor Controller</a></td><td>Y</td><td>Drive motor device configuration.</td></tr><tr><td><code>angle</code></td><td><a href="../../devices/motor-controllers/#motor-controller-configuration">Motor Controller</a></td><td>Y</td><td>Angle motor device configuration.</td></tr><tr><td><code>encoder</code></td><td><a href="../../devices/absolute-encoders.md#absolute-encoder-configuration">Absolute Encoder</a></td><td>Y</td><td>Absolute encoder device configuration.</td></tr><tr><td><code>inverted</code></td><td><a href="swerve-module-configuration.md#motorconfig">MotorConfig</a></td><td>Y</td><td>Inversion state of each motor as a boolean.</td></tr><tr><td><code>absoluteEncoderOffset</code></td><td>Degrees</td><td>Y</td><td>Absolute encoder offset from 0 in degrees. May need to be a negative number.</td></tr><tr><td><code>absoluteEncoderInverted</code></td><td>Bool</td><td>N</td><td>Inversion state of the Absolute Encoder.</td></tr><tr><td><code>location</code></td><td><a href="swerve-module-configuration.md#location">Location</a></td><td>Y</td><td>The location of the swerve module from the center of the robot in inches. +x is torwards the robot front, and +y is torwards robot left.</td></tr><tr><td><code>conversionFactors</code></td><td><a href="physical-properties-configuration.md#conversion-factor-composition">Conversion Factor Composition </a><br>Overrides the value in <a href="physical-properties-configuration.md#fields"><code>physicalproperties.json</code></a> if present and not equal to <code>0</code>.</td><td>N</td><td><em>OVERRIDE</em> Conversion factors applied to the motor controller for the onboard PID, used to override this setting in <a href="https://github.com/BroncBotz3481/YAGSL/wiki/Swerve-Drive"><code>swervedrive.json</code></a></td></tr><tr><td><code>useCosineCompensator</code></td><td>Bool</td><td>N</td><td>Disabled cosine compensation which scales the velocity of modules which are not completely aligned with desired angles by the cosine of the difference between the angles.</td></tr></tbody></table>

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

## Example Configuration

<pre class="language-json"><code class="lang-json">{
  "drive": {
    <a data-footnote-ref href="#user-content-fn-1">"type": "sparkmax"</a>,
    "id": 2,
    "canbus": null
  },
  "angle": {
    <a data-footnote-ref href="#user-content-fn-2">"type": "sparkmax"</a>,
    "id": 1,
    "canbus": null
  },
  "encoder": {
    <a data-footnote-ref href="#user-content-fn-3">"type": "cancoder"</a>,
    "id": 10,
    "canbus": null
  },
  "inverted": {
    "drive": false,
    "angle": false
  },
  "absoluteEncoderInverted": false,
  "absoluteEncoderOffset": -50.977,
  "location": {
    "front": 12,
    "left": -12
  }
}
</code></pre>

[^1]: See more at [motor-controllers](../../devices/motor-controllers/ "mention")

[^2]: See more at [motor-controllers](../../devices/motor-controllers/ "mention")

[^3]: See more at [absolute-encoders.md](../../devices/absolute-encoders.md "mention")
