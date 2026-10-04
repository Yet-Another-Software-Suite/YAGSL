package swervelib.core.parser.json;

import io.avaje.jsonb.Json;

import swervelib.core.parser.PIDFConfig;

/**
 * {@link yams.core.mechanisms.swerve.SwerveModule} PID with Feedforward for the drive motor and angle motor.
 */
@Json
public class PIDFPropertiesJson
{

  /**
   * The PIDF with Integral Zone used for the drive motor.
   */
  public PIDFConfig drive;
  /**
   * The PIDF with Integral Zone used for the angle motor.
   */
  public PIDFConfig angle;
}
