package exec;


import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class KNNSelect_Heap {

	ArrayList<Tuple> qAnswer;

	public FocalPoint[] focalPoints;
	DataScan dataScan;

	public KNNSelect_Heap(FocalPoint[] focalPoints, DataScan dataScan){
		this.focalPoints = focalPoints;
		qAnswer = new ArrayList<Tuple>();
		
		this.dataScan = dataScan;
	}

	public ArrayList<Tuple> execute() {

		Tuple tuple;

		while (dataScan.hasNext()) {
			tuple = dataScan.getNext();
			if (tuple == null)
				break;

			for (FocalPoint fp : focalPoints) {
				if (fp.nearestNeighbours.size() < fp.k) {
					fp.nearestNeighbours.add(tuple);	
				}
				else {			
					fp.nearestNeighbours.add(tuple);
					Tuple removedTuple = fp.nearestNeighbours.remove();
					qAnswer.remove(removedTuple);
				}
			}
		}
		
		for (Tuple t : focalPoints[0].nearestNeighbours) {
			boolean belongsToIntersection = true;
			for (FocalPoint fp : focalPoints) {
				if (!fp.nearestNeighbours.contains(t)){
					belongsToIntersection = false;
				}
			}
			if (belongsToIntersection)
				qAnswer.add(t);
		}

		return qAnswer;
	}

}
