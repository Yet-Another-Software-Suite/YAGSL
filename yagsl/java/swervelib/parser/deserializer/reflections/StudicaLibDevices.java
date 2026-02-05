package swervelib.parser.deserializer.reflections;

import com.studica.frc.Navx;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Rotation3d;
import java.util.function.Supplier;

/**
 * StudicaLib Gyroscope and other devices.
 */
public class StudicaLibDevices
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
    var gyro = new Navx(canid);
    return Pair.of(gyro::getRotation3d, gyro);
  }

}
