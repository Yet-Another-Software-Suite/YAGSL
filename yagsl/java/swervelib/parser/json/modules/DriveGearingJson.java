package swervelib.parser.json.modules;

/**
 * Drive motor composite JSON parse class.
 */
public class DriveGearingJson
{

  /**
   * Reduction ratio for the motor to the wheel. X where "X:1"
   */
  public double gearRatio;
  /**
   * Diameter of the wheel in inches.
   */
  public double diameter;

  /**
   * Compare the gear ratio and wheel diameter of this object to another.
   *
   * @param o Other {@link DriveGearingJson} to compare against.
   * @return True if the gear ratios and diameters are equal.
   */
  public boolean equals(DriveGearingJson o)
  {
    return o.gearRatio == gearRatio && o.diameter == diameter;
  }
}
