package exec;


import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class KNNSelect_Naiive {

	ArrayList<Tuple> qAnswer;

	public FocalPoint[] focalPoints;

	public KNNSelect_Naiive(FocalPoint[] focalPoints){
		this.focalPoints = focalPoints;
		qAnswer = new ArrayList<Tuple>();
	}

	public ArrayList<Tuple> execute() {

		Tuple tuple;

		DataScan scanner = new DataScan();

		for (FocalPoint fp : focalPoints) {
			scanner.reset();

			System.out.println("Reading data file for focal point " + fp);
			while (true) {
				tuple = scanner.getNext();
				if (tuple == null)
					break;

				if (fp.nearestNeighbours.size() < fp.k) {
					fp.nearestNeighbours.add(tuple);	
				}
				else {					
					fp.nearestNeighbours.add(tuple);
					fp.nearestNeighbours.remove();
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
