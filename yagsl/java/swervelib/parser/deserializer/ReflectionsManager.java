package swervelib.parser.deserializer;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import swervelib.parser.deserializer.reflections.CTREDevices.MotorControllerType;
import swervelib.parser.json.SwerveDriveJson.GyroAxis;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;

/**
 * Create classes only if the vendor dep exists.
 */
public class ReflectionsManager
{

  /**
   * Vendors that supply their own vendordep to communicate with their products.
   */
  public enum VENDOR
  {
    /**
     * REVLib
     */
    REV("com.revrobotics.spark.SparkBase"),
    /**
     * CTRE Phoenix 5 and 6
     */
    PHOENIX5("com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX"),
    /**
     * CTRE Phoenix 6
     */
    PHOENIX6("com.ctre.phoenix6.hardware.TalonFXS"),
    /**
     * ThriftyLib
     */
    THRIFTYBOT("com.thethriftybot.ThriftyNova"),
    /**
     * Studica
     */
    STUDICA("com.studica.frc.Navx");

    /**
     * Class to check for to confirm existence of vendordep.
     */
    private final String classToCheck;

    /**
     * Constructor for VENDOR enum.
     *
     * @param checkClass Class to check for to confirm existence of vendordep.
     */
    VENDOR(String checkClass)
    {
      classToCheck = checkClass;
    }

    /**
     * Check if the vendordep exists.
     *
     * @return Boolean on existence of their library in the current program.
     */
    public boolean exists()
    {
      try
      {
        Class.forName(classToCheck);
        return true;
      } catch (ClassNotFoundException e)
      {
        return false;
      }
    }
  }

  /**
   * {@link SmartMotorController} vendor classes.
   */
  public enum VendorMotorController
  {
    /**
     * TalonFX Motor Controller wihtin KrakenX60 and KrakenX44
     */
    TALONFX("CTREDevices"),
    /**
     * TalonFXS Motor Controller
     */
    TALONFXS("CTREDevices"),
    /**
     * SparkMax Motor Controller
     */
    SPARKMAX("REVDevices"),
    /**
     * SparkFlex Motor Controller
     */
    SPARKFLEX("REVDevices"),
    /**
     * ThriftyBot Nova Motor Controller
     */
    NOVA("ThriftyBotDevices"),
    /**
     * not a motor controller
     */
    NONE("");

    /**
     * Reflection package name
     */
    private final String packageName = "swervelib.parser.deserializer.reflections";
    /**
     * Reflection vendor class.
     */
    private final String className;

    /**
     * Constructor for MotorControllers enum.
     *
     * @param className Class vendor object
     */
    VendorMotorController(String className)
    {
      this.className = packageName + "." + className;
    }

    /**
     * Get the {@link MotorControllerType} as a {@link SmartMotorController}.
     *
     * @param canid  CAN ID of the {@link MotorControllerType}
     * @param canbus CAN bus name of the {@link MotorControllerType}
     * @param config {@link SmartMotorControllerConfig} to apply to the {@link SmartMotorController}
     * @param motor  {@link DCMotor} to use with the {@link SmartMotorController}
     * @return {@link SmartMotorController}
     */
    public SmartMotorController getMotorController(int canid, String canbus, SmartMotorControllerConfig config,
                                                   DCMotor motor)
    {
      try
      {
        Class<?> wrapper = Class.forName(className);
        return (SmartMotorController) wrapper.getMethod("getMotorController",
                                                        int.class,
                                                        String.class,
                                                        SmartMotorControllerConfig.class,
                                                        DCMotor.class,
                                                        String.class)
                                             .invoke(null, canid, canbus, config, motor, this.name());
      } catch (InvocationTargetException e)
      {
        System.err.println("Error getting motor controller: " + className + ".getMotorController");
        throw new RuntimeException(e.getTargetException());
      } catch (Exception e)
      {
        System.err.println("Error getting motor controller: " + className + ".getMotorController");
        throw new RuntimeException(e);
      }
    }

