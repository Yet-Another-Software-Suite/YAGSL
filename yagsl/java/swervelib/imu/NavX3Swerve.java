package swervelib.imu;

import static edu.wpi.first.units.Units.Degree;
import static edu.wpi.first.units.Units.DegreesPerSecond;
import static edu.wpi.first.units.Units.Milliseconds;

import com.studica.frc.Navx;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.MutAngularVelocity;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.Timer;
import java.util.Optional;

/**
 * Communicates with the NavX({@link Navx}) as the IMU.
 */
public class NavX3Swerve extends SwerveIMU
{

  /**
   * Mutable {@link MutAngularVelocity} for readings.
   */
  private final MutAngularVelocity yawVel   = new MutAngularVelocity(0, 0, DegreesPerSecond);
  /**
   * NavX IMU.
   */
  private       Navx               imu;
  /**
   * Offset for the NavX.
   */
  private       Rotation3d         offset   = new Rotation3d();
  /**
   * An {@link Alert} for if there is an error instantiating the NavX.
   */
  private       Alert              navXError;
  /**
   * Inversion state of the {@link Navx}.
   */
  private       boolean            inverted = false;

  /**
   * Constructor for the NavX({@link com.studica.frc.Navx}) swerve.
   *
   * @param canid CAN ID of the NavX3.
   */
  public NavX3Swerve(Integer canid)
  {
    navXError = new Alert("IMU", "Error instantiating NavX.", AlertType.kError);
    try
    {
      imu = new Navx(canid);

    } catch (RuntimeException ex)
    {
      navXError.setText("Error instantiating NavX: " + ex.getMessage());
      navXError.set(true);
    }
  }

  @Override
  public void close()
  {
//    imu.close();
  }

  /**
   * Reset offset to current gyro reading.
   */
  @Override
  public void factoryDefault()
  {
    // gyro.reset(); // Reported to be slow
    offset = imu.getRotation3d();
  }

  /**
   * Clear sticky faults on IMU.
   */
  @Override
  public void clearStickyFaults()
  {
  }

  /**
   * Set the gyro offset.
   *
   * @param offset gyro offset as a {@link Rotation3d}.
   */
  public void setOffset(Rotation3d offset)
  {
    this.offset = offset;
  }

  /**
   * Set the gyro to invert its default direction
   *
   * @param invertIMU invert gyro direction
   */
  public void setInverted(boolean invertIMU)
  {
    inverted = invertIMU;
//    setOffset(getRawRotation3d());
  }

  private Rotation3d negate(Rotation3d rot) {
    return new Rotation3d(-rot.getX(), -rot.getY(), -rot.getZ());
  }

  /**
   * Fetch the {@link Rotation3d} from the IMU without any zeroing. Robot relative.
   *
   * @return {@link Rotation3d} from the IMU.
   */
  @Override
  public Rotation3d getRawRotation3d()
  {
    Rotation3d rotation = imu.getRotation3d();
    return inverted ? negate(rotation) : rotation;
  }

  /**
   * Fetch the {@link Rotation3d} from the IMU. Robot relative.
   *
   * @return {@link Rotation3d} from the IMU.
   */
  @Override
  public Rotation3d getRotation3d()
  {
    return getRawRotation3d().rotateBy(offset.unaryMinus());
  }

  /**
   * Fetch the acceleration [x, y, z] from the IMU in meters per second squared. If acceleration isn't supported returns
   * empty.
   *
   * @return {@link Translation3d} of the acceleration as an {@link Optional}.
   */
  @Override
  public Optional<Translation3d> getAccel()
  {
    return Optional.empty();
  }

  @Override
  public MutAngularVelocity getYawAngularVelocity()
  {
    // x, y, z
    AngularVelocity[] angularVelocity = imu.getAngularVel();
    return yawVel.mut_replace(angularVelocity[2]);
  }

  /**
   * Get the instantiated NavX({@link Navx}) IMU object.
   *
   * @return IMU object.
   */
  @Override
  public Object getIMU()
  {
    return imu;
  }
}
