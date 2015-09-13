package driver;

import java.util.ArrayList;
import java.util.Random;


import data.Tuple;
import exec.Join_Join_Unchained_BlockMarking;
import exec.DataScan;
import exec.Join_Join_Unchained_Conc_Correct;


public class JoinJoin_UnChained_Setup {

	private DataScan E2;
	ArrayList<Tuple> E1, E3;
	
	int k1_2 = 70;
	int k3_2 = 50;
	
	public JoinJoin_UnChained_Setup() {
		E2 = new DataScan(1000);
		
		generateClustersForE1(1000);
		
		E3 = new ArrayList<Tuple>();
		int i = 0;
		for (Tuple t : E2.getData()) {
			i++;
			if (i % 25 != 0)
				continue;
			E3.add(t);
		}
		
		//addClustersForE3(7000 * 1);
	}
	
	public void generateClustersForE1(int numPoints) {
		E1 = new ArrayList<Tuple>();
		System.out.println("Generating Clusters for E1");
		Random rand = new Random();
		for (int i = 0; i < numPoints; i++) {
			Tuple tuple = new Tuple();
			boolean sign = rand.nextBoolean();
			int negative = sign? 1 : -1;
			tuple.xCoord = negative * rand.nextDouble() * 500;
			tuple.xCoord = negative * rand.nextDouble() * 500;
			E1.add(tuple);
		}	
	}
	
	public void addClustersForE3(int numPoints) {
		int referenceX = 10000;
		int referenceY = 10000;
		System.out.println("Generating Clusters for E3");
		Random rand = new Random();
		for (int i = 0; i < numPoints; i++) {
			Tuple tuple = new Tuple();
			boolean sign = rand.nextBoolean();
			int negative = sign? 1 : -1;
			tuple.xCoord = referenceX + negative * rand.nextDouble() * 500;
			tuple.xCoord = referenceY + negative * rand.nextDouble() * 500;
			E3.add(tuple);
		}	
	}


	public ArrayList<String> runPlanAB_First_ConcCorrect() {
		Join_Join_Unchained_Conc_Correct join_join= new Join_Join_Unchained_Conc_Correct(E1, E2, E3, k1_2, k3_2);
		return join_join.execute();
	}

	public ArrayList<String> runPlanAB_First_BlockMarking() {
		Join_Join_Unchained_BlockMarking join_join= new Join_Join_Unchained_BlockMarking(E1, E2, E3, k1_2, k3_2);
		return join_join.execute();
	}

	
	
	public ArrayList<String> runPlanCB_First_ConcCorrect() {
		Join_Join_Unchained_Conc_Correct join_join= new Join_Join_Unchained_Conc_Correct(E3, E2, E1, k3_2, k1_2);
		return join_join.execute();
	}
	
	public ArrayList<String> runPlanCB_First_BlockMarking() {
		Join_Join_Unchained_BlockMarking join_join= new Join_Join_Unchained_BlockMarking(E3, E2, E1, k3_2, k1_2);
		return join_join.execute();
	}

}
