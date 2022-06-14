package project12.group19.infrastructure.configuration;

import project12.group19.api.domain.Item;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.space.Hole;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionState;
import project12.group19.incubating.WaterLake;
import project12.group19.math.parser.Parser;
import project12.group19.math.parser.component.ComponentRegistry;
import project12.group19.math.parser.expression.PostfixExpression;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationTranslator {
    public static Configuration translate(ConfigurationContainer container) {
        Stream<WaterLake> deprecatedLake = container.tryGetDouble("startingLakeX").stream()
                .mapToObj(startingX -> new WaterLake(
                        startingX,
                        container.getDouble("endingLakeX"),
                        container.getDouble("startingLakeY"),
                        container.getDouble("endingLakeY")
                ));

        Stream<WaterLake> multiLakes = container.streamChildren("obstacles.lakes")
                .map(lake -> new WaterLake(
                        lake.getDouble("start.x"),
                        lake.getDouble("end.x"),
                        lake.getDouble("start.y"),
                        lake.getDouble("end.y")
                ));

        Set<WaterLake> lakes = Stream.concat(deprecatedLake, multiLakes).collect(Collectors.toSet());

        Stream<Item> trees = container.streamChildren("obstacles.trees")
                .map(tree -> new Item.Standard("tree", PlanarCoordinate.create(tree.getDouble("x"), tree.getDouble("y"))));

        Stream<Item> sandpits = container.streamChildren("obstacles.sandpits")
                .map(pit -> new Item.Standard("sandpit", PlanarCoordinate.create(pit.getDouble("x"), pit.getDouble("y"))));

        Set<Item> obstacles = Stream.concat(trees, sandpits).collect(Collectors.toSet());

        // TODO: merge lakes into obstacles

        double velocityNoise = container.getDouble(List.of("noise.velocity", "noise.value"), 0.0);
        double directionNoise = container.getDouble(List.of("noise.direction", "noise.value"), 0.0);

        return new Configuration.Standard(
                container.getValue(List.of("heightProfile", "course.surface"), value -> {
                    PostfixExpression heightExpression = new Parser(ComponentRegistry.standard()).parse(value);
                    return (x, y) -> {
                        if (lakes.stream().anyMatch(lake -> lake.contains(x, y))) {
                            return -1;
                        }
                        PostfixExpression resolved = heightExpression.resolve(Map.of("x", x, "y", y));
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
                }),
                obstacles,
                MotionState.create(
                        container.getDouble(List.of("vx", "course.ball.velocity.x"), 0.0),
                        container.getDouble(List.of("vy", "course.ball.velocity.y"), 0.0),
                        container.getDouble(List.of("x0", "course.ball.position.x"), 0.0),
                        container.getDouble(List.of("y0", "course.ball.position.y"), 0.0)
                ),
                Friction.create(
                        container.getDouble("mus", "course.friction.default.static"),
                        container.getDouble("muk", "course.friction.default.kinetic")
                ),
                Friction.create(
                        container.getDouble("muss", "course.friction.sand.static"),
                        container.getDouble("muks", "course.friction.sand.kinetic")
                ),
                new Hole(
                        container.getDouble("xt", "course.target.x"),
                        container.getDouble("yt", "course.target.y"),
                        container.getDouble("r", "course.target.radius")
                ),
                container.getDouble(List.of("timeScale", "engine.rates.scale"), 1.0),
                container.getString("player"),
                lakes,
                PlanarDimensions.create(
                        container.getDouble("course.width", 50.0),
                        container.getDouble("course.height", 50.0)
                ),
                container.getInt("engine.rates.tick", 100),
                container.getInt("engine.rates.refresh", 60),
                velocityNoise > 0 || directionNoise > 0 ? new Configuration.Noise.Standard(velocityNoise, directionNoise) : null
        );
    }
}
