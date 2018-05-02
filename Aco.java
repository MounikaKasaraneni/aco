import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.io.*;

public class Aco{
	 static int features=760;
	static List<Integer>featureArray = new ArrayList<Integer>();
	static List<Integer>destinationArray = new ArrayList<Integer>();
	static int destination = 759;
	public static void main(String []args) throws Exception
	{
		String line;
		int numFeatures=0;
		String line1="";
		String line2= "";
		int records=0;
		int records1 = 0;
		int round = 1;
		
		// antRouteFeatures.txt file provides the ant number: features in subset : number of features in each subset
		File antRouteFeatures = new File("antRouteFeatures.txt");
		//ant.txt file provides the list of features in a subset
		File ant = new File("ant.txt");
		//updatePheromone.txt provides the updated pheromone value
		File upPheromone = new File("updatePheromone.txt");
		
		 
		// Reading the given data 
		//InitializeAnts.txt is provided as args[0],  which has the list of features
		File file = new File(args[0]);
		//corrMatrix.txt is provided as args[1], which has the correlation matrix
		File file1 = new File(args[1]);
		//updatePheromone.txt is provided as args[2], which has the updated pheromone values
		File file2 = new File(args[2]);
			
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(file));
		//*********************************Reading initialized ants ***************
		while ((line = reader.readLine()) != null) {
			String[] words = line.split(",");
		    for(int i=0;i<words.length;i++)
			{
				featureArray.add(Integer.parseInt(words[i]));
			}
		}
		
		
		numFeatures = featureArray.size();
		double pheromone[][] = new double[features][features];
	    double corrArray [][] = new double[features][features];
			
			@SuppressWarnings("resource")
			BufferedReader reader1 = new BufferedReader(new FileReader(file1));
			//*********************************Reading correlation matrix **************
			while ((line1 = reader1.readLine()) != null) {
				   String[] words1 = line1.split("\t");
				   for( int i=0;i<words1.length;i++) {
					   corrArray[records][i] =  Double.parseDouble(words1[i]);
				   }
				   records++;
			}	
	
			@SuppressWarnings("resource")
			BufferedReader reader2 = new BufferedReader(new FileReader(file2));
			//*********************************Reading updated Pheromone**************
			while ((line2 = reader2.readLine()) != null) {
				   String[] words2 = line2.split("\t");
				   for( int i=0;i<words2.length;i++) {
					   pheromone[records1][i] =  Double.parseDouble(words2[i]);
				   }
				   records1++;
			}	
			
		    pheromone = updatePheromone(pheromone, corrArray, numFeatures);
			FileWriter writer2 = new FileWriter(upPheromone, false); 
			BufferedWriter bw2  = new BufferedWriter(writer2);
			PrintWriter pw2 = new PrintWriter(bw2);
			// printing the updated pheromone to output file	
			for(int i=0;i<pheromone.length;i++)
			{
				for(int j=0;j<pheromone[i].length;j++)
				{
					pw2.print(pheromone[i][j]);
						pw2.print("\t");
				}
				pw2.println("");
			}	
		
			
			
//*********GENERATE ANTS, RELEASE ANTS ******		
		int numberOfAnts = numFeatures-1;
		int antSource[] = new int[numberOfAnts];
		for(int i=0 ; i< numberOfAnts ; i++)
		{
			antSource[i] = featureArray.get(i); 
		}
			

		List<List<Integer>> subset= new ArrayList<List<Integer>> (numberOfAnts);
		List<Integer> setOfFeature = new ArrayList<Integer>();
		for(int i = 0; i < numberOfAnts; i++)  {
	        subset.add(new ArrayList<Integer>());
	    }
		
		HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		
		// writing the ants , routes and number of features to output files
		FileWriter writer = new FileWriter(antRouteFeatures, false); 
		BufferedWriter bw  = new BufferedWriter(writer);
		PrintWriter pw = new PrintWriter(bw);
		
		FileWriter writer1 = new FileWriter(ant, false); 
		BufferedWriter bw1  = new BufferedWriter(writer1);
		PrintWriter pw1 = new PrintWriter(bw1);
		
		
		//*********MAKE ANTS TRAVERSE FROM SOURCE TO DESTINATION and GET FEATURE SUBSET********
		for(int i=0;i<numberOfAnts;i++)
		{
			
			setOfFeature= getFeatureSubSet(antSource[i], destination, pheromone, corrArray);
			int x = antSource[i] + 1;
			pw.print(x+":"); 
			for(int j=0;j<setOfFeature.size();j++)
			{
				 
				int y = setOfFeature.get(j) + 2;
				int k = setOfFeature.get(j);
				if(j==0)
				{
					pw1.print(1+",");
				}
				pw.print(k);
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
		pw2.flush();
		pw2.close();
		
		
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
			eta=corrArray[sourceNode][remainingNodes.get(i)];
			toque=pheromone[sourceNode][remainingNodes.get(i)];
			upper=eta*toque;
			for(int j=0; j<remainingNodes.size();j++)
			{
				eta1=corrArray[sourceNode][remainingNodes.get(j)];
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
	
	
	//***********GET SUBSET OF FEATURES FOR ONE ANT ****************************
	
	public static List<Integer> getFeatureSubSet(int source,int dest, double [][] pheromone, double [][]corrArray)
	{
		List<Integer> traversedFeatures = new ArrayList<Integer>();// to keep track of already traversed features
		List<Integer> remainingFeatures = new ArrayList<Integer>(featureArray.size());// to keep track of possible remaining features to traverse
		List<Integer> subset = new ArrayList<Integer>();
		int nextFeature;
		
		remainingFeatures.addAll(destinationArray);
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
