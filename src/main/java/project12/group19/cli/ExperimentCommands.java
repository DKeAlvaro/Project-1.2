package project12.group19.cli;

import project12.group19.api.domain.Course;
import project12.group19.api.domain.Player;
import project12.group19.api.engine.Setup;
import project12.group19.api.game.Configuration;
import project12.group19.api.game.Rules;
import project12.group19.api.game.configuration.EngineConfiguration;
import project12.group19.api.motion.MotionCalculator;
import project12.group19.api.motion.MotionHandler;
import project12.group19.api.support.ConfigurationReader;
import project12.group19.engine.EngineFactory;
import project12.group19.engine.GameHandler;
import project12.group19.engine.ScheduledEventLoop;
import project12.group19.engine.motion.StandardMotionHandler;
import project12.group19.incubating.HillClimbingVersionGamma;
import project12.group19.incubating.Reader;
import project12.group19.infrastructure.cli.Argument;
import project12.group19.infrastructure.cli.Command;
import project12.group19.infrastructure.cli.Option;
import project12.group19.player.ai.HitCalculator;
import project12.group19.player.ai.NaiveBot;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;

public class ExperimentCommands {
    public static final Command BOT_EXPERIMENTATION_COMMAND = new Command(
            List.of(
                    Argument.optional("surface")
            ),
            Set.of(
                    new Option("configuration", "Path to configuration"),
                    new Option("iterations", "Number of iterations to run"),
                    new Option("bot", "Bot to use")
            ),
            invocation -> {
                try {
                    ConfigurationReader reader = new Reader();
                    Configuration configuration = reader.read(invocation.tryGetOptionValue("configuration").orElse("configuration.properties"));
                    configuration = configuration.withEngineConfiguration(
                            configuration.getEngineConfiguration().withTiming(
                                    new EngineConfiguration.Timing.Standard(
                                            configuration.getEngineConfiguration().getTiming().getTimeStep(),
                                            OptionalDouble.of(0),
                                            OptionalDouble.of(0)
                                    )
                            )
                    );
                    MotionCalculator solver = EngineFactory.createMotionCalculator(configuration);
                    Course course = EngineFactory.createCourse(configuration);
                    Rules rules = EngineFactory.createRules(configuration);

                    for (int i = 0; i < invocation.tryGetOptionValue("iterations").map(Integer::parseInt).orElse(100); i++) {
                        System.out.println();
                        System.out.println("--- Beginning simulation #" + i + " ---");
                        String bot = invocation.tryGetOptionValue("bot").orElse("naive");
                        MotionHandler motionHandler = new StandardMotionHandler(course, rules, solver);
                        HillClimbingVersionGamma base = new HillClimbingVersionGamma(motionHandler, configuration);
                        Player player = switch (bot) {
                            case "naive" -> new NaiveBot(new HitCalculator.Adjusting());
                            case "hill-climbing" -> state -> base.hillClimbing(state.getBallState().getXPosition(), state.getBallState().getYPosition());
                            default -> throw new IllegalArgumentException("Unknown bot " + bot);
                        };

                        Setup setup = EngineFactory.createSetup(configuration, player, List.of());
                        GameHandler handler = new GameHandler(ScheduledEventLoop.standard());
                        handler.launch(setup).join();
                        System.out.println("--- End of simulation ---");
                    }

                    return 0;
                } catch (Exception e) {
                    System.out.println("Unexpected exception: " + e.getMessage());
                    e.printStackTrace(System.out);
                    return 1;
                }
            },
            Map.of(),
            "Launches bot experiments"
    );
}
