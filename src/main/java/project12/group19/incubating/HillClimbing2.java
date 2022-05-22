package project12.group19.incubating;

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
    public static double ballR = 0.5;

    public static HeightProfile profile;
    public static Friction friction;

    public static int numOfShots = 100;
    public static Shoot[] shots = new Shoot[numOfShots];
    public static Shoot[] initialSHots = new Shoot[numOfShots]; //array of shots to compare with improved shots

    public static double stepSize = 0.05;

    public HillClimbing2(Solver solver, Configuration configuration){
        HillClimbing2.solver = solver;
        HillClimbing2.profile = configuration.getHeightProfile();
        HillClimbing2.friction = configuration.getGroundFriction();
        HillClimbing2.holeX = configuration.getHole().getxHole();
        HillClimbing2.holeY = configuration.getHole().getyHole();
        HillClimbing2.configuration = configuration;
    }

    public Optional<Player.Hit> bruteForce(double startingX, double startingY) {
        Shoot currentShot;
        int iterations = 0;
        double variation = 5;
        System.out.println("Searching for shoot...");


        for (double x = 0.1; x < 15 ; x+= 0.1){
            for(double y = 0.1; y < 15; y+= 0.1){
                currentShot = new Shoot(x, y, startingX, startingY);
                iterations++;
                //System.out.println(iterations);
                System.out.println("X: "+x+" Y: "+y);
                if(currentShot.inHole()){
                    System.out.println("Solution Found in "+iterations+" iterations");
                    return Optional.of(Player.Hit.create(currentShot.getxDir(), currentShot.getYDir()));
                }
            }
        }
        System.out.println("No solution :(");
        double f = 0.5;
        return Optional.of(Player.Hit.create((holeX-startingX) * f, (holeY-startingY) * f));
    }

    public Optional<Player.Hit> StraightShot(double startingX, double startingY) {
        double f = 0.3;
        return Optional.of(Player.Hit.create((holeX-startingX) * f, (holeY-startingY) * f));
    }

    public Optional<Player.Hit> hillClimbing1(double startingX, double startingY) {
        double f = 0.5;
        int iterations;
        double noiseX;
        double noiseY;


        shots[0] = new Shoot((holeX-startingX) * f, (holeY-startingY) * f, startingX, startingY);

        for (int i = 0; i < numOfShots; i++){
            System.out.println("Shoot Nº " + i);

            noiseX = Math.random() * getDistance(holeX, startingX, holeY, startingY);
            noiseY = Math.random() * getDistance(holeX, startingX, holeY, startingY);
            if(i > 0){
                shots[i] = new Shoot((holeX-startingX) * f + noiseX, (holeY-startingY) * f + noiseY, startingX, startingY);
                System.out.println("Noise X: "+ noiseX+ " Noise Y: "+ noiseY);
            }
            System.out.println("Shot Nª "+i+" Starting conditions: xDir: " +shots[i].getxDir()+" yDir: " +shots[i].getYDir());

            iterations = 0;
            initialSHots[i] = new Shoot(shots[i].getxDir(), shots[i].getYDir(), startingX, startingY);
            while (!hasConverged(shots[i]) && iterations < 100 && !shots[i].inWater() || iterations ==0) {
                if(!lookForBetterShot(shots[i]).inWater()){
                    shots[i] = lookForBetterShot(shots[i]);
                }
                System.out.println("Improvement " + iterations + " in shot nº " + i + ". Distance to hole: " + shots[i].getDistanceToHole());
                iterations++;
                if (shots[i].inHole()) {
                    System.out.println();
                    System.out.println("Initial shot xDir: "+initialSHots[i].getxDir() + " yDir: "+initialSHots[i].getYDir());
                    System.out.println("Final shot xDir: " + shots[i].getxDir() + " yDir: " + shots[i].getYDir());
                    System.out.println("Straight shot xDir: " + (holeX-startingX) * f + " yDir: " + (holeY-startingY) * f);
                    return Optional.of(Player.Hit.create(shots[i].getxDir(), shots[i].getYDir()));
                }

            }
        }
        System.out.println("Sorting the shots...");
        sortShots(shots);
        System.out.println("Shots sorted!");
        for (Shoot shoot : shots){
            System.out.println(shoot.getDistanceToHole());
        }
        return Optional.of(Player.Hit.create(shots[0].getxDir(), shots[0].getYDir()));
    }
    public static boolean hasConverged(Shoot shoot){
        return shoot.getDistanceToHole() <= lookForBetterShot(shoot).getDistanceToHole();
    }
    public static Shoot lookForBetterShot(Shoot shoot){
        double deltaT = 0.4;
        double newX;
        double newY;

        if(Math.abs(shoot.getFinalX()-holeX) < ballR){
            newX = shoot.getxDir();
        } else if(shoot.getFinalX()>holeX){
            newX = shoot.getxDir() - deltaT;
        } else{
            newX = shoot.getxDir() + deltaT;
        }

        if(Math.abs(shoot.getFinalY()-holeY) < ballR){
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

    public static void getShotDistanceToHole(Friction friction, HeightProfile profile, MotionState current, Shoot shoot) {
        double minDistance = getDistance(holeX, current.getXPosition(), holeY, current.getYPosition());

        while (StopCondition.isMoving(profile, current, friction, stepSize)) {
            current = solver.calculate(current, stepSize);
            if(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()) < minDistance){
                minDistance = getDistance(holeX, current.getXPosition(), holeY, current.getYPosition());
                shoot.setDistanceToHole(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()));
                shoot.setFinalX(current.getXPosition());
                shoot.setFinalY(current.getYPosition());
            }
            if(profile.getHeight(current.getXPosition(), current.getYPosition()) < 0){
                shoot.setInWater();
            }
        }
        if(shoot.inWater()){
            System.out.println("Shot got into the water! ");
        }

    }

}
