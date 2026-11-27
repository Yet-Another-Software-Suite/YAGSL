---
description: >-
  AdvantageScope is a data visualization tool, courtesy of team 6328 Mechanical
  Advantage, which can visualize the swerve drive to give you feedback for
  debugging.
---

# Set up AdvantageScope

[AdvantageScope](https://github.com/Mechanical-Advantage/AdvantageScope) is included with WPILib, so there is no separate download. With every WPILib update, re-run the [WPILib installer](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html) to get the latest tools.

{% stepper %}
{% step %}
### Open AdvantageScope

Open `AdvantageScope (WPILib)`, or in VS Code open the command palette, run **WPILib: Start Tool**, and click **AdvantageScope**.
{% endstep %}

{% step %}
### Set the robot address

Click **App → Show Preferences**. In **Robot Address**, enter the SystemCore's address for your team number, `10.TE.AM.2` (team 1234 is `10.12.34.2`), and click the check mark.

<figure><img src="../.gitbook/assets/advantagescope-robot-address.gif" alt="In AdvantageScope, the user clicks App, then Show Preferences. In the preferences, they replace the Robot Address 127.0.0.1 with 10.12.34.2 and click the check mark."><figcaption><p>Setting the robot address in App → Show Preferences.</p></figcaption></figure>

Connected to the SystemCore over USB, or to its built-in Wi-Fi access point? Click **File → Use Systemcore USB Address** or **File → Use Systemcore Wi-Fi Address** instead.
{% endstep %}

{% step %}
### Connect to the robot or the simulator

Click **File → Connect to Robot → Default: NetworkTables**. To watch the simulator instead, click **File → Connect to Simulator → Default: NetworkTables**. The robot's fields appear in the sidebar.

<figure><img src="../.gitbook/assets/advantagescope-connect.gif" alt="The user clicks File, then Connect to Simulator, then Default: NetworkTables. The sidebar fills with Mechanisms, PathPlanner, Robot, Tunables, DriverStation and Tuning."><figcaption><p>Connecting to the simulator.</p></figcaption></figure>
{% endstep %}

{% step %}
### Add a Swerve tab

Click **+** at the top right, then **Swerve**. In the sidebar, open **Mechanisms → swerve**, then drag each of these fields into **Sources**:

* `Mechanisms/swerve/states/current`
* `Mechanisms/swerve/states/desired`
* `Mechanisms/swerve/chassis/current`
* `Mechanisms/swerve/chassis/desired`

<figure><img src="../.gitbook/assets/advantagescope-swerve-tab.gif" alt="The user clicks the plus button and picks Swerve. They open Mechanisms, swerve, states and chassis in the sidebar, and drag states current, states desired, chassis current and chassis desired into Sources. The swerve diagram shows each module&#x27;s measured velocity in blue and desired velocity in red, and the robot&#x27;s measured and desired velocity in green and orange."><figcaption><p>Adding the swerve fields to a Swerve tab.</p></figcaption></figure>

{% hint style="info" %}
You may need to enable your robot before AdvantageScope shows values for these fields.
{% endhint %}

{% hint style="info" %}
`swerve` is the telemetry name you gave the drive with `SwerveDriveConfig.withTelemetry("swerve", ...)`; if you named it something else, look under `Mechanisms/<that name>`. All four fields are published at every `TelemetryVerbosity`, `LOW` included.
{% endhint %}

To see the robot's pose on a 2D/3D field, add a **2D Field** or **3D Field** tab instead and drag in `Mechanisms/swerve/pose`.
{% endstep %}
{% endstepper %}

## Reading the display

Each source is drawn in the color shown beside it in **Sources**. Added in the order above, the **blue** arrows are each module's measured velocity and the **red** arrows are the velocity it was commanded to. The **green** and **orange** arrows are the robot's measured and commanded velocity.

Large, persistent gaps between the measured and commanded arrows usually point to a tuning issue. See [how to tune PIDF gains](tune-pidf-gains.md) and [swerve drive drift causes](../fix-a-problem/swerve-drift-causes.md).
