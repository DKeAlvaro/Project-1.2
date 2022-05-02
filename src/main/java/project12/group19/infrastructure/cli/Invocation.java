package project12.group19.infrastructure.cli;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Invocation {
    Command getInvokedCommand();
    List<Command> getInvokedCommandHierarchy();
    Map<String, List<String>> getArguments();
    Map<String, List<String>> getOptions();

    default List<String> getArgumentValues(String name) {
        return getArguments().getOrDefault(name, List.of());
    }
    default <T> List<T> getArgumentValues(String name, Function<String, T> transformer) {
        return getArgumentValues(name).stream().map(transformer).collect(Collectors.toList());
    }
    default Optional<String> tryGetArgumentValue(String name) {
        return getArgumentValues(name).stream().findFirst();
    }
    default <T> Optional<T> tryGetArgumentValue(String name, Function<String, T> transformer) {
        return tryGetArgumentValue(name).map(transformer);
    }
    default String getArgumentValue(String name) {
        return tryGetArgumentValue(name).orElseThrow(() -> new IllegalStateException("Argument with name " + name + " is not present"));
    }
    default String getArgumentValue(String name, String fallback) {
        return tryGetArgumentValue(name).orElse(fallback);
    }
    default <T> T getArgumentValue(String name, Function<String, T> transformer) {
        return transformer.apply(getArgumentValue(name));
    }
    default <T> T getArgumentValue(String name, Function<String, T> transformer, T fallback) {
        return tryGetArgumentValue(name, transformer).orElse(fallback);
    }

    default List<String> getOptionValues(String name) {
        return getOptions().getOrDefault(name.replaceAll("^-*", ""), List.of());
    }
    default <T> List<T> getOptionValues(String name, Function<String, T> transformer) {
        return getOptionValues(name).stream().map(transformer).collect(Collectors.toList());
    }
    default Optional<String> tryGetOptionValue(String name) {
        return getOptionValues(name).stream().findFirst();
    }
    default <T> Optional<T> tryGetOptionValue(String name, Function<String, T> transformer) {
        return tryGetOptionValue(name).map(transformer);
    }

    default String getOptionValue(String name) {
        return tryGetOptionValue(name).orElseThrow(() -> {
            String message = "Option with name " + name + " is not present";
            return new IllegalStateException(message);
        });
    }

    default <T> T getOptionValue(String name, Function<String, T> transformer) {
        return transformer.apply(getOptionValue(name));
    }

    default <T> T getOptionValue(String name, Function<String, T> transformer, T fallback) {
        return tryGetOptionValue(name, transformer).orElse(fallback);
    }

    record Standard(
            List<Command> hierarchy,
            Map<String, List<String>> arguments,
            Map<String, List<String>> options
    ) implements Invocation {
        @Override
        public Command getInvokedCommand() {
            List<Command> commands = getInvokedCommandHierarchy();
            return commands.get(commands.size() - 1);
        }

        @Override
        public List<Command> getInvokedCommandHierarchy() {
            return hierarchy;
        }

        @Override
        public Map<String, List<String>> getArguments() {
            return arguments;
        }

        @Override
        public Map<String, List<String>> getOptions() {
            return options;
        }
    }
}

