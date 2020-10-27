import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

public class AirSupport extends Tower{
    public static final double RADIUS_EFFECT = 200;
    private static final int PRICE = 500;
    private static final int DAMAGE = 500;
    private static final double SPEED = 3.0;

    private double countExplodedBomb;
    private double x, y, time, currentTime;
    private boolean dropBomb, hasExploded;
    private int explosivesCount;
    private Point droppingCoordinate;
    private Explosives[] explosives = new Explosives[ShadowDefend.MAX_DATA];

    /**
     * Creates air support
     *
     * @param newLoc position where passive tower placement
     * @param towerNum number of passive towers that have been called
     */
    public AirSupport(Point newLoc, int towerNum) {
        super(newLoc, towerNum);
        this.dropBomb = false;
        this.hasExploded = false;
        this.explosivesCount = 0;
        this.countExplodedBomb = 0;
        this.time = Tower.chooseRandomTime(ShadowDefend.getTimescale());
    }

    public int getPrice(){return PRICE;}
    public String getImagePath(){return ShadowDefend.BUY_AIRPLANE;}
    public Image getImage(){return new Image(ShadowDefend.BUY_AIRPLANE);}
    public boolean timeToDrop(){return dropBomb;}

    /**
     * logic to indicates that all explosives have been dropped.
     */
    public boolean hasAllExploded(){
        return ((countExplodedBomb==explosivesCount) && (countExplodedBomb>0) && (explosivesCount >0));
    }

    public Point getDroppingCoordinate(){return droppingCoordinate;}

    /**
     * Compute horizontal movement
     *
     * @param timeScale game timescale
     * @param currentTime counting time
     */
    private void horizontalPath(int timeScale, double currentTime){
        if(x < ShadowDefend.WIDTH + (ShadowDefend.FPS*SPEED*chooseRandomTime(timeScale)) ){
            x+= SPEED*timeScale;
            getImage().draw(x, getNewLoc().y, new DrawOptions().setRotation(Math.PI/2));
            if(currentTime >= time){
                droppingCoordinate = new Point(x, getNewLoc().y);
                dropBomb = true;
            }
        }
    }

    /**
     * Compute vertical movement
     *
     * @param timeScale game timescale
     * @param currentTime counting time
     */
    private void verticalPath(int timeScale, double currentTime){
        if(y < ShadowDefend.HEIGHT+(ShadowDefend.FPS*SPEED*chooseRandomTime(timeScale))){
            y+= SPEED*timeScale;
            getImage().draw(getNewLoc().x, y, new DrawOptions().setRotation(Math.PI));
            if(currentTime >= time){
                droppingCoordinate = new Point(getNewLoc().x, y);
                dropBomb = true;
            }
        }
    }

    /**
     * Update status if a bomb has exploded
     *
     * @param status update status of an explosive.
     */
    public void updateExploded(boolean status){ this.hasExploded = status; }

    /**
     * Calculate magnitude between a slicer's current position and center of explosion.
     *
     * @param slicer a moving slicer
     * @param explosive an explosive.
     */
    public boolean inExplosionRange(Enemy slicer, Explosives explosive){
        Point slicerPoint = slicer.Position();
        Vector2 targetPoint = slicerPoint.asVector();
        Vector2 explosivePoint = explosive.getCenter().asVector();
        Vector2 distance = targetPoint.sub(explosivePoint);
        double magnitude = Math.abs(distance.length());
        return magnitude < RADIUS_EFFECT;
    }

    /**
     * Damage any slicers within the radius effect
     *
     * @param slicers moving slicers within the radius effects
     * @param explosive a dropped explosive.
     */
    public void bombDamage(Enemy[] slicers, Explosives explosive){
        for (Enemy s : slicers) {
            if (s != null) {
                if (inExplosionRange(s, explosive)) {
                    s.updateSlicerHealth(DAMAGE);
                    s.spawnChildSlicer(s, s.isTerminated());
                }
            }
        }
    }

    /**
     * Updates explosives conditions.
     *
     * @param slicers moving slicers.
     */
    public void explosionAction(Enemy[] slicers){
        for(int i = 0; i<explosives.length; i++){
            if(explosives[i]!= null){
                hasExploded = explosives[i].timeToExplode();
                updateExploded(hasExploded);
                if(hasExploded) {
                    bombDamage(slicers, explosives[i]);
                    countExplodedBomb++;
                    explosives[i] = null;
                }
                if (countExplodedBomb == explosivesCount) {
                    break;
                }
            }
        }
    }

    /**
     * Updates a passive tower, from reading slicers.
     *
     * @param slicers moving slicers.
     */
    @Override
    public void updatePassiveTower(Enemy[] slicers){
        int timeScale = ShadowDefend.getTimescale();
        currentTime += timeScale;
        if(getTowerNum()%2 == 0){
            verticalPath(timeScale, currentTime/ShadowDefend.FPS);
        }else{
            horizontalPath(timeScale, currentTime/ShadowDefend.FPS);
        }

        if(timeToDrop()){
            currentTime = 0;
            dropBomb = false;
            explosives[explosivesCount] = new Explosives(getDroppingCoordinate());
            if(droppingCoordinate.x < ShadowDefend.WIDTH && droppingCoordinate.y < ShadowDefend.HEIGHT){
               explosivesCount ++;
            }
            time = Tower.chooseRandomTime(ShadowDefend.getTimescale());
        }

        for(Explosives e: explosives){
            if(e != null && !ShadowDefend.getBuyPanelRect().intersects(e.getRect())
                    && !ShadowDefend.getStatusPanelRect().intersects(e.getRect())){
                e.updateExplosives();
            }
        }
        explosionAction(slicers);
    }
}
