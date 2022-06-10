package project12.group19.math.parser.component;

import project12.group19.math.parser.OperationDefinition;
import project12.group19.math.parser.BinaryOperatorDefinition;
import project12.group19.math.parser.UnaryOperatorDefinition;

import java.util.*;
import java.util.stream.Collectors;

public class ComponentRegistry {
    private final Map<String, Double> constants = new HashMap<>();
    private final Map<String, OperationDefinition> operations = new HashMap<>();
    private final Map<String, BinaryOperatorDefinition> binaryOperators = new HashMap<>();
    private final Map<String, UnaryOperatorDefinition> unaryOperators = new HashMap<>();

    public boolean hasConstant(String name) {
        return constants.get(name) != null;
    }

    public OptionalDouble tryGetConstant(String name) {
        Double subject = constants.get(name);
        return subject == null ? OptionalDouble.empty() : OptionalDouble.of(subject);
    }

    public double getConstant(String name) {
        return tryGetConstant(name).orElseThrow(() -> new IllegalArgumentException("Unknown constant " + name));
    }

    public UnaryOperatorDefinition getUnaryOperator(String name) {
        return unaryOperators.get(name);
    }

    public BinaryOperatorDefinition getBinaryOperator(String name) {
        return binaryOperators.get(name);
    }

    public OperationDefinition getOperation(String name) {
        return operations.get(name);
    }

    public List<String> getBinaryOperatorNames() {
        return binaryOperators.keySet().stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .collect(Collectors.toList());
    }
    public List<String> getUnaryOperatorNames() {
        return unaryOperators.keySet().stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .collect(Collectors.toList());
    }

    public ComponentRegistry withConstant(String constant, double value) {
        constants.put(constant, value);
        return this;
    }

    public ComponentRegistry withBinaryOperator(BinaryOperatorDefinition operator) {
        binaryOperators.put(operator.getName(), operator);
        operator.getAliases().forEach(alias -> binaryOperators.put(alias, operator));
        return this;
    }

    public ComponentRegistry withUnaryOperator(UnaryOperatorDefinition operator) {
        unaryOperators.put(operator.getName(), operator);
        operator.getAliases().forEach(alias -> unaryOperators.put(alias, operator));
        return this;
    }

    public ComponentRegistry withOperation(OperationDefinition operation) {
        operations.put(operation.getName(), operation);
        operation.getAliases().forEach(alias -> operations.put(alias, operation));
        return this;
    }

    public static ComponentRegistry standard() {
        return new ComponentRegistry()
                .withConstant("pi", Math.PI)
                .withConstant("e", Math.E)
                .withConstant("tau", Math.PI * 2)
                .withConstant("g", 9.80665)
                .withUnaryOperator(UnaryOperatorDefinition.Standard.PLUS)
                .withUnaryOperator(UnaryOperatorDefinition.Standard.MINUS)
                .withBinaryOperator(BinaryOperatorDefinition.Standard.ADD)
                .withBinaryOperator(BinaryOperatorDefinition.Standard.SUBTRACT)
                .withBinaryOperator(BinaryOperatorDefinition.Standard.MULTIPLY)
                .withBinaryOperator(BinaryOperatorDefinition.Standard.DIVIDE)
                .withBinaryOperator(BinaryOperatorDefinition.Standard.POWER)
                .withOperation(OperationDefinition.Standard.SINE)
                .withOperation(OperationDefinition.Standard.COSINE)
                .withOperation(OperationDefinition.Standard.MINIMUM)
                .withOperation(OperationDefinition.Standard.MAXIMUM)
                .withOperation(OperationDefinition.Standard.ABSOLUTE)
                .withOperation(OperationDefinition.Standard.LOGARITHM)
                .withOperation(OperationDefinition.Standard.NATURAL_LOGARITHM)
                .withOperation(OperationDefinition.Standard.BASE_TWO_LOGARITHM)
                .withOperation(OperationDefinition.Standard.BASE_TEN_LOGARITHM);
    }

}
