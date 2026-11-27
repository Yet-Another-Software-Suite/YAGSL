# Angular Velocity Compensation

YAGSL utilizes a method pioneered by Jack-in-the-bot for angular velocity compensation. To enable this you would use `SwerveDrive.setAngularVelocityCompensation(true, true, 0.1);` where the first parameter is the enabled state in teleop, second is enabled in autonomous, and third is the coefficient to use.

When enabling for the first time if the skew is significantly worse try inverting the value.

1. Tune by moving in a straight line while rotating.&#x20;
   * Testing is best done with angular velocity controls on the right stick.
2. Change the value until you are visually happy with the skew.
3. Ensure your tune works with different translational and rotational magnitudes.
4. If this reduces skew in teleop, it may improve auto.      &#x20;

Created by [**yapplejack**](https://github.com/yapplejack) **in PR**[ **#231**](https://github.com/BroncBotz3481/YAGSL-Example/pull/231)
