package exec;


import helper.FastKNNFinder;

import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class SelectSelect_Conc_Correct {

	ArrayList<Tuple> qAnswer;

	private FocalPoint f1, f2;
	private FastKNNFinder kNNFinder; 
	
	public SelectSelect_Conc_Correct (FocalPoint f1, FocalPoint f2, DataScan dataScan) {
		this.f1 = f1;
		this.f2 = f2;
		qAnswer = new ArrayList<Tuple>();
				
		this.kNNFinder = new FastKNNFinder(dataScan.getData());
	}

	public ArrayList<Tuple> execute() {

		kNNFinder.kNN(f1);
	
		long time = System.nanoTime();
		kNNFinder.kNN(f2);
		System.out.println("Time Conc_Correct= " + (System.nanoTime() - time)/1000000000.0);

		for (Tuple t : f1.nearestNeighbours) {
			if (f2.nearestNeighbours.contains(t)) 
				qAnswer.add(t);
		}

		return qAnswer;
	}
	
}
