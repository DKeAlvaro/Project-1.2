package project12.group19.gui;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * This is a temporary class for launching 3d GUI until we merge it in
 * {@link project12.group19.Entrypoint}.
 */
public class DesktopLauncher {

//    public static void main(String[] args) {
//        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
//        /**
//         * Sets the polling rate during idle time in non-continuous rendering mode.
//         *
//         * POLLING RATE - how frequently your mouse sensor refreshes its position each second. For instance, on the off
//         * chance that you have your mouse set to 125Hz Polling Rate, your mouse will refresh its position 125 times
//         * each second.
//         *
//         * IDLE TIME - the total time a computer or device is powered on, but has not been used.
//         *
//         * RENDERING - the process of generating a photorealistic or non-photorealistic image from a 2D or 3D model by
//         * means of a computer program.
//         */
//        config.setIdleFPS(60);
//        /**
//         * Sets whether to use vsync.
//         * VERTICAL SYNC (VSYNC) - synchronizes the refresh rate and frame rate of a monitor to prevent screen tearing.
//         * causes stuttered frame display
//         */
//        config.useVsync(true);
//        config.setTitle("Golf");
//
//        config.setWindowedMode(960, 640);
//        // for full screen
//        // config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
//        new Lwjgl3Application(new Boot(), config);
//    }

    public static void main (String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Project 1-2 Putting / Group 19");
        config.setWindowedMode(1000, 900);
        config.useVsync(true);
        new Lwjgl3Application(new Drop((x, y) -> Math.sin((x - y) / 7 + 0.5)), config);


    }
}
