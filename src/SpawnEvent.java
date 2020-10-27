public class SpawnEvent extends Events {
    private final int waveNum;
    private int SlicerTotal;
    private final String SlicerName;
    private final double spawnDelay;

    /**
     * Create parameters from a line of an event
     *
     * @param eventLine a line of spawn event information
     */
    public SpawnEvent(String eventLine){
        super(eventLine);
        this.waveNum = Integer.parseInt(getSplitInfo()[0]);
        this.SlicerTotal = Integer.parseInt(getSplitInfo()[2]);
        this.SlicerName = getSplitInfo()[3];
        this.spawnDelay = Double.parseDouble(getSplitInfo()[4])/1000;
    }


    public int getWaveNum(){return waveNum;}
    public int getSlicerTotal(){return SlicerTotal;}
    public String getSlicerName(){return SlicerName;}
    public double getSpawnDelay(){return spawnDelay;}

}
