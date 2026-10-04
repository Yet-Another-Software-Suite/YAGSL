package swervelib.core.parser.json.modules;

import io.avaje.jsonb.Json;

/**
 * Inverted motor JSON parsed class. Used to access the JSON data.
 */
@Json
public class BoolMotorJson
{

  /**
   * Drive motor inversion state.
   */
  public boolean drive;
  /**
   * Angle motor inversion state.
   */
  public boolean angle;
}
