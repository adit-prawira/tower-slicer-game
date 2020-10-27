public class ApexSlicer extends SlicerType{
    private static final int HEALTH = 25;
    private static final double SPEED = 0.75;
    private static final int PENALTY = 16;
    private static final int REWARD = 150;

    /**
     * call Apex Slicer
     *
     * @param imageRawPath raw image directory
     */
    public ApexSlicer(String imageRawPath){
        super(imageRawPath);
    }

    public String getImagePath(){
        String typeName = getClass().getName().toLowerCase();
        return getImageRawPath()+typeName+".png";
    }

    public int getHealth(){return HEALTH;}
    public double getSpeed(){return SPEED;}
    public int getPenalty(){return PENALTY;}
    public int getReward(){return REWARD;}
}
