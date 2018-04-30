
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.io.*;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
public class Aco{
	 static int features=9;
	static List<Integer>featureArray = new ArrayList<Integer>();
	static int destination = 8;
	public static void main(String []args) throws Exception
	{
		String line;
		int numFeatures=0;
		String line1="";
		String line2= "";
		int records=0;
		int records1 = 0;
		int round = 1;
		
		File antRouteFeatures = new File("antRouteFeatures.txt");
		File ant = new File("ant.txt");
		
			
		// Reading the given data 
		File file = new File(args[0]);
		File file1 = new File(args[1]);
		File file2 = new File(args[2]);
			
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(file));
		//*********************************Reading Feature numbers and Storing in feature array***************
		while ((line = reader.readLine()) != null) {
			String[] words = line.split(",");
		    for(int i=0;i<words.length;i++)
			{
				featureArray.add(Integer.parseInt(words[i]));
				
			}
		}
		
		
			numFeatures = featureArray.size();
			double pheromone[][] = new double[numFeatures][numFeatures];
		    double corrArray [][] = new double[numFeatures][numFeatures];
			
			@SuppressWarnings("resource")
			BufferedReader reader1 = new BufferedReader(new FileReader(file1));
			//*********************************Reading File and Storing in 2D array***************
			while ((line1 = reader1.readLine()) != null) {
				   String[] words1 = line1.split("\t");
				   for( int i=0;i<numFeatures;i++) {
					   corrArray[records][i] =  Double.parseDouble(words1[i]);
				   }
				   records++;
			}	
	
			@SuppressWarnings("resource")
			BufferedReader reader2 = new BufferedReader(new FileReader(file2));
			//*********************************Reading File and Storing in 2D array***************
			while ((line2 = reader2.readLine()) != null) {
				   String[] words2 = line2.split("\t");
				   for( int i=0;i<numFeatures;i++) {
					   pheromone[records1][i] =  Double.parseDouble(words2[i]);
				   }
				   records1++;
			}	
			
		    pheromone = updatePheromone(pheromone, corrArray, numFeatures);
			
//*********GENERATE ANTS, RELEASE ANTS ******		
		int numberOfAnts = numFeatures - 1;
		int antSource[] = new int[numberOfAnts];
		for(int i=0 ; i< numberOfAnts ; i++)
		{
			antSource[i] = featureArray.get(i); 
		}
			
//*********MAKE ANTS TRAVERSE FROM SOURCE TO DESTINATION and GET FEATURE SUBSET********
		List<List<Integer>> subset= new ArrayList<List<Integer>> (numberOfAnts);
		List<Integer> setOfFeature = new ArrayList<Integer>();
		for(int i = 0; i < numberOfAnts; i++)  {
	        subset.add(new ArrayList<Integer>());
	    }
		
		HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		
		//antRouteFeatures.createNewFile();
		FileWriter writer = new FileWriter(antRouteFeatures, false); 
		BufferedWriter bw  = new BufferedWriter(writer);
		PrintWriter pw = new PrintWriter(bw);
		
		FileWriter writer1 = new FileWriter(ant, false); 
		BufferedWriter bw1  = new BufferedWriter(writer1);
		PrintWriter pw1 = new PrintWriter(bw1);
		
		
		
		for(int i=0;i<numberOfAnts;i++)
		{
			
			setOfFeature= getFeatureSubSet(antSource[i], destination, pheromone, corrArray);
			int x = antSource[i] + 1;
			pw.print(x+":"); 
			for(int j=0;j<setOfFeature.size();j++)
			{
				 
				int y = setOfFeature.get(j) + 1;
				pw.print(y);
				pw1.print(y);
				if(j<setOfFeature.size()-1)
				{
					pw.print(",");
					pw1.print(",");
				}
				else
				{
					int z = j+1;
					pw.print(":"+z);
				}
			}
			pw.println(" ");
			pw1.println(" ");	
			subset.add(setOfFeature);
		}
		
		
		
		pw.flush();
		pw.close();
		pw1.flush();
		pw1.close();
		
		
	}
	
	
	

	
	//*************************UPDATE PHEROMONE ******************************
	
	public static double[][] updatePheromone(double[][] pheromone, double[][] correlationArray, int featureCount)
	{
		double s = 0.01; 
		for(int i=0 ; i< featureArray.size(); i++)
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
	public static int getNextNode(int sourceNode, List<Integer> remainingNodes, double [][] pheromone, double [][] corrArray)
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
	
	public static List<Integer> getFeatureSubSet(int source,int dest, double [][] pheromone, double [][]corrArray)
	{
		List<Integer> traversedFeatures = new ArrayList<Integer>();// to keep track of already traversed features
		List<Integer> remainingFeatures = new ArrayList<Integer>(featureArray.size());// to keep track of possible remaining features to traverse
		List<Integer> subset = new ArrayList<Integer>();
		int nextFeature;
		
		remainingFeatures.addAll(featureArray);
		remainingFeatures.remove(source);
		traversedFeatures.add(source);
		
		do{
			nextFeature = getNextNode(source, remainingFeatures, pheromone, corrArray);
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