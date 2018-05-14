import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.WritableComparable;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.DoubleWritable;
import java.io.*;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Writable;
import java.util.*;
import java.io.*;
import java.util.Random; 


public class acomr{
	//****************ARRAYWRITABLE*********************************************************

	public static class ArrayWritables extends ArrayWritable {
		public ArrayWritables() {
			super(ArrayWritable.class);
		}
		public ArrayWritables(Class valueClass) {
			super(valueClass);
		}
		
	}
	
	public static class DoubleArrayWritable extends ArrayWritable {

        public DoubleArrayWritable(Writable[] values) {
                super(DoubleWritable.class, values);
        }

        public DoubleArrayWritable() {
                super(DoubleWritable.class);
        }
        public DoubleArrayWritable(Class valueClass, Writable[] values) {
                super(DoubleWritable.class, values);
        }

        public DoubleArrayWritable(Class valueClass) {
                super(DoubleWritable.class);
        }

        public DoubleArrayWritable(String[] strings) {
                super(strings);
        }
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (String s : super.toStrings())
			{
				sb.append(s).append(" ");
			}
			return sb.toString();
		}
	}
	
	//*********************MAPPER**************************************************************
	public static class MapDischarges extends Mapper<Object, Text,NullWritable, DoubleArrayWritable >
	{	
		ArrayList<ArrayList<Double>> array1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> subList = new ArrayList<Double>();
		DoubleArrayWritable outputArray = new DoubleArrayWritable();
		DoubleArrayWritable outputArray1 = new DoubleArrayWritable();
		DoubleArrayWritable outputArray2 = new DoubleArrayWritable();
		
		static int destination = 49;
		//IntWritable outputKey = new IntWritable();			
			static int columns=760;
			int j=0,k=0,s=0,check=0;
			int p=0;
			double inputMatrix[][]=new double[columns][columns];
			double pheromone[][]=new double[columns][columns];
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException
		{
			List<Integer> features = new  ArrayList<Integer>();
			List<Double> Corr = new  ArrayList<Double>();
			List<Double> Pher = new  ArrayList<Double>();
			String line = value.toString();
			String[] words = line.split(" ");
			if(s<columns)
			{
				for(int i=0;i<words.length;i++)
				{
					inputMatrix[j][i]=Double.parseDouble(words[i]);
				}
				j++;
				
			}
			else if(columns <= s && s<(2*columns))
			{	
				for(int i=0;i<words.length;i++)
				{
					pheromone[k][i]=Double.parseDouble(words[i]);
				}
				k++;
			}
			else
			{ 
				for(int i=0;i<words.length;i++)
				{
					features.add(Integer.parseInt(words[i]));	
				}				
			}
			s++;
	
			check = columns+columns+1;
			if(s == check)
			{

				//*********GENERATE ANTS, RELEASE ANTS ******		
				int numberOfAnts = (features.size()) - 1;
				//int numAnts[] = new int[columns-1];
				int antCount = 10;
			System.out.println("num="+antCount);
				int antSource[] = new int[antCount];
				Random rand = new Random();
				ArrayList<Integer> arr = new ArrayList<Integer>(numberOfAnts);
				int y=0;
				while(arr.size() < antCount)
				{
					int random_integer = rand.nextInt(numberOfAnts-1);
				
					if(!(arr.contains(random_integer)))
					{
						if(features.contains(random_integer))
						{
						arr.add(random_integer);
						antSource[y] =random_integer;
						System.out.println("ants"+antSource[y]);
						y++;
						}
					}
				}
				pheromone = updatePheromone(pheromone,inputMatrix,features);
				for(int i=0;i<pheromone.length;i++)
				{
					for (int j=0;j<pheromone[i].length;j++)
					{
						System.out.print(pheromone[i][j]+"\t");
					}
					System.out.println("");
				}
				List<List<Integer>> subset= new ArrayList<List<Integer>> (numberOfAnts);
				List<Integer> setOfFeature = new ArrayList<Integer>();			
				// Finding the routes for each ant
				for(int i = 0; i < numberOfAnts; i++)  {
					subset.add(new ArrayList<Integer>());
				}
			
				for(int i=0;i<antSource.length;i++)
				{
					System.out.println("source" +antSource[i] + "\t" + "path: ");
					setOfFeature= getFeatureSubSet(antSource[i], destination, pheromone, inputMatrix, features);
					DoubleWritable [] array2 = new DoubleWritable[setOfFeature.size()];
					for (int k1 = 0; k1 < setOfFeature.size(); k1++) {
						array2[k1] = new DoubleWritable(setOfFeature.get(k1));
						System.out.print(setOfFeature.get(k1)+"\t");
					}
					System.out.println("");
		
					outputArray.set(array2);
					//outputKey.set(antSource[i]);
					context.write(NullWritable.get(), outputArray);	
				}
				DoubleWritable [] arrayPher = new DoubleWritable[columns];
				for(int i=(columns-1);i>=0;i--)
				{
					
					for(int j=0;j<columns;j++)
					{
						Pher.add(pheromone[i][j]);
					}
						for (int k1 = 0; k1 < Pher.size(); k1++) {
						arrayPher[k1] = new DoubleWritable(Pher.get(k1));
						System.out.print(Pher.get(k1)+"\t");
						}
						outputArray2.set(arrayPher);
						//outputKey.set(j);
						context.write(NullWritable.get(), outputArray2);
					Pher.clear();
				}
				DoubleWritable [] arrayCorr = new DoubleWritable[columns];
				for(int i=(columns-1);i>= 0;i--)
				{
					
					for(int j=0;j<columns;j++)
					{
						Corr.add(inputMatrix[i][j]);
					}
						for (int k1 = 0; k1 < Corr.size(); k1++) {
						arrayCorr[k1] = new DoubleWritable(Corr.get(k1));
						System.out.print(Corr.get(k1)+"\t");
						}
						outputArray1.set(arrayCorr);
						//outputKey.set(j);
						context.write(NullWritable.get(), outputArray1);
					Corr.clear();
				}
				
			}
		}
		
		//*************************UPDATE PHEROMONE ******************************
	
		public static double[][] updatePheromone(double[][] pheromone, double[][] correlationArray, List<Integer> featureArray)
		{
			double s = 0.1; 
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

	
		public static int getNextNode(int sourceNode, List<Integer> remainingNodes, double pheromone[][], double[][] corrArray, int destination)
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
				eta=corrArray[sourceNode][remainingNodes.get(i)];
				            //probabilities[i] = calculateProb(sourceNode, remainingNodes,pheromone, corrArray);
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
			nextNode = destination;
		
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
	
		public static List<Integer> getFeatureSubSet(int source,int dest,  double pheromone[][],double[][]inputMatrix, List<Integer> featureA)
		{
			
			List<Integer> traversedFeatures = new ArrayList<Integer>();// to keep track of already traversed features
			List<Integer> remainingFeatures = new ArrayList<Integer>(featureA.size());// to keep track of possible remaining features to traverse
			List<Integer> subset = new ArrayList<Integer>();
			int nextFeature;
		
			remainingFeatures.addAll(featureA);
			for(Integer x: remainingFeatures)
			{
				if(x == source)
				{
					remainingFeatures.remove(x);
					break;
				}	
			}
			
			traversedFeatures.add(source);
			
			
			//Loop to find next node to traverse till the destination is reached
			do{
				
				nextFeature = getNextNode(source, remainingFeatures, pheromone, inputMatrix, dest);
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

	// *********************************REDUCER CLASS ***************************************************************
	public static class ReduceDischarges extends Reducer<NullWritable, DoubleArrayWritable ,NullWritable, DoubleArrayWritable >
	{
		public void reduce(NullWritable key, DoubleArrayWritable values, Context context)throws Exception, InterruptedException
		{
			context.write(key, values);
		}
	}
	

	
	//************************************DRIVER CLASS *************************************************************************8
	public static void main(String [] args) throws Exception
	{
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "mounika_kasaraneni_Program1");
        job.setJarByClass(acomr.class);
		//setting the mapper , reducer class and output key and value class 
        job.setMapperClass(MapDischarges.class);
        job.setReducerClass(ReduceDischarges.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(DoubleArrayWritable.class);
		job.setMapOutputKeyClass(NullWritable.class);
		job.setMapOutputValueClass(DoubleArrayWritable.class);
		// setting input and output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	
}

