package project12.group19.incubating;

import project12.group19.MotionStateClass;
import project12.group19.api.domain.Item;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionState;
import project12.group19.api.support.ConfigurationReader;
import project12.group19.math.parser.Parser;
import project12.group19.math.parser.component.ComponentRegistry;
import project12.group19.math.parser.expression.InfixExpression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Reader implements ConfigurationReader {
    public Configuration read(String path) throws IOException {

        double xSpeed = 0;
        double ySpeed = 0;
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

        Map<String, String> values = new HashMap<>();

        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int index = 0;

        while ((st = br.readLine()) != null) {
            index = 0;
            while (st.charAt(index) != '=') {
                index++;
            }
            int separatorIndex = index;
            if (st.charAt(index) == '=') {
                String key = st.substring(0, separatorIndex - 1).trim();
                String value = st.substring(separatorIndex + 1).trim();
                values.put(key, value);
            }
        }
        xPosition = Integer.parseInt(values.get("x0"));
        yPosition = Integer.parseInt(values.get("y0"));
        xHole = Integer.parseInt(values.get("xt"));
        yHole = Integer.parseInt(values.get("yt"));
        radius = Double.parseDouble(values.get("r"));
        groundFriction = Friction.create(
                Double.parseDouble(values.get("mus")),
                Double.parseDouble(values.get("muk"))
        );

        InfixExpression heightExpression = new Parser(ComponentRegistry.standard()).parse(values.get("heightProfile"));
        heightProfile = (x, y) -> {
            InfixExpression resolved = heightExpression.resolve(Map.of("x", x, "y", y, "pi", Math.PI));
            return resolved.calculate()
                    .orElseThrow(() -> {
                        String message = String.format(
                                "Height profile function %s is not defined in point x=.4%f, y=.4%f",
                                resolved,
                                x,
                                y
                        );
                        return new IllegalArgumentException(message);
                    });
        };

        initialMotion = new MotionStateClass(xSpeed, ySpeed, xPosition, yPosition);
        hole = new Hole(xHole, yHole, radius);

        return new Configuration.Standard(heightProfile, obstacles, initialMotion, groundFriction, sandFriction, hole);
    }
}
