package project12.group19.api.motion;

import project12.group19.api.geometry.space.HeightProfile;

public interface AccelerationCalc {


    static double accelerationX(HeightProfile profile, MotionState motion, Friction f, double scale){ return 0;}

     static double accelerationY(HeightProfile profile, MotionState motion, Friction f, double scale){ return 0;}

}
