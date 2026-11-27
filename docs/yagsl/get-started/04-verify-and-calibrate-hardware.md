# 6. Check and calibrate your hardware

Before your first drive, confirm every sensor and motor reports direction consistently and capture each module's absolute encoder offset. Skipping this step is the single most common cause of a swerve drive that "spins out" or drives in the wrong direction.

Work through the steps in order, with the robot on blocks.

{% hint style="info" %}
The [config generator's Quick Start Guide](https://config.yagsl.com/guide) walks through this same process as interactive, printable checklists.
{% endhint %}

{% stepper %}
{% step %}
### Label every device and check the CAN IDs

Label each motor controller, encoder and the gyroscope with its type, CAN ID (or channel) and bus before you start. This is the easiest step to get wrong and the hardest mistake to spot afterward.

<figure><img src="../.gitbook/assets/yagsl-label-devices.png" alt="A robot from above with four modules, each tagged with its drive, angle and encoder IDs, and the gyro in the middle. Beside it, the front left module&#x27;s three labels: FL drive, sparkflex_vortex, ID 2, canbus 1; FL angle, sparkmax_neo550, ID 1, canbus 1; FL absolute encoder, cancoder_can, ID 10, empty canbus. Every CAN ID must be unique on its bus and not shared with other devices such as the power hub."><figcaption><p>Label every device with its type, ID and bus.</p></figcaption></figure>

{% hint style="danger" %}
Make sure every CAN ID is unique across your entire robot, and that each device's CAN bus in your config matches the SystemCore bus it is actually wired to (an empty `canbus` means `can_s0`). A known failure mode: if a SparkMAX shares a CAN ID with your REV Power Distribution Hub, the SparkMAX simply won't move, with no obvious error.
{% endhint %}
{% endstep %}

{% step %}
### Deploy, stay disabled, and open AdvantageScope

Deploy your code from [step 4](write-the-swerve-code.md) with the config from step 2 and **keep the robot disabled**. Open AdvantageScope's `Mechanisms/swerve` NetworkTables tree, where the swerve code publishes these values. The `swerve` in the path is the name that code passes to `SwerveDriveConfig.withTelemetry("swerve", ...)`.

<figure><img src="../.gitbook/assets/yagsl-telemetry.png" alt="The Mechanisms/swerve tree in AdvantageScope, with each module&#x27;s drive and azimuth telemetry and its raw absolute encoder."><figcaption><p>The Mechanisms/swerve tree in AdvantageScope. Each module's drive/azimuth telemetry and raw absolute encoder are visible per-module.</p></figcaption></figure>

{% hint style="warning" %}
If you're using a hardware vendor client to poke at motors/encoders directly, your robot program must not be commanding those devices at the same time. Keep the robot disabled while you use the client, and power-cycle the robot when you're done so the SystemCore and your robot program start from a clean state.
{% endhint %}
{% endstep %}

{% step %}
### Pick a front and check the gyroscope

**Front is whatever the gyroscope reports as `0`.** Pick a front, and expect that you may need to change it later if it turns out to be inconvenient. Then lift the robot and turn it counterclockwise (seen from above): `Mechanisms/swerve/gyro` (the gyro's `Rotation2d`) must go **up**. If it goes down, set `gyroInvert` to `true` in `swervedrive.json`.

<figure><img src="../.gitbook/assets/yagsl-gyro-ccw.gif" alt="An animation of a robot seen from above turning 90 degrees counterclockwise, twice. On the left the gyro reading goes up to 90 degrees, which is correct, so gyroInvert stays false. On the right it goes down to minus 90 degrees, so gyroInvert must be set to true."><figcaption><p>Turn the robot counterclockwise: the gyro should go up.</p></figcaption></figure>

If you're using a NavX, recalibrate it once it arrives at your shop because NavX units ship calibrated to the humidity of their factory, not your build space. Recalibrate again periodically, especially at competition.
{% endstep %}

{% step %}
### Check each drive motor

Point the module forward, then roll its wheel forward by hand. That module's `modules/<name>/drive/measurement/position` (the distance the wheel has rolled, in meters) must go **up**. If it goes down, set `"inverted": { "drive": true }` in that module's JSON.

<figure><img src="../.gitbook/assets/yagsl-check-drive.gif" alt="An animation of a wheel seen from the side being rolled forward by hand. On the left drive/measurement/position goes up to 0.244 meters, which is correct. On the right it goes down to minus 0.244 meters, so inverted.drive must be set to true."><figcaption><p>Roll the wheel forward: the drive position should go up.</p></figcaption></figure>
{% endstep %}

{% step %}
### Check each angle motor and absolute encoder

Turn the module counterclockwise (seen from above) by hand. Both `modules/<name>/azimuth/mechanism/position` (relative, in rotations) and `modules/<name>/encoder` (absolute, in degrees) must go **up**.

* If the **encoder** goes down, set `"absoluteEncoderInverted": true`.
* If the **azimuth** position goes down, set `"inverted": { "angle": true }`.

<figure><img src="../.gitbook/assets/yagsl-check-angle.gif" alt="An animation of three modules seen from above, each turned 90 degrees counterclockwise by hand. In the first both readings go up: azimuth position to 0.25 rotations and encoder to 90 degrees, which is correct. In the second the encoder goes down to minus 90 degrees, so absoluteEncoderInverted must be set to true. In the third the azimuth position goes down to minus 0.25 rotations, so inverted.angle must be set to true."><figcaption><p>Turn the module counterclockwise: both readings should go up.</p></figcaption></figure>

See [Determine Inversion](../fix-a-problem/determine-inversion.md) for the full decision procedure if any of these don't behave as expected.
{% endstep %}

{% step %}
### Align every wheel

Turn all four wheels by hand so they point forward, with their bevel gears facing the robot's left, and so rolling them forward makes the drive position go up.

<figure><img src="../.gitbook/assets/yagsl-align-wheels.gif" alt="An animation of a robot seen from above. The four modules start at different angles and turn until every wheel points forward with its bevel gear on the robot&#x27;s left side."><figcaption><p>Every wheel forward, bevel gears on the left.</p></figcaption></figure>

<figure><img src="../.gitbook/assets/devilbots_cropped_swerve_orientation.png" alt="Swerve modules with their bevel gears facing left."><figcaption><p>The left and right are physical left and right.</p></figcaption></figure>
{% endstep %}

{% step %}
### Record each module's offset

With the wheels still aligned and the robot still disabled, read each module's `Mechanisms/swerve/modules/<name>/encoder` and enter it as that module's absolute encoder offset.

In AdvantageScope, connected to the robot as in [Set up AdvantageScope](../tune-and-extend/set-up-advantagescope.md), open **Mechanisms → swerve → modules** in the sidebar. Open each module in turn and read the number beside **encoder**. It's in degrees; hover over it to check.

<figure><img src="../.gitbook/assets/advantagescope-read-offsets.gif" alt="In AdvantageScope&#x27;s sidebar, the user opens Mechanisms, swerve and modules, then opens each module in turn and points at its encoder reading: frontleft 5.713, frontright 13.447, backleft 25.752 and backright 39.023. Hovering shows units: degrees."><figcaption><p>Reading each module's encoder in AdvantageScope.</p></figcaption></figure>

<figure><img src="../.gitbook/assets/yagsl-record-offsets.gif" alt="An animation of each module&#x27;s encoder reading in AdvantageScope being copied into its JSON, one module at a time: frontleft 132.363 degrees, frontright minus 50.977, backleft 271.143 and backright 12.656, each into that module&#x27;s absoluteEncoderOffset."><figcaption><p>Each encoder reading becomes that module's offset.</p></figcaption></figure>

The config generator walks you through it. Its Quick Start Guide's [Align Modules step](https://config.yagsl.com/guide#align) shows how to align the wheels and which reading to copy for each module.

<figure><img src="../.gitbook/assets/yagsl-generator-guide-align.png" alt="The config generator&#x27;s Quick Start Guide on step 6, Align Modules. A checklist item says to rotate every wheel forward with the bevel gear left, over an animation of four modules turning to face forward. The next says to record each module&#x27;s absolute encoder offset, reading NT:/Mechanisms/swerve/modules/[module_name]/encoder in AdvantageScope and copying it into that module&#x27;s absoluteEncoderOffset."><figcaption><p>The Align Modules step of the config generator's Quick Start Guide.</p></figcaption></figure>

Then, in the [config generator](https://config.yagsl.com), click **Upload Config** to load your configuration, enter each reading in that module's tab under **Offset (degrees)**, and click **Download Config**. You can also edit `absoluteEncoderOffset` in each module's JSON file directly.

<figure><img src="../.gitbook/assets/yagsl-generator-offsets.gif" alt="In the config generator, the user opens the Front Left, Front Right, Back Left and Back Right tabs in turn, and types each module&#x27;s offset under Offset (degrees): 132.363, minus 50.977, 271.143 and 12.656."><figcaption><p>Entering each module's offset in the config generator.</p></figcaption></figure>

The absolute encoder offset is what lets your swerve module remember wheel orientation across power cycles. Redeploy once all four are in.
{% endstep %}

{% step %}
### Run the spin test

With the robot lifted, command pure counterclockwise rotation and watch the modules in AdvantageScope's Swerve view. It should spin cleanly counterclockwise.

<figure><img src="../.gitbook/assets/yagsl-spin-test.gif" alt="An animation of three robots seen from above, commanded to spin counterclockwise. The first spins cleanly counterclockwise: correct. The second has one wheel pushing against the others: invert that module&#x27;s drive motor. The third spins clockwise: check for a diagonal module swap first."><figcaption><p>What the spin test can look like.</p></figcaption></figure>

You can also run it from the dashboard: with drive tuning on, set `Tuning/swerve/modules/drive/inplace` to `true` and give `modules/drive/velocity` a small positive speed. Every module turns tangent to the robot and it should spin counterclockwise.

<figure><img src="../.gitbook/assets/yagsl-ascope-spin-swerve.png" alt="The AdvantageScope Swerve tab during drive tuning with modules/drive/inplace on: every module points tangent to the robot at plus or minus 45 degrees with its arrow pointing counterclockwise around the center, desired in red and current in blue, so the robot spins counterclockwise in place."><figcaption><p>The spin test from the dashboard: modules/drive/inplace on, every module tangent to the robot.</p></figcaption></figure>

[Determine Inversion](../fix-a-problem/determine-inversion.md) lists every symptom and its cause.
{% endstep %}
{% endstepper %}
