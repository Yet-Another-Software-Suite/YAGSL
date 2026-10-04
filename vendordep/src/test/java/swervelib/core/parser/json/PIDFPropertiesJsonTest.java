package swervelib.core.parser.json;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;

class PIDFPropertiesJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

  // -- Standard PIDF values -------------------------------------------------

  private static final String STANDARD_JSON =
      "{"
          + "\"drive\":{\"p\":0.5,\"i\":0.0,\"d\":0.01},"
          + "\"angle\":{\"p\":2.0,\"i\":0.001,\"d\":0.1}"
          + "}";

  @Test
  void testStandardDriveP() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(STANDARD_JSON);
    assertEquals(0.5, obj.drive.p, 0.001);
  }

  @Test
  void testStandardDriveI() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(STANDARD_JSON);
    assertEquals(0.0, obj.drive.i, 0.001);
  }

  @Test
  void testStandardDriveD() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(STANDARD_JSON);
    assertEquals(0.01, obj.drive.d, 0.001);
  }

  @Test
  void testStandardAngleP() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(STANDARD_JSON);
    assertEquals(2.0, obj.angle.p, 0.001);
  }

  @Test
  void testStandardAngleI() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(STANDARD_JSON);
    assertEquals(0.001, obj.angle.i, 0.001);
  }

  @Test
  void testStandardAngleD() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(STANDARD_JSON);
    assertEquals(0.1, obj.angle.d, 0.001);
  }

  // -- All zeroes -----------------------------------------------------------

  private static final String ZERO_JSON =
      "{"
          + "\"drive\":{\"p\":0.0,\"i\":0.0,\"d\":0.0},"
          + "\"angle\":{\"p\":0.0,\"i\":0.0,\"d\":0.0}"
          + "}";

  @Test
  void testZeroDriveP() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(ZERO_JSON);
    assertEquals(0.0, obj.drive.p, 0.001);
  }

  @Test
  void testZeroDriveI() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(ZERO_JSON);
    assertEquals(0.0, obj.drive.i, 0.001);
  }

  @Test
  void testZeroDriveD() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(ZERO_JSON);
    assertEquals(0.0, obj.drive.d, 0.001);
  }

  @Test
  void testZeroAngleP() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(ZERO_JSON);
    assertEquals(0.0, obj.angle.p, 0.001);
  }

  @Test
  void testZeroAngleI() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(ZERO_JSON);
    assertEquals(0.0, obj.angle.i, 0.001);
  }

  @Test
  void testZeroAngleD() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(ZERO_JSON);
    assertEquals(0.0, obj.angle.d, 0.001);
  }

  // -- Large values ---------------------------------------------------------

  private static final String LARGE_JSON =
      "{"
          + "\"drive\":{\"p\":50.0,\"i\":0.0,\"d\":0.32},"
          + "\"angle\":{\"p\":100.0,\"i\":0.0,\"d\":0.0}"
          + "}";

  @Test
  void testLargeDriveP() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(LARGE_JSON);
    assertEquals(50.0, obj.drive.p, 0.001);
  }

  @Test
  void testLargeDriveD() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(LARGE_JSON);
    assertEquals(0.32, obj.drive.d, 0.001);
  }

  @Test
  void testLargeAngleP() throws Exception {
    PIDFPropertiesJson obj = mapper.type(PIDFPropertiesJson.class).fromJson(LARGE_JSON);
    assertEquals(100.0, obj.angle.p, 0.001);
  }
}
