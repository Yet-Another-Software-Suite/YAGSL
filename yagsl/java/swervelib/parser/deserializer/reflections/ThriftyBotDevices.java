package swervelib.parser.deserializer.reflections;

import static edu.wpi.first.units.Units.Rotations;

import com.thethriftybot.devices.ThriftyEncoder;
import com.thethriftybot.devices.ThriftyNova;
import com.thethriftybot.devices.ThriftyNova.ExternalEncoder;
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
   * @param canid    CAN ID of the encoder.
   * @param canbus   CAN bus name of the encoder.
   * @param inverted Inversion of the encoder.
   * @return {@link Supplier} of {@link Angle} and {@link com.thethriftybot.devices.ThriftyEncoder}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus, boolean inverted)
  {
    var encoder = new ThriftyEncoder(canid);
    return Pair.of(() -> Rotations.of(encoder.getPosition() / 16383.0 * (inverted ? -1 : 1)), encoder);
  }

  /**
   * Absolute encoder types.
   */
  public enum AbsoluteEncoderType
  {
    /**
     * Redux Canandmag encoder.
     */
    CANANDMAG(ExternalEncoder.REDUX_ENCODER),
    /**
     * 10 pin encoder.
     */
    THRIFTY10PIN(ExternalEncoder.THRIFTY_10_PIN_ENCODER),
    /**
     * Through bore encoder.
     */
    THROUGHBORE(ExternalEncoder.REV_ENCODER),
    /**
     * SRX Mag encoder.
     */
    SRXMAG(ExternalEncoder.SRX_MAG_ENCODER),
    /**
     * DutyCycle encoder.
     */
    DUTYCYCLE(ExternalEncoder.REV_ENCODER);

    /**
     * Absolute encoder type.
     */
    public final ExternalEncoder encoder;

    /**
     * Constructor for AbsoluteEncoderType enum.
     *
     * @param encoder External encoder type.
     */
    AbsoluteEncoderType(ExternalEncoder encoder)
    {
      this.encoder = encoder;
    }
  }

  /**
   * Get the attached absolute encoder.
   *
   * @param attachType      Absolute encoder type. Only DutyCycle and analog inputs are supported.
   * @param motorController Spark motor controller to get the absolute encoder from.
   * @param inverted        Inverted absolute encoder readings.
   * @return {@link Pair} of {@link Supplier} and DutyCycleEncoder {@link Object}
   */
  public static Pair<Supplier<Angle>, Object> getAttachedAbsoluteEncoder(String attachType, Object motorController,
                                                                         boolean inverted)
  {
    // Will throw an error if invalid motor controller type is given.
    var encoderType = AbsoluteEncoderType.valueOf(attachType.toUpperCase()).encoder;
    ((ThriftyNova) motorController).setExternalEncoder(encoderType);
    return Pair.of(() -> Rotations.of(((ThriftyNova) motorController).getPositionAbs() * (inverted ? -1 : 1)),
                   encoderType);
  }
}
