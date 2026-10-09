package swervelib.core.telemetry;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import org.wpilib.networktables.DoubleArrayPublisher;
import org.wpilib.networktables.DoublePublisher;
import org.wpilib.networktables.NetworkTableInstance;
import org.wpilib.networktables.StringPublisher;
import org.wpilib.networktables.StructArrayPublisher;
import org.wpilib.networktables.StructPublisher;
import org.wpilib.util.Alert;

import org.wpilib.driverstation.RobotState;
import org.wpilib.framework.RobotBase;
import org.wpilib.system.Timer;

/**
 * Telemetry to describe the {@link yams.core.mechanisms.swerve.SwerveDrive} following frc-web-components. (Which follows AdvantageKit)
 */
public class SwerveDriveTelemetry
{

  // WPILib 2027's 3 argument Alert constructor is (id, text, level), and ids must be unique, so every Alert here
  // passes (group, id, text, level).
  /**
   * An {@link Alert} for if the CAN ID is greater than 40.
   */
  public static final  Alert                                   canIdWarning             = new Alert("JSON", "canIdWarning",
                                                                                                    "CAN IDs greater than 40 can cause undefined behaviour, please use a CAN ID below 40!",
                                                                                                    Alert.Level.MEDIUM);
  /**
   * An {@link Alert} for if there is an I2C lockup issue on the roboRIO.
   */
  public static final  Alert                                   i2cLockupWarning         = new Alert("IMU", "i2cLockupWarning",
                                                                                                    "I2C lockup issue detected on roboRIO. Check console for more information.",
                                                                                                    Alert.Level.MEDIUM);
  /**
   * NavX serial comm issue.
   */
  public static final  Alert                                   serialCommsIssueWarning  = new Alert("IMU", "serialCommsIssueWarning",
                                                                                                    "Serial comms is interrupted with USB and other serial traffic and causes intermittent connected/disconnection issues. Please consider another protocol or be mindful of this.",
                                                                                                    Alert.Level.MEDIUM);
  /**
   * Module counter publisher for NT4
   */
  private static final DoublePublisher                         moduleCountPublisher
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getDoubleTopic(
                                                                                                                  "swerve/moduleCount")
                                                                                                              .publish();
  /**
   * Module measured states for Nt4
   */
  private static final DoubleArrayPublisher                    measuredStatesArrayPublisher
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getDoubleArrayTopic(
                                                                                                                  "swerve/measuredStates")
                                                                                                              .publish();
  /**
   * Desired states for NT4
   */
  private static final DoubleArrayPublisher                    desiredStatesArrayPublisher
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getDoubleArrayTopic(
                                                                                                                  "swerve/desiredStates")
                                                                                                              .publish();
  /**
   * Measured chassis speeds array publisher.
   */
  private static final DoubleArrayPublisher                    measuredChassisSpeedsArrayPublisher
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getDoubleArrayTopic(
                                                                                                                  "swerve/measuredChassisSpeeds")
                                                                                                              .publish();
  /**
   * Desired chassis speeds array publisher.
   */
  private static final DoubleArrayPublisher                    desiredChassisSpeedsArrayPublisher
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getDoubleArrayTopic(
                                                                                                                  "swerve/desiredChassisSpeeds")
                                                                                                              .publish();
  /**
   * Robot rotation publisher.
   */
  private static final DoublePublisher                         robotRotationPublisher
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getDoubleTopic(
                                                                                                                  "swerve/robotRotation")
                                                                                                              .publish();
  /**
   * Max angular velocity publisher.
   */
  private static final DoublePublisher                         maxAngularVelocityPublisher
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getDoubleTopic(
                                                                                                                  "swerve/maxAngularVelocity")
                                                                                                              .publish();
  /**
   * Struct publisher for AdvantageScope swerve widgets.
   */
  private static final StructArrayPublisher<SwerveModuleVelocity> measuredStatesStruct
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getStructArrayTopic(
                                                                                                                  "swerve/advantagescope/currentStates",
                                                                                                                  SwerveModuleVelocity.struct)
                                                                                                              .publish();
  /**
   * Struct publisher for AdvantageScope swerve widgets.
   */
  private static final StructArrayPublisher<SwerveModuleVelocity> desiredStatesStruct
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getStructArrayTopic(
                                                                                                                  "swerve/advantagescope/desiredStates",
                                                                                                                  SwerveModuleVelocity.struct)
                                                                                                              .publish();
  /**
   * Measured {@link ChassisVelocities} for NT4 AdvantageScope swerve widgets.
   */
  private static final StructPublisher<ChassisVelocities>          measuredChassisSpeedsStruct
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getStructTopic(
                                                                                                                  "swerve/advantagescope/measuredChassisSpeeds",
                                                                                                                  ChassisVelocities.struct)
                                                                                                              .publish();
  /**
   * Desired {@link ChassisVelocities} for NT4 AdvantageScope swerve widgets.
   */
  private static final StructPublisher<ChassisVelocities>          desiredChassisSpeedsStruct
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getStructTopic(
                                                                                                                  "swerve/advantagescope/desiredChassisSpeeds",
                                                                                                                  ChassisVelocities.struct)
                                                                                                              .publish();
  /**
   * Robot {@link Rotation2d} for AdvantageScope swerve widgets.
   */
  private static final StructPublisher<Rotation2d>             robotRotationStruct
                                                                                        = NetworkTableInstance.getDefault()
                                                                                                              .getTable(
                                                                                                                  "SmartDashboard")
                                                                                                              .getStructTopic(
                                                                                                                  "swerve/advantagescope/robotRotation",
                                                                                                                  Rotation2d.struct)
                                                                                                              .publish();
  /**
   * Wheel locations array publisher for NT4.
   */
  private static final DoubleArrayPublisher wheelLocationsArrayPublisher
                                                                     = NetworkTableInstance.getDefault()
                                                                                           .getTable(
                                                                                               "SmartDashboard")
                                                                                           .getDoubleArrayTopic(
                                                                                               "swerve/wheelLocation")
                                                                                           .publish();
  /**
   * Max speed publisher for NT4.
   */
  private static final DoublePublisher      maxSpeedPublisher
                                                                     = NetworkTableInstance.getDefault()
                                                                                           .getTable(
                                                                                               "SmartDashboard")
                                                                                           .getDoubleTopic(
                                                                                               "swerve/maxSpeed")
                                                                                           .publish();
  /**
   * Rotation unit for NT4.
   */
  private static final StringPublisher      rotationUnitPublisher
                                                                     = NetworkTableInstance.getDefault()
                                                                                           .getTable(
                                                                                               "SmartDashboard")
                                                                                           .getStringTopic(
                                                                                               "swerve/rotationUnit")
                                                                                           .publish();
  /**
   * Chassis width publisher
   */
  private static final DoublePublisher      sizeLeftRightPublisher
                                                                     = NetworkTableInstance.getDefault()
                                                                                           .getTable(
                                                                                               "SmartDashboard")
                                                                                           .getDoubleTopic(
                                                                                               "swerve/sizeLeftRight")
                                                                                           .publish();
  /**
   * Chassis Length publisher.
   */
  private static final DoublePublisher      sizeFrontBackPublisher
                                                                     = NetworkTableInstance.getDefault()
                                                                                           .getTable(
                                                                                               "SmartDashboard")
                                                                                           .getDoubleTopic(
                                                                                               "swerve/sizeFrontBack")
                                                                                           .publish();
  /**
   * Chassis direction widget publisher.
   */
  private static final StringPublisher      forwardDirectionPublisher
                                                                     = NetworkTableInstance.getDefault()
                                                                                           .getTable(
                                                                                               "SmartDashboard")
                                                                                           .getStringTopic(
                                                                                               "swerve/forwardDirection")
                                                                                           .publish();
  /**
   * Odometry cycle time, updated whenever {@link SwerveDrive#updateOdometry()} is called.
   */
  private static final DoublePublisher      odomCycleTime
                                                                     = NetworkTableInstance.getDefault()
                                                                                           .getTable(
                                                                                               "SmartDashboard")
                                                                                           .getDoubleTopic(
                                                                                               "swerve/odomCycleMS")
                                                                                           .publish();
  /**
   * Control cycle time, updated whenever
   * {@link swervelib.SwerveModule#setDesiredState(SwerveModuleVelocity, boolean, double)} is called for the last module.
   */
  private static final DoublePublisher      ctrlCycleTime
                                                                     = NetworkTableInstance.getDefault()
                                                                                           .getTable(
                                                                                               "SmartDashboard")
                                                                                           .getDoubleTopic(
                                                                                               "swerve/controlCycleMS")
                                                                                           .publish();
  /**
   * Odometry timer to track cycle times.
   */
  private static final Timer                odomTimer                = new Timer();
  /**
   * Control timer to track cycle times.
   */
  private static final Timer                ctrlTimer                = new Timer();
  /**
   * Measured swerve module states object.
   */
  public static        SwerveModuleVelocity[]  measuredStatesObj
                                                                     = new SwerveModuleVelocity[4];
  /**
   * Desired swerve module states object
   */
  public static        SwerveModuleVelocity[]  desiredStatesObj
                                                                     = new SwerveModuleVelocity[4];
  /**
   * The maximum achievable angular velocity of the robot. This is used to visualize the angular velocity from the
   * chassis speeds properties.
   */
  public static        ChassisVelocities        measuredChassisSpeedsObj = new ChassisVelocities();
  /**
   * Describes the desired forward, sideways and angular velocity of the robot.
   */
  public static        ChassisVelocities        desiredChassisSpeedsObj  = new ChassisVelocities();
  /**
   * The robot's current rotation based on odometry or gyro readings
   */
  public static        Rotation2d           robotRotationObj         = new Rotation2d();
  /**
   * The current telemetry verbosity level.
   */
  public static        TelemetryVerbosity   verbosity
                                                                     = TelemetryVerbosity.MACHINE;
  /**
   * State of simulation of the Robot, used to optimize retrieval.
   */
  public static        boolean              isSimulation
                                                                     = RobotBase.isSimulation();
  /**
   * The number of swerve modules
   */
  public static        int                  moduleCount;
  /**
   * The Locations of the swerve drive wheels.
   */
  public static        double[]             wheelLocations;
  /**
   * An array of rotation and velocity values describing the measured state of each swerve module
   */
  public static        double[]             measuredStates;
  /**
   * An array of rotation and velocity values describing the desired state of each swerve module
   */
  public static        double[]             desiredStates;
  /**
   * The robot's current rotation based on odometry or gyro readings
   */
  public static        double               robotRotation            = 0;
  /**
   * The maximum achievable speed of the modules, used to adjust the size of the vectors.
   */
  public static        double               maxSpeed;
  /**
   * The units of the module rotations and robot rotation
   */
  public static        String               rotationUnit             = "degrees";
  /**
   * The distance between the left and right modules.
   */
  public static        double               sizeLeftRight;
  /**
   * The distance between the front and back modules.
   */
  public static        double               sizeFrontBack;
  /**
   * The direction the robot should be facing when the "Robot Rotation" is zero or blank. This option is often useful to
   * align with odometry data or match videos. 'up', 'right', 'down' or 'left'
   */
  public static        String               forwardDirection         = "up";
  /**
   * The maximum achievable angular velocity of the robot. This is used to visualize the angular velocity from the
   * chassis speeds properties.
   */
  public static        double               maxAngularVelocity;
  /**
   * The maximum achievable angular velocity of the robot. This is used to visualize the angular velocity from the
   * chassis speeds properties.
   */
  public static        double[]             measuredChassisSpeeds    = new double[3];
  /**
   * Describes the desired forward, sideways and angular velocity of the robot.
   */
  public static        double[]             desiredChassisSpeeds     = new double[3];
  /**
   * Update the telemetry settings that infrequently change.
   */
  public static        boolean              updateSettings           = true;

