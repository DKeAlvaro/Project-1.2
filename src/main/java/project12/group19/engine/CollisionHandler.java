package project12.group19.engine;

import project12.group19.api.domain.Item;
import project12.group19.api.motion.MotionState;

public interface CollisionHandler {
    MotionState apply(MotionState source, MotionState target, Item obstacle);
}
