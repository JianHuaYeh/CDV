package clustering;

import java.util.ArrayList;

public class IndexSilhouette {
	
	public static double getAi(ArrayList<double[]> set, int ii) {
		double[] pt=set.get(ii);
		double sum=0.0;
		int size=set.size();
		for (int i=0; i<size; i++) {
			if (i==ii) continue;
			double[] pt1=set.get(i);
			sum += Similarity.eucledianDistance(pt, pt1);
		}
		return sum/(size-1);
	}
	
	public static double getBi(ArrayList<ArrayList<double[]>> al, int ii, int iii) {
		int K = al.size();
		ArrayList<double[]> centroids = IndexUtil.getCentroids(al);
		ArrayList<double[]> seti = al.get(ii);
		double[] pt = seti.get(iii);
		double min=Double.MAX_VALUE;
		for (int i=0; i<K; i++) {
			if (i==ii) continue;
			double[] centroid=centroids.get(i);
			double dist = Similarity.eucledianDistance(centroid, pt);
			if (dist < min) min=dist;
		}
		return min;
	}

	public static double getSilhouette(ArrayList<ArrayList<double[]>> al) {
		// input clustering result, output index value
		int K = al.size();
		double sum=0.0;
		int count=0;
		for (int i=0; i<K; i++) {
			ArrayList<double[]> seti = al.get(i);
			for (int j=0; j<seti.size(); j++) {
				double ai=getAi(seti, j);
				double bi=getBi(al, i, j);
				if (ai < bi) sum+=(1.0-ai/bi);
				else if (ai > bi) sum+=(bi/ai-1.0);
				count++;
			}
		}
		return sum/count;
	}
}
