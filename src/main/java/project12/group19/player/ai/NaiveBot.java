package project12.group19.player.ai;

import project12.group19.api.domain.Item;
import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;

import java.io.FileNotFoundException;
import java.util.Optional;

public class NaiveBot implements Player {
    private final HitCalculator calculator;

    public NaiveBot(HitCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public Optional<Hit> play(State state) throws FileNotFoundException {
        Item target = state.getCourse().getTarget();
        return calculator.shootThrough(state, target.getCenter(), target.getSmallerDimension());
    }
}
