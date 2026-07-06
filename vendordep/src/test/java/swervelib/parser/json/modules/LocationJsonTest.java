package swervelib.parser.json.modules;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class LocationJsonTest {

  private final ObjectMapper mapper =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @Test
  void testDefaultValues() {
    LocationJson obj = new LocationJson();
    assertEquals(0.0, obj.front);
    assertEquals(0.0, obj.left);
  }

  @Test
  void testFullJsonParse() throws Exception {
    LocationJson obj = mapper.readValue("{\"front\": 12.5, \"left\": -7.25}", LocationJson.class);
    assertEquals(12.5, obj.front);
    assertEquals(-7.25, obj.left);
  }

  @Test
  void testOnlyFrontProvided() throws Exception {
    LocationJson obj = mapper.readValue("{\"front\": 8.0}", LocationJson.class);
    assertEquals(8.0, obj.front);
    assertEquals(0.0, obj.left);
  }

  @Test
  void testOnlyLeftProvided() throws Exception {
    LocationJson obj = mapper.readValue("{\"left\": 5.5}", LocationJson.class);
    assertEquals(0.0, obj.front);
    assertEquals(5.5, obj.left);
  }

  @Test
  void testNegativeValues() throws Exception {
    LocationJson obj = mapper.readValue("{\"front\": -10.0, \"left\": -10.0}", LocationJson.class);
    assertEquals(-10.0, obj.front);
    assertEquals(-10.0, obj.left);
  }

  @Test
  void testZeroValuesExplicitlySet() throws Exception {
    LocationJson obj = mapper.readValue("{\"front\": 0.0, \"left\": 0.0}", LocationJson.class);
    assertEquals(0.0, obj.front);
    assertEquals(0.0, obj.left);
  }
}
