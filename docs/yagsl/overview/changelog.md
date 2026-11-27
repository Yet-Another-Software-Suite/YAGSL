# Changelog

## Pull Requests are always reviewed!

I highly encourage anyone who wants to help make YAGSL better to create pull requests with any modifications you have made that increases your quality of life.

{% embed url="https://github.com/BroncBotz3481/YAGSL-Example/pulls?q=is%3Apr+is%3Aclosed" %}

{% embed url="https://github.com/Yet-Another-Software-Suite/YAGSL/pulls?q=is%3Apr+is%3Aclosed" %}

## Contributing

YAGSL development is done on the `dev` branch of the YAGSL repository here at `yagsl/java/swervelib`.

{% embed url="https://github.com/BroncBotz3481/YAGSL-Example/tree/dev" %}
YAGSL-Example dev branch
{% endembed %}

All PR's should be based off of and merged into here. YAGSL is propagated often to the other repositories.

## 2025.8.0

* Fix attached absolute encoders with brushed SparkMAX's

## 2025.7.2

* fix setAbsoluteEncoderOffset() by [@MEisSCAMMER](https://github.com/MEisSCAMMER) in [#338](https://github.com/BroncBotz3481/YAGSL-Example/pull/338)
* Enable Continuous Input for omegaPIDController by [@Sate04](https://github.com/Sate04) in [#337](https://github.com/BroncBotz3481/YAGSL-Example/pull/337)
* Patched crash when starting while enabled.

## 2025.7.1

* Fixed `SwerveInputStream.allianceRelativeControl`
* Prevent crashing midmatch if robot code restarts.
* Added `SwerveInputStream.translationHeadingOffset`
* Removed gyro zeroing from YAGSL startup. You must now call gyro zero on `autonomousInit` or use the `RobotModeTriggers`
* `Added RobotModeTriggers.autonomous().onTrue(Commands.runOnce(this::zeroGyroWithAlliance));`to the example code.

## 2025.7.0

* [ ] Made the old method for conversion factors throw an exception instead of throwing a warning.
* [ ] Added TalonFXS Support.
* [ ] Renamed <kbd>SwerveMotor.isAttachedAbsoluteEncoder</kbd> to <kbd>SwerveMotor.usingExternalFeedbackSensor</kbd>.
* [ ] Deprecated <kbd>SwerveDrive.pushOffsetsToEncoders</kbd> in favor of <kbd>SwerveDrive.useExternalFeedbackSensor</kbd>.
* [ ] Deprecated <kbd>SwerveDrive.restoreInternalOffset</kbd> in favor of <kbd>SwerveDrive.useInternalFeedbackSensor</kbd>.
* [ ] Updated references from Shuffleboard to any dashboard by [@DanPeled](https://github.com/DanPeled)
* [ ] Fixed Absolute Encoders attached to SparkFlex's.
* [ ] Attached absolute encoders are no longer set as the feedback device, must be explicitly called.
* [ ] Added `SwerveInputStream.driveToPose`
* [ ] Add optional optimization of module states in drive function that uses feedforward by [@clrozeboom](https://github.com/clrozeboom) in [#314](https://github.com/BroncBotz3481/YAGSL-Example/pull/314)
* [ ] Make all closable items implement `AutoClosable` by [@kytpbs](https://github.com/kytpbs) in [#317](https://github.com/BroncBotz3481/YAGSL-Example/pull/317)
* [ ] remove useless imports and variables by [@kytpbs](https://github.com/kytpbs) in [#318](https://github.com/BroncBotz3481/YAGSL-Example/pull/318)
* [ ] Set IZone of PIDControllers created from PIDFConfigs by [@CoffeeCoder1](https://github.com/CoffeeCoder1) in [#319](https://github.com/BroncBotz3481/YAGSL-Example/pull/319)
* [ ] Fix typo by [@fletch3555](https://github.com/fletch3555) in [#323](https://github.com/BroncBotz3481/YAGSL-Example/pull/323)
* [ ] Rev Through bore via DIO by [@wackyvert](https://github.com/wackyvert) in [#325](https://github.com/BroncBotz3481/YAGSL-Example/pull/325)
* [ ] stop setMotorBrake from constantly executing by [@rakosi2](https://github.com/rakosi2) in [#327](https://github.com/BroncBotz3481/YAGSL-Example/pull/327)
* [ ] Prevent rotation if controller heading inputs are not past axisDeadband by [@MrFast-js](https://github.com/MrFast-js) in [#328](https://github.com/BroncBotz3481/YAGSL-Example/pull/328)

## 2025.3.0

* [ ] Added `SwerveDrive.setModuleStateOptimization`to allow for easier debugging.
* [ ] Fixed `sparkmax_analog`
* [ ] Null pointer crash when there have been no vision results by [@jwt388](https://github.com/jwt388) in [#307](https://github.com/BroncBotz3481/YAGSL-Example/pull/307)
* [ ] Adding PigeonViaTalonSRX and attached SparkFlex by [@konnorreynolds](https://github.com/konnorreynolds) in [#292](https://github.com/BroncBotz3481/YAGSL-Example/pull/292)
* [ ] Throw an error when module configs are incorrectly set to 0 by [@wackyvert](https://github.com/wackyvert) in [#308](https://github.com/BroncBotz3481/YAGSL-Example/pull/308)
* [ ] Fixed rawAbsoluteEncoder position. by [@konnorreynolds](https://github.com/konnorreynolds) in [#309](https://github.com/BroncBotz3481/YAGSL-Example/pull/309)

## 2025.2.2

* [ ] Fixed `swerve/measuredChassisSpeeds`to report both vx and vy.
* [ ] Fixed `SwerveInputStream.robotRelative` by Team 151
* [ ] Fixed `SwerveDrive.drive` `fieldOriented` parameter to be applied. Bug cuaght by WispySparks from team 2508!
* [ ] Added `SwerveInputStream.headingOffset`

## 2025.2.1

* [ ] Fixed optimization using the relative encoder
* [ ] Fix absolute encoders detection in `SparkMaxSwerve.isAbsoluteEncoderAttached()` by changing null check to `Optional.isPresent()` (Discovered by Team 217!)
* [ ] Changed `absoluteEncoder` to an `Optional` for SparkMax's (by Team 217)
* [ ] Added a way to sysId while spinning in place, changed parameters to `SwerveDriveTest.setDriveSysIdRoutine(new Config(),this, swerveDrive, 12, true)`

## 2025.2.0

* [ ] Disabled kA for the drive motor feedforward by default.
* [ ] Fix getMaxVelocity used in feedforward by [@jwt388](https://github.com/jwt388) in [#286](https://github.com/BroncBotz3481/YAGSL-Example/pull/286)
* [ ] Maximum speed settings do not allow limiting robot to less than full speed by [@clrozeboom](https://github.com/clrozeboom) in [#277](https://github.com/BroncBotz3481/YAGSL-Example/pull/277)
* [ ] Sim modules SysId routines & New maple-sim version by [@catr1xLiu](https://github.com/catr1xLiu) in [#288](https://github.com/BroncBotz3481/YAGSL-Example/pull/288)

## 2025.1.3

* [ ] Fixed `Adjusted IMU Yaw` and set it to publish under `SmartDashboard`
* [ ] Added simulation velocities and readings to module output.
* [ ] Added `srxmag_standalone`absolute encoder type for SRX Mag Encoders attached to a stand alone TalonSRX.

## 2025.1.2

* [ ] Updated to WPILib 2025.1.1
* [ ] Updated CTRE/REV/Studica/Redux vendordeps.
* [ ] Fixed some javadocs failures.
* [ ] Touched up comments by [@yapplejack](https://github.com/yapplejack) in [#278](https://github.com/BroncBotz3481/YAGSL-Example/pull/278)

## 2025.1.1

* [ ] Fixed telemetry publishing problem. Telemetry is now published under the `SmartDashboard` network table.
* [ ] Throw an error when SparkMAX or SparkFlex configuration changes are done while the robot is enabled.
* [ ] Changed example to not set the drive motor idle mode.
* [ ] Added ThirftyNova support.
* [ ] Added `SwerveInputStream.robotRelative`and `SwerveInputStream.allianceRelativeControl`

## 2025.1.0.1

* [ ] Added configuration redundancy for SparkMAX, and SparkFlex.
* [ ] Fix loop time and use field oriented control for driveWithSetpointGenerator (PR [#271 ](https://github.com/BroncBotz3481/YAGSL-Example/pull/271)by @[**jwt388**](https://github.com/jwt388)**)**
* [ ] Added cycle times to telemetry.
* [ ] Optimized telemetry to only post settings when `SwerveDriveTelemetry.updateSettings`is `true`.
* [ ] Reduced retry delay for SparkMAX and SparkFlex to 5ms from 10ms.
* [ ] Reduced on the fly configuration delay to 10ms from 100ms.
* [ ] Added warning for on the fly configuration delay past init.
* [ ] Fixed `SimpleMotorFeedForward.calculate`to use only the velocity. This ignores acceleration unfortunately.
* [ ] Changed `SmartDashboard.put`to NT4 Publishers
* [ ] Removed `IMUVelocity`in favor of using library velocity fetches.
* [ ] Renamed `SwerveIMU.getRate()`-> `SwerveIMU.getYawAngularVelocity()`and made it return an `AngularVelocity`object.
* [ ] Added `SwerveInputStream`object for easy controller conversions.

## 2025.1.0

* [ ] Fixed vision file null-ptr exception.
* [ ] Updated to Maple-Sim 0.2.4; added `SwerveDrive.getMapleSimDrive()` by [@catr1xLiu](https://github.com/catr1xLiu) in [#262](https://github.com/BroncBotz3481/YAGSL-Example/pull/262)
* [ ] Fixed spark max brushed motor controller encoder null-ptr exceptions
* [ ] Fixed `SwerveMath.scaleTranslation`issue
* [ ] Fixed `Vision`update with `resultLists`check by @jwt388 FRC Team 151
* [ ] Changed `SwerveDrive.getMaximumVelocity()`-> `SwerveDrive.getMaximumChassisVelocity()`
* [ ] Changed `SwerveDrive.getMaximumAngularVelocity()` -> `SwerveDrive.getMaximumChassisAngularVelocity()`
* [ ] Used known motor types to calculate the drive motor feedforward.
* [ ] Extended the parser to know the motor type attached to the motor controller. (Except brushed)
* [ ] Separated chassis maximum velocity from module maximum velocity.
* [ ] Added `navx_mxp_serial` back.
* [ ] Added motor specifiers `krakenx60foc`, `krakenx60`, `falcon500foc`, `falcon500`, `sparkmax_neo550`, `sparkmax_neo`, `sparkflex_neo`, `sparkflex_vortex`, `sparkflex_neo550`
* [ ] Create the drive motor feedforward in `SwerveModule` constructor.
* [ ] Added module max speeds to `SwerveModule.maxDriveVelocity` and `SwerveModule.maxAngularVelocity`
* [ ] Updated the web configuration

## 2025.0.0

* [ ] AHRS imu variable is lost after constructor scope ends by [@clrozeboom](https://github.com/clrozeboom) in [#254](https://github.com/BroncBotz3481/YAGSL-Example/pull/254)
* [ ] Add Maple-Sim by [@catr1xLiu](https://github.com/catr1xLiu) in [#251](https://github.com/BroncBotz3481/YAGSL-Example/pull/251)
* [ ] Implement support for kA by [@Etaash-mathamsetty](https://github.com/Etaash-mathamsetty) in [#258](https://github.com/BroncBotz3481/YAGSL-Example/pull/258)
* [ ] Update to WPILIb 2025 Beta-2 by [@thenetworkgrinch](https://github.com/thenetworkgrinch) in [#257](https://github.com/BroncBotz3481/YAGSL-Example/pull/257)
* [ ] Add support to Talon SRX for an integrated absolute encoder by [@WispySparks](https://github.com/WispySparks) in [#208](https://github.com/BroncBotz3481/YAGSL-Example/pull/208)
* [ ] Implemented Path Planner DriveFeedForward using DCMotor modeling by [@catr1xLiu](https://github.com/catr1xLiu) in [#260](https://github.com/BroncBotz3481/YAGSL-Example/pull/260)
* [ ] Fixed NavX inversion.
* [ ] Fixed Absolute Encoder issue on MAXSwerve with help from Team 457 Grease Monkeys

## 2024.7.0 - Last version for 2024 WPILib

* [ ] Added inversion redundancy check to SparkMAX and SparkFlex.
* [ ] Add Warmup for Pathfinding (PR [#241](https://github.com/BroncBotz3481/YAGSL-Example/pull/241) by [TechnologyMan00](https://github.com/Technologyman00))

## 2024.6.1.0

* [ ] Added `Canandgyro` support.
* [ ] A tiny bug fix in the aim-at-speaker command of the example swerve subsystem code by (PR [#239](https://github.com/BroncBotz3481/YAGSL-Example/pull/239) by [catr1xLiu](https://github.com/catr1xLiu))

## 2024.6.0.0

* [ ] Merge Swerve configuration test changes (PR [#228](https://github.com/BroncBotz3481/YAGSL-Example/pull/228) by [**clrozeboom**](https://github.com/clrozeboom)**)**
* [ ] Angular velocity correction (huge update that massively improves YAGSL!) (PR [#231](https://github.com/BroncBotz3481/YAGSL-Example/pull/231) by [yapplejack](https://github.com/yapplejack))
* [ ] Sparkmax optimizations, changes to avg filter etc (PR [#233](https://github.com/BroncBotz3481/YAGSL-Example/pull/233) by [**yapplejack**](https://github.com/yapplejack)**)**
* [ ] Addded `sparkmax_analog5v` as a valid absolute encoder type.
* [ ] Suggestion for desaturateWheelSpeeds() to use desiredChassisSpeeds (PR [#232](https://github.com/BroncBotz3481/YAGSL-Example/pull/232) by [**yapplejack**](https://github.com/yapplejack)**)**
* [ ] Made auto synchronization optional and configurable by `SwerveDrive.setModuleEncoderAutoSynchronize`
* [ ] Fixed configurator problem for `TalonFXSwerve`

## 2024.5.0.4

* [ ] Added PhotonVision `Vision` class to example and integrate it with the example code.
* [ ] Added `getAprilTagPose` method to `Vision` (PR [#226](https://github.com/BroncBotz3481/YAGSL-Example/pull/226) by [**kreidljj**](https://github.com/kreidljj))
* [ ] Update the vision simulation on `Vision.updatePoseEstimation` (PR [#224](https://github.com/BroncBotz3481/YAGSL-Example/pull/224) by [**brandonzx3**](https://github.com/brandonzx3)**)**
* [ ] Add Standard Deviations for YAGSL SwerveDrive Pose Estimator (PR [#222](https://github.com/BroncBotz3481/YAGSL-Example/pull/222) by [**maxikyuu**](https://github.com/maxikyuu)**)**
* [ ] Updating Vendordeps and renaming CanandCoders to CanandMags (PR [#219](https://github.com/BroncBotz3481/YAGSL-Example/pull/219) by [**Turbojax07**](https://github.com/Turbojax07) and YAGSL devs)
* [ ] Fixed documentation issue with `SwerveDriveTelemetry` (Issue [#233 ](https://github.com/BroncBotz3481/YAGSL-Example/issues/223)by [**DanPeled**](https://github.com/DanPeled) )
* [ ] Fixed AbsoluteEncoders attached to the SparkMAX like Throughbores.

## 2024.5.0.3

* [ ] Fixed `SparkMaxAnalogEncoder` wrapper to work. The expected read values are in volts, 3.3v max. (Found and fixed by team Austin from team 2377)
* [ ] Fixed `CanandCoder` to `CanandMag` due to product rename. (Credit to [TurboJax07](https://github.com/Turbojax07))
* [ ] Changed default behavior of setting an attached absolute encoder up without calling `SwerevDrive.pushOffsetsToEncoders` which is now an optional optimization for MAX Swerve modules instead of a requirement. MAX Swerve no longer need to use `360` as the conversion factor IF they do not use `SwerveDrive.pushOffsetsToEncoders`

## 2024.5.0.1

* [ ] Added `SwerveDrive.setVisionMeasurementStdDevs(Matrix<N3, N1> visionMeasurementStdDevs)`
* [ ] Add ability to get the rotation rate from the IMU (PR [#216](https://github.com/BroncBotz3481/YAGSL-Example/pull/216) by [@clrozeboom](https://github.com/clrozeboom))

## 2024.5.0.0

* [ ] Changed input scaling to utilize Polar coordinate magnitude multiplication.
* [ ] Simplify placeInAppropriate0To360Scope (PR [#213](https://github.com/BroncBotz3481/YAGSL-Example/pull/213) by [GoldenStack](https://github.com/GoldenStack))
* [ ] Added auto-centering modules setting.
* [ ] Added back composite conversion factors.

## 2024.4.8.7

* [ ] Increased logging verbosity to introduce logging modes for the field and data. (PR [#185](https://github.com/BroncBotz3481/YAGSL-Example/pull/185) by [5010](https://github.com/5010TigerDynasty))
* [ ] Added `SwerveDrive.setChassisDiscretization` to allow for changing of the discrete value between cycle times which can be tuned to reduce drift. (PR [#194](https://github.com/BroncBotz3481/YAGSL-Example/pull/194) by [TechnologyMan00](https://github.com/Technologyman00))
* [ ] Renamed `SwerveDrive.pushOffsetsToControllers` to `SwerveDrive.pushOffsetsToEncoders`. (PR [#194](https://github.com/BroncBotz3481/YAGSL-Example/pull/194) by [TechnologyMan00](https://github.com/Technologyman00))
* [ ] Changed `Module[...]` to `swerve/modules` in Telemetry.

## 2024.4.8.6

* [ ] Added PIDF helper functions `SwerveModule.setDrivePIDF` and `SwerveModule.setAnglePIDF` alongside `SwerveModule.getDrivePIDF` and `SwerveModule.getAnglePIDF`.
* [ ] Changed feedforward around to use `SwerveModule.setFeedforward` instead of directly modifying `SwerveModule.feedforward`
* [ ] Renamed `SwerveModule.feedforward` to `SwerveModule.driveFeedforward`.
* [ ] Added anti-jitter disabling option via `SwerveModule.setAntiJitter` which also modifies the encoder offsets that are pushed to the motor controllers.
* [ ] Updated vendordeps

## 2024.4.8.5

* [ ] Fixed NavX inversion state not taking any affect on the robot.

## 2024.4.8.4

* [ ] Updated anti jitter to run before cosine compensation.

## 2024.4.8.3

* [ ] Updated `Pigeon2Swerve` to use `imu.getRotation3d()` instead of handling the CAN timeouts.
* [ ] Massively reduced memory footprint required. (with help from [@TheGamer1002](https://github.com/TheGamer1002))

## 2024.4.8.2

* [ ] Made CTRE wait for status times static for user modification. `CANCoderSwerve.STATUS_TIMEOUT_SECONDS`,`TalonFXSwerve.STATUS_TIMEOUT_SECONDS`, and `Pigeon2Swerve.STATUS_TIMEOUT_SECONDS` can be modified for desired use case.

## 2024.4.8.1

* [ ] Added `SwerveDrive.setCosineCompensation` function which can enable and disable cosine compensation as desired, disabled by default in sim because of discrepancies with real robots.

## 2024.4.8

* [ ] Added SysId support. (PR [#160](https://github.com/BroncBotz3481/YAGSL-Example/pull/160) and [#165](https://github.com/BroncBotz3481/YAGSL-Example/pull/165) by [Team 5010](https://github.com/5010TigerDynasty))
* [ ] `waitForStatusUpdate` is used instead of `refresh` with default timeouts of 20ms.
* [ ] Added `Cache` class to cache variables over a `validityPeriod` which will speed up processing and reduce CAN utilization.
* [ ] Added `SwerveDrive.updateCacheValidityPeriods` so the user can change the cache validity period to their hearts desire.
* [ ] Added `SwerveDrive.getModuleMap()` which will fetch all of the `SwerveModule`'s as a HashMap where the key is the module's filename without `.json` so `frontleft.json` would have the key of `frontleft`.

## 2024.4.7

* [ ] Modules will stay in the previous position if the desired velocity is 0.
* [ ] Cosine compensation is now reported through telemetry as expected.

## 2024.4.6.3

* [ ] Updated heading correction to use `getOdometryHeading()` instead of `getYaw()` (PR [#150](https://github.com/BroncBotz3481/YAGSL-Example/pull/150) by [@Blargleflakes](https://github.com/Blargleflakes)) This does impact heading correction PID values in `controllerproperties.json` and may NEED these value's increased by 50x.
* [ ] Added ability to disable the cosine compensator via config files. (PR [#148](https://github.com/BroncBotz3481/YAGSL-Example/pull/148) by [@fovea1959](https://github.com/fovea1959))
* [ ] Added `SwerveDrive.getGyro()` to return `SwerveIMU`.
* [ ] Added `SwerveMotor.setVoltage` and `SwerveMotor.getVoltage` and `SwerveMotor.getAppliledOutput` to the `SwerveMotor` wrapper for future use with SysId.
* [ ] Added functions to test setting voltage of all swerve module motor.
* [ ] Added function to find coupling ratio of all swerve modules.
* [ ] Added function to set steering/azimuth/angle using of all swerve modules.
* [ ] Added function to find kV for drive motors to move.
* [ ] Set the angle motor relative encoder position AFTER changing the conversion factor. (Issue [#155](https://github.com/BroncBotz3481/YAGSL-Example/issues/155))
* [ ] Fixed module open loop control by sending `maxSpeed` to the module. (by @nstrike and [@MarshallTappen](https://github.com/MarshallTappen))
* [ ] Fixed Pigeon2Swerve only using X acceleration (PR [#146](https://github.com/BroncBotz3481/YAGSL-Example/pull/146#event-11577147896) by [@dezash123](https://github.com/dezash123))
* [ ] Prevent drive motors from moving when `absoluteEncoderOffset` is not tuned. (Found by [@fovea1959](https://github.com/fovea1959))
* [ ] Updated javadocs for `SwerveDrive.addVisionMeasurement` so that they reflect the latest changes.

## 2024.4.6.1

* [ ] Fixed `SwerveDrive.resetOdometry` and utilize the pose estimation instead. (PR [#142](https://github.com/BroncBotz3481/YAGSL-Example/pull/142) by [@MarshallTappen](https://github.com/MarshallTappen) and @nstrike [commit](https://github.com/BroncBotz3481/YAGSL-Example/commit/039e5c2867690cfdd5ebd0c1e84eefc6b165adee))
* [ ] Functionalize IMU inversion (PR [#140](https://github.com/BroncBotz3481/YAGSL-Example/pull/140) by [@TechnologyMan00](https://github.com/Technologyman00) and @nstrike)
* [ ] Added `SwerveDrive.getOdometryHeading()`
* [ ] Fixed `SwerveDrive.addVisionMeasurement` with vision standard deviations.
* [ ] Added IMU readings to SmartDashboard via `Raw IMU Yaw` (gyro with invert applied) and `Adjusted IMU Yaw` (pose estimation rotation).
* [ ] Changed `navx_mxp` to `navx_mxp_serial` to notate that it's serial over MXP.
* [ ] Added warning when using `navx_mxp_serial` or `navx_usb`.
* [ ] Added back wheel speed desaturation.

## 2024.4.6

* [ ] Simplified `SwerveMath.calculateDegreesPerRotation` and `SwerveMath.calculateMetersPerRotation` to exclude encoder resolution and add a default.
* [ ] Changed `Module[...] Raw Motor Encoder` to `Module[...] Raw Angle Encoder`.
* [ ] Added `Module[...] Raw Drive Encoder`

## 2024.4.5

* [ ] Fix for TalonFX Angle motor control (by [@bhall-ctre](https://github.com/bhall-ctre), and @Wackyvert 2225 Mentor) .
  1. TalonFX's needed to use conversion factor as gear ratio rather than gear ratio + unit conversion.
  2. Conversion factor needed to be inverted.
  3. setPosition update to reflect current position.
* [ ] Changed `ma3` encoders to be read via analog input. (Discovered by [@CoZm0](https://github.com/CoZ-m0))
* [ ] Support 3 wheel swerve module setups with PathPlanner helper function. (PR #139 by [@TechnologyMan00](https://github.com/Technologyman00))
* [ ] Added `SwerveModule.getAbsoluteEncoder()` `SwerveDrive.getMaximumVelocity()` and `SwerveDrive.getMaximumAngularVelocity()`.
* [ ] Reccommend Tuner X when a compatible Tuner X config is used.
* [ ] Added ability to change heading correction deadband.

## 2024.4.2

* [ ] Added feedforward to SparkMAX's.
* [ ] Added Network Alerts (made by[ @TheGamer1002](https://github.com/TheGamer1002) PR [#136](https://github.com/BroncBotz3481/YAGSL-Example/pull/136))
* [ ] Added feedforward to TalonFX's
* [ ] Fixed TalonFX conversion factor (made by [@jkbo6](https://github.com/jbko6) PR [#128](https://github.com/BroncBotz3481/YAGSL-Example/pull/128))
* [ ] CanAndCoder tweaks (made by [@guineahawk](https://github.com/guineawheek) PR [#134](https://github.com/BroncBotz3481/YAGSL-Example/pull/134))
* [ ] Added missing SparkMAX status frame's (made by [@RoboPenguin7](https://github.com/RoboPenguin7) PR [#130](https://github.com/BroncBotz3481/YAGSL-Example/pull/130))
* [ ] Heading Correction update to make it less sensitive (made by [@balien-12](https://github.com/balien-12) PR [#132](https://github.com/BroncBotz3481/YAGSL-Example/pull/132))
* [ ] Fixed cosine compensator error (made by [@jkbo6](https://github.com/jbko6) PR [#129](https://github.com/BroncBotz3481/YAGSL-Example/pull/129))
* [ ] Updated PathPlanner path based off of alliance (made by [@MarshalTappen](https://github.com/MarshallTappen) PR [#122](https://github.com/BroncBotz3481/YAGSL-Example/pull/122))
* [ ] Fixed Rotation being off, update getDriveBaseMeters (made by [@TechnologyMan00](https://github.com/Technologyman00))
