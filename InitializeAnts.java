import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.io.*;
import java.util.Random; 
public class InitializeAnts{
	 static int features=759;
	public static void main(String []args) throws Exception
	{
		
		Random rand = new Random();
		// output file initializeAnts.txt has the final set of features to be inputed to aco
		File InitializeAnts = new File("InitializeAnts.txt");
		
		FileWriter writer1 = new FileWriter(InitializeAnts, false); 
		BufferedWriter bw1  = new BufferedWriter(writer1);
		PrintWriter pw1 = new PrintWriter(bw1);
		
		 ArrayList<Integer> arr = new ArrayList<Integer>(759);
			while(arr.size() < 759)
			{
				int random_integer = rand.nextInt(759-1);
				
				if(!(arr.contains(random_integer)))
				{
					arr.add(random_integer);
					pw1.print(random_integer);
					if(arr.size() < features)
					{
						pw1.print(",");
					}
				}
				
			}		
			pw1.flush();
			pw1.close();	
	}	
}
