package clustering;

import java.io.*;
import java.util.*;

public class KMeansClustering {
	public HashMap data;
    private int dim, n;
	boolean debug=false;

    public static void main(String[] args) {
        //KMeansClustering kmc = new KMeansClustering(args[0]);
    	//String fname = "/home/jhyeh/workspace/kmeans/data/blogdata.txt";
    	String fname = "jeremy.1-0.txt";
    	//String fname = "/home/jhyeh/workspace/hcv/data/jh.3.0.500.txt";
    	KMeansClustering kmc = new KMeansClustering(fname);
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
    
    private void normalizeData(double[][] ranges) {
        for (Iterator it=this.data.keySet().iterator(); it.hasNext(); ) {
        	Object key = it.next();
            double[] freqs = (double[])this.data.get(key);
            for (int i=0; i<this.dim; i++) {
        		double range = Math.abs(ranges[i][0]-ranges[i][1]);
        		double base = ranges[i][0];
            	freqs[i] = (freqs[i]-base)/range;
            }
            this.data.put(key, freqs);
        }
    }
	
    public void debug() 
	{
		debug=true;
	}

	public static ArrayList lastmatches = new ArrayList();
	public static double[][] clusters; //center
    public void doClustering(int method, int k) 
	{
        // ranges[this.dim][2]
        double[][] ranges = findRanges();
        normalizeData(ranges);
        // clusters[k][this.dim]
        // now every dim data is normalized to 0~1
        clusters = new double[k][this.dim];
        boolean done = false;
		
		if(true)
		{
			int rnd[]=new int[k];
			HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
			for(int i=0; i<k; i++)	tmp.put(i, i);
			for(int i=0, s=this.data.size(); i<k; i++) //�~�P
			{
				int r=(int)(Math.random()*s);
				if(tmp.get(i)==null)	tmp.put(i, i);
				if(tmp.get(r)==null)	tmp.put(r, r);
				int t=tmp.get(r);
				tmp.put(r, tmp.get(i));
				tmp.put(i, t);
//System.err.println("sort:"+i+":"+t+":"+r);
			}
			for(int i=0; i<k; i++)
				rnd[i]=tmp.get(i);
			Arrays.sort(rnd);
if(debug)
{
	for(int i=0; i<k; i++) System.err.print(rnd[i]+",");
	System.err.println("center");
}
			Iterator it=this.data.keySet().iterator();
			for(int i=0, j=0; i<k; j++)
			{
				double[] pt = (double[])this.data.get(it.next());
				if(rnd[i]<=j)
					clusters[i++]=pt;
			}
		}

        int t=0;
        for (; t<100; t++) {
            ArrayList bestmatches = new ArrayList();
            for (int i=0; i<k; i++) bestmatches.add(new HashSet());

            for (Iterator it=this.data.keySet().iterator(); it.hasNext(); ) 
			{
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
if(debug)
{
	for (Iterator it=lastmatches.iterator(); it.hasNext(); ) {
		HashSet set = (HashSet)it.next();
		System.err.print(set.size()+",");
	}
	System.err.println("");
}
            for (int i=0; i<k; i++) //��������
			{
                HashSet set = (HashSet)bestmatches.get(i);
                int count=0;
                double[] row = new double[this.dim];
                for (Iterator it=set.iterator(); it.hasNext(); ) 
				{
                    String blog = (String)it.next();
                    double[] freqs = (double[])this.data.get(blog);
                    for (int j=0; j<this.dim; j++)
                        row[j] += freqs[j];
                    count++;
                }
				if (count == 0) continue;
                for (int j=0; j<row.length; j++)	row[j] /= count;
                clusters[i] = row;
            }
        }
        
        //if (t==100) System.err.println("KMeansClustering loop upper bound reached.");
        int[] setsize = new int[k];
        int count=0;
		StringBuffer str=new StringBuffer();
System.err.print("*"+k+"*");		
        for (int i=0; i<k; i++) {
        	HashSet set = (HashSet)lastmatches.get(i);
System.err.print(","+set.size());
//str.append(","+Arrays.toString(clusters[i]));
//        	setsize[count++] = set.size();
        }
System.err.println(str.toString());

    }

    public KMeansClustering(String s) {
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

}
