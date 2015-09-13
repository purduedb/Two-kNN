package exec;


import helper.FastKNNFinder;

import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class SelectSelect_Smart {

	ArrayList<Tuple> qAnswer;

	private FocalPoint f1, f2;

	private FastKNNFinder kNNFinder;
	
	public SelectSelect_Smart (FocalPoint f1, FocalPoint f2, DataScan dataScan) {
		this.f1 = f1;
		this.f2 = f2;
		qAnswer = new ArrayList<Tuple>();
		
		kNNFinder = new FastKNNFinder(dataScan.getData());
	}

	public ArrayList<Tuple> execute() {

		kNNFinder.kNN(f1);
		
		double searchThreshold = f1.searchRadius + Math.sqrt(Math.pow(f1.getXCoord() - f2.getXCoord(), 2) + Math.pow(f1.getYCoord() - f2.getYCoord(), 2));
	
		long time = System.nanoTime();
		kNNFinder.kNN(f2, searchThreshold);
		System.out.println("Time Smart= " + (System.nanoTime() - time)/1000000000.0);

		for (Tuple t : f1.nearestNeighbours) {
			if (f2.nearestNeighbours.contains(t)) 
				qAnswer.add(t);
		}

		return qAnswer;
	}

}
