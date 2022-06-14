package project12.group19.api.game;

import project12.group19.api.domain.Item;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionState;
import project12.group19.incubating.WaterLake;

import java.util.Optional;
import java.util.Set;

public interface Configuration {
    HeightProfile getHeightProfile();
    Set<Item> getObstacles();
    MotionState getInitialMotion();
    Friction getGroundFriction();
    Friction getSandFriction();
    Hole getHole();
    double getTimeScale();
    String getPlayer();
    Set<WaterLake> getLakes();
    PlanarDimensions getDimensions();
    int getDesiredTickRate();
    int getDesiredRefreshRate();
    Optional<Noise> getNoise();

    interface Noise {
        double getVelocityRange();
        double getDirectionRange();

        record Standard(double velocityRange, double directionRange) implements Noise {
            @Override
            public double getVelocityRange() {
                return velocityRange;
            }

            @Override
            public double getDirectionRange() {
                return directionRange;
            }
        }
    }

    record Standard(
            HeightProfile heightProfile,
            Set<Item> obstacles,
            MotionState initialMotion,
            Friction groundFriction,
            Friction sandFriction,
            Hole hole,
            double timeScale,
            String player,
            Set<WaterLake> lakes,
            PlanarDimensions dimensions,
            int tickRate,
            int refreshRate,
            Noise noise
    ) implements Configuration {
        public Standard(
                HeightProfile heightProfile,
                Set<Item> obstacles,
                MotionState initialMotion,
                Friction groundFriction,
                Friction sandFriction,
                Hole hole,
                Set<WaterLake> lakes,
                PlanarDimensions dimensions
        ) {
            this(
                    heightProfile,
                    obstacles,
                    initialMotion,
                    groundFriction,
                    sandFriction,
                    hole,
                    1,
                    null,
                    lakes,
                    dimensions,
                    60,
                    60,
                    new Noise.Standard(0.0, 0.0)
            );
        }
        public Standard(
                HeightProfile heightProfile,
                Set<Item> obstacles,
                MotionState initialMotion,
                Friction groundFriction,
                Friction sandFriction,
                Hole hole,
                Set<WaterLake> lakes
        ) {
            this(
                    heightProfile,
                    obstacles,
                    initialMotion,
                    groundFriction,
                    sandFriction,
                    hole,
                    lakes,
                    PlanarDimensions.create(50, 50)
            );
        }

        @Override
        public HeightProfile getHeightProfile() {
            return heightProfile;
        }

        @Override
        public Set<Item> getObstacles() {
            return obstacles;
        }

        @Override
        public MotionState getInitialMotion() {
            return initialMotion;
        }

        @Override
        public Friction getGroundFriction() {
            return groundFriction;
        }

        @Override
        public Friction getSandFriction() {
            return sandFriction;
        }

        @Override
        public Hole getHole(){
            return hole;
        }

        @Override
        public double getTimeScale() {
            return timeScale;
        }

        @Override
        public String getPlayer() {
            return player;
        }

        @Override
        public Set<WaterLake> getLakes(){
            return lakes;
        }

        @Override
        public PlanarDimensions getDimensions() {
            return dimensions;
        }

        @Override
        public int getDesiredTickRate() {
            return tickRate;
        }

        @Override
        public int getDesiredRefreshRate() {
            return refreshRate;
        }

        @Override
        public Optional<Noise> getNoise() {
            return Optional.ofNullable(noise);
        }
    }
}
