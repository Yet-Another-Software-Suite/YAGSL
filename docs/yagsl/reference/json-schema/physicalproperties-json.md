# physicalproperties.json

Default gearing and current limits shared by every module, unless a module's own JSON file overrides `gearing`. Parsed into `swervelib.core.parser.json.PhysicalPropertiesJson`.

```json
{
  "gearing": {
    "drive": {
      "gearRatio": 6.75,
      "diameter": 4.0
    },
    "angle": {
      "gearRatio": 12.8
    }
  },
  "statorCurrentLimit": {
    "drive": 40,
    "angle": 20
  }
}
```

| Field                      | Type                | Required          | Description                                          |
| -------------------------- | ------------------- | ----------------- | ---------------------------------------------------- |
| `gearing.drive.gearRatio`  | number              | yes               | Drive motor reduction, as the `X` in `X:1`.          |
| `gearing.drive.diameter`   | number (inches)     | yes               | Wheel diameter.                                      |
| `gearing.angle.gearRatio`  | number              | yes               | Angle/steering motor reduction, as the `X` in `X:1`. |
| `statorCurrentLimit.drive` | whole number (amps) | no (default `40`) | Drive motor stator current limit.                    |
| `statorCurrentLimit.angle` | whole number (amps) | no (default `20`) | Angle motor stator current limit.                    |

See [Standard Conversion Factors](../standard-conversion-factors.md) for gear ratios/wheel diameters of common COTS swerve modules.

{% hint style="warning" %}
Several fields that existed before 2026.8.05 are gone: `friction`, `steerRotationalInertia`, `robotMass`, `rampRate`, `wheelGripCoefficientOfFriction`, `optimalVoltage`. See [Schema Changes](../../upgrading/schema-changes.md) for why.
{% endhint %}
