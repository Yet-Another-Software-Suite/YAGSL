package swervelib.core.parser.json.modules;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;

class DriveGearingJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

  @Test
  void testDefaultConstruction() {
    DriveGearingJson obj = new DriveGearingJson();
    assertEquals(0.0, obj.gearRatio);
    assertEquals(0.0, obj.diameter);
  }

  @Test
  void testJsonParseSdsL2() throws Exception {
    DriveGearingJson obj =
        mapper.type(DriveGearingJson.class).fromJson("{\"gearRatio\": 6.75, \"diameter\": 4.0}");
    assertEquals(6.75, obj.gearRatio);
    assertEquals(4.0, obj.diameter);
  }

  @Test
  void testJsonParseSdsL3() throws Exception {
    DriveGearingJson obj =
        mapper.type(DriveGearingJson.class).fromJson("{\"gearRatio\": 8.14, \"diameter\": 3.0}");
    assertEquals(8.14, obj.gearRatio);
    assertEquals(3.0, obj.diameter);
  }

  @Test
  void testEqualsWhenSameValues() {
    DriveGearingJson a = new DriveGearingJson();
    a.gearRatio = 6.75;
    a.diameter = 4.0;

    DriveGearingJson b = new DriveGearingJson();
    b.gearRatio = 6.75;
    b.diameter = 4.0;

    assertTrue(a.equals(b));
  }

  @Test
  void testEqualsWhenDifferentGearRatio() {
    DriveGearingJson a = new DriveGearingJson();
    a.gearRatio = 6.75;
    a.diameter = 4.0;

    DriveGearingJson b = new DriveGearingJson();
    b.gearRatio = 8.14;
    b.diameter = 4.0;

    assertFalse(a.equals(b));
  }

  @Test
  void testEqualsWhenDifferentDiameter() {
    DriveGearingJson a = new DriveGearingJson();
    a.gearRatio = 6.75;
    a.diameter = 4.0;

    DriveGearingJson b = new DriveGearingJson();
    b.gearRatio = 6.75;
    b.diameter = 3.0;

    assertFalse(a.equals(b));
  }

  @Test
  void testEqualsWhenSameGearRatioDifferentDiameter() {
    DriveGearingJson a = new DriveGearingJson();
    a.gearRatio = 6.75;
    a.diameter = 4.0;

    DriveGearingJson b = new DriveGearingJson();
    b.gearRatio = 6.75;
    b.diameter = 3.0;

    assertFalse(a.equals(b));
  }
}
