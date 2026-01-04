package swervelib.parser.json.modules;

/**
 * Conversion Factors parsed JSON class
 */
public class ConversionFactorsJson
{

  /**
   * Drive motor conversion factors composition.
   */
  public DriveConversionFactorsJson drive = new DriveConversionFactorsJson();
  /**
   * Angle motor conversion factors composition.
   */
  public AngleConversionFactorsJson angle = new AngleConversionFactorsJson();

}
