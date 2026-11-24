package swervelib.commands2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import swervelib.core.parser.deserializer.reflections.REVDevices;

class AttachedEncoderSparkMaxTest extends AttachedEncoderTest {
  @Override
  String angleMotor() {
    return "sparkmax_neo";
  }

  @Override
  Map<String, Boolean> working() {
    return sparkWorking();
  }

  @Override
  Map<String, String> failing() {
    return Map.of("thrifty", "use thrifty_analog");
  }

  /**
   * An analog encoder on a SPARK reads 3.3 V per rotation for analog_attached and 5 V per rotation for
   * analog5v_attached, as YAGSL 2026 did, with the inversion applied to the reading. REVLib's simulation doesn't pass
   * a simulated analog voltage to the SPARK's reading, so the conversion is checked on its own.
   */
  @ParameterizedTest
  @CsvSource({
    "1.65,  3.3, false, 0.5",
    "0.825, 3.3, false, 0.25",
    "0.825, 3.3, true,  0.75",
    "2.5,   5.0, false, 0.5",
    "1.25,  5.0, false, 0.25",
    "1.25,  5.0, true,  0.75"
  })
  void analogReading(double volts, double voltsPerRotation, boolean inverted, double rotations) {
    assertEquals(rotations, REVDevices.analogRotations(volts, voltsPerRotation, inverted), 1e-9);
  }
}
