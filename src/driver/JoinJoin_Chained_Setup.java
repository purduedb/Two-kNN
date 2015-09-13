package driver;

import java.util.ArrayList;
import java.util.Random;


import data.Tuple;
import exec.DataScan;
import exec.Join_join_Chained_Intersection;
import exec.Join_join_Chained_Nested;
import exec.Join_join_Chained_Nested_Cached;


public class JoinJoin_Chained_Setup {

	ArrayList<Tuple> E1, E2, E3;
	
	int k1_2 = 10;
	int k2_3 = 10;
	
	public JoinJoin_Chained_Setup() {
		DataScan dataScan = new DataScan(1000);
		
			
		E1 = new ArrayList<Tuple>();
		E2 = new ArrayList<Tuple>();
		E3 = new ArrayList<Tuple>();
		int i = 0;
		for (Tuple t : dataScan.getData()) {
			i++;
			if (i % 2 != 0)
				continue;
			E1.add(t);
			E2.add(t);
			E3.add(t);
		}
		
		//generateClustersForE1(7000 * 10);
		//addClustersForE3(7000 * 10);
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


	public ArrayList<String> runPlanA() {
		Join_join_Chained_Intersection join_join= new Join_join_Chained_Intersection(E1, E2, E3, k1_2, k2_3);
		return join_join.execute();
	}

	public ArrayList<String> runPlanB() {
		Join_join_Chained_Nested join_join= new Join_join_Chained_Nested(E1, E2, E3, k1_2, k2_3);
		return join_join.execute();
	}
	
	public ArrayList<String> runPlanC() {
		Join_join_Chained_Nested_Cached join_join= new Join_join_Chained_Nested_Cached(E1, E2, E3, k1_2, k2_3);
		return join_join.execute();
	}

}
