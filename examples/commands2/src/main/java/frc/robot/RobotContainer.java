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
import org.wpilib.command2.button.RobotModeTriggers;
import org.wpilib.command2.button.Trigger;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchState;
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
  // YALL (Limelight) and PhotonLib have no 2027_alpha7 vendordep yet, so the vision subsystems live in the example's
  // disabled-vision/ folder, outside the build. Move them back into java/ and restore these once they do.
//  private final LimelightVisionSubsystem limelightVision = new LimelightVisionSubsystem(swerve);
//  private final PhotonVisionSubsystem    photonVision    = new PhotonVisionSubsystem(swerve);

  // Toggled with the A button to switch the drive stream between angular velocity control (right stick X spins the
  // robot) and heading control (the right stick points the way the robot should face).
  private       boolean headingControlEnabled = false;

  // Field relative drive stream. Xbox sticks read negative when pushed forward/left, so every axis is negated:
  //  - translation: left stick forward drives away from the driver station, left drives left.
  //  - angular velocity: right stick right spins clockwise (YAMS' positive rotation is counter-clockwise).
  //  - heading: the right stick points the direction to face, so up faces away from the driver station.
  // Translation is alliance relative (flipped on red) by YAMS; the heading axes are flipped here to match, since a
  // field relative heading is not.
  private final SwerveInputStream driveStream = swerve.getAngularVelocityStream(() -> -driverXbox.getLeftY(),
                                                                                 () -> -driverXbox.getLeftX(),
                                                                                 () -> -driverXbox.getRightX())
                                                       .withControllerHeadingAxis(
                                                           () -> -driverXbox.getRightX() * allianceSign(),
                                                           () -> -driverXbox.getRightY() * allianceSign())
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
    swerve.setDefaultCommand(swerve.driveFieldRelative(driveStream));
    // Utility (formerly test) mode is for YAMS' live tuning: hold the drive with an idle command so the default drive
    // command doesn't fight the tuning command. See the "Live tuning" section of the README.
    RobotModeTriggers.utility().whileTrue(Commands.idle(swerve).withName("Live Tuning"));
    driverXbox.a().onTrue(Commands.runOnce(() -> headingControlEnabled = !headingControlEnabled));
    driverXbox.b().whileTrue(swerve.sysIdModule("frontleft"));
    driverXbox.x().whileTrue(swerve.driveToPose(new Pose2d(Meters.of(3), Meters.of(3), Rotation2d.fromDegrees(180))));
    driverXbox.y().whileTrue(swerve.driveToPoseYAMS(new Pose2d(Meters.of(3), Meters.of(3), Rotation2d.fromDegrees(180))));
    driverXbox.menu().and(driverXbox.view()).onTrue(swerve.zeroGyro());
  }

  /**
   * Sign that turns a blue alliance relative input into an alliance relative one: -1 on the red alliance (whose
   * driver station faces the other way down the field), 1 otherwise.
   *
   * @return -1 on the red alliance, 1 otherwise.
   */
  private static double allianceSign()
  {
    return MatchState.getAlliance().orElse(Alliance.BLUE) == Alliance.RED ? -1 : 1;
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
