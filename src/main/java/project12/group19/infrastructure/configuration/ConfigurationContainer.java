package project12.group19.infrastructure.configuration;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A helper structure that provides some advanced access to
 * properties-style configuration. It allows some simple type
 * conversions and anchoring to specific point - resolving keys only
 * within some prefix, which is called an anchor here to avoid
 * ambiguity. It is implied that configuration nesting is done via
 * dot-separated keys; the root-level container has access to all the
 * keys, but with use of anchor it is possible to specify a namespace to
 * operate within, using only remainder of keys to refer to data and
 * ignoring all "outer" keys. For example, if properties contain
 * following entries:
 *
 * {@code
 *   alpha.beta.gamma = 12
 *   alpha.beta.delta.sigma = 14
 *   alpha.omega = 15
 *   epsilon.omicron = 14
 * }
 *
 * Then anchor of {@code [alpha, beta]} would allow to refer to
 * options {@code gamma} and {@code delta.sigma} and would render
 * {@code alpha.omega} and {@code epsilon.omicron} inaccessible.
 *
 * @param properties Whole configuration.
 * @param anchor Position within configuration to resolve keys from.
 */
public record ConfigurationContainer(Properties properties, List<String> anchor) {
    public ConfigurationContainer(Properties properties) {
        this(properties, List.of());
    }
    public ConfigurationContainer(Properties properties, String... anchor) {
        this(properties, List.of(anchor));
    }

    private String toPath(String key) {
        return Stream.concat(anchor.stream(), Stream.of(key)).collect(Collectors.joining("."));
    }
    private String toPath(String... keys) {
        return Stream.concat(anchor.stream(), Arrays.stream(keys)).collect(Collectors.joining("."));
    }

    private String keySpecification(String key) {
        return key + " (full path: " + toPath(key) + ")";
    }

    private IllegalStateException missingKeyException(String key) {
        String message = "Configuration value " + keySpecification(key) + " is missing";
        return new IllegalStateException(message);
    }

    private IllegalStateException missingKeysException(List<String> keys) {
        String message = keys.stream()
                .map(this::keySpecification)
                .collect(Collectors.joining(
                        ", ",
                        "Configuration value for keys [",
                        "] is missing"
                ));
        return new IllegalStateException(message);
    }

    public Optional<String> tryGetName() {
        return anchor.isEmpty() ? Optional.empty() : Optional.of(anchor.get(anchor.size() - 1));
    }

    public String getName() {
        return tryGetName().orElseThrow(() -> new IllegalStateException("Root-level container doesn't have a name"));
    }

    public boolean contains(String key) {
        return properties.containsKey(toPath(key));
    }

    public Optional<String> tryGetString(String key) {
        return Optional.ofNullable(properties.getProperty(toPath(key)));
    }

    public String getString(String key) {
        return tryGetString(key).orElseThrow(() -> missingKeyException(key));
    }

    public String getString(String key, String fallback) {
        return tryGetString(key).orElse(fallback);
    }

