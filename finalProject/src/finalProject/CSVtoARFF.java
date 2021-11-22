package finalProject;

import java.io.File;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class CSVtoARFF
{
	private final String csv;
	private final String arff;

	//String csv,String arff
	
	public CSVtoARFF(String csv_file,String arff_file) throws Exception
	{
		this.csv = csv_file;
		this.arff = arff_file;
		
	}
	
	
	private void csvConvert() throws Exception
	{
		CSVLoader loader = new CSVLoader();
		System.out.println("reading csv");
		loader.setSource(new File(this.csv));
		System.out.println("Loading data");
		Instances data = loader.getDataSet();
//		Instances data = DataSource.read(this.csv);
		
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(data);
	    System.out.println("Creating new file");
	    saver.setFile(new File(this.arff));
	    System.out.println("Writing out to " + this.arff);
	    saver.writeBatch();
	}
	

	public static void main(String[] args) throws Exception
	{
		
		String csv = "C:\\Users\\samku\\Documents\\GitHub\\javaPractice\\finalProject\\snp_density_test.csv";
		String arff = "C:\\Users\\samku\\Documents\\GitHub\\javaPractice\\finalProject\\snp_density_test.arff";
		
//		String csv = "C:\\Users\\samku\\Documents\\GitHub\\javaPractice\\finalProject\\snp_density_train.csv";
//		String arff = "C:\\Users\\samku\\Documents\\GitHub\\javaPractice\\finalProject\\snp_density_train.arff";
		
		// convert & write out arff
		System.out.println("Converting file");
		CSVtoARFF converter = new CSVtoARFF(csv,arff);
		System.out.println(""+converter.csv);
		System.out.println(""+converter.arff);
		converter.csvConvert();
				
	}
}
