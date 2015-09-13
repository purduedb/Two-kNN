package helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

import data.FocalPoint;
import data.Tuple;

public class FastKNNFinder {

	public GridCell[][] grid;
	private double minX, minY, maxX, maxY;
	private int numRows = 100;
	private int numColumns = 100;
	private Iterator<Tuple> iterator;
	ArrayList<Tuple> data;

	private double gridCellWidth, gridCellHeight;
	private double mapWidth, mapHeight;
	private double gridCellDiagonal;

	public FastKNNFinder(ArrayList<Tuple> data) {
		this.data = data;
		iterator = data.iterator();
		
		grid = new GridCell[numRows][numColumns];
		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numColumns; j++) {
				grid[i][j] = new GridCell();
				grid[i][j].xIndex = i;
				grid[i][j].yIndex = j;
			}

		setMapBoundries();
		indexData();
	}

	public void kNN(FocalPoint fp) {

		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numColumns; j++)
				grid[i][j].visited = false;

		ArrayList<GridCell> locality = new ArrayList<GridCell>();

		int xIndex = (int)((fp.getXCoord() - minX) / mapWidth * numRows);
		if (xIndex == numRows) xIndex--;
		int yIndex = (int)((fp.getYCoord() - minY) / mapHeight * numColumns);
		if (yIndex == numColumns) yIndex--;

		locality.add(grid[xIndex][yIndex]);
		grid[xIndex][yIndex].visited = true;

		// Start with the MaxDist loops
		Comparator<GridCell> comparer = new GridCellComparer();
		PriorityQueue<GridCell> maxDistQueue = new PriorityQueue<GridCell>(5, comparer);

		addEightNeighbours(maxDistQueue, xIndex, yIndex, true, fp);
		int currentK = grid[xIndex][yIndex].points.size();
		double currentMaxDist = getInitialMaxDist(xIndex, yIndex);
		while (true) {
			GridCell cell = maxDistQueue.remove();
			if (cell.distance <= currentMaxDist) {
				locality.add(cell);
				currentK += cell.points.size();
				addEightNeighbours(maxDistQueue, cell.xIndex, cell.yIndex, true, fp);
			}
			else if (cell.distance > currentMaxDist) {
				if (currentK < fp.k) {
					locality.add(cell);
					currentK += cell.points.size();
					currentMaxDist = cell.distance;
					addEightNeighbours(maxDistQueue, cell.xIndex, cell.yIndex, true, fp);
				}
				else {
					// return the cell back as it will be considered for the minDist iterations 
					maxDistQueue.add(cell);
					break;
				}
			}
		}

		// Go for the MinDist loops
		PriorityQueue<GridCell> minDistQueue = new PriorityQueue<GridCell>(5, comparer);
		for (GridCell cell : maxDistQueue) {
			cell.distance = getMinDist(fp, cell.xIndex, cell.yIndex);
			minDistQueue.add(cell);
		}
		maxDistQueue.clear();

		while (true) {
			GridCell cell = minDistQueue.remove();
			if (cell.distance <= currentMaxDist) {
				locality.add(cell);
				addEightNeighbours(minDistQueue, cell.xIndex, cell.yIndex, false, fp);
			}
			else
				break;
		}

		// Finally:
		// Loop on the locality only to find the kNN
		//System.out.println("Locality Size: " + locality.size());
		for (GridCell cell : locality) {
			for (Tuple tuple : cell.points)
				if (fp.nearestNeighbours.size() < fp.k) {
					fp.nearestNeighbours.add(tuple);
				}
				else {
					//double tupleDist = Math.sqrt(Math.pow(tuple.xCoord - fp.getXCoord(), 2) + Math.pow(tuple.yCoord - fp.getYCoord(), 2));
					//if (fp.searchRadius > tupleDist) {
						fp.nearestNeighbours.add(tuple);
						fp.nearestNeighbours.remove();
						//Tuple peek = fp.nearestNeighbours.peek();
						//fp.searchRadius = Math.sqrt(Math.pow(peek.xCoord - fp.getXCoord(), 2) + Math.pow(peek.yCoord - fp.getYCoord(), 2));
					//}
				}
		}
		// Set the search radius
		Tuple peek = fp.nearestNeighbours.peek();
		fp.searchRadius = Math.sqrt(Math.pow(peek.xCoord - fp.getXCoord(), 2) + Math.pow(peek.yCoord - fp.getYCoord(), 2));

	}
	
	public void kNN(FocalPoint fp, double searchThreshold) {

		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numColumns; j++)
				grid[i][j].visited = false;

		ArrayList<GridCell> locality = new ArrayList<GridCell>();

		int xIndex = (int)((fp.getXCoord() - minX) / mapWidth * numRows);
		if (xIndex == numRows) xIndex--;
		int yIndex = (int)((fp.getYCoord() - minY) / mapHeight * numColumns);
		if (yIndex == numColumns) yIndex--;

		locality.add(grid[xIndex][yIndex]);
		grid[xIndex][yIndex].visited = true;

		// Start with the MaxDist loops
		Comparator<GridCell> comparer = new GridCellComparer();
		PriorityQueue<GridCell> maxDistQueue = new PriorityQueue<GridCell>(5, comparer);

		addEightNeighbours(maxDistQueue, xIndex, yIndex, true, fp);
		int currentK = grid[xIndex][yIndex].points.size();
		double currentMaxDist = getInitialMaxDist(xIndex, yIndex);
		while (true) {
			GridCell cell = maxDistQueue.remove();
			if (getMinDist(fp, cell.xIndex, cell.yIndex) > searchThreshold) {
				//System.out.println("Here");
				break;
			}
			if (cell.distance <= currentMaxDist) {
				locality.add(cell);
				currentK += cell.points.size();
				addEightNeighbours(maxDistQueue, cell.xIndex, cell.yIndex, true, fp);
			}
			else if (cell.distance > currentMaxDist) {
				if (currentK < fp.k) {
					locality.add(cell);
					currentK += cell.points.size();
					currentMaxDist = cell.distance;
					addEightNeighbours(maxDistQueue, cell.xIndex, cell.yIndex, true, fp);
				}
				else {
					// return the cell back as it will be considered for the minDist iterations 
					maxDistQueue.add(cell);
					break;
				}
			}
		}

		// Go for the MinDist loops
		PriorityQueue<GridCell> minDistQueue = new PriorityQueue<GridCell>(5, comparer);
		for (GridCell cell : maxDistQueue) {
			cell.distance = getMinDist(fp, cell.xIndex, cell.yIndex);
			minDistQueue.add(cell);
		}
		maxDistQueue.clear();

		while (true) {
			GridCell cell = minDistQueue.remove();
			if (getMinDist(fp, cell.xIndex, cell.yIndex) > searchThreshold) {
				//System.out.println("Here");
				break;
			}
			if (cell.distance <= currentMaxDist) {
				locality.add(cell);
				addEightNeighbours(minDistQueue, cell.xIndex, cell.yIndex, false, fp);
			}
			else
				break;
		}

		// Finally:
		// Loop on the locality only to find the kNN
		System.out.println("Locality Size: " + locality.size());
		for (GridCell cell : locality) {
			for (Tuple tuple : cell.points)
				if (fp.nearestNeighbours.size() < fp.k) {
					fp.nearestNeighbours.add(tuple);
				}
				else {
					//double tupleDist = Math.sqrt(Math.pow(tuple.xCoord - fp.getXCoord(), 2) + Math.pow(tuple.yCoord - fp.getYCoord(), 2));
					//if (fp.searchRadius > tupleDist) {
						fp.nearestNeighbours.add(tuple);
						fp.nearestNeighbours.remove();
						//Tuple peek = fp.nearestNeighbours.peek();
						//fp.searchRadius = Math.sqrt(Math.pow(peek.xCoord - fp.getXCoord(), 2) + Math.pow(peek.yCoord - fp.getYCoord(), 2));
					//}
				}
		}
		// Set the search radius
		Tuple peek = fp.nearestNeighbours.peek();
		fp.searchRadius = Math.sqrt(Math.pow(peek.xCoord - fp.getXCoord(), 2) + Math.pow(peek.yCoord - fp.getYCoord(), 2));

	}

	private void indexData() {
		iterator = data.iterator();
		Tuple tuple;
		while (iterator.hasNext()) {
			tuple = iterator.next();

			int gridXIndex = (int)((tuple.xCoord - minX) / mapWidth * numRows);
			if (gridXIndex == numRows) gridXIndex--;
			int gridYIndex = (int)((tuple.yCoord - minY) / mapHeight * numColumns);
			if (gridYIndex == numColumns) gridYIndex--;

			grid[gridXIndex][gridYIndex].points.add(tuple);
		}
	}

	private void setMapBoundries() {
		Tuple tuple;

		iterator = data.iterator();

		minX = Double.MAX_VALUE; minY = Double.MAX_VALUE;
		maxX = Double.MIN_VALUE; maxY = Double.MIN_VALUE;

		while (iterator.hasNext()) {
			tuple = iterator.next();

			if (tuple.xCoord > maxX)
				maxX = tuple.xCoord;
			if (tuple.yCoord > maxY)
				maxY = tuple.yCoord;

			if (tuple.xCoord < minX)
				minX = tuple.xCoord;
			if (tuple.yCoord < minY)
				minY = tuple.yCoord;
		}

//		System.out.println("Max X = " + maxX);
//		System.out.println("Max Y = " + maxY);
//		System.out.println("Min X = " + minX);
//		System.out.println("Min Y = " + minY);

		mapWidth = maxX - minX;
		mapHeight = maxY - minY;

		gridCellWidth = mapWidth / numRows;
		gridCellHeight = mapHeight / numColumns;
		
		gridCellDiagonal = Math.sqrt(gridCellWidth * gridCellWidth + gridCellHeight * gridCellHeight);
	}

	private double getInitialMaxDist(int xIndex, int yIndex) {
		double maxDist = Double.MIN_VALUE, temp;
		if (xIndex != 0) {
			temp = grid[xIndex - 1][yIndex].distance;
			maxDist = (temp > maxDist) ? temp : maxDist;

			if (yIndex != 0) {
				temp = grid[xIndex - 1][yIndex - 1].distance;
				maxDist = (temp > maxDist) ? temp : maxDist;
			}
			if (yIndex != (numColumns - 1)) {
				temp = grid[xIndex - 1][yIndex + 1].distance;
				maxDist = (temp > maxDist) ? temp : maxDist;
			}
		}
		if (xIndex != (numRows - 1)) {
			temp = grid[xIndex + 1][yIndex].distance;
			maxDist = (temp > maxDist) ? temp : maxDist;

			if (yIndex != 0) {
				temp = grid[xIndex + 1][yIndex - 1].distance;
				maxDist = (temp > maxDist) ? temp : maxDist;
			}
			if (yIndex != (numColumns - 1)) {
				temp = grid[xIndex + 1][yIndex + 1].distance;
				maxDist = (temp > maxDist) ? temp : maxDist;
			}
		}
		if (yIndex != 0) {
			temp = grid[xIndex][yIndex - 1].distance;
			maxDist = (temp > maxDist) ? temp : maxDist;
		}
		if (yIndex != (numColumns - 1)) {
			temp = grid[xIndex][yIndex + 1].distance;
			maxDist = (temp > maxDist) ? temp : maxDist;
		}

		return maxDist;
	}

	// minOrMax = false for minDist loops, true for maxDist loops
	private void addEightNeighbours(PriorityQueue<GridCell> pq, int xIndex, int yIndex, boolean minOrMax, FocalPoint fp) {
		if (xIndex != 0) {
			if (!grid[xIndex - 1][yIndex].visited) {
				grid[xIndex - 1][yIndex].distance =minOrMax ? getMaxDist(fp, xIndex - 1, yIndex) : getMinDist(fp, xIndex - 1, yIndex); 
				pq.add(grid[xIndex - 1][yIndex]);
				grid[xIndex - 1][yIndex].visited = true;
			}
			if (yIndex != 0) {
				if (!grid[xIndex - 1][yIndex - 1].visited) {
					grid[xIndex - 1][yIndex - 1].distance =minOrMax ? getMaxDist(fp, xIndex - 1, yIndex - 1) : getMinDist(fp, xIndex - 1, yIndex - 1);
					pq.add(grid[xIndex - 1][yIndex - 1]);
					grid[xIndex - 1][yIndex - 1].visited = true;
				}
			}
			if (yIndex != (numColumns - 1))
				if (!grid[xIndex - 1][yIndex + 1].visited) {
					grid[xIndex - 1][yIndex + 1].distance =minOrMax ? getMaxDist(fp, xIndex - 1, yIndex + 1) : getMinDist(fp, xIndex - 1, yIndex + 1);
					pq.add(grid[xIndex - 1][yIndex + 1]);
					grid[xIndex - 1][yIndex + 1].visited = true;
				}
		}
		if (xIndex != (numRows - 1)) {
			if (!grid[xIndex + 1][yIndex].visited) {
				grid[xIndex + 1][yIndex].distance =minOrMax ? getMaxDist(fp, xIndex + 1, yIndex) : getMinDist(fp, xIndex + 1, yIndex);
				pq.add(grid[xIndex + 1][yIndex]);
				grid[xIndex + 1][yIndex].visited = true;
			}
			if (yIndex != 0)
				if (!grid[xIndex + 1][yIndex - 1].visited) {
					grid[xIndex + 1][yIndex - 1].distance =minOrMax ? getMaxDist(fp, xIndex + 1, yIndex - 1) : getMinDist(fp, xIndex + 1, yIndex - 1);
					pq.add(grid[xIndex + 1][yIndex - 1]);
					grid[xIndex + 1][yIndex - 1].visited = true;
				}
			if (yIndex != (numColumns - 1))
				if (!grid[xIndex + 1][yIndex + 1].visited) {
					grid[xIndex + 1][yIndex + 1].distance =minOrMax ? getMaxDist(fp, xIndex + 1, yIndex + 1) : getMinDist(fp, xIndex + 1, yIndex + 1);
					pq.add(grid[xIndex + 1][yIndex + 1]);
					grid[xIndex + 1][yIndex + 1].visited = true;
				}
		}
		if (yIndex != 0)
			if (!grid[xIndex][yIndex - 1].visited) {
				grid[xIndex][yIndex - 1].distance =minOrMax ? getMaxDist(fp, xIndex, yIndex - 1) : getMinDist(fp, xIndex, yIndex - 1);
				pq.add(grid[xIndex][yIndex - 1]);
				grid[xIndex][yIndex - 1].visited = true;
			}
		if (yIndex != (numColumns - 1))
			if (!grid[xIndex][yIndex + 1].visited) {
				grid[xIndex][yIndex + 1].distance =minOrMax ? getMaxDist(fp, xIndex, yIndex + 1) : getMinDist(fp, xIndex, yIndex + 1);
				pq.add(grid[xIndex][yIndex + 1]);
				grid[xIndex][yIndex + 1].visited = true;
			}
	}

	public double getMaxDist(FocalPoint fp, int x, int y) {
		Tuple bottom_left = new Tuple(); //bottom left
		bottom_left.xCoord = minX + x * gridCellWidth;
		bottom_left.yCoord = minY + y * gridCellHeight;
		Tuple top_right = new Tuple(); //top right
		top_right.xCoord = bottom_left.xCoord + gridCellWidth;
		top_right.yCoord = bottom_left.yCoord + gridCellHeight;

		double xCoord, yCoord;
		if (fp.getXCoord() < ((bottom_left.xCoord + top_right.xCoord) / 2))
			xCoord = top_right.xCoord;
		else
			xCoord = bottom_left.xCoord;

		if (fp.getYCoord() < ((bottom_left.yCoord + top_right.yCoord) / 2))
			yCoord = top_right.yCoord;
		else
			yCoord = bottom_left.yCoord;

		return Math.sqrt(Math.pow(xCoord - fp.getXCoord(), 2) + Math.pow(yCoord - fp.getYCoord(), 2));
	}

	public double getMinDist(FocalPoint fp, int x, int y) {
		Tuple bottom_left = new Tuple(); //bottom left
		bottom_left.xCoord = minX + x * gridCellWidth;
		bottom_left.yCoord = minY + y * gridCellHeight;
		Tuple top_right = new Tuple(); //top right
		top_right.xCoord = bottom_left.xCoord + gridCellWidth;
		top_right.yCoord = bottom_left.yCoord + gridCellHeight;

		double xCoord, yCoord;
		if (bottom_left.xCoord > fp.getXCoord())
			xCoord = bottom_left.xCoord;
		else if (top_right.xCoord < fp.getXCoord())
			xCoord = top_right.xCoord;
		else
			xCoord = fp.getXCoord();

		if (bottom_left.yCoord > fp.getYCoord())
			yCoord = bottom_left.yCoord;
		else if (top_right.yCoord < fp.getYCoord())
			yCoord = top_right.yCoord;
		else
			yCoord = fp.getYCoord();

		return Math.sqrt(Math.pow(xCoord - fp.getXCoord(), 2) + Math.pow(yCoord - fp.getYCoord(), 2));
	}

	public int getNumRows() {
		return numRows;
	}
	
	public int getNumColumns() {
		return numColumns;
	}
	
	public double getMinX() {
		return minX;
	}
	
	public double getMinY() {
		return minY;
	}
	
	public double getMaxX() {
		return maxX;
	}
	
	public double getMaxY() {
		return maxY;
	}

	public double getMapWidth () {
		return mapWidth;
	}
	
	public double getMapHeight () {
		return mapHeight;
	}
	
	public double getGrigCellHeight () {
		return gridCellHeight;
	}
	
	public double getGrigCellWidth () {
		return gridCellWidth;
	}
	
	public int getPointCountInCell(int x, int y) {
		return grid[x][y].points.size();
	}
	
	public double getGridCellDiagonal () {
		return gridCellDiagonal;
	}
}
