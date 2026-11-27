---
description: YAGSL-Example provides simulation out of the box!
---

# Simulation

## How it works?

YAGSL Simulates the entire robot using the same simulation modules provided by vendors to varying degrees of support. There is an entirely simulated SwerveModule named `SwerveModuleSimulation` and `SwerveIMUSimulation` which provide necessary data.

## How do I enable it?

{% hint style="warning" %}
Cosine compensation and heading correction should be disabled when running simulation or else the simulation may be uncontrollable!
{% endhint %}

For simulation to work properly all you have to do is ensure that heading compensation and cosine compensation are disabled like in the example below.

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
<strong>    swerveDrive.setHeadingCorrection(false); // Heading correction should only be used while controlling the robot via angle.
</strong><strong>    swerveDrive.setCosineCompensator(false); // Disables cosine compensation for simulations since it causes discrepancies not seen in real life.
</strong>  }
</code></pre>

Then run simulation like you normally would in wpilib.

For more information checkout WPILib documentaiton on simulation!

{% embed url="https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-simulation/introduction.html" %}
