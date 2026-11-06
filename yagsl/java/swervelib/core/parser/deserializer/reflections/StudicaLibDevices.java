package swervelib.core.parser.deserializer.reflections;

import com.studica.frc.Navx;
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
   * Get the gyroscope attitude supplier and gyroscope object.
   *
   * @param canid  CAN ID of the gyroscope.
   * @param canbus CAN bus of the gyroscope, a Systemcore CAN bus number.
   * @return {@link Pair} of the attitude {@link Supplier} and the gyroscope {@link Object}
   */
  public static Pair<Supplier<Rotation3d>, Object> getGyro(int canid, String canbus)
  {
    var gyro = new Navx(canid, CANBuses.requireCANPort(canbus, "Studica"));
    // The navX has no attitude until its first reading arrives, or while it is disconnected, so keep the last one.
    var lastRotation = new AtomicReference<>(Rotation3d.ZERO);
    Supplier<Rotation3d> rotation = () -> {
      gyro.getRotation3d().ifPresent(lastRotation::set);
      return lastRotation.get();
    };
    return Pair.of(rotation, gyro);
  }

}
