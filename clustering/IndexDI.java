package clustering;

import java.util.ArrayList;

public class IndexDI {
	
	private static double diameter(ArrayList<double[]> set) {
		//Object[] objs = set.toArray();
		double max = Double.MIN_VALUE;
		//for (int i=0; i<objs.length; i++) {
		for (int i=0; i<set.size(); i++) {
			//double[] pt1 = (double[])objs[i];
			double[] pt1 = set.get(i);
			//for (int j=i+1; j<objs.length; j++) {
			for (int j=i+1; j<set.size(); j++) {
				//double[] pt2 = (double[])objs[j];
				double[] pt2 = set.get(j);
				double dist = Similarity.eucledianDistance(pt1, pt2);
				if (dist > max) max = dist;
			}
		}
		return max;
	}
	
	public static double getDI(ArrayList<ArrayList<double[]>> al) {
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
			for (int j=i+1; j<al.size(); j++) {
				ArrayList<double[]> setj = al.get(j);
				double d = IndexUtil.distInterClusterMin(seti, setj);
				d /= maxDiameter;
				if (d < vd) vd = d;
			}
		}
		return vd;
	}

}
