package swervelib.core.parser.json.modules;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;

class BoolMotorJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

  @Test
  void testDefaultValues() {
    BoolMotorJson obj = new BoolMotorJson();
    assertFalse(obj.drive);
    assertFalse(obj.angle);
  }

  @Test
  void testDriveTrueAngleFalse() throws Exception {
    BoolMotorJson obj =
        mapper.type(BoolMotorJson.class).fromJson("{\"drive\": true, \"angle\": false}");
    assertTrue(obj.drive);
    assertFalse(obj.angle);
  }

  @Test
  void testDriveFalseAngleTrue() throws Exception {
    BoolMotorJson obj =
        mapper.type(BoolMotorJson.class).fromJson("{\"drive\": false, \"angle\": true}");
    assertFalse(obj.drive);
    assertTrue(obj.angle);
  }

  @Test
  void testBothTrue() throws Exception {
    BoolMotorJson obj =
        mapper.type(BoolMotorJson.class).fromJson("{\"drive\": true, \"angle\": true}");
    assertTrue(obj.drive);
    assertTrue(obj.angle);
  }
}
