package clustering;

import java.util.*;

/*
 * data settings for Circular_5_2:
 * double[][] centers = {{10,6}, {10,10}, {10,14}, {6,10}, {14,10}};
 * int samplesPerCluster = 50;
 * ArrayList<double[]> pts = generateSphericalSamples(centers, 2, samplesPerCluster);
 * 
 * data settings for Circular_6_2:
 * double[][] centers = {{5,5}, {9,9}, {20,5}, {16,16}, {5,20}, {20,20}};
 * int samplesPerCluster = 50;
 * ArrayList<double[]> pts = generateSphericalSamples(centers, 2, samplesPerCluster);
 * 
 * data settings for Elliptical_10_2:
 * double[][] centers = {{-15,-3}, {-10,0}, {-6,3}, {-2,-6}, {0,0}, {1,8}, {5,4}, {8,10}, {11,-1}, {15,3}};
 * int samplesPerCluster = 50;
 * ArrayList<double[]> pts = generateEllipticalSamples(centers, 2.5, 1.5, samplesPerCluster);
 *
 * data settings for Spherical_4_3:
 * double[][] centers = {{0,0,0}, {5,5,5}, {10,10,10}, {15,15,15}};
 * int samplesPerCluster = 50;
 * ArrayList<double[]> pts = generateSphericalSamples(centers, 3, samplesPerCluster);
 * 
 * data settings for Elliptical2_10_2:
 * double[][] centers = {{5,7}, {10,10}, {14,13}, {18,4}, {20,10}, {21,18}, {25,14}, {28,20}, {31,9}, {35,13}};
 * int samplesPerCluster = 50;
 * ArrayList<double[]> pts = generateEllipticalSamples(centers, 2.5, 1.5, samplesPerCluster);
 * 
 * data settings for Circular_10_2:
 * double[][] centers = {{5,7}, {10,10}, {14,13}, {18,4}, {20,10}, {21,18}, {25,14}, {28,20}, {31,9}, {35,13}};
 * int samplesPerCluster = 50;
 * ArrayList<double[]> pts = generateSphericalSamples(centers, 2, samplesPerCluster);
 * 
 * data settings for CircularA_5_2:
 * double[][] centers = {{6,6}, {2.1,6}, {9.9,6}, {6,2.1}, {6,9.9}};
 * int samplesPerCluster = 100;
 * ArrayList<double[]> pts = generateSphericalSamples(centers, 2, samplesPerCluster);
 * 
 * data settings for CircularA_9_2:
 * double[][] centers = {{6,6}, {2.1,6}, {9.9,6}, {6,2.1}, {6,9.9}, {2.1,2.1}, {2.1,9.9}, {9.9,2.1}, {9.9,9.9}};
 * int samplesPerCluster = 200;
 * ArrayList<double[]> pts = generateSphericalSamples(centers, 2, samplesPerCluster);
 * 
 * data settings for EllipticalA_2_2:
 * double[][] centers = {{2.5,2}, {2.5,4}};
 * int samplesPerCluster = 1000;
 * ArrayList<double[]> pts = generateEllipticalSamples(centers, 3, 1, samplesPerCluster);
 * 
 * data settings for CircularA_9_2:
 * double[][] centers = {{6,6}, {2.1,6}, {9.9,6}, {6,2.1}, {6,9.9}, {2.1, 2.1}, {2.1, 9.9}, {9.9, 2.1}, {9.9, 9.9}};
 * int samplesPerCluster = 200;
 * ArrayList<double[]> pts = generateEllipticalSamples(centers, 2, samplesPerCluster);
 * 
 * data settings for SquareA_4_2:
 * double[][] centers = {{1,1}, {5.1,1}, {1,5.1}, {5.1,5.1}};
 * int samplesPerCluster = 200;
 * ArrayList<double[]> pts = generateSquareSamples(centers, 2, samplesPerCluster);
 * 
 * data settings for CircularB_5_2: (1x5圓)
 * double[][] centers = {{2,10}, {6,10}, {10,10}, {14,10}, {18,10}};
 * int samplesPerCluster = 50;
 * ArrayList<double[]> pts = generateSphericalSamples(centers, 2, samplesPerCluster);
 *
 * data settings for CircularB_2_2: (大+小圓)
 * double[][] centers = {{2,10}, {10,10}, {18,10}};
 * double[] radia = {2.0, 6.0, 2.0};
 * int[] samplesPerCluster = {50, 450, 50};
 * ArrayList<double[]> pts = generateSphericalSamples(centers, 2, samplesPerCluster);
 * int sampleSize = 0;\
 * for (int sample: samplesPerCluster) sampleSize+=sample;
 * 
 * data settings for EllipticalA_2_2: (90度交叉橢圓)
 * double[][] centers = {{10,10}, {10,6}};
 * double[][] radia = {{6,2}, {2,6}};
 * int samplesPerCluster = 1000;
 * ArrayList<double[]> pts = generateEllipticalSamples(centers, radia, samplesPerCluster);
 */

