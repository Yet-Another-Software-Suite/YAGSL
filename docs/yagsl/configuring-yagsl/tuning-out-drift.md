# Tuning out Drift

When trying to calculate your current pose, the pose estimator does an excellent job to a degree (you can tune this but most teams don't).

The very first thing that you should tune is [the drive and angle PIDs. ](how-to-tune-pidf.md)

Teams should tune the `SwerveDrivePoseEstimator` `visionStdDev`if you’re using vision to help odometry. To tune vision it’s mostly guess and check on the standard deviation with a scale depending on distance. Some teams experience better stability by filtering out all estimated poses X distance away.

After that you can tune discretization to compensate for the system delay. More information on tuning discretization is [here.](../overview/our-features/chassis-speed-discretization.md)

After that you can tune your [angular velocity coefficient to compensate for gyro system delay](../overview/our-features/angular-velocity-compensation.md) (not as necessary when you have a pigeon on a canivore).

All of these are individual tuning steps which build on top of each other.&#x20;

{% hint style="warning" %}
If you mess with a lower step all the higher steps will be out of sync, and if you need to replace a part like your gyro you may need to redo all of it anyways.
{% endhint %}

The math heavy explanation on tuning out drift is available here.

{% embed url="https://github.com/calcmogul/controls-engineering-in-frc" %}
