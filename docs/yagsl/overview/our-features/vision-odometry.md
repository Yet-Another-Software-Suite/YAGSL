---
description: >-
  YAGSL handles odometry for you and extends it so you can add whatever data you
  want!
---

# Vision Odometry

## What is Vision Odometry?

Vision odometry data is normally added by using [`SwerveDrivePoseEstimator.addVisionMeasurement`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/estimator/PoseEstimator.html#addVisionMeasurement\(edu.wpi.first.math.geometry.Pose2d,double,edu.wpi.first.math.Matrix\)) and since YAGSL handles [`SwerveDrivePoseEstimator`](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/math/estimator/SwerveDrivePoseEstimator.html) for you we extend the functions inside of [`SwerveDrive`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html) to handle vision measurements with [`SwerveDrive.addVisionMeasurement`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html#addVisionMeasurement(edu.wpi.first.math.geometry.Pose2d,double)) instead of creating your own estimator.

## How do I use Vision Odometry?

There is an example usage in YAGSL-Example where we show a dummy usecase while using fake vision measurements. Effectively you would use the [`SwerveDrive`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/SwerveDrive.html) class as the pose estimator.

<pre class="language-java"><code class="lang-java">/**
 * Add a fake vision reading for testing purposes.
 */
public void addFakeVisionReading()
{
<strong>  swerveDrive.addVisionMeasurement(new Pose2d(3, 3, Rotation2d.fromDegrees(65)), Timer.getFPGATimestamp());
</strong>}
</code></pre>
