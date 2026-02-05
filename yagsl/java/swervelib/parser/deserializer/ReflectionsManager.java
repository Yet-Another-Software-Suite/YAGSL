package swervelib.parser.deserializer;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import java.util.function.Supplier;
import swervelib.parser.deserializer.reflections.CTREDevices.MotorControllerType;
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
  public enum MotorControllers
  {
    TALONFX("CTREDevices"),
    TALONFXS("CTREDevices"),
    SPARKMAX("REVDevices"),
    SPARKFLEX("REVDevices"),
    NOVA("ThriftyBotDevices");

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
    MotorControllers(String className)
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
      } catch (Exception e)
      {
        throw new RuntimeException(e);
      }
    }
  }

  public enum AbsoluteEncoder
  {
    CANCODER("CTREDevices"),
    CANANDMAG("ReduxDevices"),
    ANDYMARK("AndyMarkDevices"),
    THRIFTYBOT10PIN("ThriftyBotDevices");

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
     * Get the angle.
     *
     * @param canid  CAN ID of the encoder.
     * @param canbus CAN bus name of the encoder.
     * @return {@link Supplier} of {@link Angle}
     */
    public Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus)
    {
      try
      {
        Class<?> wrapper = Class.forName(className);
        return (Pair<Supplier<Angle>, Object>) wrapper.getMethod("getAbsoluteEncoder",
                                                                 int.class,
                                                                 String.class)
                                                      .invoke(null, canid, canbus);
      } catch (Exception e)
      {
        throw new RuntimeException(e);
      }
    }
  }

  public enum Gyro
  {
    PIGEON2("CTREDevices"),
    NAVX3("StudicaLibDevices"),
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
     * @param canid  CAN ID of the encoder.
     * @param canbus CAN bus name of the encoder.
     * @return {@link Supplier} of {@link Angle}
     */
    public Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus)
    {
      try
      {
        Class<?> wrapper = Class.forName(className);
        return (Pair<Supplier<Angle>, Object>) wrapper.getMethod("getGyroAngle",
                                                                 int.class,
                                                                 String.class)
                                                      .invoke(null, canid, canbus);
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
