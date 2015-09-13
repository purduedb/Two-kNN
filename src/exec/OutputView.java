package exec;

import java.util.ArrayList;

import data.Tuple;

public class OutputView extends Operator {
	
	public ArrayList<Tuple> allTuples;
		
	public OutputView() {
		allTuples = new ArrayList<Tuple>();
	}

	@Override
	public void process(Tuple tuple, boolean fromInner) {
		//System.out.println(tuple.sign + "" + tuple.objID);
		//allTuples.add(tuple);	
	}

}
