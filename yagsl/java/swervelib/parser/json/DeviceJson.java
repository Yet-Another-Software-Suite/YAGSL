package swervelib.parser.json;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.revrobotics.spark.SparkAbsoluteEncoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.thethriftybot.devices.ThriftyNova;
import com.thethriftybot.devices.ThriftyNova.ExternalEncoder;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import yams.motorcontrollers.SmartMotorController;
import yams.motorcontrollers.SmartMotorControllerConfig;
import yams.motorcontrollers.local.NovaWrapper;
import yams.motorcontrollers.local.SparkWrapper;
import yams.motorcontrollers.remote.TalonFXSWrapper;
import yams.motorcontrollers.remote.TalonFXWrapper;

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

  public enum VENDOR
  {CTRE, REV, THRIFTYBOT, ANDYMARK, REDUX, STUDICA, SMARTIO, LIMELIGHT, UNKNOWN}


  /**
   * Get the vendor of the device.
   *
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
        case "navx":
        case "navx2":
        case "navx3":
          return VENDOR.STUDICA;
        case "talonfx":
        case "talonfxs":
        case "cancoder":
        case "pigeon1":
        case "pigeon2":
          return VENDOR.CTRE;
        case "sparkmax":
        case "sparkflex":
          return VENDOR.REV;
        case "revthroughbore":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "pwm": return VENDOR.SMARTIO;
          }
        case "nova":
          return VENDOR.THRIFTYBOT;
        case "andymarkhexbore":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "pwm": return VENDOR.SMARTIO;
            case "can": return VENDOR.ANDYMARK;
          }
        case "canandgyro": return VENDOR.REDUX;
        case "canandmag":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "pwm": return VENDOR.SMARTIO;
            case "can": return VENDOR.REDUX;
          }
        case "srxmag":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "pwm": return VENDOR.SMARTIO;
          }
        case "thrifty":
          switch (vendorConnectionType)
          {
            case "attached": return attachedType;
            case "pwm": return VENDOR.SMARTIO;
          }
      }
    }
    return VENDOR.UNKNOWN;
  }

//  public Canandmag getReduxEncoder()
//  {
//    String[] vendorData           = type.split("_");
//    String   vendorType           = vendorData[0];
//    String   vendorConnectionType = vendorData[1];
//    switch (vendorType)
//    {
//      case "canandmag": return new Canandmag(id);
//      default: throw new IllegalArgumentException("Invalid encoder type: " + vendorType);
//    }
//  }

  /**
   * Get the CTRE encoder. (only {@link CANcoder}s are supported)
   *
   * @return {@link CANcoder}
   */
  public CANcoder getCTREEncoder()
  {
    String[] vendorData           = type.split("_");
    String   vendorType           = vendorData[0];
    String   vendorConnectionType = vendorData[1];
    switch (vendorType)
    {
      case "cancoder": return new CANcoder(id);
      default: throw new IllegalArgumentException("Invalid encoder type: " + vendorType);
    }
  }

  /**
   * Get the ThriftyBot encoder type.
   *
   * @return ThriftyBot encoder type.
   */
  public ExternalEncoder getThriftyEncoder()
  {
    String[] vendorData           = type.split("_");
    String   vendorType           = vendorData[0];
    String   vendorConnectionType = vendorData[1];
    switch (vendorType)
    {
      case "canandmag": return ExternalEncoder.REDUX_ENCODER;
      case "revthroughbore": return ExternalEncoder.REV_ENCODER;
      case "srxmag": return ExternalEncoder.SRX_MAG_ENCODER;
      default: throw new IllegalArgumentException("Invalid encoder type: " + vendorType);
    }
  }

  /**
   * Get the Spark encoder.
   *
   * @param vendorMotorController {@link SparkMax} or {@link SparkFlex} vendor motor controller
   * @return {@link SparkAbsoluteEncoder}
   */
  public SparkAbsoluteEncoder getSparkEncoder(Object vendorMotorController)
  {
    if (vendorMotorController instanceof SparkBase) {return ((SparkBase) vendorMotorController).getAbsoluteEncoder();}
    throw new IllegalArgumentException(
        "Invalid vendor motor controller type: " + vendorMotorController.getClass().getSimpleName());
  }

  /**
   * SmartIO Encoder.
   *
   * @return {@link DutyCycleEncoder}
   */
  public DutyCycleEncoder getSmartIOEncoder()
  {
    return new DutyCycleEncoder(channel);
  }

  /**
   * AndyMark Hex Bore Encoder.
   *
   * @return Object
   */
  public Object getAndyMarkEncoder()
  {
    throw new UnsupportedOperationException("AndyMark hex bore encoder are not yet supported.");
  }

  /**
   * Get the {@link SmartMotorController} from the {@link DeviceJson} when given the
   * {@link SmartMotorControllerConfig}.
   *
   * @param config                {@link SmartMotorControllerConfig} to apply when creating
   *                              {@link SmartMotorController}.
   * @param vendorMotorController Vendor motor controller.
   * @return {@link SmartMotorController}
   */
  public SmartMotorController getMotorController(SmartMotorControllerConfig config, Object vendorMotorController)
  {
    String[] subtypes            = type.split("_");
    String   motorControllerType = subtypes[0].toLowerCase();
    String   motorType           = subtypes[1].toLowerCase();
    DCMotor  motor               = getDCMotor(motorType);
    if (vendorMotorController == null)
    {
      vendorMotorController = getVendorMotorController();
    }
    if (vendorMotorController instanceof TalonFX)
    {
      return new TalonFXWrapper((TalonFX) vendorMotorController, motor, config);
    } else if (vendorMotorController instanceof TalonFXS)
    {
      return new TalonFXSWrapper((TalonFXS) vendorMotorController, motor, config);
    } else if (vendorMotorController instanceof SparkMax)
    {
      return new SparkWrapper((SparkMax) vendorMotorController, motor, config);
    } else if (vendorMotorController instanceof SparkFlex)
    {
      return new SparkWrapper((SparkFlex) vendorMotorController, motor, config);
    } else if (vendorMotorController instanceof ThriftyNova)
    {
      return new NovaWrapper((ThriftyNova) vendorMotorController, motor, config);
    }
    throw new IllegalArgumentException("Invalid motor controller type: " + motorControllerType);
  }

  /**
   * Get the vendor motor controller.
   *
   * @return Vendor motor controller.
   */
  public Object getVendorMotorController()
  {
    String[] subtypes            = type.split("_");
    String   motorControllerType = subtypes[0].toLowerCase();
    String   motorType           = subtypes[1].toLowerCase();
    switch (motorControllerType)
    {
      case "talonfx":
        return new TalonFX(id, new CANBus(canbus));
      case "talonfxs":
        return new TalonFXS(id, new CANBus(canbus));
      case "sparkmax":
        return new SparkMax(id, MotorType.kBrushless);
      case "sparkflex":
        return new SparkFlex(id, MotorType.kBrushless);
      case "nova":
        switch (motorType)
        {
          case "neo":
          case "neo2":
          case "neo550":
          case "vortex":
            return new ThriftyNova(id, ThriftyNova.MotorType.NEO);
          case "minion":
            return new ThriftyNova(id, ThriftyNova.MotorType.MINION);
        }
      default:
        throw new IllegalArgumentException("Invalid motor controller type: " + motorControllerType);
    }
  }


}
