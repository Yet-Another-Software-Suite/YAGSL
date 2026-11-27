---
description: This is only supported on some motor controllers!
---

# Offset Offloading

## What is Offset Offloading?

Offset Offloading is where the absolute encoder offset is stored on the absolute encoder (or Motor Controller if the absolute encoder is attached to the motor controller) instead of the roboRIO. This normally results in a faster loop cycle on the roboRIO however it can add some instability to the Swerve Drive if anything breaks during a match, like a CAN bus or a low brown-out.&#x20;

## How do I enable Offset Offloading?

{% hint style="warning" %}
[`SwerveDrive.pushOffsetsToEncoders`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#pushOffsetsToEncoders()) and [`SwerveDrive.restoreInternalOffset`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#restoreInternalOffset()) are now deprecated as of YAGSL 2026.
{% endhint %}

Offset offloading is enabled by [`SwerveDrive.pushOffsetsToEncoders`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#pushOffsetsToEncoders()) and disabled by using [`SwerveDrive.restoreInternalOffset`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#restoreInternalOffset()) both of these call  a function of the same name in `SwerveModule` for every instiantiated module.

<pre class="language-java"><code class="lang-java">  /**
   * Initialize {@link SwerveDrive} with the directory provided.
   *
   * @param directory Directory of swerve drive config files.
   */
  public SwerveSubsystem(File directory)
  {
    // Configure the Telemetry before creating the SwerveDrive to avoid unnecessary objects being created.
    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
    swerveDrive = new SwerveParser(directory).createSwerveDrive(Constants.MAX_SPEED);
<strong>    swerveDrive.pushOffsetsToEncoders();
</strong>  }
</code></pre>
