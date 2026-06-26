package swervelib.parser;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class PIDFConfigTest {

  private final ObjectMapper mapper =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  // ── Default construction ─────────────────────────────────────────────────

  @Test
  void testDefaultConstructionPIsZero() {
    PIDFConfig config = new PIDFConfig();
    assertEquals(0.0, config.p, 0.0);
  }

  @Test
  void testDefaultConstructionIIsZero() {
    PIDFConfig config = new PIDFConfig();
    assertEquals(0.0, config.i, 0.0);
  }

  @Test
  void testDefaultConstructionDIsZero() {
    PIDFConfig config = new PIDFConfig();
    assertEquals(0.0, config.d, 0.0);
  }

  // ── Field assignment ─────────────────────────────────────────────────────

  @Test
  void testFieldAssignmentP() {
    PIDFConfig config = new PIDFConfig();
    config.p = 1.5;
    assertEquals(1.5, config.p, 0.0);
  }

  @Test
  void testFieldAssignmentI() {
    PIDFConfig config = new PIDFConfig();
    config.i = 0.01;
    assertEquals(0.01, config.i, 0.0);
  }

  @Test
  void testFieldAssignmentD() {
    PIDFConfig config = new PIDFConfig();
    config.d = 0.1;
    assertEquals(0.1, config.d, 0.0);
  }

  @Test
  void testFieldAssignmentAllValues() {
    PIDFConfig config = new PIDFConfig();
    config.p = 1.5;
    config.i = 0.01;
    config.d = 0.1;
    assertEquals(1.5, config.p, 0.0);
    assertEquals(0.01, config.i, 0.0);
    assertEquals(0.1, config.d, 0.0);
  }

  // ── JSON deserialization ─────────────────────────────────────────────────

  @Test
  void testJsonDeserializationP() throws IOException {
    String json = "{\"p\": 2.5, \"i\": 0.001, \"d\": 0.05}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(2.5, config.p, 0.0001);
  }

  @Test
  void testJsonDeserializationI() throws IOException {
    String json = "{\"p\": 2.5, \"i\": 0.001, \"d\": 0.05}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(0.001, config.i, 0.00001);
  }

  @Test
  void testJsonDeserializationD() throws IOException {
    String json = "{\"p\": 2.5, \"i\": 0.001, \"d\": 0.05}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(0.05, config.d, 0.0001);
  }

  // ── JSON zeroes ──────────────────────────────────────────────────────────

  @Test
  void testJsonZeroesP() throws IOException {
    String json = "{\"p\": 0.0, \"i\": 0.0, \"d\": 0.0}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(0.0, config.p, 0.0);
  }

  @Test
  void testJsonZeroesI() throws IOException {
    String json = "{\"p\": 0.0, \"i\": 0.0, \"d\": 0.0}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(0.0, config.i, 0.0);
  }

  @Test
  void testJsonZeroesD() throws IOException {
    String json = "{\"p\": 0.0, \"i\": 0.0, \"d\": 0.0}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(0.0, config.d, 0.0);
  }

  // ── JSON large values ────────────────────────────────────────────────────

  @Test
  void testJsonLargeValuesP() throws IOException {
    String json = "{\"p\": 100.0, \"i\": 0.5, \"d\": 10.0}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(100.0, config.p, 0.0001);
  }

  @Test
  void testJsonLargeValuesI() throws IOException {
    String json = "{\"p\": 100.0, \"i\": 0.5, \"d\": 10.0}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(0.5, config.i, 0.0001);
  }

  @Test
  void testJsonLargeValuesD() throws IOException {
    String json = "{\"p\": 100.0, \"i\": 0.5, \"d\": 10.0}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(10.0, config.d, 0.0001);
  }

  // ── FAIL_ON_UNKNOWN_PROPERTIES=false ignores extra fields ────────────────

  @Test
  void testJsonIgnoresExtraFieldsDoesNotThrow() throws IOException {
    String json = "{\"p\": 1.0, \"i\": 0.0, \"d\": 0.5, \"f\": 0.1, \"iZone\": 0.05}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertNotNull(config);
  }

  @Test
  void testJsonIgnoresExtraFieldsPreservesP() throws IOException {
    String json = "{\"p\": 1.0, \"i\": 0.0, \"d\": 0.5, \"f\": 0.1, \"iZone\": 0.05}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(1.0, config.p, 0.0001);
  }

  @Test
  void testJsonIgnoresExtraFieldsPreservesD() throws IOException {
    String json = "{\"p\": 1.0, \"i\": 0.0, \"d\": 0.5, \"f\": 0.1, \"iZone\": 0.05}";
    PIDFConfig config = mapper.readValue(json, PIDFConfig.class);
    assertEquals(0.5, config.d, 0.0001);
  }
}
