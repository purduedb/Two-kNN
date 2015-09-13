package exec;

import helper.FastKNNFinder;
import java.util.ArrayList;
import java.util.HashSet;

import data.FocalPoint;
import data.Tuple;


public class Join_Join_Unchained_Conc_Correct {
	
	private ArrayList<String> qAnswer;

	ArrayList<Tuple> E1, E3;
	private int k1_2, k3_2;
	
	private FastKNNFinder kNNFinder;
	private HashSet<String> E2PointsInAnswer;
	
	public Join_Join_Unchained_Conc_Correct(ArrayList<Tuple> E1, DataScan E2, ArrayList<Tuple> E3, int k1_2, int k3_2){

		qAnswer = new ArrayList<String>();

		this.E1 = E1;
		this.E3 = E3;
		
		this.k1_2 = k1_2;
		this.k3_2 = k3_2;
		
		kNNFinder = new FastKNNFinder(E2.getData());
		E2PointsInAnswer = new HashSet<String>();
	}

	public ArrayList<String> execute() {

		int i = 0;
		int c = 0;
		for (Tuple tuple : E1) {
			//System.out.println(i++);
			FocalPoint fp = new FocalPoint();
			fp.k = k1_2;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);
			kNNFinder.kNN(fp);
			
			for (Tuple e2Tuple : fp.nearestNeighbours) {
				E2PointsInAnswer.add(e2Tuple.movingObjectID + "-" + e2Tuple.timeStamp);
			}
		}
		
		c = 0;
		for (Tuple tuple : E3) {
			c++;
			//if (c % 100 != 0)
				//continue;
			//System.out.println(i++);
			
			FocalPoint fp = new FocalPoint();
			fp.k = k3_2;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);
			kNNFinder.kNN(fp);
			
			for (Tuple e2Tuple : fp.nearestNeighbours) {
				if (E2PointsInAnswer.contains(e2Tuple.movingObjectID + "-" + e2Tuple.timeStamp))
					qAnswer.add(tuple.movingObjectID + "-->" + e2Tuple.movingObjectID + " - " + e2Tuple.timeStamp);
			}
		}
		

		return qAnswer;
	}

}
