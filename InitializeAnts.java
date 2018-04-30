
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.io.*;
public class InitializeAnts{
	 static int features=9;
	public static void main(String []args) throws Exception
	{
		
		File InitializeAnts = new File("InitializeAnts.txt");
		
		FileWriter writer1 = new FileWriter(InitializeAnts, false); 
		BufferedWriter bw1  = new BufferedWriter(writer1);
		PrintWriter pw1 = new PrintWriter(bw1);
		
		
			for(int i=0;i<features;i++)
			{
				pw1.print(i );
				if(i < features - 1)
				{
					pw1.print(",");
				}
			}		
			pw1.flush();
			pw1.close();
				
		
	}
	
}