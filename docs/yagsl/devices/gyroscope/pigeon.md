# Pigeon

This refers to the very first iteration of the CTRE Pigeon which is discontinued and only supported via Phoenix 5.

## Phoenix Tuner

To communicate and update this device you need to install the Phoenix tuner.

{% embed url="https://store.ctr-electronics.com/software/" %}
Phoenix Tuner is available under "Phoenix Framework for HERO C#"&#x20;
{% endembed %}

## Documentation

The Pigeon is only available in Phoenix 5 so the documentation is located below. You must include Phoenix 5 libraries in YAGSL partly because of this device.

{% embed url="https://v5.docs.ctr-electronics.com/en/stable/ch11_BringUpPigeon.html" %}
Guide to programming and configuring the Pigeon
{% endembed %}

## YAGSL Checklist

* [ ] Set a unique CAN ID.
* [ ] Update to the latest available firmware.

## Communication

After setting the CAN ID you set the `id` portion of the JSON configuration for the `pigeon` to the CAN ID. This device is not compatible with CANivore's thus the `canbus` option does not apply and should be `""` or `"rio"` or `null`.&#x20;

## Example `swervedrive.json`

<pre class="language-json"><code class="lang-json">{
  "imu": {
<strong>    "type": "<a data-footnote-ref href="#user-content-fn-1">pigeon</a>",
</strong>    "id": <a data-footnote-ref href="#user-content-fn-2">13</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-3">null</a>
  },
  "invertedIMU": true,
  "modules": [
    "frontleft.json",
    "frontright.json",
    "backleft.json",
    "backright.json"
  ]
}
</code></pre>

[^1]: Pigeon device

[^2]: CAN ID 13

[^3]: Not applicable so `null` is the selected choice.
