package project12.group19;

import project12.group19.api.domain.Player;
import project12.group19.api.engine.Setup;
import project12.group19.api.game.Configuration;
import project12.group19.api.motion.Solver;
import project12.group19.api.ui.GUI;
import project12.group19.engine.GameHandler;
import project12.group19.incubating.HitsReader;
import project12.group19.incubating.Reader;
import project12.group19.player.FixedPlayer;
import project12.group19.player.ai.HitCalculator;
import project12.group19.player.ai.NaiveBot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Entrypoint {
    public static final String DEFAULT_CONFIGURATION_FILE = "configuration.properties";
    public static final String DEFAULT_REPLAY_FILE = "replay.txt";

    private static Configuration loadConfiguration(String path) throws IOException {
        System.out.println("Loading configuration from `" + path + "`");
        return new Reader().read(path);
    }

    private static Configuration resolveConfiguration(String[] args) throws Exception {
        if (args.length > 0) {
            return loadConfiguration(args[0]);
        }

        System.out.println("No arguments given, loading file from default location");
        return loadConfiguration(DEFAULT_CONFIGURATION_FILE);
    }

    private static double[][] loadHits(String path) throws IOException {
        System.out.println("Loading hits from `" + path + "`");
        return new HitsReader().read(path);
    }

    private static Optional<double[][]> resolveReplay(String[] args) throws IOException {
        if (args.length > 1) {
            return Optional.of(loadHits(args[1]));
        }

        if (Files.exists(Paths.get(DEFAULT_REPLAY_FILE))) {
            return Optional.of(loadHits(DEFAULT_REPLAY_FILE));
        }

        return Optional.empty();
    }

    private static GUI createUI(Configuration configuration, boolean showControls) {
        return new GUI(
                configuration.getHeightProfile(),
                GUI.TRANSLATOR.toPixelX(configuration.getHole().getxHole()),
                GUI.TRANSLATOR.toPixelY(configuration.getHole().getyHole()),
                configuration.getHole().getRadius() * 25,
                GUI.TRANSLATOR.toPixelX(configuration.getInitialMotion().getXPosition()) / 12,
                GUI.TRANSLATOR.toPixelY(configuration.getInitialMotion().getYPosition()) / 12,
                (int) (configuration.getHeightProfile().getHeight(
                        configuration.getInitialMotion().getXPosition(),
                        configuration.getInitialMotion().getYPosition()
                ) * 12)
        );
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Putting :: DKE Project 1-2 :: Group 19");
        System.out.println();
        System.out.println("  Usage: <executable> [path to configuration=configuration.properties] [path to hits replay]");
        System.out.println();
        System.out.println("Running...");
        System.out.println();

        Optional<double[][]> replay = resolveReplay(args);
        Configuration configuration = resolveConfiguration(args);
        GUI gui = createUI(configuration, replay.isEmpty());
        Map<String, Player> players = new HashMap<>();
        players.put("human", gui.getController());
        players.put("bot.naive", new NaiveBot(new HitCalculator.Directed(3)));
        replay.map(FixedPlayer::new).ifPresent(player -> players.put("replay", player));

        String selection = Optional.ofNullable(configuration.getPlayer()).orElse("human");
        Player player = Optional.ofNullable(players.get(selection))
                .orElseThrow(() -> new IllegalArgumentException("Unknown player type: " + selection));

        Player loggingWrapper = state -> {
            Optional<Player.Hit> response = player.play(state);
            response.ifPresent(hit -> System.out.println("Hit was made: " + hit));
            return response;
        };

        Setup.Standard setup = new Setup.Standard(configuration, 600, 10, new Solver(), loggingWrapper, List.of(gui::render));

        new GameHandler().launch(setup);

        System.out.println("That's all, folks!");
    }
}
