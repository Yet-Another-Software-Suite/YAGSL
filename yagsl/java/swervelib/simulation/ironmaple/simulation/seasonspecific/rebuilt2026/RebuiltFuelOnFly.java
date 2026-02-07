package swervelib.simulation.ironmaple.simulation.seasonspecific.rebuilt2026;

import static edu.wpi.first.units.Units.Centimeter;
import static edu.wpi.first.units.Units.Inches;
import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import swervelib.simulation.ironmaple.simulation.gamepieces.GamePieceProjectile;
import swervelib.simulation.ironmaple.simulation.seasonspecific.reefscape2025.ReefscapeAlgaeOnField;

/**
 *
 *
 * <h1>Represents an FUEL launched into the air.</h1>
 *
 * <p>This class models a {@link RebuiltFuelOnField} launched into the air.
 *
 * <p>The simulation will determine if the FUEL hits its target—the HUB.
 *
 * <p>The user can specify a callback using {@link #setHitHubCallBack(Runnable)}, which will be triggered when the FUEL
 * hits the HUB.
 */
public class RebuiltFuelOnFly extends GamePieceProjectile {



    public RebuiltFuelOnFly(
            Translation2d robotPosition,
            Translation2d shooterPositionOnRobot,
            ChassisSpeeds chassisSpeeds,
            Rotation2d shooterFacing,
            Distance initialHeight,
            LinearVelocity launchingSpeed,
            Angle shooterAngle) {
        super(
                RebuiltFuelOnField.REBUILT_FUEL_INFO,
                robotPosition,
                shooterPositionOnRobot,
                chassisSpeeds,
                shooterFacing,
                initialHeight,
                launchingSpeed,
                shooterAngle);

        super.withTouchGroundHeight(Centimeter.of(7.5).in(Meters));
        super.enableBecomesGamePieceOnFieldAfterTouchGround();
    }
}
