package swervelib.parser.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class MotorConfigDoubleTest {

  private static final double DELTA = 1e-9;

  private static ObjectMapper mapper() {
    return new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Test
  void defaultConstructor_driveAndAngleAreZero() {
    MotorConfigDouble config = new MotorConfigDouble();
    assertEquals(0.0, config.drive, DELTA);
    assertEquals(0.0, config.angle, DELTA);
  }

  @Test
  void parameterizedConstructor_angleFirstDriveSecond() {
    // Signature: MotorConfigDouble(double angle, double drive)
    MotorConfigDouble config = new MotorConfigDouble(0.5, 1.5);
    assertEquals(0.5, config.angle, DELTA);
    assertEquals(1.5, config.drive, DELTA);
  }

  @Test
  void jsonDeserialization_driveAndAngle_parsedCorrectly() throws Exception {
    String json = "{\"drive\": 2.5, \"angle\": 1.0}";
    MotorConfigDouble config = mapper().readValue(json, MotorConfigDouble.class);
    assertNotNull(config);
    assertEquals(2.5, config.drive, DELTA);
    assertEquals(1.0, config.angle, DELTA);
  }

  @Test
  void jsonDeserialization_emptyObject_defaultsToZero() throws Exception {
    String json = "{}";
    MotorConfigDouble config = mapper().readValue(json, MotorConfigDouble.class);
    assertNotNull(config);
    assertEquals(0.0, config.drive, DELTA);
    assertEquals(0.0, config.angle, DELTA);
  }
}
