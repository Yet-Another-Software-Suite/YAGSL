package swervelib.parser.deserializer.reflections;

import static edu.wpi.first.units.Units.Rotations;

import com.thethriftybot.devices.ThriftyEncoder;
import com.thethriftybot.devices.ThriftyNova;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import java.util.function.Supplier;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.local.NovaWrapper;

/**
 * Reflection class for {@link yams.motorcontrollers.SmartMotorController}s and other devices from ThriftyBot.
 */
public class ThriftyBotDevices
{

  /**
   * Motor controller types.
   */
  public enum MotorControllerType
  {
    /**
     * {@link ThriftyNova}
     */
    NOVA
  }

  /**
   * Get the {@link ThriftyNova} as a {@link SmartMotorController}.
   *
   * @param canid               CAN ID of the {@link ThriftyNova}
   * @param canbus              CAN bus name of the {@link ThriftyNova}
   * @param config              {@link SmartMotorControllerConfig} to apply to the {@link SmartMotorController}
   * @param motor               {@link DCMotor} to use with the {@link SmartMotorController}
   * @param motorControllerType Motor controller type.
   * @return {@link SmartMotorController}
   */
  public static SmartMotorController getMotorController(int canid, String canbus, SmartMotorControllerConfig config,
                                                        DCMotor motor, String motorControllerType)
  {
    MotorControllerType.valueOf(motorControllerType.toUpperCase());
    return new NovaWrapper(new ThriftyNova(canid), motor, config);
  }

  /**
   * Get the {@link com.thethriftybot.devices.ThriftyEncoder} angle.
   *
   * @param canid  CAN ID of the encoder.
   * @param canbus CAN bus name of the encoder.
   * @return {@link Supplier} of {@link Angle} and {@link com.thethriftybot.devices.ThriftyEncoder}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus)
  {
    var encoder = new ThriftyEncoder(canid);
    return Pair.of(() -> Rotations.of(encoder.getPosition() / 16383.0), encoder);
  }


}
