package swervelib.core.parser.json.modules;

import static org.junit.jupiter.api.Assertions.*;

import io.avaje.jsonb.Jsonb;
import org.junit.jupiter.api.Test;

class LocationJsonTest {

  private final Jsonb mapper = Jsonb.builder().build();

  @Test
  void testDefaultValues() {
    LocationJson obj = new LocationJson();
    assertEquals(0.0, obj.front);
    assertEquals(0.0, obj.left);
  }

  @Test
  void testFullJsonParse() throws Exception {
    LocationJson obj =
        mapper.type(LocationJson.class).fromJson("{\"front\": 12.5, \"left\": -7.25}");
    assertEquals(12.5, obj.front);
    assertEquals(-7.25, obj.left);
  }

  @Test
  void testOnlyFrontProvided() throws Exception {
    LocationJson obj = mapper.type(LocationJson.class).fromJson("{\"front\": 8.0}");
    assertEquals(8.0, obj.front);
    assertEquals(0.0, obj.left);
  }

  @Test
  void testOnlyLeftProvided() throws Exception {
    LocationJson obj = mapper.type(LocationJson.class).fromJson("{\"left\": 5.5}");
    assertEquals(0.0, obj.front);
    assertEquals(5.5, obj.left);
  }

  @Test
  void testNegativeValues() throws Exception {
    LocationJson obj =
        mapper.type(LocationJson.class).fromJson("{\"front\": -10.0, \"left\": -10.0}");
    assertEquals(-10.0, obj.front);
    assertEquals(-10.0, obj.left);
  }

  @Test
  void testZeroValuesExplicitlySet() throws Exception {
    LocationJson obj = mapper.type(LocationJson.class).fromJson("{\"front\": 0.0, \"left\": 0.0}");
    assertEquals(0.0, obj.front);
    assertEquals(0.0, obj.left);
  }
}
