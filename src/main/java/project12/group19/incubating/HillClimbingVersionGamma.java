package project12.group19.incubating;

import project12.group19.api.domain.Hit;
import project12.group19.api.domain.Player;
import project12.group19.api.domain.State;
import project12.group19.api.game.BallStatus;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.*;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;


public class HillClimbingVersionGamma implements Player {
    // Maximum number of seconds to simulate
    private static final double TIME_LIMIT = 10.0;
    private static final double DEFAULT_STEP_SIZE = 0.05;
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
    static double stepSize = 0.05;
    static boolean combsPrinted = false;

    private static MotionHandler handler;

    public HillClimbingVersionGamma(MotionHandler handler, Configuration configuration, double stepSize) {
        HillClimbingVersionGamma.handler = handler;
        HillClimbingVersionGamma.profile = configuration.getHeightProfile();
        HillClimbingVersionGamma.friction = configuration.getGroundFriction();
        HillClimbingVersionGamma.holeX = configuration.getHole().getxHole();
        HillClimbingVersionGamma.holeY = configuration.getHole().getyHole();
        HillClimbingVersionGamma.holeR = configuration.getHole().getRadius();
        HillClimbingVersionGamma.configuration = configuration;
        HillClimbingVersionGamma.stepSize = stepSize;
    }

    public HillClimbingVersionGamma(MotionHandler handler, Configuration configuration) {
        this(handler, configuration, DEFAULT_STEP_SIZE);
    }

    @Override
    public Optional<Hit> play(State state) throws FileNotFoundException {
        return hillClimbing(state.getBallState().getXPosition(), state.getBallState().getYPosition());
    }

