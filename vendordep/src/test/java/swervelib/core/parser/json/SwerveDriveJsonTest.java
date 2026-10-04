package swervelib.core.parser.json;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;
import org.wpilib.math.geometry.Rotation3d;
import swervelib.core.parser.json.SwerveDriveJson.GyroAxis;

class SwerveDriveJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

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

  // -- GyroAxis.toRobotAttitude tests ---------------------------------------

  private static final Rotation3d GYRO_ATTITUDE = new Rotation3d(0.1, 0.2, 0.3);

  @Test
  void testYawAxisPassesThroughFullAttitude() {
    Rotation3d attitude = GyroAxis.YAW.toRobotAttitude(() -> GYRO_ATTITUDE).get();
    assertEquals(GYRO_ATTITUDE.getX(), attitude.getX(), 1e-9);
    assertEquals(GYRO_ATTITUDE.getY(), attitude.getY(), 1e-9);
    assertEquals(GYRO_ATTITUDE.getZ(), attitude.getZ(), 1e-9);
  }

  @Test
  void testPitchAxisBecomesHeading() {
    Rotation3d attitude = GyroAxis.PITCH.toRobotAttitude(() -> GYRO_ATTITUDE).get();
    assertEquals(0.0, attitude.getX(), 1e-9);
    assertEquals(0.0, attitude.getY(), 1e-9);
    assertEquals(GYRO_ATTITUDE.getY(), attitude.getZ(), 1e-9);
  }

  @Test
  void testRollAxisBecomesHeading() {
    Rotation3d attitude = GyroAxis.ROLL.toRobotAttitude(() -> GYRO_ATTITUDE).get();
    assertEquals(0.0, attitude.getX(), 1e-9);
    assertEquals(0.0, attitude.getY(), 1e-9);
    assertEquals(GYRO_ATTITUDE.getX(), attitude.getZ(), 1e-9);
  }

  @Test
  void testAttitudeIsReadOnEveryCall() {
    Rotation3d[] gyro = {new Rotation3d(0, 0, 0.5)};
    var attitude = GyroAxis.YAW.toRobotAttitude(() -> gyro[0]);
    gyro[0] = new Rotation3d(0, 0, 1.0);
    assertEquals(1.0, attitude.get().getZ(), 1e-9);
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
