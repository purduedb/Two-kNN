package helper;

import java.util.Comparator;

import data.Tuple;

public class TupleComparer implements Comparator<Tuple>{

	private double referenceX, referenceY;
	
	public TupleComparer(double xCoord, double yCoord) {
		this.referenceX = xCoord;
		this.referenceY = yCoord;
	}

	@Override
	public int compare(Tuple a, Tuple b) {
		
		double aDist = Math.sqrt(Math.pow(a.xCoord - referenceX, 2) + Math.pow(a.yCoord - referenceY, 2));
		double bDist = Math.sqrt(Math.pow(b.xCoord - referenceX, 2) + Math.pow(b.yCoord - referenceY, 2));
		
		if (aDist > bDist)
			return -1;
		if (aDist < bDist)
			return 1;
		
		return 0;
	}

}
