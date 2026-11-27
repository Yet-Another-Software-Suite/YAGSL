# How to tune PIDF

{% hint style="warning" %}
Swerve steering/angle/azimuth motors have PID wrapping enabled at the topmost and bottommost point (e.g "front" and "back") when tuning your PID you may want to test with left or right translation.
{% endhint %}

## The basic idea

YAGSL has PIDF values in [`pidfproperties.json`](configuration/pidf-properties-configuration/) within the swerve module folder.

This has been hashed and boiled down to the simplest form like this before.

* P: if you’re not where you want to be, get there.
* I: if you haven’t been where you want to be for a long time, get there faster
* D: if you’re getting close to where you want to be, slow down.
  * [Video](https://www.youtube.com/watch?v=qKy98Cbcltw) found by 78 found by [this CD Post](https://www.chiefdelphi.com/t/finally-i-understand-pid/450811)

<figure><img src="../.gitbook/assets/pid_explainer.png" alt=""><figcaption></figcaption></figure>

## Tuning PIDF

Another good way to describe it [from CTRE](https://pro.docs.ctr-electronics.com/en/latest/docs/api-reference/device-specific/talonfx/closed-loop-requests.html) is

Manual tuning typically follows this process:

1. Set $$P$$, $$I$$ and $$D$$ to zero.
2. Increase $$P$$ until the output **starts** to **oscillate** **around** the setpoint.
3. Increase $$D$$ as much as possible **without** introducing **jittering** to the response.

## WPI Walkthrough

WPILib has lots of great documentation including PID so I would highly encourage you to check it out!

{% embed url="https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/introduction-to-pid.html" %}
Basics of PID
{% endembed %}

{% embed url="https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-flywheel.html" %}
Tuning Velocity, like the drive motors!
{% endembed %}

{% embed url="https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/tuning-turret.html" %}
Tuning position, like the steering/angle/azimuth motors!
{% endembed %}

## Starting Points

The PIDF values will vary for every robot so tuning and testing are required. Here are some starting points I have documented with the types of motor controllers they correspond to.

{% tabs %}
{% tab title="Drive Motor" %}
#### SparkMax

When the `drive` motor `type` for every module is `sparkmax` then this is what the starting point can be.

<pre class="language-json"><code class="lang-json">{
<strong>  "drive": {
</strong><strong>    "p": 0.0020645,
</strong><strong>    "i": 0,
</strong><strong>    "d": 0,
</strong><strong>    "f": 0,
</strong><strong>    "iz": 0
</strong><strong>  },
</strong>  "angle": {
    "p": 0.01,
    "i": 0,
    "d": 0,
    "f": 0,
    "iz": 0
  }
}
</code></pre>

#### TalonFX

When the drive motor `type` for every module is `talonfx` or alike (`kraken`/`falcon`) then this is what the starting point can be.

<pre class="language-json"><code class="lang-json">{
<strong>  "drive": {
</strong><strong>    "p": 1,
</strong><strong>    "i": 0,
</strong><strong>    "d": 0,
</strong><strong>    "f": 0,
</strong><strong>    "iz": 0
</strong><strong>  },
</strong>  "angle": {
    "p": 50,
    "i": 0,
    "d": 0.32,
    "f": 0,
    "iz": 0
  }
}
</code></pre>
{% endtab %}

{% tab title="Angle Motor" %}
#### SparkMax

When the `angle` motor `type` for every module is `sparkmax` then this is what the starting point can be.

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "p": 0.0020645,
    "i": 0,
    "d": 0,
    "f": 0,
    "iz": 0
  },
<strong>  "angle": {
</strong><strong>    "p": 0.01,
</strong><strong>    "i": 0,
</strong><strong>    "d": 0,
</strong><strong>    "f": 0,
</strong><strong>    "iz": 0
</strong><strong>  }
</strong>}
</code></pre>

#### TalonFX

When the `angle` motor `type` for every module is `talonfx` or alike (`kraken`/`falcon`) then this is what the starting point can be.

<pre class="language-json"><code class="lang-json">{
  "drive": {
    "p": 1,
    "i": 0,
    "d": 0,
    "f": 0,
    "iz": 0
  },
<strong>  "angle": {
</strong><strong>    "p": 50,
</strong><strong>    "i": 0,
</strong><strong>    "d": 0.32,
</strong><strong>    "f": 0,
</strong><strong>    "iz": 0
</strong><strong>  }
</strong>}
</code></pre>
{% endtab %}
{% endtabs %}
