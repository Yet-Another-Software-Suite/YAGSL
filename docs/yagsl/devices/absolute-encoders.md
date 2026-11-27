# Absolute Encoders

YAGSL supports most common FRC absolute encoders. Absolute encoders are required if you are using a brushed motor .

The absolute encoder value will show up in your driver dashboard under `swerve/modules/.../Raw Absolute Encoder` and can be used to tune the `absoluteEncoderOffset` in module JSON configuration files under most circumstances.

## Absolute Encoder Checklist

* [ ] All Absolute Encoder's spin counterclockwise positive.
* [ ] Magnetic Absolute encoders have a good read on the magnet while still and in operation (while the robot moves).
* [ ] Absolute Encoders have unique CAN ID's or Analog Input Channel's.
* [ ] Absolute Encoder are defined with the correct ID or Analog Input Channel.
* [ ] Absolute Encoders have full range (`0`-`360`)

## Swerve Absolute Encoder Wrapper

YAGSL created wrappers over all supported Absolute Encoders to uniformly fetch and set data that is needed for a Swerve Module to operate. This wrapper is called [`SwerveAbsoluteEncoder`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/encoders/SwerveAbsoluteEncoder.html). All [`SwerveAbsoluteEncoder`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/encoders/SwerveAbsoluteEncoder.html)'s can be fetched via the [`SwerveModule`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveModule.html#configuration) configuration object [`SwerveModuleConfiguration`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveModuleConfiguration.html) absolute encoder attribute [`absoluteEncoder`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveModuleConfiguration.html#absoluteEncoder). The [`SwerveModule`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveModule.html) is able to be fetched by [`SwerveDrive.getModules()`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#getModules()) easily.

YAGSL created wrappers over all supported Motor Controllers to uniformly fetch and set data that is needed for a Swerve Drive to operate. This wrapper is called [`SwerveMotor`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/motors/SwerveMotor.html). All [`SwerveMotor`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/motors/SwerveMotor.html)'s can be fetched via the [`SwerveModule`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveModule.html#configuration) configuration object [`SwerveModuleConfiguration`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveModuleConfiguration.html) motor definitions [`angleMotor`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveModuleConfiguration.html#angleMotor) and [`driveMotor`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveModuleConfiguration.html#driveMotor). The [`SwerveModule`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveModule.html) is able to be fetched by [`SwerveDrive.getModules()`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#getModules()) easily.

