package aco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
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
		
//****CORRELATION******************
		corrArray= generateCorrArray(inputMatrix);
		//*********REMEMBER : corrArray[i][j] = the correlation element between feature i and j*****
		/*********** just a check **************88
		for(int i=0;i< corrArray.length;i++)
		{
			for(int j=0;j<corrArray[i].length;j++)
			{
				System.out.print(Math.round (corrArray[i][j] * 100.0) / 100.0);
				System.out.print(" ");
			}
			System.out.println(" ");
		}
		*/
		
//******PHEROMONE*************
		double pheromone[][] = new double[features][features];
		int round = 1;
		if(round == 1) {
			pheromone = initializePheromone(features);
			
		}
		else
		{
			pheromone = updatePheromone(pheromone, corrArray, features);
			
			/*********************** Just a CHECK ************************8
			/*for(int i=0;i< pheromone.length;i++)
			{
				for(int j=0;j<pheromone[i].length;j++)
				{
					System.out.print(Math.round (pheromone[i][j] * 100.0) / 100.0);
					System.out.print(" ");
				}
				System.out.println(" ");
			}*/
		}
		
//*********GENERATE ANTS, RELEASE ANTS ******		
		int numberOfAnts = features - 1;
		double antSource[] = new int[numberOfAnts];
		for(int i=0 ; i< numberOfAnts ; i++)
		{
			antSource[i] = i; /*************CHECK : Need to generate ants randomly not serially*/
		}
//**********DESTINATION************
		int destination = 759;
		
		
//***************CALCULATE PROBABILITY********
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
	
	//*************************INITIALIZE PHEROMONE ******************************
	
	public static double[][] initializePheromone(int featureCount)
	{
		double pheromone[][] = new double[featureCount][featureCount];
		for(int i=0;i<featureCount;i++)
		{
			for(int j=0;j<featureCount;j++)
			{
				pheromone[i][j] = 1;
			}
		}
		return pheromone;
	}
	
	
	//*************************UPDATE PHEROMONE ******************************
	
	public static double[][] updatePheromone(double[][] previousPheromone, double[][] correlationArray, int featureCount)
	{
		double s = 0.01; 
		double currentPheromone[][] = new double[featureCount][featureCount];
		for(int i=0 ; i< featureCount ; i++)
		{
			for(int j=0;j<featureCount ; j++)
			{
				currentPheromone[i][j] = ((1-s)*previousPheromone[i][j]) + correlationArray[i][j];
			}
		}
		return currentPheromone;
	}
	
	//*********************CALCULATE Pij *******************8
		public static double calculateProb(int i, int j)
		{
			double j=0;
			return j;
		}
	
	//*****************CALUCULATING NEXT NODE TO TRAVERSE ***********************8
	public static int getNextNode(int sourceNode, List<Integer> remainingNodes)
	{
		int nextNode;
		double probabilities[] = new double[remainingNodes.size()];
		double max;
		/* this loop to find probabilities for the nodes not traversed from source node */
		for(int i=0; i<remainingNodes.size();i++)
		{
			probabilities[i] = calculateProb(sourceNode, remainingNodes.get(i));
		}
		
		max = probabilities[0];
		nextNode = remainingNodes.get(0);
		
		/*this loop to check the node which has maximum probability */
		for(int i=0; i< probabilities.length; i++)
		{
			if(max < probabilities[i])
			{
				max = probabilities[i];
				nextNode = remainingNodes.get(i);
			}
		}
		return nextNode;
	}
	
	
	/*  loop for only one ant finding feature subset */
	
	List<Integer> traversedFeatures = new ArrayList<Integer>();
	List<Integer> remainingFeatures = new ArrayList<Integer>();
	int dest = 759;
	int nextFeature;
	int source = 1;//only for now................
	do{
		nextFeature = getNextNode(source, remainingFeatures);
		traversedFeatures.add(nextFeature);
		if(nextFeature == dest)
		{
			/*featureSetOfAnt[source] = traversedfeatures;*/
		}
		else 
		{
			remainingFeatures.remove(nextFeature);
			source = nextFeature;
		}
	}while(nextNode!= dest);
	
}