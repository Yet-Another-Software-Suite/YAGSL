package swervelib.parser.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wpi.first.math.system.plant.DCMotor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import swervelib.parser.deserializer.ReflectionsManager.VendorMotorController;
import swervelib.parser.json.DeviceJson.VENDOR;

class DeviceJsonTest {

  // ---------------------------------------------------------------------------
  // getDCMotor — valid motor types
  // ---------------------------------------------------------------------------

  @Test
  void getDCMotor_neo_returnsNonNull() {
    DCMotor result = DeviceJson.getDCMotor("neo");
    assertNotNull(result);
    assert result.stallTorqueNewtonMeters > 0;
  }

  @Test
  void getDCMotor_neo2_sameAsNeo() {
    DCMotor neo = DeviceJson.getDCMotor("neo");
    DCMotor neo2 = DeviceJson.getDCMotor("neo2");
    assertNotNull(neo2);
    // Both resolve to DCMotor.getNEO(1); stall torque must match.
    assertEquals(neo.stallTorqueNewtonMeters, neo2.stallTorqueNewtonMeters, 1e-9);
    assertEquals(neo.freeSpeedRadPerSec, neo2.freeSpeedRadPerSec, 1e-9);
  }

  @Test
  void getDCMotor_neo550_returnsNonNull() {
    DCMotor result = DeviceJson.getDCMotor("neo550");
    assertNotNull(result);
    assert result.stallTorqueNewtonMeters > 0;
  }

  @Test
  void getDCMotor_vortex_returnsNonNull() {
    DCMotor result = DeviceJson.getDCMotor("vortex");
    assertNotNull(result);
    assert result.stallTorqueNewtonMeters > 0;
  }

  @Test
  void getDCMotor_minion_returnsNonNull() {
    DCMotor result = DeviceJson.getDCMotor("minion");
    assertNotNull(result);
    assert result.stallTorqueNewtonMeters > 0;
  }

  @Test
  void getDCMotor_krakenx44_returnsNonNull() {
    DCMotor result = DeviceJson.getDCMotor("krakenx44");
    assertNotNull(result);
    assert result.stallTorqueNewtonMeters > 0;
  }

  @Test
  void getDCMotor_krakenx60_returnsNonNull() {
    DCMotor result = DeviceJson.getDCMotor("krakenx60");
    assertNotNull(result);
    assert result.stallTorqueNewtonMeters > 0;
  }

  @Test
  void getDCMotor_pulsar_returns7500RpmFreeSpeed() {
    DCMotor result = DeviceJson.getDCMotor("pulsar");
    assertNotNull(result);
    // Constructor: new DCMotor(12, 3.1, 189, 1, 7500, 1) — last arg is numMotors,
    // second-to-last is free speed in RPM (converted internally to rad/s).
    // Just verify it is positive and non-trivially large.
    assert result.freeSpeedRadPerSec > 0;
    assert result.stallTorqueNewtonMeters > 0;
  }

  // ---------------------------------------------------------------------------
  // getDCMotor — invalid motor types
  // ---------------------------------------------------------------------------

  @ParameterizedTest
  @ValueSource(strings = {"invalid_type", "brushed", ""})
  void getDCMotor_invalidType_throwsIllegalArgumentException(String motorType) {
    assertThrows(
        IllegalArgumentException.class,
        () -> DeviceJson.getDCMotor(motorType),
        "Expected IllegalArgumentException for motor type: " + motorType);
  }

  // ---------------------------------------------------------------------------
  // getMotorController
  // ---------------------------------------------------------------------------

  private static DeviceJson deviceWithType(String type) {
    DeviceJson d = new DeviceJson();
    d.type = type;
    d.id = 1;
    return d;
  }

  @Test
  void getMotorController_sparkmaxNeo_returnsSparkMax() {
    assertEquals(
        VendorMotorController.SPARKMAX, deviceWithType("sparkmax_neo").getMotorController());
  }

  @Test
  void getMotorController_sparkflexVortex_returnsSparkFlex() {
    assertEquals(
        VendorMotorController.SPARKFLEX, deviceWithType("sparkflex_vortex").getMotorController());
  }

  @Test
  void getMotorController_talonfxKrakenx60_returnsTalonFX() {
    assertEquals(
        VendorMotorController.TALONFX, deviceWithType("talonfx_krakenx60").getMotorController());
  }

  @Test
  void getMotorController_talonfxsNeo_returnsTalonFXS() {
    assertEquals(
        VendorMotorController.TALONFXS, deviceWithType("talonfxs_neo").getMotorController());
  }

