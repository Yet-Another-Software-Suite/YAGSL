package swervelib.core.parser.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class SwerveDriveSchemaTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V7);

  // -- Helpers --------------------------------------------------------------

  private JsonSchema getSchema(String schemaFileName) throws IOException {
    try (InputStream in =
        getClass().getClassLoader().getResourceAsStream("schemas/" + schemaFileName)) {
      assertNotNull(in, "schema resource not found: schemas/" + schemaFileName);
      return factory.getSchema(in);
    }
  }

  private JsonNode getInstance(String relativePath) throws IOException {
    try (InputStream in =
        getClass().getClassLoader().getResourceAsStream("swervedrive/" + relativePath)) {
      assertNotNull(in, "instance resource not found: swervedrive/" + relativePath);
      return mapper.readTree(in);
    }
  }

  // -- Schemas are processable ---------------------------------------------

  @Test
  void swervedriveSchema_isProcessable() throws IOException {
    assertNotNull(getSchema("swervedrive.json"));
  }

  @Test
  void moduleSchema_isProcessable() throws IOException {
    assertNotNull(getSchema("module.json"));
  }

  @Test
  void physicalPropertiesSchema_isProcessable() throws IOException {
    assertNotNull(getSchema("physicalproperties.json"));
  }

  @Test
  void pidfPropertiesSchema_isProcessable() throws IOException {
    assertNotNull(getSchema("pidfproperties.json"));
  }

  // -- Schemas validate the real bundled configuration ---------------------

  @Test
  void swervedriveSchema_validatesRealSwervedriveJson() throws IOException {
    Set<ValidationMessage> errors =
        getSchema("swervedrive.json").validate(getInstance("swervedrive.json"));
    assertTrue(errors.isEmpty(), () -> "unexpected validation errors: " + errors);
  }

  @ParameterizedTest
  @ValueSource(strings = {"fl.json", "fr.json", "bl.json", "br.json"})
  void moduleSchema_validatesRealModuleJson(String moduleFile) throws IOException {
    Set<ValidationMessage> errors =
        getSchema("module.json").validate(getInstance("modules/" + moduleFile));
    assertTrue(errors.isEmpty(), () -> "unexpected validation errors: " + errors);
  }

  @Test
  void physicalPropertiesSchema_validatesRealPhysicalPropertiesJson() throws IOException {
    Set<ValidationMessage> errors =
        getSchema("physicalproperties.json")
            .validate(getInstance("modules/physicalproperties.json"));
    assertTrue(errors.isEmpty(), () -> "unexpected validation errors: " + errors);
  }

  @Test
  void pidfPropertiesSchema_validatesRealPidfPropertiesJson() throws IOException {
    Set<ValidationMessage> errors =
        getSchema("pidfproperties.json").validate(getInstance("modules/pidfproperties.json"));
    assertTrue(errors.isEmpty(), () -> "unexpected validation errors: " + errors);
  }

  // -- Schemas reject configuration missing required fields ----------------

  @Test
  void swervedriveSchema_rejectsMissingGyro() throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("swervedrive.json");
    instance.remove("gyro");
    Set<ValidationMessage> errors = getSchema("swervedrive.json").validate(instance);
    assertFalse(errors.isEmpty(), "expected validation errors for missing 'gyro' field");
  }

  @Test
  void swervedriveSchema_acceptsNavx3OnAUsbPort() throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("swervedrive.json");
    ((ObjectNode) instance.get("gyro")).put("type", "navx3_usb").put("channel", 2);
    Set<ValidationMessage> errors = getSchema("swervedrive.json").validate(instance);
    assertTrue(errors.isEmpty(), () -> "expected navx3_usb on USB2 to be valid, got " + errors);
  }

  @Test
  void swervedriveSchema_rejectsAUsbPortThatDoesNotExist() throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("swervedrive.json");
    ((ObjectNode) instance.get("gyro")).put("type", "navx3_usb").put("channel", 4);
    Set<ValidationMessage> errors = getSchema("swervedrive.json").validate(instance);
    assertFalse(errors.isEmpty(), "expected USB port 4 to be rejected");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "",
        "0",
        "4",
        "5",
        "24",
        "CAN_S0",
        "CAN_S4",
        "CAN_D0",
        "CAN_D19",
        "rio",
        "My CANivore"
      })
  void schemas_acceptEveryCanbus(String canbus) throws IOException {
    ObjectNode module = (ObjectNode) getInstance("modules/fl.json");
    ((ObjectNode) module.get("drive")).put("canbus", canbus);
    ((ObjectNode) module.get("angle")).put("canbus", canbus);
    ((ObjectNode) module.get("absoluteEncoder")).put("canbus", canbus);
    Set<ValidationMessage> moduleErrors = getSchema("module.json").validate(module);
    assertTrue(
        moduleErrors.isEmpty(),
        () -> "expected canbus \"" + canbus + "\" to be valid, got " + moduleErrors);

    ObjectNode swerve = (ObjectNode) getInstance("swervedrive.json");
    ((ObjectNode) swerve.get("gyro")).put("canbus", canbus);
    Set<ValidationMessage> swerveErrors = getSchema("swervedrive.json").validate(swerve);
    assertTrue(
        swerveErrors.isEmpty(),
        () -> "expected canbus \"" + canbus + "\" to be valid, got " + swerveErrors);
  }

  @ParameterizedTest
  @ValueSource(strings = {"25", "100"})
  void schemas_rejectACanbusNumberThatDoesNotExist(String canbus) throws IOException {
    ObjectNode module = (ObjectNode) getInstance("modules/fl.json");
    ((ObjectNode) module.get("drive")).put("canbus", canbus);
    assertFalse(
        getSchema("module.json").validate(module).isEmpty(),
        "expected canbus " + canbus + " to be rejected");

    ObjectNode swerve = (ObjectNode) getInstance("swervedrive.json");
    ((ObjectNode) swerve.get("gyro")).put("canbus", canbus);
    assertFalse(
        getSchema("swervedrive.json").validate(swerve).isEmpty(),
        "expected canbus " + canbus + " to be rejected");
  }

  @ParameterizedTest
  @ValueSource(strings = {"talonfx_falcon", "talonfx_falcon500"})
  void moduleSchema_acceptsFalconMotors(String type) throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("modules/fl.json");
    ((ObjectNode) instance.get("drive")).put("type", type);
    ((ObjectNode) instance.get("angle")).put("type", type);
    // A TalonFX angle motor can't read an absolute encoder plugged into it, so the module uses a
    // CANcoder.
    ((ObjectNode) instance.get("absoluteEncoder")).put("type", "cancoder_can");
    Set<ValidationMessage> errors = getSchema("module.json").validate(instance);
    assertTrue(errors.isEmpty(), "expected " + type + " to be a valid motor type, got " + errors);
  }

  @Test
  void moduleSchema_rejectsMissingDrive() throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("modules/fl.json");
    instance.remove("drive");
    Set<ValidationMessage> errors = getSchema("module.json").validate(instance);
    assertFalse(errors.isEmpty(), "expected validation errors for missing 'drive' field");
  }

  @Test
  void physicalPropertiesSchema_rejectsMissingGearing() throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("modules/physicalproperties.json");
    instance.remove("gearing");
    Set<ValidationMessage> errors = getSchema("physicalproperties.json").validate(instance);
    assertFalse(errors.isEmpty(), "expected validation errors for missing 'gearing' field");
  }

  @Test
  void pidfPropertiesSchema_rejectsMissingDrive() throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("modules/pidfproperties.json");
    instance.remove("drive");
    Set<ValidationMessage> errors = getSchema("pidfproperties.json").validate(instance);
    assertFalse(errors.isEmpty(), "expected validation errors for missing 'drive' field");
  }

  // -- Absolute encoders plugged into the angle motor controller ---------------

  /**
   * The module schema flags an absolute encoder the angle motor controller can't read, which YAGSL fails on at startup:
   * any _attached type on a TalonFX or TalonFXS, thrifty_attached without a Nova, and analog5v_attached on a Nova.
   */
  @ParameterizedTest
  @CsvSource({
    "talonfx_krakenx60, revthroughbore_attached, false",
    "talonfxs_minion,   dutycycle_attached,      false",
    "talonfx_krakenx60, cancoder_can,            true",
    "sparkmax_neo,      thrifty_attached,        false",
    "sparkflex_vortex,  thrifty_attached,        false",
    "sparkmax_neo,      thrifty_analog,          true",
    "nova_neo,          thrifty_attached,        true",
    "nova_neo,          analog5v_attached,       false",
    "nova_neo,          analog_attached,         true",
    "sparkmax_neo,      analog5v_attached,       true",
    "sparkflex_vortex,  revthroughbore_attached, true"
  })
  void moduleSchema_checksAttachedEncoders(String angle, String encoder, boolean valid)
      throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("modules/fl.json");
    ((ObjectNode) instance.get("angle")).put("type", angle);
    ((ObjectNode) instance.get("absoluteEncoder")).put("type", encoder);
    Set<ValidationMessage> errors = getSchema("module.json").validate(instance);
    assertEquals(
        valid, errors.isEmpty(), encoder + " on a " + angle + " angle motor controller: " + errors);
  }

  /**
   * The module schema only takes an absolute encoder gear ratio above 0 and up to 1: the encoder turns that many times
   * per module turn, and an absolute encoder only reads one rotation.
   */
  @ParameterizedTest
  @CsvSource({
    "1, true",
    "0.5, true",
    "0.01, true",
    "1.01, false",
    "2, false",
    "0, false",
    "-1, false"
  })
  void moduleSchema_checksAbsoluteEncoderGearRatio(double gearRatio, boolean valid)
      throws IOException {
    ObjectNode instance = (ObjectNode) getInstance("modules/fl.json");
    instance.put("absoluteEncoderGearRatio", gearRatio);
    Set<ValidationMessage> errors = getSchema("module.json").validate(instance);
    assertEquals(valid, errors.isEmpty(), "absoluteEncoderGearRatio " + gearRatio + ": " + errors);
  }
}
