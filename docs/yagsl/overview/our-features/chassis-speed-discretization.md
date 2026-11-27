---
description: More math...
---

# Chassis Speed Discretization

## What is Chassis Speed discretization?

Chassis Speed discretization is used to reduce the skew in translational movement of the Swerve Drive. It is called only if  [`SwerveDrive.chassisVelocityCorrection`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#chassisVelocityCorrection) is `true`.&#x20;

## How do I use Chassis Speed Discretization?

Chassis Speed discretization simply allows you to set your own values for [`ChassisSpeeds.discretize`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/ChassisSpeeds.html#discretize\(edu.wpi.first.math.kinematics.ChassisSpeeds,double\)) in WPILib, the default value of `0.02`dt should be good enough for most teams. To enable and customize discretization all you have to do is call [`SwerveDrive.setChassisDiscretization`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#setChassisDiscretization(boolean,double)) as shown below.

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
<strong>    swerveDrive.setChassisDiscretization(true, 0.02);
</strong>  }
</code></pre>
