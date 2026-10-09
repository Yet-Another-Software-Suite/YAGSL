package swervelib.core.parser.json;

import io.avaje.jsonb.Json;

import swervelib.core.parser.json.modules.BoolMotorJson;
import swervelib.core.parser.json.modules.GearingJson;
import swervelib.core.parser.json.modules.LocationJson;

/**
 * {@link yams.core.mechanisms.swerve.SwerveModule} JSON parsed class. Used to access the JSON data.
 */
@Json
public class ModuleJson
{

  /**
   * Drive motor device configuration.
   */
  public DeviceJson            drive;
  /**
   * Angle motor device configuration.
   */
  public DeviceJson  angle;
  /**
   * Conversion Factors composition. Auto-calculates the conversion factors.
   */
  public GearingJson   gearing = new GearingJson();
  /**
   * Absolute encoder device configuration.
   */
  public DeviceJson    absoluteEncoder;
  /**
   * Defines which motors are inverted.
   */
  public BoolMotorJson inverted;
  /**
   * Absolute encoder offset from 0 in degrees.
   */
  public double                absoluteEncoderOffset;
  /**
   * Absolute encoder inversion state.
   */
  public boolean               absoluteEncoderInverted = false;
  /**
   * Reduction ratio for the absolute encoder to the motor. X where "X:1"
   */
  public double absoluteEncoderGearRatio = 1;
  /**
   * The location of the swerve module from the center of the robot in inches.
   */
  public LocationJson          location;


}
