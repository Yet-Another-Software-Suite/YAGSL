package swervelib.parser.deserializer.reflections;

import static edu.wpi.first.units.Units.Radians;

import com.andymark.jni.AM_CAN_HexBoreEncoder;
import edu.wpi.first.math.Pair;
import edu.wpi.first.units.measure.Angle;
import java.util.function.Supplier;

/**
 * Andymark Devices
 */
public class AndymarkDevices
{

  /**
   * Get the {@link com.andymark.jni.AM_CAN_HexBoreEncoder} angle.
   *
   * @param canid  CAN ID of the encoder.
   * @param canbus CAN bus name of the encoder.
   * @return {@link Supplier} of {@link Angle} and {@link com.andymark.jni.AM_CAN_HexBoreEncoder}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus)
  {
    var encoder = new AM_CAN_HexBoreEncoder(canid);
    return Pair.of(() -> Radians.of(encoder.getAngleRadians()), encoder);
  }
}
