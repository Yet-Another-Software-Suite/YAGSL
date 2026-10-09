package swervelib.core.parser.json.modules;

import io.avaje.jsonb.Json;

/**
 * Conversion Factors parsed JSON class
 */
@Json
public class GearingJson
{

  /**
   * Drive motor conversion factors composition.
   */
  public DriveGearingJson drive = new DriveGearingJson();
  /**
   * Angle motor conversion factors composition.
   */
  public AngleGearingJson angle = new AngleGearingJson();

}
