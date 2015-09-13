package exec;

import java.util.ArrayList;
import java.util.HashMap;
import data.Tuple;

public class Intersect extends Operator {
	
	public HashMap<Integer, Tuple> outerState;
	public HashMap<Integer, ArrayList<Integer>> innerState;

	public Intersect() {
		//outerState = new HashMap<Integer, Tuple>(); Currently useless but may be beneficial if we use the tuple.declareLastOfK flag
		innerState = new HashMap<Integer, ArrayList<Integer>>();
	}

	@Override
	public void process(Tuple tuple, boolean fromInner) {

		if (fromInner) {
			ArrayList<Integer> KList;
			if (tuple.declareFirstOfK) {
				KList = new ArrayList<Integer>();
				innerState.put(tuple.objID, KList);
			}
			else if (tuple.declareLastOfK) {
				// We can intersect with outer, but since we ignore the update from inner in the left deep plan, we should ignore it here to have a fair comparison
			}
			else {
				KList = innerState.get(tuple.objID);
				KList.add(tuple.secondaryID);
			}
		}
		else {
			// The following line is useless if we use the tuple.declareLastOfK flag to trigger intersection
			//outerState.put(tuple.objID, tuple);

			// Intersect on sec key print for all in the knn list
			if (innerState.containsKey(tuple.secondaryID)) {
				for (int near : innerState.get(tuple.secondaryID)) {
					Tuple outTuple = new Tuple();
					outTuple.objID = tuple.objID;
					outTuple.sign = tuple.sign;
					outTuple.xCoord = tuple.xCoord;
					outTuple.yCoord = tuple.yCoord;
					for (int k : tuple.data)
						outTuple.data.add(k);
					
					outTuple.data.add(near);

					parent.process(outTuple, this.isInner);
				}
			}
		}
	}	

}
