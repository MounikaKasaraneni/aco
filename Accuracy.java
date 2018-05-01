//importing the required packages
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
// getting the highest accuracy out of one iteration
public class Accuracies {
static int count=0;
static int i=0;
	public static void main(String[] args) throws IOException {
		 File file = new File("accuracyoutput.txt"); 
		  BufferedReader br = new BufferedReader(new FileReader(file));
		   String st;
	  List <Integer> newarray=new ArrayList<Integer>();
		System.out.println(count);
		String[] accuracy=new String[4];
		BufferedReader br1 = new BufferedReader(new FileReader(file));
		String st1;
		HashMap< Double,Integer> map = new HashMap<>();   
		  while ((st1 = br1.readLine()) != null)
		  {
			 
		    accuracy[count]=st1;
		    i++;
		    // Store count as key and accuracy as value in map
		    map.put( Double.parseDouble(st1),count);
		    count++;
		  }
		for(int j=0;j<count;j++)
		{
			System.out.println(accuracy[j]);
		}
		//getting the accuracies in the order
		Arrays.sort(accuracy);
		System.out.printf("Modified arr[] : %s",
                Arrays.toString(accuracy));
		int g=accuracy.length-1;
		//System.out.println("\n"+g);
		Double highest = Double.parseDouble(accuracy[g]);
		
		double threshold = 3.0;
		
		double checkAccuracy = highest - threshold;
		
		//getting the accuracy value is for which ant
		for(int i=0;i<accuracy.length;i++)
		{
		if(Double.parseDouble(accuracy[i])>=checkAccuracy)
		{
			double y=Double.parseDouble(accuracy[i]);
			if(map.containsKey(y))
			{
				int a = map.get(y);
				String line32 = Files.readAllLines(Paths.get("antRouteFeatures.txt")).get(a);
				//System.out.println("\n");
				//System.out.println(line32);
				String value[]=line32.split(":");
				//System.out.println(value[1]);
				String num[]=value[1].split(",");
				for(int h=0;h<num.length;h++)
				{
					newarray.add(Integer.parseInt(num[h]));
				}
			}

		}
		}
		//printing the features without duplication from the selected ants accuracy
		List<Integer> finalfeatures= newarray.stream().distinct().collect(Collectors.toList());
		for(int i=0;i<finalfeatures.size();i++){
		    System.out.println(finalfeatures.get(i));
		   
		} 
	}

}
