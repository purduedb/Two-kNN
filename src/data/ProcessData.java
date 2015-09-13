package data;

public class ProcessData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Starting the conversion process");
		String inputFileName = "C:\\Users\\Ahmed\\Desktop\\RA\\2011-2012\\Term1\\KNN\\BerlinMod Data\\trips.0.csv";
		String outputFileName = "C:\\Users\\Ahmed\\Desktop\\RA\\2011-2012\\Term1\\KNN\\BerlinMod Data\\trips.10.dat";
	
		BerlinModReader converter = new BerlinModReader();
		converter.ConvertSecondo(inputFileName, outputFileName, 10000);
	}

}
