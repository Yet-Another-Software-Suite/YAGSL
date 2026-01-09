package swervelib.simulation.ironmaple.simulation.seasonspecific.crescendo2024;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructPublisher;
import swervelib.simulation.ironmaple.simulation.Goal;

import java.util.List;

import static edu.wpi.first.units.Units.Centimeters;

/**
 *
 *
 * <h2>Simulates a <strong>AMPS</strong> on the field.</h2>
 *
 * <p>This class simulates the <strong>AMP</strong> on the field where <strong>NOTES</strong>s can be scored.
 */
public class CrescendoAmp extends Goal {

    protected static final Translation3d blueAmpPose = new Translation3d(1.81, 8.3, 0.66);
    protected static final Translation3d redAmpPose = new Translation3d(14.7, 8.3, 0.66);

    protected final Arena2024Crescendo crescendoArena;

    StructPublisher<Pose3d> posePublisher;

    /**
     *
     *
     * <h2>Creates an Amp of the specified color.</h2>
     *
     * @param arena  The host arena of this amp.
     * @param isBlue Wether this is the blue amp or the red one.
     */
    public CrescendoAmp(Arena2024Crescendo arena, boolean isBlue) {
        super(
                arena,
                Centimeters.of(61),
                Centimeters.of(30),
                Centimeters.of(46),
                "Note",
                isBlue ? blueAmpPose : redAmpPose,
                isBlue);

        crescendoArena = arena;

        setNeededAngle(new Rotation3d(0, Math.PI / 2, Math.PI / 2));

        StructPublisher<Pose3d> ampPublisher = NetworkTableInstance.getDefault()
                .getStructTopic(isBlue ? "BlueAmp" : "RedAmp", Pose3d.struct)
                .publish();
        ampPublisher.set(new Pose3d(position, this.pieceAngle));
    }

    @Override
    protected void addPoints() {
        crescendoArena.addValueToMatchBreakdown(isBlue, "TotalNotesInAmp", 1.0);
        arena.addValueToMatchBreakdown(isBlue, "AmplifiedScore", crescendoArena.isAmped(isBlue) ? 2 : 0);

        crescendoArena.addAmpCharge(isBlue);
        arena.addToScore(isBlue, crescendoArena.isAmped(isBlue) ? 2 : 1);
    }

    @Override
    public void draw(List<Pose3d> drawList) {
    }
}
