package project12.group19.api.motion;

public class FrictionC implements Friction{
    private double staticCoefficient;
    private double dynamicCoefficient;
    public FrictionC(double staticCoefficient, double dynamicCoefficient){
        this.staticCoefficient = staticCoefficient;
        this.dynamicCoefficient = dynamicCoefficient;
    }
    @Override
    public double getStaticCoefficient() {
        // TODO Auto-generated method stub
        return staticCoefficient;
    }

    @Override
    public double getDynamicCoefficient() {
        // TODO Auto-generated method stub
        return dynamicCoefficient;
    }
    
}
