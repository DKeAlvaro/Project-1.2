package project12.group19.gui;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import project12.group19.api.domain.State;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.MotionState;
import project12.group19.api.ui.Renderer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class LibGdxAdapter implements Renderer, Closeable {
    private final AtomicReference<Lwjgl3Application> application = new AtomicReference<>();
    private final Drop delegate;
    private final Executor containment;

    public LibGdxAdapter(HeightProfile surface) {
        delegate = new Drop(surface);

        containment = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        containment.execute(() -> {
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setForegroundFPS(60);
            config.setTitle("Project 1-2 Putting / Group 19");
            config.setWindowedMode(1000, 900);
            config.useVsync(true);
            application.set(new Lwjgl3Application(delegate, config));
        });
    }

    @Override
    public void render(State state) {
        MotionState ballState = state.getBallState();
        double x = ballState.getXPosition();
        double y = ballState.getYPosition();
        double z = state.getCourse().getSurface().getHeight(x, y);
        delegate.setBallLocation((float) x, (float) y, (float) z);
    }

    @Override
    public void close() throws IOException {
        Optional.ofNullable(application.get()).ifPresent(Lwjgl3Application::exit);
    }
}