<pre class="language-java"><code class="lang-java">import com.ctre.phoenix6.hardware.CANcoder;
 
   /**
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

    for(SwerveModule m : swerveDrive.getModules())
    {
      System.out.println("Module Name: "+m.configuration.name);
      <a data-footnote-ref href="#user-content-fn-1">CANcoder</a> absoluteEncoder = (<a data-footnote-ref href="#user-content-fn-2">CANcoder</a>)m.configuration.absoluteEncoder.getAbsoluteEncoder();
    }
  }
</code></pre>

## Absolute Encoder Configuration

{% hint style="warning" %}
Only CTRE devices currently support the `canbus` option, if your device is using the roboRIO `canbus` you must use the value of `null` or `"rio"` for supported CTRE devices. If you are using a CANivore, and the device is on the CANivore bus, the name must match the CANivore name.
{% endhint %}

{% hint style="success" %}
If your absolute encoder is attached to your SparkMAX, use the function [`SwerveDrive.pushOffsetsToEncoders()`](https://broncbotz3481.github.io/YAGSL-Lib/docs/swervelib/SwerveDrive.html#pushOffsetsToEncoders\(\)) for the best performance. This sets the onboard PID sensor to the attached encoder!
{% endhint %}

Inside any module JSON such as `frontleft.json`,`frontright.json`,`backleft.json`,`backright.json` this is what you would see to configure a absolute encoder.

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "type": <a data-footnote-ref href="#user-content-fn-3">"sparkmax"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-4">5</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-5">null</a>
  },
  "angle": {
    "type": <a data-footnote-ref href="#user-content-fn-6">"sparkmax"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-7">6</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-8">null</a>
  },
  "encoder": {
    "type": <a data-footnote-ref href="#user-content-fn-9">"cancoder"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-10">11</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-11">null</a>
  },
  "inverted": {
    "drive": <a data-footnote-ref href="#user-content-fn-12">false</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-13">false</a>
  },
  "absoluteEncoderOffset": <a data-footnote-ref href="#user-content-fn-14">-18.281</a>,
  "absoluteEncoderInverted": <a data-footnote-ref href="#user-content-fn-15">false</a>,
  "location": {
    "front": <a data-footnote-ref href="#user-content-fn-16">-12</a>,
    "left": <a data-footnote-ref href="#user-content-fn-17">-12</a>
  }
}
</code></pre>

## Possible Absolute Encoder Types

{% hint style="warning" %}
Try inverting your steering/angle/azimuth motor if your module keeps spinning around.
{% endhint %}

<table data-full-width="true"><thead><tr><th width="538">Device</th><th width="269">type</th></tr></thead><tbody><tr><td>None</td><td><code>none</code></td></tr><tr><td><a href="https://docs.revrobotics.com/brushless/spark-max/encoders/absolute#data-port-connector-information">Integrated/Attached</a> (via SparkMAX DutyCycle)</td><td><a data-footnote-ref href="#user-content-fn-18"><code>attached</code></a></td></tr><tr><td><a href="https://docs.revrobotics.com/brushless/spark-max/encoders/absolute#data-port-connector-information">SparkMax Analog</a> (via SparkMAX Analog Pin)</td><td><a data-footnote-ref href="#user-content-fn-19"><code>sparkmax_analog</code></a></td></tr><tr><td><a href="https://docs.revrobotics.com/brushless/spark-max/encoders/absolute#data-port-connector-information">SparkMax Analog</a> (via SparkMAX Analog Pin  with 5V power)</td><td><a data-footnote-ref href="#user-content-fn-20"><code>sparkmax_analog5v</code></a></td></tr><tr><td><a href="https://docs.reduxrobotics.com/canandmag/spark-max#using-the-pwm-output-with-spark-max">Canandmag</a> (via SparkMAX)</td><td><a data-footnote-ref href="#user-content-fn-21"><code>canandmag</code></a></td></tr><tr><td><a href="https://docs.reduxrobotics.com/canandmag/getting-started">Canandmag </a>(via CAN)</td><td><a data-footnote-ref href="#user-content-fn-22"><code>canandmag_can</code></a></td></tr><tr><td><a href="https://pro.docs.ctr-electronics.com/en/latest/docs/hardware-reference/cancoder/index.html">CANcoder</a></td><td><a data-footnote-ref href="#user-content-fn-23"><code>cancoder</code></a></td></tr><tr><td><a href="https://www.revrobotics.com/rev-11-1271/">Throughbore</a> (via DIO)</td><td><a data-footnote-ref href="#user-content-fn-24"><code>throughbore</code></a></td></tr><tr><td><a href="https://www.thethriftybot.com/products/thrifty-absolute-magnetic-encoder">Thrifty Absolute Magnetic Encoder</a> (via Analog Input)</td><td><a data-footnote-ref href="#user-content-fn-25"><code>thrifty</code></a></td></tr><tr><td><a href="https://www.andymark.com/products/ma3-absolute-encoder-with-cable">MA3</a> (via Analog Input)</td><td><a data-footnote-ref href="#user-content-fn-26"><code>ma3</code></a></td></tr><tr><td><a href="https://store.ctr-electronics.com/srx-mag-encoder/">SRX Mag</a> (via PWM)</td><td><a data-footnote-ref href="#user-content-fn-27"><code>ctre_mag</code></a></td></tr><tr><td><a href="https://www.andymark.com/products/am-mag-encoder">AM Mag</a> (via PWM)</td><td><a data-footnote-ref href="#user-content-fn-28"><code>am_mag</code></a></td></tr><tr><td>PWM DutyCycle</td><td><code>dutycycle</code></td></tr><tr><td>Analog Encoder</td><td><code>analog</code></td></tr></tbody></table>

[^1]: [`CANcoder`](https://api.ctr-electronics.com/phoenix6/release/java/com/ctre/phoenix6/hardware/CANcoder.html) from Phoenix 6, initialized via CAN bus and CAN ID from configurations.

[^2]: [`CANcoder`](https://api.ctr-electronics.com/phoenix6/release/java/com/ctre/phoenix6/hardware/CANcoder.html) from Phoenix 6, initialized via CAN bus and CAN ID from configurations. Cast `Object` to [`CANcoder`](https://api.ctr-electronics.com/phoenix6/release/java/com/ctre/phoenix6/hardware/CANcoder.html)

[^3]: SparkMAX brushless mode is selected.

[^4]: The SparkMAX has a CAN ID of `5`.

[^5]: SparkMAX is not compatible with CANivore so the `canbus` should be `null` or `""`.

[^6]: SparkMAX brushless mode is selected.

[^7]: The SparkMAX has a CAN ID of `6`.

[^8]: SparkMAX is not compatible with CANivore so the `canbus` should be `null` or `""`.

[^9]: [`CANcoder`](https://api.ctr-electronics.com/phoenix6/release/java/com/ctre/phoenix6/hardware/CANcoder.html) is selected using the CAN ID and bus bellow.

[^10]: [`CANcoder`](https://api.ctr-electronics.com/phoenix6/release/java/com/ctre/phoenix6/hardware/CANcoder.html) id for this module.

[^11]: CANivore name of which the [`CANcoder`](https://api.ctr-electronics.com/phoenix6/release/java/com/ctre/phoenix6/hardware/CANcoder.html) resides on.

[^12]: The drive motor spins counter clockwise positive without any inversion.

[^13]: The steering/angle/azimuth motor spins counterclockwise positive without inversion.

[^14]: Offset read from `Module[...] Raw Absolute Encoder` when the wheel was facing straight and bevels the same way.\
    \
    Offsets are in degrees.

[^15]: This should rarely, if ever, be true.

[^16]: The center of this module is `-12`in from the center of the robot "frontwise".

[^17]: The center of this module is `-12`in from the center of the robot "left".

[^18]: ```json
    "encoder": {
        "type": "attached",
        "id": 11,
        "canbus": null
      },
    ```

[^19]: ```json
    "encoder": {
        "type": "sparkmax_analog",
        "id": 11,
        "canbus": null
      },
    ```

[^20]: ```json
    "encoder": {
        "type": "sparkmax_analog5v",
        "id": 11,
        "canbus": null
      },
    ```

[^21]: ```json
    "encoder": {
        "type": "canandmag",
        "id": 11,
        "canbus": null
      },
    ```

[^22]: ```json
    "encoder": {
        "type": "canandmag_can",
        "id": 11,
        "canbus": null
      },
    ```

[^23]: ```json
    "encoder": {
        "type": "cancoder",
        "id": 11,
        "canbus": null
      },
    ```

[^24]: ```json
    "encoder": {
        "type": "throughbore",
        "id": 11,
        "canbus": null
      },
    ```

[^25]: ```json
    "encoder": {
        "type": "thrifty",
        "id": 11,
        "canbus": null
      },
    ```

[^26]: ```json
    "encoder": {
        "type": "ma3",
        "id": 11,
        "canbus": null
      },
    ```

[^27]: ```json
    "encoder": {
        "type": "ctre_mag",
        "id": 11,
        "canbus": null
      },
    ```

[^28]: ```json
    "encoder": {
        "type": "am_mag",
        "id": 11,
        "canbus": null
      },
    ```
