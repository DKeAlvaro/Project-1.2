package project12.group19.engine;

import project12.group19.api.domain.Hit;
import project12.group19.api.domain.State;
import project12.group19.api.game.Engine;
import project12.group19.api.game.Setup;
import project12.group19.api.engine.EventLoop;
import project12.group19.api.game.lifecycle.GameStats;
import project12.group19.api.game.state.Round;
import project12.group19.api.physics.motion.MotionResult;
import project12.group19.api.physics.motion.MotionState;

import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameHandler implements Engine {
    private final EventLoop executor;

    public GameHandler(EventLoop executor) {
        this.executor = executor;
    }

    private record StateWrapper(State state, long ticks, boolean exceptional) {
        public StateWrapper next(State state, boolean exceptional) {
            return new StateWrapper(state, ticks + 1, exceptional);
        }

        public StateWrapper next(State state) {
            return next(state, false);
        }

        public StateWrapper failed() {
            return next(state, true);
        }

        public static StateWrapper create(State state) {
            return new StateWrapper(state, 0, false);
        }
    }

    @Override
    public CompletableFuture<GameStats> launch(Setup setup) {
        MotionState initialMotion = setup.getConfiguration().getInitialMotion();
        Setup.Timing timing = setup.getTiming();
        double computationToNotificationRatio = timing.getComputationInterval() / (double) timing.getNotificationInterval();
        EventLoop.Task<StateWrapper> task = handle -> {
            State state;
            try {
                state = tick(handle.state(), setup);
            } catch (RuntimeException | FileNotFoundException e) {
                e.printStackTrace();
                return EventLoop.Iteration.terminal(handle.failed());
            }

            // The notifications are ran only when rounded-down current
            // number of ticks divided by ratio is different from
            // previous one
            // this makes notifications run only the first time number
            // of ticks divided by ratio gets bigger than some specific
            // number
            long notificationsPreviousTick = (long) ((handle.ticks() - 1) / computationToNotificationRatio);
            long notificationsThisTick = (long) (handle.ticks() / computationToNotificationRatio);
            if (notificationsPreviousTick != notificationsThisTick) {
                for (Consumer<State> listener : setup.getListeners()) {
                    try {
                        listener.accept(state);
                    } catch (RuntimeException e) {
                        System.out.println("Error in listener " + listener + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            return EventLoop.Iteration.create(handle.next(state), state.isTerminal());
        };

        StateWrapper bootstrap = StateWrapper.create(State.initial(initialMotion, setup.getCourse(), setup.getRules()));

        return executor.submit(task, bootstrap, timing.getComputationInterval(), TimeUnit.NANOSECONDS)
                .thenApply(handle -> new GameStats.Standard(handle.state().getRounds(), handle.exceptional()));
    }

    private State tick(State current, Setup setup) throws FileNotFoundException {
        if (current.isTerminal()) {
            return current;
        }

        MotionResult next = setup.getMotionHandler().next(current.getBallState(), setup.getTiming().getStep());
        Round round = current.getLastRound();

        switch (next.getStatus()) {
            case SCORED:
                System.out.println("Successfully scored at " + next.getState().getPosition());
                return current.terminateRound(next.getState().getPosition(), next.getStatus());
            case DROWNED:
                // intentional fallthrough
            case ESCAPED:
                System.out.println("Foul: ended in " + next.getStatus() + " state at " + next.getState().getPosition());
                return current
                    .terminateRound(next.getState().getPosition(), next.getStatus())
                    .nextRound(round.getStartingPosition());
            case MOVING:
                return current.withBallState(next.getState());
            case STOPPED:
                if (round.getHit() != null) {
                    System.out.println("Ball has stopped after a hit at " + next.getState().getPosition());
                    return current
                            .terminateRound(next.getState().getPosition(), next.getStatus())
                            .nextRound(next.getState().getPosition());
                }

                Optional<Hit> originalHit = setup.getPlayer().play(current)
                        .map(setup.getHitMutator());
                originalHit.ifPresent(v -> System.out.println("Player submitted a hit: " + v));
                Optional<Hit> modifiedHit = originalHit.map(setup.getHitMutator());
                modifiedHit.ifPresent(v -> System.out.println("Hit after applying a mutation: " + v));
                return originalHit
                        .map(current::withHit)
                        .orElse(current);
            default:
                throw new RuntimeException("Unexpected state");
        }
    }
}
