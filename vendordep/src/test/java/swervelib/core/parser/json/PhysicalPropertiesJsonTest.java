package swervelib.core.parser.json;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;

class PhysicalPropertiesJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

  // -- Default values -------------------------------------------------------

  @Test
  void testDefaultGearingNotNull() {
    PhysicalPropertiesJson obj = new PhysicalPropertiesJson();
    assertNotNull(obj.gearing);
  }

  @Test
  void testDefaultStatorCurrentLimitDrive() {
    PhysicalPropertiesJson obj = new PhysicalPropertiesJson();
    assertEquals(40, obj.statorCurrentLimit.drive);
  }

  @Test
  void testDefaultStatorCurrentLimitAngle() {
    PhysicalPropertiesJson obj = new PhysicalPropertiesJson();
    assertEquals(20, obj.statorCurrentLimit.angle);
  }

  // -- Full JSON deserialization ---------------------------------------------

  private static final String FULL_JSON =
      "{"
          + "\"gearing\":{"
          + "\"drive\":{\"gearRatio\":8.14,\"diameter\":4.0},"
          + "\"angle\":{\"gearRatio\":21.43}"
          + "},"
          + "\"statorCurrentLimit\":{\"drive\":60,\"angle\":25}"
          + "}";

  @Test
  void testFullJsonDriveGearRatio() throws Exception {
    PhysicalPropertiesJson obj = mapper.type(PhysicalPropertiesJson.class).fromJson(FULL_JSON);
    assertEquals(8.14, obj.gearing.drive.gearRatio, 0.001);
  }

  @Test
  void testFullJsonDriveDiameter() throws Exception {
    PhysicalPropertiesJson obj = mapper.type(PhysicalPropertiesJson.class).fromJson(FULL_JSON);
    assertEquals(4.0, obj.gearing.drive.diameter, 0.001);
  }

  @Test
  void testFullJsonAngleGearRatio() throws Exception {
    PhysicalPropertiesJson obj = mapper.type(PhysicalPropertiesJson.class).fromJson(FULL_JSON);
    assertEquals(21.43, obj.gearing.angle.gearRatio, 0.001);
  }

  @Test
  void testFullJsonStatorCurrentLimitDrive() throws Exception {
    PhysicalPropertiesJson obj = mapper.type(PhysicalPropertiesJson.class).fromJson(FULL_JSON);
    assertEquals(60, obj.statorCurrentLimit.drive);
  }

  @Test
  void testFullJsonStatorCurrentLimitAngle() throws Exception {
    PhysicalPropertiesJson obj = mapper.type(PhysicalPropertiesJson.class).fromJson(FULL_JSON);
    assertEquals(25, obj.statorCurrentLimit.angle);
  }

  // -- JSON with only gearing (statorCurrentLimit defaults) -----------------

  private static final String GEARING_ONLY_JSON =
      "{"
          + "\"gearing\":{"
          + "\"drive\":{\"gearRatio\":6.75,\"diameter\":4.0},"
          + "\"angle\":{\"gearRatio\":12.8}"
          + "}"
          + "}";

  @Test
  void testGearingOnlyStatorCurrentLimitDriveDefault() throws Exception {
    PhysicalPropertiesJson obj =
        mapper.type(PhysicalPropertiesJson.class).fromJson(GEARING_ONLY_JSON);
    assertEquals(40, obj.statorCurrentLimit.drive);
  }

  @Test
  void testGearingOnlyStatorCurrentLimitAngleDefault() throws Exception {
    PhysicalPropertiesJson obj =
        mapper.type(PhysicalPropertiesJson.class).fromJson(GEARING_ONLY_JSON);
    assertEquals(20, obj.statorCurrentLimit.angle);
  }
}
