package helper;

import java.util.ArrayList;

import data.Tuple;

public class GridCell {
	public ArrayList<Tuple> points;
	public boolean visited;
	public int xIndex, yIndex;

	public boolean mayContributeToAnswer;
	public double distance; // Could be maxDist or minDist

	public GridCell() {
		points = new  ArrayList<Tuple>();
	}
}
