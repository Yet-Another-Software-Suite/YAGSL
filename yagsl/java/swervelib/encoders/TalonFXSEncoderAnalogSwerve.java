package swervelib.encoders;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Rotations;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXSConfiguration;
import com.ctre.phoenix6.configs.TalonFXSConfigurator;
import com.ctre.phoenix6.hardware.TalonFXS;
import com.ctre.phoenix6.signals.ExternalFeedbackSensorSourceValue;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Voltage;
import swervelib.motors.SwerveMotor;
import swervelib.motors.TalonFXSSwerve;

/**
 * Swerve Absolute Encoder for CTRE TalonFXS.
 */
public class TalonFXSEncoderAnalogSwerve extends SwerveAbsoluteEncoder
{

  /**
   *
   * Reference to a TalonFXS for polling its attached absolute encoder.
   */
  private final TalonFXS talonFXS;
  /**
   * Analog absolute encoder voltage.
   */
  private final StatusSignal<Voltage> analogVoltage;
  /**
   * Offset of the absolute encoder.
   */
  private       Angle                 offset = Degrees.of(0);
  /**
   * Inverted state of the encoder.
   */
  private boolean inverted = false;

  public TalonFXSEncoderAnalogSwerve(SwerveMotor motor)
  {
    if(!(motor instanceof TalonFXSSwerve))
    {
      throw new IllegalArgumentException("Motor must be a TalonFXSSwerve");
    }
    talonFXS = (TalonFXS)motor.getMotor();
    analogVoltage = talonFXS.getAnalogVoltage();
  }


  @Override
  public void close()
  {
    // Do nothing
  }

  @Override
  public void factoryDefault()
  {
    // Do nothing
  }

  @Override
  public void clearStickyFaults()
  {
    // Do nothing
  }

  @Override
  public void configure(boolean inverted)
  {
    this.inverted = inverted;
  }

  @Override
  public double getAbsolutePosition()
  {
    var angle = Rotations.of(analogVoltage.refresh().getValueAsDouble() / 3.3);
    return (inverted ? Rotations.of(1).minus(angle) : angle).minus(offset).in(Degrees);
  }

  @Override
  public Object getAbsoluteEncoder()
  {
    return analogVoltage;
  }

  @Override
  public boolean setAbsoluteEncoderOffset(double offset)
  {
    this.offset = Degrees.of(offset);
    return true;
  }

  @Override
  public double getVelocity()
  {
    return 0;
  }
}
