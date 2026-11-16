package swervelib.core.parser.deserializer.reflections;

import com.studica.frc.Navx;
import com.studica.frc.UsbPort;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.util.Pair;
import swervelib.core.parser.CANBuses;

/**
 * StudicaLib Gyroscope and other devices.
 */
public class StudicaLibDevices
{

  /**
   * Get the gyroscope attitude supplier and gyroscope object of a navX3 on CAN.
   *
   * @param canid  CAN ID of the gyroscope.
   * @param canbus CAN bus of the gyroscope, a Systemcore CAN bus number.
   * @return {@link Pair} of the attitude {@link Supplier} and the gyroscope {@link Object}
   */
  public static Pair<Supplier<Rotation3d>, Object> getGyro(int canid, String canbus)
  {
    return attitude(new Navx(canid, CANBuses.requireCANPort(canbus, "Studica")));
  }

  /**
   * Get the gyroscope attitude supplier and gyroscope object of a navX3 plugged into one of the Systemcore's USB ports.
   *
   * @param port USB port number, 0 to 3 for {@link UsbPort#USB0} to {@link UsbPort#USB3}.
   * @return {@link Pair} of the attitude {@link Supplier} and the gyroscope {@link Object}
   * @throws IllegalArgumentException if the port is not a {@link UsbPort}.
   */
  public static Pair<Supplier<Rotation3d>, Object> getUsbGyro(int port)
  {
    UsbPort[] ports = UsbPort.values();
    if (port < 0 || port >= ports.length)
    {
      throw new IllegalArgumentException(
          "USB port " + port + " does not exist. Use 0 to " + (ports.length - 1) + " for USB0 to USB" + (ports.length - 1)
          + ".");
    }
    return attitude(Navx.usb(ports[port]));
  }

  /**
   * The navX has no attitude until its first reading arrives, or while it is disconnected, so keep the last one.
   *
   * @param gyro navX to read.
   * @return {@link Pair} of the attitude {@link Supplier} and the gyroscope.
   */
  private static Pair<Supplier<Rotation3d>, Object> attitude(Navx gyro)
  {
    var lastRotation = new AtomicReference<>(Rotation3d.ZERO);
    Supplier<Rotation3d> rotation = () -> {
      gyro.getRotation3d().ifPresent(lastRotation::set);
      return lastRotation.get();
    };
    return Pair.of(rotation, gyro);
  }

}
