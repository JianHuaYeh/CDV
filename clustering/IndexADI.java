package clustering;

import java.util.ArrayList;

public class IndexADI {
	private static double diameter(ArrayList<double[]> set) {
		//double[] centroid = IndexUtil.findCentroid(set);
		
		Object[] objs = set.toArray();
		double max = Double.MIN_VALUE;
		for (int i=0; i<objs.length; i++) {
			double[] pt1 = (double[])objs[i];
			for (int j=i+1; j<objs.length; j++) {
				double[] pt2 = (double[])objs[j];
				double dist = Similarity.eucledianDistance(pt1, pt2);
				if (dist > max) max = dist;
			}
		}
		return max;
	}
	
	/*private static double distTriInequality(ArrayList<double[]> set1, ArrayList<double[]> set2) {
		double[] centroid = IndexUtil.findCentroid(set2);
		Object[] objs1 = set1.toArray();
		Object[] objs2 = set2.toArray();
		double min = Double.MIN_VALUE;
		for (int i=0; i<objs1.length; i++) {
			double[] pt1 = (double[])objs1[i];
			double dist1 = Similarity.eucledianDistance(centroid, pt1); 
			for (int j=0; j<objs2.length; j++) {
				double[] pt2 = (double[])objs2[j];
				double dist2 = Similarity.eucledianDistance(centroid, pt2);
				double dist = Math.abs(dist1-dist2);
				if (dist < min) min = dist;
			}
		}
		return min;
	}*/
	
	private static double distTriInequality(ArrayList<double[]> set1, ArrayList<double[]> set2) {
		double[] centroid = IndexUtil.findCentroid(set2);
		double min = Double.MAX_VALUE;
		for (int i=0; i<set1.size(); i++) {
			double[] pt1 = set1.get(i);
			double dist1 = Similarity.eucledianDistance(centroid, pt1); 
			for (int j=0; j<set2.size(); j++) {
				double[] pt2 = set2.get(j);
				double dist2 = Similarity.eucledianDistance(centroid, pt2);
				double dist = Math.abs(dist1-dist2);
				if (dist < min) min = dist;
			}
		}
		return min;
	}

	public static double getADI(ArrayList<ArrayList<double[]>> al) {
		// input clustering result, output index value
		double maxDiameter = Double.MIN_VALUE;
		for (int k=0; k<al.size(); k++) {
			ArrayList<double[]> set = al.get(k);
			double dia = diameter(set);
			if (dia > maxDiameter) maxDiameter=dia;
		}
		
		double vd = Double.MAX_VALUE;
		for (int i=0; i<al.size(); i++) {
			ArrayList<double[]> seti = al.get(i);
			for (int j=0; j<al.size(); j++) {
				if (i == j) continue;
				ArrayList<double[]> setj = al.get(j);
				//double d = IndexUtil.distInterClusterMin(seti, setj);
				double d = distTriInequality(seti, setj);
				d /= maxDiameter;
				if (d < vd) vd = d;
			}
		}
		
		return vd;
	}

}
