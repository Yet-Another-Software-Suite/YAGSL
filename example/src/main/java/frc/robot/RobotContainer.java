// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.MilliOhms;
import static org.wpilib.units.Units.Volts;

import com.pathplanner.lib.auto.AutoBuilder;
import frc.robot.subsystems.swervedrive.SwerveDriveSubsystem;
import org.wpilib.command2.Command;
import org.wpilib.command2.Commands;
import org.wpilib.command2.button.CommandXboxController;
import org.wpilib.command2.button.Trigger;
import org.wpilib.driverstation.XboxController;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.interpolation.InterpolatingDoubleTreeMap;
import org.wpilib.tunable.Selectable;
import org.wpilib.tunable.Tunables;
import yams.commands2.swerve.SwerveInputStream;
import yams.core.motorcontrollers.simulation.BatterySim;

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
  // Establish a Selectable that is published as a tunable, allowing selection of desired auto.
  // Built from PathPlanner's AutoBuilder once the swerve subsystem has configured it, so autos discovered in
  // deploy/pathplanner/autos (e.g. "New Auto") show up automatically.
  private final Selectable<Command> autoChooser;

  private final SwerveDriveSubsystem     swerve          = new SwerveDriveSubsystem();
  // YALL (Limelight) and PhotonLib have no 2027_alpha7 vendordep yet, so the vision subsystems are excluded from the
  // build (see build.gradle). Restore these once they do.
//  private final LimelightVisionSubsystem limelightVision = new LimelightVisionSubsystem(swerve);
//  private final PhotonVisionSubsystem    photonVision    = new PhotonVisionSubsystem(swerve);

  // Toggled by a button press to switch the drive stream between angular velocity (right stick X rotates) and
  // heading (right stick X/Y picks the desired heading angle) control.
  private       boolean headingControlEnabled = false;

  private final SwerveInputStream driveStream = swerve.getAngularVelocityStream(driverXbox::getLeftY,
                                                                                 driverXbox::getLeftX,
                                                                                 driverXbox::getLeftTrigger)
                                                       .withControllerHeadingAxis(driverXbox::getRightX,
                                                                                  driverXbox::getRightY)
                                                       .withHeadingControl(() -> headingControlEnabled)
                                                       .withDeadband(0.05)
                                                       .withAllianceRelativeControl();
  public RobotContainer()
  {
    configureBatterySim();

    autoChooser = AutoBuilder.buildAutoChooser();
    Tunables.publish("Auto Chooser", autoChooser);

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
   * named factories in {@link org.wpilib.command2.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link org.wpilib.command2.button.CommandPS4Controller PS4}
   * controllers or {@link org.wpilib.command2.button.CommandJoystick Flight joysticks}.
   */
  private void configureBindings()
  {
    swerve.setDefaultCommand(swerve.drive(driveStream));
    driverXbox.button(XboxController.Button.A).whileTrue(swerve.sysIdModule("frontleft"));
    driverXbox.x().whileTrue(swerve.driveToPointPathPlanner(new Pose2d(Meters.of(3), Meters.of(3), Rotation2d.fromDegrees(180))));
    driverXbox.y().whileTrue(swerve.driveToPointYAMS(new Pose2d(Meters.of(3), Meters.of(3), Rotation2d.fromDegrees(180))));
    driverXbox.menu().and(driverXbox.view()).onTrue(swerve.zeroGyro());
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
