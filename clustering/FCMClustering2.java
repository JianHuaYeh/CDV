package clustering;

import java.io.*;
import java.util.*;

public class FCMClustering2 {
	public HashMap<String, double[]> data;
	public static ArrayList lastmatches = new ArrayList();
	public static double[][] clusters; //center
    private int dim, n;
	boolean debug=false;
	// fcm
	public int maxcycle;//��憭抒�翮隞�甈⊥
	public double m;//��m
	public double limit;//霂臬榆���
	public HashMap<String, Double>[] umatrix;

    public static void main(String[] args) {
        //KMeansClustering kmc = new KMeansClustering(args[0]);
    	//String fname = "/home/jhyeh/workspace/kmeans/data/blogdata.txt";
    	String fname = "jh.2.1.5.txt";
    	//String fname = "/home/jhyeh/workspace/hcv/data/jh.3.0.500.txt";
    	FCMClustering2 fcm = new FCMClustering2(fname);
    	fcm.debug();
    	for (int i=0; i<100; i++) {
    		fcm.doClustering(0, 10);
    	}
    }

    public FCMClustering2(String s) {
        this.data = loadData(s);
        if (this.data == null) System.exit(0);
        // fcm
        this.limit = 0.1;
        this.m = 2;
        this.maxcycle = 500;
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
	
    public void debug() { debug=true; }

    public void doClustering(int method, int k) {
    	// fcm
    	double min_dis = 0.001;//頝氖���撠��
    	double f_temp, lastv=0.0, delta, t_temp;
    	this.umatrix = (HashMap<String, Double>[])new HashMap[k];
    	
        // ranges[this.dim][2]
        double[][] ranges = findRanges();
        normalizeData(ranges);
        // clusters[k][this.dim]
        // now every dim data is normalized to 0~1
        this.clusters = new double[k][this.dim];
        boolean done = false;
		
		if (true) {
			int rnd[]=new int[k];
			HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
			for(int i=0; i<k; i++) tmp.put(i, i);
			for(int i=0, s=this.data.size(); i<k; i++) {
				int r=(int)(Math.random()*s);
				if(tmp.get(i)==null) tmp.put(i, i);
				if(tmp.get(r)==null) tmp.put(r, r);
				int t=tmp.get(r);
				tmp.put(r, tmp.get(i));
				tmp.put(i, t);
				//System.err.println("sort:"+i+":"+t+":"+r);
			}
			for(int i=0; i<k; i++) rnd[i]=tmp.get(i);
			Arrays.sort(rnd);
			
			if(debug) {
				for(int i=0; i<k; i++) System.err.print(rnd[i]+",");
				System.err.println("center");
			}
			
			Iterator<String> it=this.data.keySet().iterator();
			for(int i=0, j=0; i<k; j++) {
				double[] pt = (double[])this.data.get(it.next());
				if(rnd[i]<=j) clusters[i++]=pt;
			}
		}
		
		int cycle = 0;
		delta = Double.MAX_VALUE;
		while ((cycle<this.maxcycle) && (delta>this.limit)) {
			//霈∠����
			for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
				String key = it.next();
				int flagtemp = 0;
				int count = 0;
				double[] v1 = this.data.get(key);
				
				for(int j=0; j<k; j++) {
					f_temp = 0;
					for(int t=0; t<k; t++) {
						double[] v2 = this.clusters[t];
						double d = Similarity.eucledianDistance(v1, v2);
						if (d > min_dis) f_temp += Math.pow(d, -2/(m-1));
						else flagtemp = 1;
					} // for t
						
					double[] v3 = this.clusters[j];
					if (this.umatrix[j] == null) this.umatrix[j] = new HashMap<String, Double>();
					if(flagtemp == 1) {
						this.umatrix[j].put(key, 0.0);
						flagtemp = 0;
					}
					
					//憒��雿踹�|Xk-Vi||=0��蝵格���-1
					double d = Similarity.eucledianDistance(v1, v3);
					if (this.umatrix[j] == null) this.umatrix[j] = new HashMap<String, Double>();
					if (d > min_dis) {
						double sh2 = Math.pow(d, -2/(m-1))/f_temp;
						this.umatrix[j].put(key, sh2);
					}
					else{
						count++;
						this.umatrix[j].put(key, -1.0);
					}
				} // for j
				
				// 憒��雿踹�|Xk-Vi||=0撠梯悟������k銝蛹���掩��撅漲銝粹��
				if(count > 0){
					for(int j= 0; j<k; j++){
						if (this.umatrix[j] == null) this.umatrix[j] = new HashMap<String, Double>();
						if ((this.umatrix[j].get(key) != null) && (this.umatrix[j].get(key) == -1.0))
							this.umatrix[j].put(key, 1/(double)(count));
						else this.umatrix[j].put(key, 0.0);
					}
				}
			} // for i, dataset iterator
			
			//f_temp = objectfun(umatrix, rescenter, pattern, cata, numpattern, dimension, m);
		    //delta = Math.abs(f_temp - lastv);
			//lastv = f_temp;
			f_temp = objFunction(this.umatrix, this.clusters, k, m);
			delta = Math.abs(f_temp-lastv);
			if(debug) {
				System.err.println("cycle="+cycle+",delta="+delta+",f_temp="+f_temp+",lastv="+lastv);
			}
			lastv = f_temp;

			//霈∠��掩銝剖�����
			for(int i=0; i<k; i++) {
				for(int j=0; j<this.dim; j++) {
					f_temp = 0;
					for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
						String key = it.next();
						//f_temp += Math.pow(umatrix[i * numpattern + k], m) * pattern[k * dimension + j];
						double u = (umatrix[i].get(key)==null)?0:umatrix[i].get(key);
						f_temp += Math.pow(u, m)*this.data.get(key)[j];
					}
					this.clusters[i][j] = f_temp;
					f_temp = 0;
					for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
						String key = it.next();
						//f_temp += Math.pow(umatrix[i * numpattern + k], m);
						double u = (umatrix[i].get(key)==null)?0:umatrix[i].get(key);
						f_temp += Math.pow(u, m);
					}
					this.clusters[i][j] /= f_temp;
				}
			}
			
			cycle++;
		} // while

	}
    
    public double objFunction(HashMap<String, Double>[] umatrix, double[][] clusters, int k, double m) {
    	//甇文�霈∠�������
    	double result=0.0;
    			
    	for(int i=0; i<k; i++) {
    		for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
    			String key = it.next();
    			double[] v1 = this.data.get(key);
    			double[] v2 = clusters[i];
    			double d = Similarity.eucledianDistance(v1, v2);
    			double u = (umatrix[i].get(key)==null)?0:umatrix[i].get(key);
    			result += Math.pow(u, m)*d*d;
    		}
    	}
    	return result;
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
            //System.err.println("Total "+count+" records.");
            return result;
        } catch (Exception e) {
            //System.err.println(e);
        	e.printStackTrace(System.err);
        }
        return null;
    }
}
