import bagel.util.Point;
import bagel.Image;
import bagel.util.Rectangle;
public class Explosives{
    private Point dropCoordinate;
    private static final String EXPLOSIVE_IMAGE = "res/images/explosive.png";
    private static final double DETONATION_TIME = 2.0;
    private double count;

    /**
     * Create Explosives
     *
     * @param dropCoordinate explosive dropped position
     */
    public Explosives(Point dropCoordinate) {
        this.dropCoordinate = dropCoordinate;
        this.count = 0;
    }

    public Image getExplosivesImage(){return new Image(EXPLOSIVE_IMAGE);}
    public void updateExplosives(){getExplosivesImage().draw(dropCoordinate.x, dropCoordinate.y);}
    public Rectangle getRect(){return new Rectangle(getExplosivesImage().getBoundingBoxAt(dropCoordinate));}
    public Point getCenter(){return getRect().centre();}

    /**
     * Detonation countdown.
     */
    public double detonationCountdown(){
        count++;
        return count/ShadowDefend.FPS;
    }

    /**
     * Logic that indicates the explosive have to explode.
     */
    public boolean timeToExplode(){return detonationCountdown() >= DETONATION_TIME/ShadowDefend.getTimescale();}
}
