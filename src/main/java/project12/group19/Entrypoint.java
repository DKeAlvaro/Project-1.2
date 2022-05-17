package project12.group19;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import project12.group19.api.game.Configuration;
import project12.group19.api.ui.GUI;
import project12.group19.api.ui.Renderer;
import project12.group19.gui.Drop;
import project12.group19.incubating.HitsReader;
import project12.group19.incubating.Reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    private static Renderer createUI(Configuration configuration, boolean showControls, boolean use3d) {

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

        Configuration configuration = resolveConfiguration(args);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Project 1-2 Putting / Group 19");
        config.setWindowedMode(1000, 900);
        config.useVsync(true);
        new Lwjgl3Application(new Drop(configuration), config);
    }
}
