
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.io.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
public class Corr{
	static int features=760;
	static double corrArray [][] = new double[features][features];
	public static void main(String []args) throws Exception
	{
		String line;
		int records=0;
		File corrMatix = new File("corrMatrix.txt");
		double inputMatrix[][] = new double[34918][760];
		// intilialzing the feature array by storing the numbers for features
				
			// Reading the given data 
			File file = new File("train_data.csv");
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(file));
			//*********************************Reading File and Storing in 2D array***************
			   while ((line = reader.readLine()) != null) {
				   String[] words = line.split(",");
				   for( int i=0;i<features;i++) {
					   inputMatrix[records][i] =  Double.parseDouble(words[i]);
				   }
				   records++;
			   }	

			//****CORRELATION******************
			corrArray= generateCorrArray(inputMatrix);
			FileWriter writer1 = new FileWriter(corrMatix, false); 
			BufferedWriter bw1  = new BufferedWriter(writer1);
			PrintWriter pw1 = new PrintWriter(bw1);
		
			for(int i=0;i<corrArray.length;i++)
			{
				for(int j=0;j<corrArray[i].length;j++)
				{
					if (Double.isNaN(corrArray[i][j]))
					{
						corrArray[i][j] = 0;
					}
					pw1.print(corrArray[i][j] +"\t");
				}
				pw1.println("");
			}
			
			pw1.flush();
		pw1.close();
	}
	
	
	//*******************Generating Correlation Matrix**********************************
	public static double[][] generateCorrArray(double inputArray[][]) throws Exception
	{
		PearsonsCorrelation pc = new PearsonsCorrelation();
		RealMatrix input =new Array2DRowRealMatrix(inputArray);
		RealMatrix corr;
		corr  = pc.computeCorrelationMatrix(input);
		double correlationArray[][] = corr.getData();
		return correlationArray ;
	}
	
	
		
}