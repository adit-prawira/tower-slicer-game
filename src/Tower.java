import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.DrawOptions;

public abstract class Tower{
    // Constants for initial time Range
    private static final double MAX_TIME_RANGE = 2;
    private static final double MIN_TIME_RANGE = 1;

    private Point newLoc;
    private double angle;
    private int towerNum;

    /**
     * Created Active Tower type assigned point
     *
     * @param newLoc placement point
     */
    public Tower(Point newLoc){
        this.newLoc = newLoc;
        this.angle = 0;
        this.towerNum = 0;
    }

    /**
     * Created Passive Tower type assigned point
     *
     * @param newLoc placement point
     * @param towerNum number of passive tower to manage movement every time it is being called.
     */
    public Tower(Point newLoc, int towerNum){
        this.newLoc = newLoc;
        this.angle = 0;
        this.towerNum = towerNum;
    }

    public String getImagePath(){return null;}
    public Image getImage(){return new Image(getImagePath());}
    public Point getNewLoc(){return newLoc;}
    public int getPrice(){return 0;}
    public Rectangle getRect(){return null;}
    public double getRadiusEffect(){return 0;}

    /**
     * Setting active tower's facing direction.
     *
     * @param angle updated angle.
     */
    public void setAngle(double angle){this.angle = angle;}

    /**
     * Update and draw an active tower based on a targeted slicer.
     * as default behaviour.
     *
     * @param slicer any moving targeted slicer.
     */
    public void updateActiveTower(Enemy slicer){
        getImage().draw(getNewLoc().x, getNewLoc().y,
                new DrawOptions().setRotation(angle));
    }

    public int getTowerNum(){return towerNum;}
    public void updatePassiveTower(Enemy[] slicers){}
    public boolean hasAllExploded(){return false;}

    /**
     * Randomly choose explosive dropping time between 1-2.
     *
     * @param timeScale game time scale.
     */
    public static double chooseRandomTime(int timeScale){

        //Time interval will be affected by changing timeScale;
        final double maxTimeRange = MAX_TIME_RANGE/timeScale;
        final double minTimeRange = MIN_TIME_RANGE/timeScale;
        return (Math.random()*(maxTimeRange - minTimeRange))+minTimeRange;
    }
}
