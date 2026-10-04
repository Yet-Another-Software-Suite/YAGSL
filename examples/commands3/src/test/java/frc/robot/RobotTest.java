package frc.robot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import frc.robot.opmodes.auto.DriveToPoseSquareAuto;
import frc.robot.opmodes.auto.LeaveStartingAreaAuto;
import frc.robot.opmodes.auto.OutAndBackAuto;
import frc.robot.opmodes.teleop.AngularVelocityTeleop;
import frc.robot.opmodes.teleop.DemoModeTeleop;
import frc.robot.opmodes.teleop.HeadingTeleop;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.wpilib.hardware.hal.HAL;
import org.wpilib.simulation.SimHooks;

/**
 * Smoke test: builds the whole robot (YAGSL JSON parsing, the YAMS swerve drive, and every opmode) in simulation and
 * runs the robot loop for a second, so a broken JSON config, vendordep, or API change fails the build instead of the
 * robot.
 */
class RobotTest
{

  @BeforeAll
  static void initializeHal()
  {
    HAL.initialize();
  }

  @Test
  void robotBuildsAndRuns()
  {
    Robot robot = new Robot();
    assertNotNull(new AngularVelocityTeleop(robot));
    assertNotNull(new HeadingTeleop(robot));
    assertNotNull(new DemoModeTeleop(robot));
    assertNotNull(new LeaveStartingAreaAuto(robot));
    assertNotNull(new OutAndBackAuto(robot));
    assertNotNull(new DriveToPoseSquareAuto(robot));
    assertDoesNotThrow(() -> {
      for (int i = 0; i < 50; i++)
      {
        robot.robotPeriodic();
        robot.simulationPeriodic();
        SimHooks.stepTiming(0.02);
      }
    });
  }
}
