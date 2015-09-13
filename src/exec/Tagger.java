package exec;

import java.util.HashMap;

import data.Tuple;

public class Tagger extends Operator {

	public HashMap<Integer, Tuple> state; // ID, timeStamp

	public Operator unckle;
	public boolean isInnerToUnckle;
	
	public Tagger() {
		state = new HashMap<Integer, Tuple>();
	}

	@Override
	public void process(Tuple tuple, boolean fromInner) {

		if (state.containsKey(tuple.objID))
			tuple.sign = Tuple.Sign.UPDATE;
		else 
			tuple.sign = Tuple.Sign.POSITIVE;
		state.put(tuple.objID, tuple);
		
		if (unckle != null)
			unckle.process(tuple, isInnerToUnckle);
		
		parent.process(tuple, isInner);
	}	

}
