---
description: This one is a bit complicated
---

# Max Speed

## What is Max Speed?

YAGSL stores the maximum speed and uses it in the following functions

* [ ] [`SwerveDriveKinematics.desaturateWheelSpeeds`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveDriveKinematics.html#desaturateWheelSpeeds\(edu.wpi.first.math.kinematics.SwerveModuleState\[],double\))
* [ ] [`SwerveMath.calculateMaxAngularVelocity`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/math/SwerveMath.html#calculateMaxAngularVelocity(double,double,double))
* [ ] [`SwerveDriveTelemetry.maxSpeed`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/telemetry/SwerveDriveTelemetry.html#maxSpeed)
* [ ] [`SwerveMath.antiJitter`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/math/SwerveMath.html#antiJitter(edu.wpi.first.math.kinematics.SwerveModuleState,edu.wpi.first.math.kinematics.SwerveModuleState,double))
* [ ] [`SwerveMath.createDriveFeedforward`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/math/SwerveMath.html#createDriveFeedforward(double,double,double))

The maximum speed represents the physical maximum speed of the robot.

## How do I change my Max Speed?

You can change your maximum speed by using `SwerveDrive.setMaximumSpeed` the initial maximum speed is given in [`SwerveParser.createSwerveDrive`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/SwerveParser.html#createSwerveDrive(double)) for initial values like the maximum angular velocity and drive feedforward and telemetry maximum speed.

{% hint style="info" %}
The maximum speed in telemetry does not change because most widgets do not support this and it might break them!
{% endhint %}

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
<strong>    swerveDrive.setMaximumSpeed(Units.feetToMeters(12.4)); // 12.4ft/s to m/s
</strong>  }
</code></pre>
