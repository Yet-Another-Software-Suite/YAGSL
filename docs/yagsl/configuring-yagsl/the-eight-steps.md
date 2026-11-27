# The eight steps

## Something is wrong but it's driving-ish?

When the inversion state changes do not work and nothing else you know of fixes your issue's the debugging of such setups can be painful and arduous. Here are the eight steps that are unfortunately necessary to accomplish debugging this.

Affectionally known as _"Translational Axis change based off of the robot heading"_ (the X and Y axis change in field oriented based off your robot turning.) these steps will resolve it somewhere along the way.

## Prepare your robot code.

In order for these tests to be conducted quickly and effectively please use the following or a variation of the following as your drive code. You may need to port it to a joystick or negate the axis' data retrieved but this will help you debug your issues faster.

{% code title="RobotContainer.java" %}
```java
...
    SwerveSubsystem drivebase;
    CommandXboxController driverXbox;
...
    // Applies deadbands and inverts controls because joysticks
    // are back-right positive while robot
    // controls are front-left positive
    // left stick controls translation
    // right stick controls the desired angle NOT angular rotation
    Command driveFieldOrientedDirectAngle = drivebase.driveCommand(
        () -> MathUtil.applyDeadband(driverXbox.getLeftY(), OperatorConstants.LEFT_Y_DEADBAND),
        () -> MathUtil.applyDeadband(driverXbox.getLeftX(), OperatorConstants.LEFT_X_DEADBAND),
        () -> driverXbox.getRightX(),
        () -> driverXbox.getRightY());
        
    drivebase.setDefaultCommand(driveFieldOrientedDirectAngle);
....
```
{% endcode %}

<pre class="language-java" data-title="SwerveSubsystem.java"><code class="lang-java">...
public class SwerveSubsytem extends SubsystemBase
{
...
  SwerveDrive swerveDrive;
  /**
   * Command to drive the robot using translative values and heading as a setpoint.
   *
   * @param translationX Translation in the X direction. Cubed for smoother controls.
   * @param translationY Translation in the Y direction. Cubed for smoother controls.
   * @param headingX     Heading X to calculate angle of the joystick.
   * @param headingY     Heading Y to calculate angle of the joystick.
   * @return Drive command.
   */
  public Command driveCommand(DoubleSupplier translationX, DoubleSupplier translationY, DoubleSupplier headingX,
                              DoubleSupplier headingY)
  {
    // swerveDrive.setHeadingCorrection(true); // Normally you would want heading correction for this kind of control.
    return run(() -> {

      Translation2d scaledInputs = SwerveMath.scaleTranslation(new Translation2d(translationX.getAsDouble(),
                                                                                 translationY.getAsDouble()), 0.8);

      // Make the robot move
<strong>      driveFieldOriented(swerveDrive.swerveController.getTargetSpeeds(scaledInputs.getX(), scaledInputs.getY(),
</strong><strong>                                                                      headingX.getAsDouble(),
</strong><strong>                                                                      headingY.getAsDouble(),
</strong><strong>                                                                      swerveDrive.getOdometryHeading().getRadians(),
</strong><strong>                                                                      swerveDrive.getMaximumVelocity()));
</strong>    });
  }
  
