---
description: What is a Swerve Module?
---

# Swerve Modules

You may see a bunch of different classes in other teams code which represent a `SwerveModule` and wonder why there isn't a standard class. There is a very good reason for that, every motor, absolute encoder, gear ratio, and installation can be different! Some teams try to prevent more issue's in their Swerve Drive than others because they had more time to debug it. YAGSL aims to take into account all common issues and address them in JSON configuration files.

{% hint style="warning" %}
If you are using a magnetic encoder ensure that the magnet is glued correctly so it does not slip.
{% endhint %}

## What is in a Swerve Module?

* [ ] Drive Gears (ratio must be known)
* [ ] Steering Gears (ratio must be known)
* [ ] Drive Motor (+ controller)
* [ ] Angle/Azimuth/Steering Motor (+ controller)
* [ ] Absolute Encoder

## A review of a typical brushless motor controller

This may seem out of place but when debugging swerve drives this comes in handy very quickly!

Smart Motor Controllers typically have the following features

* [ ] Rotate in either direction.
* [ ] Have sensors on the motor that read in either direction.
* [ ] Burn up when jammed or can't move, causing very high amperage utilization.
* [ ] Drop the voltage level available when running.
* [ ] Need a minimum amount of voltage to turn against friction.
* [ ] Greased gears attaches to shaft.
* [ ] Ramp up to speed at a configurable rate to avoid using too much power instantly.
* [ ] Have integrated PID loops which can control the output based off of a connected sensors input (typically an encoder).
* [ ] Are connected to a CAN bus, by default the `rio` but if a [CANivore](https://store.ctr-electronics.com/canivore/) is connected it could be the name of the [CANivore](https://store.ctr-electronics.com/canivore/) as long as the motor controller supports it.
* [ ] Rotate the wheel in more than one shaft rotation proportional to the gears.

All of these need to be set correctly in order to configure a Swerve Module properly. If one of these are not set correctly you might experience behavior that you won't easily be able to identify.

#### TL;DR

1. Motors can break in many ways and are only expected to operate in one way, refer to here while debugging.
2. Swerve Modules contain, **drive gears**, **steering gears**, **drive motor**, **steering motor**, and a **absolute encoder**.

## How do you code a Swerve Module?

Swerve Module's can get very complicated, very quickly. I will keep this example simple and may expand on it in the future. It will not include all of the steps above however there will be space for that in the constructor. For the purposes of this tutorial I will use [`CANSparkMax`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkmax) from REVLib simply because it can be more verbose.
