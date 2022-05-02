package project12.group19.incubating;

import project12.group19.api.domain.Player;
import project12.group19.api.game.Configuration;
import project12.group19.api.geometry.space.HeightProfile;
import project12.group19.api.motion.Friction;
import project12.group19.api.motion.FrictionC;
import project12.group19.api.motion.MotionState;
import project12.group19.api.motion.Solver;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class HillClimbing2 {

    public static Solver solver;
    public static Configuration configuration;

    public static double holeX;
    public static double holeY;
    public static double holeR;

    public static HeightProfile profile;
    public static Friction friction;

    public static int numOfShots = 20;
    public static Shoot[] shots = new Shoot[numOfShots];

    public static double stepSize = 0.05;

    public HillClimbing2(Solver solver, Configuration configuration){
        HillClimbing2.solver = solver;
        HillClimbing2.profile = configuration.getHeightProfile();
        HillClimbing2.friction = configuration.getGroundFriction();
        HillClimbing2.holeX = configuration.getHole().getxHole();
        HillClimbing2.holeY = configuration.getHole().getyHole();
    }

    public Optional<Player.Hit> bruteForce(double startingX, double startingY) {
        Shoot currentShot;
        int iterations = 0;
        System.out.println("Searching for shoot...");

        for (double x = 0.1; x < 15 ; x+= 0.1){
            for(double y = 0.1; y < 15; y+= 0.1){
                currentShot = new Shoot(x, y, startingX, startingY);
                iterations++;
                //System.out.println(iterations);
                System.out.println("X: "+x+" Y: "+y);
                if(currentShot.inHole()){
                    System.out.println("Solution Found!");
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
        double iterations;
        double min = 3;
        double max = 0.5;
        double noise;

        shots[0] = new Shoot((holeX-startingX) * f, (holeY-startingY) * f, startingX, startingY);

        for (int i = 0; i <= numOfShots; i++){
            noise = Math.random() +0.5;
            if(i > 0){
                shots[i] = new Shoot((holeX-startingX) * f * noise, (holeY-startingY) * f * noise, startingX, startingY);
            }
            System.out.println("Noise: "+ noise);
            System.out.println("Shot Nª "+i+" Starting conditions: xDir: " +shots[i].getxDir()+" yDir: " +shots[i].getYDir());

            iterations = 0;
            while (!hasConverged(shots[i]) && iterations < 100){
                shots[i] = lookForBetterShot(shots[i]);
                System.out.println("Improvement "+(int)iterations+ " in shot nº "+i + ". Distance to hole: " +shots[i].getDistanceToHole());
                iterations++;

                if(shots[i].inHole()){
                    System.out.println();
                    System.out.println("Shoot xDir: "+shots[i].getxDir()+" yDir: "+shots[i].getYDir());
                    System.out.println("Straight shot xDir: "+ shots[0].getxDir()+" yDir: "+ shots[0].getYDir());
                    return Optional.of(Player.Hit.create(shots[i].getxDir(), shots[i].getYDir()));
                }
            }
            System.out.println();
        }
        sortShots(shots);
        return Optional.of(Player.Hit.create(shots[0].getxDir(), shots[0].getYDir()));
    }
    public static boolean hasConverged(Shoot shoot){
        return shoot.getDistanceToHole() < lookForBetterShot(shoot).getDistanceToHole();
    }
    public static Shoot lookForBetterShot(Shoot shoot){
        double deltaT = 0.5;
        double newX;
        double newY;

        if(Math.abs(shoot.getFinalX()-holeX) < 0.4){
            newX = shoot.getxDir();
        } else if(shoot.getFinalX()>holeX){
            newX = shoot.getxDir() - deltaT;
        } else{
            newX = shoot.getxDir() + deltaT;
        }

        if(Math.abs(shoot.getFinalY()-holeY) < 0.4){
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

        while (solver.isMoving(profile, current, new FrictionC(friction.getStaticCoefficient(), 0.2))) {
            current = solver.calculate(current, Solver.acceleration(profile, current, friction, stepSize), stepSize);
            if(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()) < minDistance){
                minDistance = getDistance(holeX, current.getXPosition(), holeY, current.getYPosition());
                shoot.setDistanceToHole(getDistance(holeX, current.getXPosition(), holeY, current.getYPosition()));
                shoot.setFinalX(current.getXPosition());
                shoot.setFinalY(current.getYPosition());

            }
        }

    }




}
