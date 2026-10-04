// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static org.wpilib.units.Units.MilliOhms;
import static org.wpilib.units.Units.Volts;

import frc.robot.mechanisms.SwerveDriveMechanism;
import org.wpilib.command3.Scheduler;
import org.wpilib.command3.button.CommandXboxController;
import org.wpilib.driverstation.internal.DriverStationBackend;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.math.interpolation.InterpolatingDoubleTreeMap;
import yams.core.motorcontrollers.simulation.BatterySim;

/**
 * Holds the robot's mechanisms and the driver controller shared by the opmodes in {@code frc.robot.opmodes}.
 * {@link OpModeRobot} finds the {@code @Teleop} and {@code @Autonomous} opmodes in this package (and its subpackages)
 * and constructs the one selected on the driver station.
 */
public class Robot extends OpModeRobot
{

  /**
   * Swerve drive built by YAGSL from {@code deploy/swerve/base}.
   */
  public final SwerveDriveMechanism  swerve     = new SwerveDriveMechanism();
  /**
   * Driver controller.
   */
  public final CommandXboxController driverXbox = new CommandXboxController(0);

  private final Scheduler scheduler = Scheduler.getDefault();

  /**
   * This function is run when the robot is first started up and should be used for any initialization code. How the
   * robot drives is up to the selected opmode: each teleop opmode runs its own drive command, and each autonomous
   * opmode its own routine.
   */
  public Robot()
  {
    configureBatterySim();

    if (isSimulation())
    {
      DriverStationBackend.silenceJoystickConnectionAlert(true);
    }
  }

  /**
   * Configure the simulated battery's state-of-charge curve and discharge behavior.
   */
  private void configureBatterySim()
  {
    InterpolatingDoubleTreeMap wornBatteryCurve = new InterpolatingDoubleTreeMap();
    wornBatteryCurve.put(0.00, 6.0);
    wornBatteryCurve.put(0.05, 6.5);
    wornBatteryCurve.put(0.10, 9.5);
    wornBatteryCurve.put(0.20, 10.2);
    wornBatteryCurve.put(0.40, 10.6);
    wornBatteryCurve.put(0.60, 10.9);
    wornBatteryCurve.put(0.80, 11.2);
    wornBatteryCurve.put(0.90, 11.4);
    wornBatteryCurve.put(1.00, 11.6);
    BatterySim.replaceSOCInterpolation(wornBatteryCurve);
    BatterySim.enableDischarge(18, Volts.of(11), MilliOhms.of(20));
  }

  /**
   * This function is called every 20 ms, no matter the mode. Mechanisms have no periodic() hook in Commands v3, so
   * the swerve drive's update runs here, before the scheduler.
   */
  @Override
  public void robotPeriodic()
  {
    swerve.periodic();
    scheduler.run();
  }

  /**
   * This function is called periodically whilst in simulation.
   */
  @Override
  public void simulationPeriodic()
  {
    swerve.simulationPeriodic();
  }
}
