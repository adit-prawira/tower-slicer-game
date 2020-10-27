import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.Image;
import bagel.Input;

public class TowerLoader{
    private final Point mouseLoc;
    private final Rectangle tank, superTank, airSupport;
    private static Tower placeTower;
    private Image img;
    /**
     * Load Bought Tower
     *
     * @param mouseLoc mouse point when choosing a tower
     */
    public TowerLoader(Point mouseLoc){
        this.tank = ShadowDefend.buyTankRect();
        this.superTank = ShadowDefend.buySuperTanRect();
        this.airSupport = ShadowDefend.buyAirSupportRect();
        this.mouseLoc = mouseLoc;
    }

    /**
     * Make sure a tower has been selected
     */
    public boolean validSelection(){
        return tank.intersects(mouseLoc) || superTank.intersects(mouseLoc) ||  airSupport.intersects(mouseLoc);
    }

    public boolean ableToBuyTank(){
        img = getImage(ShadowDefend.BUY_TANK);
        return tank.intersects(mouseLoc) && ShadowDefend.getCurrentFund() >= ShadowDefend.FIXED_PRICE1;
    }
    public boolean ableToBuySuperTank(){
        img = getImage(ShadowDefend.BUY_SUPERTANK);
        return superTank.intersects(mouseLoc) && ShadowDefend.getCurrentFund() >= ShadowDefend.FIXED_PRICE2;
    }
    public boolean ableToBuyAirSupport(){
        img = getImage(ShadowDefend.BUY_AIRPLANE);
        return airSupport.intersects(mouseLoc)&& ShadowDefend.getCurrentFund() >= ShadowDefend.FIXED_PRICE3;
    }

    /**
     * Tower image getter
     *
     * @param path tower image directory
     */
    public Image getImage(String path){
        return new Image(path);
    }

    /**
     * render tower on valid position only
     *
     * @param input mouse tower placement point
     */
    public void renderValidPosition(Input input){
        if(ableToBuyTank()){
            img.draw(input.getMouseX(), input.getMouseY());
        }
        if(ableToBuySuperTank()){
            img.draw(input.getMouseX(), input.getMouseY());
        }
        if(ableToBuyAirSupport()){
            img.draw(input.getMouseX(), input.getMouseY());
        }
    }

    /**
     * Return boolean value if chosen tower has its bounding box
     * intersect with buying or status panel.
     */
    public boolean intersectWithPanel(Input input){
        if(validSelection()) {
            return img.getBoundingBoxAt(input.getMousePosition()).intersects(ShadowDefend.getBuyPanelRect()) ||
                    img.getBoundingBoxAt(input.getMousePosition()).intersects(ShadowDefend.getStatusPanelRect());
        }
        return false;
    }

    /**
     * Create Tower at assigned location.
     *
     * @param newLoc tower position
     * @param towerNum number of passive tower called.
     */
    public Tower selectedTower(Point newLoc, int towerNum){
        if(ableToBuyTank()){
            placeTower = new Tank(newLoc);
        }
        if(ableToBuySuperTank()){
            placeTower =  new SuperTank(newLoc);
        }
        if(ableToBuyAirSupport()) {
            placeTower = new AirSupport(newLoc, towerNum);
        }
        return placeTower;
    }
}
