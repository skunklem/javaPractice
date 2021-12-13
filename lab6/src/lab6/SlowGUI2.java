package lab6;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import lab6.SlowStep.*;

public class SlowGUI2 extends JFrame
{
	private static final long serialVersionUID = -1709315542670506804L;
	private volatile static ConcurrentHashMap<Integer,Boolean> primes = new ConcurrentHashMap<Integer,Boolean>();
	private volatile static int num_threads;
	private volatile static int stopping_point;
	private volatile static int runsStarted = 0;
	protected volatile static boolean cancel = false;
	private volatile static double timeFor1thread;
	protected volatile static boolean needToSetForSingleThreadedRun = false;
	protected volatile static boolean comparisonIsComplete = false;
	private volatile static boolean needToResetReportVariables;
	private volatile static boolean needToUpdateReport = false;
	private volatile static boolean doneFindingPrimes;
	private volatile static String report;
	private volatile static String finalReport;
	private volatile static double totalTime = 0;
	private volatile static long startTime = 0;
	private volatile static Set<Integer> nums_checked = Collections.synchronizedSet(new HashSet<>());
	private volatile static ConcurrentHashMap<Integer,Boolean> primes1 = new ConcurrentHashMap<Integer,Boolean>();
	private volatile static Set<Integer> numsChecked1 = Collections.synchronizedSet(new HashSet<>());
	
	private volatile static JButton cancelButton = new JButton("Cancel");
	private volatile static JButton startButton = new JButton("Start");
	private volatile static JTextField threadNumBox = new JTextField("5");
	private volatile static JTextField threadDirections = new JTextField("# background threads");
	private volatile static JTextField highNumBox = new JTextField("20000");
	private volatile static JTextField highNumDirections = new JTextField("Pick a high number");
	private volatile static JCheckBox compareSpeedupBox = new JCheckBox();
	private volatile static JTextField compareBoxDirections = new JTextField("Compare with 1 thread");
	private volatile static JTextArea reportBox = new JTextArea("\n\tReset the above parameters or hit Start");
	
