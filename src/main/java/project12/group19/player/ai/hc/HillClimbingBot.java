package project12.group19.player.ai.hc;

import project12.group19.api.domain.*;
import project12.group19.api.game.BallStatus;
import project12.group19.api.geometry.plane.PlanarCoordinate;
import project12.group19.api.physics.motion.Friction;
import project12.group19.api.physics.motion.MotionHandler;
import project12.group19.api.physics.motion.MotionResult;
import project12.group19.api.physics.motion.MotionState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class HillClimbingBot implements Player {
    public static final double SIMULATION_TIME_LIMIT = 10.0;
    static MotionHandler motionHandler;
    static Surface surface;
    static Friction friction;
    static double holeX;
    static double holeY;
    static double holeR;
    static List<Shoot> alreadyShot = new ArrayList<>();
    static List<Combination> combinations = new ArrayList<>();
    static double maxVel = 5;
    static double maxAngle;
    static double minAngle;
    static double maxAngleVar = 30;  //Maximum variation of angle to generate shots. Angle 0 is Straight shot
    static int iterations = 0;
    static double stepSize = 0.01;
    static boolean combsPrinted = false;


    public HillClimbingBot(MotionHandler motionHandler, Course course) {
        HillClimbingBot.motionHandler = motionHandler;
        HillClimbingBot.surface = course.getSurface();
        HillClimbingBot.holeX = course.getTarget().getCenter().getX();
        HillClimbingBot.holeY = course.getTarget().getCenter().getY();
        HillClimbingBot.holeR = course.getTarget().getSmallerDimension();
    }

    @Override
    public Optional<Hit> play(State state) throws FileNotFoundException {
        return hillClimbing(state.getBallState().getXPosition(), state.getBallState().getYPosition());
    }

    public Optional<Hit> hillClimbing(double startingX, double startingY) throws FileNotFoundException {
        try {
            if (!Files.isDirectory(Path.of("reports"))) {
                Files.createDirectory(Path.of("reports"));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        PrintStream o = new PrintStream("reports/hill-climbing3.txt");
        PrintStream console = System.out;
        System.setOut(console);

        alreadyShot = new ArrayList<>();
        combinations = new ArrayList<>();
        iterations = 0;
        getAngles(startingX, startingY);
        double minAngle = HillClimbingBot.minAngle;
        double maxAngle = HillClimbingBot.maxAngle;
        double distanceToHole = getDistance(holeX, startingX, holeY, startingY);
        double straightX = (maxVel * (holeX-startingX))/distanceToHole;
        double straightY = (maxVel * (holeY-startingY))/distanceToHole;


        Shoot currentShot = new Shoot(straightX, straightY, startingX, startingY);

        while (!currentShot.inHole() && iterations < 300){
            if(iterations != 0){
                currentShot = createShot(minAngle, maxAngle, startingX, startingY);
            }
            Shoot optimized = optimiseShot(currentShot);
            while (optimized.getDistanceToHole() < currentShot.getDistanceToHole() && optimized.getVel() < maxVel) {
                currentShot = optimized;
                if (currentShot.inHole()) {
                    break;
                }

                optimized = optimiseShot(currentShot);

            }
            System.out.println("Iterations: "+iterations);
        }
        combinations.sort(Comparator.comparingDouble(Combination::getDistanceToHole));
        System.setOut(o);
        if(!combsPrinted){
            for(Combination combination : combinations){
                System.out.println(combination.getComb());
            }
            combsPrinted = true;
        }
        System.setOut(console);
        System.out.println("Iterations: "+iterations);

        List<HitSimulation> simulations = alreadyShot.stream()
                .<HitSimulation>map(shoot -> new HitSimulation.Standard(
                        shoot.getXDir(),
                        shoot.getYDir(),
                        PlanarCoordinate.create(startingX, startingY),
                        PlanarCoordinate.create(shoot.getClosestX(), shoot.getClosestY()),
                        shoot.getDistanceToHole()
                ))
                .toList();

        if(currentShot.inHole()){
            System.out.println("Distance to hole: "+currentShot.getDistanceToHole());
            return Optional.of(Hit.create(currentShot.getXDir(), currentShot.getYDir(), simulations));
        }else{
            alreadyShot.removeIf(shot -> shot.getVel() > maxVel);
            alreadyShot.sort(Comparator.comparingDouble(Shoot::getDistanceToHole));
            System.out.println("Best distance to hole: " + alreadyShot.get(0).getDistanceToHole());

            return Optional.of(Hit.create(alreadyShot.get(0).getXDir(), alreadyShot.get(0).getYDir(), simulations));
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
        HillClimbingBot.maxAngle = alpha+maxAngleVar;
        HillClimbingBot.minAngle = alpha-maxAngleVar;
    }

    private static void updateDistances(Shoot shoot, PlanarCoordinate position) {
        if (position.distanceTo(holeX, holeR) < shoot.getDistanceToHole()) {
            setClosestDistances(shoot, position);
        }
    }

    private static void updateDistance(PlanarCoordinate position, PlanarCoordinate target, Shoot shoot) {
        double distance = position.distanceTo(target);

        if (distance < shoot.getDistanceToHole()) {
            setClosestDistances(shoot, position);
        }
    }

    public static void getShotDistanceToHole(Shoot shoot) {
        double xDir = shoot.getXDir();
        double yDir = shoot.getYDir();
        double startingX = shoot.getStartingX();
        double startingY = shoot.getStartingY();
        MotionState initial = new MotionState.Standard(xDir, yDir, startingX, startingY);
        setClosestDistances(shoot, initial.getPosition());
        PlanarCoordinate target = PlanarCoordinate.create(holeX, holeY);

        MotionResult snapshot = MotionResult.create(initial, BallStatus.MOVING);

        for (int i = 0; i < SIMULATION_TIME_LIMIT / stepSize; i++) {
            if (snapshot.getStatus().isFoulTrigger()) {
                break;
            }

            updateDistance(snapshot.getState().getPosition(), target, shoot);

            if (snapshot.getStatus().equals(BallStatus.SCORED)) {
                shoot.setInHole();
                break;
            }

            if (snapshot.getStatus().equals(BallStatus.STOPPED)) {
                break;
            }

            snapshot = motionHandler.next(snapshot.getState(), stepSize);
        }
    }

    public static Shoot optimiseShot(Shoot shoot){
        double angleVar = 60;
        double stepSize = 0.5 * Math.sqrt(shoot.getDistanceToHole());
        double initialXDir = shoot.getXDir();
        double initialYDir = shoot.getYDir();
        double startingX = shoot.getStartingX();
        double startingY = shoot.getStartingY();
        List<Shoot> newShots = new ArrayList<Shoot>();
        for(int angle = 0; angle < 360; angle+= angleVar){
            Shoot movement = new Shoot(angle, stepSize, shoot.getStartingX(), shoot.getStartingY(), true);
            double moveX = movement.getXDir();
            double moveY = movement.getYDir();
            if(getDistance(moveX, 0, moveY, 0) < 5){
                newShots.add(new Shoot(initialXDir+moveX, initialYDir+moveY, startingX, startingY));
            }
        }
        newShots.sort(Comparator.comparingDouble(Shoot::getDistanceToHole));
        return newShots.get(0);
    }

    static void setClosestDistances(Shoot shoot, PlanarCoordinate position){
        shoot.setDistanceToHole(getDistance(holeX, position.getX(), holeY, position.getY()));
        shoot.setClosestX(position.getX());
        shoot.setClosestY(position.getY());
    }
}

