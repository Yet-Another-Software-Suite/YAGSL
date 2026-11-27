# 5. Practice in simulation

Run the robot program in simulation and go through live tuning once before you touch the real robot. Simulation is practice, not tuning: the gains it finds can't be trusted on the real modules, but it teaches you the dashboard and catches config mistakes while nothing can break. You'll do the same live tuning for real in [step 7](05-deploy-and-drive.md).

Live tuning needs `TelemetryVerbosity.HIGH` and both drive-to-pose controllers, which the code from [step 4](write-the-swerve-code.md) already sets. [Tune PIDF gains](../tune-and-extend/tune-pidf-gains.md#live-tuning) explains each piece.

**Start live tuning.** Enable Utility mode, start the `Tuning/swerve/Live Tuning` command, and turn on one tuning mode. `Tuning/swerve/` values only take effect while the command runs.

<figure><img src="../.gitbook/assets/yagsl-sim-utility-mode.png" alt="The Robot State panel of the WPILib simulation GUI with two numbered markers: 1 on Utility and 2 on Enable. The OpMode dropdown is left at NONE."><figcaption><p>In simulation: in the sim GUI's Robot State panel, pick Utility (1), then Enable (2). Leave the OpMode at NONE, because a teleop op mode would drive the modules and fight the tuning.</p></figcaption></figure>

<figure><img src="../.gitbook/assets/yagsl-ascope-live-tuning.gif" alt="A recording of AdvantageScope connected to the YAGSL example in simulation, in Utility mode. 1: the tuning mode button beside the sidebar search is clicked, and the values under Tuning become editable. 2: running under Tuning/swerve/Live Tuning is clicked and turns green. 3: enabled under Tuning/swerve/modules/azimuth is clicked and turns green. 4: 15 is typed into angle, then an empty part of the sidebar is clicked to apply it. 5: in the Swerve tab every module turns to 15 degrees."><figcaption><p>Starting live tuning in AdvantageScope, recorded in simulation.</p></figcaption></figure>

<figure><img src="../.gitbook/assets/yagsl-ascope-enable-tuning.png" alt="AdvantageScope connected to the simulation with four numbered markers. 1 is the tuning mode button beside the sidebar search, which makes the values editable. 2 is the running switch under Tuning/swerve/Live Tuning, which starts the tuning command. 3 is the enabled switch under Tuning/swerve/modules/azimuth, which turns azimuth tuning on. 4 is the angle field under Tuning/swerve/modules/azimuth, where you type the setpoint."><figcaption><p>In AdvantageScope: turn on tuning mode (1), click <code>running</code> to start the tuning command (2), turn one tuning mode on (3), then type the setpoint (4).</p></figcaption></figure>

**Tune the azimuth first**, so the wheels point where they're told while you tune the drive. Steering holds a position, and PID alone is enough for that, whichever method you use for the drive.

1. Set `Tuning/swerve/modules/azimuth/enabled` to `true`, then switch `modules/azimuth/angle` between `0` and `45`.
2. Start with a small `feedback/p`. The wheels crawl toward the angle.
3. Raise `feedback/p` until the wheels overshoot the angle and swing back.
4. Add `feedback/d` until the overshoot is gone.

To see it, graph three things for one module, in degrees:

* the angle you typed, `Tuning/swerve/modules/azimuth/angle`;
* the azimuth motor's position, `Mechanisms/swerve/modules/frontleft/azimuth/mechanism/position`;
* the absolute encoder, `Mechanisms/swerve/modules/frontleft/encoder`, added after the motor's position so it's drawn underneath it.

The motor's position and the absolute encoder should line up exactly, so the absolute encoder's line stays hidden. If you can see it, the two readings disagree: check that module's absolute encoder offset, its inversion and the angle gear ratio.

<figure><img src="../.gitbook/assets/yagsl-tune-azimuth.gif" alt="A recording of AdvantageScope tuning the azimuth of the YAGSL example in simulation. A line graph shows the angle typed into Tuning/swerve/modules/azimuth/angle stepping between 0 and 45 degrees and the front left azimuth motor&#x27;s position following it, with the absolute encoder drawn underneath and hidden because it lines up exactly, with the swerve view beside it. Step 1, P 0.5 and D 0: the angle crawls toward the setpoint. Step 2, P 60 and D 0: it overshoots to about 60 degrees and swings back. Step 3, P 60 and D 2: it reaches 45 degrees with no overshoot."><figcaption><p>Azimuth tuning, recorded in simulation: P too low, then overshooting, then D.</p></figcaption></figure>

**Then tune the drive.** Turn azimuth tuning off, set `modules/drive/enabled` to `true`, and switch `modules/drive/velocity` between `0` and `2` meters per second. Graph that speed against what a module's drive motor measures, such as `Mechanisms/swerve/modules/frontleft/drive/measurement/velocity`. Both are in meters per second.

Pick one of these methods. PID only is often enough to get started.

{% tabs %}
{% tab title="PID only" %}
Use the feedback gains alone, starting with every gain at `0`.

1. Raise `feedback/p`. The wheels speed up but stop short of the setpoint. More P gets closer, and too much makes the speed shake.
2. Add `feedback/i` until the speed reaches the setpoint. Too much I overshoots.

<figure><img src="../.gitbook/assets/yagsl-tune-drive-pid.gif" alt="A recording of AdvantageScope tuning the drive of the YAGSL example in simulation with feedback only. A line graph shows the speed typed into Tuning/swerve/modules/drive/velocity stepping between 0 and 2 meters per second and the speed the front left drive motor measures, with the swerve view beside it. Step 1, P 0.4 and I 0: the speed settles at about 0.7 meters per second. Step 2, P 2 and I 0: it settles at about 1.5. Step 3, P 2 and I 5: it reaches 2 meters per second."><figcaption><p>PID only, recorded in simulation: P alone stops short, and I closes the gap.</p></figcaption></figure>
{% endtab %}

{% tab title="PID and FF" %}
Feedforward works out the voltage each speed needs, so feedback only fixes small errors. The wheels reach their speed sooner and track paths better than with PID only.

1. With every feedback gain at `0`, raise `feedforward/v` until the speed lands on the setpoint.
2. Add `feedback/p` so it gets there faster and holds.

<figure><img src="../.gitbook/assets/yagsl-tune-drive-pid-ff.gif" alt="A recording of AdvantageScope tuning the drive of the YAGSL example in simulation with feedforward and feedback. A line graph shows the speed typed into Tuning/swerve/modules/drive/velocity stepping between 0 and 2 meters per second and the speed the front left drive motor measures, with the swerve view beside it. Step 1, V 0.5 and P 0: the speed settles at about 1.6 meters per second. Step 2, V 0.64 and P 0: it lands on 2. Step 3, V 0.64 and P 2: it gets there faster and holds."><figcaption><p>PID and FF, recorded in simulation: V lands on the setpoint, then P makes it quicker.</p></figcaption></figure>
{% endtab %}

{% tab title="Config generator" %}
The config generator's [Quick Start Guide](https://config.yagsl.com/guide#sim-tune) can live-tune from your browser, with a graph of every module and buttons for the setpoint.

1. Turn on **Live**, pick **Address** and connect to `localhost`, the simulator. On the real robot, use the guide's [Tune Real Robot](https://config.yagsl.com/guide#robot-tune) step and your team number.
2. Pick **Drive**, press **Start drive tuning**, tick the safety checks and press **Everything is clear, start**.
3. Turn on **Switch between 0 and 1 m/s every 2 seconds**.
4. Type `feedforward/v`, then click away. Raise it until the speed lands on the setpoint.
5. Type `feedback/p`, then click away, so it gets there faster and holds.

When you're done, **Download pidfproperties.json** gives you the gains to copy into your project. **Azimuth** tunes the steering the same way.

<figure><img src="../.gitbook/assets/yagsl-tune-drive-generator.gif" alt="A recording of the config generator&#x27;s live tuner connected to the YAGSL example in simulation, in Drive mode. Step 1: the switch to step the setpoint between 0 and 1 meter per second is turned on. Step 2: with every gain at 0 the wheels don&#x27;t move. Step 3: feedforward/v is typed as 0.64 and the wheels reach the setpoint. Step 4: feedback/p is typed as 2 and they get there faster. Each value is applied by clicking away."><figcaption><p>The config generator's live tuner, connected to the simulator.</p></figcaption></figure>

**Or press Auto.** Auto runs the same tests as WPILib's SysId: it drives every wheel slowly, then quickly, each way, and fits the feedforward to how they moved. Then it works out a P to go with it and fills in all six gains. Auto gets close, not perfect, so check its gains against the graph and adjust from there.

In this recording Auto found `feedforward/v` = 0.63, close to the 0.64 found by hand in the other tabs. Its `feedback/p` made the speed shake a little in simulation, so P is lowered to 2 at the end.

<figure><img src="../.gitbook/assets/yagsl-tune-drive-auto.gif" alt="A recording of the config generator&#x27;s live tuner connected to the YAGSL example in simulation, in Drive mode, starting with every gain at 0. Auto is pressed and runs four tests while the graph shows the wheel speed: slowly forward, slowly backward, quickly forward and quickly backward. It then fills in the gains: feedback/p 5.76, feedforward/v 0.63 and feedforward/a 0.1. With the setpoint switching between 0 and 1 meter per second the speed shakes a little around it, so feedback/p is lowered to 2 by typing it and clicking away, and the speed then tracks the setpoint without shaking."><figcaption><p>Auto works out the gains with SysId's tests. They're a starting point to check against the graph.</p></figcaption></figure>
{% endtab %}
{% endtabs %}

{% hint style="warning" %}
Apply a value by clicking away from the box. Don't press Enter: it disables the robot from the Driver Station.
{% endhint %}

The gains in these recordings are what worked for the YAGSL example in simulation. They aren't starting values for your robot.

**Save what you find.** Values typed on the dashboard are gone the next time the robot program starts. In simulation, copy them into `pidfproperties_sim.json`, which YAGSL reads only in simulation.

<figure><img src="../.gitbook/assets/yagsl-live-tuning-save.png" alt="The tuning values under Tuning/swerve/modules map to the JSON: azimuth/feedback/p to angle.p, azimuth/feedback/d to angle.d, drive/feedforward/v to drive.v and drive/feedback/p to drive.p. In simulation they go into pidfproperties_sim.json, which is read only in simulation. On the real robot they go into pidfproperties.json."><figcaption><p>Where the live values belong.</p></figcaption></figure>

[YAMS' live tuning page](https://yams.yassrobotics.com/documentation/understanding/live-tuning#swerve-drive) covers every swerve tuning key, `feedforward/s`, and auto-align tuning.
