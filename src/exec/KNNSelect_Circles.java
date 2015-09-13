package exec;


import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class KNNSelect_Circles {

	ArrayList<Tuple> qAnswer;

	public FocalPoint[] focalPoints;
	private DataScan dataScan;

	public KNNSelect_Circles(FocalPoint[] focalPoints, DataScan dataScan){
		this.focalPoints = focalPoints;
		qAnswer = new ArrayList<Tuple>();
		
		this.dataScan = dataScan;
	}

	public ArrayList<Tuple> execute() {

		Tuple tuple;

		boolean toBeAddedToAnswer;

		int kMin = Integer.MAX_VALUE;
		for (FocalPoint fp : focalPoints) {
			if (fp.k < kMin)
				kMin = fp.k;
		}

		while (dataScan.hasNext()) {
			tuple = dataScan.getNext();
			toBeAddedToAnswer = true;
			if (tuple == null)
				break;

			for (FocalPoint fp : focalPoints) {
				if (fp.nearestNeighbours.size() < kMin) {
					fp.nearestNeighbours.add(tuple);	
				}
				else {
					double tupleDist = Math.sqrt(Math.pow(tuple.xCoord - fp.getXCoord(), 2) + Math.pow(tuple.yCoord - fp.getYCoord(), 2));

					if (tupleDist < fp.searchRadius) {

						fp.nearestNeighbours.add(tuple);
						if (fp.nearestNeighbours.size() > fp.k) {
							Tuple removedTuple = fp.nearestNeighbours.remove();
							//if (answer.contains(removedTuple))
							qAnswer.remove(removedTuple);

							Tuple peek = fp.nearestNeighbours.peek();

							fp.searchRadius = Math.sqrt(Math.pow(peek.xCoord - fp.getXCoord(), 2) + Math.pow(peek.yCoord - fp.getYCoord(), 2));

							updateSearchRadii(fp);
						}
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

	private void updateSearchRadii(FocalPoint referenceFP) {

		for (FocalPoint fp: focalPoints) {
			if (fp.k <= referenceFP.k)
				continue;

			double dist = Math.sqrt(Math.pow(referenceFP.getXCoord() - fp.getXCoord(), 2) + Math.pow(referenceFP.getYCoord() - fp.getYCoord(), 2));

			if (dist + referenceFP.searchRadius < fp.searchRadius) {
				//System.out.println(fp.k + "radius changed");
				fp.searchRadius = dist + referenceFP.searchRadius;
			}



			//				double maxDist = Double.MIN_VALUE;
			//				for (Tuple tuple : referenceFP.nearestNeighbours) {
			//					double dist = Math.sqrt(Math.pow(tuple.xCoord - fp.getXCoord(), 2) + Math.pow(tuple.yCoord - fp.getYCoord(), 2));
			//					if (dist > maxDist)
			//						maxDist = dist;
			//				}
			//				
			//				if (maxDist < fp.searchRadius) {
			//					fp.searchRadius = maxDist;
			//				}


			//				double maxDist = fp.searchRadius + referenceFP.searchRadius;
			//				if (maxDist < fp.searchRadius)
			//					fp.searchRadius = maxDist;
		}


	}

}
