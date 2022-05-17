package project12.group19.api.game;

import project12.group19.api.game.lifecycle.BallStatus;
import project12.group19.api.game.lifecycle.Round;

import java.util.List;

public record Game(Rules rules, List<Round> rounds) {
    public int getNumberOfFouls() {
        int counter = 0;

        for (Round round : rounds) {
            if (round.status().equals(BallStatus.ESCAPED)) {
                counter++;
            }
        }

        return counter;
    }
}
