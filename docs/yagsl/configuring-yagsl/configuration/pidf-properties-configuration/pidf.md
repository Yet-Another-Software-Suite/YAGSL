# PIDF

## PIDF Configuration

The PIDF configurations map 1:1 with [`PIDFConfig`](https://yet-another-software-suite.github.io/YAGSL/javadocs/swervelib/parser/PIDFConfig.html) which stores information regarding the PID or PIDF configurations for the robot, such as module velocity & position, and robot heading. Not every parameter is used on every PIDF configuration. For example `heading` only takes into account the PID.

## Fields

A `0` in any place basically disables that portion of the PIDF.

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th>Required</th><th>Description</th></tr></thead><tbody><tr><td><code>p</code></td><td>kP Gain</td><td>Y</td><td>Proportional Gain for the PID.</td></tr><tr><td><code>i</code></td><td>kI Gain</td><td>Y</td><td>Integral Gain for the PID.</td></tr><tr><td><code>d</code></td><td>kD Gain</td><td>Y</td><td>Derivative Gain for the PID.</td></tr><tr><td><code>f</code></td><td>Number</td><td>N</td><td>Feedforward for the PID.</td></tr><tr><td><code>iz</code></td><td>Number</td><td>N</td><td>Integral zone for the integrator of the PID.</td></tr><tr><td><code>output</code></td><td><a href="pidf.md#range">Range</a></td><td>N</td><td>The output range for the PID.</td></tr></tbody></table>

#### Range

<table data-full-width="true"><thead><tr><th>Name</th><th>Units</th><th>Required</th><th>Description</th></tr></thead><tbody><tr><td><code>min</code></td><td>Number</td><td>N</td><td>The minimum value in the PID range. Defaults to <code>-1</code>.</td></tr><tr><td><code>max</code></td><td>Number</td><td>N</td><td>The maximum value in the PID range. Defaults to <code>1</code>.</td></tr></tbody></table>
