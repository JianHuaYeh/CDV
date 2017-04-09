package clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

public class FCMClustering extends FCMOrig {
	public HashMap<String, double[]> data;
    private int dim, n;
	public static ArrayList<HashSet<String>> lastmatches = new ArrayList<HashSet<String>>();
	public static double[][] clusters; //center
	public static double[][] umatrix; //membership matrix

	public static void main(String[] args) {
        //KMeansClustering kmc = new KMeansClustering(args[0]);
    	//String fname = "/home/jhyeh/workspace/kmeans/data/blogdata.txt";
    	String fname = "CircularB_5_2.txt";
    	//String fname = "/home/jhyeh/workspace/hcv/data/jh.3.0.500.txt";
    	FCMClustering fcm = new FCMClustering(fname);
    	for (int i=0; i<1; i++) {
    		fcm.doClustering(0, 10);
    	}
	}
	
	public FCMClustering(String s) {
		//FCMOrig(int numpattern, int dimension, int cata, int maxcycle, double m, double limit)
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
            //System.err.println("Data file contains "+n+" kinds of words.");
            while ((line=br.readLine()) != null) {
                Object[] objs = makeData(line, n);
                if (objs == null) continue; 
                result.put(objs[0], objs[1]);
                count++;
            }
            br.close();
            //System.err.println("Total "+count+" records.");
            return result;
        } catch (Exception e) {
            //System.err.println(e);
        	e.printStackTrace(System.err);
        }
        return null;
    }
	
	public HashMap<Integer, String> makePattern(HashMap<String, double[]> data, double[] pattern) {
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		int index = 0;
		int count=0;
		for (Iterator<String> it=data.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			result.put(count, key);
			count++;
			double[] vec = data.get(key);
			for (double d: vec) pattern[index++] = d;
		}
		//System.err.println("Total "+count+" records, "+index+" cells, dim="+this.dim);
		return result;
	}
	
	public Object[] makeResult(double[] um, HashMap<Integer, String> map, double[] rescenter, int k) {
		// umatrix processing
		double[][] umatrix = new double[k][this.data.size()];
		int index=0;
		for (int i=0; i<k; i++)
			for (int j=0; j<this.data.size(); j++)
				umatrix[i][j] = um[index++];
		
		// cluster center processing
		double[][] clusters = new double[k][this.dim];
		index=0;
		for (int i=0; i<k; i++)
			for (int j=0; j<this.dim; j++)
				clusters[i][j] = rescenter[index++];
		
		// lastmatches: cluster result processing
		ArrayList<HashSet<String>> bestmatches = new ArrayList<HashSet<String>>();
		for (int i=0; i<k; i++) {
			bestmatches.add(new HashSet<String>());
		}
		for (int j=0; j<this.data.size(); j++) {
			// find the biggest membership
			double max = Double.MIN_VALUE;
			int maxidx = -1;
			for (int i=0; i<k; i++) {
				if (umatrix[i][j] > max) {
					max = umatrix[i][j];
					maxidx = i;
				}
			}
			//System.out.println("maxidx="+maxidx);
			String key = map.get(j);
			bestmatches.get(maxidx).add(key);
		}
		
		Object[] result = new Object[]{umatrix, clusters, bestmatches};
		return result;
	}
    
	public void doClustering(int method, int k) {
		// fit current data into FCMOrig
		double[] pattern = new double[this.data.size() * this.dim];
		double[] um = new double[this.data.size() * k];
		double[] rescenter = new double[k * this.dim];
		double result=0;
        numpattern = data.size();
		dimension = dim;
		cata = k;
		m = 2;
		limit = 0.1;
		maxcycle = 100;
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		
		//����
		//getPattern(pattern);
		map = makePattern(this.data, pattern);
		
		//�銵CM_fun
		FCM_fun(pattern, this.dim, this.data.size(), k, m, maxcycle, limit, um, rescenter, result);
		
		//颲蝏��
		Export(um, rescenter, "/Desktop/");
		Object[] objs = makeResult(um, map, rescenter, k);
		umatrix = (double[][])objs[0];
		clusters = (double[][])objs[1];
		lastmatches = (ArrayList<HashSet<String>>)objs[2];
		
		/*String str = "(";
		for (HashSet<String> set: lastmatches) {
			str += set.size()+",";
		}
		str = str.substring(0, str.length()-1)+")";
		System.err.println(str);*/
	}
}
