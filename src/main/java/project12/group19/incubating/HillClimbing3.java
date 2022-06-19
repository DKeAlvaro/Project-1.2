package project12.group19.incubating;

import project12.group19.api.domain.Player;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.MotionState;
import project12.group19.api.motion.Solver;
import project12.group19.api.motion.StopCondition;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;


public class HillClimbing3 {
    static Solver solver;
    static HeightProfile profile;
    static Friction friction;
    static double holeX;
    static double holeY;
    static double holeR;
    static Configuration configuration;
    static List<Shoot> alreadyShot = new ArrayList<>();
    static List<Comb> combs = new ArrayList<>();
    static double maxVel = 5;
    static double maxAngle;
    static double minAngle; //
    static double maxAngleVar = 30;  //Maximum variation of angle to generate shots. Angle 0 is Straight shot
    static int iterations = 0;
    static double stepSize = 0.01;
    static boolean combsPrinted = false;


    public HillClimbing3(Solver solver, Configuration configuration) {
        HillClimbing3.solver = solver;
        HillClimbing3.profile = configuration.getHeightProfile();
        HillClimbing3.friction = configuration.getGroundFriction();
        HillClimbing3.holeX = configuration.getHole().getxHole();
        HillClimbing3.holeY = configuration.getHole().getyHole();
        HillClimbing3.holeR = configuration.getHole().getRadius();
        HillClimbing3.configuration = configuration;
    }

    public Optional<Player.Hit> hillClimbing(double startingX, double startingY) throws FileNotFoundException {
        PrintStream o = new PrintStream("newHillClimbing.txt");
        PrintStream console = System.out;
        System.setOut(console);

        alreadyShot = new ArrayList<>();
        combs = new ArrayList<>();
        iterations = 0;
        getAngles(startingX, startingY);
        double minAngle = HillClimbing3.minAngle;
        double maxAngle = HillClimbing3.maxAngle;
        double distanceToHole = getDistance(holeX, startingX, holeY, startingY);
        double straightX = (maxVel * (holeX-startingX))/distanceToHole;
        double straightY = (maxVel * (holeY-startingY))/distanceToHole;


        Shoot currentShot = new Shoot(straightX, straightY, startingX, startingY);

        while (!currentShot.inHole() && iterations < 500){
            if(iterations != 0){
                currentShot = createShot(minAngle, maxAngle, startingX, startingY);
            }
            while (!currentShot.hasConverged()) {
                currentShot = optimiseShot(currentShot);
            }
            System.out.println("Iterations: "+iterations);
        }
        combs.sort(Comparator.comparingDouble(Comb::getDistanceToHole));
        System.setOut(o);
        if(!combsPrinted){
            for(Comb comb : combs){
                System.out.println(comb.getComb());
            }
            combsPrinted = true;
        }
        System.setOut(console);
        System.out.println("Iterations: "+iterations);

        if(currentShot.inHole()){
            System.out.println("Distance to hole: "+currentShot.getDistanceToHole());
            return Optional.of(Player.Hit.create(currentShot.getxDir(), currentShot.getYDir()));
        }else{
            alreadyShot.sort(Comparator.comparingDouble(Shoot::getDistanceToHole));
            System.out.println("Best distance to hole: " + alreadyShot.get(0).getDistanceToHole());
            return Optional.of(Player.Hit.create(alreadyShot.get(0).getxDir(), alreadyShot.get(0).getYDir()));
        }

    }

    public static Shoot createShot(double minAngle, double maxAngle, double startingX, double startingY){
        double angle = random(minAngle, maxAngle);
        double vel = Math.sqrt(Math.random()) * maxVel; //TODO: Explain why there is a sqrt()
        double xDir = vel * Math.cos(Math.toRadians(angle));
        double yDir = vel * Math.sin(Math.toRadians(angle));
        return new Shoot(xDir, yDir, startingX, startingY);
    }

    public static double getDistance(double finalX, double startingX, double finalY, double startingY) {
        return Math.sqrt(Math.pow((finalX - startingX), 2) + Math.pow((finalY - startingY), 2));
    }

    public static double random(double min, double max){
        Random r = new Random();
        return r.nextDouble(max -min) + min;
    }

    public static void getAngles(double startingX, double startingY){
        double distanceToHole = getDistance(holeX, startingX, holeY, startingY);
        double straightX = (maxVel * (holeX-startingX))/distanceToHole;
        double straightY = (maxVel * (holeY-startingY))/distanceToHole;    //Straight shot to hole with maximum velocity
        double alpha = Math.toDegrees(Math.atan2(straightY, straightX));
        HillClimbing3.maxAngle = alpha+maxAngleVar;
        HillClimbing3.minAngle = alpha-maxAngleVar;
    }

    public static void getShotDistanceToHole(Friction friction, HeightProfile profile, Shoot shoot) {
        double xDir = shoot.getxDir();
        double yDir = shoot.getYDir();
        double startingX = shoot.getStartingX();
        double startingY = shoot.getStartingY();
        MotionState current = new MotionState.Standard(xDir, yDir, startingX, startingY);
        setClosestDistances(shoot, current);
        double minDistance = getDistance(holeX, current.getXPosition(), holeY, current.getYPosition());

        while (StopCondition.isMoving(profile, current, friction, stepSize) && !shoot.inHole()) {
            current = solver.calculate(current, stepSize);
            if(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()) < minDistance){
                minDistance = getDistance(holeX, current.getXPosition(), holeY, current.getYPosition());
                setClosestDistances(shoot, current);
                if(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()) < holeR*0.9 && !shoot.inWater()){
                    shoot.setInHole();
                    setClosestDistances(shoot, current);
                }
            }
            if(profile.getHeight(current.getXPosition(), current.getYPosition()) < 0 && !shoot.inHole()) {
                shoot.setInWater();
                break;
            }
        }
    }

    public static Shoot optimiseShot(Shoot shoot){
        double angleVar = 60;
        double stepSize = 0.1*Math.sqrt(shoot.getDistanceToHole());
        double initialXDir = shoot.getxDir();
        double initialYDir = shoot.getYDir();
        double startingX = shoot.getStartingX();
        double startingY = shoot.getStartingY();
        List<Shoot> newShots = new ArrayList<Shoot>();
        for(int angle = 0; angle < 360; angle+= angleVar){
            Shoot movement = new Shoot(angle, stepSize, shoot.getStartingX(), shoot.getStartingY(), true);
            double moveX = movement.getxDir();
            double moveY = movement.getYDir();
            newShots.add(new Shoot(initialXDir+moveX, initialYDir+moveY, startingX, startingY));
        }
        newShots.sort(Comparator.comparingDouble(Shoot::getDistanceToHole));
        return newShots.get(0);

    }

    static void setClosestDistances(Shoot shoot, MotionState current){
        shoot.setDistanceToHole(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()));
        shoot.setClosestX(current.getXPosition());
        shoot.setClosestY(current.getYPosition());
    }
}

