---
description: How does Swerve Drive work?
---

# 🦀 Swerve Drive

<figure><img src="../../.gitbook/assets/image.png" alt=""><figcaption><p>Swerve Drive simulation from YAGSL</p></figcaption></figure>

## Tips while building a Swerve Drive

* Center the gyroscope in the robot, this will help prevent a small drift and ensure more accurate odometry.
* Make sure the magnets (if you're using them) are glue'd in right!
* Set aside time, assume you will mess up building 1 module or otherwise need a spare during competitions.
* Programming a Swerve Drive is hard, and while YAGSL tries to make it easier there are many things you must know to fully understand what you are doing!
* Use the right tools for the job! Debugging a Swerve Drive is difficult enough by text only, try out other Dashboards like [AdvantageScope](https://github.com/Mechanical-Advantage/AdvantageScope/blob/main/docs/NAVIGATION.md), or [FRC Web Components](https://github.com/frc-web-components/app/releases) they have excellent visualization tools that are sure to help you out!

## The Basics

Swerve Drives move around by moving each wheel to a specific angle/azimuth and rotating the wheel to go in that direction. Swerve Drives are unique because they can rotate independently of their translational movement, meaning you can move in any direction while facing any direction. As a result of the you can "turn in-place" and rotate while moving around an area. The rotation of your robot is referred to as the **heading.**

## What is a Swerve Drive?

A Swerve Drive typically consists of 4 Swerve Modules (which are in essence a drive motor, a angle/azimuth motor, and an absolute encoder),  and a gyroscope (centered is best). The motors, absolute encoders, and gyroscope do not matter and can all work together with varying degrees of success. As a rule of thumb if you can stick to one system do it (all REV, all CTRE). This will give you the best feature set however they are not the same! For all other use cases YAGSL is the best choice because YAGSL was built with abstraction in mind to make all sensors and motor controllers functionally equivalent.&#x20;

#### TL;DR

A swerve drive is composed of

* [ ] Gyroscope
* [ ] Swerve Module
  * [ ] Angle/Azimuth Motor (+ controller)
  * [ ] Drive Motor
  * [ ] Absolute Encoder

## How does a Swerve Drive work in code?

Swerve Drives move each module into a specific angle determined by the direction you want to go and heading you want to face. For FRC we can get these value's by hand by calculating the kinematics of the robot or use [`SwerveDriveKinematics`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveDriveKinematics.html)  which uses the module locations to determine what the rotation and speed of each wheel should be given a [`ChassisSpeeds`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/ChassisSpeeds.html) object and returns an [`SwerveModuleState`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveModuleState.html) array. The [`SwerveModuleState`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveModuleState.html) can then be used to set the angle/azimuth and speed corresponding with each Swerve Module to go in the desired direction facing the desired heading.&#x20;

### `SwerveDriveKinematics`

<pre class="language-java" data-title="SwerveDrive.java" data-line-numbers data-full-width="false"><code class="lang-java">// Import relevant classes.
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;

// Example SwerveDrive class
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
<strong>        kinematics = new SwerveDriveKinematics(
</strong><strong>            new Translation2d(Units.inchesToMeters(12.5), Units.inchesToMeters(12.5)), // Front Left
</strong><strong>            new Translation2d(Units.inchesToMeters(12.5), Units.inchesToMeters(-12.5)), // Front Right
</strong><strong>            new Translation2d(Units.inchesToMeters(-12.5), Units.inchesToMeters(12.5)), // Back Left
</strong><strong>            new Translation2d(Units.inchesToMeters(-12.5), Units.inchesToMeters(-12.5))  // Back Right
</strong><strong>        );
</strong>    }
}
</code></pre>

{% hint style="info" %}
The order is defines what the output order of module angle/azimuth's and speeds will be!
{% endhint %}

### `SwerveModuleState`

[`SwerveDriveKinematics`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveDriveKinematics.html) are used to generate the [`SwerveModuleState`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveModuleState.html) of each module in the given order, the example below shows how you can do this in the `drive()` function with a given `ChassisSpeeds` object.

[`SwerveModuleState`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveModuleState.html) have 2 properties reflecting the properties of a Swerve Module, `angle` and `speedMetersPerSecond`. The goal after getting them is setting the correct swerve module (based on the order given at the construction of the [`SwerveDriveKinematics`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveDriveKinematics.html) object) to the angle and speed given in the [`SwerveModuleState`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveModuleState.html)!

<pre class="language-java" data-title="SwerveDrive.java" data-line-numbers data-full-width="false"><code class="lang-java">// Import relevant classes.
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.math.kinematics.SwerveModuleState;

// Example SwerveDrive class
public class SwerveDrive 
{

    // Attributes
    SwerveDriveKinematics kinematics;
    
    // Constructor
    public SwerveDrive() 
    {
        // Create SwerveDriveKinematics object
        // 12.5in from center of robot to center of wheel.
        // 12.5in is converted to meters to work with object.
        // Translation2d(x,y) == Translation2d(front, left)
        kinematics = new SwerveDriveKinematics(
            new Translation2d(Units.inchesToMeters(12.5), Units.inchesToMeters(12.5)), // Front Left
            new Translation2d(Units.inchesToMeters(12.5), Units.inchesToMeters(-12.5)), // Front Right
            new Translation2d(Units.inchesToMeters(-12.5), Units.inchesToMeters(12.5)), // Back Left
            new Translation2d(Units.inchesToMeters(-12.5), Units.inchesToMeters(-12.5))  // Back Right
        );
    }
    