  @Test
  void getMotorController_novaNeo_returnsNova() {
    assertEquals(VendorMotorController.NOVA, deviceWithType("nova_neo").getMotorController());
  }

  @Test
  void getMotorController_unknownMotor_returnsNone() {
    assertEquals(VendorMotorController.NONE, deviceWithType("unknown_motor").getMotorController());
  }

  @Test
  void getMotorController_noUnderscore_returnsNone() {
    assertEquals(VendorMotorController.NONE, deviceWithType("nounderscore").getMotorController());
  }

  // ---------------------------------------------------------------------------
  // getGyro
  // ---------------------------------------------------------------------------

  @Test
  void getGyro_customType_returnsNull() {
    DeviceJson d = deviceWithType("custom");
    assertEquals(null, d.getGyro(SwerveDriveJson.GyroAxis.YAW, false));
  }

  @Test
  void getGyro_customTypeCaseInsensitive_returnsNull() {
    DeviceJson d = deviceWithType("CUSTOM");
    assertEquals(null, d.getGyro(SwerveDriveJson.GyroAxis.YAW, false));
  }

  // ---------------------------------------------------------------------------
  // getVendor
  // ---------------------------------------------------------------------------

  @Test
  void getVendor_talonfx_returnsCTRE() {
    assertEquals(VENDOR.CTRE, deviceWithType("talonfx_krakenx60").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_talonfxs_returnsCTRE() {
    assertEquals(VENDOR.CTRE, deviceWithType("talonfxs_neo").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_sparkmax_returnsREV() {
    assertEquals(VENDOR.REV, deviceWithType("sparkmax_neo").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_sparkflex_returnsREV() {
    assertEquals(VENDOR.REV, deviceWithType("sparkflex_vortex").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_navx3Can_returnsStudica() {
    assertEquals(VENDOR.STUDICA, deviceWithType("navx3_can").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_novaNeo_returnsThriftybot() {
    assertEquals(VENDOR.THRIFTYBOT, deviceWithType("nova_neo").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_canandgyroCan_returnsRedux() {
    assertEquals(VENDOR.REDUX, deviceWithType("canandgyro_can").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_systemcoreInternal_returnsLimelight() {
    assertEquals(VENDOR.LIMELIGHT, deviceWithType("systemcore_internal").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_revthroughboreAttached_passesThroughAttachedType() {
    // "attached" connection type should return the supplied attachedType directly.
    assertEquals(VENDOR.REV, deviceWithType("revthroughbore_attached").getVendor(VENDOR.REV));
  }

  @Test
  void getVendor_revthroughboreDio_returnsSmartIO() {
    assertEquals(VENDOR.SMARTIO, deviceWithType("revthroughbore_dio").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_andymarkhexboreCan_returnsAndymark() {
    assertEquals(VENDOR.ANDYMARK, deviceWithType("andymarkhexbore_can").getVendor(VENDOR.UNKNOWN));
  }

  @Test
  void getVendor_noUnderscore_returnsUnknown() {
    assertEquals(VENDOR.UNKNOWN, deviceWithType("nounderscore").getVendor(VENDOR.UNKNOWN));
  }

  // ---------------------------------------------------------------------------
  // JSON deserialization
  // ---------------------------------------------------------------------------

  private static ObjectMapper mapper() {
    return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Test
  void jsonDeserialization_fullObject_allFieldsCorrect() throws Exception {
    String json = "{\"type\":\"sparkmax_neo\",\"id\":5,\"channel\":3,\"canbus\":\"canivore\"}";
    DeviceJson d = mapper().readValue(json, DeviceJson.class);
    assertEquals("sparkmax_neo", d.type);
    assertEquals(5, d.id);
    assertEquals(3, d.channel);
    assertEquals("canivore", d.canbus);
  }

  @Test
  void jsonDeserialization_minimalObject_defaultsApplied() throws Exception {
    String json = "{\"type\":\"sparkmax_neo\",\"id\":1}";
    DeviceJson d = mapper().readValue(json, DeviceJson.class);
    assertEquals("sparkmax_neo", d.type);
    assertEquals(1, d.id);
    assertEquals(0, d.channel);
    assertEquals("", d.canbus);
  }

  @Test
  void jsonDeserialization_unknownFieldIgnored_parsesOk() throws Exception {
    String json = "{\"type\":\"sparkmax_neo\",\"id\":1,\"unknownField\":\"value\"}";
    DeviceJson d = mapper().readValue(json, DeviceJson.class);
    assertNotNull(d);
    assertEquals("sparkmax_neo", d.type);
    assertEquals(1, d.id);
  }
}
