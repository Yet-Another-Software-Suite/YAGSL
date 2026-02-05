package swervelib.parser.deserializer.reflections;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.TalonFXS;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import java.util.function.Supplier;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.remote.TalonFXSWrapper;
import yams.motorcontrollers.remote.TalonFXWrapper;

/**
 * Reflection class for {@link TalonFX} and {@link TalonFXS}s and other devices from CTRE.
 */
public class CTREDevices
{

  /**
   * Motor controller types.
   */
  public enum MotorControllerType
  {
    /**
     * {@link com.ctre.phoenix6.hardware.TalonFX}
     */
    TALONFX,
    /**
     * {@link com.ctre.phoenix6.hardware.TalonFXS}
     */
    TALONFXS
  }

  /**
   * Get the {@link MotorControllerType} as a {@link SmartMotorController}.
   *
   * @param canid               CAN ID of the {@link MotorControllerType}
   * @param canbus              CAN bus name of the {@link MotorControllerType}
   * @param config              {@link SmartMotorControllerConfig} to apply to the {@link SmartMotorController}
   * @param motor               {@link DCMotor} to use with the {@link SmartMotorController}
   * @param motorControllerType Motor controller type.
   * @return {@link SmartMotorController}
   */
  public static SmartMotorController getMotorController(int canid, String canbus, SmartMotorControllerConfig config,
                                                        DCMotor motor, String motorControllerType)
  {
    // Will throw an error if invalid motor controller type is given.
    var motorType = MotorControllerType.valueOf(motorControllerType.toUpperCase());
    switch (motorType)
    {
      case TALONFX ->
      {
        var motorController = new TalonFX(canid, new CANBus(canbus));
        return new TalonFXWrapper(motorController, motor, config);
      }
      case TALONFXS ->
      {
        var motorController = new TalonFXS(canid, new CANBus(canbus));
        return new TalonFXSWrapper(motorController, motor, config);
      }
    }
    throw new RuntimeException(
        "MotorType not defined. Unsure how you got here... really shouldn't be possible. Here's a cookie (;;)");
  }

  /**
   * Get the gyroscope angle supplier and gyroscope object.
   *
   * @param canid  CAN ID of the gyroscope.
   * @param canbus CAN bus name of the gyroscope.
   * @return {@link Pair} of {@link Supplier} and {@link Object}
   */
  public static Pair<Supplier<Rotation3d>, Object> getGyroAngle(int canid, String canbus)
  {
    var gyro = new Pigeon2(canid, new CANBus(canbus));
    return Pair.of(gyro::getRotation3d, gyro);
  }

  /**
   * Get the {@link com.ctre.phoenix6.hardware.CANcoder} angle.
   *
   * @param canid  CAN ID of the encoder.
   * @param canbus CAN bus name of the encoder.
   * @return {@link Supplier} of {@link Angle} and {@link com.ctre.phoenix6.hardware.CANcoder}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus)
  {
    var encoder = new CANcoder(canid, new CANBus(canbus));
    return Pair.of(() -> encoder.getPosition().getValue(), encoder);
  }
}
