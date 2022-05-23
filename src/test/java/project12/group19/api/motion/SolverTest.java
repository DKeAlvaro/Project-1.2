package project12.group19.api.motion;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.math.ode.Euler;
import project12.group19.math.ode.ODESolver;
import project12.group19.math.ode.RK2;
import project12.group19.math.ode.RK4;
import project12.group19.math.parser.Parser;
import project12.group19.math.parser.component.ComponentRegistry;
import project12.group19.math.parser.expression.InfixExpression;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class SolverTest {
    public static Stream<Arguments> testInputs() {
        return Stream.of(
                // function, starting x, starting y, initial velocity x, initial velocity y, static friction, dynamic friction
                Arguments.of("e^(0 - ((x^2+y^2)/40)", -1.0, -0.5, 3.0, 0.0, 0.2, 0.1)
        );
    }

    @ParameterizedTest
    @Disabled
    @MethodSource("testInputs")
    public void supplyTestData(
            String function,
            double startingX,
            double startingY,
            double initialVelocityX,
            double initialVelocityY,
            double staticFriction,
            double dynamicFriction
    ) {
        Friction friction = new Friction.Standard(staticFriction, dynamicFriction);
        int maxStepPower = 6;
        List<ODESolver> solvers = List.of(new Euler(), new RK2(), new RK4());
        Parser parser = new Parser(ComponentRegistry.standard());
        InfixExpression expression = parser.parse(function);
        HeightProfile profile = (x, y) -> {
            Map<String, Double> var = Map.of("e", Math.E, "pi", Math.PI, "x", x, "y", y);
            return expression.resolve(var).calculate().getAsDouble();
        };
        StopCondition condition = new StopCondition();
        System.out.println("Solver, step size, terminal x, terminal y");
        try {
            File file = new File("testing2.txt");
            FileWriter writer = new FileWriter(file);
            BufferedWriter bWriter = new BufferedWriter(writer);
            PrintWriter pWriter = new PrintWriter(bWriter);
            for (ODESolver delegate : solvers) {
                Solver solver = new Solver(delegate, profile, friction);
                System.out.println("xxx");

                for (int i = 1; i <= maxStepPower; i++) {
                    double step = Math.pow(0.1, i);
                    MotionState state = new MotionState.Standard(initialVelocityX, initialVelocityY, startingX, startingY);
                    long startTime = System.nanoTime();
                    while (condition.isMoving(profile, state, friction, step)) {
                        state = solver.calculate(state, step);
                    }
                    long stopTime = System.nanoTime();
                    long timeElapsed = stopTime - startTime;
                    String solverName = delegate.getClass().getSimpleName();
                    pWriter.println(solverName + ",10E-" + i + "," + state.getXPosition() + "," + state.getYPosition()+ ","+ timeElapsed);


                    pWriter.flush();
                    bWriter.flush();
                    writer.flush();
                }
            }

            pWriter.close();
            bWriter.close();
            writer.close();
        } catch (Exception e) {
            System.out.println("Exception");
        }
    }
}
