package project12.group19.api.game;

import project12.group19.api.domain.Item;
import project12.group19.api.game.configuration.EngineConfiguration;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.physics.motion.Friction;
import project12.group19.api.physics.motion.MotionState;

import java.util.Set;

public interface Configuration {
    String getSurface();
    Set<Item> getItems();
    MotionState getInitialMotion();
    Friction getGroundFriction();
    Friction getSandFriction();
    Item.Target getTarget();
    PlanarDimensions getDimensions();
    EngineConfiguration getEngineConfiguration();

    default Configuration withEngineConfiguration(EngineConfiguration configuration) {
        return new Standard(
                getSurface(),
                getItems(),
                getInitialMotion(),
                getGroundFriction(),
                getSandFriction(),
                getTarget(),
                getDimensions(),
                configuration
        );
    }

    record Standard(
            String surface,
            Set<Item> obstacles,
            MotionState initialMotion,
            Friction groundFriction,
            Friction sandFriction,
            Item.Target target,
            PlanarDimensions dimensions,
            EngineConfiguration engineConfiguration
    ) implements Configuration {
        @Override
        public String getSurface() {
            return surface;
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
        public Item.Target getTarget(){
            return target;
        }

        @Override
        public PlanarDimensions getDimensions() {
            return dimensions;
        }

        @Override
        public EngineConfiguration getEngineConfiguration() {
            return engineConfiguration;
        }
    }
}
