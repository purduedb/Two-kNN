package driver;

import java.util.ArrayList;
import java.util.Random;


import data.Tuple;
import exec.CachedKNNJoin;
import exec.DataScan;
import exec.Intersect;
import exec.KNNJoin;
import exec.OutputView;
import exec.Tagger;

public class JoinSetup {

	private final int ID_RANGE = 1000000;
	// 10000, 100
	private final int DATASIZE = 100000;
	private final int k = 2;

	private Tuple[] data;
	private int numStreams;


	public JoinSetup(int numStreams) {
		this.numStreams = numStreams; 

		Random rand = new Random();
		//int time = 0;
		data = new Tuple[DATASIZE];

		for (int i = 0; i < DATASIZE; i++) {
			data[i] = new Tuple();
			//data[i].streamIndex = rand.nextInt(numStreams);
			//data[i].timeStamp = time++;
			data[i].objID = rand.nextInt(ID_RANGE);
			data[i].data.add(data[i].objID);
			data[i].xCoord = rand.nextInt(101);
			data[i].yCoord = rand.nextInt(101);
		}
		int j = 1;
		for (int i = 0; i < DATASIZE; i++) {
			data[i].streamIndex = numStreams - j;
			//System.out.println(data[i].streamIndex);
			if ((DATASIZE - i) < DATASIZE/numStreams)
				continue;
			if (i % (DATASIZE/numStreams) == 0 && i != 0)
				j++;
		}
		
	}

	public ArrayList<Tuple> runPlanA() {

		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		Tagger rRange = new Tagger();
		Tagger sRange = new Tagger();
		KNNJoin joinRS = new KNNJoin(k);

		Tagger tRange = new Tagger();
		KNNJoin joinST = new KNNJoin(k);
		
		OutputView opView = new OutputView();

		// Setting lefts and rights as necessary
		joinRS.outer = rRange; joinRS.inner = sRange;
		joinST.outer = joinRS; joinST.inner = tRange;
		
		// Setting parents as necessary
		rRange.parent = joinRS;
		sRange.parent = joinRS;
		
		joinRS.parent = joinST;
		tRange.parent = joinST;
				
		joinST.parent = opView;
		
		
		// Setting Positions as necessary
		rRange.isInner = false;
		sRange.isInner = true;
		
		joinRS.isInner = false;
		tRange.isInner = true;
		
		Tagger[] streamTaggers = new Tagger[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;

		dataScan.streamTaggers = streamTaggers;

		while(dataScan.hasNext()) {
			dataScan.process(null, false);
		}

		return opView.allTuples;
	}	

	public ArrayList<Tuple> runPlanB() {

		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		Tagger rRange = new Tagger();
		Tagger sRange = new Tagger();
		KNNJoin joinRS = new KNNJoin(k);

		Tagger tRange = new Tagger();
		KNNJoin joinST = new KNNJoin(k);
		
		Intersect intersect = new Intersect();
		
		OutputView opView = new OutputView();

		// Setting lefts and rights as necessary
		joinRS.outer = rRange; joinRS.inner = sRange;
		joinST.outer = sRange; joinST.inner = tRange;
		intersect.outer = joinRS; intersect.inner = joinST;
		
		// Setting parents as necessary
		rRange.parent = joinRS;
		sRange.parent = joinRS;
		sRange.unckle = joinST;
		
		joinRS.parent = intersect;
		tRange.parent = joinST;
				
		joinST.parent = intersect;
		
		intersect.parent = opView;
		
		
		// Setting Positions as necessary
		rRange.isInner = false;
		sRange.isInner = true;
		sRange.isInnerToUnckle = false;
		tRange.isInner = true;
		
		joinRS.isInner = false;
		joinST.isInner = true;
		
				
		Tagger[] streamTaggers = new Tagger[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;

		dataScan.streamTaggers = streamTaggers;

		while(dataScan.hasNext()) {
			dataScan.process(null, false);
		}

		return opView.allTuples;
	}	
	
	public ArrayList<Tuple> runPlanC() {

		DataScan dataScan = new DataScan();
		dataScan.getData(data);
		Tagger rRange = new Tagger();
		Tagger sRange = new Tagger();
		KNNJoin joinRS = new KNNJoin(k);

		Tagger tRange = new Tagger();
		CachedKNNJoin joinST = new CachedKNNJoin(k);
		
		OutputView opView = new OutputView();

		// Setting lefts and rights as necessary
		joinRS.outer = rRange; joinRS.inner = sRange;
		joinST.outer = joinRS; joinST.inner = tRange;
		
		// Setting parents as necessary
		rRange.parent = joinRS;
		sRange.parent = joinRS;
		
		joinRS.parent = joinST;
		tRange.parent = joinST;
				
		joinST.parent = opView;
		
		
		// Setting Positions as necessary
		rRange.isInner = false;
		sRange.isInner = true;
		
		joinRS.isInner = false;
		tRange.isInner = true;
		
		Tagger[] streamTaggers = new Tagger[numStreams];
		streamTaggers[0] = rRange;
		streamTaggers[1] = sRange;
		streamTaggers[2] = tRange;

		dataScan.streamTaggers = streamTaggers;

		while(dataScan.hasNext()) {
			dataScan.process(null, false);
		}

		return opView.allTuples;
	}	

}
