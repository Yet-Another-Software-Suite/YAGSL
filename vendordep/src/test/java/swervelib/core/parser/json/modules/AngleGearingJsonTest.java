package swervelib.core.parser.json.modules;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;

class AngleGearingJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

  @Test
  void testDefaultConstruction() {
    AngleGearingJson obj = new AngleGearingJson();
    assertEquals(0.0, obj.gearRatio);
  }

  @Test
  void testJsonParseStandard() throws Exception {
    AngleGearingJson obj = mapper.type(AngleGearingJson.class).fromJson("{\"gearRatio\": 12.8}");
    assertEquals(12.8, obj.gearRatio);
  }

  @Test
  void testJsonParseHighPrecision() throws Exception {
    AngleGearingJson obj =
        mapper.type(AngleGearingJson.class).fromJson("{\"gearRatio\": 21.4285714286}");
    assertEquals(21.4285714286, obj.gearRatio);
  }

  @Test
  void testEqualsWithMatchingDriveGearingJson() {
    AngleGearingJson angle = new AngleGearingJson();
    angle.gearRatio = 12.8;

    DriveGearingJson drive = new DriveGearingJson();
    drive.gearRatio = 12.8;
    drive.diameter = 4.0;

    assertTrue(angle.equals(drive));
  }

  @Test
  void testEqualsWithDifferentGearRatioInDriveGearingJson() {
    AngleGearingJson angle = new AngleGearingJson();
    angle.gearRatio = 12.8;

    DriveGearingJson drive = new DriveGearingJson();
    drive.gearRatio = 10.0;
    drive.diameter = 4.0;

    assertFalse(angle.equals(drive));
  }
}
