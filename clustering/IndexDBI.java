package clustering;

import java.util.ArrayList;
import java.util.Iterator;

public class IndexDBI {
	
	private static double DBI_Siq(ArrayList<double[]> set, int q) {
		// find centroid (mean point)
		double[] centroid = IndexUtil.findCentroid(set);
		
		// calculate S(i,q)
		double sum = 0.0;
		for (Iterator<double[]> it=set.iterator(); it.hasNext(); ) {
			double[] point = it.next();
			double dist = Similarity.eucledianDistance(centroid, point);
			sum += Math.pow(dist, q);
		}
		sum /= set.size();
		sum = Math.pow(sum, 1.0/q);
		return sum;
	}
	
	private static double DBI_dijt(ArrayList<double[]> s1, ArrayList<double[]> s2, int t) {
		double[] centroid1 = IndexUtil.findCentroid(s1);
		double[] centroid2 = IndexUtil.findCentroid(s2);
		
		double dist = Similarity.minKowskiDistance(centroid1, centroid2, t);
		return dist;
	}
	
	private static double DBI_Riqt(ArrayList<ArrayList<double[]>> al, int i, int q, int t) {
		ArrayList<double[]> seti = al.get(i);
		double max = Double.MIN_VALUE;
		for (int j=0; j<al.size(); j++) {
			if (j==i) continue;
			ArrayList<double[]> setj = al.get(j);
			double siq = DBI_Siq(seti, q);
			double sjq = DBI_Siq(setj, q);
			double dijt = DBI_dijt(seti, setj, t);
			double r = (siq+sjq)/dijt;
			if (r > max) max = r;
		}
		return max;
	}

	public static double getDBI(ArrayList<ArrayList<double[]>> al, int q, int t) {
		// input clustering result, output index value
		double sum = 0.0;
		for (int i=0; i<al.size(); i++) {
			sum += DBI_Riqt(al, i, q, t);
		}
		sum /= al.size();
		return sum;
	}
	
	public static double getDBI(ArrayList<ArrayList<double[]>> al) {
		return getDBI(al, 2, 2);
	}
}
