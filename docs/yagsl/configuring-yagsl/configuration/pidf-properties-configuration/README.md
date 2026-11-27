# PIDF Properties Configuration

## Swerve Module PID Configuration (`modules/pidfpropreties.json`)

This file configures the PIDF values with integral zone and maximum output of the drive and motor modules for every swerve module. It maps 1:1 to [`PIDFPropertiesJson`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/json/PIDFPropertiesJson.html) and is used while initializing the [`SwerveDriveConfiguration`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveDriveConfiguration.html) object.

{% hint style="warning" %}
TalonFX/Kraken/Falcon angle motors require a high PID (around \~`50` kP, and \~`0.32` kD).&#x20;
{% endhint %}

## Fields

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th>Required</th><th>Description</th></tr></thead><tbody><tr><td><code>drive</code></td><td><a href="pidf.md">PIDF with Integral Zone and Output limit</a></td><td>Y</td><td>The configuration which will be used for the PIDF on the drive motor.</td></tr><tr><td><code>angle</code></td><td><a href="pidf.md">PIDF with Integral Zone and Output limit</a></td><td>Y</td><td>The configuration which will be used for the PIDF on the angle motor.</td></tr></tbody></table>

## Example Configuration File

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "p": 0.0020645,
    "i": 0,
    "d": 0,
    "f": 0,
    "iz": 0
  },
  "angle": {
<strong>    <a data-footnote-ref href="#user-content-fn-1">"p": 0.01</a>,
</strong>    "i": 0,
<strong>    <a data-footnote-ref href="#user-content-fn-2">"d": 0</a>,
</strong>    "f": 0,
    "iz": 0
  }
}
</code></pre>

[how-to-tune-pidf.md](../../how-to-tune-pidf.md "mention")

[^1]: For TalonFX motor controllers like Krakens and Falcons, this should be around `50`. See more information [how-to-tune-pidf.md](../../how-to-tune-pidf.md "mention")

[^2]: For TalonFX motor controllers this should be around `0.32`. See more information [how-to-tune-pidf.md](../../how-to-tune-pidf.md "mention")
