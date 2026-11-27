---
description: How does Swerve Drive work?
---

# Swerve Drive Kinematics

<figure><img src="../.gitbook/assets/sim_sample.png" alt=""><figcaption><p>Swerve Drive simulation</p></figcaption></figure>

## Tips while building a swerve drive

* Center the gyroscope in the robot, this will help prevent a small drift and ensure more accurate odometry.
* Make sure the magnets (if you're using them) are secured correctly.
* Set aside time for rework, or keep a spare module during competitions.
* Programming a Swerve Drive requires you to understand its behavior.
* Use the right tools for the job. Try a dashboard like [AdvantageScope](https://github.com/Mechanical-Advantage/AdvantageScope/tree/main). See [how to set up AdvantageScope](../tune-and-extend/set-up-advantagescope.md).

## The basics

Swerve Drives move around by moving each wheel to a specific angle/azimuth and rotating the wheel to go in that direction. Swerve Drives are unique because they can rotate independently of their translational movement, meaning you can move in any direction while facing any direction. As a result you can "turn in-place" and rotate while moving around an area. The rotation of your robot is referred to as the **heading.**

## What is a swerve drive?

A Swerve Drive typically consists of four Swerve Modules (which are in essence a drive motor, an angle/azimuth motor, and an absolute encoder), and a gyroscope (centered is best). The motors, absolute encoders, and gyroscope do not matter and can all work together with varying degrees of success. As a rule of thumb, if you can stick to one system do it (all REV, all CTRE). This will give you the best feature set, however they are not required to be the same. For all other use cases YAGSL is the best choice because it (and YAMS underneath it) was built with abstraction in mind, making all sensors and motor controllers functionally equivalent.

#### TL;DR

A swerve drive is composed of

* [ ] Gyroscope
* [ ] Swerve Module
  * [ ] Angle/Azimuth Motor (+ controller)
  * [ ] Drive Motor
  * [ ] Absolute Encoder

#### TL;DR Things that cause issues

* [ ] Bad Center of Gravity
* [ ] Non-centered gyroscope
* [ ] Non-square drive train

This is not a complete list and will grow over time.

## How does a Swerve Drive work in code?

Swerve Drives move each module to a specific angle determined by the direction you want to go and heading you want to face. For FRC we can get these values by hand by calculating the kinematics of the robot, or use WPILib's `SwerveDriveKinematics` (`org.wpilib.math.kinematics`), which uses the module locations to determine what the rotation and speed of each wheel should be given a `ChassisVelocities` object, and returns a `SwerveModuleVelocity` array. The `SwerveModuleVelocity` can then be used to set the angle/azimuth and speed corresponding to each Swerve Module to go in the desired direction while facing the desired heading.

{% hint style="info" %}
YAMS's `SwerveDrive` builds and manages `SwerveDriveKinematics`, `SwerveModuleVelocity`, and odometry for you internally based on the module locations in your JSON config. The illustration below shows what it's doing under the hood. It is useful for debugging, not something you need to write yourself.
{% endhint %}

### `SwerveDriveKinematics`

<pre class="language-java" data-title="SwerveDrive.java" data-line-numbers data-full-width="false"><code class="lang-java">// Import relevant classes.
import static org.wpilib.units.Units.Inches;
import static org.wpilib.units.Units.Meters;

import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.SwerveDriveKinematics;

// Illustrative example of what YAMS builds internally from your module locations.
public class SwerveDrive {

    // Attributes
<strong>    SwerveDriveKinematics kinematics;
</strong>
    // Constructor
    public SwerveDrive() {
        // Create SwerveDriveKinematics object
        // 12.5in from center of robot to center of wheel.
        // 12.5in is converted to meters to work with object.
        // Translation2d(x,y) == Translation2d(front, left)
<strong>        double offset = Inches.of(12.5).in(Meters);
</strong><strong>        kinematics = new SwerveDriveKinematics(
</strong><strong>            new Translation2d(offset, offset),   // Front Left
</strong><strong>            new Translation2d(offset, -offset),  // Front Right
</strong><strong>            new Translation2d(-offset, offset),  // Back Left
</strong><strong>            new Translation2d(-offset, -offset)  // Back Right
</strong><strong>        );
</strong>    }
}
</code></pre>

{% hint style="info" %}
The order defines what the output order of module angle/azimuth's and speeds will be! This is exactly why each module JSON file has a `location` field. See the [JSON Schema reference](../reference/json-schema/module-json.md).
{% endhint %}

### `SwerveModuleVelocity`

`SwerveDriveKinematics` is used to generate the `SwerveModuleVelocity` of each module in the given order. The example below shows how you can do this in a `drive()` function given a `ChassisVelocities` object.

`SwerveModuleVelocity` has two properties reflecting the properties of a Swerve Module: `angle` and `velocity` (in meters per second). The goal is to set the correct swerve module (based on the order given at construction of the `SwerveDriveKinematics` object) to the angle and velocity given in the `SwerveModuleVelocity`.

<figure><img src="../.gitbook/assets/yagsl-kinematics.gif" alt="An animation of a robot seen from above with four modules. For each ChassisVelocities, each module&#x27;s velocity is drawn as the robot&#x27;s velocity plus the spin at that module, and the sum is its SwerveModuleVelocity. Driving forward, every module points forward. Driving left, every module points left. Spinning counterclockwise, every module is tangent to a circle around the center. Driving forward while spinning, each module&#x27;s angle and speed differ. A table lists each module&#x27;s angle and velocity."><figcaption><p>Each module's velocity is the robot's velocity plus the spin at that module.</p></figcaption></figure>

<pre class="language-java" data-title="SwerveDrive.java" data-line-numbers data-full-width="false"><code class="lang-java">// Simple drive function
public void drive()
{
    // Create test ChassisVelocities going X = 14in per second, Y = 4in per second, and spinning at 30deg per second.
    // ChassisVelocities takes meters per second and radians per second.
<strong>    ChassisVelocities testVelocities = new ChassisVelocities(Inches.of(14).in(Meters), Inches.of(4).in(Meters), Math.toRadians(30));
</strong>
    // Get the SwerveModuleVelocity for each module given the desired velocities.
<strong>    SwerveModuleVelocity[] moduleVelocities = kinematics.toSwerveModuleVelocities(testVelocities);
</strong><strong>    // Output order is Front-Left, Front-Right, Back-Left, Back-Right
</strong>}
</code></pre>

{% hint style="danger" %}
Swerve Drive code does not work without [`SwerveDriveOdometry`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveDriveOdometry.html) or [`SwerveDrivePoseEstimator`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/estimator/SwerveDrivePoseEstimator.html) to keep track of module positions and angles. YAMS's `SwerveDrive` maintains a `SwerveDrivePoseEstimator` for you. See [Telemetry, Simulation & Vision](telemetry-and-vision.md).
{% endhint %}

### `SwerveDriveOdometry`

Life isn't that easy. You have to keep continuous track of the robot's current positioning, specifically the **heading**, **speed**, and **module positions**, collectively known as **odometry**. This is the only way to correctly generate usable `SwerveModuleVelocity`s.

Odometry must be updated every single loop, just like a subsystem's `periodic()`. YAMS's `SwerveDrive` does this for you inside `drive.updateTelemetry()`, which is why your subsystem (Commands v2) or robot class (Commands v3) must call it every loop. You don't need to call a separate odometry update method yourself, but understanding _why_ it must happen every loop is essential for debugging drift and lag.

{% hint style="info" %}
`SwerveDriveOdometry` can be replaced by `SwerveDrivePoseEstimator`, which additionally fuses vision measurements; this is what YAMS uses. See [Telemetry, Simulation & Vision](telemetry-and-vision.md).
{% endhint %}

## Conclusion

There are many more intricacies to Swerve Drives than covered on this page, but this is sufficient for a basic understanding of how a Swerve Drive is programmed. We'd highly encourage the reader to look at as many examples as they can find to understand some of the gotchas further, or continue on to use YAGSL.
