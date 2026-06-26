package swervelib.parser.deserializer.reflections;

import static edu.wpi.first.units.Units.Degrees;

import com.studica.frc.Navx;
import edu.wpi.first.math.Pair;
import edu.wpi.first.units.measure.Angle;
import java.util.function.Supplier;
import swervelib.parser.json.SwerveDriveJson.GyroAxis;

/**
 * StudicaLib Gyroscope and other devices.
 */
public class StudicaLibDevices
{

  /**
   * Get the gyroscope angle supplier and gyroscope object.
   *
   * @param canid    CAN ID of the gyroscope.
   * @param canbus   CAN bus name of the gyroscope.
   * @param axis     Gyro axis.
   * @param inverted Inverted gyro readings.
   * @return {@link Pair} of {@link Supplier} and {@link Object}
   */
  public static Pair<Supplier<Angle>, Object> getGyroAngle(int canid, String canbus, GyroAxis axis, boolean inverted)
  {
    var gyro = new Navx(canid);
    switch (axis)
    {
      case YAW:
        return Pair.of(() -> Degrees.of(gyro.getYaw() * (inverted ? -1 : 1)), gyro);
      case PITCH:
        return Pair.of(() -> Degrees.of(gyro.getPitch() * (inverted ? -1 : 1)), gyro);
      case ROLL:
        return Pair.of(() -> Degrees.of(gyro.getRoll() * (inverted ? -1 : 1)), gyro);
      default: throw new IllegalArgumentException("Invalid gyro axis: " + axis);
    }
  }

}
