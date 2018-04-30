
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.io.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
public class pheromone{
	 static int features=9;
	
	static double pheromone[][] = new double[features][features];
	
	public static void main(String []args) throws Exception
	{
		int featureCount  =features;
		double pheromone[][] = new double[featureCount][featureCount];
		File Pheromone = new File("Pheromone.txt");
		
		FileWriter writer1 = new FileWriter(Pheromone, false); 
		BufferedWriter bw1  = new BufferedWriter(writer1);
		PrintWriter pw1 = new PrintWriter(bw1);
		
		
		for(int i=0;i<featureCount;i++)
		{
			for(int j=0;j<featureCount;j++)
			{
				pheromone[i][j] = 1;
				pw1.print(pheromone[i][j] +"\t");
			}
				pw1.println("");
		}
						
			pw1.flush();
		pw1.close();
				
		
	}
	
}