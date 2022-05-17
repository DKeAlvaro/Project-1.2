package project12.group19.engine;

import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.engine.Engine;
import project12.group19.api.engine.Setup;
import project12.group19.api.game.state.Round;
import project12.group19.api.motion.MotionResult;
import project12.group19.api.motion.MotionState;

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
        AtomicReference<State> state = new AtomicReference<>(State.initial(initialMotion, setup.getCourse(), setup.getRules()));
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
            try {
                setup.getListeners().forEach(listener -> listener.accept(state.get()));
                return !state.get().isTerminal();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }, refreshInterval, TimeUnit.NANOSECONDS);

        CompletableFuture.allOf(calculations, refresh).join();
    }

    private State tick(State current, Setup setup) {
        if (current.isTerminal()) {
            return current;
        }

        double deltaT = (1.0 / setup.getConfiguration().getDesiredTickRate()) * setup.getConfiguration().getTimeScale();
        MotionResult next = setup.getMotionHandler().next(current.getBallState(), deltaT);
        Round round = current.getLastRound();

        switch (next.getStatus()) {
            case SCORED:
                System.out.println("Successfully scored!");
                return current.terminateRound(next.getState().getPosition(), next.getStatus());
            case DROWNED:
                // intentional fallthrough
            case ESCAPED:
                System.out.println("Foul: ended in " + next.getStatus() + " state");
                return current
                    .terminateRound(next.getState().getPosition(), next.getStatus())
                    .nextRound(round.getStartingPosition());
            case MOVING:
                return current.withBallState(next.getState());
            case STOPPED:
                if (round.getHit() != null) {
                    System.out.println("Ball has stopped after a hit");
                    return current
                            .terminateRound(next.getState().getPosition(), next.getStatus())
                            .nextRound(next.getState().getPosition());
                }

                Optional<Player.Hit> hit = setup.getPlayer().play(current);
                hit.ifPresent(v -> System.out.println("Performing a hit: " + v));
                return hit
                        .map(current::withHit)
                        .orElse(current);
            default:
                throw new RuntimeException("Unexpected state");
        }
    }
}
