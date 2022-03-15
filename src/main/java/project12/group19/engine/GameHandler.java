package project12.group19.engine;

import project12.group19.api.domain.Course;
import project12.group19.api.domain.Item;
import project12.group19.api.domain.State;
import project12.group19.api.engine.Engine;
import project12.group19.api.engine.Setup;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionCalculator;
import project12.group19.api.motion.MotionState;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GameHandler implements Engine {
    @Override
    public void launch(Setup setup) {
        EventLoop loop = new EventLoop(Executors.newScheduledThreadPool(1));
        MotionState initialMotion = setup.getConfiguration().getInitialMotion();
        Course course = new Course.Standard(
                setup.getConfiguration().getHeightProfile(),
                new Item.Standard("ball", PlanarCoordinate.create(initialMotion.getXPosition(), initialMotion.getYPosition())),
                setup.getConfiguration().getObstacles()
        );
        AtomicReference<State> state = new AtomicReference<>(new State.Standard(
                course,
                initialMotion,
                false,
                false,
                0,
                0
        ));
        long interval = (long) (1_000_000_000.0 / setup.getDesiredTickRate());
        loop.schedule(epoch -> {
            state.set(tick(state.get(), setup));
            setup.getListeners().forEach(listener -> listener.accept(state.get()));
            return true;
        }, interval, TimeUnit.NANOSECONDS).join();
    }

    private State tick(State current, Setup setup) {
        double deltaT = 1.0 / setup.getDesiredTickRate();

        return new State.Standard(
                current.getCourse(),
                setup.getMotionCalculator().calculate(current.getBallState(), null, deltaT),
                false,
                false,
                current.getHits(),
                current.getFouls()
        );
    }

    public static void main(String[] args) {
        // dirty, dirty testing

        Setup setup = new Setup.Standard(
                new Configuration.Standard(
                        null,
                        Collections.emptySet(),
                        MotionState.zero(),
                        Friction.create(0.1, 0.1),
                        Friction.create(0.1, 0.1),
                        new Hole(-1, -1, 0.3)
                ),
                3,
                3,
                new MotionCalculator.Circlular(PlanarCoordinate.create(1, 1)),
                course -> Optional.empty(),
                Collections.singletonList(state -> System.out.println(state.getBallState()))
        );

        new GameHandler().launch(setup);
    }
}
