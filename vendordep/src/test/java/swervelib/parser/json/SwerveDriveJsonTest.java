package swervelib.parser.json;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import swervelib.parser.json.SwerveDriveJson.GyroAxis;

class SwerveDriveJsonTest {

  private final ObjectMapper mapper =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  // -- GyroAxis enum tests --------------------------------------------------

  @Test
  void testGyroAxisYawExists() {
    assertEquals("YAW", GyroAxis.YAW.name());
  }

  @Test
  void testGyroAxisPitchExists() {
    assertEquals("PITCH", GyroAxis.PITCH.name());
  }

  @Test
  void testGyroAxisRollExists() {
    assertEquals("ROLL", GyroAxis.ROLL.name());
  }

  @Test
  void testGyroAxisValueOfYaw() {
    assertEquals(GyroAxis.YAW, GyroAxis.valueOf("YAW"));
  }

  @Test
  void testGyroAxisValuesLength() {
    assertEquals(3, GyroAxis.values().length);
  }

  // -- SwerveDriveJson JSON deserialization ---------------------------------

  @Test
  void testFullJsonGyroNotNull() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertNotNull(obj.gyro);
  }

  @Test
  void testFullJsonGyroType() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals("pigeon2_can", obj.gyro.type);
  }

  @Test
  void testFullJsonGyroId() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals(1, obj.gyro.id);
  }

  @Test
  void testFullJsonGyroAxis() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals("yaw", obj.gyroAxis);
  }

  @Test
  void testFullJsonGyroInvert() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertTrue(obj.gyroInvert);
  }

  @Test
  void testFullJsonModulesLength() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals(4, obj.modules.length);
  }

  @Test
  void testFullJsonModulesFirstEntry() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals("fl.json", obj.modules[0]);
  }

  @Test
  void testDefaultGyroAxis() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"navx3_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals("yaw", obj.gyroAxis);
  }

  @Test
  void testGyroAxisPitch() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"pitch\",\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals("pitch", obj.gyroAxis);
  }

  @Test
  void testGyroAxisRoll() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"roll\",\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals("roll", obj.gyroAxis);
  }

  @Test
  void testGyroInvertFalse() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertFalse(obj.gyroInvert);
  }

  @Test
  void testThreeModuleArray() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"navx3_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.readValue(json, SwerveDriveJson.class);
    assertEquals(3, obj.modules.length);
  }
}
