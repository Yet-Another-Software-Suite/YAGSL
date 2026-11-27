# Physical Properties Configuration

## Swerve Module Physical Properties (`modules/physicalproperties.json`)

This JSON configures the physical properties shared with all the Swerve Modules. It maps 1:1 with [`PhysicalPropertiesJson`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/json/PhysicalPropertiesJson.html) which creates [`SwerveModulePhysicalCharacteristics`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveModulePhysicalCharacteristics.htmll).

{% hint style="info" %}
Use the [standard-conversion-factors.md](../standard-conversion-factors.md "mention") page to set the conversion factors for your robot if you're using a COTS swerve module!
{% endhint %}

## Fields

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th width="115">Required</th><th>Description</th></tr></thead><tbody><tr><td><code>conversionFactors</code></td><td><a href="physical-properties-configuration.md#conversion-factor-composition">Conversion Factor Composition</a></td><td>Y</td><td>Conversion factor composition. Factor is calculated on startup.</td></tr><tr><td><code>optimalVoltage</code></td><td>Voltage</td><td>Y</td><td>Optimal voltage to compensate to and base feedforward calculations off of.</td></tr><tr><td><code>currentLimit</code></td><td><a href="physical-properties-configuration.md#motorconfig">MotorConfig</a><br>More info <a href="../../fundamentals/swerve-modules.md#current-limiting">here</a>.</td><td>N</td><td>The current limit in AMPs to apply to the motors. Stator limit for SparkMAXs, Supply limit for TalonFXs.</td></tr><tr><td><code>rampRate</code></td><td><a href="physical-properties-configuration.md#motorconfig">MotorConfig</a></td><td>N</td><td>The minimum number of seconds to take for the motor to go from 0 to full throttle.</td></tr><tr><td><code>friction</code></td><td><a href="physical-properties-configuration.md#motorconfig">MotorConfig</a></td><td>N</td><td>The minimum voltage to move the wheel or module. Defaults to <code>0.2</code> for drive motors, and <code>0.3</code> for angle motors.</td></tr><tr><td><code>robotMass</code></td><td>Lb (Pounds)</td><td>N</td><td>Default is <code>50</code>kgs.</td></tr><tr><td><code>steerRotationalInertia</code></td><td>KilogramMetersSquare</td><td>N</td><td>The rotational inertia in <code>KilogramMetersSquare</code> . Defaults to <code>0.03</code>.</td></tr><tr><td><code>wheelGripCoefficientOfFriction</code></td><td>Coefficient of Friction on Carpet</td><td>N</td><td>The grip tape coefficient of friction on carpet. Used to calculate the practical maximum acceleration.</td></tr></tbody></table>

#### MotorConfig

| Name    | Units  | Required | Description        |
| ------- | ------ | -------- | ------------------ |
| `drive` | Number | Y        | Drive motor value. |
| `angle` | Number | Y        | Angle motor value. |

#### Conversion Factor Composition

<table><thead><tr><th>Name</th><th>Units</th><th width="62">Required</th><th>Description</th></tr></thead><tbody><tr><td><code>drive</code></td><td><a href="physical-properties-configuration.md#drive-conversion-factor-composition">Drive Composition</a></td><td>Y</td><td>Drive conversion factor composition</td></tr><tr><td><code>angle</code></td><td><a href="physical-properties-configuration.md#angle-conversion-factor-composition">Angle Composition</a></td><td>Y</td><td>Angle conversion factor composition</td></tr></tbody></table>

#### Drive Conversion Factor Composition

<table><thead><tr><th width="149">Name</th><th width="117">Units</th><th width="106">Required</th><th>Description</th></tr></thead><tbody><tr><td><code>diameter</code></td><td>Inches</td><td>Y</td><td>Diameter of the wheel.</td></tr><tr><td><code>gearRatio</code></td><td>Number</td><td>Y</td><td>Gear ratio of the drive motor to wheel.</td></tr><tr><td><code>factor</code></td><td>Number</td><td>N</td><td>Pre-calculated conversion factor.</td></tr></tbody></table>

#### Angle Conversion Factor Composition

<table><thead><tr><th width="156">Name</th><th width="123">Units</th><th width="87">Required</th><th>Description</th></tr></thead><tbody><tr><td><code>gearRatio</code></td><td>Number</td><td>Y</td><td>Gear ratio of the angle/steering/azimuth motor to wheel.</td></tr><tr><td><code>factor</code></td><td>Number</td><td>N</td><td>Pre-calculated conversion factor.</td></tr></tbody></table>

## Example Configuration File

<pre class="language-json"><code class="lang-json">{
  <a data-footnote-ref href="#user-content-fn-1">"conversionFactors"</a>: {
	"angle": {"gearRatio": 28.125},
	"drive": {"diameter": 4, "gearRatio": 6.75}
  },
  "currentLimit": {
    "drive": 40,
    "angle": 20
  },
  "rampRate": {
    "drive": 0.25,
    "angle": 0.25
  },
  "wheelGripCoefficientOfFriction": 1.19,
  "optimalVoltage": 12
}
</code></pre>

[^1]: Can be found for COTS swerve modules in [standard-conversion-factors.md](../standard-conversion-factors.md "mention")
