package driver;

public class JoinJoin_UnChained_Runner {

	private static void runTest() {

		int i;
		
		Runtime r = Runtime.getRuntime();
		System.out.println("Starting Execution Join Join Unchained");

		System.out.println("Reading data points from disk");
		JoinJoin_UnChained_Setup setup = new JoinJoin_UnChained_Setup();
		System.out.println("Done reading from disk");
		
		//*********----------***********
		r.gc();
		long time = System.nanoTime();
		
		i = 0;
		setup.runPlanAB_First_ConcCorrect();
//		for (String s : setup.runPlanA()) {
//			System.out.println(i++ + ", " + s);
//		}
		System.out.println("Finished plan AB ConcCorrect");
		System.out.println("Time = " + (System.nanoTime() - time)/1000000000.0);
		
		//*********----------***********
		r.gc();
		time = System.nanoTime();
		i = 0;
		setup.runPlanAB_First_BlockMarking();
//		for (String s : setup.runPlanB()) {
//			System.out.println(i++ + ", " + s);
//		}
		System.out.println("Finished plan AB");
		System.out.println("Time = " + (System.nanoTime() - time)/1000000000.0);
		

		
		r.gc();
		time = System.nanoTime();
		i = 0;
		setup.runPlanCB_First_BlockMarking();
//		for (String s : setup.runPlanB()) {
//			System.out.println(i++ + ", " + s);
//		}
		System.out.println("Finished plan CB");
		System.out.println("Time = " + (System.nanoTime() - time)/1000000000.0);

	}

	public static void main(String[] args) {

		runTest();

	}

}
