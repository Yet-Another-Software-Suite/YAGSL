# Swerve Module Physical Properties

## Swerve Module Physical Properties (`physicalproperties.json`)

This JSON configures the physical properties shared with all the Swerve Modules. It maps 1:1 with [`PhysicalPropertiesJson.java`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/parser/json/PhysicalPropertiesJson.java) which creates [`SwerveModulePhysicalCharacteristics.java`](https://github.com/BroncBotz3481/YAGSL-Example/tree/main/src/main/java/swervelib/parser/SwerveModulePhysicalCharacteristics.java).

## Fields

| Name                             | Units                                                           | Required | Description                                                                                            |
| -------------------------------- | --------------------------------------------------------------- | -------- | ------------------------------------------------------------------------------------------------------ |
| `conversionFactor`               | [MotorConfig](swerve-module-physical-properties.md#motorconfig) | N        | Conversion factor applied to the motor controller for the onboard PID.                                 |
| `optimalVoltage`                 | Voltage                                                         | Y        | Optimal voltage to compensate to and base feedforward calculations off of.                             |
| `currentLimit`                   | [MotorConfig](swerve-module-physical-properties.md#motorconfig) | N        | The current limit in AMPs to apply to the motors.                                                      |
| `rampRate`                       | [MotorConfig](swerve-module-physical-properties.md#motorconfig) | N        | The minimum number of seconds to take for the motor to go from 0 to full throttle.                     |
| `wheelGripCoefficientOfFriction` | Coefficient of Friction on Carpet                               | N        | The grip tape coefficient of friction on carpet. Used to calculate the practical maximum acceleration. |

#### MotorConfig

| Name    | Units  | Required | Description        |
| ------- | ------ | -------- | ------------------ |
| `drive` | Number | Y        | Drive motor value. |
| `angle` | Number | Y        | Angle motor value. |
