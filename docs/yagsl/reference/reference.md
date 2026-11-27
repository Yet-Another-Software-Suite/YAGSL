# All reference pages

Lookup material: exact JSON field names and types, supported hardware, and the YAMS API YAGSL hands you. Come here when you know what you're doing and need a precise answer. For step-by-step guidance, see [Your first swerve robot](../get-started/tutorial.md), [Find your problem](../fix-a-problem/fix-a-problem.md) or [Tune and extend](../tune-and-extend/how-to.md).

* [JSON Configuration Schema](json-schema/): every field in `swervedrive.json`, module files, `physicalproperties.json`, and `pidfproperties.json`.
* [Supported Hardware](hardware/): gyro, motor controller, and absolute encoder type tables plus per-device notes.
* [Standard Conversion Factors](standard-conversion-factors.md): gear ratios/wheel diameters for common COTS swerve modules.
* [Common build issues](../fix-a-problem/common-build-issues.md) and [Common configuration issues](../fix-a-problem/common-configuration-issues.md): checklists of what's commonly built or configured wrong on a swerve drive.
* [Vendordeps and offline install](vendordep-installation.md): what each YAGSL vendordep contains, pinning a release, offline installs, and which vendor library each device type needs.
* [API Reference](api-reference.md): the `swervelib` packages, the `SwerveParser` entry points, and where to find docs for `SwerveDrive`, `SwerveModule`, and friends (they're YAMS classes).

Upgrading from an older YAGSL? See [Upgrade a 2026 project to YAGSL 2027](../upgrading/upgrade-from-2026.md) and the [Changelog](../upgrading/changelog.md).
