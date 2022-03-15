

public class solverAttempt1 {
    private double x; //current x
     private double y; //curent y
     private double xv; //velocity x 
     private double yv; // velocity y
     private double h;

     private double g; //gravity
     private double uk; //kinetic friction
     private double stepSize;
     private double accX; // acceleration x
     private double accY; //acceleration y
     private double [] outputArray;// very small number for derivation
     

     
public solverAttempt1( double kineticFriction, double stepSize, double initialX, double initialY,  double initialVX, double inititialVY){
    this.uk = kineticFriction;
    this.stepSize = stepSize;
    this.x= initialX;
    this.y= initialY;
    
    this.xv= initialVX;
    this.yv = inititialVY;
    this.accX = 0; 
    this.accY = 0;
    double [] outputArray  = {x,y,xv,yv};
    this.h = 0.00000001; 

}

    


    public static void main(String[]args){
        solverAttempt1 solver = new solverAttempt1(0.06, 0.1, 0, 0, 1, 0);
        
        System.out.println(solver.derivativeHX(1000, 1));

    }

    public  double [] newState(double x, double y, double xv, double yv, double deltaH){
        
        x = x+ stepSize* xv;
        y = y + stepSize * yv;
        xv= xv + stepSize * accX; //to do: initial acceleration
        yv= yv + stepSize * accY;
        this.x=x;
        this.y = y;
        this.xv = xv;
        this.yv = yv;
        accX = accelerationX (x);
        accY = accelerationY (y);
        outputArray[0] = x;
        outputArray[1]=y;
        outputArray [2] = xv;
        outputArray [3]= yv;
        return outputArray;



    }

    public  double accelerationX ( double x, double y){
        double accX = (-1)*g* (derivativeHX(x,y)) - (uk*g*xv)/Math.sqrt(xv*xv+yv*yv);
        return accX;
    }

    public  double accelerationY (double x,double y){
        double accY = (-1)*g*(derivativeHY(x,y)) - (uk*g*yv)/Math.sqrt(xv*xv+yv*yv);
        return accY;
    }

    public double derivativeHX(double x, double y){
        
        return (heightFunction(x+h, y) - heightFunction(x, y))/h;

    }
    public double derivativeHY(double x, double y){
        return (heightFunction(x, y+h) - heightFunction(x,y))/h;
        
    }
    public double heightFunction (double x, double y){
        // TO DO: get the function from the input file
        return Math.pow(x, 3);

    }
    






}
