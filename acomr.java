import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.hadoop.io.DoubleWritable;
import java.io.*;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Writable;




public class acomr{
	
	
	//****************ARRAYWRITABLE*********************************************************

	public static class ArrayWritables extends ArrayWritable {
		public ArrayWritables() {
			super(ArrayWritable.class);
		}
		public ArrayWritables(Class valueClass) {
			super(valueClass);
        // TODO Auto-generated constructor stub
		}
		
	}
	
	
	public static class IntArrayWritable extends ArrayWritable {

        public IntArrayWritable(Writable[] values) {
                super(IntWritable.class, values);
        }

        public IntArrayWritable() {
                super(IntWritable.class);
        }
        public IntArrayWritable(Class valueClass, Writable[] values) {
                super(IntWritable.class, values);
        }

        public IntArrayWritable(Class valueClass) {
                super(IntWritable.class);
        }

        public IntArrayWritable(String[] strings) {
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
	public static class MapDischarges extends Mapper<Object, Text,IntWritable, IntArrayWritable >
	{	
		ArrayList<ArrayList<Double>> array1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> subList = new ArrayList<Double>();
		IntArrayWritable outputArray = new IntArrayWritable();
		static List<Integer>featureArray = new ArrayList<Integer>();
		static int destination = 8;

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException
		{
			String line = value.toString();
			String[] words = line.split(",");
			
			//Reading the input and storing in a list
			for(int j=0;j<words.length;j++)
			{
				subList.add(Double.parseDouble(words[j]));
			}
			array1.add(subList);
			
			int columns= subList.size();
			int rows= array1.size();
			double [][] inputMatrix = new double [rows][columns];
			IntWritable pair= new IntWritable(1);
			
			ArrayList<Double> subList2 = new ArrayList<Double>();
			//Storing the input in a matrix
			for(int i=0;i<rows;i++)
			{
				subList2 = array1.get(i);
				for(int j=0;j<columns;j++)
				{
					inputMatrix[i][j]= subList2.get(j);
					
				}
			}
			
			for(int i=0;i<columns;i++)
			{
				featureArray.add(i);
			}
			/*
			PearsonsCorrelation pc = new PearsonsCorrelation();
			RealMatrix input =new Array2DRowRealMatrix(inputMatrix);
			RealMatrix corr;
			corr  = pc.computeCorrelationMatrix(input);
			double correlationArray[][] = corr.getData();
			//}catch(Exception e)
			*/
			
			//Initializing pheromone
			double pheromone[][] = new double[columns][columns];
			pheromone = initializePheromone(columns);
			                   
			
			//*********GENERATE ANTS, RELEASE ANTS ******		
			int numberOfAnts = columns - 1;
			int antSource[] = new int[numberOfAnts];
			for(int i=0 ; i< numberOfAnts ; i++)
			{
				antSource[i] = i; /*************CHECK : Need to generate ants randomly not serially*/
			}
			
			List<List<Integer>> subset= new ArrayList<List<Integer>> (numberOfAnts);
			List<Integer> setOfFeature = new ArrayList<Integer>();
						
			// Finding the routes for each ant
			for(int i = 0; i < numberOfAnts; i++)  {
				subset.add(new ArrayList<Integer>());
			}
		
			for(int i=0;i<numberOfAnts;i++)
			{
				setOfFeature= getFeatureSubSet(antSource[i], destination, pheromone);
				IntWritable [] array2 = new IntWritable[setOfFeature.size()];
			
				//rewriting the input to be sent int the form of hadoop writable		
				for (int k1 = 0; k1 < setOfFeature.size(); k1++) {
					array2[k1] = new IntWritable(setOfFeature.get(k1));
				}
				outputArray.set(array2);
				context.write(new IntWritable(i), outputArray);
			}
			
			
		}
		
		
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
	
		public static int getNextNode(int sourceNode, List<Integer> remainingNodes, double pheromone[][])
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
				//eta=corrArray[sourceNode][remainingNodes.get(i)];//probabilities[i] = calculateProb(sourceNode, remainingNodes,pheromone, corrArray);
				eta=1;
				toque=pheromone[sourceNode][remainingNodes.get(i)];
				upper=eta*toque;
				for(int j=0; j<remainingNodes.size();j++)
				{
					//eta1=corrArray[sourceNode][remainingNodes.get(j)];//probabilities[i] = calculateProb(sourceNode, remainingNodes,pheromone, corrArray);
					eta1=1;
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
	
		public static List<Integer> getFeatureSubSet(int source,int dest,  double pheromone[][])
		{
			List<Integer> traversedFeatures = new ArrayList<Integer>();// to keep track of already traversed features
			List<Integer> remainingFeatures = new ArrayList<Integer>(8);// to keep track of possible remaining features to traverse
			List<Integer> subset = new ArrayList<Integer>();
			int nextFeature;
		
			remainingFeatures.addAll(featureArray);
			remainingFeatures.remove(source);
			traversedFeatures.add(source);
		
			//Loop to find next node to traverse till the destination is reached
			do{
				nextFeature = getNextNode(source, remainingFeatures, pheromone);
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
	public static class ReduceDischarges extends Reducer<IntWritable, IntArrayWritable ,IntWritable, IntArrayWritable >
	{
		public void reduce(IntWritable key, IntArrayWritable values, Context context)throws Exception, InterruptedException
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
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntArrayWritable.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(IntArrayWritable.class);
		// setting input and output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	
}
