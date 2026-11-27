# Heading Correction

## What is Heading Correction?

Heading correction is used to keep the robot heading the same as the previous heading while the robot is translating. It is aggressive and will prevent angular rotation based control schemes from working.

Heading correction was added to YAGSL by Team 1466 and improved upon by 7525 Pioneers and BoiledBurntBagel of 6036.

## How do I enable it?

You can enable or disable heading correction using [`SwerveDrive.setHeadingCorrection`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#setHeadingCorrection(boolean)) from anywhere. The deadband is an arbitrary value that represents both meters persecond and radians epr second.&#x20;

## What does heading correction do in code?

Heading correction is used in [`SwerveDrive.drive`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#drive(edu.wpi.first.math.kinematics.ChassisSpeeds,boolean,edu.wpi.first.math.geometry.Translation2d)) to control the heading via the deadband [`SwerveDrive.setHeadingCorrection`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#setHeadingCorrection(boolean,double)). Heading correction uses the heading PID from `controllerproperties.json` and the current yaw to calculate an omega turning speed using [`SwerveController.headingCalculate`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveController.html#headingCalculate(double,double)).

```java
    // Heading Angular Velocity Deadband, might make a configuration option later.
    // Originally made by Team 1466 Webb Robotics.
    // Modified by Team 7525 Pioneers and BoiledBurntBagel of 6036
    if (headingCorrection)
    {
      if (Math.abs(velocity.omegaRadiansPerSecond) < HEADING_CORRECTION_DEADBAND
          && (Math.abs(velocity.vxMetersPerSecond) > HEADING_CORRECTION_DEADBAND
              || Math.abs(velocity.vyMetersPerSecond) > HEADING_CORRECTION_DEADBAND))
      {
        if (!correctionEnabled)
        {
          lastHeadingRadians = getYaw().getRadians();
          correctionEnabled = true;
        }
        velocity.omegaRadiansPerSecond =
            swerveController.headingCalculate(lastHeadingRadians, getYaw().getRadians());
      } else
      {
        correctionEnabled = false;
      }
    }
```
