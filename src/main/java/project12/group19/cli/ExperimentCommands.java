package project12.group19.cli;

import project12.group19.api.domain.Course;
import project12.group19.api.domain.Player;
import project12.group19.api.engine.Setup;
import project12.group19.api.game.Configuration;
import project12.group19.api.game.Rules;
import project12.group19.api.game.configuration.EngineConfiguration;
import project12.group19.api.game.lifecycle.GameStats;
import project12.group19.api.motion.MotionCalculator;
import project12.group19.api.motion.MotionHandler;
import project12.group19.api.support.ConfigurationReader;
import project12.group19.engine.EngineFactory;
import project12.group19.engine.GameHandler;
import project12.group19.engine.ScheduledEventLoop;
import project12.group19.engine.motion.StandardMotionHandler;
import project12.group19.incubating.Reader;
import project12.group19.infrastructure.cli.Argument;
import project12.group19.infrastructure.cli.Command;
import project12.group19.infrastructure.cli.Option;
import project12.group19.player.ai.HitCalculator;
import project12.group19.player.ai.NaiveBot;
import project12.group19.player.ai.hc.HillClimbingBot;

import java.time.ZonedDateTime;
import java.util.*;

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
                    String identifier = ZonedDateTime.now().toString();
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
                    String surface = configuration.getSurface();
                    MotionCalculator solver = EngineFactory.createMotionCalculator(configuration);
                    Course course = EngineFactory.createCourse(configuration);
                    Rules rules = EngineFactory.createRules(configuration);

                    System.out.println("--- Running experiments : " + identifier + " ---");
                    System.out.println("--- Surface: " + configuration.getSurface() + " ---");
                    System.out.println();
                    List<String> bots = invocation.getOptionValues("bot");
                    if (bots.isEmpty()) {
                        bots = List.of("naive", "hill-climbing");
                    }

                    Map<String, List<GameStats>> results = new HashMap<>();
                    for (String bot : bots) {
                        for (int i = 1; i <= invocation.tryGetOptionValue("iterations").map(Integer::parseInt).orElse(100); i++) {
                            System.out.println();
                            System.out.println("--- Beginning simulation " + bot + "#" + i + " ---");
                            MotionHandler motionHandler = new StandardMotionHandler(course, rules, solver);
                            Player player = switch (bot) {
                                case "naive" -> new NaiveBot(new HitCalculator.Adjusting());
                                case "hill-climbing" -> new HillClimbingBot(motionHandler, configuration);
                                default -> throw new IllegalArgumentException("Unknown bot " + bot);
                            };

                            Setup setup = EngineFactory.createSetup(configuration, player, List.of());
                            GameHandler handler = new GameHandler(ScheduledEventLoop.standard());
                            GameStats stats = handler.launch(setup).join();
                            results.computeIfAbsent(bot, key -> new ArrayList<>()).add(stats);
                            System.out.println("--- End of simulation " + bot + "#" + i + " ---");
                        }
                    }

                    System.out.println("--- Simulation results ---");
                    System.out.println("bot, surface, id, result, hits, simulations per hit");
                    results.forEach((bot, list) -> {
                        for (int i = 0; i < list.size(); i++) {
                            GameStats result = list.get(i);
                            String line = String.format(
                                    "%s, \"%s\", %d, %s, %d, %f",
                                    bot,
                                    surface,
                                    i + 1,
                                    result.getRounds().get(result.getRounds().size() - 1).getTerminationReason(),
                                    result.getRounds().size(),
                                    result.getRounds().stream().mapToInt(round -> round.getHit().getSimulations().size()).sum() / (double) result.getRounds().size()
                            );
                            System.out.println(line);
                        }
                    });

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
