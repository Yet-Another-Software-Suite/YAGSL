package swervelib.core.parser.deserializer.reflections;

import static org.wpilib.units.Units.Radians;

import com.andymark.jni.AM_CAN_HexBoreEncoder;
import org.wpilib.util.Pair;
import org.wpilib.units.measure.Angle;
import java.util.function.Supplier;

/**
 * Andymark Devices
 */
public class AndymarkDevices
{

  /**
   * Get the {@link com.andymark.jni.AM_CAN_HexBoreEncoder} angle.
   *
   * @param canid    CAN ID of the encoder.
   * @param canbus   CAN bus name of the encoder.
   * @param inverted Inverted encoder readings.
   * @return {@link Supplier} of {@link Angle} and {@link com.andymark.jni.AM_CAN_HexBoreEncoder}
   */
  public static Pair<Supplier<Angle>, Object> getAbsoluteEncoder(int canid, String canbus, boolean inverted)
  {
    var encoder = new AM_CAN_HexBoreEncoder(canid);
    return Pair.of(() -> Radians.of(encoder.getAngleRadians() * (inverted ? -1 : 1)), encoder);
  }
}
