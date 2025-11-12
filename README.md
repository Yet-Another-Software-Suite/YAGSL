# Yet Another Generic Swerve Library (YAGSL) Example project

YAGSL is intended to be an easy implementation of a generic swerve drive that should work for most
square swerve drives. The project is documented
on [here](https://docs.yagsl.com). 

This example is intended to be a starting place on how to use YAGSL. By no means is this intended to
be the base of your robot project. YAGSL provides an easy way to generate a SwerveDrive which can be
used in both TimedRobot and Command-Based Robot templates.


# Overview

### Installation

Vendor URL:

```
https://yet-another-software-suite.github.io/YAGSL/yagsl/yagsl.json
```

[Javadocs here](https://yet-another-software-suite.github.io/YAGSL/javadocs/)  
[Library here](https://github.com/Yet-Another-Software-Suite/YAGSL/)  
[Code here](https://github.com/Yet-Another-Software-Suite/YAGSL/tree/main/yagsl/java/swervelib)  
[WIKI](https://docs.yagsl.com)  
[Config Generation](https://yet-another-software-suite.github.io/YAGSL/config_generator/)

# Create an issue if there is any errors you find!

We will be actively montoring this and fix any issues when we can!

## Development
Development happens here!

# Support our developers!
Visit our organization [here](https://yetanothersoftwaresuite.com)

### TL;DR Generate and download your configuration [here](https://broncbotz3481.github.io/YAGSL-Example/) and unzip it so that it follows structure below:

```text
deploy
└── swerve
    ├── controllerproperties.json
    ├── modules
    │   ├── backleft.json
    │   ├── backright.json
    │   ├── frontleft.json
    │   ├── frontright.json
    │   ├── physicalproperties.json
    │   └── pidfproperties.json
    └── swervedrive.json
```

### Then create your SwerveDrive object like this.

```java
import java.io.File;
import edu.wpi.first.wpilibj.Filesystem;
import swervelib.parser.SwerveParser;
import swervelib.SwerveDrive;
import edu.wpi.first.math.util.Units;


SwerveDrive swerveDrive=new SwerveParser(new File(Filesystem.getDeployDirectory(),"swerve")).createSwerveDrive(Units.feetToMeters(14.5));
```


# Configuration Tips

# Maintainers
- @thenetworkgrinch
- @Technologyman00 

# Special Thanks to Team 7900! Trial N' Terror
Without the debugging and aid of Team 7900 the project could never be as stable or active as it is.
Falcon Support would not have been possible without support from Team 1466 Webb Robotics!