package swervelib.parser.json;

import static edu.wpi.first.units.Units.Kilogram;
import static edu.wpi.first.units.Units.Pounds;

import swervelib.parser.json.modules.ConversionFactorsJson;

/**
 * {@link swervelib.parser.SwerveModulePhysicalCharacteristics} parsed data. Used to configure the SwerveModule.
 */
public class PhysicalPropertiesJson
{
  /**
   * Conversion Factors composition. Auto-calculates the conversion factors.
   */
  public ConversionFactorsJson conversionFactors              = new ConversionFactorsJson();
  /**
   * The current limit in AMPs to apply to the motors.
   */
  public MotorConfigInt        statorCurrentLimit                   = new MotorConfigInt(40, 20);

}

