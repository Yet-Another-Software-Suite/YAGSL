# Module JSON

One file per swerve module (e.g. `frontleft.json`), referenced by name from `swervedrive.json`'s `modules` array. Parsed into `swervelib.core.parser.json.ModuleJson`.

```json
{
  "drive": {
    "type": "sparkmax_neo",
    "id": 1,
    "canbus": ""
  },
  "angle": {
    "type": "sparkmax_neo",
    "id": 2,
    "canbus": ""
  },
  "absoluteEncoder": {
    "type": "revthroughbore_attached",
    "id": 0,
    "channel": 0,
    "canbus": ""
  },
  "inverted": {
    "drive": false,
    "angle": false
  },
  "absoluteEncoderOffset": -45.0,
  "absoluteEncoderInverted": false,
  "location": {
    "front": 12.0,
    "left": 12.0
  }
}
```

| Field                      | Type                                        | Required             | Description                                                                                                                                                                                                                                                                                    |
| -------------------------- | ------------------------------------------- | -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `drive`                    | [Device](device-types.md#motor-controllers) | yes                  | Drive motor controller. See [Device type strings](device-types.md#motor-controllers).                                                                                                                                                                                                          |
| `angle`                    | [Device](device-types.md#motor-controllers) | yes                  | Angle/steering/azimuth motor controller.                                                                                                                                                                                                                                                       |
| `absoluteEncoder`          | [Device](device-types.md#absolute-encoders) | yes                  | Module's absolute encoder. See [Device type strings](device-types.md#absolute-encoders).                                                                                                                                                                                                       |
| `gearing`                  | object                                      | no                   | Per-module override of drive/angle gearing. If omitted, the module uses the defaults from `physicalproperties.json`. A drive override with only a `gearRatio` keeps the shared wheel diameter. Same shape as [physicalproperties.json's `gearing`](physicalproperties-json.md).                |
| `inverted.drive`           | boolean                                     | yes                  | Invert the drive motor.                                                                                                                                                                                                                                                                        |
| `inverted.angle`           | boolean                                     | yes                  | Invert the angle motor.                                                                                                                                                                                                                                                                        |
| `absoluteEncoderOffset`    | number (degrees)                            | yes                  | Offset so the wheel reads "forward" when physically pointed forward with the bevel gear to the left. Determined by rotating the wheel to face forward and reading the raw absolute encoder value.                                                                                              |
| `absoluteEncoderInverted`  | boolean                                     | no (default `false`) | Invert the absolute encoder's reading.                                                                                                                                                                                                                                                         |
| `absoluteEncoderGearRatio` | number                                      | no (default `1`)     | How many times the absolute encoder turns for each turn of the module, as the `X` in `X:1`: above 0 and up to 1, since an absolute encoder only reads one rotation. Leave it out when the encoder turns with the module. In the config generator, it is under a module's **Advanced** section. |
| `location.front`           | number (inches)                             | yes                  | Distance from the robot's center to the module's center, along the front/back axis.                                                                                                                                                                                                            |
| `location.left`            | number (inches)                             | yes                  | Distance from the robot's center to the module's center, along the left/right axis.                                                                                                                                                                                                            |

## `drive` / `angle` object

| Field    | Type   | Description                                                                                                                                                                                                                                                                |
| -------- | ------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`   | string | Controller+motor type string, see [Device type strings](device-types.md#motor-controllers).                                                                                                                                                                                |
| `id`     | number | CAN ID of the motor controller.                                                                                                                                                                                                                                            |
| `canbus` | string | CAN bus the controller is on: `""` for the SystemCore bus `can_s0`, or the bus number, such as `"1"` for `can_s1`. CTRE controllers also take a CANivore's name; REV and Thrifty controllers only take `""` or a number. See [CAN buses](./#control-system-and-can-buses). |

## `absoluteEncoder` object

| Field     | Type   | Description                                                                                                                                                                       |
| --------- | ------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`    | string | Encoder type string, see [Device type strings](device-types.md#absolute-encoders).                                                                                                |
| `id`      | number | CAN ID, if the encoder is on CAN. Ignored otherwise.                                                                                                                              |
| `channel` | number | SystemCore SmartIO channel, for `_dio` (duty cycle) and `_analog` encoders. Ignored otherwise.                                                                                    |
| `canbus`  | string | CAN bus, for `_can` encoders: `""` for `can_s0`, or the bus number. A CANcoder also takes a CANivore's name. See [CAN buses](./#control-system-and-can-buses). Ignored otherwise. |

The JSON Schema file requires all four fields (`type`, `id`, `channel`, `canbus`), so set the ones your encoder doesn't use to `0` / `""`.

An `_attached` encoder has no `id`, `channel` or `canbus` of its own: it is read through the module's `angle` motor controller, so it needs a SparkMAX, SparkFlex or Thrifty Nova angle motor. See [Device type strings](device-types.md#absolute-encoders) for which encoders each one accepts.

{% hint style="info" %}
There is no per-module `useCosineCompensator` toggle anymore. Cosine compensation is always applied by the parser. See [Module Behaviors](../../how-it-works/module-behaviors.md) for what it does.
{% endhint %}
