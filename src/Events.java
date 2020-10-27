//Parent class which distinguish between spawn and delay event.
public class Events{
    private static final int SPAWN_INFO_LENGTH = 5;
    private static final int DELAY_INFO_LENGTH = 3;
    private String[] splitInfo;

    /**
     * Split a line of information
     *
     * @param eventLine a line of information of an event
     */
    public Events(String eventLine){
        this.splitInfo = eventLine.split(",");
    }

    public boolean isSpawnEvent(){return splitInfo.length == SPAWN_INFO_LENGTH;}
    public boolean isDelayEvent(){return splitInfo.length == DELAY_INFO_LENGTH;}

    public String[] getSplitInfo(){return splitInfo;}

    public int getWaveNum(){return 0;}
    public int getSlicerTotal(){return 0;}
    public String getSlicerName(){return null;}
    public double getSpawnDelay(){return 0.0;}
    public double getDelayTime(){return 0.0;}

}
