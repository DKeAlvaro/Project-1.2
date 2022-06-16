package project12.group19.api.game;

import project12.group19.api.domain.Course;
import project12.group19.api.domain.Item;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionState;
import project12.group19.incubating.WaterLake;

import java.util.OptionalDouble;
import java.util.Set;

public interface Configuration {
    String getSurface();

    /**
     * @deprecated use {@link Course#getSurface()}
     */
    @Deprecated
    HeightProfile getHeightProfile();
    Set<Item> getItems();
    MotionState getInitialMotion();
    Friction getGroundFriction();
    Friction getSandFriction();
    Hole getHole();
    double getTimeScale();
    PlanarDimensions getDimensions();
    int getDesiredTickRate();
    int getDesiredRefreshRate();
    Noise getNoise();

    interface Noise {
        OptionalDouble getVelocityRange();
        OptionalDouble getDirectionRange();

        static Noise empty() {
            return new Standard(OptionalDouble.empty(), OptionalDouble.empty());
        }

        record Standard(OptionalDouble velocityRange, OptionalDouble directionRange) implements Noise {
            @Override
            public OptionalDouble getVelocityRange() {
                return velocityRange;
            }

            @Override
            public OptionalDouble getDirectionRange() {
                return directionRange;
            }
        }
    }

    record Standard(
            String surface,
            HeightProfile heightProfile,
            Set<Item> obstacles,
            MotionState initialMotion,
            Friction groundFriction,
            Friction sandFriction,
            Hole hole,
            double timeScale,
            PlanarDimensions dimensions,
            int tickRate,
            int refreshRate,
            Noise noise
    ) implements Configuration {
        public Standard(
                String surface,
                HeightProfile heightProfile,
                Set<Item> items,
                MotionState initialMotion,
                Friction groundFriction,
                Friction sandFriction,
                Hole hole,
                PlanarDimensions dimensions
        ) {
            this(
                    surface,
                    heightProfile,
                    items,
                    initialMotion,
                    groundFriction,
                    sandFriction,
                    hole,
                    1,
                    dimensions,
                    100,
                    60,
                    new Noise.Standard(OptionalDouble.empty(), OptionalDouble.empty())
            );
        }
        public Standard(
                String surface,
                HeightProfile heightProfile,
                Set<Item> items,
                MotionState initialMotion,
                Friction groundFriction,
                Friction sandFriction,
                Hole hole,
                Set<WaterLake> lakes
        ) {
            this(
                    surface,
                    heightProfile,
                    items,
                    initialMotion,
                    groundFriction,
                    sandFriction,
                    hole,
                    PlanarDimensions.create(50, 50)
            );
        }

        @Override
        public String getSurface() {
            return surface;
        }

        @Override
        public HeightProfile getHeightProfile() {
            return heightProfile;
        }

        @Override
        public Set<Item> getItems() {
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
        public Noise getNoise() {
            return noise;
        }
    }
}
