package exec;

import helper.FastKNNFinder;
import java.util.ArrayList;
import data.FocalPoint;
import data.Tuple;

public class Select_Join_BlockMarking {
	
	private class Flags {
		public boolean visited;
		public boolean mayContributeToAnswer;
	}

	private ArrayList<String> qAnswer;

	private FocalPoint focalPoint;  // for Inner

	// For Join
	private DataScan outer;
	private int k; // of join
	private FastKNNFinder kNNFinder;
	private Flags[][] flags;

	public Select_Join_BlockMarking(FocalPoint focalPoint, DataScan inner, DataScan outer, int k){
		this.focalPoint = focalPoint;

		qAnswer = new ArrayList<String>();

		this.outer = outer;
		this.k = k;

		kNNFinder = new FastKNNFinder(inner.getData());
		
		flags = new Flags[kNNFinder.getNumRows()][kNNFinder.getNumColumns()];
		for (int i = 0; i < kNNFinder.getNumRows(); i++)
			for (int j = 0; j < kNNFinder.getNumColumns(); j++)
				flags[i][j] = new Flags();
	}

	public ArrayList<String> execute() {

		Tuple tuple;

		kNNFinder.kNN(focalPoint);

		int gridXIndex = (int)((focalPoint.getXCoord() - kNNFinder.getMinX()) / kNNFinder.getMapWidth() * kNNFinder.getNumRows());
		if (gridXIndex == kNNFinder.getNumRows()) gridXIndex--;
		int gridYIndex = (int)((focalPoint.getYCoord() - kNNFinder.getMinY()) / kNNFinder.getMapHeight() * kNNFinder.getNumColumns());
		if (gridYIndex == kNNFinder.getNumColumns()) gridYIndex--;

		setGridFlags(gridXIndex, gridYIndex);

		outer.reset();

		int c = 0;
		int kNNCalculations = 0;
		while (outer.hasNext()) {
			tuple = outer.getNext();
			c++;
			if (c % 256 != 0)
				continue;
			
			FocalPoint fp = new FocalPoint();
			fp.k = k;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);

			gridXIndex = (int)((tuple.xCoord - kNNFinder.getMinX()) / kNNFinder.getMapWidth() * kNNFinder.getNumRows());
			if (gridXIndex == kNNFinder.getNumRows()) gridXIndex--;
			gridYIndex = (int)((tuple.yCoord - kNNFinder.getMinY()) / kNNFinder.getMapHeight() * kNNFinder.getNumColumns());
			if (gridYIndex == kNNFinder.getNumColumns()) gridYIndex--;

			if (!flags[gridXIndex][gridYIndex].mayContributeToAnswer)
				continue;


			kNNCalculations++;

			kNNFinder.kNN(fp);
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

		if (x < 0 || x == kNNFinder.getNumRows())
			return;
		if (y < 0 || y == kNNFinder.getNumColumns())
			return;

		if (flags[x][y].visited)
			return;

		flags[x][y].visited = true;

		Tuple bottom_left = new Tuple(); //bottom left
		bottom_left.xCoord = kNNFinder.getMinX() + x * kNNFinder.getGrigCellWidth();
		bottom_left.yCoord = kNNFinder.getMinY() + y * kNNFinder.getGrigCellHeight();
		Tuple top_right = new Tuple(); //top right
		top_right.xCoord = bottom_left.xCoord + kNNFinder.getGrigCellWidth();
		top_right.yCoord = bottom_left.yCoord + kNNFinder.getGrigCellHeight();

		// check if the focal point is inside (first call only goes here)
		if (focalPoint.getXCoord() <= top_right.xCoord && focalPoint.getYCoord() <= top_right.yCoord
				&& focalPoint.getXCoord() >= bottom_left.xCoord && focalPoint.getYCoord() >= bottom_left.yCoord)
			flags[x][y].mayContributeToAnswer = true;
		else {
			FocalPoint cellCenter = new FocalPoint();
			cellCenter.k = k;
			cellCenter.setFocalPoint(kNNFinder.getMinX() + (x + 0.5) * kNNFinder.getGrigCellWidth() , kNNFinder.getMinY() + (y + 0.5) * kNNFinder.getGrigCellHeight());
			kNNFinder.kNN(cellCenter);
			double distanceToCenter = Math.sqrt(Math.pow(cellCenter.getXCoord() - focalPoint.getXCoord(), 2) + Math.pow(cellCenter.getYCoord() - focalPoint.getYCoord(), 2));
			if (cellCenter.searchRadius + kNNFinder.getGridCellDiagonal() + focalPoint.searchRadius > distanceToCenter)
				flags[x][y].mayContributeToAnswer = true;
		}
		


		if (flags[x][y].mayContributeToAnswer) {
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

}
