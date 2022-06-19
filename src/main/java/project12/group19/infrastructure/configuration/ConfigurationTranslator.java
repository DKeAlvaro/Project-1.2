package project12.group19.infrastructure.configuration;

import project12.group19.api.domain.Item;
import project12.group19.api.game.Configuration;
import project12.group19.api.game.configuration.EngineConfiguration;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.plane.PlanarDimensions;
import project12.group19.api.geometry.plane.PlanarShape;
import project12.group19.api.physics.motion.Friction;
import project12.group19.api.physics.motion.MotionState;
import project12.group19.math.parser.Parser;
import project12.group19.math.parser.component.ComponentRegistry;
import project12.group19.math.parser.expression.PostfixExpression;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationTranslator {
    public static final double DEFAULT_TREE_RADIUS = 0.25;
    public static final double DEFAULT_SANDPIT_WIDTH = 2;
    public static final double DEFAULT_SANDPIT_HEIGHT = 1;
    public static final double DEFAULT_LAKE_WIDTH = 2;
    public static final double DEFAULT_LAKE_HEIGHT = 1;

    public static final double DEFAULT_STATIC_FRICTION = 0.2;
    public static final double DEFAULT_KINETIC_FRICTION = 0.1;
    public static final double DEFAULT_SAND_STATIC_FRICTION = 0.3;
    public static final double DEFAULT_SAND_KINETIC_FRICTION = 0.25;

    private static Map<Object, Object> translateMetadata(ConfigurationContainer container) {
        return container.streamDescendantKeys().collect(Collectors.toMap(Function.identity(), container::getString));
    }

    private static Map<Object, Object> extractMetadata(ConfigurationContainer container) {
        return container.scoped("metadata", ConfigurationTranslator::translateMetadata);
    }

    private static Stream<Item> translateTrees(ConfigurationContainer root) {
        return root.scoped("course.items.trees", scope -> {
            double fallbackRadius = scope.getDouble("defaults.radius", DEFAULT_TREE_RADIUS);
            return scope.streamChildren("instances")
                    .map(tree -> {
                        double radius = tree.tryGetDouble("radius").orElse(fallbackRadius);
                        PlanarCoordinate position = PlanarCoordinate.create(
                                tree.getDouble("x"),
                                tree.getDouble("y")
                        );
                        PlanarShape shape = PlanarShape.Ellipse.centered(position, PlanarDimensions.square(radius * 2));
                        Map<Object, Object> metadata = extractMetadata(tree);
                        return Item.Obstacle.create(shape, metadata);
                    });
        });
    }

    private static Stream<Item> translateSandpits(ConfigurationContainer root, Friction defaultFriction) {
        return root.scoped("course.items.sandpits", scope -> {
            ConfigurationContainer defaults = scope.getChild("defaults");
            double defaultSandpitWidth = defaults.tryGetDouble("width", "radius").orElse(DEFAULT_SANDPIT_WIDTH);
            double defaultSandpitHeight = defaults.tryGetDouble("height", "radius").orElse(DEFAULT_SANDPIT_HEIGHT);
            return scope.streamChildren("instances")
                    .map(pit -> {
                        double x = pit.getDouble("x");
                        double y = pit.getDouble("y");
                        double width = pit.getDouble(List.of("width", "radius"), defaultSandpitWidth);
                        double height = pit.getDouble(List.of("height", "radius"), defaultSandpitHeight);
                        Friction friction = Friction.create(
                                pit.getDouble("friction.static", defaultFriction.getStaticCoefficient()),
                                pit.getDouble("friction.kinetic", defaultFriction.getDynamicCoefficient())
                        );
                        PlanarShape shape = PlanarShape.Ellipse.centered(x, y, width, height);
                        Map<Object, Object> metadata = extractMetadata(pit);

                        return Item.Overlay.create(shape, friction, metadata);
                    });
        });
    }

    private static Item translateLake(double x, double y, double width, double height, Map<Object, Object> metadata) {
        return Item.RestrictedZone.create(PlanarShape.Ellipse.centered(x, y, width, height), metadata);
    }

    private static Stream<Item> translateLakes(ConfigurationContainer root) {
        Stream<Item> deprecatedLake = root.tryGetDouble("startingLakeX").stream()
                .mapToObj(x -> {
                    double y = root.getDouble("startingLakeY");
                    double width = root.getDouble("endingLakeX") - x;
                    double height = root.getDouble("endingLakeY") - y;
                    return translateLake(x, y, width, height, Map.of());
                });

        return root.scoped("course.items.lakes", scope -> {
            double defaultHeight = scope.getDouble("defaults.height", DEFAULT_LAKE_HEIGHT);
            double defaultWidth = scope.getDouble("defaults.width", DEFAULT_LAKE_WIDTH);

            Stream<Item> multiLakes = root.streamChildren("course.items.lakes.instances")
                    .map(lake -> translateLake(
                            lake.getDouble("x"),
                            lake.getDouble("y"),
                            lake.getDouble("width", defaultWidth),
                            lake.getDouble("height", defaultHeight),
                            extractMetadata(lake)
                    ));

            return Stream.concat(multiLakes, deprecatedLake);
        });
    }

    private static Stream<Item> translateItems(ConfigurationContainer root, Friction sandFriction) {
        return Stream.concat(
                Stream.concat(
                        translateTrees(root),
                        translateSandpits(root, sandFriction)
                ),
                translateLakes(root)
        );
    }

    private static EngineConfiguration translateEngineConfiguration(ConfigurationContainer scoped) {
        EngineConfiguration.Physics physics = scoped.scoped("physics", scope ->
                new EngineConfiguration.Physics.Standard(
                        scope.tryGetString("ode-solver").map(String::toLowerCase).orElse(null),
                        scope.tryGetString("acceleration").map(String::toLowerCase).orElse(null)
                )
        );

        EngineConfiguration.Timing timing = scoped.scoped("timing", scope ->
                new EngineConfiguration.Timing.Standard(
                        scope.getDouble("step", 0.01),
                        scope.tryGetDouble("intervals.computation"),
                        scope.tryGetDouble("intervals.notification")
                )
        );

        EngineConfiguration.Noise noise = scoped.scoped("noise", scope ->
                new EngineConfiguration.Noise.Standard(
                        scope.tryGetDouble("velocity", "value"),
                        scope.tryGetDouble("direction", "value")
                )
        );

        return new EngineConfiguration.Standard(timing, noise, physics);
    }

    public static Configuration translate(ConfigurationContainer container) {
        Friction sandFriction = Friction.create(
                container.getDouble(List.of("muss", "course.friction.sand.static"), DEFAULT_SAND_STATIC_FRICTION),
                container.getDouble(List.of("muks", "course.friction.sand.kinetic"), DEFAULT_SAND_KINETIC_FRICTION)
        );

        Set<Item> items = translateItems(container, sandFriction).collect(Collectors.toSet());

        String surface = container.getString(List.of("heightProfile", "course.surface"));
        PostfixExpression expression = new Parser(ComponentRegistry.standard()).parse(surface);
        return new Configuration.Standard(
                surface,
                items,
                MotionState.create(
                        container.getDouble(List.of("vx", "course.ball.velocity.x"), 0.0),
                        container.getDouble(List.of("vy", "course.ball.velocity.y"), 0.0),
                        container.getDouble(List.of("x0", "course.ball.position.x"), 0.0),
                        container.getDouble(List.of("y0", "course.ball.position.y"), 0.0)
                ),
                Friction.create(
                        container.getDouble(List.of("mus", "course.friction.default.static"), DEFAULT_STATIC_FRICTION),
                        container.getDouble(List.of("muk", "course.friction.default.kinetic"), DEFAULT_KINETIC_FRICTION)
                ),
                sandFriction,
                Item.Target.create(
                        container.getDouble("xt", "course.target.x"),
                        container.getDouble("yt", "course.target.y"),
                        container.getDouble(List.of("r", "course.target.radius"), 0.1)
                ),
                PlanarDimensions.create(
                        container.getDouble("course.width", 50.0),
                        container.getDouble("course.height", 50.0)
                ),
                translateEngineConfiguration(container.getChild("engine"))
        );
    }
}
