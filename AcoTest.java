package aco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
public class AcoTest{
	static int features=9;
	static List<Integer>featureArray = new ArrayList<Integer>();
	static double pheromone[][] = new double[features][features];
	static double corrArray [][] = new double[features][features];
	static int destination = 8;
	public static void main(String []args) throws Exception
	{
		
		
		for(int i=0;i<features;i++)
		{
			featureArray.add(i);
		}
		String line;
		int records=0;
		
		double inputMatrix[][] = new double[11][9];
		
		File file = new File("train_data_test.csv");
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
		
//******PHEROMONE*************
		
		int round = 1;
		if(round == 1) {
			pheromone = initializePheromone(features);
		}
		else
		{
			pheromone = updatePheromone(pheromone, corrArray, features);
		}
		
//*********GENERATE ANTS, RELEASE ANTS ******		
		int numberOfAnts = features - 1;
		int antSource[] = new int[numberOfAnts];
		for(int i=0 ; i< numberOfAnts ; i++)
		{
			antSource[i] = i; /*************CHECK : Need to generate ants randomly not serially*/
		}
			
//*********MAKE ANTS TRAVERSE FROM SOURCE TO DESTINATION and GET FEATURE SUBSET********
		List<List<Integer>> subset= new ArrayList<List<Integer>> (numberOfAnts);
		List<Integer> setOfFeature = new ArrayList<Integer>();
		for(int i = 0; i < numberOfAnts; i++)  {
	        subset.add(new ArrayList<Integer>());
	    }
		
		for(int i=0;i<numberOfAnts;i++)
		{
			System.out.println("\n"+"source node= " + antSource[i]);
			System.out.println("destination node = "+ destination);;
			setOfFeature= getFeatureSubSet(antSource[i], destination);
			System.out.println("traversed path: "+"\n");
			for(int j=0;j<setOfFeature.size();j++)
			{
				System.out.print(" "+ setOfFeature.get(j));
			}
			System.out.println("\n"+"/////////////////////////////");
			
			subset.add(setOfFeature);
		}
		round ++;
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
	
	//*****************CALUCULATING NEXT NODE TO TRAVERSE ***********************8
	public static int getNextNode(int sourceNode, List<Integer> remainingNodes)
	{
		double lower=0.0,upper=0.0;
		int nextNode;
		double eta=0.0,eta1=0.0;
		double toque=0.0,toque1=0.0;
		double probabilities[] = new double[remainingNodes.size()];
		double max;
		/* this loop to find probabilities for the nodes not traversed from source node */
		for(int i=0; i<remainingNodes.size();i++)
		{
			eta=corrArray[sourceNode][remainingNodes.get(i)];//probabilities[i] = calculateProb(sourceNode, remainingNodes,pheromone, corrArray);
			toque=pheromone[sourceNode][remainingNodes.get(i)];
			upper=eta*toque;
			for(int j=0; j<remainingNodes.size();j++)
			{
				eta1=corrArray[sourceNode][remainingNodes.get(j)];//probabilities[i] = calculateProb(sourceNode, remainingNodes,pheromone, corrArray);
				toque1=pheromone[sourceNode][remainingNodes.get(j)];
				lower+=eta1*toque1;
			}
			probabilities[i]=upper/lower;
		}
		
		max = 0.0;
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
	
	
	//***********GET SUBSET OF FEATURES FOR ONE ANT **********
	
	public static List<Integer> getFeatureSubSet(int source,int dest)
	{
		List<Integer> traversedFeatures = new ArrayList<Integer>();// to keep track of already traversed features
		List<Integer> remainingFeatures = new ArrayList<Integer>(featureArray.size());// to keep track of possible remaining features to traverse
		List<Integer> subset = new ArrayList<Integer>();
		int nextFeature;
		
		remainingFeatures.addAll(featureArray);
		remainingFeatures.remove(source);
		traversedFeatures.add(source);
		
		//Loop to find next node to traverse till the destination is reached
		do{
			nextFeature = getNextNode(source, remainingFeatures);
			traversedFeatures.add(nextFeature);
			
			if(nextFeature == dest)
			{
				subset = traversedFeatures;
				break;
			}
			else 
			{
				for(Integer x: remainingFeatures)
				{
					if(x == nextFeature)
					{
						remainingFeatures.remove(x);
						break;
					}	
				}
				source = nextFeature;
			}
		}while(nextFeature!= dest);
		return subset;
	}
		
}
