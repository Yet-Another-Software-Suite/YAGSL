package swervelib.parser.json;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class ModuleJsonTest {

  private final ObjectMapper mapper =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  // -- Default values -------------------------------------------------------

  @Test
  void testDefaultAbsoluteEncoderInverted() {
    ModuleJson obj = new ModuleJson();
    assertFalse(obj.absoluteEncoderInverted);
  }

  @Test
  void testDefaultAbsoluteEncoderGearRatio() {
    ModuleJson obj = new ModuleJson();
    assertEquals(1.0, obj.absoluteEncoderGearRatio, 0.001);
  }

  @Test
  void testDefaultGearingNotNull() {
    ModuleJson obj = new ModuleJson();
    assertNotNull(obj.gearing);
  }

  // -- Full JSON deserialization ---------------------------------------------

  private static final String FULL_JSON =
      "{"
          + "\"drive\":{\"type\":\"sparkmax_neo\",\"id\":1,\"canbus\":\"\"},"
          + "\"angle\":{\"type\":\"sparkflex_vortex\",\"id\":2,\"canbus\":\"\"},"
          + "\"absoluteEncoder\":{\"type\":\"cancoder_can\",\"id\":10,\"channel\":0,\"canbus\":\"\"},"
          + "\"inverted\":{\"drive\":true,\"angle\":false},"
          + "\"absoluteEncoderOffset\":-139.043,"
          + "\"absoluteEncoderInverted\":true,"
          + "\"absoluteEncoderGearRatio\":2.0,"
          + "\"location\":{\"front\":11.5,\"left\":-11.5}"
          + "}";

  @Test
  void testFullJsonDriveType() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals("sparkmax_neo", obj.drive.type);
  }

  @Test
  void testFullJsonDriveId() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals(1, obj.drive.id);
  }

  @Test
  void testFullJsonAngleType() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals("sparkflex_vortex", obj.angle.type);
  }

  @Test
  void testFullJsonAbsoluteEncoderType() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals("cancoder_can", obj.absoluteEncoder.type);
  }

  @Test
  void testFullJsonAbsoluteEncoderId() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals(10, obj.absoluteEncoder.id);
  }

  @Test
  void testFullJsonInvertedDrive() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertTrue(obj.inverted.drive);
  }

  @Test
  void testFullJsonInvertedAngle() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertFalse(obj.inverted.angle);
  }

  @Test
  void testFullJsonAbsoluteEncoderOffset() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals(-139.043, obj.absoluteEncoderOffset, 0.001);
  }

  @Test
  void testFullJsonAbsoluteEncoderInverted() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertTrue(obj.absoluteEncoderInverted);
  }

  @Test
  void testFullJsonAbsoluteEncoderGearRatio() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals(2.0, obj.absoluteEncoderGearRatio, 0.001);
  }

  @Test
  void testFullJsonLocationFront() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals(11.5, obj.location.front, 0.001);
  }

  @Test
  void testFullJsonLocationLeft() throws Exception {
    ModuleJson obj = mapper.readValue(FULL_JSON, ModuleJson.class);
    assertEquals(-11.5, obj.location.left, 0.001);
  }

  // -- Default optional fields when absent ----------------------------------

  private static final String MINIMAL_JSON =
      "{"
          + "\"drive\":{\"type\":\"sparkmax_neo\",\"id\":1,\"canbus\":\"\"},"
          + "\"angle\":{\"type\":\"sparkmax_neo\",\"id\":2,\"canbus\":\"\"},"
          + "\"absoluteEncoder\":{\"type\":\"revthroughbore_attached\",\"id\":0,\"channel\":0,\"canbus\":\"\"},"
          + "\"inverted\":{\"drive\":false,\"angle\":false},"
          + "\"absoluteEncoderOffset\":0.0,"
          + "\"location\":{\"front\":5.0,\"left\":5.0}"
          + "}";

  @Test
  void testDefaultAbsoluteEncoderInvertedWhenAbsent() throws Exception {
    ModuleJson obj = mapper.readValue(MINIMAL_JSON, ModuleJson.class);
    assertFalse(obj.absoluteEncoderInverted);
  }

  @Test
  void testDefaultAbsoluteEncoderGearRatioWhenAbsent() throws Exception {
    ModuleJson obj = mapper.readValue(MINIMAL_JSON, ModuleJson.class);
    assertEquals(1.0, obj.absoluteEncoderGearRatio, 0.001);
  }
}
