# Gyroscope

YAGSL supports a wide variety of Gyroscopes in an effort to best help teams of all budgets, while we do recommend the NavX or Pigeon2 and officially support and have tested them, we do try to support others with varying degrees of success.

## Gyroscope Checklist

* [ ] [Gyroscope readings increase when rotated counter clockwise](#user-content-fn-1)[^1].
* [ ] Yaw reading is the robot heading.
* [ ] Gyroscope `0` is the desired robot "front".

## Swerve IMU wrapper

YAGSL creates wrappers over all supported Gyroscope types to uniformly fetch and set data that is needed for a Swerve Drive to operate, this wrapper is called [`SwerveIMU`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/imu/SwerveIMU.html). All Gyroscope's can be fetched from the [`SwerveDrive`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#swerveDriveConfiguration) object via the [`SwerveDriveConfiguration`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveDriveConfiguration.html) object which is generated from the `swervedrive.json` file given. For a user program to fetch the raw gyroscope object all you must do is as follows while casting the object to the right type, in our case this will be a NavX `AHRS` class.

<pre class="language-java"><code class="lang-java">   /**
   * Initialize {@link SwerveDrive} with the directory provided.
   *
   * @param directory Directory of swerve drive config files.
   */
  public SwerveSubsystem(File directory)
  {
    // Angle conversion factor is 360 / (GEAR RATIO * ENCODER RESOLUTION)
    //  In this case the gear ratio is 12.8 motor revolutions per wheel rotation.
    //  The encoder resolution per motor revolution is 1 per motor revolution.
    double angleConversionFactor = SwerveMath.calculateDegreesPerSteeringRotation(12.8, 1);
    // Motor conversion factor is (PI * WHEEL DIAMETER IN METERS) / (GEAR RATIO * ENCODER RESOLUTION).
    //  In this case the wheel diameter is 4 inches, which must be converted to meters to get meters/second.
    //  The gear ratio is 6.75 motor revolutions per wheel rotation.
    //  The encoder resolution per motor revolution is 1 per motor revolution.
    double driveConversionFactor = SwerveMath.calculateMetersPerRotation(Units.inchesToMeters(4), 6.75, 1);

    // Configure the Telemetry before creating the SwerveDrive to avoid unnecessary objects being created.
    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
    try
    {
      swerveDrive = new SwerveParser(directory).createSwerveDrive(maximumSpeed, angleConversionFactor, driveConversionFactor);
    } catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    swerveDrive.setHeadingCorrection(false); // Heading correction should only be used while controlling the robot via angle.

<strong>    <a data-footnote-ref href="#user-content-fn-2">AHRS</a> navx = (<a data-footnote-ref href="#user-content-fn-3">AHRS</a>)swerveDrive.getGyro().getIMU();
</strong>
  }
</code></pre>

## Recommended Gyroscopes

These gyroscopes have been thoroughly tested and are used by many FRC teams. Generally speaking these will give you the best performance and reliability as well as help from the community at large.

### [NavX2 MXP](gyroscope/navx.md) by Studica

{% embed url="https://www.studica.ca/en/navx-2-mxp-robotics-navigation-sensor" %}

## [Pigeon2 IMU](gyroscope/pigeon-2.0.md) by CTRE

{% embed url="https://store.ctr-electronics.com/pigeon-2/" %}

## Gyroscope Configuration

{% hint style="warning" %}
Only CTRE devices currently support the `canbus` option, if your device is using the roboRIO `canbus` you must use the value of `null` or `"rio"` for supported CTRE devices. If you are using a CANivore, and the device is on the CANivore bus, the name must match the CANivore name.
{% endhint %}

In `swervedrive.json` you specify the gyroscope with the

<pre class="language-json"><code class="lang-json">{
  "imu": {
<strong>    "type": "<a data-footnote-ref href="#user-content-fn-4">pigeon2</a>",
</strong><strong>    <a data-footnote-ref href="#user-content-fn-5">"id"</a>: <a data-footnote-ref href="#user-content-fn-6">13</a>,
</strong><strong>    <a data-footnote-ref href="#user-content-fn-7">"canbus"</a>: "<a data-footnote-ref href="#user-content-fn-8">canivore</a>"
</strong>  },
  "invertedIMU": true,
  "modules": [
    "frontleft.json",
    "frontright.json",
    "backleft.json",
    "backright.json"
  ]
}
</code></pre>

{% hint style="warning" %}
If your robot spins out of control without any controller input you probably need to invert your IMU here.
{% endhint %}

### Possible Gyroscope Types

| Device                                                                  | type                | Communication                                      |
| ----------------------------------------------------------------------- | ------------------- | -------------------------------------------------- |
| [Pigeon](gyroscope/pigeon.md)                                           | `pigeon`            | CAN; does not support CANivore.                    |
| [Pigeon2](gyroscope/pigeon-2.0.md)                                      | `pigeon2`           | CAN; supports CANivore                             |
| [Canandgyro](https://docs.reduxrobotics.com/canandgyro/getting-started) | `canandgyro`        | CAN; does not support CANivore.                    |
| [NavX](gyroscope/navx.md)                                               | `navx` , `navx_spi` | roboRIO MXP SPI                                    |
| [NavX](gyroscope/navx.md)                                               | `navx_i2c`          | I2C port on the roboRIO MXP. Not recommended!      |
| [NavX](gyroscope/navx.md)                                               | `navx_usb`          | Serial over USB Cable to roboRIO (not recommended) |
| [NavX](gyroscope/navx.md)                                               | `navx_mxp_serial`   | Serial over roboRIO MXP                            |
| [NavX3](gyroscope/navx3.md)                                            | `navx3`             | CAN                                                |
| [ADIS16448](gyroscope/adis16448.md)                                     | `adis16448`         | roboRIO MXP                                        |
| [ADIS16470](gyroscope/adis16470.md)                                     | `adis16470`         | roboRIO SPI port                                   |
| [ADXRS450](gyroscope/adxrs450.md)                                       | `adxrs450`          | roboRIO SPI port.                                  |
| Analog Gyro                                                             | `analog`            | AnalogInput                                        |

[^1]: Also known as CounterClockWise Positive or CCW+

[^2]: NavX `AHRS` class which is used to represent and communicate with the NavX in Java.

[^3]: Casts java `Object` to `AHRS` class this should be changed to whatever the native class is for your Gyroscope.

[^4]: Selects the `pigeon2` IMU device to instantiate and use.

[^5]: This refers to the CAN ID in this case however it could refer to the AnalogInput of the roboRIO depending on the selected type.

[^6]: CAN ID of the Pigeon 2 is 13

[^7]: The CAN bus is not always used and can be `null` or `""` to select the default (roboRIO) CAN bus which is normally `"rio"`

[^8]: name should match the canivore name if the device is on that CAN bus, else it should be `"rio"` or `null` or `""` to indicate the roboRIO CAN bus.
