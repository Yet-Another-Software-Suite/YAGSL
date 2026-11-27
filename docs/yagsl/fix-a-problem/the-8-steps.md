# The 8 steps

## Something is wrong but it's driving-ish?

When changing inversion states doesn't work and nothing else you know of fixes the issue, debugging can be painful and arduous. Here are the eight steps that are unfortunately necessary to work through it.

Affectionately known as _"Translational Axis change based off of the robot heading"_ (the X and Y axis change in field-oriented control based on your robot turning), these steps will resolve it somewhere along the way.

{% hint style="info" %}
This is only a problem in field-relative driving. In robot-relative driving, the directions are supposed to change as the robot turns: forward is always the robot's front.
{% endhint %}

## Prepare your robot code

To run these tests, use the following (or a variation of it) as your drive code. You may need to port it to your joystick or negate axis data.

{% code title="RobotContainer.java" %}
```java
SwerveDriveSubsystem swerve;
CommandXboxController driverXbox;

// Xbox sticks read negative when pushed forward/left, while robot controls are
// front-left positive, so every axis is negated.
// Left stick controls translation, right stick points the desired
// heading directly (NOT angular velocity).
SwerveInputStream driveDirectAngle =
    swerve.getAngularVelocityStream(() -> -driverXbox.getLeftY(),
                                    () -> -driverXbox.getLeftX(),
                                    () -> -driverXbox.getRightX())
          .withControllerHeadingAxis(() -> -driverXbox.getRightX(),
                                     () -> -driverXbox.getRightY())
          .withHeadingControl(() -> true)
          .withDeadband(0.05)
          .withAllianceRelativeControl();

swerve.setDefaultCommand(swerve.driveFieldRelative(driveDirectAngle));
```
{% endcode %}

`getAngularVelocityStream(x, y, rot)` and `driveFieldRelative(stream)` are the two small helpers from the YAGSL `commands2` example's `SwerveDriveSubsystem`: the first returns `SwerveInputStream.of(drive, x, y, rot)` and the second runs `drive.setFieldRelativeChassisSpeeds(stream.get())` every loop. On Commands v3, use the `commands3` example's `HeadingTeleop` opmode, which builds the same stream and drives it from a `while (true)` coroutine.

`withControllerHeadingAxis(headingX, headingY)` plus `withHeadingControl(() -> true)` makes the robot turn to face wherever the right stick points instead of spinning at an angular velocity, the same "point the stick where you want the robot to face" behavior the old `SwerveController.getTargetSpeeds()` heading-hold path provided. Heading control needs a rotation controller on the drive (`SwerveDriveConfig.withRotationController(...)`). If you leave `withHeadingControl` off and drive with the angular-velocity stream instead, the steps below are identical; only the exact symptom description changes slightly.

{% hint style="info" %}
A stick heading is field relative, not alliance relative. If you're testing from the red alliance wall, flip both heading axes (multiply them by `-1`), as the `commands2` example's `RobotContainer` does.
{% endhint %}

## The eight steps

{% hint style="danger" %}
**The steps are dangerous.**

Six of them will cause your robot to spin out of control.

One of them will be correct.

One of them will appear to be correct but have the translational axis change based on the robot's heading.
{% endhint %}

{% hint style="warning" %}
You are not expected to complete all of these steps to get a functioning swerve drive. Somewhere along the way your issue should disappear.
{% endhint %}

1. Start by setting `gyroInvert` in `swervedrive.json` to `false` **AND** `inverted.drive` to `false` in every module JSON.
2. THEN set `gyroInvert` to `true`.
3. THEN invert all of the drive motors in the module JSONs by setting `inverted.drive` to `true`.
4. THEN set `gyroInvert` back to `false`.
5.  THEN [flip the modules](#user-content-fn-1)[^1].\\

    <figure><img src="../.gitbook/assets/image-48.png" alt=""><figcaption><p>Depiction of flipping the modules correctly.</p></figcaption></figure>
6. THEN un-invert all of the drive motors (`inverted.drive` back to `false`).
7. THEN set `gyroInvert` to `true`.
8. THEN set your drive motors' `inverted.drive` back to `true`.

{% hint style="danger" %}
IF none of these work, you most likely have an incorrect hardware configuration, something is not working as expected, or something is wired incorrectly.
{% endhint %}

## Verifying behavior

Sometimes the eight steps don't look done when they really are. Here's an example where the robot is actually tuned correctly, but the desired front of the robot is really the back of the robot.

<details>

<summary>How to swap module configurations</summary>

For the examples, we label modules with numbers to reduce confusion; when changing module files around, we assign the numbers to the respective initial module configuration names. For the example above:

1. `frontleft.json`
2. `frontright.json`
3. `backleft.json`
4. `backright.json`

**Swapping `frontleft.json` with `backright.json`**

```json
{
  "drive": { "type": "sparkmax_neo", "id": 4, "canbus": "" },
  "angle": { "type": "sparkmax_neo", "id": 3, "canbus": "" },
  "absoluteEncoder": { "type": "cancoder_can", "id": 9, "channel": 0, "canbus": "" },
  "inverted": { "drive": false, "angle": false },
  "absoluteEncoderOffset": -114.609,
  "location": { "front": 12, "left": 12 }
}
```

```json
{
  "drive": { "type": "sparkmax_neo", "id": 5, "canbus": "" },
  "angle": { "type": "sparkmax_neo", "id": 6, "canbus": "" },
  "absoluteEncoder": { "type": "cancoder_can", "id": 11, "channel": 0, "canbus": "" },
  "inverted": { "drive": false, "angle": false },
  "absoluteEncoderOffset": -18.281,
  "location": { "front": -12, "left": -12 }
}
```

Swap the device IDs and offsets between the two files (keep each file's own `location`) and you've swapped the module configurations correctly.

#### The easy way

1. Change the `location` negations to the desired module side.
2. Rename the files without overwriting each other.

</details>

[^1]: Change the device configuration for the drive AND angle motor controllers AND the absolute encoders in the following way:

    (Front Left -> Back Right) (Front Right -> Back Left) (Back Left -> Front Right) (Back Right -> Front Left)
