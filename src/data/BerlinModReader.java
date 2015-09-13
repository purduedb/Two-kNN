package data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class BerlinModReader {

	private final String fileDelimeter = ",";

	private final int NUM_OBJECTS = 2000;

	private String inputFileName, outputFileName;
	private ArrayList<Trip> trips = new ArrayList<Trip>(); // Each trip contains the movement details of a certain moving object

	/* Each input file from Berlin mod oputput (*.csv) represents trips of 2000 moving objects.
	 * All files were generated with the same procedures, the only difference is the seed of the generator.
	 * The input file name has the seed explicitly written into the name
	 * For example: trips.501.csv is a file generated with the seed 501, the file has 2000 objects,
	 * and the offset of the IDs of the moving objects is 2000 * 501 = 1002000.
	 * */
	private int seed, offset;
	private int period; // period time is in milli-seconds

	public void ConvertSecondo(String inputFileName, String outputFileName, int period)
	{
		this.inputFileName = inputFileName;
		this.outputFileName = outputFileName;
		this.period = period;


		calculateTrips();
		writeOutput();
	}

	private void calculateTrips() {
		try {
			System.out.println("Calculating the trips");
			BufferedReader input =  new BufferedReader(new FileReader(inputFileName));

			// Skip the first line which is literally "movingObjectID,Tripid,Tstart,Tend,Xstart,Ystart,Xend,Yend"
			String line = input.readLine();

			// In all lines, I ignore the Tripid" because it is useless
			// Each line will be split into tokens. the comma ',' is the tokenizer
			String[] tokens;

			int movingObjectID = -1;
			double xEnd, yEnd, xStart, yStart;
			long tEnd, tStart;
			ArrayList<LocationUpdate> locationUpdates = new ArrayList<LocationUpdate>();

			line = input.readLine();

			while (line != null) {
				tokens = line.split(fileDelimeter);

				if (movingObjectID != Integer.parseInt(tokens[0])) {
					movingObjectID = Integer.parseInt(tokens[0]);
					locationUpdates = new ArrayList<LocationUpdate>();
					trips.add(new Trip(movingObjectID, locationUpdates));
				}

				tStart = getTimeTicks(tokens[2]);  
				xStart = Double.parseDouble(tokens[4]);
				yStart = Double.parseDouble(tokens[5]);

				tEnd = getTimeTicks(tokens[3]);  
				xEnd = Double.parseDouble(tokens[6]);
				yEnd = Double.parseDouble(tokens[7]);

				if (locationUpdates.size() == 0) { // first time
					// put the start location as the first location update
					locationUpdates.add(new LocationUpdate(xStart, yStart, tStart));
				}

				locationUpdates.add(new LocationUpdate(xEnd, yEnd, tEnd));
				line = input.readLine();
			}

			System.out.println("End of input file reached");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private long getTimeTicks(String date) {
		// Time is represented: yyyy-mm-dd-hh:mm:ss.sss
		String dateDelim = "-";
		String timeDelim = ":";

		String[] dateItems = date.split(dateDelim);
		int year = Integer.parseInt(dateItems[0]);
		int month = Integer.parseInt(dateItems[1]);
		int day = Integer.parseInt(dateItems[2]);

		// Some of the dates in the file contain hours, minutes and seconds, while others do not
		// For dates not containing time we assume them as zero
		int hour = 0;
		int minute = 0;
		int second = 0;

		// For dates which have time, we get it
		if (dateItems.length == 4) {
			String[] timeItems = dateItems[3].split(timeDelim);
			// Some dates just have hours
			hour = Integer.parseInt(timeItems[0]);
			// Others also have minutes
			if (timeItems.length > 1)
				minute = Integer.parseInt(timeItems[1]);
			// And others have seconds as well
			if (timeItems.length > 2)
				second = (int)Math.floor(Double.parseDouble(timeItems[2]));
		}

		Calendar cal = new GregorianCalendar(year, month, day, hour, minute, second);
		return cal.getTimeInMillis();
	}

	// For the following method, all the moving objects report their position (if it was changed) every fixed period of time
	private void writeOutput() {
		try {
			
			// Trying to get the first time-stamp, which is the time of the earliest movement among all the moving objects
			// And the last time stamp which is the time of the final location update among all the moving objects
			long timeStart = Long.MAX_VALUE, timeEnd = -1;
			LocationUpdate firstLocationUpdate;
			LocationUpdate lastLocationUpdate;
			Trip trip;
			for (int i = 0; i < trips.size(); i++) {
				trip = trips.get(i);

				firstLocationUpdate = (LocationUpdate)trip.movements.get(0);
				if (firstLocationUpdate.time < timeStart)
					timeStart = firstLocationUpdate.time;

				lastLocationUpdate = (LocationUpdate)trip.movements.get(trip.movements.size() - 1);
				if (lastLocationUpdate.time > timeEnd)
					timeEnd = lastLocationUpdate.time;
			}

			long time = timeStart;
			System.out.println("TStart = " + timeStart);
			System.out.println("TEnd = " + timeEnd);
			System.out.println("Period = " + period);
			
			long step = (timeEnd - timeStart) / 10;
			



			FileOutputStream fout = new FileOutputStream(outputFileName);
			DataOutputStream out = new DataOutputStream(fout);


			double[] xLocation = new double[NUM_OBJECTS];
			double[] yLocation = new double[NUM_OBJECTS];
			long[] timeOfUpdate = new long[NUM_OBJECTS];
			int[] startIndex = new int[NUM_OBJECTS];
			//Tuple.Sign[] signs = new Tuple.Sign[NUM_OBJECTS];
			for (int i = 0; i < NUM_OBJECTS; i++) {
				//signs[i] = Tuple.Sign.OTHER;
				startIndex[i] = 0;
			}

			while (time <= timeEnd) {
				for (int i = 0; i < trips.size(); i++) {
					trip = trips.get(i);
					LocationUpdate locationUpdate = new LocationUpdate();
					long start = -1;
					int index = startIndex[i] - 1;
					while (start < time && index < trip.movements.size()) {
						index++;
						startIndex[i] = index;
						locationUpdate = (LocationUpdate)trip.movements.get(index);
						start = locationUpdate.time;
					}

					double oldX = xLocation[i], OldY = yLocation[i];

					if (start == time) {
						xLocation[i] = locationUpdate.xCoord;
						yLocation[i] = locationUpdate.yCoord;
					}
					else if (start > time) {
						if (index == 0)
							continue;

						// get the location on the path
						double x1, y1, x2, y2;
						long t1, t2;
						locationUpdate = (LocationUpdate)trip.movements.get(index - 1);
						x1 = locationUpdate.xCoord;
						y1 = locationUpdate.yCoord;
						t1 = locationUpdate.time;
						locationUpdate = (LocationUpdate)trip.movements.get(index);
						x2 = locationUpdate.xCoord;
						y2 = locationUpdate.yCoord;
						t2 = locationUpdate.time;

						if (x2 == x1 && y2 == y1)
							continue;
						
						xLocation[i] = x1 + (double)(time - t1) * (x2 - x1) / (double)(t2 - t1);
						yLocation[i] = y1 + (double)(time - t1) * (y2 - y1) / (double)(t2 - t1);
					}

					// If the location is the same, produce nothing into the stream, but if this occurs for a long
					// period of time (5 minutes), produce a negative tuple
//					Tuple.Sign sign = Tuple.Sign.OTHER;
//					if (oldX == xLocation[i] && OldY == yLocation[i])
//						if (time - timeOfUpdate[i] > 300000 && signs[i] != Tuple.Sign.NEGATIVE)
//							sign = Tuple.Sign.NEGATIVE;
//						else
//							continue;
//
//					signs[i] = sign;
					timeOfUpdate[i] = time;


					// Write a tuple into the stream:

					out.writeInt(i+1);
					out.writeLong(time);
					out.writeDouble(xLocation[i]);
					out.writeDouble(yLocation[i]);
					
//					LongWritable l = new LongWritable(i + 1 + offset);
//					JSONWritable json = new JSONWritable();
//					json.put("Time", time);
//					json.put("X-Loc", xLocation[i]);
//					json.put("Y-Loc", yLocation[i]);
//					json.put("Sign", sign);
//					writer.append(l, json);
				}

				time += period;
				if (time%step ==0)
					System.out.println(time);
				
//				if (time > nextFileTime && time <= timeEnd) {
//					writer.close();
//					writer = SequenceFile.createWriter(fs, conf, new Path(sequenceFileName + "." + currFile),
//							LongWritable.class, JSONWritable.class);
//				}
			}

			out.close();
			System.out.println("End Writing");
		}
		catch (IOException io) {
			// TODO Auto-generated catch block
			io.printStackTrace();	
		}
	}

}
