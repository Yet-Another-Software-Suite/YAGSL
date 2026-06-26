package swervelib.parser.json.modules;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class BoolMotorJsonTest {

  private final ObjectMapper mapper =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @Test
  void testDefaultValues() {
    BoolMotorJson obj = new BoolMotorJson();
    assertFalse(obj.drive);
    assertFalse(obj.angle);
  }

  @Test
  void testDriveTrueAngleFalse() throws Exception {
    BoolMotorJson obj =
        mapper.readValue("{\"drive\": true, \"angle\": false}", BoolMotorJson.class);
    assertTrue(obj.drive);
    assertFalse(obj.angle);
  }

  @Test
  void testDriveFalseAngleTrue() throws Exception {
    BoolMotorJson obj =
        mapper.readValue("{\"drive\": false, \"angle\": true}", BoolMotorJson.class);
    assertFalse(obj.drive);
    assertTrue(obj.angle);
  }

  @Test
  void testBothTrue() throws Exception {
    BoolMotorJson obj =
        mapper.readValue("{\"drive\": true, \"angle\": true}", BoolMotorJson.class);
    assertTrue(obj.drive);
    assertTrue(obj.angle);
  }
}
