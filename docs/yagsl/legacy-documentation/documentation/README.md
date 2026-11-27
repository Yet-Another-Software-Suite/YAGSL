---
hidden: true
---

# Documentation

## Features and Resources

* Features and resources are located on the discussions page [here](https://github.com/BroncBotz3481/YAGSL-Example/discussions) https://github.com/BroncBotz3481/YAGSL-Example/discussions

## How to create a swerve drive?

YAGSL is unique in the fact that you can create a swerve drive based entirely off of JSON configuration files. The JSON configuration files should be located in the [`deploy`](https://github.com/BroncBotz3481/YAGSL-Example/src/main/deploy) directory. You can also create the Configuration objects manually and instantiate your Swerve Drive that way.

### How to create a SwerveDrive using JSON.

This example program creates the [`SwerveDrive`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/SwerveDrive.java) in the [`SwerveSubsystem`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/frc/robot/subsystems/swervedrive2/SwerveSubsystem.java), as you should only interact with it in the SwerveSubsystem if you are using command based programming.

```java
import java.io.File;
import edu.wpi.first.wpilibj.Filesystem;
import swervelib.parser.SwerveParser;
import swervelib.SwerveDrive;
import edu.wpi.first.math.util.Units;


double maximumSpeed = Units.feetToMeters(4.5)
File swerveJsonDirectory = new File(Filesystem.getDeployDirectory(),"swerve");
SwerveDrive  swerveDrive = new SwerveParser(directory).createSwerveDrive(maximumSpeed);

```

This way is fast and easy, no more large unmaintainable and daunting constants file to worry about! To create a JSON directory look at the JSON documentation.

## Creating a swerve drive.

* [ ] Install NavX, ReduxLib, CTRE, and REV vendor dependencies.
* [ ] Install YAGSL (`https://broncbotz3481.github.io/YAGSL-Lib/yagsl/yagsl.json`)
* [ ] Copy [example JSON directory](https://github.com/BroncBotz3481/YAGSL/tree/main/deploy) or create your own then move it into `src/main/frc/deploy/swerve` or generate one from [here](https://broncbotz3481.github.io/YAGSL-Example)
* [ ] Create SwerveDrive from JSON directory via `SwerveDrive drive = new SwerveParser(new File(Filesystem.getDeployDirectory(), "swerve")).createSwerveDrive(maximumSpeed);`
* [ ] View the java docs in [docs/index.html](https://broncbotz3481.github.io/YAGSL/)
* [ ] Experiment!

## Migrating Old Configuration Files

1. Delete `wheelDiamter`, `gearRatio`, `encoderPulsePerRotation` from `physicalproperties.json`
2. Add `optimalVoltage` to `physicalproperties.json`
3. Delete `maxSpeed` and `optimalVoltage` from `swervedrive.json`
4. **IF** a swerve module doesn't have the same drive motor or steering motor as the rest of the swerve drive you **MUST** specify a `conversionFactor` for BOTH the drive and steering motor in the modules configuration JSON file. IF one of the motors is the same as the rest of the swerve drive and you want to use that `conversionFactor`, set the `conversionFactor` in the module JSON configuration to 0.
5. You MUST specify the maximum speed when creating a `SwerveDrive` through `new SwerveParser(directory).createSwerveDrive(maximumSpeed);`
6. IF you do not want to set `conversionFactor` in `swervedrive.json`. You can pass it into the constructor as a parameter like this

```java
double DriveConversionFactor = SwerveMath.calculateMetersPerRotation(Units.inchesToMeters(WHEEL_DIAMETER), GEAR_RATIO, ENCODER_RESOLUTION);
double SteeringConversionFactor = SwerveMath.calculateDegreesPerSteeringRotation(GEAR_RATIO, ENCODER_RESOLUTION);
new SwerveParser(directory).createSwerveDrive(maximumSpeed, SteeringConversionFactor, DriveConversionFactor);
```

## Contributions

This library is based off of 95's [SwervyBot code](https://github.com/first95/FRC2023/tree/main/SwervyBot) in addition to a wide variety of other code bases. A huge thank you to every team which has open sourced their swerve code!
