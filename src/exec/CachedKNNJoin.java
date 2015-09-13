package exec;

import helper.TupleComparer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import data.Tuple;

public class CachedKNNJoin extends Operator {

	public int k;

	public HashMap<Integer, ArrayList<Integer>> cache;

	public CachedKNNJoin(int k) {
		this.k = k;
		cache = new HashMap<Integer, ArrayList<Integer>>();
	}

	@Override
	public void process(Tuple tuple, boolean fromInner) {
		if (fromInner)
			return;

		// else: (from Outer)

		ArrayList<Integer> KList = cache.get(tuple.secondaryID);
		if (KList == null) {
			// Get the KNN List
			Comparator<Tuple> comparer = new TupleComparer(tuple);
			PriorityQueue<Tuple> queue = new PriorityQueue<Tuple> (k, comparer);

			for (Tuple t : ((Tagger)inner).state.values()) {
				if (queue.size() < k)
					queue.add(t);
				else {
					double tDist = Math.sqrt(Math.pow(t.xCoord - tuple.xCoord, 2) + Math.pow(t.yCoord - tuple.yCoord, 2));
					Tuple max = queue.peek();
					double maxDist = Math.sqrt(Math.pow(max.xCoord - tuple.xCoord, 2) + Math.pow(max.yCoord - tuple.yCoord, 2));
					
					if (tDist < maxDist) {
						queue.add(t);
						queue.remove();
					}
				}
			}

			KList = new ArrayList<Integer>();
			// Loop on the knn and forward to parent
			int i = 0;
			while (!queue.isEmpty() && i++ < k) {
				Tuple near = queue.remove();
				KList.add(near.objID);
			}

			cache.put(tuple.secondaryID, KList);
		}

		Tuple outTuple;
		for (int near : KList) {
			outTuple = new Tuple();
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
