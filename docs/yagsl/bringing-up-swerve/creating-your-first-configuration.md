# Creating your first configuration

## DevilBotz 2876 Swerve Bring-Up Checklist

Compliments of the [DevilBotz 2876](https://www.thebluealliance.com/team/2876/2024)!

Updated: 2024-02-03

## Printable version

{% file src="../.gitbook/assets/2024-02-03 DevilBotz 2876 Swerve Bring-Up Checklist.pdf" %}

## Resources

* [YAGSL Wiki](https://docs.yagsl.com/) - [https://yagsl.gitbook.io/yagsl/](https://docs.yagsl.com/)
* [REV Robotics Hardware Client 2](https://docs.revrobotics.com/rev-hardware-client-2) - [https://docs.revrobotics.com/rev-hardware-client-2](https://docs.revrobotics.com/rev-hardware-client-2)
  * _for configuring Spark Max Motor Controllers and other Rev devices_
* [Phoenix Tuner X](https://v6.docs.ctr-electronics.com/en/stable/docs/tuner/index.html) - [https://v6.docs.ctr-electronics.com/en/stable/docs/tuner/index.html](https://v6.docs.ctr-electronics.com/en/stable/docs/tuner/index.html)
  * _for configuring CanCoders and other CTR devices_

## Swerve Orientation Diagram

{% hint style="warning" %}
_Note: When viewed from the top, make sure the sides of the wheel with the bevel gear are pointing to the **left**_
{% endhint %}

<figure><img src="../.gitbook/assets/devilbots_cropped_swerve_orientation.png" alt=""><figcaption></figcaption></figure>

### Step 1: Module Types

<table data-header-hidden data-full-width="true"><thead><tr><th></th><th></th></tr></thead><tbody><tr><td></td><td><strong>Model, Version, Etc</strong></td></tr><tr><td><em>Motor</em></td><td></td></tr><tr><td><em>Controller</em></td><td></td></tr><tr><td><em>Absolute Encoder</em></td><td></td></tr><tr><td><em>IMU</em></td><td></td></tr></tbody></table>

### Step 2: Build Specific Details

1. Measure the module center relative to the **robot center**

<table data-full-width="true"><thead><tr><th>Module</th><th>X "Front" (Inches)</th><th>Y "Left" (Inches)</th></tr></thead><tbody><tr><td><em>Front Left (FL)</em></td><td>+</td><td>+</td></tr><tr><td><em>Front Right (FR)</em></td><td>+</td><td>-</td></tr><tr><td><em>Back Left (BL)</em></td><td>-</td><td>+</td></tr><tr><td><em>Back Right (BR)</em></td><td>-</td><td>-</td></tr></tbody></table>

2. Measure the wheel diameter _in inches_
3. [Determine the _reported_ internal encoder resolution](#user-content-fn-1)[^1]
   * _Note: Most encoders now normalize the reported values to `-1` to `1`, so the Encoder Resolution when computing the conversion factors should generally be “`1`”. Only known exception is the TalonSRX._
4. Find the drive/angle gear ratio from the swerve module manufacturer specs
5. (Optional) Calculate the [drive/angle conversion factors](#user-content-fn-2)[^2]
   * Drive Motor Conversion Factor (meters/rotation) = (PI \* WHEEL DIAMETER IN METERS) / (GEAR RATIO \* ENCODER RESOLUTION)
   * Angle Motor Conversion Factor (degrees/rotation) = 360 / (GEAR RATIO \* ENCODER RESOLUTION)

<table data-full-width="true"><thead><tr><th>Motor</th><th align="center">Wheel Diameter (meters)</th><th>Gear Ratio</th><th align="center">Encoder Resolution (CPR)</th><th>Conversion Factor</th></tr></thead><tbody><tr><td><em>Drive</em></td><td align="center"></td><td></td><td align="center">1</td><td></td></tr><tr><td><em>Angle</em></td><td align="center"><strong>N/A</strong></td><td></td><td align="center">1</td><td></td></tr></tbody></table>

### Step 3: Electrical Characteristics

6. Set/Verify the CAN IDs for each module

{% hint style="warning" %}
_Note: Update the FW for each module and reset any stored settings to factory defaults_
{% endhint %}

<table data-full-width="true"><thead><tr><th>Module</th><th>Motor CAN IDs</th><th>Motor CAN IDs</th><th>Encoder CAN/Channel ID</th></tr></thead><tbody><tr><td></td><td><strong>Drive</strong></td><td><strong>Angle</strong></td><td><strong>Absolute Encoder</strong></td></tr><tr><td><em>Front Left (FL)</em></td><td></td><td></td><td></td></tr><tr><td><em>Front Right (FR)</em></td><td></td><td></td><td></td></tr><tr><td><em>Back Left (BL)</em></td><td></td><td></td><td></td></tr><tr><td><em>Back Right (BR)</em></td><td></td><td></td><td></td></tr></tbody></table>

7. Check Inversion
   1. Rotate the _drive_ wheel **CCW** (moving “forward”)
      * The built-in encoder value should **increase**. If not, _invert_ the drive motor.
   2. Rotate the _angle_ wheel **CCW** (when viewed from the top)
      * The built-in encoder value should **increase**. If not, _invert_ the angle motor.
      * The absolute encoder value should **increase**. If not, _invert_ the absolute encoder.
   3. Rotate the entire _robot_ **CCW**. The gyro angle (yaw) should **increase**. If not, _invert_ the IMU

{% hint style="warning" %}
_Note: If you are using the hardware utilities for accessing the motors controllers and/or absolute encoders, the RoboRio must **not** be active on the CAN bus. The most reliable way to disable the RoboRio, **without affecting the CAN BUS termination**, is to temporarily disconnect it from power by pulling the 10A fuse on the Power Distribution Panel (PDP) feeding the RoboRio and **then** power cycle the robot._
{% endhint %}

<table data-full-width="true"><thead><tr><th>Module</th><th>Inverted?</th><th></th><th></th><th></th></tr></thead><tbody><tr><td></td><td><strong>Drive</strong></td><td><strong>Angle</strong></td><td><strong>Absolute Encoder</strong></td><td><strong>IMU</strong></td></tr><tr><td><em>Front Left (FL)</em></td><td></td><td></td><td></td><td></td></tr><tr><td><em>Front Right (FR)</em></td><td></td><td></td><td></td><td></td></tr><tr><td><em>Back Left (BL)</em></td><td></td><td></td><td></td><td></td></tr><tr><td><em>Back Right (BR)</em></td><td></td><td></td><td></td><td></td></tr></tbody></table>

### Step 4: Absolute Encoder Offsets

8. Turn Robot On (Disabled so the wheels can be turned manually)
9. Manually Turn All 4 wheels so that they are all pointing forward and forward rotation results in increasing drive encoder values (see the black arrows in [Orientation Diagram](creating-your-first-configuration.md#swerve-orientation-diagram-1)).
10. Measure the absolute encoder value for each module

<table data-full-width="true"><thead><tr><th>Module</th><th>Angle Absolute Offset (degrees)</th><th></th></tr></thead><tbody><tr><td><em>Front Left (FL)</em></td><td></td><td></td></tr><tr><td><em>Front Right (FR)</em></td><td></td><td></td></tr><tr><td><em>Back Left (BL)</em></td><td></td><td></td></tr><tr><td><em>Back Right (BR)</em></td><td></td><td></td></tr></tbody></table>

### Step 5: Input data into the configuration webpage

Open the webpage and import your data into the config files.\
[https://yet-another-software-suite.github.io/YAGSL/config_generator/](https://yet-another-software-suite.github.io/YAGSL/config_generator/)

[^1]: IF you are using a SparkMAX or TalonFX this is going to be `1`!

[^2]: These convert rotations to meters/degrees for the motor controller.
