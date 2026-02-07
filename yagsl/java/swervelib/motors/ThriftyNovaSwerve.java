// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package swervelib.motors;

import com.thethriftybot.devices.ThriftyNova;
import com.thethriftybot.devices.ThriftyNova.CurrentType;
import com.thethriftybot.devices.ThriftyNova.EncoderType;
import com.thethriftybot.devices.ThriftyNova.PIDSlot;
import com.thethriftybot.util.Conversion;
import com.thethriftybot.util.Conversion.PositionUnit;
import com.thethriftybot.util.Conversion.VelocityUnit;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.RobotBase;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import swervelib.encoders.SwerveAbsoluteEncoder;
import swervelib.parser.PIDFConfig;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;

/**
 * An implementation of {@link ThriftyNova} as a {@link SwerveMotor}.
 */
public class ThriftyNovaSwerve extends SwerveMotor
{

  /**
   * {@link DCMotor} for simulation and calculations.
   */
  private final DCMotor                         simMotor;
  /**
   * Closed-loop PID controller.
   */
  public        PIDController                   pid;
  /**
   * ThriftyNova Instance.
   */
  private       ThriftyNova                     motor;
  /**
   * Factory default already occurred.
   */
  private       boolean                         factoryDefaultOccurred   = false;
  /**
   * Position conversion object for the motor encoder
   */
  private       Conversion                      positionConversion;
  /**
   * Velocity conversion object for the motor encoder
   */
  private       Conversion                      velocityConversion;
  /**
   * The position conversion factor for the encoder
   */
  private       double                          positionConversionFactor = 1.0;
  /**
   * The position conversion factor for the encoder
   */
  private       double                          velocityConversionFactor = 1.0 / 60.0;
  /**
   * Absolute encoder attached to the SparkMax (if exists)
   */
  private       Optional<SwerveAbsoluteEncoder> absoluteEncoder          = Optional.empty();
  /**
   * Supplier for the velocity of the motor controller.
   */
  private       Supplier<Double>                velocity;
  /**
   * Supplier for the position of the motor controller.
   */
  private       Supplier<Double>                position;

  /**
   * Initialize the swerve motor.
   *
   * @param motor        The SwerveMotor as a ThriftyNova object.
   * @param isDriveMotor Is the motor being initialized a drive motor?
   * @param motorType    {@link DCMotor} controlled by the {@link ThriftyNova}
   */
  public ThriftyNovaSwerve(ThriftyNova motor, boolean isDriveMotor, DCMotor motorType)
  {
    this.motor = motor;
    this.isDriveMotor = isDriveMotor;
    this.simMotor = motorType;
    factoryDefaults();
    clearStickyFaults();

    motor.usePIDSlot(PIDSlot.SLOT0);
    pid = new PIDController(0, 0, 0);
    if (RobotBase.isReal())
    {motor.pid0.setPID(pid);}

    if (isDriveMotor)
    {
      positionConversion = new Conversion(PositionUnit.ROTATIONS, EncoderType.INTERNAL);
      velocityConversion = new Conversion(VelocityUnit.ROTATIONS_PER_SEC, EncoderType.INTERNAL);
    } else
    {
      positionConversion = new Conversion(PositionUnit.ROTATIONS, EncoderType.INTERNAL);
      velocityConversion = new Conversion(VelocityUnit.ROTATIONS_PER_SEC, EncoderType.INTERNAL);
    }

    position = this::getConvertedPosition;
    velocity = this::getConvertedVelocity;
  }

  /**
   * Initialize the {@link SwerveMotor} as a {@link ThriftyNova} connected to a Brushless Motor.
   *
   * @param id           CAN ID of the ThriftyNova.
   * @param isDriveMotor Is the motor being initialized a drive motor?
   * @param motor        {@link DCMotor} controlled by the {@link ThriftyNova}
   */
  public ThriftyNovaSwerve(int id, boolean isDriveMotor, DCMotor motor)
  {
    this(new ThriftyNova(id), isDriveMotor, motor);
  }

