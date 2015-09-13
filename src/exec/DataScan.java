package exec;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import data.Tuple;

public class DataScan {

	private FileInputStream fin;
	private DataInputStream din;
	private String inputFileName = "C:\\Users\\Ahmed\\Desktop\\RA\\2011-2012\\Term1\\KNN\\BerlinMod Data\\trips."; 

	Iterator<Tuple> iterator;
	ArrayList<Tuple> data;

	public DataScan (int fileID) {
		inputFileName += fileID + ".dat";
		try {
			fin = new FileInputStream(inputFileName);
			din = new DataInputStream(fin);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		data = new ArrayList<Tuple>();
		try {
			while(true) {
				Tuple tuple = new Tuple();
				tuple.movingObjectID = din.readInt();

				tuple.timeStamp = din.readLong();
				tuple.xCoord = din.readDouble();
				tuple.yCoord = din.readDouble();

				data.add(tuple);
			}
		} catch (EOFException e) {
			iterator = data.iterator();
			System.out.println("Number of tuples = " + data.size());
		}
		catch (IOException e) {
			e.printStackTrace();
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

	public ArrayList<Tuple> getData() {
		return data;
	}
	
}
