package swervelib.commands2;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.wpilib.units.Units.Milliseconds;
import static org.wpilib.units.Units.Seconds;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import org.wpilib.units.measure.Time;
import yams.core.motorcontrollers.SmartMotorController;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.wpilib.command2.CommandScheduler;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.hardware.hal.HAL;
import org.wpilib.hardware.hal.RobotMode;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import org.wpilib.simulation.DriverStationSim;
import org.wpilib.simulation.SimHooks;
import yams.commands2.config.SwerveDriveConfig;
import yams.commands2.swerve.SwerveDrive;
import yams.core.mechanisms.swerve.SwerveModule;

/**
 * Drives a simulated swerve drive with the default gains the config generator (config.yagsl.com) fills in for each
 * motor type, and checks that the azimuth reaches a 90 degree turn and the drive reaches 3 meters per second on common
 * module gearings. The defaults are PID only: s, v and a stay 0 (YAGSL derives the drive's kV from the motor's free
 * speed and the gearing), and tuning the feedforward is left to the user. Keep these gains the same as the config
 * generator's lib/default-gains.ts.
 *
 * <p>TalonFX and TalonFXS drives run in real time with a 1 ms simulation period, as Phoenix simulates them on its own
 * thread against the clock. The other vendors' time is stepped a robot loop at a time.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class SimGainsTest {

  @TempDir static Path tempDir;

  /** Motor types this class checks, in a JVM of its own: at most {@link #MAX_DRIVES_PER_JVM}. */
  abstract String[] motors();

  private static final double LOOP = 0.02;
  private static final double AZIMUTH_TARGET_DEG = 90;
  private static final double DRIVE_TARGET_MPS = 3;

  /** Drive p, angle p, angle d, the same as the config generator's lib/default-gains.ts. The rest are 0. */
  static final Map<String, double[]> DEFAULT_GAINS = Map.ofEntries(
      Map.entry("talonfx_krakenx44", new double[]{1, 15, 0.3}),
      Map.entry("talonfx_krakenx60", new double[]{1, 15, 0.3}),
      Map.entry("talonfx_falcon", new double[]{1, 15, 0.3}),
      Map.entry("talonfxs_neo", new double[]{1, 15, 0.3}),
      Map.entry("talonfxs_neo2", new double[]{1, 15, 0.3}),
      Map.entry("talonfxs_neo550", new double[]{1, 8, 0.6}),
      Map.entry("talonfxs_vortex", new double[]{1, 15, 0.3}),
      Map.entry("talonfxs_pulsar", new double[]{1, 15, 0.3}),
      Map.entry("sparkmax_neo", new double[]{1, 40, 0.3}),
      Map.entry("sparkmax_neo2", new double[]{1, 40, 0.3}),
      Map.entry("sparkmax_neo550", new double[]{4, 12, 0.6}),
      Map.entry("sparkmax_vortex", new double[]{1, 40, 0.3}),
      Map.entry("sparkmax_pulsar", new double[]{1, 30, 0.3}),
      Map.entry("sparkmax_minion", new double[]{1, 30, 0.3}),
      Map.entry("talonfxs_minion", new double[]{1, 15, 0.3}),
      Map.entry("sparkflex_neo", new double[]{1, 40, 0.3}),
      Map.entry("sparkflex_neo2", new double[]{1, 40, 0.3}),
      Map.entry("sparkflex_neo550", new double[]{4, 12, 0.6}),
      Map.entry("sparkflex_vortex", new double[]{1, 40, 0.3}),
      Map.entry("sparkflex_minion", new double[]{1, 30, 0.3}),
      Map.entry("sparkflex_pulsar", new double[]{1, 30, 0.3}),
      Map.entry("nova_neo", new double[]{2, 40, 0.3}),
      Map.entry("nova_neo2", new double[]{2, 40, 0.3}),
      Map.entry("nova_neo550", new double[]{4, 12, 0.6}),
      Map.entry("nova_vortex", new double[]{2, 40, 0.3}),
      Map.entry("nova_minion", new double[]{2, 30, 0.3}),
      Map.entry("nova_pulsar", new double[]{2, 30, 0.3}));

  /**
   * Drive gear ratio, azimuth gear ratio: the config generator's default. The gains were also checked on the
   * examples' 5.36:1 and 18.75:1 and an MK4i L2's 6.75:1 and 21.43:1 when they were picked.
   */
  private static final double[][] GEARINGS = {{6.75, 12.8}};

  /**
   * Drives built so far in this JVM. Each gets its own CAN IDs: vendor simulations keep every device a JVM creates,
   * and match simulated devices by CAN ID whatever the bus, so drives must never share an ID.
   */
  private static int drivesBuilt = 0;

  /**
   * At most 4 drives per JVM, so their IDs never overlap, even across device types: motors 1 to 32, CANcoders 33 to
   * 48 and Pigeons 49 to 52. The CTRE simulation mixes up a Talon and a CANcoder that share an ID.
   */
  static final int MAX_DRIVES_PER_JVM = 4;

  Stream<Arguments> gains() {
    Stream.Builder<Arguments> args = Stream.builder();
    for (String motor : motors()) {
      for (double[] gearing : GEARINGS) {
        args.add(Arguments.of(motor, gearing[0], gearing[1]));
      }
    }
    return args.build();
  }

  static String pidf(String motor) {
    double[] g = DEFAULT_GAINS.get(motor);
    return String.format(Locale.ROOT,
        "{\"drive\":{\"p\":%s,\"i\":0,\"d\":0,\"s\":0,\"v\":0,\"a\":0},"
            + "\"angle\":{\"p\":%s,\"i\":0,\"d\":%s,\"s\":0,\"v\":0,\"a\":0}}", g[0], g[1], g[2]);
  }

  @BeforeAll
  static void setUp() {
    drivesBuilt = 0;
    HAL.initialize();
    DriverStationSim.setDsAttached(true);
    DriverStationSim.setRobotMode(RobotMode.TELEOPERATED);
    DriverStationSim.setEnabled(true);
    DriverStationSim.notifyNewData();
  }

  @AfterAll
  static void tearDown() {
    SimHooks.resumeTiming();
  }

  private static Path writeConfig(String motor, double driveGearing, double azimuthGearing, String pidf)
      throws IOException {
    Path dir = Files.createTempDirectory(tempDir, motor);
    Path modules = Files.createDirectories(dir.resolve("modules"));
    String[] names = {"fl", "fr", "bl", "br"};
    if (drivesBuilt >= MAX_DRIVES_PER_JVM) {
      throw new IllegalStateException("Out of CAN IDs: build at most " + MAX_DRIVES_PER_JVM + " drives per test JVM");
    }
    int drive = drivesBuilt++;
    Files.writeString(dir.resolve("swervedrive.json"),
        "{\"gyro\":{\"type\":\"pigeon2_can\",\"id\":" + (49 + drive) + ",\"canbus\":\"\"},"
            + "\"gyroAxis\":\"yaw\",\"gyroInvert\":false,"
            + "\"modules\":[\"fl.json\",\"fr.json\",\"bl.json\",\"br.json\"]}");
    Files.writeString(modules.resolve("pidfproperties.json"), pidf);
    Files.writeString(modules.resolve("physicalproperties.json"), String.format(Locale.ROOT,
        "{\"gearing\":{\"drive\":{\"gearRatio\":%s,\"diameter\":4},\"angle\":{\"gearRatio\":%s}},"
            + "\"statorCurrentLimit\":{\"drive\":40,\"angle\":20}}", driveGearing, azimuthGearing));
    for (int i = 0; i < names.length; i++) {
      double front = i < 2 ? 10.875 : -10.875;
      double left = i % 2 == 0 ? 10.875 : -10.875;
      int driveId = 1 + 8 * drive + 2 * i;
      int angleId = driveId + 1;
      Files.writeString(modules.resolve(names[i] + ".json"),
          "{\"drive\":{\"type\":\"" + motor + "\",\"id\":" + driveId + ",\"canbus\":\"\"},"
              + "\"angle\":{\"type\":\"" + motor + "\",\"id\":" + angleId + ",\"canbus\":\"\"},"
              + "\"absoluteEncoder\":{\"type\":\"cancoder_can\",\"id\":" + (33 + 4 * drive + i) + ",\"canbus\":\"\"},"
              + "\"inverted\":{\"drive\":false,\"angle\":false},"
              + "\"absoluteEncoderOffset\":0,\"absoluteEncoderInverted\":false,"
              + "\"location\":{\"front\":" + front + ",\"left\":" + left + "}}");
    }
    return dir;
  }

  /**
   * Whether the drive being measured simulates in real time. Phoenix simulates a TalonFX or TalonFXS on its own thread
   * against the clock, so those drives run in real time with a 1 ms simulation period. The other vendors simulate in
   * step with the robot loop, so their time is stepped 20 ms at a time.
   */
  private static boolean realTime = false;

  /** Simulation period of a real time drive. */
  private static final Time REAL_TIME_PERIOD = Milliseconds.of(1);

  /** Run one 20 ms robot loop. */
  static void step(SwerveDrive drive) {
    if (realTime) {
      long period = Math.round(REAL_TIME_PERIOD.in(Seconds) * 1e9);
      long next = System.nanoTime();
      for (int i = 0; i < Math.round(LOOP / REAL_TIME_PERIOD.in(Seconds)); i++) {
        drive.simIterate();
        next += period;
        while (System.nanoTime() < next) {
          Thread.onSpinWait();
        }
      }
    } else {
      SimHooks.stepTiming(LOOP);
      drive.simIterate();
    }
    drive.updateTelemetry();
  }

  /** How one module answered a 0 to 90 degree azimuth step and a 0 to 3 m/s drive step. */
  record Response(double azimuthSettle, double azimuthPeak, double azimuthFinal,
                  double driveSettle, double drivePeak, double driveFinal) {

    boolean passes() {
      return azimuthSettle <= 0.75
             && azimuthPeak <= AZIMUTH_TARGET_DEG + 15
             && Math.abs(azimuthFinal - AZIMUTH_TARGET_DEG) <= 1.5
             && driveSettle <= 1.0
             && drivePeak <= DRIVE_TARGET_MPS * 1.1
             && Math.abs(driveFinal - DRIVE_TARGET_MPS) <= 0.03 * DRIVE_TARGET_MPS;
    }

    @Override
    public String toString() {
      return String.format(Locale.ROOT,
          "azimuth: settle %.2fs peak %.1f final %.2f deg | drive: settle %.2fs peak %.2f final %.3f m/s",
          azimuthSettle, azimuthPeak, azimuthFinal, driveSettle, drivePeak, driveFinal);
    }
  }

  /** Time the value last came within the band and stayed, or NaN if it ended outside it. */
  private static double settle(double settled, double value, double target, double band, int i) {
    if (Math.abs(value - target) <= band) {
      return Double.isNaN(settled) ? (i + 1) * LOOP : settled;
    }
    return Double.NaN;
  }

  /** Error from the target, folded so a module pointing the opposite way has no error. */
  static double foldedError(double angleDeg, double targetDeg) {
    double e = Math.IEEEremainder(targetDeg - angleDeg, 360);
    if (e > 90) {
      e -= 180;
    } else if (e < -90) {
      e += 180;
    }
    return e;
  }

  static SwerveDrive build(String motor, double driveGearing, double azimuthGearing, String pidf)
      throws IOException {
    // Time runs while the devices are created: vendor configuration calls wait on it.
    SimHooks.resumeTiming();
    SwerveParser.parse(writeConfig(motor, driveGearing, azimuthGearing, pidf).toFile());
    SubsystemBase subsystem = new SubsystemBase() {};
    SwerveDrive drive = SwerveParser.createSwerveDrive(new SwerveDriveConfig().withSubsystem(subsystem));
    CommandScheduler.getInstance().unregisterSubsystem(subsystem);
    realTime = motor.startsWith("talonfx");
    if (realTime) {
      for (SwerveModule module : drive.getModules()) {
        for (SmartMotorController motorController : new SmartMotorController[]{
            module.getDriveMotorController(), module.getAzimuthMotorController()}) {
          motorController.applyConfig(motorController.getConfig().withSimulationPeriod(REAL_TIME_PERIOD));
        }
      }
    }
    return drive;
  }

  static Response measure(SwerveDrive drive) {
    if (realTime) {
      SimHooks.resumeTiming();
    } else {
      SimHooks.pauseTiming();
    }
    SwerveModule module = drive.getModules()[0];
    for (int i = 0; i < 25; i++) {
      drive.setAzimuthAngles(Rotation2d.ZERO);
      step(drive);
    }
    double settleAz = Double.NaN, peakAz = 0, finalAz = 0;
    boolean reachedAz = false;
    for (int i = 0; i < 50; i++) {
      drive.setAzimuthAngles(Rotation2d.fromDegrees(AZIMUTH_TARGET_DEG));
      step(drive);
      // A module may flip and point the other way; that is the same heading for a wheel.
      finalAz = AZIMUTH_TARGET_DEG - foldedError(module.getState().angle.getDegrees(), AZIMUTH_TARGET_DEG);
      settleAz = settle(settleAz, finalAz, AZIMUTH_TARGET_DEG, 2, i);
      if (!Double.isNaN(settleAz) || reachedAz) {
        reachedAz = true;
        peakAz = Math.max(peakAz, AZIMUTH_TARGET_DEG + Math.abs(finalAz - AZIMUTH_TARGET_DEG));
      }
    }
    for (int i = 0; i < 25; i++) {
      drive.setAzimuthAngles(Rotation2d.ZERO);
      step(drive);
    }
    SwerveModuleVelocity[] states = new SwerveModuleVelocity[drive.getModules().length];
    double settleDr = Double.NaN, peakDr = 0, finalDr = 0;
    for (int i = 0; i < 75; i++) {
      for (int m = 0; m < states.length; m++) {
        states[m] = new SwerveModuleVelocity(DRIVE_TARGET_MPS, Rotation2d.ZERO);
      }
      drive.setSwerveModuleStates(states);
      step(drive);
      // Speed along the commanded heading, so a flipped module driving backwards counts as forwards.
      finalDr = module.getState().velocity * module.getState().angle.getCos();
      peakDr = Math.max(peakDr, finalDr);
      settleDr = settle(settleDr, finalDr, DRIVE_TARGET_MPS, 0.05 * DRIVE_TARGET_MPS, i);
    }
    for (int i = 0; i < 50; i++) {
      drive.stop();
      step(drive);
    }
    return new Response(settleAz, peakAz, finalAz, settleDr, peakDr, finalDr);
  }

  @ParameterizedTest(name = "{0}, {1}:1 drive, {2}:1 azimuth")
  @MethodSource("gains")
  void defaultGainsReachTheirTargetsInSim(String motor, double driveGearing, double azimuthGearing)
      throws IOException {
    String pidf = pidf(motor);
    SwerveDrive drive = build(motor, driveGearing, azimuthGearing, pidf);
    // Two tries: Phoenix simulates on its own thread, so a TalonFX or TalonFXS run now and then sees a one-loop blip
    // that an identical run does not. Gains that are wrong fail both.
    Response response = measure(drive);
    System.out.printf(Locale.ROOT, "SIMGAINS %s %.2f/%.2f %s%n", motor, driveGearing, azimuthGearing, response);
    if (!response.passes()) {
      response = measure(drive);
      System.out.printf(Locale.ROOT, "SIMGAINS %s %.2f/%.2f retry %s%n", motor, driveGearing, azimuthGearing, response);
    }
    assertTrue(response.passes(), motor + " with " + pidf + ": " + response);
  }
}

