package data;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;

public class ConversionValidator {

	public static void main(String[] args) {

		String inputFileName = "C:\\Users\\Ahmed\\Desktop\\RA\\2011-2012\\Term1\\KNN\\BerlinMod Data\\trips.1000.dat";

		FileInputStream fin;
		try {
			fin = new FileInputStream(inputFileName);
			DataInputStream din = new DataInputStream(fin);

			while (true)
				System.out.println(din.readInt() + ", " + din.readLong() + ", " + din.readDouble() + ", " + din.readDouble());



		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("End of file reached");
			e.printStackTrace();
		}



	}

}
