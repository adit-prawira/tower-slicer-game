import bagel.*;
import bagel.Image;
import bagel.Window;
import bagel.map.TiledMap;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.Font;
import bagel.util.Colour;
import bagel.DrawOptions;

/**
 * ShadowDefend, a tower defence game.
 */
public class ShadowDefend extends AbstractGame {
    // Constants for Initial lives and fund.
    private static final int INITIAL_HEALTH = 25;
    private static final int INITIAL_FUND = 500;

    // Font size constants.
    private static final int MONEY_FONT_SIZE = 48;
    private static final int KEYBINDS_FONT_SIZE = 14;
    private static final int PRICE_FONT_SIZE = 24;

    // Constant of Window's dimension.
    public static final int HEIGHT = 768;
    public static final int WIDTH = 1024;

    // Constants for text set up.
    private static final int SPACE_5PX = 5;
    private static final int SPACE_10PX = 10;
    private static final int SPACE_BETWEEN_CENTER = 120;
    private static final int KEY1_YPOST = 20;
    private static final int KEY2_YPOST = 45;
    private static final int KEY3_YPOST = 60;
    private static final int KEY4_YPOST = 75;
    private static final int FUND_YPOST = 65;
    private static final int FUND_XPOST = WIDTH-200;
    private static final int TIMESCALE_XPOST = 200;
    private static final int STATUS_XPOST = 450;

    // Constants for game conditions.
    private static final int MAX_LEVEL = 2;
    private static final int REWARD_CONST1 = 150;
    private static final int REWARD_CONST2 = 100;
    private static final int MAX_TIMESCALE = 5;
    private static final int MIN_TIMESCALE = 1;
    public static final int MAX_DATA = 1000;

    //String constants for blocked property.
    private static final String BLOCKED_PROPERTY = "blocked";

    //Resources directory constants.
    private static final String MAP_FILE = "res/levels/1.tmx";
    private static final String MAP_FILE2 = "res/levels/2.tmx";
    private static final String BUY_PANEL = "res/images/buypanel.png";
    private static final String STATUS_PANEL = "res/images/statuspanel.png";
    private static final String EVENT_FILE = "res/levels/waves.txt";
    public static final String BUY_TANK = "res/images/tank.png";
    public static final String BUY_SUPERTANK = "res/images/supertank.png";
    public static final String BUY_AIRPLANE = "res/images/airsupport.png";

    // Constant of the assumed FPS.
    public static final double FPS = 60;

    // The spawn delay (in seconds) to spawn slicers
    private static final String TOWER_PASSIVE = "airsupport";

    public static final int FIXED_PRICE1 = 250, FIXED_PRICE2 = 600, FIXED_PRICE3 = 500;

    //Constants of Rendered texts.
    private static final String PRICE1 = "$250", PRICE2 = "$600", PRICE3 = "$500";
    private static final String KEY_BINDS = "Key binds:";
    private static final String S_KEY = "S - Start Wave";
    private static final String L_KEY = "L - Increase Timescale";
    private static final String K_KEY = "K - Decrease Timescale";

    private String currentMoney;
    private final double statusPanelMidY, statusTextY,  waveStatusY;
    private final double liveWidth, liveTextX;
    private final double price1MidX, price2MidX, price3MidX, priceYPost;
    private final double keyBindsMid;
    private double frameCount;

    private final Point tankBuyLoc;
    private final Point superTankBuyLoc;
    private final Point airSupportBuyLoc;
    private final Point buyPanelLoc;
    private Point[] polyline, polyline2;

    private static Rectangle chooseTank;
    private static Rectangle chooseSuperTank;
    private static Rectangle chooseAirSupport;
    private static Rectangle withinBuyPanel;
    private static Rectangle withinStatusPanel;
    private Rectangle[] occupiedPosition;

    // Timescale is static because it affects all components of the game.
    private static int timescale = MIN_TIMESCALE;

