package clustering.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

public class PickFeature {
	private int[][] data;
	
	public static void main(String[] args) {
		//String fname = "/home/jhyeh/Desktop/Balance2.txt";
		String fname = "Haberman1.txt";
		PickFeature pf = new PickFeature(fname);
		String outfname = fname+".1";
		pf.pickFeatures(outfname, 1, 2);
		outfname = fname+".2";
		pf.pickFeatures(outfname, 1, 3);
		outfname = fname+".3";
		pf.pickFeatures(outfname, 2, 3);
	}
	
	public void pickFeatures(String s, String... cols) {
		int count = cols.length;
		int[] param = new int[count];
		for (int i=0; i<cols.length; i++) {
			String colstr = cols[i];
			param[i] = Integer.parseInt(colstr);
		}
		pickFeatures(s, param);
	}
	
	public void pickFeatures(String s, int... cols) {
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(s));
			for (int[] rec: this.data) {
				String line = "";
				for (int col: cols) {
					line += rec[col]+"\t";
				}
				pw.println(line.trim());
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public PickFeature(String s) {
		this.data = loadData(s);
		if (data == null) System.exit(-1);
	}
	
	public int[][] loadData(String s) {
		// find data dimension first
		int numRecs=0, numFeatures=0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(s));
			String line="";
			while ((line=br.readLine()) != null) {
				numRecs++;
				if (numFeatures == 0) {
					String[] slists = line.split("\t");
					numFeatures = slists.length;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
		
		int[][] data2 = new int[numRecs][numFeatures];
		try {
			BufferedReader br = new BufferedReader(new FileReader(s));
			String line="";
			int count = 0;
			while ((line=br.readLine()) != null) {
				int[] rec = new int[numFeatures];
				String[] slist = line.split("\t");
				for (int i=0; i<rec.length; i++) {
					rec[i] = Integer.parseInt(slist[i]);
				}
				data2[count++] = rec;
			}
			br.close();
			return data2;
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

}
