package swervelib.core.parser.deserializer.reflections;

import static org.wpilib.units.Units.Rotations;

import com.thrifty.canEncoder.CanEncoder;
import com.thrifty.canEncoder.CanEncoderConfig;
import com.thrifty.core.Motor.AbsoluteEncoderType;
import com.thrifty.core.Motor.Direction;
import com.thrifty.core.Motor.FeedbackSensorType;
import com.thrifty.core.Motor.MotorType;
import com.thrifty.nova.Nova;
import com.thrifty.nova.NovaConfig;
import org.wpilib.util.Pair;
import org.wpilib.math.system.DCMotor;
import org.wpilib.units.measure.Angle;
import java.util.function.Supplier;
import yams.core.motorcontrollers.SmartMotorController;
import yams.core.motorcontrollers.SmartMotorControllerConfig;
import yams.core.motorcontrollers.local.NovaWrapper;

/**
 * Reflection class for {@link yams.core.motorcontrollers.SmartMotorController}s and other devices from ThriftyBot.
 */
public class ThriftyBotDevices
{

  /**
   * Motor controller types.
   */
  public enum MotorControllerType
  {
    /**
     * {@link Nova}
     */
    NOVA
  }

  /**
   * Get the {@link Nova} as a {@link SmartMotorController}.
   *
   * @param canid               CAN ID of the {@link Nova}
   * @param canbus              CAN bus of the {@link Nova}. Empty for the default bus, or a raw bus ID (e.g.
   *                            {@code "1"}).
   * @param config              {@link SmartMotorControllerConfig} to apply to the {@link SmartMotorController}
   * @param motor               {@link DCMotor} to use with the {@link SmartMotorController}
   * @param motorControllerType Motor controller type.
   * @return {@link SmartMotorController}
   */
  public static SmartMotorController getMotorController(int canid, String canbus, SmartMotorControllerConfig config,
                                                        DCMotor motor, String motorControllerType)
  {
    MotorControllerType.valueOf(motorControllerType.toUpperCase());
    return new NovaWrapper(new Nova(getCANBus(canbus), canid, getMotorType(motor)), motor, config);
  }

  /**
   * Get the Thrifty CAN Encoder angle.
   *
   * @param canid    CAN ID of the encoder.
   * @param canbus   CAN bus of the encoder. Empty for the default bus, or a raw bus ID (e.g. {@code "1"}).
   * @param inverted Inversion of the encoder.
   * @return {@link Supplier} of {@link Angle} and {@link CanEncoder}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus, boolean inverted)
  {
    var encoder = new CanEncoder(getCANBus(canbus), canid);
    encoder.configure(CanEncoderConfig.direction(getDirection(inverted)));
    return Pair.of(() -> Rotations.of(encoder.status().getPositionAbs()), encoder);
  }

  /**
   * Absolute encoder types.
   */
  public enum AbsoluteEncoder
  {
    /**
     * Redux Canandmag encoder.
     */
    CANANDMAG(AbsoluteEncoderType.REDUX_ENCODER),
    /**
     * 10 pin encoder.
     */
    THRIFTY10PIN(AbsoluteEncoderType.THRIFTY_10_PIN_ENCODER),
    /**
     * Through bore encoder.
     */
    THROUGHBORE(AbsoluteEncoderType.REV_ENCODER),
    /**
     * SRX Mag encoder.
     */
    SRXMAG(AbsoluteEncoderType.SRX_MAG_ENCODER),
    /**
     * DutyCycle encoder.
     */
    DUTYCYCLE(AbsoluteEncoderType.REV_ENCODER),
    /**
     * Analog encoder.
     */
    ANALOG(AbsoluteEncoderType.ANALOG_ENCODER);

    /**
     * Absolute encoder type.
     */
    public final AbsoluteEncoderType encoder;

    /**
     * Constructor for AbsoluteEncoder enum.
     *
     * @param encoder Absolute encoder type on the Nova's data port.
     */
    AbsoluteEncoder(AbsoluteEncoderType encoder)
    {
      this.encoder = encoder;
    }
  }

  /**
   * Get the absolute encoder attached to the {@link Nova}'s data port.
   *
   * @param attachType      Absolute encoder type, one of {@link AbsoluteEncoder}.
   * @param motorController {@link Nova} to get the absolute encoder from.
   * @param inverted        Inverted absolute encoder readings.
   * @return {@link Pair} of {@link Supplier} and {@link FeedbackSensorType#ABS}, which is what
   * {@link NovaWrapper} expects as its external encoder.
   */
  public static Pair<Supplier<Angle>, Object> getAttachedAbsoluteEncoder(String attachType, Object motorController,
                                                                         boolean inverted)
  {
    // Will throw an error if invalid encoder type is given.
    var encoderType = AbsoluteEncoder.valueOf(attachType.toUpperCase()).encoder;
    var nova        = (Nova) motorController;
    nova.configure(NovaConfig.absoluteEncoderType(encoderType), NovaConfig.absoluteDirection(getDirection(inverted)));
    return Pair.of(() -> Rotations.of(nova.status().getAbsPosition()), FeedbackSensorType.ABS);
  }

  /**
   * Get the Nova {@link MotorType} for the {@link DCMotor}, defaulting to {@link MotorType#NEO}.
   *
   * @param motor {@link DCMotor} connected to the {@link Nova}.
   * @return {@link MotorType} of the motor.
   */
  private static MotorType getMotorType(DCMotor motor)
  {
    if (isSameMotor(motor, DCMotor.getMinion(1)))
    {
      return MotorType.MINION;
    }
    // Matches the pulsar in DeviceJson.getDCMotor.
    if (isSameMotor(motor, new DCMotor(12, 3.1, 189, 1, 7500, 1)))
    {
      return MotorType.PULSAR;
    }
    return MotorType.NEO;
  }

  /**
   * Check if two {@link DCMotor}s have the same characteristics.
   *
   * @param a First {@link DCMotor}.
   * @param b Second {@link DCMotor}.
   * @return Whether the stall torque and free speed match.
   */
  private static boolean isSameMotor(DCMotor a, DCMotor b)
  {
    return a.stallTorque == b.stallTorque && a.freeSpeed == b.freeSpeed;
  }

  /**
   * Get the ThriftyLib {@link Direction} for an inversion, matching {@link NovaWrapper}.
   *
   * @param inverted Inversion.
   * @return {@link Direction}
   */
  private static Direction getDirection(boolean inverted)
  {
    return inverted ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE;
  }

  /**
   * Get the ThriftyLib CAN bus ID for the CAN bus given in the JSON configuration.
   *
   * @param canbus CAN bus. Empty for the default bus, or a raw bus ID (e.g. {@code "1"}).
   * @return CAN bus ID.
   */
  private static int getCANBus(String canbus)
  {
    if (canbus == null || canbus.isBlank())
    {
      return 0;
    }
    return Integer.parseInt(canbus);
  }
}
