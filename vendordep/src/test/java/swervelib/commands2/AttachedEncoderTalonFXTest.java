package swervelib.commands2;

import java.util.HashMap;
import java.util.Map;

/** A TalonFX has no data port YAGSL supports, so every attached type says what to use instead. */
class AttachedEncoderTalonFXTest extends AttachedEncoderTest {
  @Override
  String angleMotor() {
    return "talonfx_krakenx60";
  }

  @Override
  Map<String, Boolean> working() {
    return Map.of();
  }

  @Override
  Map<String, String> failing() {
    Map<String, String> failing = new HashMap<>();
    for (String type : TYPES) {
      failing.put(type, "use a CAN absolute encoder");
    }
    return failing;
  }
}
