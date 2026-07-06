package swervelib.parser.json;

import static edu.wpi.first.units.Units.Rotations;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.wpilibj.AnalogEncoder;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import java.util.function.Supplier;
import swervelib.parser.deserializer.ReflectionsManager.AbsoluteEncoder;
import swervelib.parser.deserializer.ReflectionsManager.Gyro;
import swervelib.parser.deserializer.ReflectionsManager.VendorMotorController;
import swervelib.parser.json.SwerveDriveJson.GyroAxis;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;

/**
 * Device JSON parsed class. Used to access the JSON data.
 */
public class DeviceJson
{

  /**
   * The device type
   */
  public String type;
  /**
   * The CAN ID or pin ID of the device.
   */
  public int    id;
  /**
   * SmartIO Channel.
   */
  public int    channel = 0;
  /**
   * The CAN bus name which the device resides on if using CAN.
   */
  public String canbus  = "";

  /**
   * Get the DC motor from the motor type.
   *
   * @param motorType Motor type.
   * @return {@link DCMotor}
   */
  public static DCMotor getDCMotor(String motorType)
  {
    switch (motorType)
    {
      case "neo2":
      case "neo":
        return DCMotor.getNEO(1);
      case "neo550":
        return DCMotor.getNeo550(1);
      case "vortex":
        return DCMotor.getNeoVortex(1);
      case "minion":
        return DCMotor.getMinion(1);
      case "krakenx44":
        return DCMotor.getKrakenX44(1);
      case "krakenx60":
        return DCMotor.getKrakenX60(1);
      case "pulsar":
        return new DCMotor(12, 3.1, 189, 1, 7500, 1);
      default:
        throw new IllegalArgumentException("Invalid motor type: " + motorType);
    }
  }

  /**
   * Get the gyro angle supplier.
   *
   * @param axis     Gyro axis.
   * @param inverted Invert the gyro angle.
   * @return {@link Supplier} of {@link Angle}
   */
  public Pair<Supplier<Angle>, Object> getGyro(GyroAxis axis, boolean inverted)
  {
    if (type.contains("_"))
    {
      String[] vendorData           = type.split("_");
      String   vendorType           = vendorData[0];
      String   vendorConnectionType = vendorData[1];
      switch (vendorConnectionType)
      {
        case "can":
          switch (vendorType)
          {
            case "navx3":
              return Gyro.NAVX3.getGyro(id, canbus, axis, inverted);
            case "pigeon2":
              return Gyro.PIGEON2.getGyro(id, canbus, axis, inverted);
            case "canandgyro":
              return Gyro.CANANDGYRO.getGyro(id, canbus, axis, inverted);
          }
        case "internal":
          throw new IllegalArgumentException("Internal gyro not supported yet!");
      }
    }
    throw new IllegalArgumentException("Invalid gyro type: " + type);
  }

  /**
   * Vendor of a device (motor controller, encoder, or gyroscope).
   */
  public enum VENDOR
  {
    /**
     * Cross The Road Electronics.
     */
    CTRE,
    /**
     * REV Robotics.
     */
    REV,
    /**
     * ThriftyBot.
     */
    THRIFTYBOT,
    /**
     * AndyMark.
     */
    ANDYMARK,
    /**
     * Redux Robotics.
     */
    REDUX,
    /**
     * Studica.
     */
    STUDICA,
    /**
     * Device attached to the roboRIO SmartIO port (DIO/Analog).
     */
    SMARTIO,
    /**
     * Limelight.
     */
    LIMELIGHT,
    /**
     * Vendor could not be determined.
     */
    UNKNOWN
  }