  /**
   * Close the motor controller and release any resources it may have acquired.
   *
   * @throws RuntimeException If an exception is thrown while closing the motor.
   */
  @Override
  public void close()
  {
    try
    {
      motor.close();
    } catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private double getConvertedPosition()
  {
    double motorPosition     = motor.getPosition();
    double convertedPosition = motorPosition / positionConversionFactor;
    return convertedPosition;
  }

  /**
   * Get the velocity of the motor in meters per second or degrees per second after conversion.
   *
   * @return velocity in meters per second or degrees per second.
   */
  private double getConvertedVelocity()
  {
    double motorVelocity     = motor.getVelocity();
    double convertedVelocity = motorVelocity / velocityConversionFactor;

    return convertedVelocity;
  }

  /**
   * Set factory defaults on the motor controller.
   */
  @Override
  public void factoryDefaults()
  {
    // Factory defaults from https://docs.thethriftybot.com/thrifty-nova/gqCPUYXcVoOZ4KW3DqIr/software-resources/configure-controller-settings/factory-default
    if (!factoryDefaultOccurred)
    {
      if (RobotBase.isReal())
      {
        motor.factoryReset();
      }
      factoryDefaultOccurred = true;
    }
  }

  /**
   * Clear the sticky faults on the motor controller.
   */
  @Override
  public void clearStickyFaults()
  {
    if (RobotBase.isReal())
    {
      motor.clearErrors();
    }
  }

  /**
   * Set the absolute encoder to be a compatible absolute encoder.
   *
   * @param encoder The encoder to use.
   * @return The {@link SwerveMotor} for easy instantiation.
   */
  @Override
  public SwerveMotor setAbsoluteEncoder(SwerveAbsoluteEncoder encoder)
  {
    if (encoder == null)
    {
      motor.useEncoderType(EncoderType.INTERNAL);
      position = this::getConvertedPosition;
      velocity = this::getConvertedVelocity;
    } else
    {
      absoluteEncoder = Optional.of(encoder);
      position = absoluteEncoder.get()::getAbsolutePosition;
      velocity = absoluteEncoder.get()::getVelocity;
      motor.useEncoderType(EncoderType.ABS);
    }
    return this;
  }

  /**
   * Configure the integrated encoder for the ThriftyNova swerve module. Sets the conversion factors for position and
   * velocity, and configures the CAN status frames.
   *
   * @param positionConversionFactor The conversion factor to apply for position.
   */
  @Override
  public void configureIntegratedEncoder(double positionConversionFactor)
  {
    this.positionConversionFactor = positionConversionFactor;
    this.velocityConversionFactor = positionConversionFactor;

    motor.useEncoderType(EncoderType.INTERNAL);
    configureCANStatusFrames(0.25, 0.01, 0.01, 0.02, 0.20);
  }

  /**
   * Set the CAN status frames.
   *
   * @param fault      Fault transmission rate
   * @param sensor     Sensor transmission rate
   * @param quadSensor External quad encoder transmission rate
   * @param control    Control frame transmission rate
   * @param current    Current feedback transmission rate
   */
  public void configureCANStatusFrames(
      double fault, double sensor, double quadSensor, double control, double current)
  {
    if (RobotBase.isReal())
    {
      motor.canFreq.setFault(fault);
      motor.canFreq.setSensor(sensor);
      motor.canFreq.setQuadSensor(quadSensor);
      motor.canFreq.setControl(control);
      motor.canFreq.setCurrent(current);
      checkErrors("Configuring CAN status frames failed: ");
    }
  }

  /**
   * Configure the PIDF values for the closed loop controller. 0 is disabled or off.
   *
   * @param config Configuration class holding the PIDF values.
   */
  @Override
  public void configurePIDF(PIDFConfig config)
  {
    if (RobotBase.isReal())
    {
      motor.pid0.setP(config.p).setI(config.i).setD(config.d);
      motor.usePIDSlot(PIDSlot.SLOT0);
      checkErrors("Configuring PIDF failed: ");
    }
  }

  /**
   * Configure the PID wrapping for the position closed loop controller.
   *
   * @param minInput Minimum PID input.
   * @param maxInput Maximum PID input.
   */
  @Override
  public void configurePIDWrapping(double minInput, double maxInput)
  {
    // Do nothing
  }

  /**
   * Disable PID Wrapping on the motor.
   */
  @Override
  public void disablePIDWrapping()
  {
    // Do nothing
  }

  /**
   * Set the idle mode.
   *
   * @param isBrakeMode Set the brake mode.
   */
  @Override
  public void setMotorBrake(boolean isBrakeMode)
  {
    if (RobotBase.isReal())
    {
      motor.setBrakeMode(isBrakeMode);
      checkErrors("Setting motor brake mode failed: ");
    }
  }

  /**
   * Set the motor to be inverted.
   *
   * @param inverted State of inversion.
   */
  @Override
  public void setInverted(boolean inverted)
  {
    if (RobotBase.isReal())
    {
      motor.setInverted(inverted);
      checkErrors("Setting motor inversion failed: ");
    }
  }

  /**
   * Save the configurations from flash to EEPROM.
   */
  @Override
  public void burnFlash()
  {
    // Do nothing
  }

  /**
   * Set the percentage output.
   *
   * @param percentOutput percent out for the motor controller.
   */
  @Override
  public void set(double percentOutput)
  {
    motor.setPercent(percentOutput);
  }

  /**
   * Set the closed loop PID controller reference point.
   *
   * @param setpoint    Setpoint in MPS or Angle in degrees.
   * @param feedforward Feedforward in volt-meter-per-second or kV.
   */
  @Override
  public void setReference(double setpoint, double feedforward)
  {
    setReference(setpoint, feedforward, getPosition());
  }

  /**
   * Set the closed loop PID controller reference point.
   *
   * @param setpoint    Setpoint in meters per second or angle in degrees.
   * @param feedforward Feedforward in volt-meter-per-second or kV.
   * @param position    Only used on the angle motor, the position of the motor in degrees.
   */
  @Override
  public void setReference(double setpoint, double feedforward, double position)
  {
    if (RobotBase.isReal())
    {
      if (isDriveMotor)
      {
        double convertedSetpoint = setpoint / velocityConversionFactor;
        double motorSetpoint     = velocityConversion.toMotor(convertedSetpoint);
        motor.setVelocity(motorSetpoint, feedforward);
      } else
      {
        double convertedSetpoint = absoluteEncoder.map(it -> setpoint).orElse(setpoint / positionConversionFactor);
        double motorSetpoint     = positionConversion.toMotor(convertedSetpoint);
        motor.setPosition(motorSetpoint);
      }
    }
  }

  /**
   * Get the voltage output of the motor controller.
   *
   * @return Voltage output.
   */
  @Override
  public double getVoltage()
  {
    return motor.getVoltage();
  }

  /**
   * Set the voltage of the motor.
   *
   * @param voltage Voltage to set.
   */
  @Override
  public void setVoltage(double voltage)
  {
    motor.setVoltage(voltage);
  }

  /**
   * Get the voltage output of the motor controller.
   *
   * @return Voltage output.
   */
  @Override
  public double getAppliedOutput()
  {
    return motor.getStatorCurrent();
  }

  /**
   * Get the velocity of the integrated encoder.
   *
   * @return velocity in Meters Per Second, or Degrees per Second.
   */
  @Override
  public double getVelocity()
  {
    return velocity.get();
  }

  /**
   * Get the position of the integrated encoder.
   *
   * @return Position in Meters or Degrees.
   */
  @Override
  public double getPosition()
  {
    return position.get();
  }

  /**
   * Set the integrated encoder position.
   *
   * @param position Integrated encoder position. Should be angle in degrees or meters.
   */
  @Override
  public void setPosition(double position)
  {
    if (!absoluteEncoder.isPresent())
    {
      double convertedPosition = position / positionConversionFactor;
      double motorPosition     = positionConversion.toMotor(convertedPosition);
      motor.setEncoderPosition(motorPosition);
    }
  }

  /**
   * Set the voltage compensation for the swerve module motor.
   *
   * @param nominalVoltage Nominal voltage for operation to output to.
   */
  @Override
  public void setVoltageCompensation(double nominalVoltage)
  {
    motor.setVoltageCompensation(nominalVoltage);
  }

  /**
   * Set the current limit for the swerve drive motor, remember this may cause jumping if used in conjunction with
   * voltage compensation. This is useful to protect the motor from current spikes.
   *
   * @param currentLimit Current limit in AMPS at free speed.
   */
  @Override
  public void setCurrentLimit(int currentLimit)
  {
    if (RobotBase.isReal())
    {
      motor.setMaxCurrent(CurrentType.STATOR, currentLimit);
      checkErrors("Setting current limit failed: ");
    }
  }

  /**
   * Set the maximum rate the open/closed loop output can change by.
   *
   * @param rampRate Time in seconds to go from 0 to full throttle.
   */
  @Override
  public void setLoopRampRate(double rampRate)
  {
    if (RobotBase.isReal())
    {
      motor.setRampUp(rampRate);
      motor.setRampDown(rampRate);
      checkErrors("Setting loop ramp rate failed: ");
    }
  }

  /**
   * Get the motor object from the module.
   *
   * @return Motor object.
   */
  @Override
  public Object getMotor()
  {
    return motor;
  }

  /**
   * Queries whether the absolute encoder is directly attached to the motor controller.
   *
   * @return connected absolute encoder state.
   */
  @Override
  public boolean usingExternalFeedbackSensor()
  {
    return absoluteEncoder.isPresent();
  }

  /**
   * Checks for errors in the motor and logs them if any are found.
   *
   * @param message the message to prepend to the log and print statement
   */
  private void checkErrors(String message)
  {
    if (SwerveDriveTelemetry.verbosity.ordinal() >= TelemetryVerbosity.HIGH.ordinal())
    {
      List<ThriftyNova.Error> errors = motor.getErrors();
      if (errors.size() > 0)
      {
        for (ThriftyNova.Error error : errors)
        {
          if (SwerveDriveTelemetry.verbosity.ordinal() >= TelemetryVerbosity.MACHINE.ordinal())
          {
            System.out.println(this.getClass().getSimpleName() + ": " + message + error.toString());
          }
          DataLogManager.log(this.getClass().getSimpleName() + ": " + message + error.toString());
        }
      }
      motor.clearErrors();
    }
  }

  /**
   * Get the simulated {@link DCMotor} associated with this motor controller.
   *
   * @return Simulated {@link DCMotor} instance.
   */
  @Override
  public DCMotor getSimMotor()
  {
    return simMotor;
  }
}
