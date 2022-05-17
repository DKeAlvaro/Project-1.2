package project12.group19.engine;

import project12.group19.api.domain.Course;
import project12.group19.api.domain.Item;
import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.engine.Engine;
import project12.group19.api.engine.Setup;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.*;
import project12.group19.math.DerivativeEstimator;

import java.util.Collections;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This is the game core.
 *
 * GameHandler is responsible to
 */
public class GameHandler implements Engine {
    private static final DerivativeEstimator DERIVATIVES = new DerivativeEstimator(1E-6);

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
                state.set(next(state.get(), setup));
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

    public State next(State current, Setup setup) {
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

        HeightProfile heightProfile = setup.getConfiguration().getHeightProfile();
        Acceleration acceleration = Solver.acceleration(
                heightProfile,
                current.getBallState(),
                setup.getConfiguration().getGroundFriction(),
                deltaT
        );

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

        if (ballMotion.getAbsoluteSpeed() < 1E-3) {
            double x = ballMotion.getXPosition();
            double y = ballMotion.getYPosition();
            // TODO calculate this once and store somewhere
            double dhdx = DERIVATIVES.estimate(variableX -> OptionalDouble.of(heightProfile.getHeight(variableX, y)), x).getAsDouble();
            double dhdy = DERIVATIVES.estimate(variableY -> OptionalDouble.of(heightProfile.getHeight(x, variableY)), y).getAsDouble();
            double dh = Math.sqrt(dhdx * dhdx + dhdy * dhdy);

            if (dh < setup.getConfiguration().getGroundFriction().getStaticCoefficient()) {
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
        }

        if (Math.abs(ballMotion.getXPosition()) > 25 || Math.abs(ballMotion.getYPosition()) > 25) {
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
