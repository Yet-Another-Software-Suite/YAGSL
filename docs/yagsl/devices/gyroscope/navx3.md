# NavX3-CAN



{% hint style="info" %}
NavX3-CAN supports CAN 2.0 (roboRIO) and has CAN-FD capability. See Studica’s notes for CAN-FD requirements.
{% endhint %}

## Documentation

{% embed url="https://www.studica.co/navx3-can-imu" %}
Studica product page
{% endembed %}

{% embed url="https://github.com/Studica-Robotics/NavX" %}
Studica NavX releases, firmware tools, and vendordeps
{% endembed %}

## YAGSL Checklist

* [ ] Install **StudicaLib** vendordep (do not install both Studica and StudicaLib).
* [ ] Update NavX3-CAN firmware to **5.0.4+** using Studica Hardware Manager.
* [ ] Use Studica Hardware Manager to find the NavX3-CAN CAN ID and enter it in your config (often ships as 0).

## Calibration

NavX3-CAN ships factory-calibrated. If you want higher accuracy, use Studica Hardware Manager to re-calibrate; the sensor auto-detects its orientation during calibration.

## Communication

NavX3-CAN communicates over CAN. Connect CAN-H/CAN-L to the robot CAN bus and ensure the bus is properly terminated.

## Example `swervedrive.json`

<pre class="language-json"><code class="lang-json">{
  "imu": {
    "type": <a data-footnote-ref href="#user-content-fn-1">"navx3"</a>,
    "id": <a data-footnote-ref href="#user-content-fn-2">0</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-3">null</a>
  },
  "invertedIMU": <a data-footnote-ref href="#user-content-fn-4">false</a>,
  "modules": [
    "frontleft.json",
    "frontright.json",
    "backleft.json",
    "backright.json"
  ]
}
</code></pre>

[^1]: Select the `navx3` IMU device to instantiate and use.

[^2]: CAN ID of the NavX3-CAN as shown in Studica Hardware Manager (often ships as 0).

[^3]: Set to `null` (or omit). `canbus` is CTRE-only and is not used for navX3.
