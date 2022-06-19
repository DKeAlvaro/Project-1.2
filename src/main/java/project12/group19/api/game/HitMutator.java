package project12.group19.api.game;

import project12.group19.api.domain.Hit;

import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

/**
 * This interface exists to introduce mutations in the gameplay and to
 * make the task harder for player, for example, by adding noise to or
 * deflect hit by constant angle.
 */
public interface HitMutator extends UnaryOperator<Hit> {
    static HitMutator identity() {
        return Identity.INSTANCE;
    }

    static HitMutator noise(Random random, DoubleUnaryOperator distribution, double velocityRange, double directionRange) {
        return new Noise(random, distribution, velocityRange, directionRange);
    }

    static HitMutator noise(Random random, double velocityRange, double directionRange) {
        return noise(random, DoubleUnaryOperator.identity(), velocityRange, directionRange);
    }

    static HitMutator noise(DoubleUnaryOperator distribution, double velocityRange, double directionRange) {
        return noise(new Random(), distribution, velocityRange, directionRange);
    }

    static HitMutator noise(double velocityRange, double directionRange) {
        return noise(DoubleUnaryOperator.identity(), velocityRange, directionRange);
    }

    static HitMutator deflecting(double angle) {
        return new Deflector(angle);
    }

    static HitMutator limiting(double limit) {
        return new Limiter(limit);
    }

    /**
     * A classic noop.
     */
    class Identity implements HitMutator {
        public static final HitMutator INSTANCE = new Identity();

        @Override
        public Hit apply(Hit hit) {
            return hit;
        }
    }

    /**
     * Deflects hit by a constant angle.
     *
     * @param deflection Angle in radians to deflect the hit according to.
     */
    record Deflector(double deflection) implements HitMutator {
        @Override
        public Hit apply(Hit hit) {
            double angle = hit.getVelocityAngle() + deflection;
            double velocity = hit.getAbsoluteVelocity();
            return Hit.polar(velocity, angle);
        }
    }

    /**
     * A mutator that adds noise to hit direction and velocity.
     *
     * @param random A source of random values.
     * @param distribution A function that accepts a value from
     * uniformly distributed values in range of [-1, 1] and returns a
     * returns values from the very same range but with different
     * distribution graph.
     * @param velocityRange How much velocity can be affected, expressed
     * as a fraction of velocity (e.g. setting it to 0.1 would result in
     * absolute velocity within 90%..110% range of original value).
     * @param directionRange How much direction of hit can be altered,
     * expressed as a fraction of pi (e.g. setting it t0 0.25 would
     * result in direction within -pi/4..pi/4 sector with original angle
     * in center).
     */
    record Noise(Random random, DoubleUnaryOperator distribution, double velocityRange, double directionRange) implements HitMutator {
        public Noise(Random random, double velocityRange, double directionRange) {
            this(random, DoubleUnaryOperator.identity(), velocityRange, directionRange);
        }

        private double next() {
            // This will return a double in -1..1 range, as opposed to 0..1 of raw nextDouble
            double raw = (random.nextDouble() * 2) - 1;
            return distribution.applyAsDouble(raw);
        }

        @Override
        public Hit apply(Hit hit) {
            if (velocityRange <= 0 && directionRange <= 0) {
                return hit;
            }

            double velocity = hit.getAbsoluteVelocity();
            double angle = hit.getVelocityAngle();

            if (velocityRange > 0) {
                double offset = velocityRange * next();
                velocity = velocity * (1 + offset);
            }

            if (directionRange > 0) {
                double deflection = Math.PI * directionRange * next();
                angle = angle + deflection;
            }

            return Hit.polar(velocity, angle);
        }
    }

    /**
     * Simply downscales hits that are too powerful.
     *
     * @param limit Maximum absolute velocity allowed.
     */
    record Limiter(double limit) implements HitMutator {
        @Override
        public Hit apply(Hit hit) {
            double velocity = hit.getAbsoluteVelocity();

            if (velocity <= limit) {
                return hit;
            }

            double multiplier = limit / velocity;
            return Hit.create(hit.scaleVelocity(multiplier));
        }
    }
}
