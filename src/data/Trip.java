package data;

import java.util.ArrayList;

public class Trip {
    public int movingObjectID;
    public ArrayList<LocationUpdate> movements;

    public Trip(int moID, ArrayList<LocationUpdate> movements)
    {
        this.movingObjectID = moID;
        this.movements = movements;
    }
}