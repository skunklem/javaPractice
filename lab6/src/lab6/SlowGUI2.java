package lab6;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
//	private volatile static int numPrimesFound = 0;
//	private volatile static Set<Integer> primes = Collections.synchronizedSet(new HashSet<Integer>());
//	private volatile static Set<Integer> primes = Collections.synchronizedSet(new ConcurrentHashMap<Integer,Boolean>().keySet());
	private volatile static ConcurrentHashMap<Integer,Boolean> primes = new ConcurrentHashMap<Integer,Boolean>();
	private volatile static int num_threads;
	private volatile static int stopping_point;
	protected volatile static boolean cancel = false;
	private volatile static double timeFor1thread;
	protected volatile static boolean doingSingleThreadComparison = false;
	protected volatile static boolean comparisonIsComplete = false;
//	protected static 
//	public volatile static boolean cancel = false;
//	private final static AtomicBoolean cancel = new AtomicBoolean(false);
	private volatile static boolean doneFindingPrimes;
	private volatile static String report;
	private volatile static String finalReport;
	private volatile static double totalTime = 0;
	private volatile static long startTime = 0;
	private volatile static Set<Integer> nums_checked = Collections.synchronizedSet(new HashSet<>());
	private volatile static ConcurrentHashMap<Integer,Boolean> primes1 = new ConcurrentHashMap<Integer,Boolean>();
	private volatile static Set<Integer> numsChecked1 = Collections.synchronizedSet(new HashSet<>());
//	private volatile static List<PrimeFindingWorker> workerList = new ArrayList<PrimeFindingWorker>();
	
	private volatile static JButton cancelButton = new JButton("Cancel");
	private volatile static JButton startButton = new JButton("Start");
	private volatile static JTextField threadNumBox = new JTextField("5");
	private volatile static JTextField threadDirections = new JTextField("# background threads");
	private volatile static JTextField highNumBox = new JTextField("2000");
	private volatile static JTextField highNumDirections = new JTextField("Pick a high number");
	private volatile static JCheckBox compareSpeedupBox = new JCheckBox();
	private volatile static JTextField compareBoxDirections = new JTextField("Compare with 1 thread");
	private volatile static JTextArea reportBox = new JTextArea("\n\tReset the above parameters or hit Start");
	
	
	private static void writeFinalReport()
	{
		if (compareSpeedupBox.isSelected())
		{
			double ratio = timeFor1thread/totalTime;
			System.out.println(ratio);
			finalReport = "Comparison complete\n"
					+ "\tThreads\t1\t" + num_threads + "\n"
					+ "\tTime (sec)\t" + timeFor1thread + "\t" + totalTime + "\n"
					+ "\tRatio\t" + ratio + " : 1";
		}
		else
		{
			int numsChecked = nums_checked.size() + 1; //(+1 because 1 isn't included in the Set)
			if (doneFindingPrimes) finalReport = "Run completed\n";
			else finalReport = "Run canceled\n";
			finalReport += "\ttime passed: " + totalTime + " seconds\n"
						+ "\tnumbers checked: " + numsChecked + "\n"
						+ "\tprimes found: " + primes.size() + "\n";
		}
		SwingUtilities.invokeLater(new Runnable()
		{						
			@Override
			public void run() {
				// ensure this is the last report sent out
				do {
					System.out.println("HERE");
					if (! reportBox.isEditable()) reportBox.setEditable(true);
					reportBox.setText(finalReport);
					reportBox.setEditable(false);
				} while (! reportBox.getText().equals(finalReport));							
			} 
		});
	}
	
	private static class ReportWriter implements Runnable
	{
		@Override
		public void run()
		{
			while(! cancel)
			{
				try
				{
					long timeSoFar = System.currentTimeMillis()-startTime;
					int numsChecked = nums_checked.size() + 1; //(+1 because 1 isn't included in the Set)
					report = "Running...\n"
							+ "\ttime passed: " + timeSoFar/1000 + " seconds\n"
							+ "\tnumbers checked: " + numsChecked + "\n"
							+ "\tprimes found: " + primes.size() + "\n";
					SwingUtilities.invokeLater(new Runnable()
					{						
						@Override
						public void run() {
							if (reportBox.isEditable()) reportBox.setText(report);
						}
					});
					Thread.sleep(500); // no point in updating report any faster
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
					cancelButton.doClick();
				}
			}
		}
	}
	

	
