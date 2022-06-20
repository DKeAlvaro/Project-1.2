package project12.group19.infrastructure.cli;

import project12.group19.api.infrastructure.cli.Argument;
import project12.group19.api.infrastructure.cli.Command;
import project12.group19.api.infrastructure.cli.Invocation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CliParser {
    private final Command configuration;

    public CliParser(Command configuration) {
        this.configuration = configuration;
    }

    public Invocation parse(String... arguments) {
        return parse(Arrays.asList(arguments));
    }

    public Invocation parse(List<String> input) {
        List<String> arguments = new ArrayList<>();
        List<Map.Entry<String, String>> options = new ArrayList<>();

        List<Command> commands = new ArrayList<>();
        commands.add(configuration);
        Command current = configuration;

        boolean terminated = false;

        for (int i = 0; i < input.size(); i++) {
            String scope = input.get(i);
            if (scope.startsWith("-")) {
                options.add(Map.entry(scope, input.get(i + 1)));
                i++;
                continue;
            }

            if (!terminated && current.tryGetChild(scope).isPresent()) {
                current = current.getChild(scope);
                commands.add(current);
                continue;
            }

            terminated = true;
            arguments.add(scope);
        }

        return new Invocation.Standard(
                commands,
                parseArguments(current, arguments),
                assembleOptions(current, options)
        );
    }

    private static String stripDashes(String optionName) {
        return optionName.replaceAll("^-*", "");
    }

    private Map<String, List<String>> assembleOptions(Command current, List<Map.Entry<String, String>> options) {
        Map<String, String> aliases = current.options().stream()
                .flatMap(option ->
                        Stream
                                .concat(Stream.of(option.id()), option.names().stream())
                                .map(alias -> Map.entry(alias, option.id()))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return options.stream()
                .map(entry -> {
                    String name = stripDashes(entry.getKey());
                    String resolved = aliases.getOrDefault(name, name);
                    return Map.entry(resolved, entry.getValue());
                })
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    private Map<String, List<String>> parseArguments(Command command, List<String> arguments) {
        Queue<String> unprocessed = new ArrayDeque<>(arguments);
        Map<String, List<String>> accumulator = new HashMap<>(command.arguments().size());
        for (Argument argument : command.arguments()) {
            List<String> values = new ArrayList<>(argument.arity().minimum());
            for (int i = 0; i < argument.arity().maximum(); i++) {
                if (unprocessed.isEmpty()) {
                    if (argument.arity().minimum() <= i) {
                        break;
                    }

                    String message = "Not enough values provided for argument " + argument.name() +
                            "(expected at least " + argument.arity().minimum() + ", got " + i + ")\n";

                    throw new IllegalArgumentException(message + reportProcessedArguments(command, arguments, accumulator, unprocessed));
                }

                values.add(unprocessed.remove());
            }

            if (!values.isEmpty()) {
                accumulator.put(argument.name(), values);
            }
        }

        if (!unprocessed.isEmpty()) {
            throw new IllegalArgumentException("Extra arguments passed!\n" + reportProcessedArguments(command, arguments, accumulator, unprocessed));
        }

        return accumulator;
    }

    private static StringBuilder reportProcessedArguments(
            Command command,
            List<String> input,
            Map<String, List<String>> processed,
            Collection<String> unprocessed
    ) {
        StringBuilder builder = new StringBuilder("Provided arguments:\n");

        for (String value : input) {
            builder.append("  [").append(value).append("]\n");
        }

        builder.append("Processed arguments:\n");

        for (Argument argument : command.arguments()) {
            builder.append("  ").append(argument.name()).append(":");

            List<String> values = processed.getOrDefault(argument.name(), List.of());
            if (values.isEmpty()) {
                builder.append(" (None)\n");
            } else {
                builder.append("\n");
                for (String value : values) {
                    builder.append("    [").append(value).append("]\n");
                }
            }
        }

        builder.append("Unprocessed arguments:\n");
        for (String value : unprocessed) {
            builder.append("  [").append(value).append("]\n");
        }

        return builder;
    }

    public static void main(String[] args) {
        Invocation invocation = new CliParser(Command.HELP).parse(args);
        System.exit(invocation.getInvokedCommand().executor().applyAsInt(invocation));
    }
}
