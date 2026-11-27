# Telemetry

## How it works?

YAGSL uses telemetry in `SwerveDrive` and `SwerveModule` for consolidation. There are a few remainders of telemetry which is pushed from [`SwerveDrive.updateOdometry()`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#updateOdometry()). All Telemtry is bound by a verbosity level in [`SwerveDriveTelemtry.verbosity`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/telemetry/SwerveDriveTelemetry.html#verbosity) as a static variable. The following are the options which you can utilize.

```java
/**
 * Verbosity of telemetry data sent back.
 */
 public enum TelemetryVerbosity
{
  /**
   * No telemetry data is sent back.
   */
  NONE,
  /**
   * Low telemetry data, only post the robot position on the field.
   */
  LOW,
  /**
   * Medium telemetry data, swerve directory
   */
  INFO,
  /**
   * Info level + field info
   */
  POSE,
  /**
   * Full swerve drive data is sent back in both human and machine readable forms.
   */
  HIGH,
  /**
   * Only send the machine readable data related to swerve drive.
   */
  MACHINE
}
```

## How do I enable it?

{% hint style="warning" %}
Higher telemetry could induce some lag on the robot and slow down the cycle times. So be cautious on what you chose!
{% endhint %}

To configure the telemetry please change [`SwerveDriveTelemetry.verbosity`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/telemetry/SwerveDriveTelemetry.html#verbosity) to one of the [enum values](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/telemetry/SwerveDriveTelemetry.TelemetryVerbosity.html) that you desire.

<pre class="language-java"><code class="lang-java">  /**
   * Initialize {@link SwerveDrive} with the directory provided.
   *
   * @param directory Directory of swerve drive config files.
   */
  public SwerveSubsystem(File directory)
  {
    // Configure the Telemetry before creating the SwerveDrive to avoid unnecessary objects being created.
<strong>    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
</strong>    swerveDrive = new SwerveParser(directory).createSwerveDrive(Constants.MAX_SPEED);
  }
</code></pre>

## What does it do?

Telemetry outputs relevant Swerve Drive data to NetworkTables on the roboRIO. You can view these using any dashboard you like!

Some dashboard support swerve drive widgets based off of the `SmartDashboard/swerve` NetworkTable entry.

All Swerve Module data is stored under the relevant modules in `SmartDashboard/swerve/modules/` which is invaluable during setup.

### Swerve Module

<figure><img src="../../.gitbook/assets/image (3).png" alt="Shuffleboard Tree"><figcaption><p>Swerve Module Telemetry in Shuffleboard</p></figcaption></figure>

### Swerve Drive

<figure><img src="../../.gitbook/assets/image (1) (1).png" alt=""><figcaption><p>Swerve Drive Telemetry in Shuffleboard with circled relevant keys</p></figcaption></figure>

## Recommended Shuffleboard Layout

<figure><img src="../../.gitbook/assets/image (2) (1).png" alt=""><figcaption><p>Raw Absolute Encoder group on left side, Raw Angle Encoder group on right side.</p></figcaption></figure>

I use this layout during setup to ensure all modules are reading CCW+. This is the most common overlooked part while configuring swerve drives.

If the **Raw Angle Encoder** is CCW- (decreasing while rotated counter clockwise) then the angle motor needs to be inverted in the configuration file for that module.

If the **Raw Absolute Encoder** is CCW- (decreasing while the wheel is rotated counter clockwise) then the absolute encoder might need to be inverted for that module.

## Alternative Dashboard Support

There are a few dashboard which support YAGSL widgets too! These developers are amazing and you should definitely checkout their work!

* [Elastic Dashboard](https://github.com/Gold872/elastic-dashboard)
* [FRC Web Components](https://github.com/frc-web-components/app/releases/latest)
* [Advantage Scope](https://github.com/Mechanical-Advantage/AdvantageScope)
