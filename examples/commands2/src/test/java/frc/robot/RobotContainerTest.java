package frc.robot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.wpilib.command2.CommandScheduler;
import org.wpilib.hardware.hal.HAL;
import org.wpilib.simulation.SimHooks;

/**
 * Smoke test: builds the whole robot (YAGSL JSON parsing, the YAMS swerve drive, and PathPlanner) in simulation and
 * runs the scheduler for a second, so a broken JSON config, vendordep, or API change fails the build instead of the
 * robot.
 */
class RobotContainerTest
{

  @BeforeAll
  static void initializeHal()
  {
    HAL.initialize();
  }

  @Test
  void robotContainerBuildsAndRuns()
  {
    RobotContainer container = new RobotContainer();
    assertNotNull(container.getAutonomousCommand());
    assertDoesNotThrow(() -> {
      for (int i = 0; i < 50; i++)
      {
        CommandScheduler.getInstance().run();
        SimHooks.stepTiming(0.02);
      }
    });
  }
}
