import bagel.util.Point;
import bagel.Image;
import bagel.util.Rectangle;
import bagel.util.Vector2;

public class SuperTank extends Tower{
    // Constants for Super Tank
    public static final double TIME_COOL = 0.5;
    private static final int PRICE = 600;
    private static final int DAMAGE = 3;
    private static final double RADIUS_EFFECT = 150;
    private static final String PROJECT_IMAGE = "res/images/supertank_projectile.png";

    private double angle;
    private boolean isCoolingDown;
    private Projectile projectile;

    /**
     * Create Super Tank.
     *
     * @param newLoc position of placed active tower.
     */
    public SuperTank(Point newLoc) {
        super(newLoc);
        this.angle = 0;
        this.projectile = new Projectile(PROJECT_IMAGE, newLoc);
        this.isCoolingDown = false;

    }

    public int getPrice(){return PRICE;}
    public double getRadiusEffect(){return RADIUS_EFFECT;}
    public String getImagePath(){return ShadowDefend.BUY_SUPERTANK;}
    public Image getImage(){return new Image(ShadowDefend.BUY_SUPERTANK);}
    public Rectangle getRect(){return new Rectangle(getImage().getBoundingBoxAt(getNewLoc()));}
    public Point getCenter(){return getRect().centre();}
    public String getTypeName(){return getClass().getName().toLowerCase();}

    /**
     * Cooling state of a Super Tank
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
     * Self-oriented Super Tank and shoot projectile logic
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
     * Updates Super Tank Action by a reading of nearby slicers.
     *
     * @param slicer a moving slicer within the radius effect.
     */
    @Override
    public void updateActiveTower(Enemy slicer){
        if(!isCoolingDown){
            if(slicer!=null && slicer.returnMagnitude() < RADIUS_EFFECT) {
                if (optimalShootingCondition(slicer, projectile)){
                    updateProjectile(slicer);
                }else{
                    damagingSlicer(slicer);
                }
            }
        }else{
            coolingState();
        }
        super.setAngle(angle);
        super.updateActiveTower(slicer);
    }
}
