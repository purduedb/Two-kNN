package exec;

import helper.FastKNNFinder;
import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class Select_Join_Conc_Correct {

	private ArrayList<String> qAnswer;

	private FocalPoint focalPoint;  // for Inner

	private DataScan outer;
	private int k;
	
	private FastKNNFinder fastKNNFinder;
	
	public Select_Join_Conc_Correct(FocalPoint focalPoint, DataScan inner, DataScan outer, int k){
		this.focalPoint = focalPoint;

		qAnswer = new ArrayList<String>();

		this.outer = outer;
		this.k = k;
		
		fastKNNFinder = new FastKNNFinder(inner.getData());
	}

	public ArrayList<String> execute() {
		Tuple tuple;

		fastKNNFinder.kNN(focalPoint);

		outer.reset();
		int c = 0;
		while (outer.hasNext()) {
			tuple = outer.getNext();
			c++;
			if (c % 256 != 0)
				continue;
			FocalPoint fp = new FocalPoint();
			fp.k = k;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);

			fastKNNFinder.kNN(fp);
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

}
