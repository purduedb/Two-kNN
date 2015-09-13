package driver;


import data.FocalPoint;
import exec.DataScan;
import exec.SelectSelect_Conc_Correct;
import exec.SelectSelect_Smart;


public class SelectRunner {

	private static void runTest() {

		int i;
		
		System.out.println("2 Select");
		System.out.println("Starting Execution");

		System.out.println("Reading data points from disk");
		DataScan dataScan = new DataScan(100);
		System.out.println("Done reading from disk");
		
		int factor = 1024;
		
		FocalPoint f1 = new FocalPoint();
		f1.k = 10;
		f1.setFocalPoint(895, 797);
		FocalPoint f2 = new FocalPoint();
		f2.k = f1.k * factor;
		f2.setFocalPoint(0, 400);
		
		dataScan.reset();
		SelectSelect_Conc_Correct selectA = new SelectSelect_Conc_Correct(f1, f2, dataScan);
		
		long time = System.nanoTime();
		i = 0;
		selectA.execute();
//		for (Tuple t : selectA.execute()) {
//			System.out.println(i++ + ", " + t.movingObjectID + ", " + t.timeStamp + ", " + t.xCoord + ", " + t.yCoord);
//		}
		System.out.println("Finished plan A");
		System.out.println("Time = " + (System.nanoTime() - time)/1000000000.0);
		
		// ------------------------------
		
		f1 = new FocalPoint();
		f1.k = 10;
		f1.setFocalPoint(895, 797);
		f2 = new FocalPoint();
		f2.k = f1.k * factor;
		f2.setFocalPoint(0, 400);
		
		dataScan.reset();
		SelectSelect_Smart selectB = new SelectSelect_Smart(f1, f2, dataScan);
		time = System.nanoTime();
		i = 0;

		selectB.execute();
//		for (Tuple t : selectB.execute()) {
//			System.out.println(i++ + ", " + t.movingObjectID + ", " + t.timeStamp + ", " + t.xCoord + ", " + t.yCoord);
//		}
		System.out.println("Finished plan B");
		System.out.println("Time = " + (System.nanoTime() - time)/1000000000.0);
	}

	public static void main(String[] args) {

		runTest();

	}

}