  /**
   * Get the vendor of the device.
   *
   * @param attachedType Vendor to return when the device is directly attached (not on CAN/DIO/Analog),
   *                      since the attached vendor cannot be determined from the device type string alone.
   * @return Vendor of the device.
   */
  public VENDOR getVendor(VENDOR attachedType)
  {
    if (type.contains("_"))
    {
      String[] vendorData           = type.split("_");
      String   vendorType           = vendorData[0];
      String   vendorConnectionType = vendorData[1];
      switch (vendorType)
      {
        case "systemcore":
          return VENDOR.LIMELIGHT;
        case "navx3":
          return VENDOR.STUDICA;
        case "talonfx":
        case "talonfxs":
        case "cancoder":
        case "pigeon2":
          return VENDOR.CTRE;
        case "sparkmax":
        case "sparkflex":
          return VENDOR.REV;
        case "revthroughbore":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "dio": return VENDOR.SMARTIO;
          }
        case "nova":
          return VENDOR.THRIFTYBOT;
        case "andymarkhexbore":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "dio":
            case "analog": return VENDOR.SMARTIO;
            case "can": return VENDOR.ANDYMARK;
          }
        case "canandgyro": return VENDOR.REDUX;
        case "canandmag":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "dio": return VENDOR.SMARTIO;
            case "can": return VENDOR.REDUX;
          }
        case "srxmag":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "analog": return VENDOR.SMARTIO;
          }
        case "thrifty":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "analog": return VENDOR.SMARTIO;
          }
      }
    }
    return VENDOR.UNKNOWN;
  }

  /**
   * Get the {@link VendorMotorController} object for the device.
   *
   * @return {@link VendorMotorController} object for the device.
   */
  public VendorMotorController getMotorController()
  {
    if (type.contains("_"))
    {
      String[] vendorData           = type.split("_");
      String   vendorType           = vendorData[0];
      String   vendorConnectionType = vendorData[1];
      switch (vendorType)
      {
        case "nova":
          return VendorMotorController.NOVA;
        case "sparkmax":
          return VendorMotorController.SPARKMAX;
        case "sparkflex":
          return VendorMotorController.SPARKFLEX;
        case "talonfx":
          return VendorMotorController.TALONFX;
        case "talonfxs":
          return VendorMotorController.TALONFXS;
      }
    }
    return VendorMotorController.NONE;
  }

  /**
   * Get the Absolute Encoder Supplier and Vendor Absolute Encoder Object.
   *
   * @param angleMotorVendor     Vendor of the angle/steering/azimuth motor controller, used when the
   *                             absolute encoder is attached to the angle motor controller.
   * @param angleMotorController Angle/steering/azimuth {@link SmartMotorController}, used when the
   *                             absolute encoder is attached to the angle motor controller.
   * @param inverted             Inversion of the absolute encoder.
   * @return Pair of {@link Supplier} and Vendor Absolute Encoder {@link Object}
   */
  public Pair<Supplier<Angle>, Object> getAbsoluteEncoder(VendorMotorController angleMotorVendor,
                                                          SmartMotorController angleMotorController, boolean inverted)
  {
    String[] vendorData           = type.split("_");
    String   vendorType           = vendorData[0];
    String   vendorConnectionType = vendorData[1];
    switch (vendorConnectionType)
    {
      case "analog":
      {
        var analogEncoder = new AnalogEncoder(id);
        analogEncoder.setInverted(inverted);
        return Pair.of(() -> Rotations.of(analogEncoder.get()), analogEncoder);
      }
      case "dio":
      {
        var dutyCycleEncoder = new DutyCycleEncoder(id);
        dutyCycleEncoder.setInverted(inverted);
        return Pair.of(() -> Rotations.of(dutyCycleEncoder.get()), dutyCycleEncoder);
      }
      case "attached":
        switch (vendorType)
        {
          case "andymarkhexbore":
          case "canandmag":
          case "revthroughbore":
          case "srxmag":
          case "dutycycle":
            return angleMotorVendor.getAbsoluteEncoder("dutycycle", angleMotorController, inverted);
          case "analog":
            return angleMotorVendor.getAbsoluteEncoder("analog", angleMotorController, inverted);
          case "analog5v":
            return angleMotorVendor.getAbsoluteEncoder("analog5v", angleMotorController, inverted);
          default: throw new IllegalArgumentException("Invalid encoder type: " + vendorType);
        }
      case "can":
        switch (vendorType)
        {
          case "cancoder": return AbsoluteEncoder.CANCODER.getAbsoluteEncoder(id, canbus, inverted);
          case "canandmag": return AbsoluteEncoder.CANANDMAG.getAbsoluteEncoder(id, canbus, inverted);
          case "andymarkhexbore": return AbsoluteEncoder.ANDYMARK.getAbsoluteEncoder(id, canbus, inverted);
          case "splineencoder": return AbsoluteEncoder.SPLINE_ENCODER.getAbsoluteEncoder(id, canbus, inverted);
          default: throw new IllegalArgumentException("Invalid encoder type: " + vendorType);
        }
      default: throw new IllegalArgumentException("Invalid encoder connection type: " + vendorConnectionType);
    }
  }

  /**
   * Get the {@link SmartMotorController} from the {@link DeviceJson} when given the
   * {@link SmartMotorControllerConfig}.
   *
   * @param config {@link SmartMotorControllerConfig} to apply when creating {@link SmartMotorController}.
   * @return {@link SmartMotorController}
   */
  public SmartMotorController getSmartMotorController(SmartMotorControllerConfig config)
  {
    String[] subtypes            = type.split("_");
    String   motorControllerType = subtypes[0].toLowerCase();
    String   motorType           = subtypes[1].toLowerCase();
    DCMotor  motor               = getDCMotor(motorType);
    switch (motorControllerType)
    {
      case "talonfx":
        return VendorMotorController.TALONFX.getMotorController(id, canbus, config, motor);
      case "talonfxs":
        return VendorMotorController.TALONFXS.getMotorController(id, canbus, config, motor);
      case "sparkmax":
        return VendorMotorController.SPARKMAX.getMotorController(id, canbus, config, motor);
      case "sparkflex":
        return VendorMotorController.SPARKFLEX.getMotorController(id, canbus, config, motor);
      case "nova":
        return VendorMotorController.NOVA.getMotorController(id, canbus, config, motor);
      default:
        throw new IllegalArgumentException("Invalid motor controller type: " + motorControllerType);
    }
  }

}
