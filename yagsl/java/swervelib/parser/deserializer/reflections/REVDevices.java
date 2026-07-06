package swervelib.parser.deserializer.reflections;

import static edu.wpi.first.units.Units.Rotations;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.encoder.SplineEncoder;
import com.revrobotics.encoder.config.DetachedEncoderConfig;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
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
   * Absolute encoder types.
   */
  public enum AbsoluteEncoder
  {
    /**
     * {@link com.revrobotics.encoder.SplineEncoder}
     */
    SPLINEENCODER,
    /**
     * Analog input {@link com.revrobotics.spark.FeedbackSensor#kAnalogSensor}
     */
    ANALOG,
    /**
     * Analog input {@link com.revrobotics.spark.FeedbackSensor#kAnalogSensor}
     */
    ANALOG5V,
    /**
     * Duty cycle {@link com.revrobotics.spark.FeedbackSensor#kAbsoluteEncoder}
     */
    DUTYCYCLE
  }

  /**
   * Map of CAN ID to motor controller.
   */
  private static Map<Integer, SmartMotorController>          motorControllers = new HashMap<Integer, SmartMotorController>();
  /**
   * Encoder object hash map.
   */
  private static Map<Integer, Pair<Supplier<Angle>, Object>> encoders         = new HashMap<Integer, Pair<Supplier<Angle>, Object>>();

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
    if (motorControllers.containsKey(canid))
    {
      return motorControllers.get(canid);
    }
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
    var smc = new SparkWrapper(motorController, motor, config);
    motorControllers.put(canid, smc);
    return smc;
  }

  /**
   * Get the {@link Angle} {@link Supplier} and the encoder object.
   *
   * @param canid    CAN ID of the encoder.
   * @param canbus   CAN bus name for the encoder.
   * @param inverted Inversion of the encoder.
   * @return {@link Pair} of {@link Supplier} and {@link Object}
   * @implNote {@link Angle} is in the range of [0, 1) by default.
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus, boolean inverted)
  {
    if (encoders.containsKey(canid))
    {
      return encoders.get(canid);
    }
    var encoder = new SplineEncoder(canid);
    encoder.configure(new DetachedEncoderConfig().inverted(inverted).velocityConversionFactor(1.0 / 60.0),
                      ResetMode.kNoResetSafeParameters);
    encoders.put(canid, Pair.of(() -> Rotations.of(encoder.getAngle()), encoder));
    return encoders.get(canid);
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
    var encoderType = AbsoluteEncoder.valueOf(attachType.toUpperCase());
    switch (encoderType)
    {
      case SPLINEENCODER:
        throw new UnsupportedOperationException("Spline encoders are not attached absolute encoders.");
      case ANALOG:
      case ANALOG5V:
        if (motorController instanceof SparkMax || motorController instanceof SparkFlex)
        {
          SparkBase spark =
              (motorController instanceof SparkMax) ? (SparkMax) motorController : (SparkFlex) motorController;
          double baseVoltage = (encoderType == AbsoluteEncoder.ANALOG) ? 3.3 : 5.0;
          // Configure Analog Encoders
          SparkBaseConfig cfg = (motorController instanceof SparkMax) ? new SparkMaxConfig() : new SparkFlexConfig();
          cfg.closedLoop.feedbackSensor(FeedbackSensor.kAnalogSensor);
          // Enable CAN frames
          cfg.signals
              .analogVelocityAlwaysOn(true)
              .analogVoltageAlwaysOn(true)
              .analogPositionAlwaysOn(true)
              .analogVoltagePeriodMs(20)
              .analogPositionPeriodMs(20)
              .analogVelocityPeriodMs(20);
          // Configure analog sensor to report in Rotations
          cfg.analogSensor.inverted(inverted)
                          .positionConversionFactor(1.0 / baseVoltage)
                          .velocityConversionFactor(1.0 / baseVoltage);
          spark.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
          var analogSensor = spark.getAnalog();
          return Pair.of(() -> Rotations.of(analogSensor.getPosition()), analogSensor);
        }
      case DUTYCYCLE:
        if (motorController instanceof SparkMax || motorController instanceof SparkFlex)
        {
          SparkBase spark =
              (motorController instanceof SparkMax) ? (SparkMax) motorController : (SparkFlex) motorController;
          SparkBaseConfig cfg = (motorController instanceof SparkMax) ? new SparkMaxConfig() : new SparkFlexConfig();
          // Configure Duty Cycle Encoders CAN Frame
          cfg.signals
              .absoluteEncoderPositionAlwaysOn(true)
              .absoluteEncoderPositionPeriodMs(20);
          // Configure conversion factors to Rotations and Rotations per second
          cfg.absoluteEncoder.inverted(inverted)
                             .positionConversionFactor(1.0)
                             .velocityConversionFactor(1.0 / 60.0);
          spark.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
          var encoder = spark.getAbsoluteEncoder();
          return Pair.of(() -> Rotations.of(encoder.getPosition()), encoder);
        }
    }
    throw new UnsupportedOperationException("Invalid encoder type: " + encoderType);
  }
}
