package clustering;

import java.util.*;

public class KMeansExpr {
	private String fname;
	private int method;
	private int k;
	private int rounds;
	private HashMap<String, double[]> data;
	private ArrayList<HashSet<String>> result;
	private ArrayList<ArrayList<double[]>> indexArray;

	public static void main(String[] args) {
    	//String fname = "/home/jhyeh/workspace/hcv/data/Circular_5_2.txt";
    	//KMeansExpr expr = new KMeansExpr(fname, 0, 5, 50);
		
		int k = Integer.parseInt(args[1]);
		KMeansExpr expr = new KMeansExpr(args[0], 0, k, 50);
    	expr.go();
    }
	
	public KMeansExpr(String s, int m, int k, int r) {
		this.fname = s;
		this.method = m;
		this.k = k;
		this.rounds = r;
	}
	
	public void go() {
		KMeansClusteringOrig2 kmc = new KMeansClusteringOrig2(fname);
		
		System.err.println("DBI/DI/ADI/PBM/HCV");
		int count=0;
		while (count<this.rounds) {
			kmc.doClustering(this.method, this.k);
			this.data = kmc.getData();
			this.result = kmc.getClusteringResult();
			this.indexArray = makeIndexArray();
			
			String str="";
			for (Iterator<ArrayList<double[]>> it=this.indexArray.iterator(); it.hasNext(); ) {
				ArrayList<double[]> al = it.next();
				str += al.size()+"\t";
			}
    	
			try {
				// calculate DBI
				double dbi = IndexDBI.getDBI(this.indexArray);
				// calculate DI
				double di = IndexDI.getDI(this.indexArray);
				// calculate ADI
				double adi = IndexADI.getADI(this.indexArray);
				// calculate PBM
				double pbm = IndexPBM.getPBM(this.indexArray);
				// calculate HCV
				double hcv = IndexHCV.getHCV(this.indexArray);
				// calculate HCV2
				double hcv2 = IndexHCV.getHCV2(this.indexArray);
				//System.err.println(""+dbi+"\t"+di+"\t"+adi+"\t"+pbm);
				str += ""+dbi+"\t"+di+"\t"+adi+"\t"+pbm+"\t"+hcv+"\t"+hcv2;
				System.err.println(str);
				count++;
			} catch (Exception e) {}
		
		}
	}
	
	public ArrayList<ArrayList<double[]>> makeIndexArray() {
		ArrayList<ArrayList<double[]>> r = new ArrayList<ArrayList<double[]>>();
		for (Iterator<HashSet<String>> it=result.iterator(); it.hasNext(); ) {
			HashSet<String> set = it.next();
			ArrayList<double[]> al = new ArrayList<double[]>();
			for (Iterator<String> it2=set.iterator(); it2.hasNext(); ) {
				String key = it2.next();
				double[] rec = this.data.get(key);
				al.add(rec);
			}
			r.add(al);
		}
		return r;
	}
	
}
