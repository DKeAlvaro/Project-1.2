package project12.group19.infrastructure.cli;

public record Argument(
        String name,
        Arity arity,
        String description
) {
    public Argument(String name) {
        this(name, new Arity(1, 1));
    }
    public Argument(String name, Arity arity) {
        this(name, arity, null);
    }

    public record Arity(Integer minimum, Integer maximum) {
        public Arity(int value) {
            this(value, value);
        }
    }

    public static Argument regular(String name) {
        return new Argument(name);
    }

    public static Argument optional(String name) {
        return new Argument(name, new Arity(0, 1));
    }

    public static Argument variableLength(String name, String description) {
        return new Argument(name, new Arity(0, Integer.MAX_VALUE), description);
    }

    public static Argument variableLength(String name) {
        return variableLength(name, null);
    }
}
