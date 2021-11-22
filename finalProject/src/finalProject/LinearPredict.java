package finalProject;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.output.prediction.CSV;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.RandomForest;

public class LinearPredict {
//	private final Instances snpData;
	
//	private final Instances getInstancesFromDataSource(DataSource source) throws Exception
//	{
//		Instances dataSet = source.getDataSet();
//		dataSet.setClassIndex(dataSet.numAttributes()-1); // predicting last column
//		return dataSet;	
//	}
	
//	public LinearPredict(DataSource snpARFF) throws Exception
//	{
//		Instances dataSet = snpARFF.getDataSet();
//		dataSet.setClassIndex(dataSet.numAttributes()-1); // predicting last column
//		this.snpData = dataSet;	
//	}
//		LinearPredict lp = new LinearPredict(trainingData);
//		System.out.println(lp.snpData);	
	
	
	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Loading data\n");
		// load training data
		DataSource trainingSource = new DataSource("snp_density_train.arff");
		Instances trainingData = trainingSource.getDataSet();
		trainingData.setClassIndex(trainingData.numAttributes()-1);
		// load testing data
		DataSource testingARFFSource = new DataSource("snp_density_test.arff");
		Instances testingData = testingARFFSource.getDataSet();
		testingData.setClassIndex(testingData.numAttributes()-1);
	
		Map<String,Double> errorLog = new HashMap<String,Double>();
		Map<String,AbstractClassifier> classifiers = new HashMap<String,AbstractClassifier>();
		
		// prepare to test multiple classifiers
		classifiers.put("LinearRegression", new LinearRegression());
		classifiers.put("RandomForest", new RandomForest());
		classifiers.put("MultilayerPerceptron", new MultilayerPerceptron());
		
		for (Map.Entry<String,AbstractClassifier> pair : classifiers.entrySet())
		{
			String name = pair.getKey();
			AbstractClassifier classifier = pair.getValue();
		
			// create prediction models using training data
			System.out.println("Creating " + name + " model\n");
			classifier.buildClassifier(trainingData);

			// prepare to output cross-validation results as csv
			StringBuffer buffer = new StringBuffer();
			CSV csv = new CSV();
			csv.setBuffer(buffer);
			csv.setOutputFile(new java.io.File(name + "_output.csv")); // to store to output csv
			
			// evaluate model
			Evaluation eval = new Evaluation(trainingData);
//			eval.evaluateModel(classifier, testingData);
			eval.crossValidateModel(classifier, testingData, 10, new Random(1), csv);
			errorLog.put(name, eval.relativeAbsoluteError());
			
			// look at regression results/evaluation
			System.out.println(eval.toSummaryString(name + " evaluation results:", false));
//			System.out.println("Prediction as csv:");	// uncomment to show csv output
//			System.out.println(buffer.toString());		// uncomment to show csv output
			System.out.println("\n");
			System.out.println();
		}

		// decide best model (via lowest relative absolute error)
		double lowestError = 100.0;
		String bestClassifier = "undecided";
		for (Map.Entry<String,Double> pair : errorLog.entrySet())
		{
			String name = pair.getKey();
			double error = pair.getValue().doubleValue();
			
			if ( error < lowestError )
				{
				lowestError = error;
				bestClassifier = name;
				}
		}
		System.out.println("The best classifier was "+ bestClassifier);
	}

}
