package project12.group19.api.math.expression;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

public interface Expression {
    OptionalDouble evaluate(Context context);

    record Constant(double value) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            return OptionalDouble.of(value);
        }
    }

    record Variable(String name) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            Expression expression = context.getVariables().get(name);
            if (expression == null) {
                return OptionalDouble.empty();
            }
            return expression.evaluate(context);
        }
    }

    record Subtraction(Expression minuend, Expression subtrahend) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            OptionalDouble a = minuend.evaluate(context);
            OptionalDouble b = subtrahend.evaluate(context);

            return a.isPresent() && b.isPresent() ? OptionalDouble.of(a.getAsDouble() - b.getAsDouble()) : OptionalDouble.empty();
        }
    }

    record Sum(List<Expression> components) implements Expression {
        public Sum(Expression... components) {
            this(Arrays.asList(components));
        }

        public Sum(Expression left, Expression right) {
            this(Arrays.asList(left, right));
        }

        @Override
        public OptionalDouble evaluate(Context context) {
            double sum = 0;
            for (Expression component : components) {
                OptionalDouble evaluation = component.evaluate(context);

                if (evaluation.isEmpty()) {
                    return OptionalDouble.empty();
                }

                sum += evaluation.getAsDouble();
            }

            return OptionalDouble.of(sum);
        }
    }

    record Multiplication(List<Expression> components) implements Expression {
        public Multiplication(Expression... components) {
            this(Arrays.asList(components));
        }

        public Multiplication(Expression left, Expression right) {
            this(Arrays.asList(left, right));
        }

        @Override
        public OptionalDouble evaluate(Context context) {
            double accumulator = 1;

            for (Expression component : components) {
                OptionalDouble evaluation = component.evaluate(context);

                if (evaluation.isEmpty()) {
                    return OptionalDouble.empty();
                }

                accumulator *= evaluation.getAsDouble();
            }

            return OptionalDouble.of(accumulator);
        }
    }

    record Division(Expression dividend, Expression divisor) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            OptionalDouble a = dividend.evaluate(context);
            OptionalDouble b = dividend.evaluate(context);

            if (a.isEmpty() || b.isEmpty() || b.getAsDouble() == 0) {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(a.getAsDouble() / b.getAsDouble());
        }
    }

    record Sine(Expression argument) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            OptionalDouble evaluation = argument.evaluate(context);

            if (evaluation.isEmpty()) {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(Math.sin(evaluation.getAsDouble()));
        }
    }

    record Cosine(Expression argument) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            OptionalDouble evaluation = argument.evaluate(context);

            if (evaluation.isEmpty()) {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(Math.cos(evaluation.getAsDouble()));
        }
    }

    record Absolute(Expression argument) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            OptionalDouble evaluation = argument.evaluate(context);

            if (evaluation.isEmpty()) {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(Math.abs(evaluation.getAsDouble()));
        }
    }

    record Exponent(Expression base, Expression power) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            OptionalDouble b = base.evaluate(context);
            OptionalDouble p = power.evaluate(context);

            if (b.isEmpty() || p.isEmpty()) {
                return OptionalDouble.empty();
            }

            return OptionalDouble.of(Math.pow(b.getAsDouble(), p.getAsDouble()));
        }
    }

    record Logarithm(Expression base, Expression value) implements Expression {
        @Override
        public OptionalDouble evaluate(Context context) {
            OptionalDouble b = base.evaluate(context);
            OptionalDouble v = value.evaluate(context);

            if (b.isEmpty() || v.isEmpty()) {
                return OptionalDouble.empty();
            }

            // just changing the base, nothing to see here
            return OptionalDouble.of(Math.log(b.getAsDouble()) / Math.log(v.getAsDouble()));
        }
    }
}