    //Current fund is static, need to check fund availability to buy a tower.
    private static int currentFund;

    private int eventIndex = 0;
    private int level = 1;
    private int passiveMovementCount;
    private int occupiedPostIndex = 0;
    private int spawnedSlicers;
    private int slicersFinished, slicerDefeated;
    private int towerCount;
    private int index = 0;
    private int currentHealth;

    private boolean waveStarted;

    // Start placing status as false since nothing is get picked at the start of the game.
    private boolean isPlacing = false;

    private String slicerPath;
    private String lives;
    private String strTimeScale;

    // Initialize status of the game for awaiting start.
    private String status = "Awaiting Start";

    private TiledMap map, map2;
    private final Image buyPanel, buyTank, buySuperTank;
    private final Image buyAirPlane, statusPanel;

    private final EventFileReader waveEvents;
    private Events event;

    private final Font moneyText, keyBindText, priceText;
    private final DrawOptions colour = new DrawOptions();

    private final Enemy[] slicers;
    private final Tower[] tower;
    private TowerLoader selectTower;

    /**
     * Creates a new instance of the ShadowDefend game
     */
    public ShadowDefend() {
        super(WIDTH, HEIGHT, "ShadowDefend");
        this.map = new TiledMap(MAP_FILE);
        this.map2 = new TiledMap(MAP_FILE2);

        this.buyPanel = new Image(BUY_PANEL);
        this.statusPanel = new Image(STATUS_PANEL);

        this.buyTank = new Image(BUY_TANK);
        this.buySuperTank= new Image(BUY_SUPERTANK);
        this.buyAirPlane= new Image(BUY_AIRPLANE);

        this.waveEvents= new EventFileReader(EVENT_FILE);
        this.moneyText = new Font("res/fonts/DejaVuSans-Bold.ttf", MONEY_FONT_SIZE);
        this.keyBindText = new Font("res/fonts/DejaVuSans-Bold.ttf", KEYBINDS_FONT_SIZE);
        this.priceText = new Font("res/fonts/DejaVuSans-Bold.ttf", PRICE_FONT_SIZE);

        int polylinePointCount = map.getAllPolylines().get(0).size();
        int polylinePointCount2 = map2.getAllPolylines().get(0).size();

        // Copy the polyline into an array for later use
        this.polyline = new Point[polylinePointCount];
        this.polyline2 = new Point[polylinePointCount2];

        // Initialise number of slicers and towers.
        this.slicersFinished = 0;
        this.towerCount = 0;
        this.slicerDefeated = 0;
        this.passiveMovementCount= 1;

        //Gather polyline information from the map level 1 and 2 and store it into the array.
        int i = 0, j = 0;
        for (Point point : map.getAllPolylines().get(0)) {
            polyline[i++] = point;
        }
        for (Point point : map2.getAllPolylines().get(0)) {
            polyline2[j++] = point;
        }

        currentFund = INITIAL_FUND;
        this.currentHealth = INITIAL_HEALTH;
        this.currentMoney = "$"+ currentFund;

        this.slicers = new Enemy[MAX_DATA];
        this.tower = new Tower[MAX_DATA];
        this.occupiedPosition = new Rectangle[MAX_DATA];

        this.spawnedSlicers = 0;
        this.waveStarted = false;
        this.frameCount = Integer.MAX_VALUE;

        Events event = new SpawnEvent(waveEvents.getEvents()[0]);

        //initialize the first upcoming slicer type.
        this.slicerPath = event.getSlicerName();
        new Enemy(polyline, slicerPath);
        new Enemy(polyline2, slicerPath);

        //Buy and status Panel positions on the game window.
        double buyPanelMidY = buyPanel.getHeight() / 2.0;
        double tankMidX = buyTank.getWidth() / 2.0;

        double superTankMidX = buySuperTank.getWidth() / 2.0;
        double airPlaneMidX = buyAirPlane.getWidth() / 2.0;
        double nextWidth = SPACE_BETWEEN_CENTER + tankMidX + superTankMidX;

        this.statusPanelMidY = statusPanel.getHeight() / 2.0;
        this.tankBuyLoc = new Point(64, buyPanelMidY - SPACE_10PX);
        this.superTankBuyLoc = new Point(nextWidth, buyPanelMidY - SPACE_10PX);
        this.airSupportBuyLoc = new Point(SPACE_BETWEEN_CENTER+ nextWidth + airPlaneMidX - superTankMidX,
                buyPanelMidY - SPACE_10PX);
        this.buyPanelLoc = new Point(WIDTH / 2.0, buyPanelMidY);
        Point statusPanelLoc = new Point(WIDTH/ 2.0, HEIGHT - statusPanelMidY);

        this.price1MidX = tankBuyLoc.x- tankMidX;
        this.price2MidX = superTankBuyLoc.x - superTankMidX;
        this.price3MidX = airSupportBuyLoc.x - airPlaneMidX;
        this.priceYPost = buyPanel.getHeight()- SPACE_10PX;
        this.keyBindsMid = (WIDTH/2.0 - keyBindText.getWidth(KEY_BINDS)/2.0)-SPACE_10PX;

        chooseTank = buyTank.getBoundingBoxAt(tankBuyLoc);
        chooseSuperTank = buySuperTank.getBoundingBoxAt(superTankBuyLoc);
        chooseAirSupport = buyAirPlane.getBoundingBoxAt(airSupportBuyLoc);
        withinBuyPanel = buyPanel.getBoundingBoxAt(buyPanelLoc);
        withinStatusPanel = statusPanel.getBoundingBoxAt(statusPanelLoc);

        this.lives = "Lives: "+ currentHealth;
        this.strTimeScale = "Time Scale: " + (double)getTimescale();

        //Status Panel Rendering Logic
        this.liveWidth = keyBindText.getWidth(lives);
        this.liveTextX = WIDTH-liveWidth-20;
        this.statusTextY = (HEIGHT - statusPanelMidY)+SPACE_5PX;
        this.waveStatusY = (HEIGHT - statusPanelMidY)+SPACE_5PX;
    }

