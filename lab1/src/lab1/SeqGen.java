package lab1;

import java.util.Random;

public class SeqGen {
	
	public static void main(String[] args)
	{
		Random random = new Random();
		double aaa = 0;
		int repeats = 1000;
		
		// 1000 times
		for (int x=0; x<repeats; x++)
		{
			String s = "";
			// create 3-mer
			for (int y=0; y<3; y++)
			{
				// generate random number from 0 to 100
				int n = random.nextInt(100);
				
				// assign base by number with probabilities (A:.12,C:.38,G:.39,T:.11)
				String base;
				if (n<12)
					base = "A";
				else if (n<50)
					base = "C";
				else if (n<89)
					base = "G";
				else
					base = "T";				
				// concatenate base to string to build trimer
				s = s + base;
			}
			// print out all 1000 3-mers
			System.out.println(s);
			
			// add to count if 3-mer is "AAA"
			if (s.equals("AAA"))
				aaa++;
		}
		
		// get frequency of occurrence of "AAA"
		// this should be around .12*.12*.12 = 0.001728 (~0.17 %)
		double aaa_freq = aaa/repeats;
		System.out.println("Expected frequency:");
		System.out.println((double).12*.12*.12);
		System.out.println("Actual frequency");
		System.out.println(aaa_freq);
	}
}
