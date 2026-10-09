package swervelib.core.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.wpilib.hardware.bus.CANPort;

class CANBusesTest {

  @Test
  void emptyIsCanS0() {
    assertEquals(Optional.of(CANPort.CAN_S0), CANBuses.getCANPort(""));
    assertEquals(Optional.of(CANPort.CAN_S0), CANBuses.getCANPort("  "));
    assertEquals(Optional.of(CANPort.CAN_S0), CANBuses.getCANPort(null));
  }

  @ParameterizedTest
  @CsvSource({"0, CAN_S0", "1, CAN_S1", "2, CAN_S2", "3, CAN_S3", "4, CAN_S4", "5, CAN_D0", "24, CAN_D19", "' 1 ', CAN_S1"})
  void busNumberIsThatCanPort(String canbus, CANPort expected) {
    assertEquals(Optional.of(expected), CANBuses.getCANPort(canbus));
  }

  @ParameterizedTest
  @ValueSource(strings = {"25", "99", "123456789012345678901234567890"})
  void unknownBusNumberThrows(String canbus) {
    var e = assertThrows(IllegalArgumentException.class, () -> CANBuses.getCANPort(canbus));
    assertTrue(e.getMessage().contains("does not exist"), e.getMessage());
  }

  @ParameterizedTest
  @ValueSource(strings = {"canivore", "*", "can_s1", "socketcan:can_s1"})
  void anythingElseIsVendorSpecific(String canbus) {
    assertEquals(Optional.empty(), CANBuses.getCANPort(canbus));
  }

  @Test
  void requireCANPortRejectsVendorSpecificBuses() {
    assertEquals(CANPort.CAN_S2, CANBuses.requireCANPort("2", "REV"));
    var e = assertThrows(IllegalArgumentException.class, () -> CANBuses.requireCANPort("canivore", "Thrifty Bot"));
    assertTrue(e.getMessage().startsWith("Thrifty Bot devices only use"), e.getMessage());
    assertTrue(e.getMessage().contains("\"1\" for can_s1"), e.getMessage());
  }
}