class SimGainsTalonFXTest extends SimGainsTest {
  @Override
  String[] motors() {
    return new String[]{"talonfx_krakenx60", "talonfx_krakenx44", "talonfx_falcon"};
  }
}

class SimGainsTalonFXSNeoTest extends SimGainsTest {
  @Override
  String[] motors() {
    return new String[]{"talonfxs_neo", "talonfxs_neo2", "talonfxs_neo550"};
  }
}

class SimGainsTalonFXSOtherTest extends SimGainsTest {
  @Override
  String[] motors() {
    // talonfxs_pulsar is left out: YAMS's TalonFXS simulation of the Pulsar does not drive, whatever the gains.
    return new String[]{"talonfxs_vortex", "talonfxs_minion"};
  }
}

class SimGainsSparkMaxNeoTest extends SimGainsTest {
  @Override
  String[] motors() {
    return new String[]{"sparkmax_neo", "sparkmax_neo2", "sparkmax_neo550"};
  }
}

class SimGainsSparkMaxOtherTest extends SimGainsTest {
  @Override
  String[] motors() {
    return new String[]{"sparkmax_vortex", "sparkmax_pulsar", "sparkmax_minion"};
  }
}

class SimGainsSparkFlexNeoTest extends SimGainsTest {
  @Override
  String[] motors() {
    return new String[]{"sparkflex_neo", "sparkflex_neo2", "sparkflex_neo550"};
  }
}

class SimGainsSparkFlexOtherTest extends SimGainsTest {
  @Override
  String[] motors() {
    return new String[]{"sparkflex_vortex", "sparkflex_minion", "sparkflex_pulsar"};
  }
}

class SimGainsNovaNeoTest extends SimGainsTest {
  @Override
  String[] motors() {
    return new String[]{"nova_neo", "nova_neo2", "nova_neo550"};
  }
}

class SimGainsNovaOtherTest extends SimGainsTest {
  @Override
  String[] motors() {
    return new String[]{"nova_vortex", "nova_minion", "nova_pulsar"};
  }
}
