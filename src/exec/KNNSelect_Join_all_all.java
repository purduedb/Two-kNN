package exec;


import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class KNNSelect_Join_all_all {

	private ArrayList<String> qAnswer;

	private FocalPoint focalPoint;  // for Inner

	private DataScan inner, outer;
	private int k;

	public KNNSelect_Join_all_all(FocalPoint focalPoint, DataScan inner, DataScan outer, int k){
		this.focalPoint = focalPoint;

		qAnswer = new ArrayList<String>();

		this.inner = inner;
		this.outer = outer;
		this.k = k;
	}

	public ArrayList<String> execute() {
		Tuple tuple;

		getkNN(focalPoint);

		while (outer.hasNext()) {
			tuple = outer.getNext();

			FocalPoint fp = new FocalPoint();
			fp.k = k;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);

			getkNN(fp);
			//System.out.println(fp.nearestNeighbours.size());
			for (Tuple t : fp.nearestNeighbours) {
				if (focalPoint.nearestNeighbours.contains(t)) {
					qAnswer.add(tuple.movingObjectID + "-->" + t.movingObjectID + " - " + t.timeStamp);
					//qAnswer.add(tuple.xCoord + ", " + tuple.yCoord + " --> " + t.xCoord + ", " + t.yCoord);
					//	System.out.println(tuple.xCoord + ", " + tuple.yCoord + " --> " + t.xCoord + ", " + t.yCoord);
				}
			}
		}


		return qAnswer;
	}

	private void getkNN(FocalPoint fp) {
		inner.reset();
		Tuple tuple;
		while (inner.hasNext()) {
			tuple = inner.getNext();
			if (fp.nearestNeighbours.size() < fp.k) {
				fp.nearestNeighbours.add(tuple);
			}
			else {
				double tupleDist = Math.sqrt(Math.pow(tuple.xCoord - fp.getXCoord(), 2) + Math.pow(tuple.yCoord - fp.getYCoord(), 2));
				if (fp.searchRadius > tupleDist) {
					fp.nearestNeighbours.add(tuple);
					fp.nearestNeighbours.remove();
					Tuple peek = fp.nearestNeighbours.peek();
					fp.searchRadius = Math.sqrt(Math.pow(peek.xCoord - fp.getXCoord(), 2) + Math.pow(peek.yCoord - fp.getYCoord(), 2));
				}
			}
		}

		//		System.out.println("FocalPoint is " + focalPoint.getXCoord() + ", " + focalPoint.getYCoord());
		//		for (Tuple t : focalPoint.nearestNeighbours) {
		//			System.out.println(t.xCoord + ", " + t.yCoord);
		//		}
	}

}
