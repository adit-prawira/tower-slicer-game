import bagel.Image;
import bagel.util.Rectangle;
import bagel.util.Point;
import bagel.util.Vector2;

public class Projectile {
    public static final double SPEED = 10;
    private double frameCount;
    private double timeCurrent;
    private Image image;
    private Rectangle rect;
    private String name;

    /**
     * Create Projectile.
     *
     * @param imgSrc image directory of a projectile.
     * @param current initial projectile position.
     */
    public Projectile(String imgSrc, Point current){
        this.image = new Image(imgSrc);
        this.rect = image.getBoundingBoxAt(current);
        this.timeCurrent = 0;
        this.frameCount = 0;
    }

    public Rectangle getProjectRect(){return new Rectangle(rect);}
    public void move(Vector2 dx) {rect.moveTo(rect.topLeft().asVector().add(dx).asPoint()); }
    public Point getProjectCenter(){return getProjectRect().centre();}
    public void updateProjectile() { image.draw(getProjectCenter().x, getProjectCenter().y); }
    public void setTowerClass(String name){this.name = name;}
    public void resetTimeCurrent(){
        timeCurrent = 0;
    }

    /**
     * Cool down countdown logic.
     */
    public void countCoolDown(){
        frameCount += ShadowDefend.getTimescale();
        timeCurrent = frameCount/ShadowDefend.FPS;
    }

    /**
     * Boolean logic for any active tower that has cooled down.
     */
    public boolean finishedCoolingDown(){
        if(name.equals(Enemy.SUPER_SLICER)){
            return timeCurrent > (SuperTank.TIME_COOL/ShadowDefend.getTimescale());
        }
        return timeCurrent > (Tank.TIME_COOL/ShadowDefend.getTimescale());
    }
}
