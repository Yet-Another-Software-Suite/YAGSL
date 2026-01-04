package swervelib.parser.json.modules;

/**
 * Drive motor composite JSON parse class.
 */
public class DriveConversionFactorsJson
{

  /**
   * Reduction ratio for the motor to the wheel. X where "X:1"
   */
  public double gearRatio;
  /**
   * Diameter of the wheel in inches.
   */
  public double diameter;

  public boolean equals(DriveConversionFactorsJson o)
  {
    return o.gearRatio == gearRatio && o.diameter == diameter;
  }
}
