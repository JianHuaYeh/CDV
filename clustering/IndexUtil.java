package clustering;

import java.util.*;

public class IndexUtil {
	
	public static double log10(double d) {
		return Math.log(d)/Math.log(10.0);
	}
	
	public static double distIntraClusterMax(ArrayList<double[]> set) {
		Object[] objs = set.toArray();
		double max = Double.MIN_VALUE;
		for (int i=0; i<objs.length; i++) {
			double[] pt1 = (double[])objs[i];
			for (int j=i+1; j<objs.length; j++) {
				double[] pt2 = (double[])objs[j];
				double d = Similarity.eucledianDistance(pt1, pt2);
				if (d > max) max = d;
			}
		}
		return max;
	}

	public static double distIntraClusterMin(ArrayList<double[]> set) {
		Object[] objs = set.toArray();
		double min = Double.MAX_VALUE;
		for (int i=0; i<objs.length; i++) {
			double[] pt1 = (double[])objs[i];
			for (int j=i+1; j<objs.length; j++) {
				double[] pt2 = (double[])objs[j];
				double d = Similarity.eucledianDistance(pt1, pt2);
				if (d < min) min = d;
			}
		}
		return min;
	}

	public static double distIntraClusterAvg(ArrayList<double[]> set, double[] pt) {
		Object[] objs = set.toArray();
		double sum = 0.0;
		int count = 0;
		for (int i=0; i<objs.length; i++) {
			double[] pt1 = (double[])objs[i];
			sum += Similarity.eucledianDistance(pt1, pt);
			count++;
		}
		return ((count==0)? 0.0: sum/count);
	}
	
	public static double distIntraClusterAvg(ArrayList<double[]> set) {
		Object[] objs = set.toArray();
		double sum = 0.0;
		int count = 0;
		for (int i=0; i<objs.length; i++) {
			double[] pt1 = (double[])objs[i];
			for (int j=i+1; j<objs.length; j++) {
				double[] pt2 = (double[])objs[j];
				sum += Similarity.eucledianDistance(pt1, pt2);
				count++;
			}
		}
		return ((count==0)? 0.0: sum/count);
	}

	public static double distInterClusterCentroid(ArrayList<double[]> s1, ArrayList<double[]> s2) {
		double[] pt1 = findCentroid(s1);
		double[] pt2 = findCentroid(s2);
		return Similarity.eucledianDistance(pt1, pt2);
	}

	public static double distInterClusterMin(ArrayList<double[]> s1, ArrayList<double[]> s2) {
		Object[] objs1 = s1.toArray();
		Object[] objs2 = s2.toArray();
		double min = Double.MAX_VALUE;
		for (int i=0; i<objs1.length; i++) {
			double[] pt1 = (double[])objs1[i];
			for (int j=0; j<objs2.length; j++) {
				double[] pt2 = (double[])objs2[j];
				double dist = Similarity.eucledianDistance(pt1, pt2);
				if (dist < min) min = dist;
			}
		}
		return min;
	}
	
	public static double distInterClusterMax(ArrayList<double[]> s1, ArrayList<double[]> s2) {
		Object[] objs1 = s1.toArray();
		Object[] objs2 = s2.toArray();
		double max = Double.MIN_VALUE;
		for (int i=0; i<objs1.length; i++) {
			double[] pt1 = (double[])objs1[i];
			for (int j=0; j<objs2.length; j++) {
				double[] pt2 = (double[])objs2[j];
				double dist = Similarity.eucledianDistance(pt1, pt2);
				if (dist > max) max = dist;
			}
		}
		return max;
	}

	public static double distInterClusterAvg(ArrayList<double[]> s1, ArrayList<double[]> s2) {
		Object[] objs1 = s1.toArray();
		Object[] objs2 = s2.toArray();
		double sum = 0.0;
		int count = 0;
		for (int i=0; i<objs1.length; i++) {
			double[] pt1 = (double[])objs1[i];
			for (int j=0; j<objs2.length; j++) {
				double[] pt2 = (double[])objs2[j];
				sum += Similarity.eucledianDistance(pt1, pt2);
				count++;
			}
		}
		return ((count==0)? 0.0: sum/count);
	}
	
	public static double distInterCluster(ArrayList<double[]> s1, ArrayList<double[]> s2, int method) {
		switch (method) {
			case 1: return distInterClusterMin(s1, s2);
			case 2: return distInterClusterAvg(s1, s2);
			case 0:
			default: return distInterClusterMax(s1, s2);
		}
	}
	
	public static double[] findCentroid(ArrayList<double[]> set) {
		double[] centroid = null;
		for (Iterator<double[]> it=set.iterator(); it.hasNext(); ) {
			double[] point = it.next();
			if (centroid == null) {
				centroid = new double[point.length];
				for (int i=0; i<centroid.length; i++) centroid[i] = 0.0;
			}
			for (int i=0; i<centroid.length; i++) centroid[i] += point[i];
		}
		for (int i=0; i<centroid.length; i++) centroid[i] /= set.size();
		return centroid;
	}
	
	public static ArrayList<double[]> getCentroids(ArrayList<ArrayList<double[]>> al) {
		ArrayList<double[]> result = new ArrayList<double[]>();
		for (ArrayList<double[]> set: al) {
			double[] centroid = findCentroid(set);
			result.add(centroid);
		}
		return result;
	}
	
}
