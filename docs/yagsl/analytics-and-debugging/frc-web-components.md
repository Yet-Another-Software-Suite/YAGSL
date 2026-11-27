# FRC Web Components

FRC Web Components is an easy way to visualize the Swerve Drive and give helpful feedback to YAGSL developers to aid you.

## Download

To download and install this please choose the correct file and run it from this page.<br>

{% embed url="https://github.com/frc-web-components/app/releases/latest" %}

## Configuring FRC Web Components

<figure><img src="../.gitbook/assets/yagsl_fwc.gif" alt=""><figcaption></figcaption></figure>

1. Connect the laptop to the robot
2. Open "FRC Web Components"
3. Click "Settings"

<figure><img src="../.gitbook/assets/fwc_config6.png" alt=""><figcaption><p>FRC Web Components with Settings highlighted</p></figcaption></figure>

4. Input the roboRIO IP address based off of your team number. `10.TE.AM.2`

{% embed url="https://docs.wpilib.org/en/stable/docs/networking/networking-introduction/ip-configurations.html#te-am-ip-notation" %}

<figure><img src="../.gitbook/assets/fwc_conrfig5.png" alt=""><figcaption><p>Dashboard settings for FRC Web Components.</p></figcaption></figure>

5. Open the widget menu.

<figure><img src="../.gitbook/assets/fwc_config4.png" alt=""><figcaption><p>Press here.</p></figcaption></figure>

6. Select the `Swerve Drivebase` and click `Append`

<figure><img src="../.gitbook/assets/fwc_config3.png" alt=""><figcaption></figcaption></figure>

7. Click on the widget.
8. Click "Connect to data source..."

<figure><img src="../.gitbook/assets/fwc_config2.png" alt=""><figcaption></figcaption></figure>

9. Connect to `SmartDashboard/swerve` by pressing select here.

<figure><img src="../.gitbook/assets/FWC_config1.png" alt=""><figcaption></figcaption></figure>

10. Press "Close"

## Overview

<figure><img src="../.gitbook/assets/FRC_web_component_snapshot.png" alt=""><figcaption><p>Swerve Drivebase while in motion with an incorrect configuration.</p></figcaption></figure>

{% hint style="warning" %}
The **BLUE** lines are the measured velocity and position of the swerve module.

The **RED** lines is the velocity and position of the module sent!
{% endhint %}
