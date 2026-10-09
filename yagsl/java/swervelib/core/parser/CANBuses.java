package swervelib.core.parser;

import java.util.Optional;
import org.wpilib.hardware.bus.CANPort;

/**
 * Turns the {@code canbus} of a device in the JSON configuration into a CAN bus, the same way for every vendor.
 *
 * <ul>
 *   <li>Empty: the Systemcore CAN bus {@code can_s0}.</li>
 *   <li>A bus number: {@code "0"} to {@code "4"} are the Systemcore CAN buses {@code can_s0} to {@code can_s4},
 *   so {@code "1"} is {@code can_s1}. {@code "5"} to {@code "24"} are the Motioncore CAN buses {@code can_d0} to
 *   {@code can_d19}, following {@link CANPort}.</li>
 *   <li>Anything else is a vendor specific CAN bus, such as the name of a CANivore for CTRE devices. Vendors that
 *   only use the Systemcore and Motioncore CAN buses reject it.</li>
 * </ul>
 */
public final class CANBuses
{

  /**
   * CAN bus used when a device leaves {@code canbus} empty.
   */
  public static final CANPort DEFAULT = CANPort.CAN_S0;

  private CANBuses()
  {
  }

  /**
   * Get the {@link CANPort} for the {@code canbus} of a device.
   *
   * @param canbus {@code canbus} from the JSON configuration.
   * @return The {@link CANPort}, or empty when {@code canbus} names a vendor specific CAN bus.
   * @throws IllegalArgumentException if {@code canbus} is a bus number that is not a {@link CANPort}.
   */
  public static Optional<CANPort> getCANPort(String canbus)
  {
    if (canbus == null || canbus.isBlank())
    {
      return Optional.of(DEFAULT);
    }
    String number = canbus.trim();
    if (!number.chars().allMatch(Character::isDigit))
    {
      return Optional.empty();
    }
    for (CANPort port : CANPort.values())
    {
      if (Integer.toString(port.value).equals(number))
      {
        return Optional.of(port);
      }
    }
    throw new IllegalArgumentException(
        "CAN bus " + number + " does not exist. Use 0 to 4 for the Systemcore CAN buses (can_s0 to can_s4) or 5 to "
        + "24 for the Motioncore CAN buses (can_d0 to can_d19).");
  }

  /**
   * Get the {@link CANPort} for the {@code canbus} of a device from a vendor that only uses the Systemcore and
   * Motioncore CAN buses.
   *
   * @param canbus {@code canbus} from the JSON configuration.
   * @param vendor Vendor name, for the error message.
   * @return The {@link CANPort}.
   * @throws IllegalArgumentException if {@code canbus} is not a Systemcore or Motioncore CAN bus number.
   */
  public static CANPort requireCANPort(String canbus, String vendor)
  {
    return getCANPort(canbus).orElseThrow(() -> new IllegalArgumentException(
        vendor + " devices only use the Systemcore and Motioncore CAN buses, but canbus is \"" + canbus
        + "\". Use \"\" for can_s0, or the bus number, such as \"1\" for can_s1."));
  }
}
