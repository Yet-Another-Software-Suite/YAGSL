package swervelib.parser.json;

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
}
