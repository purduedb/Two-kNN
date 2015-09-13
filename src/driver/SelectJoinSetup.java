package driver;

import java.util.ArrayList;


import data.FocalPoint;
import exec.DataScan;
import exec.Select_Join_Counting;
import exec.Select_Join_BlockMarking;
import exec.Select_Join_Conc_Correct;


public class SelectJoinSetup {

	private FocalPoint focalPoint;

	private DataScan inner;
	private DataScan outer;
	
	public SelectJoinSetup() {
		
		inner = new DataScan(1000);
		outer = new DataScan(1000);
		
		focalPoint = new FocalPoint();
		focalPoint.k = 1000;
		focalPoint.setFocalPoint(1195, 1297);
	}


	public ArrayList<String> runPlanA() {
		focalPoint.nearestNeighbours.clear();
		Select_Join_Conc_Correct selJoin = new Select_Join_Conc_Correct(focalPoint, inner, outer, 20);
		return selJoin.execute();
	}

	public ArrayList<String> runPlanB() {
		focalPoint.nearestNeighbours.clear();
		Select_Join_Counting selJoin = new Select_Join_Counting(focalPoint, inner, outer, 20);
		return selJoin.execute();
	}

	public ArrayList<String> runPlanC() {
		focalPoint.nearestNeighbours.clear();
		Select_Join_BlockMarking selJoin = new Select_Join_BlockMarking(focalPoint, inner, outer, 20);
		return selJoin.execute();
	}



}
