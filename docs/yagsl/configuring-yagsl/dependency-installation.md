# Dependency Installation

## WPILib

{% embed url="https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/wpilib-setup.html" %}

## REV Hardware Client 2

{% embed url="https://docs.revrobotics.com/rev-hardware-client-2" %}

## CTRE Tuner X

{% embed url="https://v6.docs.ctr-electronics.com/en/latest/docs/installation/installation-frc.html" %}

## Vendor URLs

Bellow is a list of URL's that are required to be installed for YAGSL to work properly, these are publicised on the vendor websites and over YAGSL documentation. To install them follow this guide on WPILib docs.

{% embed url="https://docs.wpilib.org/en/stable/docs/software/vscode-overview/3rd-party-libraries.html#installing-libraries" %}



<table data-full-width="true"><thead><tr><th width="177">Vendor</th><th width="597">URL</th><th>Offline Installation</th></tr></thead><tbody><tr><td>maple-sim</td><td><code>https://shenzhen-robotics-alliance.github.io/maple-sim/vendordep/maple-sim.json</code></td><td></td></tr><tr><td>CTRE Phoenix 6</td><td><code>https://maven.ctr-electronics.com/release/com/ctre/phoenix6/latest/Phoenix6-replay-frc2026-latest.json</code></td><td><a href="https://v6.docs.ctr-electronics.com/en/latest/docs/installation/installation-frc.html">Tuner X</a></td></tr><tr><td>CTRE Phoenix 5</td><td><code>https://maven.ctr-electronics.com/release/com/ctre/phoenix/Phoenix5-replay-frc2026-latest.json</code></td><td><a href="https://store.ctr-electronics.com/software/">Phoenix Tuner</a></td></tr><tr><td>REVLib</td><td><code>https://software-metadata.revrobotics.com/REVLib-2026.json</code></td><td><a href="https://github.com/REVrobotics/REV-Software-Binaries/releases/tag/rhc-1.6.2">REV Hardware Client</a> Offline</td></tr><tr><td>Studica (NavX)</td><td><code>https://dev.studica.com/maven/release/2026/json/Studica-2026.0.0.json</code></td><td><a href="https://www.studica.ca/en/navx-2-mxp-robotics-navigation-sensor"><code>https://www.studica.ca/en/navx-2-mxp-robotics-navigation-sensor</code></a></td></tr><tr><td>ReduxLib</td><td><code>https://frcsdk.reduxrobotics.com/ReduxLib_2026.json</code></td><td><code>unavailable</code></td></tr><tr><td>YAGSL</td><td><code>https://yet-another-software-suite.github.io/YAGSL/yagsl.json</code></td><td><code>YAGSL</code></td></tr></tbody></table>

All of these vendordeps must be installed for YAGSL to work due to the generic nature of YAGSL even if you do not use any of the products the vendor library supports.
