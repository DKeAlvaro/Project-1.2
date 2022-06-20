package project12.group19.infrastructure.cli;

import project12.group19.api.infrastructure.cli.Command;
import project12.group19.api.infrastructure.cli.Invocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CliRunner {
    private static final Command CONFIGURATION = new Command(
            List.of(),
            Set.of(),
            invocation -> Command.HELP.executor().applyAsInt(invocation),
            Map.of(
                    "experiment", new Command(
                            List.of(),
                            Set.of(),
                            invocation -> {
                                System.out.println("Parent command for experiments. Please choose one of the following:");
                                for (String key : invocation.getInvokedCommand().children().keySet()) {
                                    System.out.println(" - " + key);
                                }
                                return 0;
                            },
                            Map.of(
                                    "bot", ExperimentCommands.BOT_EXPERIMENTATION_COMMAND
                            ),
                            "Parent command for experiments"
                    )
            ),
            "Group 19 putting"
    );

    public void run(String[] args) {
        Invocation invocation = new CliParser(CONFIGURATION).parse(args);
        System.exit(invocation.getInvokedCommand().executor().applyAsInt(invocation));
    }
}
