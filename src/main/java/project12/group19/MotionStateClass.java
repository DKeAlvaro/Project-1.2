package project12.group19;

import project12.group19.api.motion.MotionState;

public class MotionStateClass implements MotionState {
    double xSpeed;
    double ySpeed;
    double xPosition;
    double yPosition;
    


    

    public MotionStateClass(double xSpeed, double ySpeed, double xPosition, double yPosition){
        this.xSpeed =xSpeed;
        this.ySpeed = ySpeed;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
    public double getXSpeed(){
        return xSpeed;
    }
    public double getYSpeed(){
        return ySpeed;
    }
    public double getXPosition(){
        return xPosition;
    }
    public double getYPosition(){
        return yPosition;
    }

}
