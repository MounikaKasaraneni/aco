package aco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
public class AcoTest{
	
	
	public static void main(String []args) throws Exception
	{
		String line;
		int records=0;
		int features=9;
		double inputMatrix[][] = new double[11][9];
		double corrArray [][] = new double[features][features];
		File file = new File("C:\\Users\\HP\\eclipse-workspace\\aco\\src\\aco\\train_data.csv");
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
		
		corrArray= generateCorrArray(inputMatrix);
		//*********REMEMBER : corrArray[i][j] = the correlation element between feature i and j*****
		for(int i=0;i< corrArray.length;i++)
		{
			for(int j=0;j<corrArray[i].length;j++)
			{
				System.out.print(Math.round (corrArray[i][j] * 100.0) / 100.0);
				System.out.print(" ");
			}
			System.out.println(" ");
		}
	}
	
	//*******************Generating Correlation Matrix**********************************
	public static double[][] generateCorrArray(double inputArray[][]) throws Exception
	{
		PearsonsCorrelation pc = new PearsonsCorrelation();
		RealMatrix input =new Array2DRowRealMatrix(inputArray);
		RealMatrix corr;
		corr  = pc.computeCorrelationMatrix(input);
		double correlationArray[][] = corr.getData();
		
		//********check 1: Printing the correlation matrix which is stored in an array ********
		/*for(int i=0;i< correlationArray.length;i++)
		{
			for(int j=0;j<correlationArray[i].length;j++)
			{
				System.out.print(Math.round (correlationArray[i][j] * 100.0) / 100.0);
				System.out.print(" ");
			}
			System.out.println(" ");
		}
		*/
		return correlationArray ;
	}
}
