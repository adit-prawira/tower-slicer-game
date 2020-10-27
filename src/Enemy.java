import bagel.Input;
import bagel.util.Point;
import bagel.util.Vector2;

/**
 * A regular slicer.
 */
public class Enemy extends Movable {
    private static final String RAW_IMAGE_PATH = "res/images/";
    public static final String SLICER = "slicer", MEGA_SLICER = "megaslicer";
    public static final String SUPER_SLICER ="superslicer", APEX_SLICER = "apexslicer";

    private static SlicerType callSlicer;
    private final Point[] polyline;
    private Point currentPoint;
    private String name;
    private Vector2 distance;
    private int targetPointIndex;
    private int currentHealth, penalty, reward;
    private boolean finished;
    private double magnitude;
    private double speed;

    /**
     * Creates a new Slicer
     *
     * @param polyline The polyline that the slicer must traverse
     */
    public Enemy(Point[] polyline, String slicerName) {
        super(polyline[0], chooseSlicer(slicerName));
        this.polyline = polyline;
        this.targetPointIndex = 1;
        this.finished = false;
        this.currentHealth = callSlicer.getHealth();
        this.penalty = callSlicer.getPenalty();
        this.reward = callSlicer.getReward();
        this.name = callSlicer.getClass().getName().toLowerCase();
        this.speed = callSlicer.getSpeed();
    }

    /**
     * Updates the current state of the slicer. The slicer moves towards its next target point in
     * the polyline at its specified movement rate.
     */
    @Override
    public void update(Input input) {
        if (finished) {
            return;
        }
        // Obtain where we currently are, and where we want to be
        currentPoint = getCenter();
        updatePosition(currentPoint);
        Point targetPoint = polyline[targetPointIndex];
        // Convert them to vectors to perform some very basic vector math
        Vector2 target = targetPoint.asVector();
        Vector2 current = currentPoint.asVector();
        Vector2 distance = target.sub(current);

        // Distance we are (in pixels) away from our target point
        double magnitude = distance.length();
        // Check if we are close to the target point
        if (magnitude < speed*ShadowDefend.getTimescale()) {
            // Check if we have reached the end
            if (targetPointIndex == polyline.length - 1) {
                finished = true;
                return;
            } else {
                // Make our focus the next point in the polyline
                targetPointIndex += 1;
            }
        }
        // Move towards the target point
        // We do this by getting a unit vector in the direction of our target, and multiplying it
        // by the speed of the slicer (accounting for the timescale)
        super.move(distance.normalised().mul(speed * ShadowDefend.getTimescale()));

        // Update current rotation angle to face target point
        setAngle(Math.atan2(targetPoint.y - currentPoint.y, targetPoint.x - currentPoint.x));
        super.update(input);
    }

    /**
     * Choosing slicers to spawn based on waves.txt file
     */
    public static String chooseSlicer(String name){
        name = name.toLowerCase();
        if(name.equals(SLICER)){
            callSlicer = new Slicer(RAW_IMAGE_PATH);
        }
        if(name.equals(SUPER_SLICER)){
            callSlicer = new SuperSlicer(RAW_IMAGE_PATH);
        }
        if(name.equals(MEGA_SLICER)){
            callSlicer = new MegaSlicer(RAW_IMAGE_PATH);
        }
        if(name.equals(APEX_SLICER)){
            callSlicer = new ApexSlicer(RAW_IMAGE_PATH);
        }
        return callSlicer.getImagePath();
    }

    /**
     * Status of slicer indicates that it is being targeted by a tower.
     *
     * @param t tower near a slicer.
     */
    public boolean isTargeted(Tower t){
        Vector2 slicerPoint = currentPoint.asVector();
        Vector2 towerPoint = t.getNewLoc().asVector();
        distance = slicerPoint.sub(towerPoint);
        magnitude = distance.length();
        return magnitude < t.getRadiusEffect();
    }

    /**
     * Calling child slicers when non-regular slicers have been defeated
     *
     * @param defeated status of defeated slicer
     */
    public void spawnChildSlicer(Enemy slicer, boolean defeated){
        String name = slicer.getSlicerType();
        if(name.equals(SUPER_SLICER)&& defeated){
            callChildSlicer(SLICER);
        }
        if(name.equals(MEGA_SLICER)  && isTerminated() && defeated){
            callChildSlicer(SUPER_SLICER);
        }
        if(name.equals(APEX_SLICER) && isTerminated() && defeated){
            callChildSlicer(MEGA_SLICER);
        }
    }

    /**
     * Update a slicer's specification based on its child specification.
     *
     * @param childType child slicer name.
     */
    public void callChildSlicer(String childType){
        ShadowDefend.addRewardToFund(reward);
        super.newConditions(currentPoint, chooseSlicer(childType));
        currentHealth = callSlicer.getHealth();
        penalty = callSlicer.getPenalty();
        reward = callSlicer.getReward();
        speed = callSlicer.getSpeed();
        updatePosition(currentPoint);
        updateType(callSlicer.getClass().getName().toLowerCase());
    }

    /**
     * Status of Slicer that has been terminated
     */
    public boolean isTerminated(){
        return currentHealth <= 0;
    }

    /**
     * Update slicer's health buy hit point of a tower.
     *
     * @param damage damage inflicted by a tower.
     */
    public void updateSlicerHealth(int damage){currentHealth -= damage;}
    public void updateType(String name){this.name = name;}

    public Point Position(){return currentPoint;}
    public void updatePosition(Point post){this.currentPoint = post;}

    /**
     * Check for finished slicer
     */
    public boolean isFinished() { return finished; }

    /**
     * Functions below are to transfer updated specifications to ShadowDefend.
     */
    public String getSlicerType(){return name;}
    public int SlicerPenalty(){return penalty;}
    public int SlicerReward(){return reward;}
    public double returnMagnitude(){return magnitude;}
    public Vector2 returnDistance(){return distance;}
}
