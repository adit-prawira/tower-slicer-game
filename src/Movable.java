import bagel.DrawOptions;
import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;

/**
 * Represents a game entity
 */
public abstract class Movable{

    private Image image;
    private Rectangle rect;
    private double angle;
    private Point point;

    /**
     * Creates a new Movable
     *
     * @param point    The starting point for the entity
     * @param imageSrc The image which will be rendered at the entity's point
     */
    public Movable(Point point, String imageSrc) {
        this.image = new Image(imageSrc);
        this.rect = image.getBoundingBoxAt(point);
        this.angle = 0;
        this.point = point;
    }

    /**
     * Updates a new Movable
     *
     * @param point    The latest point for the entity
     * @param imageSrc The latest image which will be rendered at the entity's point
     */
    public void newConditions(Point point, String imageSrc) {
        this.image = new Image(imageSrc);
        this.rect = image.getBoundingBoxAt(point);
        this.angle = 0;
        this.point = point;
    }

    public Rectangle getRect() { return new Rectangle(rect);}

    /**
     * Moves by a specified delta
     *
     * @param dx The move delta vector
     */
    public void move(Vector2 dx) { rect.moveTo(rect.topLeft().asVector().add(dx).asPoint()); }

    public Point getCenter() {return getRect().centre();}

    public void setAngle(double angle) {this.angle = angle;}

    /**
     * Updates the Movable. Render Movable at current position as default behaviour.
     */
    public void update(Input input) {
        image.draw(getCenter().x, getCenter().y, new DrawOptions().setRotation(angle));
    }
}