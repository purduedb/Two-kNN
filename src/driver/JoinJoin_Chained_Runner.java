package driver;

public class JoinJoin_Chained_Runner {

	private static void runTest() {

		int i;
		

		System.out.println("Starting Execution Join Join Chained");

		System.out.println("Reading data points from disk");
		//JoinJoin_Chained_Setup setup = new JoinJoin_Chained_Setup();
		JoinJoin_Chained_Setup_Clustered setup = new JoinJoin_Chained_Setup_Clustered();
		System.out.println("Done reading from disk");
		

		long time = System.nanoTime();
		
		i = 0;
		setup.runPlanA();
//		for (String s : setup.runPlanA()) {
//			System.out.println(i++ + ", " + s);
//		}
		System.out.println("Finished plan A");
		System.out.println("Time = " + (System.nanoTime() - time)/1000000000.0);
		
		//*********----------***********

		time = System.nanoTime();
		i = 0;
		//setup.runPlanB();
//		for (String s : setup.runPlanB()) {
//			System.out.println(i++ + ", " + s);
//		}
		System.out.println("Finished plan B");
		System.out.println("Time = " + (System.nanoTime() - time)/1000000000.0);
		
		//*********----------***********

		time = System.nanoTime();
		i = 0;
		setup.runPlanC();
//		for (String s : setup.runPlanC()) {
//			System.out.println(i++ + ", " + s);
//		}
		System.out.println("Finished plan C");
		System.out.println("Time = " + (System.nanoTime() - time)/1000000000.0);

	}

	public static void main(String[] args) {

		runTest();

	}

}
