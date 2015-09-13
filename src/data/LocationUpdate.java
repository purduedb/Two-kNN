package data;

public class LocationUpdate {
	
	public double xCoord, yCoord;
    public long time;

    public LocationUpdate() {
    	
    }
    
    public LocationUpdate(double x, double y, long time) {
        this.xCoord = x;
        this.yCoord = y;
        this.time = time;
    }

}
