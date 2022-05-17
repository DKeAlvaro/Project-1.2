package project12.group19.engine;

import project12.group19.api.domain.Course;
import project12.group19.api.domain.Item;
import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.engine.Engine;
import project12.group19.api.engine.Setup;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarRectangle;
import project12.group19.api.motion.MotionState;
import project12.group19.api.motion.StopCondition;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GameHandler implements Engine {
    @Override
    public void launch(Setup setup) {
        EventLoop loop = new EventLoop(Executors.newScheduledThreadPool(4));
        MotionState initialMotion = setup.getConfiguration().getInitialMotion();
        Course course = new Course.Standard(
                setup.getConfiguration().getHeightProfile(),
                new Item.Standard("ball", PlanarCoordinate.create(initialMotion.getXPosition(), initialMotion.getYPosition())),
                setup.getConfiguration().getObstacles(),
                setup.getConfiguration().getHole()
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
        CompletableFuture<Void> calculations = loop.schedule(epoch -> {
            try {
                state.set(tick(state.get(), setup));
                return true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return false;
            }
        }, interval, TimeUnit.NANOSECONDS);

        long refreshInterval = (long) (1_000_000_000.0 / setup.getDesiredRefreshRate());
        CompletableFuture<Void> refresh = loop.schedule(epoch -> {
            setup.getListeners().forEach(listener -> listener.accept(state.get()));
            return !state.get().isTerminal();
        }, refreshInterval, TimeUnit.NANOSECONDS);

        CompletableFuture.allOf(calculations, refresh).join();
    }

    private State tick(State current, Setup setup) {
        if (current.isTerminal()) {
            return current;
        }

        double targetDistanceX = current.getBallState().getXPosition() - current.getCourse().getHole().getxHole();
        double targetDistanceY = current.getBallState().getYPosition() - current.getCourse().getHole().getyHole();
        double targetDistance = Math.sqrt(targetDistanceX * targetDistanceX + targetDistanceY * targetDistanceY);

        if (targetDistance <= current.getCourse().getHole().getRadius()) {
            return new State.Standard(
                    current.getCourse(),
                    current.getBallState(),
                    true,
                    true,
                    current.getHits(),
                    current.getFouls()
            );
        }

        double deltaT = (1.0 / setup.getDesiredTickRate()) * setup.getConfiguration().getTimeScale();

        Optional<Player.Hit> hit = setup.getPlayer().play(current);
        int hits = hit.map(any -> current.getHits() + 1).orElse(current.getHits());
        MotionState ballMotion = hit
                .<MotionState>map(identity -> new MotionState.Standard(
                        current.getBallState().getXSpeed() + identity.getXVelocity(),
                        current.getBallState().getYSpeed() + identity.getYVelocity(),
                        current.getBallState().getXPosition(),
                        current.getBallState().getYPosition()
                ))
                .orElse(current.getBallState());

        if (!StopCondition.isMoving(setup.getConfiguration().getHeightProfile(), ballMotion, setup.getConfiguration().getGroundFriction(), deltaT)) {
            if (!current.isStatic()) {
                System.out.println("The ball has stopped");
            }

            return new State.Standard(
                    current.getCourse(),
                    ballMotion,
                    false,
                    true,
                    hits,
                    current.getFouls()
            );
        }


        double width = setup.getConfiguration().getDimensions().getWidth();
        double height = setup.getConfiguration().getDimensions().getHeight();
        PlanarRectangle boundaries = PlanarRectangle.create(-width/2, -height/2, width, height);
        if (!boundaries.includes(ballMotion.getPosition())) {
            boolean termination = current.getFouls() >= 3;
            return new State.Standard(
                    current.getCourse(),
                    termination ? current.getBallState() : setup.getConfiguration().getInitialMotion(),
                    termination,
                    false,
                    hits,
                    current.getFouls() + 1
            );
        }

        return new State.Standard(
                current.getCourse(),
                setup.getMotionCalculator().calculate(ballMotion, deltaT),
                false,
                false,
                hits,
                current.getFouls()
        );
    }
}
