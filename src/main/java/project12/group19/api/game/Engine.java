package project12.group19.api.game;

import project12.group19.api.game.lifecycle.GameStats;

import java.util.concurrent.CompletableFuture;

public interface Engine {
    CompletableFuture<GameStats> launch(Setup setup);
}
