public class Slicer extends SlicerType{
    private static final int HEALTH = 1;
    private static final double SPEED = 2;
    private static final int PENALTY = 1;
    private static final int REWARD = 2;

    /**
     * call Regular Slicer
     *
     * @param imageRawPath raw image directory
     */
    public Slicer(String imageRawPath){super(imageRawPath);}

    public String getImagePath(){
        String typeName = getClass().getName().toLowerCase();
        return getImageRawPath()+typeName+".png";
    }

    public int getHealth(){return HEALTH;}
    public double getSpeed(){return SPEED;}
    public int getPenalty(){return PENALTY;}
    public int getReward(){return REWARD;}
}
