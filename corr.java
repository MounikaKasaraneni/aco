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
import org.apache.commons.math3.exception.MathIllegalNumberException;




public class corr{
	
	
	//****************ARRAYWRITABLE*********************************************************
	public static class IntArrayWritable extends ArrayWritable {
		public IntArrayWritable() {
			super(IntWritable.class);
		}
	}
	public class TwoDArrayWritables extends TwoDArrayWritable
	{
		public TwoDArrayWritables() {
			super(DoubleWritable.class);
		}
		public TwoDArrayWritables(Class valueClass) {
			super(valueClass);
        // TODO Auto-generated constructor stub
		}
	}
	
	
	//*********************MAPPER**************************************************************
	public static class MapDischarges extends Mapper<Object, Text,IntWritable, TwoDArrayWritable >
	{	
		ArrayList<ArrayList<Double>> array1 = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> subList = new ArrayList<Double>();
		IntArrayWritable outputArray = new IntArrayWritable();
		TwoDArrayWritable array = new TwoDArrayWritable (DoubleWritable.class);
			
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
			
		
			PearsonsCorrelation pc = new PearsonsCorrelation();
			Array2DRowRealMatrix input =new Array2DRowRealMatrix(inputMatrix);
			RealMatrix corr;
			corr  = pc.computeCorrelationMatrix(input);
			double correlationArray[][] = corr.getData();
			
			DoubleWritable [][]sendingArray = new DoubleWritable[columns][columns];
			for(int i=0;i<columns;i++)
			{
				for(int j=0;j<columns;j++)
				{
					sendingArray[i][j]= new DoubleWritable(correlationArray[i][j]);
					
				}
			}
			array.set(sendingArray);
			context.write(pair, array);
			
		
		}
	}
	
	// *********************************REDUCER CLASS ***************************************************************
	public static class ReduceDischarges extends Reducer<IntWritable, TwoDArrayWritable ,IntWritable, TwoDArrayWritable >
	{
	
		public void reduce(IntWritable key, TwoDArrayWritable values, Context context)throws IOException, InterruptedException
		{
			context.write(key, values);
		}
	}
	
	//************************************DRIVER CLASS *************************************************************************8
	public static void main(String [] args) throws Exception
	{
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "correlation array");
        job.setJarByClass(corr.class);
		//setting the mapper , reducer class and output key and value class 
        job.setMapperClass(MapDischarges.class);
        job.setReducerClass(ReduceDischarges.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(TwoDArrayWritable.class);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(TwoDArrayWritable.class);
		// setting input and output paths
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	
}
