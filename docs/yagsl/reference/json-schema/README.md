# JSON Configuration Schema

YAGSL builds a YAMS `SwerveDrive` from a directory of JSON files. `SwerveParser` (`swervelib.commands2.SwerveParser` or `swervelib.commands3.SwerveParser`, both built on `swervelib.core.parser.SwerveParser`) reads this directory and constructs the equivalent YAMS `SwerveDriveConfig` / `SwerveModuleConfig` / `SmartMotorControllerConfig` objects for you. You rarely need to hand-edit these files, since [config.yagsl.com](https://config.yagsl.com) generates them for you, but knowing the schema is useful for debugging, scripting, or hand-tweaking a single value.

{% hint style="info" %}
If you're setting up a robot for the first time, use the [Tutorial](../../get-started/tutorial.md) section instead. This is reference material for looking up exact field names and types.
{% endhint %}

## Directory layout

```
src/main/deploy
└── swerve
    └── base                       <- name is arbitrary, passed to `SwerveParser.parse(...)`
        ├── swervedrive.json
        └── modules
            ├── frontleft.json
            ├── frontright.json
            ├── backleft.json
            ├── backright.json     <- module filenames are arbitrary, referenced by swervedrive.json
            ├── physicalproperties.json
            ├── pidfproperties.json
            └── pidfproperties_sim.json   (optional)
```

`SwerveParser.parse(directory)` reads, in order:

1. `swervedrive.json`: top-level drive config, the gyro plus the list of module files.
2. `modules/pidfproperties.json`: default PID + feedforward gains shared by every module. If `modules/pidfproperties_sim.json` also exists, it's used instead whenever `RobotBase.isSimulation()` is `true`, so you can tune sim and real gains independently.
3. `modules/physicalproperties.json`: default gearing and current limits shared by every module.
4. Each file listed in `swervedrive.json`'s `modules` array: one per swerve module, describing that module's hardware, wiring, and location. A module file may override the gearing from `physicalproperties.json` with its own `gearing` object.

The JSON is read with WPILib's JSON library (avaje-jsonb), using adapters generated at compile time for the classes in `swervelib.core.parser.json`. Fields a class doesn't have (for example a `$schema` key, or a field left over from an older config) are ignored rather than rejected, and fields missing from a file keep the default listed on each page.

{% hint style="warning" %}
Device `type` strings are **not** checked while the files are read. An invalid or unsupported `type` only fails later, when `SwerveParser.createSwerveDrive(...)` builds that device.
{% endhint %}

## Pages in this section

* [swervedrive.json](swervedrive-json.md)
* [module json (per-module file)](module-json.md)
* [physicalproperties.json](physicalproperties-json.md)
* [pidfproperties.json / pidfproperties\_sim.json](pidfproperties-json.md)
* [Device type strings](device-types.md): every valid `type` value for gyros, motor controllers, and absolute encoders.

{% hint style="warning" %}
Moving a 2026 config to YAGSL 2027, or coming from a pre-2026.8.05 `swerve/` directory (`imu`, `encoder`, `conversionFactors`, `controllerproperties.json`, PIDF `f`/`iz`)? See [Schema Changes](../../upgrading/schema-changes.md) for both migration guides.
{% endhint %}

## Control system and CAN buses

YAGSL 2027 runs on the SystemCore, which has several CAN buses of its own (`can_s0` to `can_s4`) and more on an attached Motioncore (`can_d0` to `can_d19`). Every device's `canbus` field is read the same way for every vendor, by `swervelib.core.parser.CANBuses`:

| `canbus` value                                      | CAN bus                                                                |
| --------------------------------------------------- | ---------------------------------------------------------------------- |
| `""` (empty or missing)                             | SystemCore `can_s0`                                                    |
| `"0"` to `"4"`                                      | SystemCore `can_s0` to `can_s4`, so `"1"` is `can_s1`                  |
| `"5"` to `"24"`                                     | Motioncore `can_d0` to `can_d19`, so `"5"` is `can_d0`                 |
| `"CAN_S0"` to `"CAN_S4"`, `"CAN_D0"` to `"CAN_D19"` | The same buses by their `CANPort` name, so `"CAN_S1"` is `can_s1`      |
| any other number                                    | Rejected with `CAN bus <n> does not exist`                             |
| anything else                                       | Passed to the vendor as is, such as a CANivore's name for CTRE devices |

The bus numbers and names follow WPILib's `CANPort` enum, and a name has to be spelled exactly as the enum names it: `"can_s1"` isn't a `CANPort` name, so it counts as anything else. Whitespace around the value is trimmed, and `canbus` is always a JSON **string** (`"1"`, not `1`). For a step by step guide, see [Put devices on a different CAN bus](../../tune-and-extend/choose-a-can-bus.md).

What each vendor does with a value that is not a bus number or `CANPort` name:

| Vendor                                                          | Non-numeric `canbus`                                                             |
| --------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| CTRE (`talonfx_*`, `talonfxs_*`, `pigeon2_can`, `cancoder_can`) | Used as the Phoenix 6 `CANBus` name, such as a CANivore's name or serial number. |
| Redux (`canandgyro_can`, `canandmag_can`)                       | Used as a ReduxLib bus string, such as `"socketcan:can_s1"`.                     |
| REV (`sparkmax_*`, `sparkflex_*`, `splineencoder_can`)          | Rejected. REV devices only accept `""`, a bus number or its `CANPort` name.      |
| ThriftyBot (`nova_*`)                                           | Rejected. Thrifty devices only accept `""`, a bus number or its `CANPort` name.  |

`canbus` is ignored for devices that aren't on CAN (`systemcore_internal`, `custom`, and `_dio`, `_analog`, and `_attached` absolute encoders). It is currently also ignored by `navx3_can` and `andymarkhexbore_can`, which are always created on their vendor library's default bus.

{% hint style="warning" %}
**Changed in 2027:** a CTRE device with an empty `canbus` is now on `can_s0`, like every other vendor. Before the `canbus` values were unified in `2026.10.09` it ended up on `can_s2`, so a CTRE device wired to `can_s2` now needs `"canbus": "2"`. REV devices no longer accept a port name such as `"can_s1"`; use the bus number `"1"` or the `CANPort` name `"CAN_S1"`.
{% endhint %}
