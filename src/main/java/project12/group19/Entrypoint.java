package project12.group19;

import java.io.IOException;
import java.lang.module.Configuration;

import project12.group19.api.engine.Setup;
import project12.group19.api.motion.MotionCalculator;
import project12.group19.api.motion.MotionState;
// import project12.group19.api.game.Configuration;
import project12.group19.api.ui.GUI;

import project12.group19.incubating.Reader;

public class Entrypoint {
    public static void main(String[] args) throws Exception {
        // System.out.println("Hello group 19!");
        String location = "C:\\Users\\Alvaro\\Desktop\\example_inputfile.txt";
        Reader reader = new Reader();
        project12.group19.api.game.Configuration configuration;
        configuration = reader.read(location);
        

        new Setup.Standard(configuration, 3, 3, null, null, null);
        new GUI((int)configuration.getHole().getxHole(), (int)configuration.getHole().getyHole(), (int)configuration.getHole().getRadius(), (int)configuration.getInitialMotion().getXPosition(), (int)configuration.getInitialMotion().getYPosition(), 0);

    }
}
