package data;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import data.Tuple;


public class ClusteredData {

	Iterator<Tuple> iterator;
	ArrayList<Tuple> data;

	public ClusteredData (int n) {

		data = new ArrayList<Tuple>();
		Random rand = new Random();
		
		double clusterFocusX = 5, clusterFocusY = 5;
		int sign;
		
		while(n-- > 0) {
			Tuple tuple = new Tuple();
			tuple.movingObjectID = n;

			tuple.timeStamp = rand.nextLong();
			sign = rand.nextBoolean() ? 1 : -1;
			tuple.xCoord = clusterFocusX + sign * rand.nextDouble() * 10;
			sign = rand.nextBoolean() ? 1 : -1;
			tuple.yCoord = clusterFocusY + sign * rand.nextDouble() * 10;

			data.add(tuple);
		}
	}

	public void reset() {
		iterator = data.iterator();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public Tuple getNext() {
		return iterator.next();
	}


}









