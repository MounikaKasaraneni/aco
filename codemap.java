package bigdata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class codemap {
static String[][] features=new String[50000][760];
private static BufferedReader br;
	public static void main(String[] args) throws IOException {
	map();
	}

	public static void map() throws IOException {
		int j=0,k=0,countants=0;
		int ants=400;
		String text;
		BufferedReader bufferedReader = br = new BufferedReader(new FileReader("C:\\Users\\Jyotsna\\Desktop\\Dataset\\train_data.csv"));
		//...................READING FILE.....................
		// read all features of each row
		while ((text = bufferedReader.readLine()) != null) 
		{
			String[] words = text.split(",");
			for(int i=0;i<760;i++)
			{
				features[j][i]=words[i];
			}
			j=j+1;		
		}
		//...................GENERATING ANTS AND ASSIGNING THEM RANDOMLY TO DIFFERENT FEATURES..........
	int rows=j;

	int[] n1= new int[400];
	for(int i=0;i<ants;i++)
	{
	Random rand=new Random();
	 int n= rand.nextInt(760);//**********ERROR CORRECTION NEEDED*************************
	 if(Arrays.asList(n1).contains(n))
	 {
		 continue;
	 }
	 else
	 {
		 n1[k]=n;
		 System.out.println(n1[k]);
		 k=k+1;
	 }
	
	}
	
	for(int h=0;h<rows;h++)
	{
		int l=n1[countants];
		//System.out.println(l+","+features[h][l]);
		for(int i=1;i<10;i++)
		{
			
	}
}
}
}

