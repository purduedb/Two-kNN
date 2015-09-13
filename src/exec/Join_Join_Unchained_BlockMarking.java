package exec;

import helper.FastKNNFinder;
import java.util.ArrayList;
import java.util.HashSet;

import data.FocalPoint;
import data.Tuple;


public class Join_Join_Unchained_BlockMarking {

	private class Flags {
		boolean E2InAnswer;
		boolean mayContributeToAnswer;
		boolean visited;
	}

	Flags[][] flags;

	private ArrayList<String> qAnswer;

	ArrayList<Tuple> E1, E3;
	private int k1_2, k3_2;

	private FastKNNFinder kNNFinder;
	private HashSet<String> E2PointsInAnswer;

	public Join_Join_Unchained_BlockMarking(ArrayList<Tuple> E1, DataScan E2, ArrayList<Tuple> E3, int k1_2, int k3_2){

		qAnswer = new ArrayList<String>();

		this.E1 = E1;
		this.E3 = E3;

		this.k1_2 = k1_2;
		this.k3_2 = k3_2;

		kNNFinder = new FastKNNFinder(E2.getData());
		flags = new Flags[kNNFinder.getNumRows()][kNNFinder.getNumColumns()];
		for (int i = 0; i < kNNFinder.getNumRows(); i++)
			for (int j = 0; j < kNNFinder.getNumColumns(); j++)
				flags[i][j] = new Flags();

		E2PointsInAnswer = new HashSet<String>();
	}

	public ArrayList<String> execute() {

		int gridXIndex, gridYIndex; 

//		int count_cells = 0;
		int c = 0;

		for (Tuple tuple : E1) {
			
			FocalPoint fp = new FocalPoint();
			fp.k = k1_2;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);
			kNNFinder.kNN(fp);

			for (Tuple e2Tuple : fp.nearestNeighbours) {
				E2PointsInAnswer.add(e2Tuple.movingObjectID + "-" + e2Tuple.timeStamp);
				gridXIndex = (int)((e2Tuple.xCoord - kNNFinder.getMinX()) / kNNFinder.getMapWidth() * kNNFinder.getNumRows());
				if (gridXIndex == kNNFinder.getNumRows()) gridXIndex--;
				gridYIndex = (int)((e2Tuple.yCoord - kNNFinder.getMinY()) / kNNFinder.getMapHeight() * kNNFinder.getNumColumns());
				if (gridYIndex == kNNFinder.getNumColumns()) gridYIndex--;

				
				if (!flags[gridXIndex][gridYIndex].E2InAnswer) {
					//count_cells++;
					flags[gridXIndex][gridYIndex].E2InAnswer = true;
				}
			}
		}

		double time = System.nanoTime();
		preprocessGrid();
		System.out.println("Preprocessing Time = " + (System.nanoTime() - time)/1000000000.0);

		int i = 0;
		//int excluded = 0;
		c = 0;
		for (Tuple tuple : E3) {
			c++;
			//if (c % 100 != 0)
				//continue;
			//System.out.println(++i);

			FocalPoint fp = new FocalPoint();
			fp.k = k3_2;
			fp.setFocalPoint(tuple.xCoord, tuple.yCoord);

			gridXIndex = (int)((fp.getXCoord() - kNNFinder.getMinX()) / kNNFinder.getMapWidth() * kNNFinder.getNumRows());
			if (gridXIndex == kNNFinder.getNumRows()) gridXIndex--;
			gridYIndex = (int)((fp.getYCoord() - kNNFinder.getMinY()) / kNNFinder.getMapHeight() * kNNFinder.getNumColumns());
			if (gridYIndex == kNNFinder.getNumColumns()) gridYIndex--;


			if (!flags[gridXIndex][gridYIndex].mayContributeToAnswer) {
				//excluded ++;
				continue;
			}


			kNNFinder.kNN(fp);

			for (Tuple e2Tuple : fp.nearestNeighbours) {
				if (E2PointsInAnswer.contains(e2Tuple.movingObjectID + "-" + e2Tuple.timeStamp))
					qAnswer.add(tuple.movingObjectID + "-->" + e2Tuple.movingObjectID + " - " + e2Tuple.timeStamp);
			}
		}

//		System.out.println("Contributing cells" + count_cells );
//		System.out.println("Excluded " +excluded);

		return qAnswer;
	}

	private void preprocessGrid() {
		for (int xIndex = 0; xIndex < kNNFinder.getNumRows(); xIndex++) {
			for (int yIndex = 0; yIndex < kNNFinder.getNumColumns(); yIndex++) {
				if (flags[xIndex][yIndex].E2InAnswer) {
					flags[xIndex][yIndex].mayContributeToAnswer = true;
					continue;
				}

				FocalPoint cellCenter = new FocalPoint();
				cellCenter.k = k3_2;
				cellCenter.setFocalPoint(kNNFinder.getMinX() + (xIndex + 0.5) * kNNFinder.getGrigCellWidth() , kNNFinder.getMinY() + (yIndex + 0.5) * kNNFinder.getGrigCellHeight());
				kNNFinder.kNN(cellCenter);
				double scopeRadius = cellCenter.searchRadius + kNNFinder.getGridCellDiagonal();

				clearFlags();
				if (countWithinScope(cellCenter, scopeRadius, xIndex, yIndex) > 0)
					flags[xIndex][yIndex].mayContributeToAnswer = true;
			}
		}
	}

	private void clearFlags() {
		for (int i = 0; i < kNNFinder.getNumRows(); i++)
			for (int j = 0; j < kNNFinder.getNumColumns(); j++)
				flags[i][j].visited = false;
	}

	private int countWithinScope (FocalPoint fp, double scopeRadius, int xIndex, int yIndex) {

		if (xIndex < 0 || xIndex >= kNNFinder.getNumRows())
			return 0;
		if (yIndex < 0 || yIndex >= kNNFinder.getNumColumns())
			return 0;

		if (flags[xIndex][yIndex].visited)
			return 0;
		flags[xIndex][yIndex].visited = true;

		if (kNNFinder.getMinDist(fp, xIndex, yIndex) > scopeRadius)
			return 0;

		if (flags[xIndex][yIndex].E2InAnswer)			return 1;

		if(countWithinScope(fp, scopeRadius, xIndex + 1, yIndex) > 0)			return 1;
		if(countWithinScope(fp, scopeRadius, xIndex + 1, yIndex + 1) > 0)			return 1;
		if(countWithinScope(fp, scopeRadius, xIndex + 1, yIndex - 1) > 0)			return 1;
		if(countWithinScope(fp, scopeRadius, xIndex - 1, yIndex) > 0)			return 1;
		if(countWithinScope(fp, scopeRadius, xIndex - 1, yIndex + 1) > 0)			return 1;
		if(countWithinScope(fp, scopeRadius, xIndex - 1, yIndex - 1) > 0)			return 1;
		if(countWithinScope(fp, scopeRadius, xIndex, yIndex + 1) > 0)			return 1;
		if(countWithinScope(fp, scopeRadius, xIndex, yIndex - 1) > 0)			return 1;

		return 0;
	}
}
