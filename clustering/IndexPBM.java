package clustering;

import java.util.ArrayList;
import java.util.Iterator;

public class IndexPBM {
	
	private static double PBM_E1(ArrayList<ArrayList<double[]>> al) {
		// find centroid (mean point) with 1 cluster only configuration
		double[] centroid = new double[al.get(0).get(0).length];
		int count=0;
		for (Iterator<ArrayList<double[]>> it=al.iterator(); it.hasNext(); ) {
			ArrayList<double[]> set = it.next();
			for (Iterator<double[]> it2=set.iterator(); it2.hasNext(); ) {
				double[] point = it2.next();
				for (int i=0; i<point.length; i++) {
					centroid[i] += point[i];
				}
				count++;
			}			
		}
		if (count==0) return 0.0;
		for (int i=0; i<centroid.length; i++) {
			centroid[i] /= count;
		}
		
		// now we have centroid
		double sum = 0.0;
		for (Iterator<ArrayList<double[]>> it=al.iterator(); it.hasNext(); ) {
			ArrayList<double[]> set = it.next();
			for (Iterator<double[]> it2=set.iterator(); it2.hasNext(); ) {
				double[] point = it2.next();
				double dist = Similarity.eucledianDistance(centroid, point);
				sum += dist;
			}			
		}
		
		return sum;
	}

	private static double PBM_Ei(ArrayList<double[]> set) {
		// find centroid (mean point)
		double[] centroid = IndexUtil.findCentroid(set);
		
		// calculate S(i,q)
		double sum = 0.0;
		for (Iterator<double[]> it=set.iterator(); it.hasNext(); ) {
			double[] point = it.next();
			double dist = Similarity.eucledianDistance(centroid, point);
			sum += dist;
		}
		return sum;
	}
	
	private static double PBM_DK(ArrayList<ArrayList<double[]>> al) {
		double max = Double.MIN_VALUE;
		int K = al.size();
		for (int i=0; i<K; i++) {
			ArrayList<double[]> seti = al.get(i);
			double[] ci = IndexUtil.findCentroid(seti);
			for (int j=i+1; j<K; j++) {
				ArrayList<double[]> setj = al.get(j);
				double[] cj = IndexUtil.findCentroid(setj);
				double dist = Similarity.eucledianDistance(ci, cj);
				if (dist > max) max = dist;
			}
		}
		return max;
	}
	
	public static double getPBM(ArrayList<ArrayList<double[]>> al) {
		// input clustering result, output index value
		int K = al.size();
		//double E1 = PBM_Ei(al.get(0));
		double E1 = PBM_E1(al);
		double EK = 0.0;
		for (int i=0; i<K; i++) {
			ArrayList<double[]> seti = al.get(i);
			double Ei = PBM_Ei(seti);
			EK += Ei;
		}
		double DK = PBM_DK(al);
		double pbm = Math.pow((1.0/K)*(E1/EK)*DK, 2);
		return pbm;
	}

}
