---
description: >-
  Auto-centering modules could cause some unwanted jitter which is why it is
  disabled by default!
---

# Auto-centering Modules

## What is auto-centering modules?

Auto-centering is how your robot will center the wheels when there is no input provided. When auto-centering is enabled the Swerve Module will "snap" to `0` every time there is no input form the controller or autonomous. This can be desired by some teams but it is in no way recommended. Auto-centering causes issues at startup that sometimes misaligns your robot for autonomous, this can also cause more drift than normal. Auto-centering modules works by calling [`SwerveModule.setAntiJitter(false)`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveModule.html#setAntiJitter(boolean)) so all side-effects of that action are present in swerve drives which use this!

## How do I enable auto-centering modules?

You enable auto-centering by using `SwerveDrive.setAutoCenteringModules(true)`

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
<strong>    swerveDrive.setAutoCenteringModules(true);
</strong>  }
</code></pre>

