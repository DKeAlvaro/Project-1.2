package project12.group19.infrastructure.configuration;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class ConfigurationContainerTest {
    @Test
    public void correctlyReportsRootContains() {
        Properties wrapped = new Properties();
        wrapped.setProperty("alpha.beta.gamma", "12");
        ConfigurationContainer sut = new ConfigurationContainer(wrapped);
        assertThat(sut.contains("alpha.beta.gamma"), is(true));
        assertThat(sut.contains("delta"), is(false));
    }

    @Test
    public void correctlyReportsAnchoredContains() {
        Properties wrapped = new Properties();
        wrapped.setProperty("alpha.beta.gamma", "12");
        for (List<String> anchor : List.of(List.of("alpha.beta"), List.of("alpha", "beta"))) {
            ConfigurationContainer sut = new ConfigurationContainer(wrapped, anchor);
            assertThat(sut.contains("gamma"), is(true));
            assertThat(sut.contains("delta"), is(false));
        }
    }

    @Test
    public void correctlyResolvesRootLevelDefinitions() {
        Properties wrapped = new Properties();
        wrapped.setProperty("alpha.beta", "12");
        assertThat(new ConfigurationContainer(wrapped).getInt("alpha.beta"), equalTo(12));
    }

    @Test
    public void correctlyResolvesAnchoredDefinitions() {
        Properties wrapped = new Properties();
        wrapped.setProperty("alpha.beta.gamma", "12");
        wrapped.setProperty("alpha.delta.epsilon", "13");
        for (List<String> anchor : List.of(List.of("alpha.beta"), List.of("alpha", "beta"))) {
            ConfigurationContainer sut = new ConfigurationContainer(wrapped, anchor);
            assertThat(sut.getInt("gamma"), equalTo(12));
            assertThat(sut.contains("epsilon"), is(false));
            assertThat(sut.contains("alpha.delta.epsilon"), is(false));
        }
    }

    @Test
    public void correctlyAccessesChildren() {
        Map<String, Integer> foundation = Map.of(
                "alpha", 12,
                "beta", 13,
                "gamma", 14
        );
        Properties wrapped = new Properties();
        foundation.forEach((key, value) -> wrapped.setProperty("anchored.children." + key + ".value", value.toString()));
        ConfigurationContainer sut = new ConfigurationContainer(wrapped, "anchored");
        Map<String, Integer> result = sut.streamChildren("children")
                .collect(Collectors.toMap(
                        ConfigurationContainer::getName,
                        container -> container.getInt("value"))
                );

        assertThat(result, equalTo(foundation));
    }
}