    /**
     * The entry-point for the game
     *
     * @param args Optional command-line arguments
     */
    public static void main(String[] args) {
        new ShadowDefend().run();
    }

    public static int getTimescale() {
        return timescale;
    }

    //Functions below are static to check validity of rendering tower, explosives, etc.
    public static Rectangle buyTankRect(){return chooseTank;}
    public static Rectangle buySuperTanRect(){return chooseSuperTank;}
    public static Rectangle buyAirSupportRect(){return chooseAirSupport;}
    public static int getCurrentFund(){return currentFund;}
    public static Rectangle getBuyPanelRect(){return withinBuyPanel;}
    public static Rectangle getStatusPanelRect(){return withinStatusPanel;}
    public static boolean intersectBuyPanel(Point coordinate ){return withinBuyPanel.intersects(coordinate);}
    public static boolean intersectStatusPanel(Point coordinate ){return withinStatusPanel.intersects(coordinate);}

    /**
     * Increases the timescale
     */
    private void increaseTimescale() {
        if(timescale < MAX_TIMESCALE) {
            timescale++;
        }
    }

    /**
     * Decreases the timescale but doesn't go below the base timescale
     */
    private void decreaseTimescale() {
        if (timescale > MIN_TIMESCALE) {
            timescale--;
        }
    }

    /**
     * Update lives when slicers reaching final destination and close
     * Window when lives are lower than 0
     *
     * @param penalty specific penalty given from finished slicers
     */
    private void updateHealth(int penalty){
        currentHealth -= penalty;
        if(currentHealth <= 0){
            Window.close();
        }
    }

