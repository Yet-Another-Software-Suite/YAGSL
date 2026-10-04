package swervelib.core.parser.deserializer.reflections;


import static org.wpilib.units.Units.Rotations;

import com.reduxrobotics.canandgyro.Canandgyro;
import com.reduxrobotics.canandmag.Canandmag;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.util.Pair;
import org.wpilib.units.measure.Angle;
import java.util.function.Supplier;

/**
 * Reflective class for {@link com.reduxrobotics.canandgyro.Canandgyro} and other devices.
 */
public class ReduxDevices
{

  // ReduxLib 2027 rejects an empty bus string, so devices on the default bus use the bus-less constructors.

  /**
   * Get the gyroscope attitude supplier and gyroscope object.
   *
   * @param canid  CAN ID of the gyroscope.
   * @param canbus CAN bus name of the gyroscope.
   * @return {@link Pair} of the attitude {@link Supplier} and the gyroscope {@link Object}
   */
  public static Pair<Supplier<Rotation3d>, Object> getGyro(int canid, String canbus)
  {
    var gyro = canbus == null || canbus.isBlank() ? new Canandgyro(canid) : new Canandgyro(canid, canbus);
    return Pair.of(gyro::getRotation3d, gyro);
  }

  /**
   * Get the {@link com.reduxrobotics.canandmag.Canandmag} angle.
   *
   * @param canid    CAN ID of the encoder.
   * @param canbus   CAN bus name of the encoder.
   * @param inverted Inverted encoder readings.
   * @return {@link Supplier} of {@link Angle} and {@link com.reduxrobotics.canandmag.Canandmag}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus, boolean inverted)
  {
    var encoder = canbus == null || canbus.isBlank() ? new Canandmag(canid) : new Canandmag(canid, canbus);
    encoder.setSettings(encoder.getSettings().setInvertDirection(inverted));
    return Pair.of(() -> Rotations.of(encoder.getAbsPosition()), encoder);
  }
}
