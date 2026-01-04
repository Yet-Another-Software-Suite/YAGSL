package swervelib.parser.json.modules;


/**
 * Angle motor conversion factors composite JSON parse class.
 */
public class AngleConversionFactorsJson
{

  /**
   * Reduction ratio for the motor to the wheel. X where "X:1"
   */
  public double gearRatio;

  public boolean equals(DriveConversionFactorsJson o)
  {
    return o.gearRatio == gearRatio;
  }
}
