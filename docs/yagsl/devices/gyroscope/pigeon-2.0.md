# Pigeon 2.0

The Pigeon 2.0 is the next iteration of the Pigeon IMU by CTRE. This device is compatible with the CANivore.

{% embed url="https://store.ctr-electronics.com/pigeon-2/" %}

## Tuner X

This device can be upgraded via CTRE Tuner X.

{% embed url="https://v6.docs.ctr-electronics.com/en/latest/docs/tuner/index.html" %}

## Documentation

{% embed url="https://pro.docs.ctr-electronics.com/en/latest/docs/hardware-reference/pigeon2/index.html" %}

{% embed url="https://v6.docs.ctr-electronics.com/en/latest/docs/tuner/pigeon-cal.html" %}

Pay close attention to the LED status code to help you debug this at any time.  Remember that any settings changed in Tuner X for the Pigeon 2.0 are overwritten on startup of YAGSL.

## YAGSL Checklist

* [ ] Yaw increments CoutnerClockWise Positive.
* [ ] Pigeon 2.0 is as centered as possible.
* [ ] Pigeon 2.0 is updated to the latest firmware.
* [ ] Pigeon 2.0 is updated to a unique CAN ID.
* [ ] Calibrate the Pigeon 2.0 when it is placed on the robot.

## Communication

This device communicated with the roboRIO over the CAN bus and can be paired with the [CANivore ](https://store.ctr-electronics.com/canivore/)that way it does not take up bandwidth on the roboRIO's CAN bus. To do this you need to set a name for the [CANivore ](https://pro.docs.ctr-electronics.com/en/latest/docs/canivore/canivore-setup.html)and use that as the `canbus` name in YAGSL.

{% embed url="https://pro.docs.ctr-electronics.com/en/latest/docs/canivore/canivore-setup.html" %}

## Example `swervedrive.json`

The following is an example of `swervedrive.json` which sets up the Pigeon 2.0. Keep in mind that the Pigeon 2.0 is compatible with the CANivore so the `canbus` parameter actually works!

<pre class="language-json"><code class="lang-json">{
  "imu": {
    "type": <a data-footnote-ref href="#user-content-fn-1">"pigeon2"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-2">13</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-3">"canivore"</a>
  },
  "invertedIMU": <a data-footnote-ref href="#user-content-fn-4">true</a>,
  "modules": [
    "frontleft.json",
    "frontright.json",
    "backleft.json",
    "backright.json"
  ]
}
</code></pre>



[^1]: Pigeon 2.0 IMU selected

[^2]: The CAN ID of the Pigeon is 13.

[^3]: The Pigeon 2.0 is on a CANivore connected to the roboRIO named "`canivore`".

[^4]: The Pigeon is inverted.
