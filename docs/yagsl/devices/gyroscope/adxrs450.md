# ADXRS450

{% hint style="warning" %}
ADXRS450 has a limit of ±300°/sec and will stop providing rotation data past the limits.\
This gyro is not recommended to be used for competitions.
{% endhint %}

{% hint style="warning" %}
Keep your robot still at startup EVERY TIME with this gyroscope.
{% endhint %}

## Documentation

{% embed url="https://wiki.analog.com/first/adxrs450_gyro_board_frc" %}
Official documentation
{% endembed %}

{% embed url="https://docs.wpilib.org/en/stable/docs/software/hardware-apis/sensors/gyros-software.html#adxrs450-gyro" %}
WPILib Documentation on how to initialize it
{% endembed %}

<figure><img src="../../.gitbook/assets/image (2) (1) (1) (1).png" alt=""><figcaption><p>Single-axis reading.</p></figcaption></figure>

The ADXRS450 is an old single axis gyroscope meaning it must be parallel to the earth for it to work!

## YAGSL Checklist

* [ ] Ensure the ADXRS450 is attached to the roboRIO firmly.
* [ ] The roboRIO is centered on the robot.
* [ ] The roboRIO is flat parallel to the earth.
* [ ] The robot DOES NOT MOVE AT ALL on startup for at least 10 seconds!

## Communication

The ADXRS450 attaches directly to the SPI port on the roboRIO and is only connectable via there with YAGSL.

## Example `swervedrive.json`

<pre class="language-json"><code class="lang-json">{
  "imu": {
    "type": <a data-footnote-ref href="#user-content-fn-1">"adxrs450"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-2">0</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-3">null</a>
  },
  "invertedIMU": <a data-footnote-ref href="#user-content-fn-4">false</a>,
  "modules": [
    "frontleft.json",
    "frontright.json",
    "backleft.json",
    "backright.json"
  ]
}
</code></pre>

[^1]: Select the `adxrs450` gyroscope as the primary gyroscope.

[^2]: ID is not relevant for the NavX so `0` is chosen arbitrarily.

[^3]: The `canbus` is not relavent for the NavX so `null` ensures nothing is set in the configuration.

[^4]: Reads default counterclockwise
