# swervedrive.json

Top-level drive configuration. Parsed into `swervelib.core.parser.json.SwerveDriveJson`.

```json
{
  "gyro": {
    "type": "pigeon2_can",
    "id": 0,
    "canbus": ""
  },
  "gyroAxis": "yaw",
  "gyroInvert": false,
  "modules": ["frontleft.json", "frontright.json", "backleft.json", "backright.json"]
}
```

| Field        | Type                           | Required             | Description                                                                                                                                                                    |
| ------------ | ------------------------------ | -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `gyro`       | [Device](device-types.md#gyro) | yes                  | The robot's gyroscope. See [Device type strings](device-types.md#gyro) for valid `type` values.                                                                                |
| `gyroAxis`   | string enum                    | no (default `"yaw"`) | Which axis of the gyro points up through the robot and is read as the heading: `yaw`, `pitch`, or `roll`. Passed to YAMS as `SwerveDriveConfig.withGyroHeadingAxis(GyroAxis)`. |
| `gyroInvert` | boolean                        | yes                  | Invert the gyroscope's heading reading. Passed to YAMS as `SwerveDriveConfig.withGyroInverted(boolean)`; the parser does not invert the gyro itself.                           |
| `modules`    | string\[]                      | yes                  | Filenames (relative to the `modules/` directory) of each module's JSON file, minimum 3, listed clockwise from front-left by convention.                                        |

## `gyro` object

| Field     | Type   | Description                                                                                                                                                                                                                                                         |
| --------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `type`    | string | Gyro type string, see [Device type strings](device-types.md#gyro). Set to `custom` to skip YAGSL's gyro setup and configure the gyro yourself; see [Custom Gyro](../hardware/gyroscopes.md#custom-gyro).                                                            |
| `id`      | number | CAN ID. Set to `0` if the gyro doesn't need one (`systemcore_internal`, `navx3_usb`, `custom`).                                                                                                                                                                     |
| `canbus`  | string | CAN bus the gyro is on: `""` for the SystemCore bus `can_s0`, or the bus number, such as `"1"` for `can_s1`. A Pigeon 2 also takes a CANivore's name. See [CAN buses](./#control-system-and-can-buses). Ignored by `systemcore_internal`, `navx3_usb` and `custom`. |
| `channel` | number | Optional, `navx3_usb` only: the SystemCore USB port the NavX3 is plugged into, `0` for USB0 to `3` for USB3. Defaults to `0`.                                                                                                                                       |

{% hint style="info" %}
When `gyro.type` is `custom`, `gyroAxis` and `gyroInvert` above are also ignored. The parser never calls `SwerveDriveConfig.withGyro()`/`withGyroHeadingAxis()`/`withGyroInverted()`, so you must call them yourself.
{% endhint %}