public class SampleGenerator {
	
	public static void main(String[] args) {
		double[][] centers = {{10,10}, {10,6}};
		double[][] radia = {{6,2}, {2,6}};
		int samplesPerCluster = 500;
		ArrayList<double[]> pts = generateEllipticalSamples(centers, radia, samplesPerCluster);
		int sampleSize = centers.length*samplesPerCluster;
		//for (int sample: samplesPerCluster) sampleSize+=sample;
		int count = 0;
		System.out.println(""+sampleSize+"\t0\t1");
		for (Iterator<double[]> it=pts.iterator(); it.hasNext(); ) {
			double[] pt = it.next();
			String outstr = ""+count;
			// for kmeans
			for (double d: pt) {
				outstr += "\t"+d;
			}
			System.out.println(outstr);
			//System.out.println(""+count+"\t"+pt[0]+"\t"+pt[1]+"\t"+pt[2]);
			count++;
		}

		for (Iterator<double[]> it=pts.iterator(); it.hasNext(); ) {
			double[] pt = it.next();
			String outstr = "";
			// for gnuplot
			for (double d: pt) {
				outstr += d+"\t";
			}
			System.err.println(outstr.trim());
			//System.err.println(""+pt[0]+"\t"+pt[1]+"\t"+pt[2]);			
		}
	}

	/*public static ArrayList<double[]> generateSphericalSamples(double[][] centroids, int radius, int samplesPerCluster) {
		ArrayList<double[]> result = new ArrayList<double[]>();
		for (double[] centroid: centroids) {
			//while (result.size() < samplesPerCluster) {
			for (int k=0; k<samplesPerCluster; k++) {
				double[] point = new double[centroid.length];
				for (int i=0; i<centroid.length; i++) {
					double axis_v = centroid[i];
					double bias = Math.random()*radius*2-radius;
					point[i] = axis_v+bias;
				}
				result.add(point);
			}
		}
		return result;
	}*/
	
	private static double distance(double[] centroid, double[] point) {
		double sumSqr = 0.0;
		for (int i=0; i<centroid.length; i++) {
			sumSqr += (centroid[i]-point[i])*(centroid[i]-point[i]);
		}
		return Math.sqrt(sumSqr);
	}
	
	private static boolean inSphere(double[] centroid, double radius, double[] point) {
		double dist = distance(centroid, point);
		return (dist <= radius);
	}
	
	public static ArrayList<double[]> generateSquareSamples(double[][] centroids, double radius, int samplesPerCluster) {
		ArrayList<double[]> result = new ArrayList<double[]>();
		for (double[] centroid: centroids) {
			int count=0;
			while (count < samplesPerCluster) {
				double[] point = new double[centroid.length];
				for (int i=0; i<centroid.length; i++) {
					double axis_v = centroid[i];
					double bias = Math.random()*radius*2-radius;
					point[i] = axis_v+bias;
				}
				result.add(point);
				count++;
			}
		}
		return result;
	}
	
	public static ArrayList<double[]> generateSphericalSamples(double[][] centroids, double radius, int samplesPerCluster) {
		ArrayList<double[]> result = new ArrayList<double[]>();
		for (double[] centroid: centroids) {
			int count=0;
			while (count < samplesPerCluster) {
			//for (int k=0; k<samplesPerCluster; k++) {
				double[] point = new double[centroid.length];
				for (int i=0; i<centroid.length; i++) {
					double axis_v = centroid[i];
					double bias = Math.random()*radius*2-radius;
					point[i] = axis_v+bias;
				}
				if (inSphere(centroid, radius, point)) {
					result.add(point);
					count++;
				}
			}
		}
		return result;
	}
	
