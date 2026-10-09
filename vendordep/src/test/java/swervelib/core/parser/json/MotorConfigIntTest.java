package swervelib.core.parser.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;

class MotorConfigIntTest {

  private static Jsonb mapper() {
    return Jsonb.builder().build();
  }

  @Test
  void defaultConstructor_driveAndAngleAreZero() {
    MotorConfigInt config = new MotorConfigInt();
    assertEquals(0, config.drive);
    assertEquals(0, config.angle);
  }

  @Test
  void parameterizedConstructor_setsCorrectValues() {
    MotorConfigInt config = new MotorConfigInt(40, 20);
    assertEquals(40, config.drive);
    assertEquals(20, config.angle);
  }

  @Test
  void parameterizedConstructor_bothZero_valuesAreZero() {
    MotorConfigInt config = new MotorConfigInt(0, 0);
    assertEquals(0, config.drive);
    assertEquals(0, config.angle);
  }

  @Test
  void jsonDeserialization_driveAndAngle_parsedCorrectly() throws Exception {
    String json = "{\"drive\": 60, \"angle\": 30}";
    MotorConfigInt config = mapper().type(MotorConfigInt.class).fromJson(json);
    assertNotNull(config);
    assertEquals(60, config.drive);
    assertEquals(30, config.angle);
  }

  @Test
  void jsonDeserialization_emptyObject_defaultsToZero() throws Exception {
    String json = "{}";
    MotorConfigInt config = mapper().type(MotorConfigInt.class).fromJson(json);
    assertNotNull(config);
    assertEquals(0, config.drive);
    assertEquals(0, config.angle);
  }
}
