// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MilliOhms;
import static edu.wpi.first.units.Units.Volts;

import com.pathplanner.lib.auto.AutoBuilder;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.swervedrive.SwerveDriveSubsystem;
import frc.robot.subsystems.vision.LimelightVisionSubsystem;
import frc.robot.subsystems.vision.PhotonVisionSubsystem;
import yams.mechanisms.swerve.utility.SwerveInputStream;
import yams.motorcontrollers.simulation.BatterySim;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{

  // Replace with CommandPS4Controller or CommandJoystick if needed
  final         CommandXboxController driverXbox = new CommandXboxController(0);
  // The robot's subsystems and commands are defined here...
  // Establish a Sendable Chooser that will be able to be sent to the SmartDashboard, allowing selection of desired auto
  // Built from PathPlanner's AutoBuilder once the swerve subsystem has configured it, so autos discovered in
  // deploy/pathplanner/autos (e.g. "New Auto") show up automatically.
  private final SendableChooser<Command> autoChooser;

  private final SwerveDriveSubsystem     swerve          = new SwerveDriveSubsystem();
  private final LimelightVisionSubsystem limelightVision = new LimelightVisionSubsystem(swerve);
  private final PhotonVisionSubsystem    photonVision    = new PhotonVisionSubsystem(swerve);

  // Toggled by a button press to switch the drive stream between angular velocity (right stick X rotates) and
  // heading (right stick X/Y picks the desired heading angle) control.
  private       boolean headingControlEnabled = false;

  private final SwerveInputStream driveStream = swerve.getAngularVelocityStream(driverXbox::getLeftY,
                                                                                 driverXbox::getLeftX,
                                                                                 ()->driverXbox.getRawAxis(2))
                                                       .withControllerHeadingAxis(driverXbox::getRightX,
                                                                                  driverXbox::getRightY)
                                                       .withHeadingControl(() -> headingControlEnabled)
                                                       .withDeadband(0.05)
                                                       .withAllianceRelativeControl();
  public RobotContainer()
  {
    configureBatterySim();

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);

    configureBindings();
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
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */
  private void configureBindings()
  {
    swerve.setDefaultCommand(swerve.drive(driveStream));
    driverXbox.button(1).whileTrue(swerve.sysIdModule("frontleft"));
    driverXbox.x().whileTrue(swerve.driveToPointPathPlanner(new Pose2d(Meters.of(3), Meters.of(3), Rotation2d.fromDegrees(180))));
    driverXbox.y().whileTrue(swerve.driveToPointYAMS(new Pose2d(Meters.of(3), Meters.of(3), Rotation2d.fromDegrees(180))));
    driverXbox.start().and(driverXbox.back()).onTrue(swerve.zeroGyro());
    driverXbox.a().toggleOnTrue(Commands.startEnd(() -> headingControlEnabled = true, () -> headingControlEnabled = false));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand()
  {
    // Pass in the selected auto from the SmartDashboard as our desired autnomous commmand
    return autoChooser.getSelected();
  }

}
