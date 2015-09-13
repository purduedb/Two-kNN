package exec;


import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;


public class KNNSelect_Join_MinDist {

	private class flags {
		public boolean visited;
		public boolean mayContributeToAnswer;
	}

	private flags[][] grid;
	private double minX, minY, maxX, maxY;
	private int numRows = 100;
	private int numColumns = 100;

	private ArrayList<String> qAnswer;

	private FocalPoint focalPoint;  // for Inner

	private DataScan inner, outer;
	private int k;

	double gridCellWidth, gridCellHeight;
	double mapWidth, mapHeight;


	public KNNSelect_Join_MinDist(FocalPoint focalPoint, DataScan inner, DataScan outer, int k){
		this.focalPoint = focalPoint;

		qAnswer = new ArrayList<String>();

		this.inner = inner;
		this.outer = outer;
		this.k = k;

		grid = new flags[numRows][numColumns];
		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numColumns; j++)
				grid[i][j] = new flags();
	}

	public ArrayList<String> execute() {

		Tuple tuple;

		setMapBoundries();
		getkNN(focalPoint);
		
		int gridXIndex = (int)((focalPoint.getXCoord() - minX) / mapWidth * numRows);
		if (gridXIndex == numRows) gridXIndex--;
		int gridYIndex = (int)((focalPoint.getYCoord() - minY) / mapHeight * numColumns);
		if (gridYIndex == numColumns) gridYIndex--;

		setGridFlags(gridXIndex, gridYIndex);

		outer.reset();
	
		int kNNCalculations = 0;
		while (outer.hasNext()) {
			tuple = outer.getNext();

			FocalPoint fp = new FocalPoint();
			fp.k = k;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);

			gridXIndex = (int)((tuple.xCoord - minX) / mapWidth * numRows);
			if (gridXIndex == numRows) gridXIndex--;
			gridYIndex = (int)((tuple.yCoord - minY) / mapHeight * numColumns);
			if (gridYIndex == numColumns) gridYIndex--;

			if (!grid[gridXIndex][gridYIndex].mayContributeToAnswer)
				continue;


			kNNCalculations++;

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

		System.out.println("kNN calculations = " + kNNCalculations);

		return qAnswer;
	}

	private void setGridFlags(int x, int y) {

		if (x < 0 || x == numRows)
			return;
		if (y < 0 || y == numColumns)
			return;

		if (grid[x][y].visited)
			return;

		grid[x][y].visited = true;

		Tuple bottom_left = new Tuple(); //bottom left
		bottom_left.xCoord = minX + x * gridCellWidth;
		bottom_left.yCoord = minY + y * gridCellHeight;
		Tuple top_right = new Tuple(); //top right
		top_right.xCoord = bottom_left.xCoord + gridCellWidth;
		top_right.yCoord = bottom_left.yCoord + gridCellHeight;

		// check if the focal point is inside (first call only goes here)
		if (focalPoint.getXCoord() <= top_right.xCoord && focalPoint.getYCoord() <= top_right.yCoord
				&& focalPoint.getXCoord() >= bottom_left.xCoord && focalPoint.getYCoord() >= bottom_left.yCoord)
			grid[x][y].mayContributeToAnswer = true;
		else {
			FocalPoint fp = getMinDistPoint(bottom_left, top_right);
			getkNN(fp);
			for (Tuple t : fp.nearestNeighbours)
				if (focalPoint.nearestNeighbours.contains(t)) {
					grid[x][y].mayContributeToAnswer = true;
					break;
				}
		}

		if (grid[x][y].mayContributeToAnswer) {
			setGridFlags(x+1, y+1);
			setGridFlags(x+1, y);
			setGridFlags(x+1, y-1);
			setGridFlags(x, y+1);
			setGridFlags(x, y-1);
			setGridFlags(x-1, y+1);
			setGridFlags(x-1, y);
			setGridFlags(x-1, y-1);
		}	

	}

	public FocalPoint getMinDistPoint(Tuple bottom_left, Tuple top_right) {

		double xCoord, yCoord;
		if (bottom_left.xCoord > focalPoint.getXCoord())
			xCoord = bottom_left.xCoord;
		else if (top_right.xCoord < focalPoint.getXCoord())
			xCoord = top_right.xCoord;
		else
			xCoord = focalPoint.getXCoord();

		if (bottom_left.yCoord > focalPoint.getYCoord())
			yCoord = bottom_left.yCoord;
		else if (top_right.yCoord < focalPoint.getYCoord())
			yCoord = top_right.yCoord;
		else
			yCoord = focalPoint.getYCoord();

		FocalPoint minDistPoint = new FocalPoint();
		minDistPoint.k = k;
		minDistPoint.setFocalPoint(xCoord, yCoord);
		return minDistPoint;
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
