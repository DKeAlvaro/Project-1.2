package project12.group19.infrastructure.cli;

import org.junit.jupiter.api.Test;
import project12.group19.api.infrastructure.cli.Argument;
import project12.group19.api.infrastructure.cli.Command;
import project12.group19.api.infrastructure.cli.Invocation;
import project12.group19.api.infrastructure.cli.Option;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
class CliParserTest {
    private static final Command CONFIGURATION = new Command(
            List.of(Argument.optional("name")),
            Set.of(new Option("verbose", "Verbosity control")),
            mock(ToIntFunction.class),
            Map.of(
                    "help", new Command(List.of(), Set.of(new Option("verbose", "Verbosity control")), mock(ToIntFunction.class), Map.of(), null),
                    "experiment", new Command(List.of(), Set.of(), mock(ToIntFunction.class), Map.of(
                            "ode", new Command(
                                    List.of(
                                            Argument.regular("name"),
                                            Argument.regular("formula"),
                                            new Argument("values", new Argument.Arity(2, Integer.MAX_VALUE))
                                    ),
                                    Set.of(
                                            new Option("bootstrapper", Set.of("b"), "Bootstrap method"),
                                            new Option("sampling-interval", Set.of("s"), "How often to print the results"),
                                            new Option("format", Set.of("f"), "In which format print the results")
                                    ),
                                    mock(ToIntFunction.class),
                                    Map.of(),
                                    null
                            ),
                            "stopping", new Command(
                                    List.of(
                                            Argument.optional("x"),
                                            Argument.variableLength("values")
                                    ),
                                    Set.of(new Option("step", "Step size")),
                                    mock(ToIntFunction.class),
                                    Map.of(),
                                    null
                            )
                    ), null)
            ),
            null
    );

    private static final CliParser SUT = new CliParser(CONFIGURATION);

    @Test
    public void basic() {
        Invocation invocation = SUT.parse(List.of());
        assertThat(invocation.getInvokedCommandHierarchy(), equalTo(List.of(CONFIGURATION)));
        assertThat(invocation.getInvokedCommand(), equalTo(CONFIGURATION));
        assertThat(invocation.getArguments().size(), equalTo(0));
        assertThat(invocation.getOptions().size(), equalTo(0));
    }

    @Test
    public void basicOption() {
        Invocation invocation = SUT.parse(List.of("--verbose", "true"));
        assertThat(invocation.getOptionValue("--verbose"), equalTo("true"));
        assertThat(invocation.getOptionValue("--verbose", Boolean::parseBoolean), equalTo(true));
    }

    @Test
    public void basicArgument() {
        Invocation invocation = SUT.parse(List.of("John"));
        assertThat(invocation.getArgumentValue("name"), equalTo("John"));
    }

    @Test
    public void nested() {
        Invocation invocation = SUT.parse(List.of("experiment", "-b", "rk4", "ode", "ab3", "x + y", "1", "2", "3", "4", "-s", "12"));
        List<Command> expectation = List.of(
                CONFIGURATION,
                CONFIGURATION.getChild("experiment"),
                CONFIGURATION.getChild("experiment").getChild("ode")
        );
        assertThat(invocation.getInvokedCommandHierarchy(), equalTo(expectation));
        assertThat(invocation.getOptionValue("--bootstrapper"), equalTo("rk4"));
        assertThat(invocation.getOptionValue("--sampling-interval"), equalTo("12"));
        assertThat(invocation.getArgumentValues("values", Integer::parseInt), equalTo(List.of(1, 2, 3, 4)));
        assertThat(invocation.getArgumentValue("formula"), equalTo("x + y"));
        assertThat(invocation.getArgumentValue("name"), equalTo("ab3"));
    }

    @Test
    public void throwsOnExtraArgument() {
        assertThrows(IllegalArgumentException.class, () -> SUT.parse("value", "value"));
    }

    @Test
    public void throwsOnNotEnoughArguments() {
        assertThrows(IllegalArgumentException.class, () -> SUT.parse("experiment", "ode"));
        assertThrows(IllegalArgumentException.class, () -> SUT.parse("experiment", "-b", "rk4", "ode", "ab3", "x + y", "1"));
    }
}
