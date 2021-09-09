package lab2;

//import java.util.Scanner;
import java.util.Random;


public class AAQuiz {
	// assign important variables
	public static String[] SHORT_NAMES = 
		{"A", "R", "N", "D", "C", "Q", "E", 
		"G", "H", "I", "L", "K", "M", "F", 
		"P", "S", "T", "W", "Y", "V" };
	public static String[] FULL_NAMES = 
		{"alanine", "arginine", "asparagine", 
		"aspartic acid", "cysteine",
		"glutamine",  "glutamic acid",
		"glycine" ,"histidine", "isoleucine",
		"leucine",  "lysine", "methionine", 
		"phenylalanine", "proline", 
		"serine", "threonine", "tryptophan", 
		"tyrosine", "valine"};

	
	public static void main(String[] args) {
		// press enter to start
		System.out.println("Amino acid quiz:\nFor each AA, input 1-letter code\n");
		System.out.println("Input quiz time (in seconds) or press enter for 30s quiz");
		String input = System.console().readLine();
		int limit;
		System.out.println("__"+input+"__");
		
		// determine length of quiz
		if (input.equals("")) limit = 30;
		else limit = Integer.parseInt(input);

		// prepare to get random numbers
		Random random = new Random();
		
		// variables to hold counts
		int correctCounts[] = new int[20];
		int incorrectCounts[] = new int[20];
		
		// get initial time
		final long START = System.currentTimeMillis();
		
		// loop of asking questions
		for(int x=0;;)
		{
			// check current time vs 30 seconds
			long now = System.currentTimeMillis();
			if (now >= START + limit * 1000)
				break;
			
			// get random index (1,20) to determine question
			int i = random.nextInt(20);
			
			// present question
			System.out.println(FULL_NAMES[i]);
			
			// get response
			String guess = System.console().readLine().toUpperCase();
			
			// verify whether answer correct and count it up
			if (guess.equals(SHORT_NAMES[i]))
			{
				System.out.println("Correct");
				correctCounts[i]++;
				
			}
			else if (guess.equals("QUIT"))
			{
				break;
			}
			else
			{
				System.out.println("Incorrect");
				incorrectCounts[i]++;
				// System.exit(0);
			}
		}
		
		// report results
		System.out.println("\nResults\n\nAmino acid\tHit\tMissed\tCode");
		
		// make AA result table readable
		String[] FULL_NAMES_LONG = new String[20];
		for (int x=0; x<20; x++)
		{
			String newname = FULL_NAMES[x];
			int l = newname.length();
			if (l < 8) newname = newname + "\t";
			FULL_NAMES_LONG[x] = newname; 
		}
		
		for (int x=0; x<20; x++)
		{
			if (incorrectCounts[x] != 0 || correctCounts[x] != 0)
				System.out.println(FULL_NAMES_LONG[x] + "\t" + correctCounts[x] + "\t" + incorrectCounts[x] + "\t" + SHORT_NAMES[x]);
		}
	}
	
	

}
