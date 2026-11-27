# Your first swerve robot

This tutorial takes a team from a bare WPILib project to a driving swerve robot using YAGSL. Follow the seven steps in order. Each one builds on the last, and by the end your robot will be driving under field-relative control with a tuned configuration.

{% hint style="success" %}
**The quickest path is the config generator's Quick Start Guide.** Fill in the form at [config.yagsl.com](https://config.yagsl.com) and press **Download Config**: the Quick Start Guide opens with checklists that take you from the YAGSL example project to a tuned robot. This tutorial explains the same steps in more depth, and how to add YAGSL to a project you already have.
{% endhint %}

{% hint style="warning" %}
Swerve drives are complicated and will not "just work" on the first try. If you've never brought up a swerve drive before, budget more than one sitting for this because gathering accurate information about your robot and calibrating hardware always takes longer than expected. If you've done this before, expect this tutorial to take about 30 minutes plus PID tuning time.
{% endhint %}

## Prerequisites

* WPILib 2027 (`2027_alpha7`) installed, and a WPILib Java robot project (new or existing) that uses either Commands v2 or Commands v3. YAGSL supports both, see [Install YAGSL](02-install-yagsl.md).
* A SystemCore robot controller.
* Your swerve modules are physically assembled and wired (motors, absolute encoders, gyroscope all on the robot).
* You know how to deploy code to your robot and open a driver dashboard (Shuffleboard, Elastic, or AdvantageScope).

{% hint style="info" %}
If your swerve drive uses only [Falcon500](https://store.ctr-electronics.com/falcon-500-powered-by-talon-fx/)/[Kraken](https://store.ctr-electronics.com/kraken-x60/)/[TalonFXS](https://store.ctr-electronics.com/products/talon-fxs), [Pigeon2.0](https://store.ctr-electronics.com/pigeon-2/), and [CANCoder](https://store.ctr-electronics.com/cancoder/) from CTRE, you can also consider the [Tuner X Swerve Drive Generator](https://pro.docs.ctr-electronics.com/en/latest/docs/tuner/tuner-swerve/index.html) as an alternative. YAGSL supports a much wider range of hardware and mixes vendors freely.
{% endhint %}

## The seven steps

1. [Gather your robot information](01-gather-your-robot-information.md): collect the CAN IDs, gear ratios, and module locations you'll need before you touch any software.
2. [Generate your configuration](03-generate-your-configuration.md): use [config.yagsl.com](https://config.yagsl.com) to turn your robot information into a config directory.
3. [Install YAGSL](02-install-yagsl.md): add the YAGSL vendordep for your command framework, the YAMS vendordep it requires, and the vendordeps for the hardware your robot has.
4. [Write the swerve code](write-the-swerve-code.md): wire up `SwerveParser` in a subsystem (Commands v2) or mechanism (Commands v3) and bind your controller.
5. [Practice in simulation](practice-in-simulation.md): learn live tuning on the dashboard while nothing can break.
6. [Check and calibrate your hardware](04-verify-and-calibrate-hardware.md): confirm every motor, encoder, and gyro reports direction correctly, and capture absolute encoder offsets.
7. [Tune and drive](05-deploy-and-drive.md): tune the modules on the real robot and take it for its first drive.

If something goes wrong along the way, [Find your problem](../fix-a-problem/fix-a-problem.md) matches what your robot is doing to the page that fixes it.