	private static void pressCancelButtonFromThread()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable() 
			{
				@Override
				public void run() {
					cancelButton.doClick();						
				}
			});
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void ensureThisIsLastReport(String finalReport)
	{
		SwingUtilities.invokeLater(new Runnable()
		{						
			@Override
			public void run() {
				// ensure this is the last report sent out
				do {
					if (! reportBox.isEditable()) reportBox.setEditable(true);
					reportBox.setText(finalReport);
					reportBox.setEditable(false);
				} while (! reportBox.getText().equals(finalReport));							
			} 
		});
	}

	private static void cancelAndReport(String finalReport)
	{
		pressCancelButtonFromThread();
		ensureThisIsLastReport(finalReport);
	}
	
	private static void cancelAndWriteRegularFinalReport()
	{
		int numsChecked = nums_checked.size() + 1; //(+1 because 1 isn't included in the Set)
		if (doneFindingPrimes) finalReport = "Run completed\n";
		else finalReport = "Run canceled\n";
		finalReport += "\ttime passed: " + totalTime + " seconds\n"
					+ "\tnumbers checked: " + numsChecked + "\n"
					+ "\tprimes found: " + primes.size() + "\n";
		cancelAndReport(finalReport);
	}
	
	private static void cancelAndWriteComparisonReport()
	{
		double ratio = timeFor1thread/totalTime;
		System.out.println(ratio);
		if (! doneFindingPrimes) finalReport = "Comparison failed";
		else
		{
			DecimalFormat f = new DecimalFormat("##.00");
			finalReport = "Comparison complete  -  found " + primes.size() + " primes\n"
				+ "\tThreads: 1 vs " + num_threads + "\n"
				+ "\tTime (sec): " + timeFor1thread + " vs " + totalTime + "\n"
				+ "\tRatio: " + f.format(ratio) + " : 1";
		}
		cancelAndReport(finalReport);
	}
	
	private static class ReportWriter implements Runnable
	{
		int numThreads;
		ConcurrentHashMap<Integer, Boolean> primesSet;
		Set<Integer> numsCheckedSet;
		
		@Override
		public void run()
		{					
			while(! cancel)
			{
				while (! needToUpdateReport)
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				if (needToResetReportVariables)
				{
					System.out.println("resetting vars");
					if (compareSpeedupBox.isSelected() && needToSetForSingleThreadedRun)
					{
						System.out.println("resetting for 1 thread");
						numThreads = 1;
						primesSet = primes1;
						numsCheckedSet = numsChecked1;
						needToSetForSingleThreadedRun = false;
					}
					else
					{
						numThreads = num_threads;
						primesSet = primes;
						numsCheckedSet = nums_checked;
					}
					needToResetReportVariables = false;
				}
				try
				{
					long timeSoFar = System.currentTimeMillis()-startTime;
					if (numsCheckedSet == null) continue; // can happen when doing normal run
					int numsChecked = numsCheckedSet.size() + 1; //(+1 because 1 isn't included in the Set)
					report = "Running " + numThreads + " workers...\n"
							+ "\ttime passed: " + timeSoFar/1000 + " seconds\n"
							+ "\tnumbers checked: " + numsChecked + "\n"
							+ "\tprimes found: " + primesSet.size() + "\n";
					SwingUtilities.invokeLater(new Runnable()
					{						
						@Override
						public void run() {
							if (reportBox.isEditable()) reportBox.setText(report);
						}
					});
					Thread.sleep(1000); // no point in updating report any faster
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					pressCancelButtonFromThread();
				}
			}
		}
	}
	
	private static class StartSingleSlowStep implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				int numberOfWorkers = num_threads;
				if (numberOfWorkers == 0)
				{
					cancelAndReport("Must select more threads than 0.");
				}
				else
				{
					if (numberOfWorkers>1)
						System.out.println("New multithreaded run with " + numberOfWorkers+ " threads. HighNum: "+stopping_point);
					
					Semaphore semaphore = new Semaphore(numberOfWorkers);
					long startTime = System.currentTimeMillis();
					
					for (int workerNum=0; workerNum<numberOfWorkers; workerNum++)
					{
						semaphore.acquire();
						System.out.println("Thread " + workerNum +" in StartSlowStep");
						new Thread(new PrimeFindingWorker(workerNum, numberOfWorkers, primes, semaphore, stopping_point, nums_checked)).start();									
					}
					
					new Thread(new ReportWriter()).start();
					needToUpdateReport = true;
					
					int returnedWorkers = 0;
					while (returnedWorkers < numberOfWorkers)
					{
						semaphore.acquire();
						returnedWorkers++;
					}
					long endTime = System.currentTimeMillis();
					totalTime = (endTime-startTime)/1000.;
					System.out.println(totalTime + " seconds");
					System.out.println(nums_checked.size() +1 + " numbers checked");
	
					if (! cancel) doneFindingPrimes = true;
					System.out.println("All workers done");
					
					cancelAndWriteRegularFinalReport();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				pressCancelButtonFromThread();
			}
		}
	}
	
	private static class StartSlowStepComparison implements Runnable
	{		
		@Override
		public void run()
		{
			try
			{
				if ((num_threads == 0) | (num_threads == 1))
				{
					cancelAndReport("Must select at least 2 threads.");
				}
				else
				{
					System.out.println("New comparison run: 1 vs " + num_threads + " threads");
					int[] numbers = {1,num_threads};
					long startTime;
					needToSetForSingleThreadedRun = true; // single threaded run always goes first
					for (int numberOfWorkers : numbers)
					{
						if (cancel) break;
						System.out.println("New run with Threads: " + numberOfWorkers+ "\tHighNum: "+stopping_point);
						
						Semaphore semaphore = new Semaphore(numberOfWorkers);
						startTime = System.currentTimeMillis();
						
						for (int workerNum=0; workerNum<numberOfWorkers; workerNum++)
						{
							semaphore.acquire();
							System.out.println("Thread " + workerNum +" in StartSlowStep");
							if (numberOfWorkers == 1)
							{
								new Thread(new PrimeFindingWorker(0,numberOfWorkers,primes1,semaphore,stopping_point,numsChecked1)).start();
							}
							else
							{
								new Thread(new PrimeFindingWorker(workerNum, numberOfWorkers, primes, semaphore, stopping_point, nums_checked)).start();
							}						
						}
						needToUpdateReport = true;	
						int returnedWorkers = 0;
						
						while (returnedWorkers < numberOfWorkers)
						{
							semaphore.acquire();
							returnedWorkers++;
						}
						long endTime = System.currentTimeMillis();
						if (numberOfWorkers == 1)
						{
							timeFor1thread = (endTime-startTime)/1000.;
							System.out.println(timeFor1thread + " seconds");
							System.out.println(numsChecked1.size() +1 + " numbers checked");
						}
						else 
						{
							totalTime = (endTime-startTime)/1000.;
							System.out.println(totalTime + " seconds");
							System.out.println(nums_checked.size() +1 + " numbers checked");
						}
						needToResetReportVariables = true;
					}
					System.out.println("All workers done");

					// sanity check
					if (! (primes1.size()==primes.size()))
						{
						String finalReport = "Different numbers of primes found. One run may not have completed";
						System.out.println(finalReport);
						cancelAndReport(finalReport);
						}
					else
					{
						doneFindingPrimes = true;
						cancelAndWriteComparisonReport();
					}
				}
				// make sure this report comes after other report
				cancelAndWriteComparisonReport();
				
				// sanity check
				if (! (primes1.size()==primes.size())) System.out.println("Different numbers of primes found. One run may not have completed");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				pressCancelButtonFromThread();
			}
		}
	}

	
	// gui prep
	private JPanel bottomPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,2,50,20));
		cancelButton.setEnabled(false);
		startButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// refresh everything
				totalTime = 0;
				cancel = false;
				PrimeFindingWorker.cancel = false;
				needToResetReportVariables = true;
				primes = new ConcurrentHashMap<Integer,Boolean>();
				primes1 = new ConcurrentHashMap<Integer,Boolean>();
				nums_checked = Collections.synchronizedSet(new HashSet<>());
				numsChecked1 = Collections.synchronizedSet(new HashSet<>());		
				startButton.setEnabled(false);
				cancelButton.setEnabled(true);
				doneFindingPrimes = false;
				reportBox.setEditable(true);
				threadNumBox.setEditable(false);
				highNumBox.setEditable(false);
				compareSpeedupBox.setEnabled(false);
				stopping_point = Integer.valueOf(highNumBox.getText());
				num_threads = Integer.valueOf(threadNumBox.getText());
				System.out.println(Integer.valueOf(threadNumBox.getText()));
				try
				{
					startTime = System.currentTimeMillis();
					if (compareSpeedupBox.isSelected()) 
						new Thread(new StartSlowStepComparison()).start();
					else 
						new Thread(new StartSingleSlowStep()).start();
					if (runsStarted == 0) new Thread(new ReportWriter()).start();
					needToUpdateReport = true;
				} 
				catch (Exception e1) {
					e1.printStackTrace();
					System.exit(1);
				}	
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// cancel and reset gui
				System.out.println("Canceling");
				startButton.setEnabled(true);
				cancelButton.setEnabled(false);
				compareSpeedupBox.setEnabled(true);
				cancel = true;
				needToUpdateReport = false;
				PrimeFindingWorker.cancel = true;				
				threadNumBox.setEditable(true);
				highNumBox.setEditable(true);
				
			}
		});
		panel.add(cancelButton);
		panel.add(startButton);
		return panel;
	}
	
	private JPanel topPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,3,10,4));
		panel.add(threadDirections);
		panel.add(compareBoxDirections);
		panel.add(highNumDirections);
		panel.add(threadNumBox);
		panel.add(compareSpeedupBox);
		panel.add(highNumBox);
		
		threadDirections.setEditable(false);
		highNumDirections.setEditable(false);
		compareBoxDirections.setEditable(false);
		
		return panel;
	}
	
	private JPanel middlePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,1));
		reportBox.setEditable(false);
		reportBox.setLineWrap(true);
		reportBox.setWrapStyleWord(true);
		panel.add(reportBox);
		return panel;
	}
	
	// constructor
	public SlowGUI2()
	{
		super("Prime Finder");
		System.out.println("Running GUI");
		setSize(430, 180);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(bottomPanel(),BorderLayout.SOUTH);
		getContentPane().add(topPanel(),BorderLayout.NORTH);
		getContentPane().add(middlePanel(),BorderLayout.CENTER);
		getRootPane().setDefaultButton(startButton);
		validate();
		setVisible(true);
	}
	
	public static void main(String[] args)
	{
		// run gui
		new SlowGUI2();
	}
}
