import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.*;

public class accuracy {
static int count=0;
static int i=0;
static int count1=0;
	public static void main(String[] args) throws IOException {
		
		File InitializeAnts = new File("InitializeAnts.txt");
		File featureCount = new File("featureCount.txt");
		File file = new File("accuracyoutput.txt");
		BufferedReader br1 = new BufferedReader(new FileReader(file));
		String st1;
		BufferedReader br2 = new BufferedReader(new FileReader(file));
		String st2;
		FileWriter writer1 = new FileWriter(InitializeAnts, false); 
		BufferedWriter bw1  = new BufferedWriter(writer1);
		PrintWriter pw1 = new PrintWriter(bw1);
		FileWriter writer2 = new FileWriter(featureCount, true); 
		BufferedWriter bw2  = new BufferedWriter(writer2);
		PrintWriter pw2 = new PrintWriter(bw2);
		while ((st1 = br1.readLine()) != null)
		{
		    count++;
		}
		String st;
	    List <Integer> newarray=new ArrayList<Integer>();
		String[] accuracy=new String[count];
		
		HashMap< Double,Integer> map = new HashMap<>();   
		while ((st2 = br2.readLine()) != null)
		{
			 
		    accuracy[count1]=st2;
		    i++;
		    // Store count as key and accuracy as value in map
		    map.put( Double.parseDouble(st2),count1);
		    count1++;
		  }
		Arrays.sort(accuracy);
		int g=accuracy.length-1;
		Double highest = Double.parseDouble(accuracy[g]);
		double threshold = 3.0;
		
		double checkAccuracy = highest - threshold;
		for(int i=0;i<accuracy.length;i++)
		{
		if(Double.parseDouble(accuracy[i])>=checkAccuracy)
		{
			double y=Double.parseDouble(accuracy[i]);
			if(map.containsKey(y))
			{
				int a = map.get(y);
				String line32 = Files.readAllLines(Paths.get("antRouteFeatures.txt")).get(a);
				String value[]=line32.split(":");
				String num[]=value[1].split(",");
				for(int h=0;h<num.length;h++)
				{
					newarray.add(Integer.parseInt(num[h]));
				}
			}

		}
		}
		List<Integer> finalfeatures= newarray.stream().distinct().collect(Collectors.toList());

		for(int i=0;i<finalfeatures.size();i++)
			{
				pw1.print(finalfeatures.get(i));
				if(i < finalfeatures.size() - 1)
				{
					pw1.print(",");
				}
			}

	int c = finalfeatures.size();
	pw2.println(c);
			pw1.flush();
			pw1.close();
			pw2.flush();
			pw2.close();
				
			
	}

}
