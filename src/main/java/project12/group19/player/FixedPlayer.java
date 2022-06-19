package project12.group19.player;

import project12.group19.api.domain.Hit;
import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;

import java.util.Optional;

public class FixedPlayer implements Player {
    private final double[][] hits;
    private int cursor = 0;

    public FixedPlayer(double[][] hits) {
        this.hits = hits;
    }

    @Override
    public Optional<Hit> play(State state) {
        double[] hit = hits[cursor];
        cursor++;
        return Optional.of(Hit.create(hit[0], hit[1]));
    }
}
