package swervelib.commands2;

import java.util.Map;

class AttachedEncoderSparkFlexTest extends AttachedEncoderTest {
  @Override
  String angleMotor() {
    return "sparkflex_vortex";
  }

  @Override
  Map<String, Boolean> working() {
    return sparkWorking();
  }

  @Override
  Map<String, String> failing() {
    return Map.of("thrifty", "use thrifty_analog");
  }
}
