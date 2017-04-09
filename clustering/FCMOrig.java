/**
 * 
 */
package clustering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FCMOrig {

	private static final String SETTING_PATH = "workspace/CDV/data/";
	private static final String FILE_DATA_IN = "data_in.txt";//颲�����
	private static final String FILE_PAR = "parameters.txt";//����蔭
	private static final String FILE_CENTER = "center.txt";//��掩銝剖��
	private static final String FILE_MATRIX = "matrix.txt";//�撅漲��
	private static final int SIZE = 10;
	
	public int numpattern;//���
	public int dimension;//瘥葵�����輕�
	public int cata;//閬�掩��掩��
	public int maxcycle;//��憭抒�翮隞�甈⊥
	public double m;//��m
	public double limit;//霂臬榆���
	
	public FCMOrig() {
		super();
	}

	public FCMOrig(int numpattern, int dimension, int cata, int maxcycle, double m, double limit) {
		this.numpattern = numpattern;
		this.dimension = dimension;
		this.cata = cata;
		this.maxcycle = maxcycle;
		this.m = m;
		this.limit = limit;
	}
	
	/**
	 * 霂餃��蔭��辣
	 * @return
	 */
	public boolean getPar() {
        
		//�����蔭��辣
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(SETTING_PATH+FILE_PAR));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
			
        //霂餃��蔭��辣
		String line = null;
		for (int i = 0; i < 6; i++) {
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO ������ catch ���
				e.printStackTrace();
			}
			switch(i) {
			case 0: numpattern = Integer.valueOf(line); break;
			case 1: dimension = Integer.valueOf(line); break;
			case 2: cata = Integer.valueOf(line); break;
			case 3: m = Double.valueOf(line); break;
			case 4: maxcycle = Integer.valueOf(line); break;
			case 5: limit = Double.valueOf(line); break;
			}
		}
		
		return true;
	}
	
	/**
	 * 霂餃��
	 * @param pattern
	 * @return
	 */
	public boolean getPattern(double[] pattern) {
		
		//�������辣
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(SETTING_PATH+FILE_DATA_IN));
		} catch (FileNotFoundException e) {
			// TODO ������ catch ���
			e.printStackTrace();
		}
		
		//霂餃����辣
		String line = null;
		String regex = ",";
		int index = 0;
		while (true) {
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO ������ catch ���
				e.printStackTrace();
			}
			
			if (line == null)
				break;
			
			String[] split = line.split(regex);
			for (int i = 0; i < split.length; i++) 
				pattern[index++] = Double.valueOf(split[i]);
		}
			
		return true;
	}
	
	/**
	 * ���摰�CM��掩蝞��
     * ����蛹銝�����掩����縑������靽⊥����靽⊥��靽⊥��
	 * @param pattern 銝箸������  ���蛹嚗�����*蝏湔=蝏湔����
	 * @param dimension 瘥葵����輕�
	 * @param numpattern ����葵�
	 * @param cata ��掩���
	 * @param m fcm�������m
	 * @param maxcycle ��憭批儐�甈⊥
	 * @param limit 蝞���翮隞������
	 * @param umatrix 颲�����   ���蛹嚗�掩����*���=������
	 * @param rescenter 颲������掩銝剖����  ���蛹嚗�掩����*蝏湔=蝏湔����
	 * @param result ��������
	 */
	public boolean FCM_fun(double[] pattern, int dimension, int numpattern, int cata, double m, 
			             int maxcycle, double limit, double[] umatrix, double[] rescenter, double result) {
		
		int j, i, k, t, count;
		int n_cycle;
		int n_selnum;
		int flagtemp;
		double f_temp, lastv, delta, t_temp;
		double[] v1 = new double[SIZE];
		double[] v2 = new double[SIZE];
		double min_dis = 0.001;//頝氖���撠��
		
		//撉����������
		if (cata >= numpattern || m <= 1)
			return false;
		
		//������翮隞��雿蛹�����掩銝剖��
		for (j = 0; j < cata; j++) {
			t_temp = Math.random();
			n_selnum = (int) (Math.random() * numpattern / cata + j * numpattern / cata);
			n_selnum = j * numpattern / cata;
			
			while (n_selnum * dimension > numpattern * dimension)
				n_selnum -= numpattern;
			
			for (i = 0; i < dimension; i++)
				rescenter[j * dimension + i] = pattern[n_selnum * dimension + i];
		}
		
		//撘�憪蜓敺芰
		n_cycle = 0;
		lastv = 0;
		
		do{
			//霈∠����
			for(i = 0; i < numpattern; i++) {
				flagtemp = 0;
				count = 0;
				for(j = 0;j < cata; j++) {
					f_temp = 0;
					for(t = 0;t < cata; t++) {
						for(k = 0; k < dimension; k++) {
							v1[k] = pattern[i * dimension + k];
							v2[k] = rescenter[t * dimension + k];
						}
						
						if (distance(v1, v2, dimension) > min_dis){
							f_temp += Math.pow(distance(v1, v2, dimension), -2 / (m - 1));
							}else{
								flagtemp = 1;
							}
						}
					
						for(k = 0; k < dimension; k++){
							v1[k] = pattern[i * dimension + k];
							v2[k] = rescenter[j * dimension + k];
						}
						
						if(flagtemp == 1){
		                   umatrix[j * numpattern + i] = 0;
						   flagtemp = 0;
						}
						
						//憒��雿踹�|Xk-Vi||=0��蝵格���-1
						if(distance(v1, v2, dimension) > min_dis) {
							double shit1 = distance(v1, v2, dimension);
							double shit2 = Math.pow(shit1, -2 / (m - 1)) / f_temp;
							int shit3 = j * numpattern + i;
							umatrix[shit3] = shit2;
						}else{
							count++;
							umatrix[j * numpattern + i] = -1;
						}
				  }//end for j
				 
				// 憒��雿踹�|Xk-Vi||=0撠梯悟������k銝蛹���掩��撅漲銝粹��
				if(count > 0){
					for(j = 0; j < cata; j++){
						if(umatrix[j * numpattern + i] == -1){
							umatrix[j * numpattern + i] = 1 / (double)(count);
						}else
							umatrix[j * numpattern +i]=0;
					}
				}
			  }//end for i
			
			  f_temp = objectfun(umatrix, rescenter, pattern, cata, numpattern, dimension, m);
		      delta = Math.abs(f_temp - lastv);
			  lastv = f_temp;
			  //System.err.println("delta="+delta);

			  //霈∠��掩銝剖�����
			  for(i = 0; i < cata; i++){
		          for(j = 0; j < dimension; j++){
					  f_temp = 0;
					  for(k = 0; k < numpattern; k++){
						  f_temp += Math.pow(umatrix[i * numpattern + k], m) * pattern[k * dimension + j];
					  }
					  rescenter[i * dimension + j] = f_temp;
					  f_temp = 0;
		              for(k = 0; k < numpattern; k++){
						  f_temp += Math.pow(umatrix[i * numpattern + k], m);
					  }
		              rescenter[i * dimension + j] /= f_temp;
				  }
			  }
		      n_cycle++;

		   } while(n_cycle < maxcycle && delta > limit);

		return true;
		
	}
	
	/**
	 * 霈∠�洹瘞�氖
	 * @param v1
	 * @param v2
	 * @param dimension
	 * @return
	 */
	public double distance(double v1[],double v2[],double dimension) {
        //餈葵��霈∠�洹瘞�氖
		int i;
		double result;
		
		result = 0;
		for(i = 0; i < dimension; i++){
			result += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		}
		
		result = Math.sqrt(result);
		
		return result;
	}
	
	/**
	 * 霈∠�������
	 * @param u
	 * @param v
	 * @param x
	 * @param c
	 * @param pattern
	 * @param dimension
	 * @param m
	 * @return
	 */
	public double objectfun(double u[],double v[],double x[],int c,int pattern,int dimension,double m) {
        //甇文�霈∠�������
		int i,j,k;
		double[] v1 = new double[SIZE];
		double[] v2 = new double[SIZE];
		double object;
		
		object = 0;
		for(i = 0; i < c; i++) {
			for(j = 0; j < pattern; j++) {
				
				for(k = 0; k < dimension; k++) {
					v1[k] = x[j * dimension + k];
					v2[k] = v[i * dimension + k];
				}
				
				object += Math.pow(u[i * pattern+j], m) * distance(v1, v2, dimension) * distance(v1, v2, dimension);
			}
		}
		
		return object;
	}
	
	/**
	 * 餈�CM蝞��
	 *
	 */
	public void runFCM() {
		
		double[] pattern = new double[numpattern * dimension];
		double[] umatrix = new double[numpattern * cata];
		double[] rescenter = new double[cata * dimension];
		double result=0;
		
		//����
		getPattern(pattern);
		
		//�銵CM_fun
		FCM_fun(pattern, dimension, numpattern, cata, m, maxcycle, limit, umatrix, rescenter, result);
			
		//颲蝏��
		Export(umatrix, rescenter);
	}
	
	/**
	 * 颲�撅漲�����掩��葉敹�
	 * @param umatrix
	 * @param rescenter
	 */
	public void Export(double[] umatrix, double[] rescenter) {
		String str = null;
		String tab = "	";
		
		//颲�撅漲��
		try {
			FileWriter matrixFileWriter = new FileWriter(SETTING_PATH+FILE_MATRIX);
			
			for (int i = 0; i < numpattern; i++) {
				str = "";
				for (int j = 0; j < cata; j++) {
					str += umatrix[j * numpattern + i] + tab;
				}
				str += "\n";
				matrixFileWriter.write(str);
			}
			
			matrixFileWriter.close();
		} catch (IOException e) {
			// TODO ������ catch ���
			e.printStackTrace();
		}
				
		//颲��掩��葉敹�
		try {
			FileWriter centerFileWriter = new FileWriter(SETTING_PATH+FILE_CENTER);
			
			for (int i = 0; i < cata; i++) {
				str = "";
				for (int j = 0; j < dimension; j++) {
					str += rescenter[i*dimension + j] + tab;
				}
				str += "\n";
				centerFileWriter.write(str);
			}
			
			centerFileWriter.close();
		} catch (IOException e) {
			// TODO ������ catch ���
			e.printStackTrace();
		}
		
		HashMap<Integer, Integer> countmap = new HashMap<Integer, Integer>();
		for (int i = 0; i < numpattern; i++) {
			int maxid=-1;
			double maxval=Double.MIN_VALUE;
			for (int j = 0; j < cata; j++) {
				double val = umatrix[j*numpattern+i];
				if (maxval < val) { maxval = val; maxid = j; }
			}
			if (countmap.get(maxid) == null) countmap.put(maxid, 1);
			else countmap.put(maxid, countmap.get(maxid)+1);
		}
		String ss = "(";
		for (int key: countmap.keySet()) ss += countmap.get(key)+", ";
		ss = ss.substring(0, ss.length()-2)+")";
		System.out.println(ss);
	}

	/**
	 * 銝餃�
	 * @param args
	 */
	public static void main(String[] args) {
		FCMOrig fcm = new FCMOrig();
		fcm.getPar();			
		fcm.runFCM();
	}
	
	public void Export(double[] umatrix, double[] rescenter, String path) {
		String str = null;
		String tab = "	";
		
		//颲�撅漲��
		try {
			FileWriter matrixFileWriter = new FileWriter(path+"membership.txt");
			
			for (int i = 0; i < numpattern; i++) {
				str = "";
				for (int j = 0; j < cata; j++) {
					str += umatrix[j * numpattern + i] + tab;
				}
				str += "\n";
				matrixFileWriter.write(str);
			}
			
			matrixFileWriter.close();
		} catch (IOException e) {
			// TODO ������ catch ���
			e.printStackTrace();
		}
				
		//颲��掩��葉敹�
		try {
			FileWriter centerFileWriter = new FileWriter(path+"centroids.txt");
			
			for (int i = 0; i < cata; i++) {
				str = "";
				for (int j = 0; j < dimension; j++) {
					str += rescenter[i*dimension + j] + tab;
				}
				str += "\n";
				centerFileWriter.write(str);
			}
			
			centerFileWriter.close();
		} catch (IOException e) {
			// TODO ������ catch ���
			e.printStackTrace();
		}
	}

}
