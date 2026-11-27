# Our Features

## Documentation

{% embed url="https://yet-another-software-suite.github.io/YAGSL/javadocs/" %}
Javadocs for YAGSL
{% endembed %}

{% embed url="https://yet-another-software-suite.github.io/YAGSL/config_generator/" %}
Tuning webpage to create configurations
{% endembed %}

{% embed url="https://github.com/Yet-Another-Software-Suite/YAGSL" %}
Example code repository
{% endembed %}

## Easy installation

Just download all of the vendordeps from WPILib online from this list: [#vendor-urls](../../configuring-yagsl/dependency-installation.md#vendor-urls "mention") following the process at:

{% embed url="https://docs.wpilib.org/en/stable/docs/software/vscode-overview/3rd-party-libraries.html#rd-party-libraries" %}

## Hardware Support

### Motors

* [Neo](https://www.revrobotics.com/rev-21-1650/)
* [Falcon 500](https://store.ctr-electronics.com/falcon-500-powered-by-talon-fx/) / [Kraken X60](https://wcproducts.com/products/kraken)
* [Brushed Motors Controlled by SparkMAX](https://www.andymark.com/products/rs775-5-motor-with-encoder-for-pg71-pg188-gearbox) with [an](https://www.mouser.com/ProductDetail/Grayhill/63R256) [attached](https://store.ctr-electronics.com/srx-mag-encoder/) [encoder](https://www.revrobotics.com/rev-11-1271/). Angle motors do not require quadrature encoders and should use duty cycle encoders attached to the dataport of the spark max.

### Absolute Encoders

* [Thrifty Absolute Encoders](https://www.thethriftybot.com/bearings/Thrifty-Absolute-Magnetic-Encoder-p421607500)
* [CANCoder](https://store.ctr-electronics.com/cancoder/)
* [REV Through Bore](https://www.revrobotics.com/rev-11-1271/)
* [Canandcoder (via SparkMAX)](https://docs.reduxrobotics.com/canandcoder/spark-max#using-the-pwm-output-with-spark-max)
* [Canandcover (via CAN)](https://docs.reduxrobotics.com/canandcoder/getting-started)
* [Throughbore](https://www.revrobotics.com/rev-11-1271/) (via PWM)
* [Thrifty Absolute Magnetic Encoder](https://www.thethriftybot.com/products/thrifty-absolute-magnetic-encoder)  (via AnalogInput)
* [MA3](https://www.andymark.com/products/ma3-absolute-encoder-with-cable)&#x20;
* [SRX Mag](https://store.ctr-electronics.com/srx-mag-encoder/)
* [AM Mag](https://www.andymark.com/products/am-mag-encoder)
* Any PWM Absolute Encoder!
* Any Anolog Absolute Encoder

### IMUs (Gyroscopes)

#### **ALL GYROSCOPES MUST BE COUNTER-CLOCKWISE POSITIVE!**

* [Pigeon IMU](https://store.ctr-electronics.com/pigeon-2/)
* [Pigeon 2 IMU](https://store.ctr-electronics.com/pigeon-2/)
* [NavX](https://www.studica.com/nav2-mxp-robotics-navigation-sensor)
* [NavX 2](https://www.studica.com/nav2-mxp-robotics-navigation-sensor)
* [NavX Micro](https://www.studica.com/navx-2-micro-9-axis-inertialmagnetic-sensor)
* [NavX 2 Micro](https://www.studica.com/navx-2-micro-9-axis-inertialmagnetic-sensor)
* [ADIS16448](https://wiki.analog.com/first/adis16448\_imu\_frc)
* [ADIS16470](https://wiki.analog.com/first/adis16470\_imu\_frc)
* Any analog gyroscope

## Simulation

* There is full simulation support out of the box in `YAGSL/examples`.

## Control

* PathPlanner is supported and there is a example in `YAGSL/examples`
* Control can be based entirely off of desired angle compared to current angle (`SwerveController.getTargetSpeeds`) or passed directly to the `SwerveDrive` in the correct units.
* The function `SwerveDrive.lockPose` moves all of the wheels to face inwards making the robot nearly impossible to move.
* Drive motors can be set to coast or brake using the function `SwerveDrive.setMotorIdleMode`
* Swerve Module drive motor feedforwards can be replaced using the function `SwerveDrive.replaceSwerveModuleFeedforward`
* Slew rate limiters can be added to `SwerveController.getTargetSpeeds` with `SwerveController.addSlewRateLimiters` to improve control of the robot.
* Momentum calculator using objects in space represented by `Matter` class to limit velocity of the robot and prevent tipping.
* CAN frames are limited to updated angles and velocities which differ from the previous angle and velocity.
* Ability to overwrite maximum speeds via `SwerveDrive.setMaximumSpeed` and `SwerveController.setMaximumAngularVelocity` or utility functions `SwerveDrive.setMaximumSpeeds`.
* Ability to use Chassis Velocity Correction using `SwerveDrive.chassisVelocityCorrection` only affecting the `SwerveDrive.drive` functions.
* Ability to control using different center of rotation with `SwerveDrive.drive`.
* Push the offsets to the motor controllers via `SwerveDrive.pushOffsetsToControllers`
* Control over `ChassisSpeeds.discretize` via `SwerveDrive.setChassisSpeedsDisctretization`
* Cosine compensation via `SwerveDrive.setCosineCompensator`
* Heading correction with `SwerveDrive.setHeadingCorrection`
* Auto-centering modules allows for modules to center to 0 whenever no input is given. This can be controlled by `SwerveDrive.setAutoCenteringModules`

## Safety Features

* The absolute encoder readings fall back to the relative encoder readings if the absolute encoder encountered a resolvable reading error.
* Angle motors are default in coast mode to help take care of the motors.
* Motor angle's are optimized to turn to the closest equivalent angle.
* You can limit your velocity given your robot weight and center of gravity in `SwerveMath.limitVelocity`.
* Current limits in the JSON configuration.
* Ramp rate limits in the JSON configuration.
* Closed-loop PID on motor controllers exclusively for SparkMAX's and TalonFX's.
* The absolute encoders and relative encoders are synchronized when the robot is not moving for 100ms.
* PID inputs are continuous or emulated to be continuous from -180 to 180.

## Odometry

* `SwerveDrive.updateOdometry` is called every 20ms, the period can be changed via `SwerveDrive.setOdometryPeriod`.
* To stop the odometry thread use `SwerveDrive.stopOdometryThread` and update odometry in a periodic.
* To zero the gyroscope call `SwerveDrive.zeroGyro`
* Robot centric velocity can be fetched via `SwerveDrive.getRobotVelocity` and field-centric is `SwerveDrive.getFieldVelocity`.
* Robot pose can be fetched via `SwerveDrive.getPose`
* Robot gyroscope readings can be fetched via `SwerveDrive.getGyroRotation3d` or `SwerveDrive.getYaw`, `SwerveDrive.getPitch`, `SwerveDrive.getRoll`.
* Robot pose can be updated with vision inputs through `SwerveDrive.addVisionMeasurement` optional standard deviation pass through.
* Gyroscope offset can be configured via `SwerveDrive.setGyroOffset` and should be used for pathplanner.
* If your translational odometry is off but controls are correct you can invert you translational odometry with the attribute `SwerveDrive.invertOdometry`

## Telemetry

* Swerve telemetry is updated to work with [frc-web-components](https://github.com/frc-web-components/frc-web-components/tree/version4) [app](https://github.com/frc-web-components/app).
* Every module angle is reported for both relative and absolute encoders.
* Every module velocity is reported.
* The desired chassis speed is reported.
* There is a Field2d which is created and updated continuously to represent the robots current position and orientation on the field.
* You can add trajectories to the field with `SwerveDrive.postTrajectory` when testing.

