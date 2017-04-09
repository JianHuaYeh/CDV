package clustering;



import java.io.*;
import java.util.*;
/**
 *
 * @author Administrator
 */
public class KMeansClusteringOrig {
    private HashMap data;
    private int dim;

    public static void main(String[] args) {
        //KMeansClustering kmc = new KMeansClustering(args[0]);
        String fname = "Elliptical_10_2.txt";
    	//String fname = "/home/jhyeh/workspace/hcv/data/jh.3.0.500.txt";
    	KMeansClusteringOrig kmc = new KMeansClusteringOrig(fname);
    	for (int i=0; i<100; i++) {
    		kmc.doClustering(0, 10);
    	}        
    }

    private double[][] findRanges() {
        double[][] ranges = new double[this.dim][];
        for (int i=0; i<this.dim; i++) {
            double[] range = new double[2];
            for (Iterator it=this.data.keySet().iterator(); it.hasNext(); ) {
                double[] freqs = (double[])this.data.get(it.next());
                double freq = freqs[i];
                if (freq < range[0]) range[0] = freq;
                else if(freq > range[1]) range[1] = freq;
            }
            ranges[i] = range;
        }
        return ranges;
    }


    public void doClustering(int method, int k) {
        // ranges[this.dim][2]
        double[][] ranges = findRanges();
        // clusters[k][this.dim]
        double[][] clusters = new double[k][this.dim];
        
        boolean done = false;
        while (!done) {
        	// random initial centroids
        	for (int i=0; i<k; i++) {
        		for (int j=0; j<this.dim; j++) {
        			double min = ranges[j][0];
        			double max = ranges[j][1];
        			clusters[i][j] = min+(max-min)*Math.random();
        		}
        	}

        	// test if every centroid get member(s)
        	ArrayList bestmatches = new ArrayList();
        	for (int i=0; i<k; i++) bestmatches.add(new HashSet());
            for (Iterator it=this.data.keySet().iterator(); it.hasNext(); ) {
                String blog = (String)it.next();
                double[] freqs = (double[])this.data.get(blog);
                int bestmatch = 0;
                double d = Double.MIN_VALUE;
                for (int i=0; i<k; i++) {
                    double sim = Similarity.similarity(clusters[i], freqs, method);
                    if (d < sim) { d = sim; bestmatch = i; }
                }
                HashSet set = (HashSet)bestmatches.get(bestmatch);
                set.add(blog);
            }
            done=true;
            for (Iterator it=bestmatches.iterator(); it.hasNext(); ) {
            	HashSet set = (HashSet)it.next();
            	if (set.size() == 0) {
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

        ArrayList lastmatches = new ArrayList();
        int t=0;
        for (; t<100; t++) {
            ArrayList bestmatches = new ArrayList();
            for (int i=0; i<k; i++) bestmatches.add(new HashSet());

            for (Iterator it=this.data.keySet().iterator(); it.hasNext(); ) {
                String blog = (String)it.next();
                double[] freqs = (double[])this.data.get(blog);
                int bestmatch = 0;
                double d = Double.MIN_VALUE;
                for (int i=0; i<k; i++) {
                    double sim = Similarity.similarity(clusters[i], freqs, method);
                    if (d < sim) { d = sim; bestmatch = i; }
                }
                HashSet set = (HashSet)bestmatches.get(bestmatch);
                set.add(blog);
            }

            if (bestmatches.equals(lastmatches)) break;
            lastmatches = bestmatches;

            for (int i=0; i<k; i++) {
                HashSet set = (HashSet)bestmatches.get(i);
                int count=0;
                double[] row = new double[this.dim];
                for (Iterator it=set.iterator(); it.hasNext(); ) {
                    String blog = (String)it.next();
                    double[] freqs = (double[])this.data.get(blog);
                    for (int j=0; j<this.dim; j++) {
                        row[j] += freqs[j];
                    }
                    count++;
                }
                for (int j=0; j<row.length; j++) {
                    if (count > 0) {
                        row[j] /= count;
                    }
                }
                clusters[i] = row;
            }
        }
        
        //if (t==100) System.err.println("KMeansClustering loop upper bound reached.");
        int[] setsize = new int[k];
        int count=0;
        for (Iterator it=lastmatches.iterator(); it.hasNext(); ) {
        	HashSet set = (HashSet)it.next();
        	//System.err.println("Set content(size:"+set.size()+"): "+set);
        	setsize[count++] = set.size();
        }
        Arrays.sort(setsize);
        for (int i: setsize)
        	System.err.print(i+" ");
        System.err.println();
    }

    public KMeansClusteringOrig(String s) {
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

    public HashMap loadData(String fname) {
        HashMap result = new HashMap();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            String line="";
            // first line is header line
            int n = countWords(br.readLine());
            this.dim = n;
            int count=0;
            System.out.println("Data file contains "+n+" kinds of words.");
            while ((line=br.readLine()) != null) {
                Object[] objs = makeData(line, n);
                if (objs == null) continue; 
                result.put(objs[0], objs[1]);
                count++;
            }
            br.close();
            System.out.println("Total "+count+" records.");
            return result;
        } catch (Exception e) {
           e.printStackTrace(System.err);
        }
        return null;
    }

}
