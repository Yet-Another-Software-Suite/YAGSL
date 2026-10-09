package swervelib.core.parser.json;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import yams.core.mechanisms.config.enums.GyroAxis;

class SwerveDriveJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

  // -- gyroAxis -> YAMS GyroAxis ---------------------------------------------

  // SwerveParser hands gyroAxis to SwerveDriveConfig#withGyroHeadingAxis via
  // GyroAxis.valueOf(gyroAxis.toUpperCase()), so every documented value must map onto YAMS's enum.
  @ParameterizedTest
  @CsvSource({"yaw, YAW", "pitch, PITCH", "roll, ROLL"})
  void testGyroAxisMapsToYamsGyroAxis(String json, GyroAxis expected) {
    assertEquals(expected, GyroAxis.valueOf(json.toUpperCase()));
  }

  @Test
  void testYamsGyroAxisHasNoUndocumentedValues() {
    assertEquals(3, GyroAxis.values().length);
  }

  // -- SwerveDriveJson JSON deserialization ---------------------------------

  @Test
  void testFullJsonGyroNotNull() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertNotNull(obj.gyro);
  }

  @Test
  void testFullJsonGyroType() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals("pigeon2_can", obj.gyro.type);
  }

  @Test
  void testFullJsonGyroId() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals(1, obj.gyro.id);
  }

  @Test
  void testFullJsonGyroAxis() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals("yaw", obj.gyroAxis);
  }

  @Test
  void testFullJsonGyroInvert() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertTrue(obj.gyroInvert);
  }

  @Test
  void testFullJsonModulesLength() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals(4, obj.modules.length);
  }

  @Test
  void testFullJsonModulesFirstEntry() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":1,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":true,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals("fl.json", obj.modules[0]);
  }

  @Test
  void testDefaultGyroAxis() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"navx3_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals("yaw", obj.gyroAxis);
  }

  @Test
  void testGyroAxisPitch() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"pitch\",\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals("pitch", obj.gyroAxis);
  }

  @Test
  void testGyroAxisRoll() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"roll\",\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals("roll", obj.gyroAxis);
  }

  @Test
  void testGyroInvertFalse() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertFalse(obj.gyroInvert);
  }

  @Test
  void testThreeModuleArray() throws Exception {
    String json =
        "{\"gyro\":{\"type\":\"navx3_can\",\"id\":0,\"canbus\":\"\"},"
            + "\"gyroInvert\":false,"
            + "\"modules\":[\"a.json\",\"b.json\",\"c.json\"]}";
    SwerveDriveJson obj = mapper.type(SwerveDriveJson.class).fromJson(json);
    assertEquals(3, obj.modules.length);
  }
}
