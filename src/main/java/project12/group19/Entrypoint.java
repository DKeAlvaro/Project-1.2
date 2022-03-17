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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
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
        int pixels = 600;
        double meters = 0.1;
        double scale = pixels / meters;
        return new GUI(
                configuration.getHeightProfile(),
                (int) (configuration.getHole().getxHole() * scale),
                (int) (configuration.getHole().getyHole() * scale),
                (int) (configuration.getHole().getRadius() * scale),
                (int) (configuration.getInitialMotion().getXPosition() * scale),
                (int) (configuration.getInitialMotion().getYPosition() * scale),
                (int) (configuration.getHeightProfile().getHeight(
                        configuration.getInitialMotion().getXPosition(),
                        configuration.getInitialMotion().getYPosition()
                ) * scale)
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
        Player player = replay
                .<Player>map(FixedPlayer::new)
                .orElse(gui.getController());

        Setup.Standard setup = new Setup.Standard(configuration, 60, 10, new Solver(), player, List.of(
                gui::render,
                state -> {
                    if (state.isStatic()) {
                        return;
                    }
                    System.out.printf(
                            "x=%.3f, y=%.3f, vx=%.3f, vy=%.3f, z=%.3f\n",
                            state.getBallState().getXPosition(),
                            state.getBallState().getYPosition(),
                            state.getBallState().getXSpeed(),
                            state.getBallState().getYSpeed(),
                            configuration.getHeightProfile().getHeight(state.getBallState().getXPosition(), state.getBallState().getYPosition())
                    );
                }
        ));

        new GameHandler().launch(setup);

        System.out.println("That's all, folks!");
    }
}
