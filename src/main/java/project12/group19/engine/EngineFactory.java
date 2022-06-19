package project12.group19.engine;

import project12.group19.api.domain.*;
import project12.group19.api.engine.Setup;
import project12.group19.api.game.Configuration;
import project12.group19.api.game.HitMutator;
import project12.group19.api.game.Rules;
import project12.group19.api.game.configuration.EngineConfiguration;
import project12.group19.api.geometry.plane.PlanarRectangle;
import project12.group19.api.motion.*;
import project12.group19.domain.StandardSurface;
import project12.group19.engine.motion.StandardMotionHandler;
import project12.group19.math.ode.Euler;
import project12.group19.math.ode.ODESolver;
import project12.group19.math.ode.RK2;
import project12.group19.math.ode.RK4;
import project12.group19.math.parser.Parser;
import project12.group19.math.parser.component.ComponentRegistry;
import project12.group19.math.parser.expression.PostfixExpression;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EngineFactory {
    private static final Map<String, AccelerationCalculator> ACCELERATION_CALCULATORS = Map.of(
            "basic", new BasicAccelerationCalculator(),
            "advanced", new AdvancedAccelerationCalculator()
    );

    private static final Map<String, ODESolver> ODE_SOLVERS = Map.of(
            "euler", new Euler(),
            "rk2", new RK2(),
            "rk4", new RK4()
    );

    public static Surface createSurface(Configuration configuration) {
        Parser parser = new Parser(ComponentRegistry.standard());
        PostfixExpression expression = parser.parse(configuration.getSurface());
        return new StandardSurface(
                expression,
                configuration.getGroundFriction(),
                configuration.getItems().stream()
                        .filter(item -> item instanceof Item.Overlay)
                        .map(Item.Overlay.class::cast)
                        .collect(Collectors.toSet())
        );
    }

    public static MotionCalculator createMotionCalculator(Configuration configuration) {
        EngineConfiguration.Physics physics = configuration.getEngineConfiguration().getPhysics();
        return new Solver(
                physics.getOdeSolver()
                        .map(EngineFactory::resolveODESolver)
                        .orElse(new Euler()),
                createSurface(configuration),
                physics.getAccelerationCalculator()
                        .map(EngineFactory::resolveAccelerationCalculator)
                        .orElse(new BasicAccelerationCalculator())
        );
    }

    public static MotionHandler createMotionHandler(Configuration configuration) {
        return new StandardMotionHandler(
                createCourse(configuration),
                createRules(configuration),
                createMotionCalculator(configuration)
        );
    }

    public static Course createCourse(Configuration configuration) {
        Item target = Item.Target.create(configuration.getHole().getPosition(), configuration.getHole().getRadius());
        Set<Item> items = Stream.concat(configuration.getItems().stream(), Stream.of(target)).collect(Collectors.toSet());
        Surface surface = createSurface(configuration);

        return new Course.Standard(
                surface,
                configuration.getGroundFriction(),
                items
        );
    }

    public static Rules createRules(Configuration configuration) {
        PlanarRectangle field = PlanarRectangle.create(
                -configuration.getDimensions().getWidth() / 2,
                -configuration.getDimensions().getHeight() / 2,
                configuration.getDimensions()
        );

        return new Rules.Standard(
                OptionalInt.of(3),
                OptionalInt.empty(),
                field,
                true
        );
    }

    public static Setup createSetup(Configuration configuration, Player player, List<Consumer<State>> listeners) {
        Course course = createCourse(configuration);
        Rules rules = createRules(configuration);

        // TODO this is a bit dirty. Continue to use optionals instead of zeros.
        double velocityNoiseRange = configuration.getEngineConfiguration().getNoise().getVelocityRange().orElse(0);
        double directionNoiseRange = configuration.getEngineConfiguration().getNoise().getDirectionRange().orElse(0);
        boolean useNoiseMutator = velocityNoiseRange > 0 || directionNoiseRange > 0;
        HitMutator hitMutator = useNoiseMutator ? HitMutator.noise(velocityNoiseRange, directionNoiseRange) : HitMutator.identity();
        EngineConfiguration.Timing timing = configuration.getEngineConfiguration().getTiming();
        EngineConfiguration.Physics physics = configuration.getEngineConfiguration().getPhysics();

        return new Setup.Standard(
                configuration,
                course,
                rules,
                new Setup.Timing.Standard(
                        timing.getTimeStep(),
                        toNanos(timing.resolveComputationalInterval()),
                        toNanos(timing.resolveNotificationInterval())
                ),
                new StandardMotionHandler(course, rules, new Solver(
                        physics.getOdeSolver()
                                .map(EngineFactory::resolveODESolver)
                                .orElse(new Euler()),
                        course.getSurface(),
                        physics.getAccelerationCalculator()
                                .map(EngineFactory::resolveAccelerationCalculator)
                                .orElse(new BasicAccelerationCalculator())
                )),
                player,
                hitMutator,
                listeners
        );
    }

    private static long toNanos(double seconds) {
        return (long) (seconds * 1_000_000_000);
    }

    private static AccelerationCalculator resolveAccelerationCalculator(String name) {
        return Optional.ofNullable(ACCELERATION_CALCULATORS.get(name.toLowerCase()))
                .orElseGet(() -> {
                    System.out.println("Warning: unknown acceleration calculator requested (" + name + ")");
                    System.out.println("Available choices are:");
                    for (String key : ACCELERATION_CALCULATORS.keySet()) {
                        System.out.println(" - " + key);
                    }
                    return new BasicAccelerationCalculator();
                });
    }

    private static ODESolver resolveODESolver(String name) {
        return Optional.ofNullable(ODE_SOLVERS.get(name.toLowerCase()))
                .orElseGet(() -> {
                    System.out.println("Warning: unknown ode solver requested (" + name + ")");
                    System.out.println("Available choices are:");
                    for (String key : ODE_SOLVERS.keySet()) {
                        System.out.println(" - " + key);
                    }
                    return new Euler();
                });
    }
}
