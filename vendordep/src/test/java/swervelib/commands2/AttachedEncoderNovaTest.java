package swervelib.commands2;

import java.util.HashMap;
import java.util.Map;

class AttachedEncoderNovaTest extends AttachedEncoderTest {
  @Override
  String angleMotor() {
    return "nova_neo";
  }

  @Override
  Map<String, Boolean> working() {
    Map<String, Boolean> working = new HashMap<>();
    NOVA_FEEDBACK.forEach(type -> working.put(type, true));
    return working;
  }

  @Override
  Map<String, String> failing() {
    return Map.of("analog5v", "use analog_attached");
  }
}
