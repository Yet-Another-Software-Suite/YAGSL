---
description: This is helpful, sometimes
---

# Cosine Compensation

## How Cosine Compensation works?

Cosine compensation scales the speed of your wheel by the cosine of the angle delta of the swerve module.&#x20;

## How do I enable it?

{% hint style="warning" %}
Cosine compensation **does not work in simulation** and is inteded for use on the robot ONLY! Please test it thoroughly before deciding to use it in competition.
{% endhint %}

Cosine compensation is easy to enable! All it takes is the funciton `SwerveDrive.setCosineCompensator`!

<pre class="language-java"><code class="lang-java"> /**
   * Initialize {@link SwerveDrive} with the directory provided.
   *
   * @param directory Directory of swerve drive config files.
   */
  public SwerveSubsystem(File directory)
  {
    // Configure the Telemetry before creating the SwerveDrive to avoid unnecessary objects being created.
    SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
    swerveDrive = new SwerveParser(directory).createSwerveDrive(Constants.MAX_SPEED);
<strong>    swerveDrive.setCosineCompensator(false); // Disables cosine compensation for simulations since it causes discrepancies not seen in real life.
</strong>  }
</code></pre>
