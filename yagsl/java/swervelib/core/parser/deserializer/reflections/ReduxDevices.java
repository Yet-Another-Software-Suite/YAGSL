package swervelib.core.parser.deserializer.reflections;


import static org.wpilib.units.Units.Rotations;

import com.reduxrobotics.canandgyro.Canandgyro;
import com.reduxrobotics.canandmag.Canandmag;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.util.Pair;
import org.wpilib.units.measure.Angle;
import java.util.function.Supplier;
import swervelib.core.parser.CANBuses;

/**
 * Reflective class for {@link com.reduxrobotics.canandgyro.Canandgyro} and other devices.
 */
public class ReduxDevices
{

  /**
   * Get the gyroscope attitude supplier and gyroscope object.
   *
   * @param canid  CAN ID of the gyroscope.
   * @param canbus CAN bus of the gyroscope, see {@link CANBuses}. Anything other than a bus number is used as a
   *               ReduxLib bus string, such as {@code "socketcan:can_s1"}.
   * @return {@link Pair} of the attitude {@link Supplier} and the gyroscope {@link Object}
   */
  public static Pair<Supplier<Rotation3d>, Object> getGyro(int canid, String canbus)
  {
    var gyro = CANBuses.getCANPort(canbus)
                       .map(port -> new Canandgyro(canid, port))
                       .orElseGet(() -> new Canandgyro(canid, canbus.trim()));
    return Pair.of(gyro::getRotation3d, gyro);
  }

  /**
   * Get the {@link com.reduxrobotics.canandmag.Canandmag} angle.
   *
   * @param canid    CAN ID of the encoder.
   * @param canbus   CAN bus of the encoder, see {@link CANBuses}. Anything other than a bus number is used as a
   *                 ReduxLib bus string, such as {@code "socketcan:can_s1"}.
   * @param inverted Inverted encoder readings.
   * @return {@link Supplier} of {@link Angle} and {@link com.reduxrobotics.canandmag.Canandmag}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus, boolean inverted)
  {
    var encoder = CANBuses.getCANPort(canbus)
                          .map(port -> new Canandmag(canid, port))
                          .orElseGet(() -> new Canandmag(canid, canbus.trim()));
    encoder.setSettings(encoder.getSettings().setInvertDirection(inverted));
    return Pair.of(() -> Rotations.of(encoder.getAbsPosition()), encoder);
  }
}
