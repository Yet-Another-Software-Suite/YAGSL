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
      // ReduxLib 2027 opens devices through Linux socketcan, so a Redux device (like this example's Canandgyro)
      // can't be created where there is no CAN bus to open: desktop simulation on Windows/macOS, or a Linux machine
      // without a can_s0 interface (like CI). Skip instead of failing there.
      Assumptions.assumeFalse(isReduxBusError(e), "ReduxLib can't open a CAN bus here: " + rootCause(e).getMessage());
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

  private static boolean isReduxBusError(Throwable e)
  {
    // ReduxJNIException is what ReduxLib throws when it can't open the bus.
    return rootCause(e).getClass().getName().startsWith("com.reduxrobotics.canand.ReduxJNI");
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
