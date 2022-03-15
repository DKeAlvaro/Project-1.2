package src.project12.group19.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import src.project12.group19.MotionStateAlvaro;
import src.project12.group19.api.domain.Item;
import src.project12.group19.api.game.Configuration;
import src.project12.group19.api.geometry.space.HeightProfile;
import src.project12.group19.api.geometry.space.Hole;
import src.project12.group19.api.motion.Friction;
import src.project12.group19.api.motion.MotionState;
import src.project12.group19.api.support.ConfigurationReader;

public class ConfigurationReaderAlvaro implements ConfigurationReader {
    public Configuration read(String path) throws IOException {

        double xSpeed;
        double ySpeed;
        double xPosition;
        double yPosition;

        double xHole;
        double yHole;
        double radius;

        HeightProfile heightProfile = null;
        Set<Item> obstacles = null;
        MotionState initialMotion;
        Friction groundFriction = null;
        Friction sandFriction = null;
        Hole hole;


        int valuesIndex = 0;
        String[] values = new String[12];
        Arrays.fill(values, "");

        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int index = 0;

        while ((st = br.readLine()) != null) {
            index = 0;
            while (st.charAt(index) != '=') {
                index++;
            }
            if (st.charAt(index) == '=') {
                index += 2;
                while (index < st.length()) {
                    values[valuesIndex] += st.charAt(index);
                    index++;
                }
                valuesIndex++;
            }
        }

        for (int i = 0; i < values.length; i++) {
            // System.out.println(values[i]);
        }
        xPosition = Integer.parseInt(values[0]);
        yPosition = Integer.parseInt(values[1]);
        xHole = Integer.parseInt(values[2]);
        yHole = Integer.parseInt(values[3]);
        radius = Double.parseDouble(values[4]);

        initialMotion = new MotionStateAlvaro(xSpeed, ySpeed, xPosition, yPosition);
        hole = new Hole(xHole, yHole, radius);

        return new Configuration.Standard(heightProfile, obstacles, initialMotion, groundFriction, sandFriction, hole);
    }
}
