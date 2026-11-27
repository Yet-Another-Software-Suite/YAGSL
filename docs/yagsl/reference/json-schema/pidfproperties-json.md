# pidfproperties.json

Default closed-loop PID + feedforward gains shared by every module. Parsed into `swervelib.core.parser.json.PIDFPropertiesJson` (each of `drive`/`angle` is a `swervelib.core.parser.PIDFConfig`).

```json
{
  "drive": {
    "p": 1,
    "i": 0,
    "d": 0
  },
  "angle": {
    "p": 40,
    "i": 0,
    "d": 0.3
  }
}
```

These are the starting gains for SparkMAX NEO modules. See [Tune PIDF gains](../../tune-and-extend/tune-pidf-gains.md#starting-points) for every motor's.

| Field | Type   | Required | Description                                                                                                                 |
| ----- | ------ | -------- | --------------------------------------------------------------------------------------------------------------------------- |
| `p`   | number | yes      | Proportional gain. Volts per wheel rotation per second of error for `drive`, volts per rotation of error for `angle`.       |
| `i`   | number | yes      | Integral gain.                                                                                                              |
| `d`   | number | yes      | Derivative gain.                                                                                                            |
| `s`   | number | no       | `kS`: static friction feedforward (`SimpleMotorFeedforward`).                                                               |
| `v`   | number | no       | `kV`: velocity feedforward. If left as `0` for the **drive** motor, the parser auto-derives it from the motor's free speed. |
| `a`   | number | no       | `kA`: acceleration feedforward.                                                                                             |

Both `drive` and `angle` are required top-level keys.

## `pidfproperties_sim.json`

Optional. If present, `SwerveParser` uses it instead of `pidfproperties.json` whenever `RobotBase.isSimulation()` is `true`. It has the same shape as above. Use it to tune simulation gains separately from your real robot's gains, without needing to swap files by hand.

{% hint style="warning" %}
There is no more `f` (feedforward) or `iz` (integral zone) field, and no more `output` min/max clamp object. Gains are now expressed as plain PID plus a `SimpleMotorFeedforward`-style `s`/`v`/`a`. See [Schema Changes](../../upgrading/schema-changes.md).
{% endhint %}

See [How to Tune PIDF Gains](../../tune-and-extend/tune-pidf-gains.md) for a tuning procedure and starting-point values.
