package swervelib.parser.json;

import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.util.Units;
import swervelib.encoders.SparkMaxEncoderSwerve;
import swervelib.encoders.SwerveAbsoluteEncoder;
import swervelib.encoders.ThriftyNovaEncoderSwerve;
import swervelib.motors.SwerveMotor;
import swervelib.motors.ThriftyNovaSwerve;
import swervelib.parser.PIDFConfig;
import swervelib.parser.json.modules.BoolMotorJson;
import swervelib.parser.json.modules.ConversionFactorsJson;
import swervelib.parser.json.modules.LocationJson;

/**
 * {@link swervelib.SwerveModule} JSON parsed class. Used to access the JSON data.
 */
public class ModuleJson
{

  /**
   * Drive motor device configuration.
   */
  public DeviceJson            drive;
  /**
   * Angle motor device configuration.
   */
  public DeviceJson            angle;
  /**
   * Conversion Factors composition. Auto-calculates the conversion factors.
   */
  public ConversionFactorsJson conversionFactors       = new ConversionFactorsJson();
  /**
   * Absolute encoder device configuration.
   */
  public DeviceJson            encoder;
  /**
   * Defines which motors are inverted.
   */
  public BoolMotorJson         inverted;
  /**
   * Absolute encoder offset from 0 in degrees.
   */
  public double                absoluteEncoderOffset;
  /**
   * Absolute encoder inversion state.
   */
  public boolean               absoluteEncoderInverted = false;
  /**
   * The location of the swerve module from the center of the robot in inches.
   */
  public LocationJson          location;
  /**
   * Should do cosine compensation when not pointing correct direction;.
   */
  public boolean               useCosineCompensator    = true;


}
