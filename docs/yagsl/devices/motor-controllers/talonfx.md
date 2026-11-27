# TalonFX

## Getting Started

The TalonFX is the motor controller used to control Kraken X60's and Falcon 500's

{% embed url="https://wcproducts.com/products/kraken" %}

{% hint style="warning" %}
If you are using Falcon 500 v2 (which are effectively discontinued) make sure that you apply the loctite fix described here!\
[https://content.vexrobotics.com/vexpro/Falcon/217-6515-753-Falcon500-V2-Upgrade.pdf](https://content.vexrobotics.com/vexpro/Falcon/217-6515-753-Falcon500-V2-Upgrade.pdf)
{% endhint %}

They are commonly used as drive motors since they can be more powerful and efficient than REV NEO's.

```java
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

TalonFX motor = new TalonFX(10);
motor.setControl(new DutyCycleOut(1.0)); // 100% full speed positive.
```

## Documentation

This device can be upgraded via CTRE Tuner X.

{% embed url="https://v6.docs.ctr-electronics.com/en/latest/docs/tuner/index.html" %}
Tuner X install and documentation
{% endembed %}

{% embed url="https://pro.docs.ctr-electronics.com/en/stable/docs/hardware-reference/talonfx/index.html" %}
Official Documentation source
{% endembed %}

Pay close attention to the LED status code to help you debug this at any time.  Remember that any settings changed in Tuner X for the Talon FX are overwritten on startup of YAGSL.

## YAGSL Checklist

* [ ] Set all TalonFX's to unique CAN ID's.
* [ ] Update all TalonFX's to the latest firmware.
* [ ] The TalonFX rotates the motor counterclockwise positive. (Invert the TalonFX programmatically if it does not rotate counterclockwise positive).
* [ ] Tune the PID value's for one drive motor.
* [ ] Tune the PID value's for one steering/azimuth/angle motor.

## Module example configuration

The following example is one for a module configuration file, e.g. `frontleft.json`, `frontright.json`, `backleft.json`, and `backright.json`.

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "type": <a data-footnote-ref href="#user-content-fn-1">"talonfx"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-2">5</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-3">null</a>
  },
  "angle": {
    "type": <a data-footnote-ref href="#user-content-fn-4">"talonfx"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-5">6</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-6">null</a>
  },
  "encoder": {
    "type": "cancoder",
    "id": 11,
    "canbus": null
  },
  "inverted": {
    "drive": <a data-footnote-ref href="#user-content-fn-7">false</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-8">false</a>
  },
  "absoluteEncoderOffset": -18.281,
  "location": {
    "front": -12,
    "left": -12
  }
}
</code></pre>

## Example `pidfproperties.json`

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "p": <a data-footnote-ref href="#user-content-fn-9">0.0020645</a>,
    "i": <a data-footnote-ref href="#user-content-fn-10">0</a>,
    "d": <a data-footnote-ref href="#user-content-fn-11">0</a>,
    "f": <a data-footnote-ref href="#user-content-fn-12">0</a>,
    "iz": 0
  },
  "angle": {
    "p": <a data-footnote-ref href="#user-content-fn-13">0.01</a>,
    "i": <a data-footnote-ref href="#user-content-fn-14">0</a>,
    "d": <a data-footnote-ref href="#user-content-fn-15">0</a>,
    "f": <a data-footnote-ref href="#user-content-fn-16">0</a>,
    "iz": 0
  }
}
</code></pre>

## Example `physicalproperties.json`

<pre class="language-json"><code class="lang-json">{
  "conversionFactor": {
    "drive": <a data-footnote-ref href="#user-content-fn-17">0.047286787200699704</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-18">16.8</a>
  },
  "currentLimit": {
    "drive": <a data-footnote-ref href="#user-content-fn-19">40</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-20">20</a>
  },
  "rampRate": {
    "drive": <a data-footnote-ref href="#user-content-fn-21">0.25</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-22">0.25</a>
  },
  "wheelGripCoefficientOfFriction": 1.19,
  "optimalVoltage": 12
}
</code></pre>

[^1]: TalonFX selected

[^2]: CAN ID for this drive motor controller TalonFX is `5`

[^3]: TalonFX's are compatible with CANivores and can reduce CAN bus utilization if they are used, set this to the name of the CANivore if the TalonFX is on the CANivore's CAN loop.

[^4]: TalonFX selected

[^5]: CAN ID for this drive motor controller TalonFX is `6`

[^6]: TalonFX's are compatible with CANivores and can reduce CAN bus utilization if they are used, set this to the name of the CANivore if the TalonFX is on the CANivore's CAN loop.

[^7]: Drive motor does not need to be inverted to rotate counterclockwise positively.

[^8]: Steering/azimuth/angle motor does not need to be inverted to rotate counterclockwise positively.

[^9]: This is the kP which is used on the TalonFX to maintain the desired velocity as meters/second.

[^10]: kI usually does not need to be set.

[^11]: kD would be useful to dampen this and achieve the velocity faster with minimal overshooting.

[^12]: This is the static feedforward as a percentage of voltage to have the wheel spin.

[^13]: This is the kP which is used on the TalonFX to maintain the desired angle as degrees.

[^14]: kI usually does not need to be set.

[^15]: kD would be useful to dampen this and achieve the velocity faster with minimal overshooting.

[^16]: This is the static feedforward as a percentage of voltage to have the wheel spin.

[^17]: Conversion factor for an MK4i L2 with all Kraken's to go from rotations/minute to meters/second.

[^18]: Conversion factor for an MK4i L2 with all Krakens to go from rotations to degrees.

[^19]: The maximum current the drive motor can draw is `40`Amps.&#x20;

[^20]: The maximum current the drive motor can draw is `20`Amps.

[^21]: The maximum ramp rate of the TalonFX, used to prevent brownouts.&#x20;

[^22]: The maximum ramp rate of the TalonFX, used to prevent brownouts.&#x20;
