package lab3;

import java.util.Random;

public class WeightedMolecule
{
	public static String generateRandomSequence(char[] alphabet, float[] weights, int length) throws Exception
	{		
		// check same length
		if (alphabet.length != weights.length)
		{
			throw new Exception("lenths of alphabet and weights are not the same");
		}
		
		// check length >= 0
		if (length < 0)
		{
			throw new Exception("lenth must be >= 0");
		}
		
		// check sum of weights ~= 1
		float sum = 0;
		for (int i=0; i<alphabet.length; i++)
		{
			sum = sum + weights[i];
		}
		if (sum > 1.01d || sum < 0.99d)
		{
			throw new Exception("weights must add up to 1");
		}
		
		Random random = new Random();
		
		// create sequence
		// keep base corresponding to the first time the random float is less than the sum of all previous weights
		StringBuilder b = new StringBuilder();
		for (int i=0; i<length; i++)
		{
			float r = random.nextFloat();
			boolean didAppend = false;
			for (int x=0; x<length; x++)
			{
				r = r - weights[x];
				if (r < 0)
				{
					b.append(alphabet[x]);
					didAppend = true;
					break;
				}
			}
			// if (by rounding error) b.append wasn't invoked, append last character in alphabet
			if (! didAppend)
				b.append(alphabet[-1]);
		}
		
		return b.toString();
				
	}
	
	
	public static void main(String[] args) throws Exception
	{
		float[] dnaWeights = { .3f, .3f, .2f, .2f };
		char[] dnaChars = { 'A', 'C', 'G', 'T'  };
		
		// a random DNA 30 mer
		System.out.println(generateRandomSequence(dnaChars, dnaWeights,30));
		
		// background rate of residues from https://www.science.org/doi/abs/10.1126/science.286.5438.295
		float proteinBackground[] =
			{0.072658f, 0.024692f, 0.050007f, 0.061087f,
		        0.041774f, 0.071589f, 0.023392f, 0.052691f, 0.063923f,
		        0.089093f, 0.023150f, 0.042931f, 0.052228f, 0.039871f,
		        0.052012f, 0.073087f, 0.055606f, 0.063321f, 0.012720f,
		        0.032955f}; 
			

		char[] proteinResidues = 
				new char[] { 'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
							 'V', 'W', 'Y' };
		
		// a random protein with 30 residues
		System.out.println(generateRandomSequence(proteinResidues, proteinBackground, 30));
		
	}
}
