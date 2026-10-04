package swervelib.core.parser.json.modules;

import io.avaje.jsonb.Json;

/**
 * Angle motor conversion factors composite JSON parse class.
 */
@Json
public class AngleGearingJson
{

  /**
   * Reduction ratio for the motor to the wheel. X where "X:1"
   */
  public double gearRatio;

  /**
   * Compare the gear ratio of this object to another.
   *
   * @param o Other {@link DriveGearingJson} to compare against.
   * @return True if the gear ratios are equal.
   */
  public boolean equals(DriveGearingJson o)
  {
    return o.gearRatio == gearRatio;
  }
}