    // Simple drive function
    public void drive()
    {
<strong>        // Create test ChassisSpeeds going X = 14in, Y=4in, and spins at 30deg per second.
</strong><strong>        ChassisSpeeds testSpeeds = new ChassisSpeeds(Units.inchesToMeters(14), Units.inchesToMeters(4), Units.degreesToRadians(30));
</strong>        
        // Get the SwerveModuleStates for each module given the desired speeds.
<strong>        SwerveModuleState[] swerveModuleStates = kinematics.toSwerveModuleStates(testSpeeds);
</strong><strong>        // Output order is Front-Left, Front-Right, Back-Left, Back-Right
</strong>    }
}
</code></pre>

{% hint style="danger" %}
Swerve Drive code does not work without [`SwerveDriveOdometry`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveDriveOdometry.html) or [`SwerveDrivePoseEstimator`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/estimator/SwerveDrivePoseEstimator.html) to keep track of module positions and angles!
{% endhint %}

### `SwerveDriveOdometry`

Unfortunately life isn't that easy. We have to keep continuous track of our current positioning of the robot, specifically the **heading**, **speed**, and **module positions** collectively known as **odometry**. This is the only way to correctly generate [`SwerveModuleState`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/kinematics/SwerveModuleState.html) which are usable.&#x20;

<pre class="language-java" data-title="SwerveDrive.java" data-line-numbers data-full-width="false"><code class="lang-java">// Import relevant classes.
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;



// Example SwerveDrive class
public class SwerveDrive extends SubsystemBase
{

    // Attributes
    SwerveDriveKinematics kinematics;
<strong>    SwerveDriveOdometry   odometry;
</strong><strong>    Gyroscope             gyro; // Psuedo-class representing a gyroscope.
</strong><strong>    SwerveModule[]        swerveModules; // Psuedo-class representing swerve modules.
</strong>    
    // Constructor
    public SwerveDrive() 
    {
    
<strong>        <a data-footnote-ref href="#user-content-fn-1">swerveModules = new SwerveModule[4];</a> // Psuedo-code; Create swerve modules here.
</strong>        
        // Create SwerveDriveKinematics object
        // 12.5in from center of robot to center of wheel.
        // 12.5in is converted to meters to work with object.
        // Translation2d(x,y) == Translation2d(front, left)
        kinematics = new SwerveDriveKinematics(
            new Translation2d(Units.inchesToMeters(12.5), Units.inchesToMeters(12.5)), // Front Left
            new Translation2d(Units.inchesToMeters(12.5), Units.inchesToMeters(-12.5)), // Front Right
            new Translation2d(Units.inchesToMeters(-12.5), Units.inchesToMeters(12.5)), // Back Left
            new Translation2d(Units.inchesToMeters(-12.5), Units.inchesToMeters(-12.5))  // Back Right
        );
        
<strong>        gyro = new Gyroscope(); // Psuedo-constructor for generating gyroscope.
</strong>
<strong>        // Create the SwerveDriveOdometry given the current angle, the robot is at x=0, r=0, and heading=0
</strong><strong>        odometry = new SwerveDriveOdometry(
</strong><strong>            kinematics,
</strong><strong>            gyro.getAngle(), // returns current gyro reading as a Rotation2d
</strong><strong>            new SwerveModulePosition[]{new SwerveModulePosition(), new SwerveModulePosition(), new SwerveModulePosition(), new SwerveModulePosition},
</strong><strong>            // Front-Left, Front-Right, Back-Left, Back-Right
</strong><strong>            new Pose2d(0,0,new Rotation2d()) // x=0, y=0, heading=0
</strong>        );
            
    }
    
    // Simple drive function
    public void drive()
    {
        // Create test ChassisSpeeds going X = 14in, Y=4in, and spins at 30deg per second.
        ChassisSpeeds testSpeeds = new ChassisSpeeds(Units.inchesToMeters(14), Units.inchesToMeters(4), Units.degreesToRadians(30));
        
        // Get the SwerveModuleStates for each module given the desired speeds.
        SwerveModuleState[] swerveModuleStates = kinematics.toSwerveModuleStates(testSpeeds);
        // Output order is Front-Left, Front-Right, Back-Left, Back-Right
    }
    
    // Fetch the current swerve module positions.
    public SwerveModulePosition[] getCurrentSwerveModulePositions()
    {
<strong>        return new SwerveModulePosition[]{
</strong><strong>            new SwerveModulePosition(swerveModules[0].getDistance(), swerveModules[0].getAngle()), // Front-Left
</strong><strong>            new SwerveModulePosition(swerveModules[1].getDistance(), swerveModules[1].getAngle()), // Front-Right
</strong><strong>            new SwerveModulePosition(swerveModules[2].getDistance(), swerveModules[2].getAngle()), // Back-Left
</strong><strong>            new SwerveModulePosition(swerveModules[3].getDistance(), swerveModules[3].getAngle())  // Back-Right
</strong><strong>        };
</strong>    }
    
    @Override
    public void periodic()
    {
<strong>        // Update the odometry every run.
</strong><strong>        <a data-footnote-ref href="#user-content-fn-2">odometry.update(gyro.getAngle(),  getCurrentSwerveModulePositions());</a>
</strong>    }
    
}
</code></pre>

{% hint style="info" %}
`SwerveDriveOdometry` can be replaced by `SwerveDrivePoseEstimator`.
{% endhint %}

Swerve Drive odometry must be updated every single run in a similar fashion to the `periodic` function from subsystems.

## Conclusion

There are many more intricacies to Swerve Drive's than covered on this page however this is sufficient for a basic understanding of how to program a Swerve Drive. I would highly encourage the reader to look at as many examples they can find to understand some of the gotcha's more or continue on to use YAGSL, Good Luck!



[^1]: This will not work as is and is representative of a \`SwerveModule\` class.

[^2]: This is not optional and necessary in order to keep track of your robot's current positioning and continue to generate correct `SwerveModuleState`'s.
