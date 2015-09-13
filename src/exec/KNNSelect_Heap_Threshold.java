package exec;


import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class KNNSelect_Heap_Threshold {

	ArrayList<Tuple> qAnswer;

	public FocalPoint[] focalPoints;
	private DataScan dataScan;

	public KNNSelect_Heap_Threshold(FocalPoint[] focalPoints, DataScan dataScan){
		this.focalPoints = focalPoints;
		qAnswer = new ArrayList<Tuple>();
		
		this.dataScan = dataScan;
	}

	public ArrayList<Tuple> execute() {

		Tuple tuple;

		boolean toBeAddedToAnswer;

		while (dataScan.hasNext()) {
			tuple = dataScan.getNext();
			toBeAddedToAnswer = true;
			if (tuple == null)
				break;

			for (FocalPoint fp : focalPoints) {
				if (fp.nearestNeighbours.size() < fp.k) {
					fp.nearestNeighbours.add(tuple);	
				}
				else {					
					double tupleDist = Math.sqrt(Math.pow(tuple.xCoord - fp.getXCoord(), 2) + Math.pow(tuple.yCoord - fp.getYCoord(), 2));
					
					if (tupleDist < fp.searchRadius) {

						fp.nearestNeighbours.add(tuple);
						Tuple removedTuple = fp.nearestNeighbours.remove();
						qAnswer.remove(removedTuple);

						Tuple peek = fp.nearestNeighbours.peek();

						fp.searchRadius = Math.sqrt(Math.pow(peek.xCoord - fp.getXCoord(), 2) + Math.pow(peek.yCoord - fp.getYCoord(), 2));
					}
					else
						toBeAddedToAnswer = false;
				}
			}

			if (toBeAddedToAnswer)
				qAnswer.add(tuple);
		}

		return qAnswer;
	}

}
