package project12.group19.gui;

import project12.group19.api.domain.State;
import project12.group19.api.motion.MotionState;
import project12.group19.api.ui.Renderer;

public class LibGdxAdapter implements Renderer {
    private final Drop delegate;

    public LibGdxAdapter(Drop delegate) {
        this.delegate = delegate;
    }

    @Override
    public void render(State state) {
        MotionState ballState = state.getBallState();
        double x = ballState.getXPosition();
        double y = ballState.getYPosition();
        double z = state.getCourse().getSurface().getHeight(x, y);
        delegate.setBallLocation((float) x, (float) y, (float) z);
    }
}
