/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.*;
import clustering.*;
/**
 *
 * @author Administrator

HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
HashMap<String, int[]> map = new HashMap<String, int[]>();
HashMap<String, String>[] responseArray = new HashMap[games.size()];
List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>(); 

cmd:
java hcv "*.txt" k  > out.csv
 */
public class hcv2_1
{
//java hcv2 jh.2.1.5.txt 10
	public static void main(String[] args) {//input: file, clus, target c, print log
		boolean showCen=false;
		String input=args[0];
		int k=Integer.parseInt(args[1]), tar=-1, prt=0, dim=0;
		if(args.length>2)	tar=Integer.parseInt(args[2]);
		if(args.length>3)	prt=Integer.parseInt(args[3]);
		double[][][] center=new double[k+1][][], disL=new double[k+1][][];
		HashMap[] ans = new HashMap[k+1];
		double h1=0.0, h2=0.0, rad09=0.0, rad0r=0.0; // CDV1 = cdv1*Sk+Pk
		StringBuffer out=new StringBuffer(",Sk,cdv1,cdv1/k0,cdv1*Sk,cdvR*Sk,CDV1,CDV2,cdv2_1,cdvR*Sk+Pk,rm,pk2,pk2_1,,,"+
		//",dim,HCV r0,HCV2 r,HCV2 R,r+,R+,HCV bill,bill 2,,r Fix"+
		",ADI,DI,PBM,DBI,Silhouette\n");
		for(int k0=1; k0<=k; k0++)//群數 1-k
		{
			KMeansClustering kmc = new KMeansClustering(input);
//			KMeansClusteringOrig kmc = new KMeansClusteringOrig(input);
			kmc.doClustering(0, k0);//分群
			ArrayList clusL = kmc.lastmatches;//分群表
			HashMap data = kmc.data;//元資料
			center[k0] = kmc.clusters;//重心組
			dim=center[1][0].length;
			double[][] dist = new double[k0][]; //點-心 距
			

			ArrayList<ArrayList<double[]>> rSet2=new ArrayList<ArrayList<double[]>>();
			ArrayList<HashSet<double[]>> rSet=new ArrayList<HashSet<double[]>>();
			//ArrayList(HashSet(double[]))
			boolean again=false;
			StringBuffer tmp= new StringBuffer();
			for(int j=0; j<k0; j++) //結果製表
			{
				HashSet set = (HashSet)clusL.get(j);
				
				if(set.size()<3)
				{
					j=k0;
					k0--;
					again=true;
					break;
				}
				
				HashSet<double[]> set2 = new HashSet<double[]>();
				ArrayList<double[]> al2=new ArrayList<double[]>();
				int s[]=new int[set.size()]; 
				int i=0; 
				for (Iterator it=set.iterator(); it.hasNext(); i++)
				{
					String blog = (String)it.next();
					s[i]=Integer.parseInt(blog);
					double[] freqs = (double[])data.get(blog);
					set2.add(freqs);
					al2.add(freqs);
				}
				Arrays.sort(s);
tmp.append("clus "+j+" :,"+ Arrays.toString(s).replace(",", " ")+"\n");
				if(al2.size()>0)
				{
					rSet.add(set2);
					rSet2.add(al2);
				}
//System.err.println(k0+"-"+j+":"+al2.size());
			}
			if(again)	continue;
if(k0==tar || prt>2) System.out.println(tmp.toString());



			disL[k0]=new double[k0][k0];
			for(int j=0; j<k0; j++)//群間距離
			{
				for(int l=0; l<k0; l++)
					disL[k0][j][l]=j!=l ? Similarity.eucledianDistance(center[k0][j], center[k0][l]) : 0; //實際中心距離
if(prt>0){
	System.out.print("c"+k0+"-"+j+", "+Arrays.toString(center[k0][j]).replace(",","-"));
	for(int l=0; l<k0; l++)	System.out.print(String.format(",%f", disL[k0][j][l]));
	System.out.println("");
}
			}
			System.out.println("--");
			




			
			double cdv1=0, cdvR=0, rad9M=0, Rk=0
			, rad9R[]=new double[k0] //95R List
			, Mk[]=new double[k0+1] //
			, radAve[]=new double[k0];
			int cSize[]=new int[k0];
			// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
			double[] cdv2_r = new double[k0];
			// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
			for(int k1=0; k1<k0; k1++)  //基礎計算
			{
				HashSet set = (HashSet)clusL.get(k1);
				dist[k1]=new double[cSize[k1]=set.size()];
				if(cSize[k1]==0) continue;
				int n=0;
				double rSum=0, cen[]=center[k0][k1];
				for (Iterator it=set.iterator(); it.hasNext(); n++)//群半徑加總
				{
					double[] freqs = (double[])data.get((String)it.next());
					rSum += (dist[k1][n] = Similarity.eucledianDistance(cen, freqs)); //點-中心 距
				}
				Arrays.sort(dist[k1]);
			// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
			        cdv2_r[k1] = dist[k1][n-1];
			// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
				Mk[k1]=1.0+Math.pow(cSize[k1], -2.0/dim)*2; // 半徑損失補償率 1+2/(r^2) //old
				cdvR += rad9R[k1]=dist[k1][dist[k1].length*19/20]; //95% R
				
				cdv1 += (radAve[k1]=(Rk=rSum/cSize[k1]))  * Mk[k1]; //avg R, 半徑矯正率:D+1/D //* (dim+1)/dim
				rad9M+=rad9R[k1]*Mk[k1];
				if(k0==1)
				{	rad09=rad9R[0];
					rad0r=radAve[0];
				}
			}
			
			double bills[]=new double[k0], billT=0, hcv=cdv1, len0=cdv1, log[][]=new double[k0][6], overTab[][]=new double[k0][k0];
			int overSum=0;
			double overA2=0;
			for(int k1=0; k1<k0; k1++) //find nearest
			{
				double minL=9999999, R1=radAve[k1]*Mk[k1], R2
				, edgeA=(dist[k1][0]+dist[k1][1]+dist[k1][2])/2 //R1/Math.pow(setA.size(), 1.0/dim)
				, edgeA2=R1*Math.pow(cSize[k1], -1.0/dim);
				HashSet setA = (HashSet)clusL.get(k1);
				int min=-1, overs[]=new int[k0];
				log[k1][0]=R1;
				log[k1][1]=edgeA;
				for(int k2=0; k2<k0; k2++)
				{
					if(k1==k2) continue;
					if(minL>disL[k0][k1][k2])	minL=disL[k0][k1][min=k2]; //useless
//					
					R2=radAve[k2]*Mk[k2];//*(dim+1)/dim;
					
					double[] vLine=getLine(center[k0][k1], center[k0][k2]) //中心向量
					, cenP=new double[dim];
					
					HashSet setB = (HashSet)clusL.get(k2);
					for(int a=0; a<dim; a++)	cenP[a]=(center[k0][k1][a]+center[k0][k2][a])/2;
					double pCen=dotProduct(vLine, cenP)//中心點
					, pA=dotProduct(vLine, center[k0][k1])
					, pB=dotProduct(vLine, center[k0][k2])
					, edgeB=(dist[k2][0]+dist[k2][1]+dist[k2][2])/2 //R2/Math.pow(setB.size(), 1.0/dim);
					, edgeB2=R2*Math.pow(cSize[k2], -1.0/dim);
					
					
					double lA[]=new double[setA.size()], lB[]=new double[setB.size()];
					
					int a=0;
					ArrayList<String> nearA=new ArrayList<String>(), nearB=new ArrayList<String>();
					for (Iterator it=setA.iterator(); it.hasNext(); a++)
					{
						String name=(String)it.next();
						double freqs[] = (double[])data.get(name);
						lA[a] = dotProduct(vLine, freqs);
						
						if(Math.abs(lA[a]-pCen)<edgeA)	nearA.add(name);
					}
					Arrays.sort(lA);

					a=0;
					for (Iterator it=setB.iterator(); it.hasNext(); a++)
					{
						String name=(String)it.next();
						double freqs[] = (double[])data.get(name);
						lB[a] = dotProduct(vLine, freqs);
						
						if(Math.abs(lB[a]-pCen)<edgeB)	nearB.add(name);
					}
					Arrays.sort(lB);
					
					int overA=0;
					double maxLA=0.0;
					ArrayList<String> nearA2=new ArrayList<String>();
					for(int j=0; j<nearA.size(); j++)
					{	
						for(int i=0; i<nearB.size(); i++) //碰撞統計
						{	if(Similarity.eucledianDistance((double[])data.get(nearB.get(i)), (double[])data.get(nearA.get(j))) < (edgeA+edgeB)/1.5)
							{	overA++;
								nearA2.add(nearA.get(j));
								break;
							}
						}
					}
					for(int j=0; j<nearA2.size(); j++)
						for(int i=0; i<nearA2.size(); i++)
							if(i!=j)	maxLA=Math.max(Similarity.eucledianDistance((double[])data.get(nearA2.get(i)), (double[])data.get(nearA2.get(j))), maxLA);
					overTab[k1][k2]=overA;
					overSum += overs[k1]=overA;
					overA2+=maxLA/(R1)/2*overA;
				//	overA2+=maxLA/(rad0r+R1)/2*overA;

if(prt>1)	System.out.println(","+k0+": "+k1+"-"+k2+",edA "+edgeA+","+edgeA2+",edB "+edgeB+","+edgeB2+",R1 "+R1+",R2 "+R2+",nA "+nearA.size()+",nB "+nearB.size()+",over "+overA+",MLA "+maxLA+","+(maxLA/R1));
				} //layer 2
				
System.out.println("dis"+k0+"-"+k1+","+
",R "+rad9R[k1]+",r "+radAve[k1]+//",jR "+rad9R[min]+
",n "+cSize[k1]+",fR "+(rad9R[k1]*Mk[k1])+",f "+(Mk[k1]));

//				if(k1!=min && rad9R[k1]+rad9R[min]>minL)	hcv+=rad09/k0; //懲罰
			} //layer 1
			
			System.out.print("\nnum");
			for(int k1=0; k1<k0; k1++) System.out.print(","+cSize[k1]);
			System.out.print("\nR");
			for(int k1=0; k1<k0; k1++) System.out.print(","+log[k1][0]);
			System.out.print("\nedge");
			for(int k1=0; k1<k0; k1++) System.out.print(","+log[k1][1]);
			System.out.print("\n\\");
			for(int k1=0; k1<k0; k1++) System.out.print(","+k1);
			System.out.println("");
			for(int k1=0; k1<k0; k1++)
			{
				System.out.print(k1);
				for(int k2=0; k2<k0; k2++)
				{
					System.out.print(","+overTab[k1][k2]);
				}
				System.out.println("");
			}
			System.out.println("\n\n");
			
			// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
			double Pk2 = 0.0 ; int count_i = 0 ; int count_j = 0 ;
			double Pk2_1 = 0.0;
			int count=0;
			double[] cr = new double[k0];
			for (int i=0; i<k0; i++) {
				int nonzero_i = 0;
			    for (int j=0; j<k0; j++) {
					if (i==j) continue;
				    int[] coll = collide(i, j, cdv2_r, cSize, clusL, data, center, k0, dim);
					count_i = coll[0];
					count_j = coll[1];
					//double cri = (double)count_i/cSize[i];
					//double crj = (double)count_j/cSize[j];
					double cri = (double)count_i/(cSize[i]*cSize[j]);
					double crj = (double)count_j/(cSize[i]*cSize[j]);
					
					//Pk2 += (1.0+cri)*(1.0+crj);
					//Pk2 += Math.sqrt((1+cri)*(1+crj));
					Pk2 += (1+cri)*(1+crj);
					//Pk2_1 += cri*crj*(cdv2_r[i]+cdv2_r[j])/2.0;
					count++;
					if (count_i != 0) {
						nonzero_i++;
						cr[i] += count_i;
					}
				}
				if (nonzero_i != 0) {
					//cr[i] /= nonzero_i;
					cr[i] /= cSize[i];
				}
			}
			int nonzero=0;
			double sum=0.0;
			for (double cri: cr) {
				if (cri != 0) {
					sum += cri;
					nonzero++;
				}
			}
			Pk2_1 = sum/nonzero;
			
			double R = 0.0;
			for (double r: cdv2_r) { R+=r;}
			Pk2 /= count;
			Pk2_1 = Math.sqrt(Pk2_1)/R;
			double cdv2_sum=0.0;			
			/*for (int i=0; i<k0; i++) {
			    cdv2_sum += cdv2_r[i];
			}
			Pk2 = Math.sqrt(Pk2/k0*cdv2_sum);*/
			//Pk2 = Math.sqrt(Pk2)/cdv2_sum;
			//Pk2 = (Pk2/(k0*cdv2_sum));
			//Pk2 = k0*cdv2_sum/Pk2;
			//Pk2 = Pk2/(Math.pow(k0, (dim-1.0/dim)*cdv2_sum);
			//Pk2 = Pk2/(cdv2_sum);
			
			// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
						
			if(true) //比較組
			{
				double Sk=Math.pow(k0, (1.0/dim)-1), Sk0=Math.pow(k0, (1.0/dim)), Pk=(rad0r*(1-Math.pow(0.99, k0)));
				out.append(k0+", "+Sk+", "+cdv1+
				","+(cdv1/k0)+
				","+(cdv1*Sk)+
				","+(rad9M*Sk)+
				","+(cdv1*Sk+Pk)+ //CDV
			// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
				","+(cdv1*Sk+Pk2)+ //CDV2
			// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
			//cdv2_1
				","+(cdv1*Sk+Pk2_1)+ 
			//cdv2_1
				","+(rad9M*Sk+Pk)+ //CDV R
				","+cdv1*Sk+
				","+Pk2+
				","+Pk2_1+				
				","+billT+
				","+overSum+
				","+(rad09*overSum/Math.pow(data.size(), (dim-1.0)/dim)));
				
				out.append(","+(new IndexADI().getADI(rSet2))+
				","+(new IndexDI().getDI(rSet2))+
				","+(new IndexPBM().getPBM(rSet2))+
				","+(new IndexDBI().getDBI(rSet2, 2, 2))+
				","+(new IndexSilhouette().getSilhouette(rSet2))+
				"\n");
			}
			
			kmc=null;
		}
		System.out.println("\n"+out.toString());
	}
	
	// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	static int[] collide(int i, int j, double[] cdv2_r, int[] cSize, ArrayList clusL,
			HashMap data, double[][][] center, int k0, int D) {
		int[] result = new int[2];
		double multiply = 1.0;
		double rij = cdv2_r[i]/Math.pow(cSize[i], 1.0/D)+cdv2_r[j]/Math.pow(cSize[j], 1.0/D);
		rij = rij * multiply;
		int coll_i = 0;
		double[] ci=center[k0][i];
		double[] cj=center[k0][j];
		HashSet seti = (HashSet)clusL.get(i);
		HashSet setj = (HashSet)clusL.get(j);
		//for (Iterator it=seti.iterator(); it.hasNext(); ) {
		//    double[] p = (double[])data.get((String)it.next());
        //            if (Similarity.eucledianDistance(p, cj) < rij) coll_i++;
        //            
		//}
		for (Iterator it=seti.iterator(); it.hasNext(); ) {
			double[] p1 = (double[])data.get((String)it.next());
			for (Iterator it2=setj.iterator(); it2.hasNext(); ) {
				double[] p2 = (double[])data.get((String)it2.next());
                if (Similarity.eucledianDistance(p1, p2) < rij) coll_i++;
            }
		}

		int coll_j = 0;
		//HashSet setj = (HashSet)clusL.get(j);
		//for (Iterator it=setj.iterator(); it.hasNext(); ) {
		//    double[] p = (double[])data.get((String)it.next());
		//    if (Similarity.eucledianDistance(p, cj) < rij) coll_j++;
		//}
		result[0] = coll_i;
		//result[1] = coll_j;
		result[1] = coll_i;
		
		return result;
	}
	// CDV2!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
	
	static double[] lineSort(HashMap data, HashSet list, double[] l)
	{
		int a=0, dim=l.length;
		double[] ans=new double[list.size()];
		for (Iterator it=list.iterator(); it.hasNext(); a++)
		{
			String blog = (String)it.next();
			double freqs[] = (double[])data.get(blog);
			for(int b=0; b<dim; b++)
				ans[a]+=l[b]*freqs[b];
		}
		Arrays.sort(ans);
		
		return ans;
	}
	
	static double dotProduct(double[] vector, double[] p)
	{
		double v=0.0;
		int dim=p.length;
		for(int b=0; b<dim; b++)
			v+=vector[b]*p[b];
		return v;
	}
	
	static double[] getLine(double[] a, double[] b)
	{
		if(a.length!=b.length) return null;
		double l[]=new double[a.length], s=0;
		for(int i=0, j=a.length; i<j; i++)
		{
			l[i]=a[i]-b[i];
			s+=l[i]*l[i];
		}
		s=Math.pow(s,0.5);
		for(int i=0, j=a.length; i<j; i++)
			l[i]/=s;
			
		return l;
	}
}