  /**
   * Drive the robot given a chassis field oriented velocity.
   *
   * @param velocity Velocity according to the field.
   */
  public void driveFieldOriented(ChassisSpeeds velocity)
  {
    swerveDrive.driveFieldOriented(velocity);
  }
...
</code></pre>

This snippet is taken directly from the YAGSL-Example project, if you are using that project you just need to be sure you use `driveFieldOrientedDirectAngle` as the default command for the `SwerveSubsystem` and correct any joystick inversions necessary.

## The eight steps.

{% hint style="danger" %}
**The steps are dangerous!**

6 of them will cause your robot to spin out of control.

1 of them will be correct.

1 of them will appear to be correct but have the translational axis change based off of the robot heading.
{% endhint %}

{% hint style="warning" %}
You are not expected to complete all of these steps to achieve a functioning swerve drive, somewhere along the way your issue should disappear.
{% endhint %}

1. Start by setting `invertIMU` in `swervedrive.json` to `false` **AND** the `drive` `invert` to `false` in module JSONs.
2. THEN set `invertIMU` to `true`.
3. THEN invert all of the drive motors in the module JSONs by setting them to `true`.
4. THEN set `invertIMU` to `false`.
5.  THEN [flip the modules](#user-content-fn-1)[^1].\\

    <figure><img src="../.gitbook/assets/image-48.png" alt=""><figcaption><p>Depiction of flipping the modules correctly.</p></figcaption></figure>
6. THEN uninvert all of the drive motors by setting them to `false`
7. THEN set `invertIMU` to `true`.
8. THEN set your drive motors inversion states to `true`.

{% hint style="danger" %}
IF none of these work you most likely have a incorrect hardware configuration, something is not working as expected, or something is wired incorrectly.
{% endhint %}

## Verifying behaviour

Sometimes the 8 steps doesnt look like its done when it really is. Here is an example on which the bot is actually tuned correctly but the desired front of the robot is really the back of the robot.



<details>

<summary>How to swap module configurations</summary>

For the examples we label with numbers as to be less confused, however when changing module files around we assign the numbers to the respective initial module configuration names. For the example above we have as follows

1. `frontleft.json`
2. `frontright.json`
3. `backleft.json`
4. `backright.json`

**Swapping `frontleft.json` with `backright.json`**

<pre class="language-json" data-title="frontleft.json"><code class="lang-json">{
<strong>  "drive": {
</strong><strong>    "type": "sparkmax",
</strong><strong>    "id": 4,
</strong><strong>    "canbus": null
</strong><strong>  },
</strong><strong>  "angle": {
</strong><strong>    "type": "sparkmax",
</strong><strong>    "id": 3,
</strong><strong>    "canbus": null
</strong><strong>  },
</strong><strong>  "encoder": {
</strong><strong>    "type": "cancoder",
</strong><strong>    "id": 9,
</strong><strong>    "canbus": null
</strong><strong>  },
</strong><strong>  "inverted": {
</strong><strong>    "drive": false,
</strong><strong>    "angle": false
</strong><strong>  },
</strong><strong>  "absoluteEncoderOffset": -114.609,
</strong>  "location": {
    "front": 12,
    "left": 12
  }
}
</code></pre>

<pre class="language-json" data-title="backright.json"><code class="lang-json"><strong>{
</strong><strong>  "drive": {
</strong><strong>    "type": "sparkmax",
</strong><strong>    "id": 5,
</strong><strong>    "canbus": null
</strong><strong>  },
</strong><strong>  "angle": {
</strong><strong>    "type": "sparkmax",
</strong><strong>    "id": 6,
</strong><strong>    "canbus": null
</strong><strong>  },
</strong><strong>  "encoder": {
</strong><strong>    "type": "cancoder",
</strong><strong>    "id": 11,
</strong><strong>    "canbus": null
</strong><strong>  },
</strong><strong>  "inverted": {
</strong><strong>    "drive": false,
</strong><strong>    "angle": false
</strong><strong>  },
</strong><strong>  "absoluteEncoderOffset": -18.281,
</strong>  "location": {
    "front": -12,
    "left": -12
  }
}
</code></pre>

Swap the highlighted lines and you have swapped the module configurations correctly.

#### The easy way

1. Change the location negations to the desired module side.
2. Rename the files without overwriting eachother.

</details>

[^1]: Change the device configuration for the drive AND angle motor controllers AND the absolute encoders in the following way.\
    \
    (Front Left -> Back Right)  \
    (Front Right -> Back Left)  \
    (Back Left -> Front Right)  \
    (Back Right -> Front Left) &#x20;
