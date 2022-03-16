package project12.group19.math.parser.component;

import project12.group19.math.parser.OperationDefinition;
import project12.group19.math.parser.OperatorDefinition;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentRegistry {
    private final Map<String, OperationDefinition> operations = new HashMap<>();
    private final Map<String, OperatorDefinition> operators = new HashMap<>();

    public OperatorDefinition getOperator(String name) {
        return operators.get(name);
    }

    public OperationDefinition getOperation(String name) {
        return operations.get(name);
    }

    public List<String> getOperatorNames() {
        return operators.keySet().stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .collect(Collectors.toList());
    }

    public ComponentRegistry withOperator(OperatorDefinition operator) {
        operators.put(operator.getName(), operator);
        operator.getAliases().forEach(alias -> operators.put(alias, operator));
        return this;
    }

    public ComponentRegistry withOperation(OperationDefinition operation) {
        operations.put(operation.getName(), operation);
        operation.getAliases().forEach(alias -> operations.put(alias, operation));
        return this;
    }

    public static ComponentRegistry standard() {
        return new ComponentRegistry()
                .withOperator(OperatorDefinition.Standard.ADD)
                .withOperator(OperatorDefinition.Standard.SUBTRACT)
                .withOperator(OperatorDefinition.Standard.MULTIPLY)
                .withOperator(OperatorDefinition.Standard.DIVIDE)
                .withOperator(OperatorDefinition.Standard.POWER)
                .withOperation(OperationDefinition.Standard.SINE)
                .withOperation(OperationDefinition.Standard.COSINE)
                .withOperation(OperationDefinition.Standard.MINIMUM)
                .withOperation(OperationDefinition.Standard.MAXIMUM)
                .withOperation(OperationDefinition.Standard.ABSOLUTE)
                .withOperation(OperationDefinition.Standard.LOGARITHM);
    }
}