//	private static class StartSlowStep implements Runnable
//	{		
//		@Override
//		public void run()
//		{
//				try
//				{					
//					System.out.println("New run with Threads: " + num_threads+ "\tHighNum: "+stopping_point);
//					
//					Semaphore semaphore = new Semaphore(num_threads);
//					for (int workerNum=0; workerNum<num_threads; workerNum++)
//					{
//						semaphore.acquire();
//						System.out.println("Thread " + workerNum +" in StartSlowStep");
//						PrimeFindingWorker pfw = new PrimeFindingWorker(workerNum, num_threads, primes, semaphore, stopping_point, nums_checked);
//						new Thread(pfw).start();						
//					}
//					
//					new Thread(new ReportWriter()).start();
//					
//					int returnedWorkers = 0;
//					
//					while (returnedWorkers < num_threads)
//					{
//						semaphore.acquire();
//						returnedWorkers++;
//					}
//					long endTime = System.currentTimeMillis();
//					totalTime = (endTime-startTime)/1000.;
//										
//					if (compareSpeedupBox.isSelected())
//					{
//						timeFor1thread = 0.;
//						startTime = System.currentTimeMillis();
//						Semaphore semaphore1 = new Semaphore(1);
//						System.out.println("Avail perm " + semaphore1.availablePermits());
//
//						System.out.println("New single threaded run");
//						new Thread(new PrimeFindingWorker(0,1,primes1,semaphore1,stopping_point,numsChecked1)).start();
////						semaphore1.acquire();
//						returnedWorkers = 0;
//						System.out.println("Avail perm " + semaphore1.availablePermits());
//						
//						while (returnedWorkers < 1)
//						{
//							semaphore1.acquire();
//							returnedWorkers++;
//						}
//						timeFor1thread = (System.currentTimeMillis()-startTime)/1000;
//						System.out.println(System.currentTimeMillis() + " " + startTime);
//						System.out.println(timeFor1thread + " seconds");
//					}					
//					doneFindingPrimes = true;
//					System.out.println("All workers done");
//					cancelButton.doClick();
//
//					// make sure this report comes after other report
//					Thread.sleep(200);
//					writeFinalReport();
//					
//					// sanity checks
//					if ((compareSpeedupBox.isSelected()))
//					{
////						System.out.println("Primes found: " + primes1.size() +" "+ primes.size());
//						if (! (primes1.size()==primes.size())) System.out.println("Different numbers of primes found. One run may not have completed");
//					}
//					else System.out.println("Not a comparison run"); //remove
//					
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//					cancelButton.doClick();
//				}
//		}
//	}

	private static class StartSlowStep implements Runnable
	{		
		@Override
		public void run()
		{
			try
			{
				int[] numbers = {0,0};
				if (compareSpeedupBox.isSelected()) numbers[0] = 1;
				numbers[1] = num_threads;
				for (int numberOfWorkers : numbers)
				{
					if (numberOfWorkers == 0) continue; // only do runs that have more than 1
					System.out.println("New run with Threads: " + numberOfWorkers+ "\tHighNum: "+stopping_point);
					
					Semaphore semaphore = new Semaphore(numberOfWorkers);
					long startTime = System.currentTimeMillis();
					for (int workerNum=0; workerNum<numberOfWorkers; workerNum++)
					{
						semaphore.acquire();
						System.out.println("Thread " + workerNum +" in StartSlowStep");
						if (numberOfWorkers == 1)
						{
							new Thread(new PrimeFindingWorker(0,1,primes1,semaphore,stopping_point,numsChecked1)).start();
						}
						else
						{
							new Thread(new PrimeFindingWorker(workerNum, numberOfWorkers, primes, semaphore, stopping_point, nums_checked)).start();
						}											
					}
					
					new Thread(new ReportWriter()).start();
					
					int returnedWorkers = 0;
					
					while (returnedWorkers < numberOfWorkers)
					{
						semaphore.acquire();
						returnedWorkers++;
					}
					long endTime = System.currentTimeMillis();
					if ((numberOfWorkers == 1) & (timeFor1thread == 0))
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
				}
				doneFindingPrimes = true;
				System.out.println("All workers done");
				cancelButton.doClick();

				// make sure this report comes after other report
				Thread.sleep(200);
				writeFinalReport();
				
				// sanity checks
				if ((compareSpeedupBox.isSelected()))
				{
//					System.out.println("Primes found: " + primes1.size() +" "+ primes.size());
					if (! (primes1.size()==primes.size())) System.out.println("Different numbers of primes found. One run may not have completed");
				}
				else System.out.println("Not a comparison run"); //remove
			}
			catch (Exception e)
			{
				e.printStackTrace();
				cancelButton.doClick();
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
//				cancel.set(false);
				PrimeFindingWorker.cancel = false;
//				comparisonIsComplete = false;
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
//				int numThreads = Integer.valueOf(threadNumBox.getText());
				num_threads = Integer.valueOf(threadNumBox.getText());
				System.out.println(Integer.valueOf(threadNumBox.getText()));
//				workerList = new ArrayList<PrimeFindingWorker>();
				try
				{
					startTime = System.currentTimeMillis();
					new Thread(new StartSlowStep()).start();
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
				PrimeFindingWorker.cancel = true;
				try
				{
					reportBox.setText(report);
				} catch (Exception e1)
				{
					e1.printStackTrace();
				}
				
				// make stoppingPointBox and threadCountBox editable 
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
//		JTextField threadDirections = new JTextField("Desired number of threads");
//		JTextField highNumDirections = new JTextField("Pick a high number");
//		JTextField threadNumBox = new JTextField("1");
//		threadNumBox.setText("1");
//		JTextField highNumBox = new JTextField("200");
//		highNumBox.setText("200");
		
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
