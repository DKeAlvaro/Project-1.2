package project12.group19.infrastructure.cli;

import java.util.Set;

public record Option(
        String id,
        Set<String> names,
        String description
) {
    public Option(String id, String description) {
        this(id, Set.of(), description);
    }

    public Option(String id, Set<String> names) {
        this(id, names, null);
    }
}
