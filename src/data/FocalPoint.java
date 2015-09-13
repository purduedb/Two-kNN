package data;

import helper.TupleComparer;

import java.util.Comparator;
import java.util.PriorityQueue;

import data.Tuple;

public class FocalPoint {
	
	private double xCoord;
	private double yCoord;
	
	public int k;
	
	public double searchRadius;
	public PriorityQueue<Tuple> nearestNeighbours;
	
	public FocalPoint() {
		searchRadius = Double.MAX_VALUE;
	}
	
	public void setFocalPoint(double xCoord, double yCoord) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		
		Comparator<Tuple> comparer = new TupleComparer(xCoord, yCoord);
		nearestNeighbours = new PriorityQueue<Tuple> (k, comparer);
	}
	
	public double getXCoord() {
		return xCoord;
	}
	
	public double getYCoord() {
		return yCoord;
	}

}
