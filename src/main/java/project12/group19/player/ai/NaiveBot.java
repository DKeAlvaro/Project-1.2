package project12.group19.player.ai;

import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.space.Hole;

import java.util.Optional;

public class NaiveBot implements Player {
    private final HitCalculator calculator;

    public NaiveBot(HitCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public Optional<Hit> play(State state) {
        Hole target = state.getCourse().getHole();
        return calculator.shootThrough(state, PlanarCoordinate.create(target.getxHole(), target.getyHole()), target.getRadius());
    }
}
