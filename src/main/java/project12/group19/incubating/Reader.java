package project12.group19.incubating;

import project12.group19.api.domain.Item;
import project12.group19.api.game.Configuration;
import project12.group19.api.game.configuration.EngineConfiguration;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.plane.PlanarShape;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionState;
import project12.group19.api.support.ConfigurationReader;
import project12.group19.infrastructure.configuration.ConfigurationContainer;
import project12.group19.infrastructure.configuration.ConfigurationTranslator;
import project12.group19.math.parser.Parser;
import project12.group19.math.parser.component.ComponentRegistry;
import project12.group19.math.parser.expression.PostfixExpression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Reader implements ConfigurationReader {
    private static int parseInteger(String source, int fallback) {
        return source != null ? Integer.parseInt(source) : fallback;
    }
    private static int parseInteger(String source) {
        return parseInteger(source, 0);
    }

    private static double parseDouble(String source, double fallback) {
        return source != null ? Double.parseDouble(source) : fallback;
    }

    private static WaterLake parseLake(Map<String, String> values) {
        if (values.containsKey("startingLakeX")) {
            double startingX = Double.parseDouble(values.get("startingLakeX"));
            double startingY = Double.parseDouble(values.get("startingLakeY"));
            double endingX = Double.parseDouble(values.get("endingLakeX"));
            double endingY = Double.parseDouble(values.get("endingLakeY"));

            return new WaterLake(startingX, endingX, startingY, endingY);
        }

        return null;
    }

    public Configuration read(String path) throws IOException {
        Properties source = new Properties();
        source.load(new BufferedReader(new FileReader(path)));
        ConfigurationContainer container = new ConfigurationContainer(source);

        int compatibility = container.getInt("api.configuration.version", 2);

        if (compatibility == 2) {
            return ConfigurationTranslator.translate(container);
        }

        // leaving previous configuration reader just in case of
        // incompatibility issues
        // set api.version = 1 in configuration.properties to switch
        // back to it

        double xSpeed;
        double ySpeed;
        double xPosition;
        double yPosition;

        double xHole;
        double yHole;
        double radius;

        double startingX;
        double startingY;
        double endingX;
        double endingY;


        HeightProfile heightProfile = null;
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
        xPosition = parseInteger(values.get("x0"));
        yPosition = parseInteger(values.get("y0"));
        xSpeed = parseInteger(values.get("vx"));
        ySpeed = parseInteger(values.get("vy"));

        xHole = Integer.parseInt(values.get("xt"));
        yHole = Integer.parseInt(values.get("yt"));
        radius = Double.parseDouble(values.get("r"));

        double fieldWidth = parseDouble(values.get("width"), 50);
        double fieldHeight = parseDouble(values.get("width"), 50);

        PlanarDimensions field = PlanarDimensions.create(
                fieldWidth,
                fieldHeight
        );

        WaterLake lake = parseLake(values);

        groundFriction = Friction.create(
                Double.parseDouble(values.get("mus")),
                Double.parseDouble(values.get("muk"))
        );
        double timescale = parseDouble(values.get("timeScale"), 1.0);
        int tickRate = parseInteger(values.get("tickRate"), 60);
        int refreshRate = parseInteger(values.get("refreshRate"), 60);
        String player = values.get("player");

        String expression = values.get("heightProfile");
        PostfixExpression heightExpression = new Parser(ComponentRegistry.standard()).parse(expression);
        heightProfile = (x, y) -> {
            if (lake != null && lake.contains(x, y)) {
                return -1;
            }
            PostfixExpression resolved = heightExpression.resolve(Map.of("x", x, "y", y, "pi", Math.PI, "e", Math.E));
            return resolved.calculate().orElseThrow(() -> {
                        String message = String.format(
                                "Height profile function %s is not defined in point x=.4%f, y=.4%f",
                                resolved,
                                x,
                                y
                        );
                        return new IllegalArgumentException(message);
                    });
        };

        initialMotion = new MotionState.Standard(xSpeed, ySpeed, xPosition, yPosition);
        hole = new Hole(xHole, yHole, radius);

        EngineConfiguration.Timing timing = new EngineConfiguration.Timing.Standard(
                1 / (double) tickRate,
                OptionalDouble.of(1 / (double) tickRate),
                OptionalDouble.of(1 / (double) refreshRate)
        );
        return new Configuration.Standard(
                expression,
                heightProfile,
                Optional.ofNullable(lake)
                        .<Set<Item>>map(identity -> {
                            PlanarCoordinate position = PlanarCoordinate.create(identity.getStartingX(), identity.getStartingY());
                            PlanarDimensions dimensions = PlanarDimensions.create(
                                    identity.getFinishingX() - identity.getStartingX(),
                                    identity.getFinishingY() - identity.getStartingY()
                            );
                            return Set.of(Item.RestrictedZone.create(new PlanarShape.Rectangle(position, dimensions)));
                        })
                        .orElse(Set.of()),
                initialMotion,
                groundFriction,
                sandFriction,
                hole,
                field,
                new EngineConfiguration.Standard(
                        timing,
                        EngineConfiguration.Noise.empty(),
                        EngineConfiguration.Physics.empty()
                )
        );
    }

}
