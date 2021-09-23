package lab4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FastaSequence
{	
	private final String header;
	private final String seq;
	
	public FastaSequence(String fasta) throws Exception
	{
		final String[] headSeq = fasta.split("\n",2);
		this.header = headSeq[0].substring(1).trim();
		this.seq = headSeq[1].trim();
		if (this.header.equals("")) throw new Exception("Empty header found");
		if (this.seq.equals("")) throw new Exception("The sequence for '" + this.header + "' is missing.");
	}
	
	public String getHeader()
	{
		return this.header;
	}
	
	public String getSequence()
	{
		return this.seq;
	}
	
	// count occurrences of provided base or bases
	public int countBase(String bases)
	{
		int count = 0;
		for (char c : seq.toCharArray())
		{
			for (char b : bases.toCharArray())
			{
				if (c == b) count++;
			}
		}
		return count;
	}
	
	public float getGCRatio()
	{
		int gc = countBase("GC");
		int len = seq.length();
		return (float)gc/len;
	}
	
	public static List<FastaSequence> readFastaFile(String filepath) throws Exception 
	{
		BufferedReader reader = new BufferedReader(new FileReader(new File(filepath)));
		
		String fastaString = "";
		List<FastaSequence> fastaList = new ArrayList<FastaSequence>();
		boolean foundFirstHeader = false;
		// read in file, build up strings of "Header\nSequence"
		for (String line = reader.readLine();line != null; line = reader.readLine())
		{
			line = line.trim();
			if (line.length() == 0) continue;
			if (line.charAt(0) == '>')
			{
				if (foundFirstHeader == false) foundFirstHeader = true;
				
				line+="\n";
				if (! fastaString.equals(""))
				{
					// create FastaSequence object if string is complete
					fastaList.add(new FastaSequence(fastaString));
					fastaString = "";
				}
				}
			
			// skip any leading lines before first fasta headerline
			if (foundFirstHeader) fastaString+=line;
		}
		if (foundFirstHeader == false) throw new Exception(filepath + " doesn't look like a fasta file.");
		
		// add final FastaSequence object to list
		fastaList.add(new FastaSequence(fastaString));
		reader.close();
		
		return fastaList;			
	}



	public static void writeTableSummary( List<FastaSequence> list, File outputFile) 
			throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		// header line
		writer.write("sequenceID numA numC numG numT sequence\n".replaceAll(" ","\t"));
		
		// get/write desired info about seq
		for( FastaSequence fs : list)
	     {
	         writer.write(fs.getHeader() + "\t" + fs.countBase("A") + "\t" + fs.countBase("C") + "\t" + fs.countBase("G") + "\t" + fs.countBase("T") + "\t" + fs.getSequence() + "\n");
	     }
		writer.flush(); writer.close();
	}

	

	public static void main(String[] args) throws Exception
	{
		// parse fasta file and return list of FastaSequence objects
		List<FastaSequence> fastaList = FastaSequence.readFastaFile("test.fasta");
		
		// print info about seqs
	    for( FastaSequence fs : fastaList)
	    {
	    	System.out.println(fs.getHeader());
	        System.out.println(fs.getSequence());
	        System.out.println(fs.getGCRatio());
	    }
	
	    // write table file with more info
	    File myFile = new File("fastaTableOut.txt");
	    writeTableSummary( fastaList,  myFile);
	}


}
