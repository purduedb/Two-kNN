package exec;

import data.Tuple;

public abstract class Operator {

	public Operator inner;
	public Operator outer;

	public Operator parent;
	
	public boolean isInner;
	
	public abstract void process(Tuple tuple, boolean fromInner);
	
}