    public String getString(List<String> keys) {
        return keys.stream()
                .map(this::tryGetString)
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity())
                .orElseThrow(() -> missingKeysException(keys));
    }

    public <T> Optional<T> tryGetValue(String key, BiFunction<? super String, ? super String, ? extends T> converter) {
        String path = toPath(key);
        return Optional.ofNullable(properties.getProperty(path))
                .map(value -> {
                    try {
                        return converter.apply(value, path);
                    } catch (RuntimeException e) {
                        throw new IllegalArgumentException("Failed to read configuration value " + path + ": " + e.getMessage(), e);
                    }
                });
    }

    public <T> T getValue(String key, BiFunction<? super String, ? super String, ? extends T> converter, T fallback) {
        return this.<T>tryGetValue(key, converter).orElse(fallback);
    }

    public <T> T getValue(String key, BiFunction<? super String, ? super String, ? extends T> converter) {
        return this.<T>tryGetValue(key, converter).orElseThrow(() -> missingKeyException(key));
    }

    public <T> Optional<T> tryGetValue(List<String> keys, BiFunction<? super String, ? super String, ? extends T> converter) {
        return keys.stream().map(key -> tryGetValue(key, converter))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(Function.identity());
    }

    public <T> T getValue(List<String> keys, BiFunction<? super String, ? super String, ? extends T> converter, T fallback) {
        return this.<T>tryGetValue(keys, converter).orElse(fallback);
    }

    public <T> T getValue(List<String> keys, BiFunction<? super String, ? super String, ? extends T> converter) {
        return this.<T>tryGetValue(keys, converter).orElseThrow(() -> missingKeysException(keys));
    }

    public <T> Optional<T> tryGetValue(String key, Function<String, ? extends T> converter) {
        return tryGetValue(key, (value, path) -> converter.apply(value));
    }

    public <T> T getValue(String key, Function<String, ? extends T> converter, T fallback) {
        return this.<T>tryGetValue(key, converter).orElse(fallback);
    }

    public <T> T getValue(String key, Function<String, ? extends T> converter) {
        return this.<T>tryGetValue(key, converter).orElseThrow(() -> missingKeyException(key));
    }

    public <T> Optional<T> tryGetValue(List<String> keys, Function<String, ? extends T> converter) {
        return tryGetValue(keys, (value, path) -> converter.apply(value));
    }

    public <T> T getValue(List<String> keys, Function<String, ? extends T> converter, T fallback) {
        return this.<T>tryGetValue(keys, converter).orElse(fallback);
    }

    public <T> T getValue(List<String> keys, Function<String, ? extends T> converter) {
        return tryGetValue(keys, converter).orElseThrow(() -> missingKeysException(keys));
    }

    public OptionalInt tryGetInt(String key) {
        return tryGetValue(key, value -> OptionalInt.of(Integer.parseInt(value)))
                .orElse(OptionalInt.empty());
    }

    public int getInt(String key, int fallback) {
        return tryGetInt(key).orElse(fallback);
    }

    public int getInt(String key) {
        return tryGetInt(key).orElseThrow(() -> missingKeyException(key));
    }

    public OptionalDouble tryGetDouble(String key) {
        return tryGetValue(key, value -> OptionalDouble.of(Double.parseDouble(value)))
                .orElse(OptionalDouble.empty());
    }

    public OptionalDouble tryGetDouble(List<String> keys) {
        return tryGetValue(keys, value -> OptionalDouble.of(Double.parseDouble(value)))
                .orElse(OptionalDouble.empty());
    }

    public OptionalDouble tryGetDouble(String... keys) {
        return tryGetDouble(Arrays.asList(keys));
    }

    public double getDouble(String key, double fallback) {
        return tryGetDouble(key).orElse(fallback);
    }

    public double getDouble(List<String> keys, double fallback) {
        for (String key : keys) {
            OptionalDouble candidate = tryGetDouble(key);

            if (candidate.isPresent()) {
                return candidate.getAsDouble();
            }
        }

        return fallback;
    }

    public double getDouble(String key) {
        return tryGetDouble(key).orElseThrow(() -> missingKeyException(key));
    }

    public double getDouble(List<String> keys) {
        for (String key : keys) {
            OptionalDouble candidate = tryGetDouble(key);

            if (candidate.isPresent()) {
                return candidate.getAsDouble();
            }
        }

        throw missingKeysException(keys);
    }

    public double getDouble(String... keys) {
        return getDouble(Arrays.asList(keys));
    }

    public Optional<Boolean> tryGetBoolean(String key) {
        return tryGetValue(key, Boolean::parseBoolean);
    }

    public boolean getBoolean(String key, boolean fallback) {
        return tryGetBoolean(key).orElse(fallback);
    }

    public boolean getBoolean(String key) {
        return tryGetBoolean(key).orElseThrow(() -> missingKeyException(key));
    }

    private static boolean isPrefixOf(String prefix, String key) {
        return key.length() > prefix.length() && key.startsWith(prefix) && key.charAt(prefix.length()) == '.';
    }

    private Stream<String> streamDescendantKeysOf(List<String> path) {
        String prefix = String.join(".", path);

        return properties.keySet().stream()
                .map(Object::toString)
                .filter(key -> isPrefixOf(prefix, key))
                .map(key -> prefix.length() == 0 ? key : key.substring(prefix.length() + 1)).distinct();
    }

    private Stream<String> streamChildKeysOf(List<String> prefix) {
        return streamDescendantKeysOf(prefix)
                .map(key -> {
                    int i = key.indexOf('.');
                    return i == -1 ? key : key.substring(0, i);
                })
                .distinct();
    }

    public Stream<String> streamChildKeys() {
        return streamChildKeysOf(anchor);
    }

    public Stream<String> streamDescendantKeys() {
        return streamDescendantKeysOf(anchor);
    }

    private Stream<ConfigurationContainer> streamChildrenOf(List<String> prefix) {
        return streamChildKeysOf(prefix)
                .map(key -> {
                    List<String> path = Stream.concat(prefix.stream(), Stream.of(key)).toList();
                    return new ConfigurationContainer(properties, path);
                });
    }

    private Stream<ConfigurationContainer> streamDescendantsOf(List<String> prefix) {
        return streamDescendantKeysOf(prefix).map(this::getChild);
    }

    public Stream<ConfigurationContainer> streamChildren(String... path) {
        return streamChildrenOf(Stream.concat(anchor.stream(), Arrays.stream(path)).toList());
    }

    public Map<String, ConfigurationContainer> getChildren(String... path) {
        return streamChildren(path).collect(Collectors.toMap(ConfigurationContainer::getName, Function.identity()));
    }

    public ConfigurationContainer getChild(String key) {
        return new ConfigurationContainer(properties, Stream.concat(anchor.stream(), Stream.of(key)).toList());
    }

    public <T> T scoped(String key, Function<ConfigurationContainer, T> factory) {
        return factory.apply(getChild(key));
    }
}
