package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.math.ode.ODESolver;
import project12.group19.math.ode.RK4;

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

        while(StopCondition.isMoving(profile, motion, friction, stepSize)){

            motion = solver.calculate(motion, stepSize);

        }
        System.out.println("X position " + motion.getXPosition() + " Y position " + motion.getYPosition());
        return motion;

    }

    public static void main(String[] args) {
        Friction f = Friction.create(0.2, 0.05);
        MotionState ms = new MotionState.Standard(2.0, 0.0, 0.0, 0.0  );
        HeightProfile h = (x,y) ->{
            return  0.1 *x +1;
        } ;
        ODESolver ode = new RK4();
        Solver s = new Solver(ode, h, f);

        TestingClass test = new TestingClass(f,ms,h,0.000001, s);
        test.getFinalPosition();
    }

}
