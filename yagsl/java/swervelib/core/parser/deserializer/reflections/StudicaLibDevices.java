package swervelib.core.parser.deserializer.reflections;

import com.studica.frc.Navx;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.util.Pair;
import java.util.function.Supplier;

/**
 * StudicaLib Gyroscope and other devices.
 */
public class StudicaLibDevices
{

  /**
   * Get the gyroscope attitude supplier and gyroscope object.
   *
   * @param canid  CAN ID of the gyroscope.
   * @param canbus CAN bus name of the gyroscope.
   * @return {@link Pair} of the attitude {@link Supplier} and the gyroscope {@link Object}
   */
  public static Pair<Supplier<Rotation3d>, Object> getGyro(int canid, String canbus)
  {
    var gyro = new Navx(canid);
    return Pair.of(gyro::getRotation3d, gyro);
  }

}
