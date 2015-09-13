package driver;

import java.util.ArrayList;


import data.FocalPoint;
import data.Tuple;
import exec.DataScan;
import exec.KNNSelect_Conc_Correct;
import exec.KNNSelect_Smart;

public class SelectSetup {

	private FocalPoint[] focalPoints;

	DataScan dataScan;
	
	public SelectSetup(int numFocalPoints) {
		this.focalPoints = new FocalPoint[numFocalPoints];
		
		dataScan = new DataScan();
	}

	private void setFocalPoints(int numFocalPoints) {
		
		int factor = 1000000;
		
		focalPoints[0] = new FocalPoint();
		focalPoints[0].k = 10;
		focalPoints[0].setFocalPoint(1195, 1297);

		focalPoints[1] = new FocalPoint();
		focalPoints[1].k = 1 * factor;
		focalPoints[1].setFocalPoint(1145, 1257);

//		focalPoints[2] = new FocalPoint();
//		focalPoints[2].k = 2 * factor;
//		focalPoints[2].setFocalPoint(1105, 1207);
//
//		focalPoints[3] = new FocalPoint();
//		focalPoints[3].k = 3 * factor;
//		focalPoints[3].setFocalPoint(1095, 1197);
//
//		focalPoints[4] = new FocalPoint();
//		focalPoints[4].k = 4 * factor;
//		focalPoints[4].setFocalPoint(1045, 1157);
//
//		focalPoints[5] = new FocalPoint();
//		focalPoints[5].k = 5 * factor;
//		focalPoints[5].setFocalPoint(1005, 1117);
//
//		focalPoints[6] = new FocalPoint();
//		focalPoints[6].k = 6 * factor;
//		focalPoints[6].setFocalPoint(995, 1097);
//
//		focalPoints[7] = new FocalPoint();
//		focalPoints[7].k = 7 * factor;
//		focalPoints[7].setFocalPoint(945, 1057);
//
//		focalPoints[8] = new FocalPoint();
//		focalPoints[8].k = 8 * factor;
//		focalPoints[8].setFocalPoint(905, 1007);
//
//		focalPoints[9] = new FocalPoint();
//		focalPoints[9].k = 9 * factor;
//		focalPoints[9].setFocalPoint(895, 987);
	}


	public ArrayList<Tuple> runPlanA() {

		setFocalPoints(focalPoints.length);

		dataScan.reset();
		
		KNNSelect_Conc_Correct select = new KNNSelect_Conc_Correct(focalPoints[0], focalPoints[1], dataScan);

		return select.execute();
	}

	public ArrayList<Tuple> runPlanB() {

		setFocalPoints(focalPoints.length);

		dataScan.reset();
		
		KNNSelect_Smart select = new KNNSelect_Smart(focalPoints[0], focalPoints[1], dataScan);

		return select.execute();
	}

}
