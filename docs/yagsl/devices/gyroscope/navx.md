---
description: Calibrating a NavX 2
---

# NavX

The NavX2 is the latest iteration of the NavX series by Studica (formally by KauiLabs) and offers excellent readings and documentation on how to calibrate and really get the most out of your gyroscope.&#x20;

{% embed url="https://www.studica.ca/en/navx-2-mxp-robotics-navigation-sensor" %}

## Getting started

You now have a NavX, congratulations! Time to configure and calibrate it, if you do not follow these steps it will not work as good as possible.

The java class for the NavX is `AHRS` imported from `import com.kauailabs.navx.frc.AHRS;`

## NavX Omni-Mount

NavX can reconfigure the yaw to any position! Here is their guide one how.

{% embed url="https://pdocs.kauailabs.com/navx-mxp/installation/omnimount/" %}

## NavXUI Installation

Java 1.7 or higher is required

{% embed url="https://pdocs.kauailabs.com/navx-mxp/software/navx-mxp-ui/" %}

{% embed url="https://www.kauailabs.com/public_files/navx-mxp/navx-mxp.zip" %}
Installation ZIP
{% endembed %}

Useful for advanced calibration to ensure optimal accuracy on the robot.

## Calibration

Full documentation for calibration is here. The original calibration is done at the factory in Hawaii so it is highly reccommended you do this at least once!

{% embed url="https://pdocs.kauailabs.com/navx-mxp/guidance/gyroaccelcalibration/" %}

We will provide a brief summary here.

Gyroscopes are sensitive to ambient temperature, so it is imperative that you recalibrate them upon reception **AND** at events. NavX instructions are as follows.

1. Place them NavX on a still platform parallel to the earth's surface (use a level!).
2. Press and hold the **CAL** button for at least 10 seconds.
3. When you release the **CAL** button ensure that the **CAL** led flashes briefly.
4. Press the **RESET** button.
5. Wait up to 15 seconds.
6. yaw accuracy may be diminished until the next On-the-fly Gyro calibration completes. (which means your first autonomous may not act right!)

## YAGSL Checklist

* [ ] Update to the latest available firmware.
* [ ] Level the NavX parallel to the earth.
* [ ] Calibrate the NavX.
* [ ] Select the connection/communication method you want to use.

## Connection Methods

### SPI (`navx`, `navx_spi`)

YAGSL supports the NavX SPI communication over the roboRIO MXP. This is the recommended method of communicating with NavX devices.

<pre class="language-json"><code class="lang-json">{
  "imu": {
<strong>    "type": <a data-footnote-ref href="#user-content-fn-1">"navx_spi"</a>,
</strong>    "id": <a data-footnote-ref href="#user-content-fn-2">0</a>,
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

<pre class="language-json"><code class="lang-json">{
  "imu": {
<strong>    "type": <a data-footnote-ref href="#user-content-fn-4">"navx"</a>,
</strong>    "id": <a data-footnote-ref href="#user-content-fn-5">0</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-6">null</a>
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

### Serial (`navx_mxp_serial`, `navx_usb`)

Serial communication is slower than SPI communication and could be more prone to interference however this is the only out of the box way to communicate with the [NavX2 micro](https://www.andymark.com/products/navx2-micro-navigation-sensor) (`navx_usb`) so this must be selected if you are using one. The MXP serial communication can be used incase the SPI MXP communication is not working at the moment.

<pre class="language-json"><code class="lang-json">{
  "imu": {
<strong>    "type": <a data-footnote-ref href="#user-content-fn-7">"navx_mxp_serial"</a>,
</strong>    "id": <a data-footnote-ref href="#user-content-fn-8">0</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-9">null</a>
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

<pre class="language-json"><code class="lang-json">{
  "imu": {
<strong>    "type": <a data-footnote-ref href="#user-content-fn-10">"navx_usb"</a>,
</strong>    "id": <a data-footnote-ref href="#user-content-fn-11">0</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-12">null</a>
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

### I2C (`navx_i2c`)

I2C communication on the MXP is supported however this is known to cause permanent lockouts on the roboRIO randomly, this issue has been thoroughly investigated and no known cause can be found. It is encouraged to avoid using this option at all costs!!

<pre class="language-json"><code class="lang-json">{
  "imu": {
<strong>    "type": <a data-footnote-ref href="#user-content-fn-13">"navx_i2c"</a>,
</strong>    "id": <a data-footnote-ref href="#user-content-fn-14">0</a>,
    "canbus": <a data-footnote-ref href="#user-content-fn-15">null</a>
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

## NavX Micro

<figure><img src="../../.gitbook/assets/navx2_micro.png" alt=""><figcaption><p>NavX micro from andymark</p></figcaption></figure>

If you are using the NavX micro please select the type of `navx_usb` to indicate you are communicating over serial via the USB or else the gyrscope will not work with YAGSL!

[^1]: NavX over SPI on the MXP is selected.

[^2]: Not applicable, can be anything `0` is chosen arbitrarily.

[^3]: Not applicable, so `null` is chosen.

[^4]: Defaults to `navx_spi` behind the scenes.

[^5]: ID is not relevant for the NavX so `0` is chosen arbitrarily.

[^6]: The `canbus` is not relavent for the NavX so `null` ensures nothing is set in the configuration.

[^7]: NavX serial communication over the MXP serial ports.

[^8]: ID is not relevant for the NavX so `0` is chosen arbitrarily.

[^9]: The `canbus` is not relavent for the NavX so `null` ensures nothing is set in the configuration.

[^10]: NavX serial communication over the USB port , this is the easiest way to communicate with the NavX2 micro.

[^11]: ID is not relevant for the NavX so `0` is chosen arbitrarily.

[^12]: The `canbus` is not relavent for the NavX so `null` ensures nothing is set in the configuration.

[^13]: NavX connected via the I2C port of the MXP. This is dangerous and one of the others should be selected instead!

[^14]: ID is not relevant for the NavX so `0` is chosen arbitrarily.

[^15]: The `canbus` is not relavent for the NavX so `null` ensures nothing is set in the configuration.
