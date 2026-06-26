package swervelib.parser.json.modules;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class AngleGearingJsonTest {

  private final ObjectMapper mapper =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @Test
  void testDefaultConstruction() {
    AngleGearingJson obj = new AngleGearingJson();
    assertEquals(0.0, obj.gearRatio);
  }

  @Test
  void testJsonParseStandard() throws Exception {
    AngleGearingJson obj =
        mapper.readValue("{\"gearRatio\": 12.8}", AngleGearingJson.class);
    assertEquals(12.8, obj.gearRatio);
  }

  @Test
  void testJsonParseHighPrecision() throws Exception {
    AngleGearingJson obj =
        mapper.readValue("{\"gearRatio\": 21.4285714286}", AngleGearingJson.class);
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
