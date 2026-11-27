# Check your gyroscope

## Define a front and expect to change it.

It's easiest to define a front, maybe it's one that you want but expect to change it. Relative locations of modules are determined by the gyroscope. If your gyroscope is installed in the wrong orientation and can't be changed you WILL have to compensate for it in code that is not in any existing examples or apart of YAGSL.&#x20;

{% hint style="warning" %}
FRONT is determined by gyroscope `0`!
{% endhint %}

## Calibrate your gyroscope...

If you are using a NavX or any gyroscope I would highly recommend that you calibrate it at least once especially at tournaments. NavX's are calibrated to the humidity of their factory (in Hawaii) and you should definitely recalibrate it when it gets to your shop. I have more documentation on this for specific gyroscopes here.

{% content-ref url="../devices/gyroscope.md" %}
[gyroscope.md](../devices/gyroscope.md)
{% endcontent-ref %}

## Why should you invert your gyorscope?

Gyrscope's should be inverted ONLY if they do not increase their yaw while moving counterclockwise!
