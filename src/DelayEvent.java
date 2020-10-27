public class DelayEvent extends Events{
    private int waveNum;
    private double delayTime;

    /**
     * Split, store and convert all string parameter from delay event
     *
     * @param eventLine line of delay event
     */
    public DelayEvent(String eventLine){
        super(eventLine);
        if(isDelayEvent()) {
            this.waveNum = Integer.parseInt(getSplitInfo()[0]);
            this.delayTime = Double.parseDouble(getSplitInfo()[2])/1000;
        }
    }
    public int getWaveNum(){return waveNum;}
    public double getDelayTime(){return delayTime;}

}
