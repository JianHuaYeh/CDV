package clustering;

import java.util.ArrayList;
import java.util.Iterator;

public class IndexHCV {

	public static double HCV_Di1(ArrayList<double[]> set) {
		// find centroid (mean point)
		double[] centroid = IndexUtil.findCentroid(set);
		
		double sum = 0.0;
		int size=set.size();
		for (Iterator<double[]> it=set.iterator(); it.hasNext(); ) {
			double[] point = it.next();
			double dist = Similarity.eucledianDistance(centroid, point);
			double di = IndexUtil.log10(dist*dist+1)/size;
			//double di = IndexUtil.log10(dist*dist/size+1);
			//System.err.println("Di_dist="+dist+", size="+size);
			sum += di;
		}
		return sum;
	}

	public static double HCV_Di3(ArrayList<double[]> set) {
		// find centroid (mean point)
		double[] centroid = IndexUtil.findCentroid(set);
		
		double sum = 0.0;
		int size=set.size();
		for (Iterator<double[]> it=set.iterator(); it.hasNext(); ) {
			double[] point = it.next();
			double dist = Similarity.eucledianDistance(centroid, point);
			//double di = IndexUtil.log10((dist*dist+1)/size);
			double di = IndexUtil.log10(dist*dist/size+1);
			//System.err.println("Di_dist="+dist+", size="+size);
			sum += di;
		}
		return sum;
	}
	
	public static double getHCV(ArrayList<ArrayList<double[]>> al) {
		int K = al.size();
		double sum = 0.0;
		//double setDist = IndexUtil.distInterClusterCentroid(al, 0);
		for (int i=0; i<K; i++) {
			ArrayList<double[]> seti = al.get(i);
			double sum2 = 0.0;
			double Di = HCV_Di3(seti);
			for (int j=0; j<K; j++) {
				if (i==j) continue;
				ArrayList<double[]> setj = al.get(j);
				//double setDist = IndexUtil.distInterClusterMax(seti, setj);
				// 0:max, 1:min, 2:avg
				double setDist = IndexUtil.distInterCluster(seti, setj, 0);
				//double setDist = IndexUtil.distInterClusterCentroid(seti, setj);
				//System.err.println("i="+i+", j="+j+", Di="+Di+", maxDist="+maxDist);
				sum2 += Di/setDist;
			}
			sum += sum2/(K-1);
		}
		
		sum /= K;
		return sum;
	}

	public static double getHCV2(ArrayList<ArrayList<double[]>> al) {
		int K = al.size();
		double sum = 0.0;
		//double setDist = IndexUtil.distInterClusterCentroid(al, 0);
		for (int i=0; i<K; i++) {
			ArrayList<double[]> seti = al.get(i);
			double sum2 = 0.0;
			double Di = HCV_Di2(seti);
			for (int j=0; j<K; j++) {
				if (i==j) continue;
				ArrayList<double[]> setj = al.get(j);
				//double setDist = IndexUtil.distInterClusterMax(seti, setj);
				// 0:max, 1:min, 2:avg
				double setDist = IndexUtil.distInterCluster(seti, setj, 0);
				//double setDist = IndexUtil.distInterClusterCentroid(seti, setj);
				//System.err.println("i="+i+", j="+j+", Di="+Di+", maxDist="+maxDist);
				sum2 += Di/setDist;
			}
			sum += sum2/(K-1);
		}
		
		sum /= K;
		return sum;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	
	public static double HCV_Di2(ArrayList<double[]> set) {
		// find centroid (mean point)
		double[] centroid = IndexUtil.findCentroid(set);
		
		double sum = 0.0;
		int size=set.size();
		for (Iterator<double[]> it=set.iterator(); it.hasNext(); ) {
			double[] point = it.next();
			double dist = Similarity.eucledianDistance(centroid, point);
			//double di = IndexUtil.log10((dist*dist)/size+1);
			double di = dist*dist;
			//System.err.println("Di_dist="+dist+", size="+size);
			sum += di;
		}
		sum = Math.sqrt(sum/size);
		
		return sum;
	}

	public static double HCV_Di4(ArrayList<double[]> set) {
		// find centroid (mean point)
		double[] centroid = IndexUtil.findCentroid(set);
		
		double sum = 0.0;
		int size=set.size();
		for (Iterator<double[]> it=set.iterator(); it.hasNext(); ) {
			double[] point = it.next();
			double dist = Similarity.eucledianDistance(centroid, point);
			//double di = IndexUtil.log10((dist*dist)/size+1);
			double di = dist*dist;
			//System.err.println("Di_dist="+dist+", size="+size);
			sum += di;
		}
		//sum = Math.sqrt(sum/size);
		sum /= size;
		
		return sum;
	}

}
