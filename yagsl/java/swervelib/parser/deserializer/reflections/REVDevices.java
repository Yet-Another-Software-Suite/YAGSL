package swervelib.parser.deserializer.reflections;

import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.system.plant.DCMotor;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.local.SparkWrapper;

/**
 * Reflection class for {@link com.revrobotics.spark.SparkBase}s and other REV devices.
 */
public class REVDevices
{

  /**
   * Motor controller types.
   */
  public enum MotorControllerType
  {
    /**
     * {@link com.revrobotics.spark.SparkFlex}
     */
    SPARKFLEX,
    /**
     * {@link com.revrobotics.spark.SparkMax}
     */
    SPARKMAX
  }

  /**
   * Get the {@link com.revrobotics.spark.SparkBase} as a {@link SmartMotorController}.
   *
   * @param canid               CAN ID of the {@link com.revrobotics.spark.SparkBase}
   * @param canbus              CAN bus name of the {@link com.revrobotics.spark.SparkBase}
   * @param config              {@link SmartMotorControllerConfig} to apply to the {@link SmartMotorController}
   * @param motor               {@link DCMotor} to use with the {@link SmartMotorController}
   * @param motorControllerType Motor controller type.
   * @return {@link SmartMotorController}
   */
  public static SmartMotorController getMotorController(int canid, String canbus, SmartMotorControllerConfig config,
                                                        DCMotor motor, String motorControllerType)
  {
    // Will throw an error if invalid motor controller type is given.
    var       motorType       = MotorControllerType.valueOf(motorControllerType.toUpperCase());
    SparkBase motorController = null;
    switch (motorType)
    {
      case SPARKFLEX ->
      {
        motorController = new SparkFlex(canid, MotorType.kBrushless);
      }
      case SPARKMAX ->
      {
        motorController = new SparkMax(canid, MotorType.kBrushless);
      }
    }
    return new SparkWrapper(motorController, motor, config);
  }
}
