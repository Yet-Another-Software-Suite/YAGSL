---
description: >-
  What each YAGSL vendordep contains, pinning a release, offline installs, and
  the vendor library each device needs
---

# Vendordeps and offline install

YAGSL 2027 targets WPILib `2027_alpha7` (Java 25) on the SystemCore. It is published as **two** vendordeps, one per WPILib command framework. Install exactly one of them: each one declares a conflict with the other. For step-by-step instructions, see [Install YAGSL](../get-started/02-install-yagsl.md).

| Command framework | Vendordep name        | Vendordep URL                                       |
| ----------------- | --------------------- | --------------------------------------------------- |
| Commands v2       | `YAGSL (Commands v2)` | `https://cdn.yassrobotics.com/yagsl_commands2.json` |
| Commands v3       | `YAGSL (Commands v3)` | `https://cdn.yassrobotics.com/yagsl_commands3.json` |

{% hint style="warning" %}
The old `https://yet-another-software-suite.github.io/YAGSL/yagsl.json` URL is the 2026 vendordep. Do not use it for a 2027 project; remove `vendordeps/yagsl.json` if you are upgrading.
{% endhint %}

## Pin a release

The URLs above always install the latest release. To pin a project to one release, use the versioned URL instead, for example `https://cdn.yassrobotics.com/yagsl_commands2-2026.10.09.json`.

## Offline

Every [GitHub release](https://github.com/Yet-Another-Software-Suite/YAGSL/releases) has a `<version>-YAGSL-Offline.zip` asset (for example `2026.10.09-YAGSL-Offline.zip`). It contains a `maven` folder and a `vendordeps` folder that merge into your WPILib `2027_alpha7` home folder, which the WPILib installer creates:

| OS      | Extract into                                                                             |
| ------- | ---------------------------------------------------------------------------------------- |
| Windows | `C:\Users\Public\wpilib\2027_alpha7`                                                     |
| Linux   | `~/.local/share/wpilib/2027_alpha7`, or `$XDG_DATA_HOME/wpilib/2027_alpha7` if it is set |
| macOS   | `~/.wpilib/2027_alpha7`                                                                  |

{% tabs %}
{% tab title="Windows (PowerShell)" %}
```powershell
Expand-Archive -Path "$HOME\Downloads\2026.10.09-YAGSL-Offline.zip" -DestinationPath "C:\Users\Public\wpilib\2027_alpha7" -Force
```
{% endtab %}

{% tab title="Linux" %}
```bash
unzip -o ~/Downloads/2026.10.09-YAGSL-Offline.zip -d "${XDG_DATA_HOME:-$HOME/.local/share}/wpilib/2027_alpha7"
```
{% endtab %}

{% tab title="macOS" %}
```bash
unzip -o ~/Downloads/2026.10.09-YAGSL-Offline.zip -d ~/.wpilib/2027_alpha7
```
{% endtab %}
{% endtabs %}

Afterwards you should have `wpilib/2027_alpha7/maven/swervelib/...` and `wpilib/2027_alpha7/vendordeps/yagsl_commands2.json` / `yagsl_commands3.json`. Then run **WPILib: Manage Vendor Libraries → Install new libraries (offline)** and pick **YAGSL (Commands v2)** or **YAGSL (Commands v3)**.

{% hint style="info" %}
The offline zip only contains YAGSL. YAMS and any vendor libraries you need have to be installed offline separately.
{% endhint %}

## What gets installed

Both vendordeps resolve from the Maven repository at `https://cdn.yassrobotics.com/`, group `swervelib`:

| Vendordep              | Java artifacts                   | Packages                                   |
| ---------------------- | -------------------------------- | ------------------------------------------ |
| `yagsl_commands2.json` | `YAGSL-core` + `YAGSL-commands2` | `swervelib.core.*` + `swervelib.commands2` |
| `yagsl_commands3.json` | `YAGSL-core` + `YAGSL-commands3` | `swervelib.core.*` + `swervelib.commands3` |

These replace the single `YAGSL-java` artifact of the 2026 releases.

## Required vendor libraries

The only library YAGSL's vendordep requires is the matching **YAMS** vendordep. WPILib offers to install it when it's missing.

| YAGSL vendordep        | Requires                                           | YAMS in turn requires                                                    |
| ---------------------- | -------------------------------------------------- | ------------------------------------------------------------------------ |
| `yagsl_commands2.json` | `https://cdn.yassrobotics.com/yams_commands2.json` | WPILib Commands v2 (`CommandsV2.json`, from the WPILib vendor libraries) |
| `yagsl_commands3.json` | `https://cdn.yassrobotics.com/yams_commands3.json` | WPILib Commands v3 (`CommandsV3.json`, from the WPILib vendor libraries) |

## Optional vendor libraries

Hardware vendor libraries are **not** required by the vendordep anymore. YAGSL loads a vendor's device classes by reflection only when your JSON config uses one of that vendor's devices, so you install the libraries for the hardware you actually have:

| Vendor library | Install it if your config uses                                                                           |
| -------------- | -------------------------------------------------------------------------------------------------------- |
| REVLib         | `sparkmax_*`, `sparkflex_*`, `splineencoder_can`, or an `_attached` encoder on a Spark                   |
| CTRE Phoenix 6 | `talonfx_*`, `talonfxs_*`, `pigeon2_can`, `cancoder_can`                                                 |
| ReduxLib       | `canandgyro_can`, `canandmag_can`                                                                        |
| ThriftyLib     | `nova_*`, or an `_attached` encoder on a Nova                                                            |
| StudicaLib     | `navx3_can`, `navx3_usb`                                                                                 |
| _(none)_       | `systemcore_internal` (WPILib's built in `OnboardIMU`), and `_dio` / `_analog` encoders on SmartIO ports |

Phoenix 5 is no longer needed for anything.

{% hint style="danger" %}
If a config uses a device whose vendor library is missing, the project still builds, but robot code crashes with a missing class error when `SwerveParser.createSwerveDrive(...)` creates that device. Install the vendor library and redeploy.
{% endhint %}

{% hint style="warning" %}
**The AndyMark CAN Hex Bore encoder (`andymarkhexbore_can`) is not available in the 2027 builds yet.** AndyMark's library has no `2027_alpha7` release, so YAGSL 2027 is built without its device class. See [Absolute Encoders](hardware/absolute-encoders.md) for what to use instead.
{% endhint %}

Use the 2027 (`2027_alpha7`) release of each vendor library, installed from the vendor's own vendordep URL or the WPILib Vendor Dependencies panel. See the [WPILib guide to installing 3rd-party libraries](https://docs.wpilib.org/en/stable/docs/software/vscode-overview/3rd-party-libraries.html#installing-libraries).

You'll also want the vendor's own tuning/firmware tools installed on your development machine (not a robot-code dependency, but needed to configure/calibrate hardware):

* [REV Hardware Client 2](https://docs.revrobotics.com/rev-hardware-client-2)
* [CTRE Tuner X](https://v6.docs.ctr-electronics.com/en/latest/docs/installation/installation-frc.html)
* Studica Hardware Manager (NavX3)
