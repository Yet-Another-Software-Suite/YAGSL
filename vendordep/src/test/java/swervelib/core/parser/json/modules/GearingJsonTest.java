package swervelib.core.parser.json.modules;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;

class GearingJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

  @Test
  void testDefaultConstructionDriveNotNull() {
    GearingJson obj = new GearingJson();
    assertNotNull(obj.drive);
  }

  @Test
  void testDefaultConstructionAngleNotNull() {
    GearingJson obj = new GearingJson();
    assertNotNull(obj.angle);
  }

  @Test
  void testDefaultDriveGearRatio() {
    GearingJson obj = new GearingJson();
    assertEquals(0.0, obj.drive.gearRatio);
  }

  @Test
  void testDefaultDriveDiameter() {
    GearingJson obj = new GearingJson();
    assertEquals(0.0, obj.drive.diameter);
  }

  @Test
  void testDefaultAngleGearRatio() {
    GearingJson obj = new GearingJson();
    assertEquals(0.0, obj.angle.gearRatio);
  }

  @Test
  void testFullJsonParse() throws Exception {
    String json =
        "{\"drive\": {\"gearRatio\": 6.75, \"diameter\": 4.0},"
            + " \"angle\": {\"gearRatio\": 12.8}}";
    GearingJson obj = mapper.type(GearingJson.class).fromJson(json);
    assertEquals(6.75, obj.drive.gearRatio);
    assertEquals(4.0, obj.drive.diameter);
    assertEquals(12.8, obj.angle.gearRatio);
  }

  @Test
  void testJsonParseWithOnlyDriveOverride() throws Exception {
    String json = "{\"drive\": {\"gearRatio\": 8.14, \"diameter\": 3.0}}";
    GearingJson obj = mapper.type(GearingJson.class).fromJson(json);
    assertEquals(8.14, obj.drive.gearRatio);
    assertEquals(3.0, obj.drive.diameter);
    assertNotNull(obj.angle);
    assertEquals(0.0, obj.angle.gearRatio);
  }

  @Test
  void testJsonParseWithOnlyAngleOverride() throws Exception {
    String json = "{\"angle\": {\"gearRatio\": 21.4285714286}}";
    GearingJson obj = mapper.type(GearingJson.class).fromJson(json);
    assertNotNull(obj.drive);
    assertEquals(0.0, obj.drive.gearRatio);
    assertEquals(0.0, obj.drive.diameter);
    assertEquals(21.4285714286, obj.angle.gearRatio);
  }
}