  /**
   * Start the ctrl timer to measure cycle time, independent of periodic loops.
   */
  public static void startCtrlCycle()
  {
    if (ctrlTimer.isRunning())
    {
      ctrlTimer.reset();
    } else
    {
      ctrlTimer.start();
    }
  }

  /**
   * Update the Control cycle time.
   */
  public static void endCtrlCycle()
  {
    if (RobotState.isTeleopEnabled() || RobotState.isAutonomousEnabled() || RobotState.isUtilityEnabled())
    {
      // 100ms per module on initialization is normal
      ctrlCycleTime.set(ctrlTimer.get() * 1000);
    }
    ctrlTimer.reset();
  }

  /**
   * Start the odom cycle timer to calculate how long each odom took. Independent of periodic loops.
   */
  public static void startOdomCycle()
  {
    if (odomTimer.isRunning())
    {

      odomTimer.reset();
    } else
    {
      odomTimer.start();

    }
  }

  /**
   * Update the odom cycle time.
   */
  public static void endOdomCycle()
  {
    if (RobotState.isTeleopEnabled() || RobotState.isAutonomousEnabled() || RobotState.isUtilityEnabled())
    {
      odomCycleTime.set(odomTimer.get() * 1000);
    }
    odomTimer.reset();
  }

  /**
   * Update only the settings that infrequently or never change.
   */
  public static void updateSwerveTelemetrySettings()
  {
    if (updateSettings)
    {
      updateSettings = false;
      wheelLocationsArrayPublisher.set(wheelLocations);
      maxSpeedPublisher.set(maxSpeed);
      rotationUnitPublisher.set(rotationUnit);
      sizeLeftRightPublisher.set(sizeLeftRight);
      sizeFrontBackPublisher.set(sizeFrontBack);
      forwardDirectionPublisher.set(forwardDirection);
    }
  }