    public Optional<Hit> hillClimbing(double startingX, double startingY) throws FileNotFoundException {
        PrintStream o = new PrintStream("hillClimbing2.txt");
        PrintStream console = System.out;
        System.setOut(console);

        getAngles(startingX, startingY);
        double minAngle = HillClimbingVersionGamma.minAngle;
        double maxAngle = HillClimbingVersionGamma.maxAngle;
        alreadyShot = new ArrayList<>();
        combs = new ArrayList<>();
        iterations = 0;

        Shoot currentShot = createShot(minAngle, maxAngle, startingX, startingY);

        for (int i = 0; i < 200; i ++) {
            if (currentShot.inHole()) {
                break;
            }

            currentShot = createShot(minAngle, maxAngle, startingX, startingY);
            while (!currentShot.hasConverged() && !currentShot.inHole()) {
                currentShot = optimiseShot(currentShot);
                System.out.println("Distance to hole: " + currentShot.getDistanceToHole());
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
        if(currentShot.inHole()){
            System.out.println("Distance to hole: "+currentShot.getDistanceToHole());
            return Optional.of(Hit.create(currentShot.getxDir(), currentShot.getYDir()));
        }else{
            alreadyShot.sort(Comparator.comparingDouble(Shoot::getDistanceToHole));
            System.out.println("Best distance to hole: " + alreadyShot.get(0).getDistanceToHole());
            return Optional.of(Hit.create(alreadyShot.get(0).getxDir(), alreadyShot.get(0).getYDir()));
        }

    }

    public static Shoot createShot(double minAngle, double maxAngle, double startingX, double startingY){
        double angle = random(minAngle, maxAngle);
        System.out.println("Angle: "+angle);
        double vel = Math.sqrt(Math.random()) * maxVel; //TODO: Explain why there is a sqrt()
        double xDir = vel * Math.cos(Math.toRadians(angle));
        double yDir = vel * Math.sin(Math.toRadians(angle));
        System.out.println("x Dir: "+xDir+ " y Dir: "+yDir);
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
        HillClimbingVersionGamma.maxAngle = alpha+maxAngleVar;
        HillClimbingVersionGamma.minAngle = alpha-maxAngleVar;
    }

    private static void updateDistance(PlanarCoordinate position, PlanarCoordinate target, Shoot shoot) {
        double distance = position.distanceTo(target);

        if (distance < shoot.getDistanceToHole()) {
            setClosestDistances(shoot, position);
        }
    }

    public static void calculateShotDistanceToHole(Shoot shoot) {
        double xDir = shoot.getxDir();
        double yDir = shoot.getYDir();
        double startingX = shoot.getStartingX();
        double startingY = shoot.getStartingY();
        MotionState current = new MotionState.Standard(xDir, yDir, startingX, startingY);
        setClosestDistances(shoot, current);
        PlanarCoordinate target = PlanarCoordinate.create(holeX, holeY);

        MotionResult cursor = MotionResult.create(current, BallStatus.MOVING);

        for (long i = 0; i < TIME_LIMIT / stepSize; i++) {
            if (cursor.getStatus().isFoulTrigger()) {
                break;
            }

            updateDistance(cursor.getState().getPosition(), target, shoot);

            if (cursor.getStatus().equals(BallStatus.SCORED)) {
                shoot.setInHole();
                break;
            }

            if (cursor.getStatus().equals(BallStatus.STOPPED)) {
                break;
            }

            cursor = handler.next(cursor.getState(), stepSize);
        }
    }

    public static double getShotDistanceToHole(Shoot shoot) {
        double xDir = shoot.getxDir();
        double yDir = shoot.getYDir();
        double startingX = shoot.getStartingX();
        double startingY = shoot.getStartingY();
        MotionState initial = new MotionState.Standard(xDir, yDir, startingX, startingY);
        PlanarCoordinate target = PlanarCoordinate.create(holeX, holeY);

        MotionResult cursor = MotionResult.create(initial, BallStatus.MOVING);

        double distance = target.distanceTo(initial.getPosition());

        for (long i = 0; i < TIME_LIMIT / stepSize; i++) {
            if (cursor.getStatus().isFoulTrigger()) {
                break;
            }

            distance = Math.min(distance, target.distanceTo(cursor.getState().getPosition()));

            if (cursor.getStatus().equals(BallStatus.SCORED)) {
                shoot.setInHole();
                break;
            }

            if (cursor.getStatus().equals(BallStatus.STOPPED)) {
                break;
            }

            cursor = handler.next(cursor.getState(), stepSize);
        }

        return distance;
    }

    public static Shoot optimiseShot(Shoot shoot){
        double deltaT = 0.1 * Math.sqrt(shoot.getDistanceToHole());
        double deltaTSquareRoot = Math.sqrt(deltaT);

        List<Shoot> candidates = List.of(
                new Shoot(shoot.getxDir(), shoot.getYDir() + deltaT, shoot.getStartingX(), shoot.getStartingY()),
                new Shoot(shoot.getxDir() + deltaTSquareRoot, shoot.getYDir() + deltaTSquareRoot, shoot.getStartingX(), shoot.getStartingY()),
                new Shoot(shoot.getxDir() + deltaT, shoot.getYDir(), shoot.getStartingX(), shoot.getStartingY()),
                new Shoot(shoot.getxDir() + deltaTSquareRoot, shoot.getYDir() - deltaTSquareRoot, shoot.getStartingX(), shoot.getStartingY()),
                new Shoot(shoot.getxDir(), shoot.getYDir() - deltaT, shoot.getStartingX(), shoot.getStartingY()),
                new Shoot(shoot.getxDir() - deltaTSquareRoot, shoot.getYDir() - deltaTSquareRoot, shoot.getStartingX(), shoot.getStartingY()),
                new Shoot(shoot.getxDir() - deltaT, shoot.getYDir(), shoot.getStartingX(), shoot.getStartingY()),
                new Shoot(shoot.getxDir() - deltaTSquareRoot, shoot.getYDir() + deltaTSquareRoot, shoot.getStartingX(), shoot.getStartingY())
        );

        for (Shoot candidate : candidates) {
            calculateShotDistanceToHole(candidate);
        }

        return candidates.stream()
                .min(Comparator.comparingDouble(Shoot::getDistanceToHole))
                .orElseThrow(() -> new RuntimeException("Impossibru"));
    }

    static void setClosestDistances(Shoot shoot, PlanarCoordinate current){
        shoot.setDistanceToHole(getDistance(holeX, current.getX(), holeY, current.getY()));
        shoot.setClosestX(current.getX());
        shoot.setClosestY(current.getY());
    }

    @Deprecated
    static void setClosestDistances(Shoot shoot, MotionState current){
        setClosestDistances(shoot, current.getPosition());
    }
}

