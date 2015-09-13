package exec;

import helper.TupleComparer;

import java.util.Comparator;
import java.util.PriorityQueue;

import data.Tuple;

public class KNNJoin extends Operator {

	public int k;

	public KNNJoin(int k) {
		this.k = k;
	}

	@Override
	public void process(Tuple tuple, boolean fromInner) {
		if (fromInner)
			return;

		// else: (from Outer)

		Tuple outTuple = new Tuple();
		// First, clear the k list of the focal point. Useful in the plan intersection only. Only for the inner.
		if (parent instanceof Intersect) {
			outTuple.objID = tuple.objID;
			outTuple.declareFirstOfK = true;
			parent.process(outTuple, this.isInner);
		}

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

		// Loop on the knn and forward to parent
		int i = 0;
		while (!queue.isEmpty() && i++ < k) {
			Tuple near = queue.remove();

			outTuple = new Tuple();

			// For intersection
			outTuple.secondaryID = near.objID;

			// Copy the location from the near (secondary relation)
			outTuple.xCoord = near.xCoord;
			outTuple.yCoord = near.yCoord;

			// Copy the data
			outTuple.objID = tuple.objID;
			outTuple.sign = tuple.sign;
			for (int k : tuple.data)
				outTuple.data.add(k);

			outTuple.data.add(near.objID);

			parent.process(outTuple, this.isInner);
		}

		// This will declare the end of k submissions to parent. Useful only in intersection plans
		if (parent instanceof Intersect) {

			outTuple = new Tuple();
			outTuple.objID = tuple.objID;
			outTuple.declareLastOfK = true;
			parent.process(outTuple, this.isInner);
		}
	}
}
