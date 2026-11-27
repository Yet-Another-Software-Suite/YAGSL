# Swerve Drive Configuration

## Swerve Drive (`swervedrive.json`)

The Swerve Drive JSON configuration file configures everything related to the overall Swerve Drive. It maps 1:1 to [`SwerveDriveJson`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/json/SwerveDriveJson.html) which creates a [`SwerveDriveConfiguration`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveDriveConfiguration.html) that is used to create the [`SwerveDrive`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html) object.

## JSON Fields

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th>Required</th><th>Description</th></tr></thead><tbody><tr><td><code>imu</code></td><td><a href="../../devices/gyroscope.md#gyroscope-configuration">Gyroscope</a></td><td>Y</td><td>Robot IMU used to determine heading of the robot.</td></tr><tr><td><code>invertedIMU</code></td><td>Boolean</td><td>Y</td><td>Inversion state of the IMU.</td></tr><tr><td><code>modules</code></td><td>String array</td><td>Y</td><td>Module JSONs in order clockwise order starting from front left.</td></tr></tbody></table>

## Example Configuration

<pre class="language-json"><code class="lang-json">{
  "imu": {
    <a data-footnote-ref href="#user-content-fn-1">"type": "pigeon2"</a>,
    <a data-footnote-ref href="#user-content-fn-2">"id": 13</a>,
    <a data-footnote-ref href="#user-content-fn-3">"canbus"</a>: <a data-footnote-ref href="#user-content-fn-4">"canivore"</a>
  },
  "invertedIMU": true,
  "modules": [
    "frontleft.json",
    "frontright.json",
    "backleft.json",
    "backright.json"
  ]
}
</code></pre>

[^1]: See more information [gyroscope.md](../../devices/gyroscope.md "mention")

[^2]: When the IMU/gyro is connected to the roboRIO this should be the channel number that it is on, if it is not connected via the SPI or MXP port. \
    \
    If the device is a CAN device this would be the CAN ID of the device.

[^3]: In this example the CANivore is named `"canivore"` and the `pigeon2` is connected to the CANivore instead of the roboRIO's CAN loop (which would be `null` or `"rio"` if the device were located on it).

[^4]: If the IMU/gyroscope is not on a CANivore this should be `null` or `"rio"`. &#x20;
