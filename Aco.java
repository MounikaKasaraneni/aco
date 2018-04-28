
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
public class Aco{
	 static int features=9;
	static List<Integer>featureArray = new ArrayList<Integer>();
	static double pheromone[][] = new double[features][features];
	static double corrArray [][] = new double[features][features];
	static int destination = 8;
	public static void main(String []args) throws Exception
	{
		String line;
		String line1;
		int records=0;
		int round = 1;
		double inputMatrix[][] = new double[11][9];
		if(round == 1)
		{
			// intilialzing the feature array by storing the numbers for features
			for(int i=0;i<9;i++)
			{
				featureArray.add(i);
			}
		
			// Reading the given data 
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
		}
		else
		{
			File file1 = new File("train_data_test.csv");
			@SuppressWarnings("resource")
			BufferedReader reader1 = new BufferedReader(new FileReader(file1));
			//*********************************Reading Feature numbers and Storing in feature array***************
		   while ((line1 = reader1.readLine()) != null) {
			   String[] words1 = line1.split(",");
			   for(int i=0;i<words1.length;i++)
				{
					featureArray.add(i);
				}
			  
		   }
		
		}
		int numFeatures = featureArray.size();
		/*
		
		
		// for full dataset replace 11 with number of rows in data file
		double inputMatrix[][] = new double[11][featureArray.size()];
		
		// get the specific columns from data set
		
		for(int r=0;r<11;r++)
		{
			for(int c=0;c<featureArray.size();c++)
			{
				inputMatrix[r][c] = dataMatrix[r][featureArray.get(c)];
			}		
		}
		*/
		
//********************************************************************************************************************		
		
		

//******PHEROMONE*************
		
		
		if(round == 1) {
			
			//****CORRELATION******************
			corrArray= generateCorrArray(inputMatrix);
			pheromone = initializePheromone(760);
		}
		else
		{
			pheromone = updatePheromone(pheromone, corrArray, 760);
		}
		
//*********GENERATE ANTS, RELEASE ANTS ******		
		int numberOfAnts = numFeatures - 1;
		int antSource[] = new int[numberOfAnts];
		for(int i=0 ; i< numberOfAnts ; i++)
		{
			antSource[i] = featureArray.get(i); /*************CHECK : Need to generate ants randomly not serially*/
		}
			
//*********MAKE ANTS TRAVERSE FROM SOURCE TO DESTINATION and GET FEATURE SUBSET********
		List<List<Integer>> subset= new ArrayList<List<Integer>> (numberOfAnts);
		List<Integer> setOfFeature = new ArrayList<Integer>();
		for(int i = 0; i < numberOfAnts; i++)  {
	        subset.add(new ArrayList<Integer>());
	    }
		
		HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		
		
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
	
	public static double[][] updatePheromone(double[][] pheromone, double[][] correlationArray, int featureCount)
	{
		double s = 0.01; 
		for(int i=0 ; i< featureArray.size() ; i++)
		{
			int x = featureArray.get(i);
			for(int j=0;j<featureArray.size() ; j++)
			{
				int y= featureArray.get(j);
				pheromone[x][y] = ((1-s)*pheromone[x][y]) + correlationArray[x][y];
			}
		}
		return pheromone;
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
