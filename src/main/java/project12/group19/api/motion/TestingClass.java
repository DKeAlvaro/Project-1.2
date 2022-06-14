package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.math.ode.ODESolver;
import project12.group19.math.ode.RK4;

import java.util.ArrayList;

public class TestingClass {
    Friction friction;
    MotionState motion;
    HeightProfile profile;
    Solver solver;
    double stepSize;
    public TestingClass(Friction friction, MotionState motion, HeightProfile profile, double stepSize, Solver solver){
        this.friction = friction;
        this.motion = motion;
        this.profile = profile;
        this.stepSize = stepSize;
        this.solver = solver;
        System.out.println("X position " + motion.getXPosition() + "Y position " + motion.getYPosition());
    }
    public MotionState getFinalPosition(){

        while(StopCondition.isMoving(profile, motion, friction, stepSize) && !(motion.getXSpeed() <=0)){
            motion = solver.calculate(motion, stepSize);

        }

        System.out.println("X position " + motion.getXPosition() + "   Y position " + motion.getYPosition());
        return motion;

    }

    public static void main(String[] args) {
        Friction f = Friction.create(0.2, 0.05);
        MotionState ms = new MotionState.Standard(5.0, 0.0, 0.0, 0.0  );
        ODESolver ode = new RK4();
        ArrayList<Double> resultsX = new ArrayList<Double>();
        ArrayList<Double> steps = new ArrayList<>();
        for (double i=0; i<=1.02; i+=0.05) {

            double a = i;
            System.out.println("Slope: " + a);
            steps.add(a);
            HeightProfile h = (x,y) ->{
                return  a * Math.cos(x)+1;
                //return Math.exp(-Math.pow(x,2)/a);
            } ;
            Solver s = new Solver(ode, h, f);
            TestingClass test = new TestingClass(f,ms,h,0.001, s);
            resultsX.add(test.getFinalPosition().getXPosition());

       }
        System.out.println(steps.toString());
        System.out.println(resultsX.toString());
    }

}
