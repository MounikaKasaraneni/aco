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
		RealMatrix corrMatrix;
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
		
		corrMatrix = generateCorrMatrix(inputMatrix);
		
		//*********to print correlation matrix**********************
		for (int i = 0; i < corrMatrix.getRowDimension(); i++) {
	        System.out.println(corrMatrix.getRowVector(i));
	    }  
	}
	
	//*******************Generating Correlation Matrix**********************************
	public static RealMatrix generateCorrMatrix(double inputArray[][]) throws Exception
	{
		PearsonsCorrelation pc = new PearsonsCorrelation();
		RealMatrix input =new Array2DRowRealMatrix(inputArray);
		RealMatrix corr;
		corr  = pc.computeCorrelationMatrix(input);
		return corr;
	}
}