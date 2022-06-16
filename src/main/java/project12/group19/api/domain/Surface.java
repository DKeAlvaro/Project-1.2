package project12.group19.api.domain;

import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.Friction;

import java.util.function.DoubleBinaryOperator;

/**
 * This interface ties up available information about course surface for
 * downstream consumers to use.
 */
public interface Surface extends HeightProfile {
    /**
     * @param x Position, x-axis component.
     * @param y Position, y-axis component.
     * @return Height of the course at specific point.
     */
    double getHeight(double x, double y);

    /**
     * @param x Position, x-axis component.
     * @param y Position, y-axis component.
     * @return Friction associated with a specific point.
     */
    Friction getFriction(double x, double y);

    default double getHeight(PlanarCoordinate coordinate) {
        return getHeight(coordinate.getX(), coordinate.getY());
    }

    default Friction getFriction(PlanarCoordinate coordinate) {
        return getFriction(coordinate.getX(), coordinate.getY());
    }

    static Surface homogeneous(DoubleBinaryOperator height, Friction friction) {
        return new Homogeneous(height, friction);
    }

    record Homogeneous(DoubleBinaryOperator height, Friction friction) implements Surface {
        @SuppressWarnings("SuspiciousNameCombination")
        @Override
        public double getHeight(double x, double y) {
            return height.applyAsDouble(x, y);
        }

        @Override
        public Friction getFriction(double x, double y) {
            return friction;
        }
    }
}
