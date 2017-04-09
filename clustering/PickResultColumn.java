package clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class PickResultColumn {
	private String prefix;
	private int min;
	private int max;
	private int col;
	
	public static void main(String[] args) {
		int m = Integer.parseInt(args[1]);
		int M = Integer.parseInt(args[2]);
		int c = Integer.parseInt(args[3]);
		PickResultColumn prc = new PickResultColumn(args[0], m, M, c);
		prc.go();
	}
	
	public PickResultColumn(String s, int m, int M, int c) {
		this.prefix = s;
		this.min = m;
		this.max = M;
		this.col = c;
	}
	
	public ArrayList<String> fetchColumn(String fname, int c) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String line="";
			// skip header lines
			line=br.readLine();
			line=br.readLine();
			while ((line=br.readLine()) != null) {
				String[] strlist = line.split("\t");
				if (strlist.length > c) {
					result.add(strlist[c]);
					//System.err.println("Adding column data: "+strlist[c]);
				}
				else {
					System.err.println("Error processing line data in file "+fname+", column="+c);
					System.err.println("["+line+"]");
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return result;
	}
	
	public void go() {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		int maxLen = Integer.MIN_VALUE;
		for (int i=min; i<=max; i++) {
			String fname = this.prefix+"."+i+".out";
			ArrayList<String> al = fetchColumn(fname, this.col+i);
			if (al == null) {
				System.err.println("Null column data");
				al = new ArrayList<String>();
			}
			result.add(al);
			maxLen = (maxLen<al.size())?al.size():maxLen;
		}
		
		//System.err.println("MaxLen = "+maxLen);
		// begin output
		int size = result.size();
		//System.err.println(""+size+" file fetched.");
		String outstr = this.prefix+"\t";
		for (int i=0; i<size; i++) {
			int index = 2+i;
			outstr += index+"\t";
		}
		System.out.println(outstr.trim());
		
		for (int i=0; i<maxLen; i++) {
			outstr = ""+i+"\t";
			for (int j=0; j<size; j++) {
				ArrayList<String> al = result.get(j);
				outstr += al.get(i)+"\t";
			}
			System.out.println(outstr.trim());
		}
	}

}