  /**
   * Upload data to smartdashboard
   */
  public static void updateData()
  {
    if (updateSettings)
    {
      updateSwerveTelemetrySettings();
    }
    measuredChassisSpeeds[0] = measuredChassisSpeedsObj.vx;
    measuredChassisSpeeds[1] = measuredChassisSpeedsObj.vy;
    measuredChassisSpeeds[2] = Math.toDegrees(measuredChassisSpeedsObj.omega);

    desiredChassisSpeeds[0] = desiredChassisSpeedsObj.vx;
    desiredChassisSpeeds[1] = desiredChassisSpeedsObj.vy;
    desiredChassisSpeeds[2] = Math.toDegrees(desiredChassisSpeedsObj.omega);

    robotRotation = robotRotationObj.getDegrees();

    for (int i = 0; i < measuredStatesObj.length; i++)
    {
      SwerveModuleVelocity state = measuredStatesObj[i];
      if (state != null)
      {
        measuredStates[i * 2] = state.angle.getDegrees();
        measuredStates[i * 2 + 1] = state.velocity;
      }
    }

    for (int i = 0; i < desiredStatesObj.length; i++)
    {
      SwerveModuleVelocity state = desiredStatesObj[i];
      if (state != null)
      {
        desiredStates[i * 2] = state.angle.getDegrees();
        desiredStates[i * 2 + 1] = state.velocity;
      }
    }

    moduleCountPublisher.set(moduleCount);
    measuredStatesArrayPublisher.set(measuredStates);
    desiredStatesArrayPublisher.set(desiredStates);
    robotRotationPublisher.set(robotRotation);
    maxAngularVelocityPublisher.set(maxAngularVelocity);

    measuredChassisSpeedsArrayPublisher.set(measuredChassisSpeeds);
    desiredChassisSpeedsArrayPublisher.set(desiredChassisSpeeds);

    desiredStatesStruct.set(desiredStatesObj);
    measuredStatesStruct.set(measuredStatesObj);
    desiredChassisSpeedsStruct.set(desiredChassisSpeedsObj);
    measuredChassisSpeedsStruct.set(measuredChassisSpeedsObj);
    robotRotationStruct.set(robotRotationObj);
  }

  /**
   * Verbosity of telemetry data sent back.
   */
  public enum TelemetryVerbosity
  {
    /**
     * No telemetry data is sent back.
     */
    NONE,
    /**
     * Low telemetry data, only post the robot position on the field.
     */
    LOW,
    /**
     * Medium telemetry data, swerve directory
     */
    INFO,
    /**
     * Info level + field info
     */
    POSE,
    /**
     * Full swerve drive data is sent back in both human and machine readable forms.
     */
    HIGH,
    /**
     * Only send the machine readable data related to swerve drive.
     */
    MACHINE
  }
}