	public static ArrayList<double[]> generateSphericalSamples(double[][] centroids, double[] radia, int[] samplesPerCluster) {
		ArrayList<double[]> result = new ArrayList<double[]>();
		for (int i=0; i<centroids.length; i++) {
			double[] centroid = centroids[i];
			double radius = radia[i];
			int samplesPCluster = samplesPerCluster[i];
			int count=0;			
			while (count < samplesPCluster) {
				double[] point = new double[centroid.length];
				for (int j=0; j<centroid.length; j++) {
					double axis_v = centroid[j];
					double bias = Math.random()*radius*2-radius;
					point[j] = axis_v+bias;
				}
				if (inSphere(centroid, radius, point)) {
					result.add(point);
					count++;
				}
			}
		}
		return result;
	}
	
	private static boolean inEllipsis(double[] centroid, double longRadius, double shortRadius, 
			double angle, double[] point) {
		// calculate point angle with axis
		//System.err.println("centroid=("+centroid[0]+","+centroid[1]+")");
		//System.err.println("point=("+point[0]+","+point[1]+")");
		double d = Similarity.eucledianDistance(point, centroid);
		//System.err.println("d="+d);
		double theta = Math.acos((point[0]-centroid[0])/d);
		//System.err.println("theta="+360.0*theta/Math.PI);
		double alpha = theta-angle;
		//System.err.println("alpha="+360.0*alpha/Math.PI);
		// corresponding point on the ellipsis
		double[] pt2 = new double[2];
		//pt2[0] = (centroid[0]+0.5*longRadius*Math.cos(angle))*Math.cos(alpha);
		pt2[0] = centroid[0]+0.5*longRadius*Math.cos(alpha);
		//pt2[1] = centroid[1]+shortRadius*Math.sin(angle)+
		//			(centroid[0]+0.5*longRadius*Math.cos(angle))*Math.sin(alpha);
		pt2[1] = centroid[1]+0.5*shortRadius*Math.sin(alpha);
		double d2 = Similarity.eucledianDistance(pt2, centroid);
		boolean result = (d2 > d);
		//System.err.println("inEllipsis(): "+result);
		return result;
	}
	
	public static ArrayList<double[]> generateEllipticalSamples(double[][] centroids,
			double longRadius, double shortRadius, int samplesPerCluster) {
		// ellipsis is only a 2D shape
		//double angle = Math.random()*Math.PI*2;	// random angle for ellipsis
		double angle = 0.0;	// random angle for ellipsis
		return generateEllipticalSamples(centroids, longRadius, shortRadius, angle, samplesPerCluster);
	}
	
	
	public static ArrayList<double[]> generateEllipticalSamples(double[][] centroids,
			double longRadius, double shortRadius, double angle, int samplesPerCluster) {
		// ellipsis is only a 2D shape
		ArrayList<double[]> result = new ArrayList<double[]>();
		for (double[] centroid: centroids) {
			int count=0;
			while (count < samplesPerCluster) {
				double[] point = new double[2];
				double bias1 = Math.random()*longRadius-0.5*longRadius;
				point[0] = centroid[0]+bias1;
				double bias2 = Math.random()*shortRadius-0.5*shortRadius;
				point[1] = centroid[1]+bias2;
				if (inEllipsis(centroid, longRadius, shortRadius, angle, point)) {
					//System.err.println("In ellipsis");
					result.add(point);
					count++;
				}
			}
		}
		return result;
	}
	
	public static ArrayList<double[]> generateEllipticalSamples(double[][] centroids,
			double[][] radia, int samplesPerCluster) {
		return generateEllipticalSamples(centroids, radia, 0, samplesPerCluster);
	}
	
	public static ArrayList<double[]> generateEllipticalSamples(double[][] centroids,
			double[][] radia, double angle, int samplesPerCluster) {
		// ellipsis is only a 2D shape
		ArrayList<double[]> result = new ArrayList<double[]>();
		for (int i=0; i<centroids.length; i++) {
			double[] centroid = centroids[i];
			double[] radius = radia[i];
			double longRadius = radius[0];
			double shortRadius = radius[1];
			int count=0;
			while (count < samplesPerCluster) {
				double[] point = new double[2];
				double bias1 = Math.random()*longRadius-0.5*longRadius;
				point[0] = centroid[0]+bias1;
				double bias2 = Math.random()*shortRadius-0.5*shortRadius;
				point[1] = centroid[1]+bias2;
				if (inEllipsis(centroid, longRadius, shortRadius, angle, point)) {
					//System.err.println("In ellipsis");
					result.add(point);
					count++;
				}
			}
		}
		return result;
	}
}
