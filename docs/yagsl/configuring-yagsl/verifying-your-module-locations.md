---
description: Your front may not be what you think it is on a robot.
---

# Verifying your Module Locations

After you have initially configured the robot with YAGSL you should test it on block to ensure the modules are facing the correct directions while in the air. You may need to [swap the modules](the-eight-steps.md#how-to-swap-module-configurations) around to ensure they are doing what the robot program thinks they're doing.

## Drive straight

The first test it to command the robot to drive straight. The modules are supposed to look like this in real life and in Elastic,[ FRC Web Components](../analytics-and-debugging/frc-web-components.md), or[ Advantage Scope](../analytics-and-debugging/advantage-scope.md).

<figure><img src="../.gitbook/assets/devilbots_cropped_swerve_orientation.png" alt=""><figcaption></figcaption></figure>

If it doesnt look like this in real life than you need to change the offending modules to match this.



## Rotate the robot

Your robot should rotate CCW+ but for the verification step after confirming your modules are driving straight. As long as the robot appears to be doing either one of these while attempting to rotate you should be fine.&#x20;

{% hint style="warning" %}
The depiction on the left would require you running through [the eight steps](the-eight-steps.md) since your translational axis changes based off your robots heading.&#x20;
{% endhint %}

<figure><img src="../.gitbook/assets/image-48.png" alt=""><figcaption></figcaption></figure>
