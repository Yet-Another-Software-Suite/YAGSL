---
description: >-
  Known-good gearing values and inversions for common commercial-off-the-shelf
  swerve modules
---

# Standard Conversion Factors

These are known-good `gearRatio`/`diameter` values for common COTS swerve modules. Set them either as the drivetrain-wide default in `modules/physicalproperties.json`, or as a per-module override in a module's own `gearing` field (module-level `gearing` wins if both are present):

```json
"gearing": {
  "drive": { "gearRatio": 6.75, "diameter": 4 },
  "angle": { "gearRatio": 12.8 }
}
```

* `drive.gearRatio`: motor revolutions per wheel revolution, as `X` where the ratio is `X:1`.
* `drive.diameter`: wheel diameter in **inches**.
* `angle.gearRatio`: motor revolutions per full azimuth revolution, as `X:1`.

The config generator at [config.yagsl.com](https://config.yagsl.com) has the SDS, REV, Thrifty Bot and WCP modules below as presets, and keeps them up to date. On its **Properties** tab, pick your **Module Type** and **Module Configuration**: it fills in all three values, and sets every module's [inversions](standard-conversion-factors.md#inversions) too. The values are listed here for reference and for teams hand-editing JSON.

Ratios are given to four decimal places. Where a module's gear train is published, the ratio is worked out from its tooth counts, so it can differ from the vendor's rounded value in the last places (MK4 L2 is 6.746, sold as 6.75). Wheel diameters are nominal: measure your own (see [Deriving your own](standard-conversion-factors.md#deriving-your-own)).

## Swerve Drive Specialties (SDS)

| Module   | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| -------- | ----------------- | ----------------- | ---------------- |
| MK4 L1   | 12.8              | 8.1429            | 4                |
| MK4 L2   | 12.8              | 6.746             | 4                |
| MK4 L3   | 12.8              | 6.1224            | 4                |
| MK4 L4   | 12.8              | 5.1429            | 4                |
| MK4i L1  | 21.4286 (150/7)   | 8.1429            | 4                |
| MK4i L2  | 21.4286 (150/7)   | 6.746             | 4                |
| MK4i L3  | 21.4286 (150/7)   | 6.1224            | 4                |
| MK4n L1+ | 18.75             | 7.125             | 4                |
| MK4n L2+ | 18.75             | 5.9028            | 4                |
| MK4n L3+ | 18.75             | 5.3571            | 4                |
| MK4c L1+ | 12.8              | 7.125             | 4                |
| MK4c L2+ | 12.8              | 5.9028            | 4                |
| MK4c L3+ | 12.8              | 5.3571            | 4                |
| MK5i R1  | 26                | 7.03              | 4                |
| MK5i R2  | 26                | 6.03              | 4                |
| MK5i R3  | 26                | 5.27              | 4                |
| MK5n R1  | 26.0909 (287/11)  | 7.03              | 4                |
| MK5n R2  | 26.0909 (287/11)  | 6.03              | 4                |
| MK5n R3  | 26.0909 (287/11)  | 5.27              | 4                |

## REV MAXSwerve

From REV's speed ratio chart: the drive reduction is the spur gear over the pinion, times the 45:15 bevel.

| Option       | Pinion | Spur gear | `angle.gearRatio`  | `drive.gearRatio` | `drive.diameter` |
| ------------ | ------ | --------- | ------------------ | ----------------- | ---------------- |
| Low          | 12T    | 22T       | 46.4236 (9424/203) | 5.5               | 3                |
| Medium       | 13T    | 22T       | 46.4236 (9424/203) | 5.0769            | 3                |
| High         | 14T    | 22T       | 46.4236 (9424/203) | 4.7143            | 3                |
| Extra High 1 | 14T    | 21T       | 46.4236 (9424/203) | 4.5               | 3                |
| Extra High 2 | 14T    | 20T       | 46.4236 (9424/203) | 4.2857            | 3                |
| Extra High 3 | 15T    | 20T       | 46.4236 (9424/203) | 4                 | 3                |
| Extra High 4 | 16T    | 20T       | 46.4236 (9424/203) | 3.75              | 3                |
| Extra High 5 | 16T    | 19T       | 46.4236 (9424/203) | 3.5625            | 3                |

## REV EasySwerve

One drive gearing, from REV's EasySwerve docs. The wheel is the 4in Traction Wheel it ships with, or the optional 4in Spiky Wheel, which measures 3.66in.

| Wheel          | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| -------------- | ----------------- | ----------------- | ---------------- |
| Traction Wheel | 20                | 6.3               | 4                |
| Spiky Wheel    | 20                | 6.3               | 3.66             |

## Thrifty Swerve

Six ratios from three pinions and two bevel shaft gears, worked out from the gear train in Thrifty's bill of materials: 36 over the pinion, times the bevel shaft gear over 24, times the 45:15 bevel. Thrifty doesn't publish a ratio table, so check the gears on your module.

| Pinion | Bevel shaft gear | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| ------ | ---------------- | ----------------- | ----------------- | ---------------- |
| 12T    | 18T              | 25                | 6.75              | 4                |
| 12T    | 16T              | 25                | 6                 | 4                |
| 13T    | 18T              | 25                | 6.2308            | 4                |
| 13T    | 16T              | 25                | 5.5385            | 4                |
| 14T    | 18T              | 25                | 5.7857            | 4                |
| 14T    | 16T              | 25                | 5.1429            | 4                |

## West Coast Products (WCP)

From WCP's ratio tables. Each ratio is named by its ratio set and pinion, so X2-11 is the X2 ratio set with the 11T pinion.

### Swerve X2, X2i, X2t and X2c

| Ratio | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| ----- | ----------------- | ----------------- | ---------------- |
| X1-10 | 12.1              | 7.6737            | 4                |
| X1-11 | 12.1              | 6.9761            | 4                |
| X1-12 | 12.1              | 6.3947            | 4                |
| X2-10 | 12.1              | 6.8211            | 4                |
| X2-11 | 12.1              | 6.201             | 4                |
| X2-12 | 12.1              | 5.6842            | 4                |
| X3-10 | 12.1              | 6.48              | 4                |
| X3-11 | 12.1              | 5.8909            | 4                |
| X3-12 | 12.1              | 5.4               | 4                |
| X4-10 | 12.1              | 5.67              | 4                |
| X4-11 | 12.1              | 5.1545            | 4                |
| X4-12 | 12.1              | 4.725             | 4                |

### Swerve X2S and X2St

| Ratio | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| ----- | ----------------- | ----------------- | ---------------- |
| X1-15 | 25.9              | 6                 | 3.5              |
| X1-16 | 25.9              | 5.625             | 3.5              |
| X1-17 | 25.9              | 5.2941            | 3.5              |
| X2-17 | 25.9              | 4.9412            | 3.5              |
| X2-18 | 25.9              | 4.6667            | 3.5              |
| X2-19 | 25.9              | 4.4211            | 3.5              |
| X3-19 | 25.9              | 4.1053            | 3.5              |
| X3-20 | 25.9              | 3.9               | 3.5              |
| X3-21 | 25.9              | 3.7143            | 3.5              |

### Swerve X

| Ratio | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| ----- | ----------------- | ----------------- | ---------------- |
| X1-10 | 11.3143 (396/35)  | 7.8462            | 4                |
| X1-11 | 11.3143 (396/35)  | 7.1329            | 4                |
| X1-12 | 11.3143 (396/35)  | 6.5385            | 4                |
| X2-10 | 11.3143 (396/35)  | 6.5571            | 4                |
| X2-11 | 11.3143 (396/35)  | 5.961             | 4                |
| X2-12 | 11.3143 (396/35)  | 5.4643            | 4                |
| X3-12 | 11.3143 (396/35)  | 5.1429            | 4                |
| X3-13 | 11.3143 (396/35)  | 4.7473            | 4                |
| X3-14 | 11.3143 (396/35)  | 4.4082            | 4                |

### Swerve X Flipped

| Ratio | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| ----- | ----------------- | ----------------- | ---------------- |
| X1-10 | 13.3714 (468/35)  | 8.1               | 4                |
| X1-11 | 13.3714 (468/35)  | 7.3636            | 4                |
| X1-12 | 13.3714 (468/35)  | 6.75              | 4                |
| X2-10 | 13.3714 (468/35)  | 6.72              | 4                |
| X2-11 | 13.3714 (468/35)  | 6.1091            | 4                |
| X2-12 | 13.3714 (468/35)  | 5.6               | 4                |
| X3-10 | 13.3714 (468/35)  | 5.5125            | 4                |
| X3-11 | 13.3714 (468/35)  | 5.0114            | 4                |
| X3-12 | 13.3714 (468/35)  | 4.5938            | 4                |

### Swerve XS

| Ratio | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| ----- | ----------------- | ----------------- | ---------------- |
| X1-12 | 41.25 (165/4)     | 6                 | 3                |
| X1-13 | 41.25 (165/4)     | 5.5385            | 3                |
| X1-14 | 41.25 (165/4)     | 5.1429            | 3                |
| X2-14 | 41.25 (165/4)     | 4.7143            | 3                |
| X2-15 | 41.25 (165/4)     | 4.4               | 3                |
| X2-16 | 41.25 (165/4)     | 4.125             | 3                |

## Plummer Industries

Not a preset in the config generator.

| Configuration      | `angle.gearRatio` | `drive.gearRatio` | `drive.diameter` |
| ------------------ | ----------------- | ----------------- | ---------------- |
| Corner, Mid Ratio  | 28                | 4                 | 2.5              |
| Corner, High Ratio | 28                | 3.25              | 2.5              |

## Inversions

Picking a module type in the config generator also sets these inversions on every module. They come from the vendors' own code and from what most teams' CTRE swerve generator files use. They assume the wheels are lined up as YAGSL lines them up: every wheel facing forward with its bevel gear on the robot's left. EasySwerve's depend on whether its motors are on the bottom or the top of the module.

In a module's JSON file, "Angle motor" is `inverted.angle`, "Drive motor" is `inverted.drive`, and "Absolute encoder" is `absoluteEncoderInverted`.

| Vendor      | Module                                     | Angle motor | Absolute encoder | Drive motor |
| ----------- | ------------------------------------------ | ----------- | ---------------- | ----------- |
| SDS         | MK4                                        | —           | —                | Inverted    |
| SDS         | MK4i                                       | Inverted    | —                | Inverted    |
| SDS         | MK4n                                       | Inverted    | —                | Inverted    |
| SDS         | MK4c                                       | —           | —                | Inverted    |
| SDS         | MK5i                                       | —           | —                | Inverted    |
| SDS         | MK5n                                       | —           | —                | Inverted    |
| REV         | MAXSwerve                                  | —           | Inverted         | —           |
| REV         | EasySwerve (Motors on bottom)              | —           | —                | Inverted    |
| REV         | EasySwerve (Motors on top)                 | Inverted    | —                | —           |
| REV         | EasySwerve, Spiky Wheel (Motors on bottom) | —           | —                | Inverted    |
| REV         | EasySwerve, Spiky Wheel (Motors on top)    | Inverted    | —                | —           |
| Thrifty Bot | Thrifty Swerve                             | —           | —                | Inverted    |
| WCP         | Swerve X2 (Standard)                       | —           | —                | Inverted    |
| WCP         | Swerve X2i / X2c                           | Inverted    | —                | Inverted    |
| WCP         | Swerve X2t                                 | —           | —                | Inverted    |
| WCP         | Swerve X2S / X2St                          | —           | Inverted         | Inverted    |
| WCP         | Swerve X                                   | Inverted    | —                | Inverted    |
| WCP         | Swerve X Flipped                           | Inverted    | —                | Inverted    |
| WCP         | Swerve XS                                  | —           | Inverted         | —           |

They're a starting point, not a guarantee: check every module with [Determine inversion](../fix-a-problem/determine-inversion.md) before you drive.

## Deriving your own

If your module isn't listed, compute the reduction from the module's datasheet: `gearRatio` is motor revolutions per output revolution (drive → wheel, angle → azimuth), and `diameter` is the wheel diameter in inches, measured on a slightly-worn tread (a fresh, un-worn wheel measures larger than it will after a few matches, and using the worn number keeps odometry accurate longer). Overestimating `diameter` makes the robot think it's traveled less than it has, so odometry-driven autonomous routines undershoot; verify against a real drive-straight test as described in [Verify Module Locations](../fix-a-problem/verify-module-locations.md).
