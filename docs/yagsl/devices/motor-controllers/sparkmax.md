# SparkMAX

This page refers to the REV SparkMAX while attached to a **BRUSHLESS MOTOR (REV NEOs)** further documentation will be added later to discuss using the REV SparkMAX in brushed mode.

{% embed url="https://www.revrobotics.com/rev-11-2158/" %}
Product link to the REV SparkMAX
{% endembed %}

## Getting Started

An small example program to create a [`CANSparkMax`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkmax) object is as follows.

<pre class="language-java"><code class="lang-java">import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;


new CANSparkMax(<a data-footnote-ref href="#user-content-fn-1">10</a>, MotorType.kBrushless);
</code></pre>

## Documentation

The SparkMAX allows you to test the motor controller PID, run the motor at a set percentage, and update the firmware all from the REV Hardware Client. Installation of the REV Hardware client is required to use the SparkMAX for YAGSL.

{% embed url="https://docs.revrobotics.com/rev-hardware-client/gs/install" %}
REV Hardware Client Installation guide + download
{% endembed %}

{% embed url="https://docs.revrobotics.com/rev-hardware-client/ion/spark-max" %}
SparkMAX guide in REV Hardware Client
{% endembed %}

{% hint style="warning" %}
The SparkMAX must be disconnected from the CAN bus **on startup** to run via the REV Hardware Client!
{% endhint %}

## Tuning PID

To tune the PID of the SparkMAX you need to open up the REV Hardware Client select the SparkMAX then open the telemetry tab and set it to position control while messing with the parameters in the lefthand pane.

{% embed url="https://docs.revrobotics.com/rev-hardware-client/ion/telemetry" %}

## YAGSL Checklist

* [ ] Set all SparkMAX's to unique CAN ID's.
* [ ] Update all SparkMAX's to the latest firmware.
* [ ] The SparkMAX rotates the motor counterclockwise positive. (Invert the SparkMAX programmatically if it does not rotate counterclockwise positive).
* [ ] Tune the PID value's for one drive motor.
* [ ] Tune the PID value's for one steering/azimuth/angle motor.

## Communication

The SparkMAX is capable of communicating via PWM this mode is not supported by YAGSL due to the fact sensors are required for both drive and steering/azimuth/angle motors.

## Module example configuration

The following example is one for a module configuration file, e.g. `frontleft.json`, `frontright.json`, `backleft.json`, and `backright.json`.

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "type": <a data-footnote-ref href="#user-content-fn-2">"sparkmax"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-3">5</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-4">null</a>
  },
  "angle": {
    "type": <a data-footnote-ref href="#user-content-fn-5">"sparkmax"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-6">6</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-7">null</a>
  },
  "encoder": {
    "type": "cancoder",
    "id": 11,
    "canbus": null
  },
  "inverted": {
    "drive": <a data-footnote-ref href="#user-content-fn-8">false</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-9">false</a>
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
    "p": <a data-footnote-ref href="#user-content-fn-10">0.0020645</a>,
    "i": <a data-footnote-ref href="#user-content-fn-11">0</a>,
    "d": <a data-footnote-ref href="#user-content-fn-12">0</a>,
    "f": <a data-footnote-ref href="#user-content-fn-13">0</a>,
    "iz": 0
  },
  "angle": {
    "p": <a data-footnote-ref href="#user-content-fn-14">0.01</a>,
    "i": <a data-footnote-ref href="#user-content-fn-15">0</a>,
    "d": <a data-footnote-ref href="#user-content-fn-16">0</a>,
    "f": <a data-footnote-ref href="#user-content-fn-17">0</a>,
    "iz": 0
  }
}
</code></pre>

## Example `physicalproperties.json`

<pre class="language-json"><code class="lang-json">{
  "conversionFactor": {
    "drive": <a data-footnote-ref href="#user-content-fn-18">0.047286787200699704</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-19">16.8</a>
  },
  "currentLimit": {
    "drive": <a data-footnote-ref href="#user-content-fn-20">40</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-21">20</a>
  },
  "rampRate": {
    "drive": <a data-footnote-ref href="#user-content-fn-22">0.25</a>,
    "angle": <a data-footnote-ref href="#user-content-fn-23">0.25</a>
  },
  "wheelGripCoefficientOfFriction": 1.19,
  "optimalVoltage": 12
}
</code></pre>

[^1]: Refers to the SparkMAX with the CAN ID of `10`

[^2]: SparkMAX is selected as the motor type.

[^3]: CAN ID for this drive motor controller SparkMAX is `5`

[^4]: SparkMAX's are not compatible with CANivore's so this must be `null` or `""`.

[^5]: SparkMAX is selected as the motor type.

[^6]: CAN ID for this drive motor controller SparkMAX is `6`

[^7]: SparkMAX's are not compatible with CANivore's so this must be `null` or `""`.

[^8]: Drive motor does not need to be inverted to rotate counterclockwise positively.

[^9]: Steering/azimuth/angle motor does not need to be inverted to rotate counterclockwise positively.

[^10]: This is the kP which is used on the SparkMAX to maintain the desired velocity as meters/second.

[^11]: kI usually does not need to be set.

[^12]: kD would be useful to dampen this and achieve the velocity faster with minimal overshooting.

[^13]: This is the static feedforward as a percentage of voltage to have the wheel spin.

[^14]: This is the kP which is used on the SparkMAX to maintain the desired angle as degrees.

[^15]: kI usually does not need to be set.

[^16]: kD would be useful to dampen this and achieve the velocity faster with minimal overshooting.

[^17]: This is the static feedforward as a percentage of voltage to have the wheel spin.

[^18]: Conversion factor for an MK4i L2 with all NEO's. This converts rotations/minute to meters/second. This is set on the motor controller using [`CANSparkMax.getEncoder().setPositionConversionFactor()`](https://codedocs.revrobotics.com/java/com/revrobotics/relativeencoder#setPositionConversionFactor\(double\))

[^19]: Conversion factor for an MK4i L2 with all NEO's. This converts rotations to degrees. This is set on the motor controller using [`CANSparkMax.getEncoder().setPositionConversionFactor()`](https://codedocs.revrobotics.com/java/com/revrobotics/relativeencoder#setPositionConversionFactor\(double\))

[^20]: The maximum current the drive motor can draw is `40`Amps. Set using [`CANSparkMax.setSmartCurrentLimit`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setSmartCurrentLimit\(int\))

[^21]: The maximum current the drive motor can draw is `20`Amps. Set using [`CANSparkMax.setSmartCurrentLimit`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setSmartCurrentLimit\(int\))

[^22]: The maximum ramp rate of the SparkMAX, used to prevent brownouts. This is set using [`CANSparkMAX.setClosedLoopRampRate`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setClosedLoopRampRate\(double\))and [`CANSparkMax.setOpenLoopRampRate`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setOpenLoopRampRate\(double\))

[^23]: The maximum ramp rate of the SparkMAX, used to prevent brownouts. This is set using [`CANSparkMAX.setClosedLoopRampRate`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setClosedLoopRampRate\(double\))and [`CANSparkMax.setOpenLoopRampRate`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setOpenLoopRampRate\(double\))
