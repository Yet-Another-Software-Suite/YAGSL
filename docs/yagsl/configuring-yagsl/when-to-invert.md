# When to invert?

Swerve Modules and Swerve Drives require some inversions to get working properly. The goal is to get everything to increase counter clockwise positive!

{% hint style="warning" %}
When your gears are grinding on the ground but not while on blocks and your wheels are facing and spinning in the right directions you may need to tune PID instead of inverting!
{% endhint %}

{% hint style="warning" %}
IF you are inverted incorrectly your modules or robot may spin "out-of-control"
{% endhint %}

## Swerve Motors

When you spin your motor counterclockwise the value in NetworkTables `swerve/modules/.../Raw Absolute Encoder` and `swerve/modules/.../Raw Angle Encoder` should both increase.

* ![](../.gitbook/assets/ShuffleboardAbsoluteEncoderHighlight.png)
* Take note of the `swerve/modules/.../Raw Absolute Encoder` value's and use them for `absoluteEncoderOffset` in the module JSONs.

## Spin your module counterclockwise

{% hint style="danger" %}
Spin your modules **COUNTERclockwise** from the top down view.
{% endhint %}

<figure><img src="../.gitbook/assets/devilbots_cropped_swerve_orientation.png" alt=""><figcaption><p>Purple shows the way your bevels should be facing (photo by Team 2876)</p></figcaption></figure>

The swerve drive should be on it's side or otherwise lifted. Your swerve module bevels must be facing the left like shown. To rotate the swerve modules they must be rotated counterclockwise like shown.

### If the `swerve/modules/.../Raw Angle Encoder` is decreasing...

Invert your angle motor for every module that is decreasing!

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "type": "sparkmax",
    "id": 2,
    "canbus": null
  },
  "angle": {
    "type": "sparkmax",
    "id": 1,
    "canbus": null
  },
  "encoder": {
    "type": "cancoder",
    "id": 10,
    "canbus": null
  },
  "inverted": {
    "drive": false,
<strong>    "angle": true
</strong>  },
  "absoluteEncoderInverted": false,
  "absoluteEncoderOffset": -50.977,
  "location": {
    "front": 12,
    "left": -12
  }
}
</code></pre>

### If the `swerve/modules/.../Raw Absolute Encoder` is decreasing...

Invert the absolute encoder in the module JSON with `absoluteEncoderInverted` as shown here.

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "type": "sparkmax",
    "id": 2,
    "canbus": null
  },
  "angle": {
    "type": "sparkmax",
    "id": 1,
    "canbus": null
  },
  "encoder": {
    "type": "cancoder",
    "id": 10,
    "canbus": null
  },
  "inverted": {
    "drive": false,
    "angle": false
  },
<strong>  "absoluteEncoderInverted": true,
</strong>  "absoluteEncoderOffset": -50.977,
  "location": {
    "front": 12,
    "left": -12
  }
}
</code></pre>

## Spin your wheel counterclockwise

{% hint style="warning" %}
Sometimes you may need to invert these if when you rotate the robot changes it's front/back while driving in field-oriented mode.
{% endhint %}

{% hint style="danger" %}
#### Odometry mismatch

Whenever your robot is driving backwards in odometry and forwards in real life while maintaining that a spin-in-place generates Counter-Clockwise-Positive movement you may need to apply this patch.

You must add `180` to every `absoluteEncoderOffset` in every module file to correct this behavior.
{% endhint %}

### If the `swerve/modules/.../Raw Drive Encoder` is decreasing...

Invert your drive motor for every module that is decreasing!

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "type": "sparkmax",
    "id": 2,
    "canbus": null
  },
  "angle": {
    "type": "sparkmax",
    "id": 1,
    "canbus": null
  },
  "encoder": {
    "type": "cancoder",
    "id": 10,
    "canbus": null
  },
  "inverted": {
<strong>    "drive": true,
</strong>    "angle": false
  },
  "absoluteEncoderInverted": false,
  "absoluteEncoderOffset": -50.977,
  "location": {
    "front": 12,
    "left": -12
  }
}
</code></pre>

## Rotate your robot counterclockwise

{% hint style="danger" %}
Keep in mind that your robot spinning counter clockwise should look like this when the wheels are powered! You **WILL** need to change your IDs for each module file if they don't.
{% endhint %}

<figure><img src="../.gitbook/assets/id_change1.png" alt=""><figcaption><p>ID's relocated in swerve module files</p></figcaption></figure>

You should notice the `Raw IMU Yaw` field in your driver dashboard increase. If it doesn't you need to invert your IMU like this.

<pre class="language-json"><code class="lang-json">{
  "imu": {
    <a data-footnote-ref href="#user-content-fn-1">"type": "pigeon2"</a>,
    "id": 13,
    "canbus": "canivore"
  },
<strong>  "invertedIMU": true,
</strong>  "modules": [
    "frontleft.json",
    "frontright.json",
    "backleft.json",
    "backright.json"
  ]
}
</code></pre>

[^1]: See more information [gyroscope.md](../devices/gyroscope.md "mention")
