package project12.group19.infrastructure.cli;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;

public record Command(
        List<Argument> arguments,
        Set<Option> options,
        ToIntFunction<Invocation> executor,
        Map<String, Command> children,
        String description
) {
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }

    public Optional<Command> tryGetChild(String name) {
        return Optional.ofNullable(children.get(name));
    }

    public Command getChild(String name) {
        return tryGetChild(name).orElseThrow(() -> {
            String message = "Child command with name " + name + " could not be found";
            throw new IllegalArgumentException(message);
        });
    }

    public static final Command HELP = new Command(
            List.of(Argument.variableLength("command", "Names of the commands to show")),
            Set.of(),
            invocation -> {
                BiFunction<String, Integer, String> indent = (text, indentation) ->
                        Pattern.compile("^", Pattern.DOTALL).matcher(text).replaceAll(" ".repeat(indentation));
                Command scope = invocation.getInvokedCommandHierarchy().get(0);
                List<String> visited = new ArrayList<>();
                for (String next : invocation.getArgumentValues("command")) {
                    visited.add(next);
                    if (!scope.hasChild(next)) {
                        System.out.println("No such command: " + visited);
                        return 1;
                    }
                    scope = scope.getChild(next);
                }

                System.out.println(String.join(" ", visited));
                System.out.println();

                if (scope.description() != null) {
                    System.out.println("Description:");
                    System.out.println(indent.apply(scope.description, 2));
                    System.out.println();
                }

                if (!scope.arguments.isEmpty()) {
                    System.out.println("Arguments:");

                    for (Argument argument : scope.arguments) {
                        System.out.println(indent.apply(argument.name() + ":", 2));
                        System.out.println(indent.apply("Arity: " + argument.arity().minimum() + " to " + argument.arity().maximum(), 4));

                        if (argument.description() != null) {
                            System.out.println(indent.apply("Description:", 4));
                            System.out.println(indent.apply(argument.description(), 6));
                        }
                    }

                    System.out.println();
                }

                if (!scope.options().isEmpty()) {
                    System.out.println("Options:");

                    for (Option option : scope.options) {
                        System.out.print(indent.apply(option.id(), 2));

                        if (option.names().isEmpty() && option.description() == null) {
                            System.out.println();
                            continue;
                        }

                        System.out.println(":");

                        if (!option.names().isEmpty()) {
                            System.out.println(indent.apply("Names: " + String.join(", ", option.names()), 4));
                        }

                        if (option.description() != null) {
                            System.out.println(indent.apply("Description:", 4));
                            System.out.println(indent.apply(option.description(), 6));
                        }
                    }
                }

                if (!scope.children().isEmpty()) {
                    System.out.println("Children: " + String.join(", ", scope.children().keySet()));
                }

                return 0;
            },
            Map.of(),
            "Try calling it against self"
    );
}
