# Spark Flex

This page refers to the REV Spark Flex while attached to a **BRUSHLESS MOTOR (REV Vortex's)** .

{% embed url="https://www.revrobotics.com/next-generation-spark-neo/" %}

{% embed url="https://www.revrobotics.com/rev-21-1652/" %}

{% embed url="https://www.revrobotics.com/rev-11-2159/" %}

## Getting Started

An small example program to create a [`CANSparkFlex`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkflex) object is as follows.

<pre class="language-java"><code class="lang-java">import com.revrobotics.CANSparkFlex;
import com.revrobotics.CANSparkLowLevel.MotorType;


new CANSparkFlex(<a data-footnote-ref href="#user-content-fn-1">10</a>, MotorType.kBrushless);
</code></pre>

## Documentation

The Spark Flex allows you to test the motor controller PID, run the motor at a set percentage, and update the firmware all from the REV Hardware Client. Installation of the REV Hardware client is required to use the SparkFlex for YAGSL.

{% embed url="https://docs.revrobotics.com/rev-hardware-client-2" %}
REV Hardware Client 2 Installation guide + download
{% endembed %}

{% embed url="https://docs.revrobotics.com/brushless/spark-flex/gs/make-it-spin" %}
Make it spin using the REV Hardware Client
{% endembed %}

{% hint style="warning" %}
The Spark Flex must be disconnected from the CAN bus **on startup** to run via the REV Hardware Client!
{% endhint %}

## Tuning PID

To tune the PID of the Spark Flex you need to open up the REV Hardware Client select the Spark Flex then open the telemetry tab and set it to position control while messing with the parameters in the lefthand pane.

{% embed url="https://docs.revrobotics.com/rev-hardware-client/ion/telemetry" %}

## YAGSL Checklist

* [ ] Set all Spark Flex's to unique CAN ID's.
* [ ] Update all Spark Flex's to the latest firmware.
* [ ] The Spark Flex rotates the motor counterclockwise positive. (Invert the SparkFlex programmatically if it does not rotate counterclockwise positive).
* [ ] Tune the PID value's for one drive motor.
* [ ] Tune the PID value's for one steering/azimuth/angle motor.

## Communication

The Spark Flex is only capable of communicating via CAN and may see some exciting new features soon with CAN FD support!

## Module example configuration

The following example is one for a module configuration file, e.g. `frontleft.json`, `frontright.json`, `backleft.json`, and `backright.json`.

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "type": <a data-footnote-ref href="#user-content-fn-2">"sparkflex"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-3">5</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-4">null</a>
  },
  "angle": {
    "type": <a data-footnote-ref href="#user-content-fn-5">"sparkflex"</a>,
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

[^1]: Refers to the Spark Flex with the CAN ID of `10`

[^2]: Spark Flex is selected as the motor type.

[^3]: CAN ID for this drive motor controller Spark Flex is `5`

[^4]: Spark Flex's are not compatible with CANivore's so this must be `null` or `""`. This may change in the future!

[^5]: Spark Flex is selected as the motor type.

[^6]: CAN ID for this drive motor controller SparkFlex is `6`

[^7]: Spark Flex's are not compatible with CANivore's so this must be `null` or `""`. This may change in the future!

[^8]: Drive motor does not need to be inverted to rotate counterclockwise positively.

[^9]: Steering/azimuth/angle motor does not need to be inverted to rotate counterclockwise positively.

[^10]: This is the kP which is used on the Spark Flex to maintain the desired velocity as meters/second.

[^11]: kI usually does not need to be set.

[^12]: kD would be useful to dampen this and achieve the velocity faster with minimal overshooting.

[^13]: This is the static feedforward as a percentage of voltage to have the wheel spin.

[^14]: This is the kP which is used on the Spark Flex to maintain the desired angle as degrees.

[^15]: kI usually does not need to be set.

[^16]: kD would be useful to dampen this and achieve the velocity faster with minimal overshooting.

[^17]: This is the static feedforward as a percentage of voltage to have the wheel spin.

[^18]: Conversion factor for an MK4i L2 with all NEO's. This converts rotations/minute to meters/second. This is set on the motor controller using [`CANSparkFlex.getEncoder().setPositionConversionFactor()`](https://codedocs.revrobotics.com/java/com/revrobotics/relativeencoder#setPositionConversionFactor\(double\))

[^19]: Conversion factor for an MK4i L2 with all NEO's. This converts rotations to degrees. This is set on the motor controller using [`CANSparkFlex.getEncoder().setPositionConversionFactor()`](https://codedocs.revrobotics.com/java/com/revrobotics/relativeencoder#setPositionConversionFactor\(double\))

[^20]: The maximum current the drive motor can draw is `40`Amps. Set using [`CANSparkFlex.setSmartCurrentLimit`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setSmartCurrentLimit\(int\))

[^21]: The maximum current the drive motor can draw is `20`Amps. Set using [`CANSparkFlex.setSmartCurrentLimit`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setSmartCurrentLimit\(int\))

[^22]: The maximum ramp rate of the SparkMAX, used to prevent brownouts. This is set using [`CANSparkFlex.setClosedLoopRampRate`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setClosedLoopRampRate\(double\))and [`CANSparkFlex.setOpenLoopRampRate`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setOpenLoopRampRate\(double\))

[^23]: The maximum ramp rate of the SparkMAX, used to prevent brownouts. This is set using [`CANSparkMAX.setClosedLoopRampRate`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setClosedLoopRampRate\(double\))and [`CANSparkMax.setOpenLoopRampRate`](https://codedocs.revrobotics.com/java/com/revrobotics/cansparkbase#setOpenLoopRampRate\(double\))
