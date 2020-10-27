import bagel.util.Point;
import bagel.Image;
import bagel.util.Rectangle;
import bagel.util.Vector2;
public class Tank extends Tower{
    // Constants for Tank
    public static final double TIME_COOL = 1.0;
    private static final int PRICE = 250;
    private static final int DAMAGE = 1;
    private static final double RADIUS_EFFECT = 100;
    private static final String PROJECT_IMAGE = "res/images/tank_projectile.png";

    private double angle;
    private boolean isCoolingDown;
    private Projectile projectile;

    /**
     * Create Tank.
     *
     * @param newLoc position of placed active tower.
     */
    public Tank(Point newLoc) {
        super(newLoc);
        this.angle = 0;
        this.projectile = new Projectile(PROJECT_IMAGE, newLoc);
        this.isCoolingDown = false;
    }

    public int getPrice(){return PRICE;}
    public double getRadiusEffect(){return RADIUS_EFFECT;}
    public String getImagePath(){return ShadowDefend.BUY_TANK;}
    public Image getImage(){return new Image(ShadowDefend.BUY_TANK);}
    public Rectangle getRect(){return new Rectangle(getImage().getBoundingBoxAt(getNewLoc()));}
    public Point getCenter(){return getRect().centre();}
    public String getTypeName(){return getClass().getName().toLowerCase();}

    /**
     * Cooling state of a Tank
     */
    public void coolingState(){
        projectile.setTowerClass(getTypeName());
        projectile.countCoolDown();
        if(projectile.finishedCoolingDown()){
            isCoolingDown = false;
            projectile.resetTimeCurrent();
            projectile = new Projectile(PROJECT_IMAGE, getNewLoc());
        }
    }

    /**
     * Calculate projectile's current post from its origin
     */
    public double projectileMagnitude(){
        Vector2 projectPoint = projectile.getProjectCenter().asVector();
        Vector2 originPoint = getCenter().asVector();
        Vector2 distance = originPoint.sub(projectPoint);
        return distance.length();
    }

    /**
     * Logic to make sure the projectile never miss a target.
     *
     * @param projectile a projectile.
     */
    public boolean optimalShootingCondition(Enemy slicer, Projectile projectile){
        return (!slicer.getRect().intersects(projectile.getProjectCenter())||
                !slicer.getRect().intersects(projectile.getProjectRect())) &&
                (projectileMagnitude()<=slicer.returnMagnitude());
    }

    /**
     * Self-oriented Tank and shoot projectile logic
     */
    public void updateProjectile(Enemy slicer){
        angle = Math.atan2(slicer.returnDistance().y, slicer.returnDistance().x)+ Math.PI/2;
        projectile.move(slicer.returnDistance().
                normalised().mul(Projectile.SPEED*ShadowDefend.getTimescale()));
        projectile.updateProjectile();
    }

    /**
     * Damage a slicer from projectile attack.
     */
    public void damagingSlicer(Enemy slicer){
        isCoolingDown = true;
        slicer.updateSlicerHealth(DAMAGE);
        slicer.spawnChildSlicer(slicer, slicer.isTerminated());
    }

    /**
     * Updates Tank Action by a reading slicers.
     *
     * @param slicer a moving slicer within the radius effect.
     */
    @Override
    public void updateActiveTower(Enemy slicer){
        if(isCoolingDown){
            coolingState();
        }else{
            if(slicer!=null && slicer.returnMagnitude() < RADIUS_EFFECT) {
                if (optimalShootingCondition(slicer, projectile)){
                    updateProjectile(slicer);
                }else{
                    damagingSlicer(slicer);
                }
            }
        }
        super.setAngle(angle);
        super.updateActiveTower(slicer);
    }
}