    /**
     * Get the {@link Angle} as a {@link Supplier} and the vendor absolute encoder object.
     *
     * @param externalEncoderType External encoder type attached to the motor controller.
     * @param motorController     {@link SmartMotorController} to get the encoder from.
     * @param inverted            Inverted absolute encoder readings.
     * @return {@link Supplier} of {@link Angle}
     */
    public Pair<Supplier<Angle>, Object> getAbsoluteEncoder(String externalEncoderType,
                                                            SmartMotorController motorController, boolean inverted)
    {
      try
      {
        Class<?> wrapper = Class.forName(className);
        return (Pair<Supplier<Angle>, Object>) wrapper.getMethod("getAttachedAbsoluteEncoder",
                                                                 String.class,
                                                                 Object.class,
                                                                 boolean.class)
                                                      .invoke(null,
                                                              externalEncoderType,
                                                              motorController.getMotorController(),
                                                              inverted);
      } catch (Exception e)
      {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Absolute encoder vendor classes.
   */
  public enum AbsoluteEncoder
  {
    /**
     * CANCoder class
     */
    CANCODER("CTREDevices"),
    /**
     * CANandMag encoder
     */
    CANANDMAG("ReduxDevices"),
    /**
     * AM CAN Encoder
     */
    ANDYMARK("AndyMarkDevices"),
    /**
     * ThriftyBot CAN encoder
     */
    THRIFTYBOT10PIN("ThriftyBotDevices"),
    /**
     * REV Robotics Spline Encoder, (if you use this for an azimuth encoder... wow...)
     */
    SPLINE_ENCODER("REVDevices");

    private final String packageName = "swervelib.parser.deserializer.reflections";
    private final String className;

    /**
     * Constructor for AbsoluteEncoder enum.
     *
     * @param className Class vendor object
     */
    AbsoluteEncoder(String className)
    {
      this.className = packageName + "." + className;
    }

    /**
     * Get the {@link Angle} as a {@link Supplier} and the vendor absolute encoder object.
     *
     * @param canid    CAN ID of the encoder.
     * @param canbus   CAN bus name of the encoder.
     * @param inverted Inverted absolute encoder readings.
     * @return {@link Supplier} of {@link Angle}
     */
    public Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus, boolean inverted)
    {
      try
      {
        Class<?> wrapper = Class.forName(className);
        return (Pair<Supplier<Angle>, Object>) wrapper.getMethod("getAbsoluteEncoder",
                                                                 int.class,
                                                                 String.class, boolean.class)
                                                      .invoke(null, canid, canbus, inverted);
      } catch (Exception e)
      {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Gyro vendor classes.
   */
  public enum Gyro
  {
    /**
     * Pigeon2 Gyro
     */
    PIGEON2("CTREDevices"),
    /**
     * NavX3 Gyro
     */
    NAVX3("StudicaLibDevices"),
    /**
     * CANandGyro
     */
    CANANDGYRO("ReduxDevices");
    private final String packageName = "swervelib.parser.deserializer.reflections";
    private final String className;

    /**
     * Constructor for Gyro enum.
     *
     * @param className Class vendor object
     */
    Gyro(String className)
    {
      this.className = packageName + "." + className;
    }

    /**
     * Get the angle.
     *
     * @param canid    CAN ID of the encoder.
     * @param canbus   CAN bus name of the encoder.
     * @param axis     Gyro axis to use for the heading.
     * @param inverted Inversion of the gyro readings.
     * @return {@link Supplier} of {@link Angle}
     */
    public Pair<Supplier<Angle>, Object> getGyro(int canid, String canbus, GyroAxis axis, boolean inverted)
    {
      try
      {
        Class<?> wrapper = Class.forName(className);
        return (Pair<Supplier<Angle>, Object>) wrapper.getMethod("getGyroAngle",
                                                                 int.class,
                                                                 String.class,
                                                                 GyroAxis.class,
                                                                 boolean.class)
                                                      .invoke(null, canid, canbus, axis, inverted);
      } catch (Exception e)
      {
        throw new RuntimeException(e);
      }
    }
  }


  /**
   * Create objects if the vendordep exists. Throw an exception when they dont.
   *
   * @param v              Vendor to check if the vendordep exists.
   * @param className      Wrapper classname to create.
   * @param parameterTypes Parameter types for the wrappers constructor.
   * @param parameters     Parameters for the wrappers constructor
   * @param <T>            Wrapper type.
   * @return Wrapper object.
   */
  public static <T> T create(VENDOR v, String className, Class<?>[] parameterTypes, Object[] parameters)
  {
    if (!v.exists())
    {
      throw new RuntimeException("Vendor " + v + " library not found! Please install it!");
    }
    try
    {
      Class<?> wrapper   = Class.forName(className);
      Object   vendorObj = wrapper.getDeclaredConstructor(parameterTypes).newInstance(parameters);
      return (T) vendorObj;
    } catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

}
