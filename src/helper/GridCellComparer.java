package helper;

import java.util.Comparator;

public class GridCellComparer implements Comparator<GridCell>{

	@Override
	public int compare(GridCell a, GridCell b) {
		
		if (a.distance < b.distance)
			return -1;
		if (a.distance > b.distance)
			return 1;
		
		return 0;
	}

}
