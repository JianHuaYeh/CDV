package clustering;



import java.io.*;
import java.util.*;
/**
 *
 * @author Administrator
 */
public class KMeansClusteringOrig2 {
    private HashMap<String, double[]> data;
    private ArrayList<HashSet<String>> clusteringResult;
    private int dim;

    public static void main(String[] args) {
        //KMeansClustering kmc = new KMeansClustering(args[0]);
    	//String fname = "/home/jhyeh/workspace/kmeans/data/blogdata.txt";
    	String fname = "Circular_6_2.txt";
    	//String fname = "/home/jhyeh/workspace/hcv/data/jh.3.0.500.txt";
    	KMeansClusteringOrig2 kmc = new KMeansClusteringOrig2(fname);
    	//for (int i=0; i<100; i++) {
    	kmc.doClustering(0, 6);
    	//}
    }

    private double[][] findRanges() {
        double[][] ranges = new double[this.dim][];
        for (int i=0; i<this.dim; i++) {
            double[] range = new double[2];
            for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
                double[] freqs = this.data.get(it.next());
                double freq = freqs[i];
                if (freq < range[0]) range[0] = freq;
                else if(freq > range[1]) range[1] = freq;
            }
            ranges[i] = range;
        }
        return ranges;
    }
    
    private void normalizeData(double[][] ranges) {
        for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
        	String key = it.next();
            double[] freqs = this.data.get(key);
            for (int i=0; i<this.dim; i++) {
        		double range = Math.abs(ranges[i][0]-ranges[i][1]);
        		double base = ranges[i][0];
            	freqs[i] = (freqs[i]-base)/range;
            }
            this.data.put(key, freqs);
        }
    }

    public void doClustering(int method, int k) {
        // ranges[this.dim][2]
        double[][] ranges = findRanges();
        //normalizeData(ranges);
        // clusters[k][this.dim]
        double[][] clusters = new double[k][this.dim];
        // now every dim data is normalized to 0~1
        
        boolean done = false;
        int count=0;
        while (!done && (count++<1000)) {
        //while (!done) {
        	// random initial centroids
        	for (int i=0; i<k; i++) {
        		for (int j=0; j<this.dim; j++) {
        			double min = ranges[j][0];
        			double max = ranges[j][1];
        			clusters[i][j] = min+(max-min)*Math.random();
        			//clusters[i][j] = Math.random();
        		}
        	}

        	// test if every centroid get member(s)
        	ArrayList<HashSet<String>> bestmatches = new ArrayList<HashSet<String>>();
        	for (int i=0; i<k; i++) bestmatches.add(new HashSet<String>());
            for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
                String blog = it.next();
                double[] freqs = this.data.get(blog);
                int bestmatch = 0;
                double d = Double.MIN_VALUE;
                for (int i=0; i<k; i++) {
                    double sim = Similarity.similarity(clusters[i], freqs, method);
                    if (d < sim) { d = sim; bestmatch = i; }
                }
                HashSet<String> set = bestmatches.get(bestmatch);
                set.add(blog);
            }
            done=true;
            for (Iterator<HashSet<String>> it=bestmatches.iterator(); it.hasNext(); ) {
            	HashSet<String> set = it.next();
            	if (set.size() <= 1) {
            		done = false;
            		break;
            	}
            }
            /*if (done) {
            	for (Iterator it=bestmatches.iterator(); it.hasNext(); ) {
            		HashSet set = (HashSet)it.next();
            		System.err.println("Initial set content: "+set);
            	}
            }*/
        }
        //System.err.println("Centroids randomly initialized.");

        ArrayList<HashSet<String>> lastmatches = new ArrayList<HashSet<String>>();
        int upperBound=15;
        int t=0;
        for (; t<upperBound; t++) {
            ArrayList<HashSet<String>> bestmatches = new ArrayList<HashSet<String>>();
            for (int i=0; i<k; i++) bestmatches.add(new HashSet<String>());

            for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
                String blog = it.next();
                double[] freqs = this.data.get(blog);
                int bestmatch = 0;
                double d = Double.MIN_VALUE;
                for (int i=0; i<k; i++) {
                    double sim = Similarity.similarity(clusters[i], freqs, method);
                    if (d < sim) { d = sim; bestmatch = i; }
                }
                HashSet<String> set = bestmatches.get(bestmatch);
                set.add(blog);
            }

            if (bestmatches.equals(lastmatches)) break;
            lastmatches = bestmatches;

            for (int i=0; i<k; i++) {
                HashSet<String> set = bestmatches.get(i);
                int count2=0;
                double[] row = new double[this.dim];
                for (Iterator<String> it=set.iterator(); it.hasNext(); ) {
                    String blog = it.next();
                    double[] freqs = (double[])this.data.get(blog);
                    for (int j=0; j<this.dim; j++) {
                        row[j] += freqs[j];
                    }
                    count2++;
                }
                for (int j=0; j<row.length; j++) {
                    if (count > 0) {
                        row[j] /= count2;
                    }
                }
                clusters[i] = row;
            }
        }
        
        //if (t==upperBound) System.err.println("KMeansClustering loop upper bound reached.");
        
        this.clusteringResult = lastmatches;
        
        /*int[] setsize = new int[k];
        count=0;
        for (Iterator it=lastmatches.iterator(); it.hasNext(); ) {
        	HashSet set = (HashSet)it.next();
        	//System.err.println("Set content(size:"+set.size()+"): "+set);
        	setsize[count++] = set.size();
        }
        Arrays.sort(setsize);
        for (int i: setsize)
        	System.err.print(i+" ");
        System.err.println();*/
        
        // make clutering result into ArrayList<ArrayList<double[]>>
    }

    public KMeansClusteringOrig2(String s) {
        this.data = loadData(s);
        if (this.data == null) System.exit(0);
    }

    private int countWords(String line) {
        // first word is "blog name", not counted
        StringTokenizer st = new StringTokenizer(line, "\t");
        return st.countTokens()-1;
    }

    private Object[] makeData(String line, int n) {
        // first word is "blog name"
        double[] freqs = new double[n];
        StringTokenizer st = new StringTokenizer(line, "\t");
        try {
        	String blogname = st.nextToken();
        	for (int i=0; i<n; i++) {
        		freqs[i] = Double.parseDouble(st.nextToken());
        	}
        	Object[] result = {blogname, freqs};
        	return result;
        } catch (Exception e) {
        	return null;
        }
    }

    public HashMap<String, double[]> loadData(String fname) {
        HashMap<String, double[]> result = new HashMap<String, double[]>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            String line="";
            // first line is header line
            int n = countWords(br.readLine());
            this.dim = n;
            int count=0;
            //System.err.println("Data file contains "+n+" kinds of words.");
            while ((line=br.readLine()) != null) {
                Object[] objs = makeData(line, n);
                if (objs == null) continue; 
                result.put((String)objs[0], (double[])objs[1]);
                count++;
            }
            br.close();
            System.err.println("Total "+count+" records.");
            return result;
        } catch (Exception e) {
            //System.err.println(e);
        	e.printStackTrace(System.err);
        }
        return null;
    }
    
    public ArrayList<HashSet<String>> getClusteringResult() {
    	return this.clusteringResult;
    }
    
    public HashMap<String, double[]> getData() {
    	return this.data;
    }

}
