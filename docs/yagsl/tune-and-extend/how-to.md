# All guides

Guides for a robot that already drives with YAGSL. If you're setting up your first swerve robot, start with [Your first swerve robot](../get-started/tutorial.md). If something is wrong, start with [Find your problem](../fix-a-problem/fix-a-problem.md).

Code samples default to WPILib Commands v2 (`swervelib.commands2`, `yams.commands2`), with a Commands v3 tab or note wherever the API differs.

## Tune

* [Tune PIDF gains](tune-pidf-gains.md), including live tuning from a dashboard

## Drive autonomously

* [Drive to a pose](drive-to-a-pose.md): a single point-to-point PID move, no path planner needed
* [Set up PathPlanner](setup-pathplanner.md): register a YAGSL-built `SwerveDrive` with PathPlanner's `AutoBuilder` (Commands v2)

## Hardware

* [Use a custom gyro](use-a-custom-gyro.md): for a heading source YAGSL's parser doesn't build, supplied as a `Rotation3d`
* [Put devices on a different CAN bus](choose-a-can-bus.md): the `canbus` values for the SystemCore and Motioncore buses, and what each vendor accepts
* [Access raw hardware devices](access-raw-hardware-devices.md): get the actual vendor motor controller, encoder and gyro objects `SwerveParser` built

## Logging and dashboards

* [Set up AdvantageScope](set-up-advantagescope.md)
* [View your DataLog in AdvantageScope](view-your-datalog-in-advantagescope.md): open a recorded `.wpilog` after the fact, not live telemetry
