package swervelib.simulation.ironmaple.simulation.seasonspecific.rebuilt2026;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import swervelib.simulation.ironmaple.simulation.gamepieces.GamePieceOnFieldSimulation;
import swervelib.simulation.ironmaple.simulation.gamepieces.GamePieceOnFieldSimulation.GamePieceInfo;

import org.dyn4j.geometry.Circle;

/**
 *
 *
 * <h1>Represents an Fuel in the 2026 rebuilt game.</h1>
*/
public class RebuiltFuelOnField extends GamePieceOnFieldSimulation {
    public static final GamePieceInfo REBUILT_FUEL_INFO = new GamePieceInfo(
            "Fuel", new Circle(Centimeters.of(7.5).in(Meters)), Centimeter.of(15), Pounds.of(0.5), 1.8, 5, 0.8);

    public RebuiltFuelOnField(Translation2d initialPosition) {
        super(REBUILT_FUEL_INFO, new Pose2d(initialPosition, new Rotation2d()));
    }
}
