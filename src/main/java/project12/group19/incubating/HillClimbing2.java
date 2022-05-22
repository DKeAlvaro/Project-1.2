package project12.group19.incubating;

import org.jetbrains.annotations.NotNull;
import project12.group19.api.domain.Player;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class HillClimbing2 {

    public static Solver solver;
    public static Configuration configuration;

    public static double holeX;
    public static double holeY;
    public static double holeR;

    public static int ruleBasedIterations = 0;

    public static HeightProfile profile;
    public static Friction friction;

    public static int numOfShots = 300;
    public static Shoot[] shots = new Shoot[numOfShots];

    public static double stepSize = 0.1;

    public HillClimbing2(Solver solver, Configuration configuration){
        HillClimbing2.solver = solver;
        HillClimbing2.profile = configuration.getHeightProfile();
        HillClimbing2.friction = configuration.getGroundFriction();
        HillClimbing2.holeX = configuration.getHole().getxHole();
        HillClimbing2.holeY = configuration.getHole().getyHole();
        HillClimbing2.holeR = configuration.getHole().getRadius();
        HillClimbing2.configuration = configuration;
    }

    public Optional<Player.Hit> StraightShot(double startingX, double startingY) {
        double f = 0.5;
        ruleBasedIterations++;
        if(new Shoot((holeX-startingX) * f, (holeX-startingX) * f, startingX, startingY).inHole()){
            System.out.println("Iterations needed "+ ruleBasedIterations);
        }
        return Optional.of(Player.Hit.create((holeX-startingX) * f, (holeX-startingX) * f));
    }

    public Optional<Player.Hit> hillClimbing1(double startingX, double startingY) {
        System.out.println("Calculating new shoot");
        int totalIterations = 0;
        double f = 0.5;
        int shotIterations;
        double noiseX;
        double noiseY;

        shots[0] = new Shoot((holeX-startingX) * f, (holeY-startingY) * f, startingX, startingY);

        for (int i = 0; i < numOfShots; i++){
            totalIterations++;
            System.out.println("Shoot " + i);

            if(i > 0){
                if(Math.random()<0.5){
                    noiseX = Math.random() * getDistance(holeX, startingX, holeY, startingY);
                    noiseY = Math.random() * getDistance(holeX, startingX, holeY, startingY);
                }else{
                    noiseX = (Math.random() * 10)-5;
                    noiseY = (Math.random() * 10)-5;
                }
                if(Math.random()<0.5) {
                    shots[i] = new Shoot((holeX - startingX) * f + noiseX, (holeY - startingY) * f + noiseY, startingX, startingY);
                }else {
                    shots[i] = new Shoot((holeX - startingX) * f - noiseX, (holeY - startingY) * f - noiseY, startingX, startingY);

                }
                System.out.println("Noise X: "+ noiseX+ " Noise Y: "+ noiseY);
            }
            System.out.println("Shot "+i+" Starting conditions: xDir: " +shots[i].getxDir()+" yDir: " +shots[i].getYDir());

            shotIterations = 0;
            while (!hasConverged(shots[i]) && shotIterations < 100 && !shots[i].inWater() || shotIterations == 0) {
                if (shots[i].inHole() && !shots[i].inWater()) {
                    System.out.println();
                    System.out.println("Final shot xDir: " + shots[i].getxDir() + " yDir: " + shots[i].getYDir() +" distance to hole: "+shots[i].getDistanceToHole());
                    System.out.println("Straight shot xDir: " + (holeX-startingX) * f + " yDir: " + (holeY-startingY) * f);
                    System.out.println("Total iterations: "+ totalIterations);
                    return Optional.of(Player.Hit.create(shots[i].getxDir(), shots[i].getYDir()));
                }

                if(!lookForBetterShot(shots[i]).inWater()){
                    shots[i] = lookForBetterShot(shots[i]);
                }
                System.out.println("New shot " + i + ". Distance to hole: " + shots[i].getDistanceToHole()+" xDir "+ shots[i].getxDir()+"yDir "+ shots[i].getYDir());
                shotIterations++;
                totalIterations++;
            }
        }
        System.out.println("Sorting the shots...");
        sortShots(shots);
        System.out.println("Shots sorted!");
        int i = 0;
        for (Shoot shoot : shots){
            System.out.println("Distance to hole: "+shoot.getDistanceToHole()+ " xDir: " + shoot.getxDir()+ " yDir: "+shoot.getYDir());
            if (shoot.getDistanceToHole() != 0 && shoot.getDistanceToHole() != 1000){
                System.out.println("Total iterations: "+ totalIterations);
                return Optional.of(Player.Hit.create(shots[i].getxDir(), shots[i].getYDir()));
            }
            i++;
        }
        return Optional.of(Player.Hit.create(shots[0].getxDir(), shots[0].getYDir()));
    }
    public static boolean hasConverged(Shoot shoot){
        return shoot.getDistanceToHole() <= lookForBetterShot(shoot).getDistanceToHole() || lookForBetterShot(shoot).inWater();
    }
    public static Shoot lookForBetterShot(Shoot shoot){
        double deltaT = 0.4;
        double newX;
        double newY;
        if(Math.abs(shoot.getFinalX()-holeX) < holeR){
            newX = shoot.getxDir();
        } else if(shoot.getFinalX()>holeX){
            newX = shoot.getxDir() - deltaT;
        } else{
            newX = shoot.getxDir() + deltaT;
        }

        if(Math.abs(shoot.getFinalY()-holeY) < holeR){
            newY = shoot.getYDir();
        } else if(shoot.getFinalY()>holeY){
            newY = shoot.getYDir() - deltaT;
        } else{
            newY = shoot.getYDir() + deltaT;
        }
        return new Shoot(newX, newY, shoot.getStartingX(), shoot.getStartingY());
    }

    public static void sortShots(Shoot[] shots) {
        Arrays.sort(shots, Comparator.comparingDouble(Shoot::getDistanceToHole));
    }


    public static double getDistance(double finalX, double startingX, double finalY, double startingY) {
        return Math.sqrt(Math.pow((finalX - startingX), 2) + Math.pow((finalY - startingY), 2));
    }

    public static void getShotDistanceToHole(Friction friction, HeightProfile profile, @NotNull MotionState current, Shoot shoot) {
        double minDistance = getDistance(holeX, current.getXPosition(), holeY, current.getYPosition());

        while (StopCondition.isMoving(profile, current, friction, stepSize)) {
            current = solver.calculate(current, stepSize);
            if(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()) < minDistance){
                minDistance = getDistance(holeX, current.getXPosition(), holeY, current.getYPosition());
                shoot.setDistanceToHole(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()));
                shoot.setFinalX(current.getXPosition());
                shoot.setFinalY(current.getYPosition());
                if(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()) < holeR){
                    shoot.setInHole();
                    break;
                }
            }
            if(profile.getHeight(current.getXPosition(), current.getYPosition()) < 0) {
                shoot.setInWater();
                break;
            }
        }
        if(shoot.inWater()){
            shoot.setDistanceToHole(1000);
            System.out.println("Shot got into the water! ");
        }

    }

    public static Optional<Player.Hit> bruteForce(double startingX, double stratingY){

        //for(double)
        return Optional.empty();
    }
}
