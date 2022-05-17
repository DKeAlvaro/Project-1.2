package project12.group19.api.game;

import project12.group19.api.domain.Item;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionState;
import project12.group19.incubating.WaterLake;

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
    WaterLake getLake();
    PlanarDimensions getDimensions();

    record Standard(
            HeightProfile heightProfile,
            Set<Item> obstacles,
            MotionState initialMotion,
            Friction groundFriction,
            Friction sandFriction,
            Hole hole,
            double timeScale,
            String player,
            WaterLake lake,
            PlanarDimensions dimensions

    ) implements Configuration {
        public Standard(
                HeightProfile heightProfile,
                Set<Item> obstacles,
                MotionState initialMotion,
                Friction groundFriction,
                Friction sandFriction,
                Hole hole,
                WaterLake lake,
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
                    lake,
                    dimensions
            );
        }
        public Standard(
                HeightProfile heightProfile,
                Set<Item> obstacles,
                MotionState initialMotion,
                Friction groundFriction,
                Friction sandFriction,
                Hole hole,
                WaterLake lake
        ) {
            this(
                    heightProfile,
                    obstacles,
                    initialMotion,
                    groundFriction,
                    sandFriction,
                    hole,
                    lake,
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
        public WaterLake getLake(){
            return lake;
        }

        @Override
        public PlanarDimensions getDimensions() {
            return dimensions;
        }
    }
}
