package exec;

import helper.FastKNNFinder;
import java.util.ArrayList;
import java.util.HashSet;

import data.FocalPoint;
import data.Tuple;


public class Join_join_Chained_Intersection {
	
	private ArrayList<String> qAnswer;

	ArrayList<Tuple> E1, E2, E3;
	private int k1_2, k2_3;
	
	private FastKNNFinder kNNFinder1_2, kNNFinder2_3;
	private HashSet<String> j1_2Points; // Points of E2 in the join
	
	public Join_join_Chained_Intersection(ArrayList<Tuple> E1, ArrayList<Tuple> E2, ArrayList<Tuple> E3, int k1_2, int k2_3){

		qAnswer = new ArrayList<String>();

		this.E1 = E1;
		this.E2 = E2;
		this.E3 = E3;
		
		this.k1_2 = k1_2;
		this.k2_3 = k2_3;
		
		kNNFinder1_2 = new FastKNNFinder(E2);
		kNNFinder2_3 = new FastKNNFinder(E3);
		j1_2Points = new HashSet<String>();
	}

	public ArrayList<String> execute() {

		for (Tuple tuple : E1) {
			FocalPoint fp = new FocalPoint();
			fp.k = k1_2;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);
			kNNFinder1_2.kNN(fp);
			
			for (Tuple e2Tuple : fp.nearestNeighbours) {
				j1_2Points.add(e2Tuple.movingObjectID + "-" + e2Tuple.timeStamp);
			}
		}
		

		for (Tuple tuple : E2) {
			FocalPoint fp = new FocalPoint();
			fp.k = k2_3;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);
			kNNFinder2_3.kNN(fp);
			
			if (j1_2Points.contains(tuple.movingObjectID + "-" + tuple.timeStamp)) {
				for (Tuple e3Tuple : fp.nearestNeighbours) {
					qAnswer.add(tuple.movingObjectID + "-->" + e3Tuple.movingObjectID + " - " + e3Tuple.timeStamp);
				}
			}
		}
		

		return qAnswer;
	}

}
