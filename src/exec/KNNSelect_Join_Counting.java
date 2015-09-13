package exec;


import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class KNNSelect_Join_Counting {

	private int[][] grid;
	private double minX, minY, maxX, maxY;
	private int numRows = 10;
	private int numColumns = 10;

	private ArrayList<String> qAnswer;
	

	private FocalPoint focalPoint;  // for Inner
	
	private DataScan inner, outer;
	private int k;
	
	double gridCellWidth, gridCellHeight;
	double mapWidth, mapHeight;
	
	boolean[][] visited;

	public KNNSelect_Join_Counting(FocalPoint focalPoint, DataScan inner, DataScan outer, int k){
		this.focalPoint = focalPoint;
		
		qAnswer = new ArrayList<String>();

		this.inner = inner;
		this.outer = outer;

		grid = new int[numRows][numColumns];
		this.k = k;
	}

	public ArrayList<String> execute() {
		
		Tuple tuple;

		setMapBoundries();
		setGridCounts();
		getkNN(focalPoint);
		outer.reset();
		
		int i = 0;
		while (outer.hasNext()) {
			//if (i % 100 == 0)
			//System.out.println(i);
			i++;
			tuple = outer.getNext();
			if (i % 50 != 0)
				continue;
			FocalPoint fp = new FocalPoint();
			fp.k = k;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);

			double radius = getRadius(tuple);
			int gridXIndex = (int)((tuple.xCoord - minX) / mapWidth * numRows);
			if (gridXIndex == numRows) gridXIndex--;
			int gridYIndex = (int)((tuple.yCoord - minY) / mapHeight * numColumns);
			if (gridYIndex == numColumns) gridYIndex--;
			
			visited = new boolean[numRows][numColumns];
			int count = countWithinRadius(tuple, radius, gridXIndex, gridYIndex);
			if (count > k)
				continue;
			
			
			getkNN(fp);
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
		
		if (gridXIndex < 0 || gridXIndex >= numRows)
			return 0;
		if (gridYIndex < 0 || gridYIndex >= numColumns)
			return 0;

		if (visited[gridXIndex][gridYIndex])
			return 0;
		visited[gridXIndex][gridYIndex] = true;
		
		// May be can calculate these once and save them somewhere.
		Tuple corner1 = new Tuple(); //bottom left
		corner1.xCoord = minX + gridXIndex * gridCellWidth;
		corner1.yCoord = minY + gridYIndex * gridCellHeight;
		
		Tuple corner2 = new Tuple(); //bottom right
		corner2.xCoord = corner1.xCoord + gridCellWidth;
		corner2.yCoord = corner1.yCoord;
		
		Tuple corner3 = new Tuple(); //top left
		corner3.xCoord = corner1.xCoord;
		corner3.yCoord = corner1.yCoord + gridCellHeight;
		
		Tuple corner4 = new Tuple(); //top right
		corner4.xCoord = corner1.xCoord + gridCellWidth;
		corner4.yCoord = corner1.yCoord + gridCellHeight;
	
		double dist;
		dist = Math.sqrt(Math.pow(outerPoint.xCoord - corner1.xCoord, 2) + Math.pow(outerPoint.yCoord - corner1.yCoord, 2));
		if (dist > radius)
			return 0;
		dist = Math.sqrt(Math.pow(outerPoint.xCoord - corner2.xCoord, 2) + Math.pow(outerPoint.yCoord - corner2.yCoord, 2));
		if (dist > radius)
			return 0;
		dist = Math.sqrt(Math.pow(outerPoint.xCoord - corner3.xCoord, 2) + Math.pow(outerPoint.yCoord - corner3.yCoord, 2));
		if (dist > radius)
			return 0;
		dist = Math.sqrt(Math.pow(outerPoint.xCoord - corner4.xCoord, 2) + Math.pow(outerPoint.yCoord - corner4.yCoord, 2));
		if (dist > radius)
			return 0;
		
		if (grid[gridXIndex][gridYIndex] > k) return grid[gridXIndex][gridYIndex];
			
		int c1 = countWithinRadius(outerPoint, radius, gridXIndex+1, gridYIndex);
		if (c1+grid[gridXIndex][gridYIndex] > k) return c1+grid[gridXIndex][gridYIndex];
		int c2 = countWithinRadius(outerPoint, radius, gridXIndex-1, gridYIndex);
		if (c1+c2+grid[gridXIndex][gridYIndex] > k) return c1+c2+grid[gridXIndex][gridYIndex];
		int c3 = countWithinRadius(outerPoint, radius, gridXIndex, gridYIndex+1);
		if (c1+c2+c3+grid[gridXIndex][gridYIndex] > k) return c1+c2+c3+grid[gridXIndex][gridYIndex];
		int c4 = countWithinRadius(outerPoint, radius, gridXIndex, gridYIndex-1);
		if (c1+c2+c3+c4+grid[gridXIndex][gridYIndex] > k) return c1+c2+c3+c4+grid[gridXIndex][gridYIndex];
		int c5 = countWithinRadius(outerPoint, radius, gridXIndex+1, gridYIndex+1);
		if (c1+c2+c3+c4+c5+grid[gridXIndex][gridYIndex] > k) return c1+c2+c3+c4+c5+grid[gridXIndex][gridYIndex];
		int c6 = countWithinRadius(outerPoint, radius, gridXIndex+1, gridYIndex-1);
		if (c1+c2+c3+c4+c5+c6+grid[gridXIndex][gridYIndex] > k) return c1+c2+c3+c4+c5+c6+grid[gridXIndex][gridYIndex];
		int c7 = countWithinRadius(outerPoint, radius, gridXIndex-1, gridYIndex-1);
		if (c1+c2+c3+c4+c5+c6+c7+grid[gridXIndex][gridYIndex] > k) return c1+c2+c3+c4+c5+c6+c7+grid[gridXIndex][gridYIndex];
		int c8 = countWithinRadius(outerPoint, radius, gridXIndex-1, gridYIndex+1);
		if (c1+c2+c3+c4+c5+c6+c7+c8+grid[gridXIndex][gridYIndex] > k) return c1+c2+c3+c4+c5+c6+c7+c8+grid[gridXIndex][gridYIndex];
		
		return grid[gridXIndex][gridYIndex] + c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8;
		
	}

	private void setMapBoundries() {
		Tuple tuple;

		inner.reset();

		minX = Double.MAX_VALUE; minY = Double.MAX_VALUE;
		maxX = Double.MIN_VALUE; maxY = Double.MIN_VALUE;

		while (inner.hasNext()) {
			tuple = inner.getNext();

			if (tuple.xCoord > maxX)
				maxX = tuple.xCoord;
			if (tuple.yCoord > maxY)
				maxY = tuple.yCoord;

			if (tuple.xCoord < minX)
				minX = tuple.xCoord;
			if (tuple.yCoord < minY)
				minY = tuple.yCoord;
		}

		System.out.println("Max X = " + maxX);
		System.out.println("Max Y = " + maxY);

		System.out.println("Min X = " + minX);
		System.out.println("Min Y = " + minY);
		
		mapWidth = maxX - minX;
		mapHeight = maxY - minY;
		
		gridCellWidth = mapWidth / numRows;
		gridCellHeight = mapHeight / numColumns;
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
	
	private void setGridCounts() {
		Tuple tuple;

		inner.reset();

		while (inner.hasNext()) {
			tuple = inner.getNext();
			
			int gridXIndex, gridYIndex;
			gridXIndex = (int)((tuple.xCoord - minX) / mapWidth * numRows);
			gridYIndex = (int)((tuple.yCoord - minY) / mapHeight * numColumns);

			try{
				grid[gridXIndex][gridYIndex]++;
			}
			catch(Exception e) {
				System.out.println("ooooops" + "  " + gridXIndex + "," + gridYIndex);
				if (gridXIndex == numRows)
					gridXIndex--;
				if (gridYIndex == numColumns)
					gridYIndex--;
				grid[gridXIndex][gridYIndex]++;
				
				System.out.println();
				System.out.println();
			}
		}

		int total = 0;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numColumns; j++) {
				total+= grid[i][j];
				//System.out.println(grid[i][j]);
			}
		}
		System.out.println(total);
	}

	private void getkNN(FocalPoint fp) {
		inner.reset();
		Tuple tuple;
		while (inner.hasNext()) {
			tuple = inner.getNext();
			if (fp.nearestNeighbours.size() < fp.k) {
				fp.nearestNeighbours.add(tuple);
			}
			else {
				double tupleDist = Math.sqrt(Math.pow(tuple.xCoord - fp.getXCoord(), 2) + Math.pow(tuple.yCoord - fp.getYCoord(), 2));
				if (fp.searchRadius > tupleDist) {
					fp.nearestNeighbours.add(tuple);
					fp.nearestNeighbours.remove();
					Tuple peek = fp.nearestNeighbours.peek();
					fp.searchRadius = Math.sqrt(Math.pow(peek.xCoord - fp.getXCoord(), 2) + Math.pow(peek.yCoord - fp.getYCoord(), 2));
				}
			}
		}
		
//		System.out.println("FocalPoint is " + focalPoint.getXCoord() + ", " + focalPoint.getYCoord());
//		for (Tuple t : focalPoint.nearestNeighbours) {
//			System.out.println(t.xCoord + ", " + t.yCoord);
//		}
	}
	
}
