package swervelib.core.telemetry;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SwerveDriveTelemetryTest {

  // WPILib 2027 Alerts need unique ids; a duplicate throws "Alert already allocated" while the
  // class
  // loads, so touching any static field is enough to catch it.
  @Test
  void testAlertsLoadWithUniqueIds() {
    assertNotNull(SwerveDriveTelemetry.canIdWarning);
    assertNotNull(SwerveDriveTelemetry.i2cLockupWarning);
    assertNotNull(SwerveDriveTelemetry.serialCommsIssueWarning);
  }
}
