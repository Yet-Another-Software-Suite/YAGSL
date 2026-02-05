package swervelib.parser.deserializer.reflections;


import static edu.wpi.first.units.Units.Rotations;

import com.reduxrobotics.sensors.canandgyro.Canandgyro;
import com.reduxrobotics.sensors.canandmag.Canandmag;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.units.measure.Angle;
import java.util.function.Supplier;

/**
 * Reflective class for {@link com.reduxrobotics.sensors.canandgyro.Canandgyro} and other devices.
 */
public class ReduxDevices
{

  /**
   * Get the gyroscope angle supplier and gyroscope object.
   *
   * @param canid  CAN ID of the gyroscope.
   * @param canbus CAN bus name of the gyroscope.
   * @return {@link Pair} of {@link Supplier} and {@link Object}
   */
  public static Pair<Supplier<Rotation3d>, Object> getGyroAngle(int canid, String canbus)
  {
    var gyro = new Canandgyro(canid, canbus);
    return Pair.of(gyro::getRotation3d, gyro);
  }

  /**
   * Get the {@link com.reduxrobotics.sensors.canandmag.Canandmag} angle.
   *
   * @param canid  CAN ID of the encoder.
   * @param canbus CAN bus name of the encoder.
   * @return {@link Supplier} of {@link Angle} and {@link com.reduxrobotics.sensors.canandmag.Canandmag}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus)
  {
    var encoder = new Canandmag(canid, canbus);
    return Pair.of(() -> Rotations.of(encoder.getAbsPosition()), encoder);
  }
}
