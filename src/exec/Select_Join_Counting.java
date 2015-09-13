package exec;


import helper.FastKNNFinder;
import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class Select_Join_Counting {

	private boolean[][] visited;
	private ArrayList<String> qAnswer;
	
	private FocalPoint focalPoint;  // for Inner
	
	private DataScan outer;
	private int k;
	private FastKNNFinder fastKNNFinder;

	public Select_Join_Counting(FocalPoint focalPoint, DataScan inner, DataScan outer, int k) {
		this.focalPoint = focalPoint;
		
		qAnswer = new ArrayList<String>();

		this.outer = outer;
		this.k = k;
		
		fastKNNFinder = new FastKNNFinder(inner.getData());
		
		visited = new boolean[fastKNNFinder.getNumRows()][fastKNNFinder.getNumColumns()];
	}

	public ArrayList<String> execute() {
		
		Tuple tuple;

		fastKNNFinder.kNN(focalPoint);
		
		outer.reset();
		int c = 0;
		while (outer.hasNext()) {
			tuple = outer.getNext();
			c++;
			if (c % 256 != 0)
				continue;
			
			FocalPoint fp = new FocalPoint();
			fp.k = k;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);

			double radius = getRadius(tuple);
			int gridXIndex = (int)((tuple.xCoord - fastKNNFinder.getMinX()) / fastKNNFinder.getMapWidth() * fastKNNFinder.getNumRows());
			if (gridXIndex == fastKNNFinder.getNumRows()) gridXIndex--;
			int gridYIndex = (int)((tuple.yCoord - fastKNNFinder.getMinY()) / fastKNNFinder.getMapHeight() * fastKNNFinder.getNumColumns());
			if (gridYIndex == fastKNNFinder.getNumColumns()) gridYIndex--;
			
			for (int i = 0; i < fastKNNFinder.getNumRows(); i++)
				for (int j = 0; j < fastKNNFinder.getNumColumns(); j++)
					visited[i][j] = false;
			
			int count = countWithinRadius(tuple, radius, gridXIndex, gridYIndex);
			if (count > k)
				continue;
			
			
			fastKNNFinder.kNN(fp);
			//System.out.println(fp.nearestNeighbours.size());
			for (Tuple t : fp.nearestNeighbours) {
				if (focalPoint.nearestNeighbours.contains(t)) {
					qAnswer.add(tuple.movingObjectID + "-->" + t.movingObjectID + " - " + t.timeStamp);
					//qAnswer.add(tuple.xCoord + ", " + tuple.yCoord + " --> " + t.xCoord + ", " + t.yCoord);
					//	System.out.println(tuple.xCoord + ", " + tuple.yCoord + " --> " + t.xCoord + ", " + t.yCoord);
				}
			}
		}

		return qAnswer;
	}
	
	private int countWithinRadius(Tuple outerPoint, double radius, int gridXIndex, int gridYIndex) {
		
		if (gridXIndex < 0 || gridXIndex >= fastKNNFinder.getNumRows())
			return 0;
		if (gridYIndex < 0 || gridYIndex >= fastKNNFinder.getNumColumns())
			return 0;

		if (visited[gridXIndex][gridYIndex])
			return 0;
		visited[gridXIndex][gridYIndex] = true;
		
		
		FocalPoint fp = new FocalPoint();
		fp.k = k;
		fp.setFocalPoint(outerPoint.xCoord, outerPoint.yCoord);
		if (fastKNNFinder.getMaxDist(fp, gridXIndex, gridYIndex) > radius)
			return 0;
				
		int countInCell = fastKNNFinder.getPointCountInCell(gridXIndex,gridYIndex);
		if (countInCell > k) return countInCell;
			
		int c1 = countWithinRadius(outerPoint, radius, gridXIndex+1, gridYIndex);
		if (c1+countInCell > k) return c1+countInCell;
		int c2 = countWithinRadius(outerPoint, radius, gridXIndex-1, gridYIndex);
		if (c1+c2+countInCell > k) return c1+c2+countInCell;
		int c3 = countWithinRadius(outerPoint, radius, gridXIndex, gridYIndex+1);
		if (c1+c2+c3+countInCell > k) return c1+c2+c3+countInCell;
		int c4 = countWithinRadius(outerPoint, radius, gridXIndex, gridYIndex-1);
		if (c1+c2+c3+c4+countInCell > k) return c1+c2+c3+c4+countInCell;
		int c5 = countWithinRadius(outerPoint, radius, gridXIndex+1, gridYIndex+1);
		if (c1+c2+c3+c4+c5+countInCell > k) return c1+c2+c3+c4+c5+countInCell;
		int c6 = countWithinRadius(outerPoint, radius, gridXIndex+1, gridYIndex-1);
		if (c1+c2+c3+c4+c5+c6+countInCell > k) return c1+c2+c3+c4+c5+c6+countInCell;
		int c7 = countWithinRadius(outerPoint, radius, gridXIndex-1, gridYIndex-1);
		if (c1+c2+c3+c4+c5+c6+c7+countInCell > k) return c1+c2+c3+c4+c5+c6+c7+countInCell;
		int c8 = countWithinRadius(outerPoint, radius, gridXIndex-1, gridYIndex+1);
		if (c1+c2+c3+c4+c5+c6+c7+c8+countInCell > k) return c1+c2+c3+c4+c5+c6+c7+c8+countInCell;
		
		return countInCell + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8;
		
	}

	private double getRadius(Tuple tuple) {
		double minDist = Double.MAX_VALUE;
		for (Tuple t : focalPoint.nearestNeighbours) {
			double tupleDist = Math.sqrt(Math.pow(tuple.xCoord - t.xCoord, 2) + Math.pow(tuple.yCoord - t.yCoord, 2));
			if (tupleDist < minDist) {
				minDist = tupleDist;
			}
		}
		return minDist;
	}
	
}
