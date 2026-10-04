package frc.robot;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assumptions;
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
    RobotContainer container;
    try
    {
      container = new RobotContainer();
    } catch (RuntimeException e)
    {
      // ReduxLib 2027 opens devices through Linux socketcan only, so a Redux device (like this example's
      // Canandgyro) can't be created on other desktop simulation platforms. Skip instead of failing there.
      Assumptions.assumeFalse(isUnsupportedReduxBus(e),
                              "ReduxLib can't open a CAN bus on this platform: " + rootCause(e).getMessage());
      throw e;
    }
    assertNotNull(container.getAutonomousCommand());
    assertDoesNotThrow(() -> {
      for (int i = 0; i < 50; i++)
      {
        CommandScheduler.getInstance().run();
        SimHooks.stepTiming(0.02);
      }
    });
  }

  private static boolean isUnsupportedReduxBus(Throwable e)
  {
    Throwable cause = rootCause(e);
    return cause.getClass().getName().startsWith("com.reduxrobotics.")
           && String.valueOf(cause.getMessage()).contains("not supported on this platform");
  }

  private static Throwable rootCause(Throwable e)
  {
    while (e.getCause() != null)
    {
      e = e.getCause();
    }
    return e;
  }
}
