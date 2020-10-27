public class SlicerType {
    private String imageRawPath;

    /**
     * Call Slicer's type based on waves.txt file
     *
     * @param imageRawPath raw image directory
     */
    public SlicerType(String imageRawPath){
        this.imageRawPath = imageRawPath;
    }
    public String getImageRawPath(){return imageRawPath;}
    public String getImagePath(){return null;}

    /**
     * Functions below allow ShadowDefend to access
     * specification of a specific Slicer
     */
    public int getHealth(){return 0;}
    public double getSpeed(){return 0.0;}
    public int getPenalty(){return 0;}
    public int getReward(){return 0;}
}