    /**
     * Update current fund when buying a tower
     *
     * @param amount amount of fund used to buy a tower
     */
    private void updateFund(int amount){
        currentFund -= amount;
    }

    /**
     * Update fund when receiving reward
     *
     * @param amount value of reward from a wave
     */
    public static void addRewardToFund(int amount){
        currentFund += amount;
    }

    /**
     * Count Reward of a wave
     *
     * @param waveNum wave number
     */
    private int countWaveReward(int waveNum){return REWARD_CONST1 + (waveNum*REWARD_CONST2);}

    /**
     * Render tower's price and color logic
     *
     * @param price price of a tower
     * @param xPost x position of towers in buying panel
     * @param yPost y position of towers in buying panel
     * @param strPrice String display of price.
     */
    private void renderPrice(int price, double xPost,
                             double yPost, String strPrice){
        if(currentFund < price){
            priceText.drawString(strPrice, xPost, yPost, colour.setBlendColour(Colour.RED));
        }else{
            priceText.drawString(strPrice, xPost, yPost, colour.setBlendColour(Colour.GREEN));
        }
    }

    /**
     * Targeted any slicers within an active tower's radius effect
     */
    private int chooseNonEmptySlicer(Tower t){
        for(int i =0; i<slicers.length;i++){
            if(slicers[i]!=null){
                if(slicers[i].isTargeted(t)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * Reset All tower when starting on next level
     */
    private void resetTower(){
        for(int i = 0; i< tower.length; i++){
            if(tower[i] != null){
                tower[i]= null;
            }
        }
    }

    /**
     * Checking placement validity
     */
    private boolean intersectWithOtherTower(Input input, Tower tower){
        return tower.getRect().intersects(input.getMousePosition());
    }

    /**
     * Guard to check if a bought tower is intersecting with any existing towers
     *
     * @param input clicked mouse input.
     * @param occupiedPosition bounding box of occupied positions.
     */
    private boolean towerIntersection(Input input, Rectangle[] occupiedPosition){
        for(Rectangle p: occupiedPosition){
            if(p!=null){
                if(p.intersects(input.getMousePosition())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * An active tower's attacking logic
     *
     * @param slicer a chosen target.
     * @param name tower name.
     */
    private Enemy activeTowerAttacking(Enemy slicer, String name){
        if (!name.equals(TOWER_PASSIVE)){
            if(slicer.isTerminated()){
                if(slicer.getSlicerType().equals("slicer")) {
                    addRewardToFund(slicer.SlicerReward());
                    slicerDefeated++;
                    slicer = null;
                }
            }
        }
        return slicer;
    }

    /**
     * A passive tower's attacking logic.
     *
     * @param slicer a slicer inside explosion range.
     */
    private Enemy passiveTowerAttacking(Enemy slicer){
        if(slicer.isTerminated()){
            if(slicer.getSlicerType().equals("slicer")) {
                addRewardToFund(slicer.SlicerReward());
                slicerDefeated++;
                slicer = null;
            }
        }
        return slicer;
    }

    /**
     * Guard to check that tower must be placed inside the Window and rendered on unblocked areas.
     *
     * @param input mouse input for tower placement.
     */
    private boolean validPoint(Input input){
        Point point = new Point(input.getMouseX(), input.getMouseY());

        boolean invalidX = point.x < 0 || point.x > WIDTH;
        boolean invalidY = point.y < 0 || point.y > HEIGHT;
        boolean outOfBounds = invalidX || invalidY;
        if (outOfBounds) {
            return false;
        }
        return !map.getPropertyBoolean((int) point.x, (int) point.y, BLOCKED_PROPERTY, false);
    }

    /**
     * Reset all game conditions on the next level
     */
    private void resetConditions(){
        level++;
        if(level <= MAX_LEVEL){
            status = "Awaiting Start";
            currentHealth = INITIAL_HEALTH;
            currentFund = INITIAL_FUND;
            resetTower();
        }else{
            status = "Winner!";
        }
        waveStarted = false;
        passiveMovementCount = 1;
        occupiedPosition = new Rectangle[MAX_DATA];
        occupiedPostIndex = 0;

        //Change level 1 map and polyline to level 2
        map = map2;
        polyline = polyline2;
        eventIndex = 0;

    }

    /**
     * Draw buying panel and its components.
     */
    private void renderBuyPanel(){
        currentMoney = "$"+ currentFund;
        buyPanel.draw(buyPanelLoc.x, buyPanelLoc.y);
        statusPanel.draw(WIDTH / 2.0, HEIGHT - statusPanelMidY);
        buyTank.draw(tankBuyLoc.x, tankBuyLoc.y);
        buySuperTank.draw(superTankBuyLoc.x, superTankBuyLoc.y);
        buyAirPlane.draw(airSupportBuyLoc.x, airSupportBuyLoc.y);

        keyBindText.drawString(KEY_BINDS, keyBindsMid, KEY1_YPOST, colour.setBlendColour(Colour.WHITE));
        keyBindText.drawString(S_KEY, keyBindsMid , KEY2_YPOST, colour.setBlendColour(Colour.WHITE));
        keyBindText.drawString(L_KEY, keyBindsMid, KEY3_YPOST, colour.setBlendColour(Colour.WHITE));
        keyBindText.drawString(K_KEY, keyBindsMid, KEY4_YPOST, colour.setBlendColour(Colour.WHITE));

        moneyText.drawString(currentMoney, FUND_XPOST, FUND_YPOST, colour.setBlendColour(Colour.WHITE));
        renderPrice(FIXED_PRICE1, price1MidX, priceYPost, PRICE1);
        renderPrice(FIXED_PRICE2, price2MidX, priceYPost, PRICE2);
        renderPrice(FIXED_PRICE3, price3MidX, priceYPost, PRICE3);
    }

    /**
     * Render status panel with updated status.
     */
    private void renderStatusPanel(){
        lives = "Lives: "+ currentHealth;
        strTimeScale = "Time Scale: " + (double)getTimescale();
        keyBindText.drawString(lives, liveTextX, statusTextY,
                colour.setBlendColour(Colour.WHITE));

        if(getTimescale()>1.0){
            keyBindText.drawString(strTimeScale, TIMESCALE_XPOST, statusTextY, colour.setBlendColour(Colour.GREEN));
        }else{
            keyBindText.drawString(strTimeScale, TIMESCALE_XPOST, statusTextY, colour.setBlendColour(Colour.WHITE));
        }
        keyBindText.drawString("Status: "+status, STATUS_XPOST, statusTextY,
                colour.setBlendColour(Colour.WHITE));
    }

    /**
     * Tower Selection Logic
     */
    private void createTowerSelection(Input input){
        //Select any available towers.
        if(input.wasPressed(MouseButtons.LEFT) &&
                intersectBuyPanel(input.getMousePosition())&&!isPlacing){
            Point mouseLoc = input.getMousePosition();
            selectTower = new TowerLoader(mouseLoc);
            if(selectTower.validSelection()){
                confirmPurchases();
            }
        }
    }

    /**
     * Confirm a purchased tower logic.
     */
    private void confirmPurchases(){
        if(selectTower.ableToBuyTank()){
            status = "Placing";
            isPlacing = true;
        }
        if(selectTower.ableToBuySuperTank()){
            status = "Placing";
            isPlacing = true;
        }
        if(selectTower.ableToBuyAirSupport()){
            status = "Placing";
            isPlacing = true;
        }
    }
    /**
     * Presenting any valid placement points
     */
    private void showValidPlacementPoint(Input input){
        //Drag Animation of purchased tower Logic

        if (isPlacing && !towerIntersection(input, occupiedPosition)
                && validPoint(input) && !selectTower.intersectWithPanel(input)) {
            selectTower.renderValidPosition(input);
        }
    }

    /**
     * Placing tower Logic
     */
    private void placingTower(Input input) {
        //Placing purchased tower logic
        if (input.wasPressed(MouseButtons.LEFT) && selectTower!=null &&
                !selectTower.intersectWithPanel(input)&&
                !towerIntersection(input, occupiedPosition) &&
                isPlacing && validPoint(input)) {

            Point newMouseLoc = input.getMousePosition();
            tower[towerCount] = selectTower.selectedTower(newMouseLoc, passiveMovementCount);

            if (tower[towerCount].getClass().getName().toLowerCase().equals(TOWER_PASSIVE)) {
                passiveMovementCount++;
            } else {
                occupiedPosition[occupiedPostIndex] = tower[towerCount].getRect();
                occupiedPostIndex++;
            }
            updateFund(tower[towerCount].getPrice());
            towerCount++;

            deselectTower();
            isPlacing = false;
        }
    }

    /**
     * Cancel tower purchase logic
     */
    private void deselectTower(){
        if(waveStarted){
            status = "Wave In Progress";
        }else{
            status = "Awaiting Start";
        }
        isPlacing = false;
    }

    /**
     * Update Placing Status
     */
    private void placingStatus(){
        if(waveStarted){
            if(isPlacing){
                status = "Placing";
            }else{
                status = "Wave in Progress";
            }
        }
    }


    /**
     * Commence attack of both passive and active towers
     */
    private void commenceAttack(){
        //Towers choose its target independently
        for (int t = 0; t<tower.length; t++) {
            for (int s = 0; s < slicers.length; s++) {
                if (slicers[s] != null && tower[t] != null) {
                    String name = tower[t].getClass().getName().toLowerCase();

                    //Updates slicers when attacked by actives towers
                    slicers[s] = activeTowerAttacking(slicers[s], name);
                    if (name.equals(TOWER_PASSIVE)) {

                        // Take out air support from the game when all explosives are dropped.
                        if (tower[t].hasAllExploded()) {
                            tower[t] = null;
                        } else {

                            // Updates slicers when attacked by passive towers
                            slicers[s] = passiveTowerAttacking(slicers[s]);
                        }
                    }
                }
            }
        }
    }

    /**
     * Process and commence a Delay Event logic
     */
    private void commenceDelayEvent(){
        event = new DelayEvent(waveEvents.getEvents()[eventIndex]);
        final double delayTime = event.getDelayTime();
        String wave = "Wave: "+event.getWaveNum();

        keyBindText.drawString(wave, SPACE_5PX, waveStatusY,
                colour.setBlendColour(Colour.WHITE));

        if(isPlacing){
            status = "Placing";
        }else{
            status = "Awaiting Start";
        }

        //Compute the upcoming event after finishing delay
        if(frameCount / FPS >= delayTime/getTimescale()){
            eventIndex ++;
        }
    }

    /**
     * Check for finishing slicers.
     */
    private void updateFinishingSlicers(){
        // Check for slicers that have finished
        for (int i = 0; i < slicers.length; i++) {
            if (slicers[i] != null && slicers[i].isFinished()) {
                updateHealth(slicers[i].SlicerPenalty());
                slicersFinished++;
                slicers[i] = null;
            }
        }
    }

    /**
     * Check for finishing slicers.
     */
    private void spawnNewSlicer(int maxSlicer, double spawnDelay){
        // Check if it is time to spawn a new slicer (and we have some left to spawn)
        if (waveStarted && frameCount / FPS >= spawnDelay && spawnedSlicers != maxSlicer) {
            slicerPath = event.getSlicerName();
            slicers[spawnedSlicers] = new Enemy(polyline, slicerPath);
            spawnedSlicers += 1;

            // Reset frame counter
            frameCount = 0;
        }
    }

    /**
     * Reset slicer status and move to the next event.
     */
    private void commenceNextEvent(){
        addRewardToFund(countWaveReward(event.getWaveNum()));
        eventIndex++;
        slicerDefeated = 0;
        spawnedSlicers = 0;
        slicersFinished = 0;
        frameCount = 0;
    }

    /**
     * Logic to get an event.
     */
    private void getEvents(){
        if (eventIndex < waveEvents.getEvents().length && level <= MAX_LEVEL) {
            event = new Events(waveEvents.getEvents()[eventIndex]);
        }
        if(eventIndex >= waveEvents.getEvents().length){
            if(level <= MAX_LEVEL){
                resetConditions();
            }
        }
    }

    /**
     * Update and draw Spawn status
     *
     * @param wave wave number in String
     */
    private void updateSpawnStatus(String wave){
        keyBindText.drawString(wave, SPACE_5PX, waveStatusY,
                colour.setBlendColour(Colour.WHITE));
        placingStatus();
    }

    /**
     * Control System logic of the game
     *
     * @param input input from keyboard
     */
     private void handlingKeys(Input input){
        if (input.wasPressed(Keys.S)) {
            waveStarted = true;
            status = "Wave in Progress";
        }
        if (input.wasPressed(Keys.L)) {
            increaseTimescale();
        }
        if (input.wasPressed(Keys.K)) {
            decreaseTimescale();
        }
    }

    /**
     * Update Towers throughout the game
     */
    private void updatesAllTower(){
        for (Tower t : tower) {
            if(t != null){
                if(t.getClass().getName().toLowerCase().equals(TOWER_PASSIVE)){
                    t.updatePassiveTower(slicers);
                }else{
                    t.updateActiveTower(slicers[chooseNonEmptySlicer(t)]);
                }
            }
        }
    }

    /**
     * Update Slicers throughout the game
     */
    private void updatesAllSlicers(Input input){
        for (Enemy s : slicers) {
            if (s != null) {
                s.update(input);
            }
        }
    }

    /**
     * Process Spawn event
     *
     * @param input take both keyboard and mouse input.
     */
    private void commenceSpawnEvent(Input input){
        event = new SpawnEvent(waveEvents.getEvents()[eventIndex]);
        String wave = "Wave: "+event.getWaveNum();
        final double spawnDelay = event.getSpawnDelay();
        int maxSlicer = event.getSlicerTotal();
        updateSpawnStatus(wave);
        spawnNewSlicer(maxSlicer, spawnDelay);
        updateFinishingSlicers();

        // Update all moving Slicers.
        updatesAllSlicers(input);

        //Attack in progress.
        commenceAttack();

        //Move to the next event and receive wave reward.
        if(slicerDefeated == maxSlicer || slicersFinished == maxSlicer-slicerDefeated){
            commenceNextEvent();
        }
    }

    /**
     * Update the state of the game, potentially reading from input
     *
     * @param input The current mouse/keyboard state
     */
    @Override
    protected void update(Input input) {
        // Increase the frame counter by the current timescale
        frameCount += getTimescale();
        getEvents();

        // Draw map and panels from the top left of the window
        map.draw(0,0,0,0,WIDTH,HEIGHT);
        renderBuyPanel(); renderStatusPanel();

        //Tower Selection Operations
        createTowerSelection(input); showValidPlacementPoint(input);
        placingTower(input);
        if(input.wasPressed(MouseButtons.RIGHT) && isPlacing){
            deselectTower();
        }

        //Updates all chosen active and passive towers.
        updatesAllTower();

        // Handle key presses
        handlingKeys(input);

        //Slicer Spawn event Operations
        if(event.isSpawnEvent()) {
            commenceSpawnEvent(input);
        }

        //Compute Delay Events and update status
        if(event.isDelayEvent()){
            commenceDelayEvent();
        }else{
            if(event.getWaveNum() > 1){
                placingStatus();
            }
        }
    }
}
