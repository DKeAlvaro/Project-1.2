package project12.group19.engine;

import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.engine.Engine;
import project12.group19.api.engine.Setup;
import project12.group19.api.engine.core.EventLoop;
import project12.group19.api.game.lifecycle.GameStats;
import project12.group19.api.game.state.Round;
import project12.group19.api.motion.MotionResult;
import project12.group19.api.motion.MotionState;

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

    /**
     * @deprecated Use {@link GameHandler#GameHandler(EventLoop)}.
     */
    @Deprecated
    public GameHandler() {
        this(ScheduledEventLoop.standard());
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
    }

    @Override
    public CompletableFuture<GameStats> launch(Setup setup) {
        MotionState initialMotion = setup.getConfiguration().getInitialMotion();
        EventLoop.Task<StateWrapper> task = handle -> {
            State state = handle.state;
            try {
                state = tick(handle.state, setup);
            } catch (RuntimeException | FileNotFoundException e) {
                e.printStackTrace();
                return EventLoop.Iteration.terminal(handle.failed());
            }

            // todo: notify only on corresponding ticks
            for (Consumer<State> listener : setup.getListeners()) {
                try {
                    listener.accept(state);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }

            return EventLoop.Iteration.create(handle.next(state), state.isTerminal());
        };

        StateWrapper bootstrap = new StateWrapper(
                State.initial(initialMotion, setup.getCourse(), setup.getRules()),
                0,
                false
        );

        return executor.submit(task, bootstrap, setup.getTiming().getComputationInterval(), TimeUnit.NANOSECONDS)
                .thenApply(handle -> new GameStats.Standard(handle.state().getRounds(), handle.exceptional()));
    }

    private static GameStats toStats(State state, boolean endedExceptionally) {
        return new GameStats.Standard(state.getRounds(), endedExceptionally);
    }

    private State tick(State current, Setup setup) throws FileNotFoundException {
        if (current.isTerminal()) {
            return current;
        }

        MotionResult next = setup.getMotionHandler().next(current.getBallState(), setup.getTiming().getStep());
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

                Optional<Player.Hit> originalHit = setup.getPlayer().play(current)
                        .map(setup.getHitMutator());
                originalHit.ifPresent(v -> System.out.println("Player submitted a hit: " + v));
                Optional<Player.Hit> modifiedHit = originalHit.map(setup.getHitMutator());
                modifiedHit.ifPresent(v -> System.out.println("Hit after applying a mutation: " + v));
                return originalHit
                        .map(current::withHit)
                        .orElse(current);
            default:
                throw new RuntimeException("Unexpected state");
        }
    }
}
