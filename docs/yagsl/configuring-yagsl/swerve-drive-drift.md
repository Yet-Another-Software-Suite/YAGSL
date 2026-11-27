---
description: Drift can be caused by anything and everything in a swerve drive...
---

# Swerve Drive Drift

Swerve drive drift has been a persistent problem for many years and there have been many discussions as to why it happens. This page will guide you through 3 categories of common causes of swerve drive drift. It is important to keep in mind that SEVERAL of these issues could be present in your robot.

## Hardware

### Wiring

Incorrect wiring can be further broken down into 3 categories for the swerve drive. These are the symptoms if the wiring is worn down, incorrectly routed, or broken some where (especially in connectors!)&#x20;

#### Encoder

* [ ] Telemetry readings will be erratic and nonsensicle.&#x20;
* [ ] Motor could spin out of control.
* [ ] Errors in the driver station console may appear.

#### Motor Controller

* [ ] Telemetry may be slow to update.
* [ ] Motors may suddenly stop or spin out of control.
* [ ] Motors may not respond to commands.
* [ ] Errors in the driver station console may appear.

#### Gyroscope/IMU

* [ ] Telemetry may be slow to update.
* [ ] Field Relative robot control may be unusable.
* [ ] Autonomous drifts uncontrollably.
* [ ] Errors in the driver station may appear.
* [ ] If your gyro is moving/shaking violently.
* [ ] If your gyro shifted during movement.

### Physical Components

#### Magnetic Encoder

* [ ] Magnet not secured to the swerve module and slips while in motion.&#x20;
  * Absolute encoder offsets would constantly need to be readjusted until magnet is secured.
* [ ] Magnetic encoder is not secured to the swerve module.

#### Motor

* [ ] Motor may not have firm connection to swerve module shaft.
  * Motor would "twitch" while running if a amperage limit is set.
* [ ] Motor is dying.
  * Motor may be pulling more amperage to get the same speeds.

#### Swerve Module

* [ ] If your wheels were misaligned
* [ ] Modules are inconsistent with the same PID.
  * Grease may need to be applied to the gears.

#### Robot

* [ ] Robot center of gravity is not ideal.

## Software

* [ ] Constant `Command Scheduler loop overruns` because of other parts of your code.
* [ ] Maximum physical velocity is not set correctly.
* [ ] If using open loop, modules run at different speeds.
* [ ] Controller input filtering.
* [ ] Use of [`SwerveDrive.setCosineCompensator`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#setCosineCompensator(boolean))
* [ ] Use of [`SwerveDrive.setHeadingCorrection`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#setHeadingCorrection(boolean))
* [ ] Use of  [`SwerveModule.setAntiJitter`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveModule.html#setAntiJitter(boolean))
* [ ] Use of  [`SwerveDrive.chassisVelocityCorrection`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#chassisVelocityCorrection)
* [ ] Use of [`SwerveDrive.setMaximumSpeeds`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#setMaximumAllowableSpeeds(double,double))
* [ ] Use of [`SwerveDrive.replaceSwerveModuleFeedforward`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#replaceSwerveModuleFeedforward(edu.wpi.first.math.controller.SimpleMotorFeedforward))
* [ ] Use of [`SwerveDrive.setGyroOffset`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#setGyroOffset(edu.wpi.first.math.geometry.Rotation3d))
* [ ] Use of [`SwerveDrive.updateCacheValidityPeriods`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#updateCacheValidityPeriods(long,long,long))
* [ ] Use of [`SwerveDrive.setOdometryPeriod`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#setOdometryPeriod(double))
* [ ] Use of [`SwerveDrive.addVisionMeasurement`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#addVisionMeasurement(edu.wpi.first.math.geometry.Pose2d,double))
* [ ] Use of `SwerveDrive.setChassisDiscretization`

### PathPlanner

* [ ] PathPlanner `AutoBuilder` PID's are not tuned well.
* [ ] Maximum module velocity is too high or too low.
* [ ] Testing path is curved.
* [ ] PathPlanner auto doesn't define preset pose.
* [ ] PathPlanner path doesnt reset odometry on start.

## Configuration

* [ ] Module locations are not correctly defined.
  * Remember these are from the center of the robot to the center of the module!
* [ ] Swerve Module configurations do not have the correct definitions for the Absolute Encoder, Angle Motor, or Drive motor.
* [ ] The conversion factor is defined inside of swerve module configuration files is different from the one in `physicalproperties.json`
  * The conversion factors in the swerve module files are completely **OPTIONAL** and do not need to be defined unless you have a reason. You should only define them in the code or use `physicalproperties.json` to avoid redundancy.
* [ ] The conversion factor is not correct.
  * If you see your swerve drive constantly going further than it should with pathplanner this is probably the case.
  * If your angle motors never align the module correctly this may also be a cause of it.
* [ ] Drive and steering/azimuth/angle motor PIDs are not tuned well enough.
* [ ] The amperage limit is too low.
* [ ] [Translational axis changes with robot orientation.](the-eight-steps.md)
