//package lab6;
//
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//import javax.swing.JTextPane;
//import javax.swing.SwingUtilities;
//import javax.swing.text.SimpleAttributeSet;
//import javax.swing.text.StyleConstants;
//import javax.swing.text.StyledDocument;
//
//import lab6.SlowStep.PrimeFindingWorker;
//
//import java.awt.BorderLayout;
//import java.awt.GridLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.*;
//
//
///*
// * Some resources
// * https://www.programcreek.com/java-api-examples/?api=weka.core.converters.CSVLoader
// * https://medium.com/@rahulvaish/linear-regression-prediction-weka-way-3fdc1643e1b6
// * https://weka.sourceforge.io/doc.stable/weka/classifiers/functions/LinearRegression.html#LinearRegression--
// */
//
//public class SlowGUI extends JFrame
//{
//	
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -1709315542670506804L;
//	private volatile static int numPrimesFound = 0;
//	private volatile static Set<Integer> primes = Collections.synchronizedSet(new HashSet<>());
//	private volatile static int num_threads;
//	private volatile static int stopping_point;
//	private volatile static boolean cancel = false;
//	private volatile static boolean doneFindingPrimes;
//	private volatile static String report = "";
//	private volatile static double totalTime = 0;
//	private volatile static long startTime = 0;
//	private volatile static Set<Integer> nums_checked = Collections.synchronizedSet(new HashSet<>());
//	
//	private volatile static JTextField threadNumBox = new JTextField("5");
//	private volatile static JTextField highNumBox = new JTextField("20000");
//	private volatile static JButton cancelButton = new JButton("Cancel");
//	private volatile static JButton startButton = new JButton("Start");
//	private volatile static JTextArea reportBox = new JTextArea("\n\tReset the above parameters or hit Start");
//	
//	private static class ReportWriter implements Runnable
//	{
//		@Override
//		public void run()
//		{
//			while(! cancel)
//			{
//				try
//				{
//					Thread.sleep(500);
//					numPrimesFound = primes.size();
//					long timeSoFar = System.currentTimeMillis()-startTime; 
//					report = "Running...\n"
//							+ "\tprimes found: " + numPrimesFound + "\n"
//							+ "\ttime passed: " + timeSoFar;
//					SwingUtilities.invokeAndWait(new Runnable()
//					{						
//						@Override
//						public void run() {
//							reportBox.setText(report);							
//						}
//					});
//					
//				} 
//				catch (Exception e) 
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					cancelButton.doClick();
////					cancel = true;
////					System.exit(1);
//				}
//			}
//		}
//	}
//	
//	private static class StartSlowStep implements Runnable
//	{
////		private volatile int numThreads;
//////		private volatile boolean cancel;
////		private volatile long STOPPING_POINT;
////		private volatile static Set<Integer> primes;
//
////		private StartSlowStep(int numThreads,long stoppingPoint,boolean toCancel,Set<Integer> primes) throws Exception
////		private StartSlowStep(int numThreads,long stoppingPoint,Set<Integer> primes) throws Exception
////		private StartSlowStep() throws Exception
////		{
//////			this.cancel = toCancel;
////			this.numThreads = numThreads;
////			this.STOPPING_POINT = stoppingPoint;
////			StartSlowStep.primes = primes;
////		}
//		
//		@Override
//		public void run()
//		{
//			while(! cancel)
//			{
//				try
//				{
//	//				int[] numbers = {1,4};
//	//				List<Integer> numWorkerList = new ArrayList<Integer>();
//	//				for (int n : numbers) numWorkerList.add(n);
//	//				
//	////				int stop = 10000;
//	//				Set<Integer> primes = ConcurrentHashMap.newKeySet();
////					Set<Integer> numsChecked = new HashSet<Integer>();
//	//				Map<Integer,Long> times = new HashMap<Integer,Long>();
//					
//					Semaphore semaphore = new Semaphore(num_threads);
////					startTime = System.currentTimeMillis();
//					
//					for (int workerNum=0; workerNum<num_threads; workerNum++)
//					{
//						semaphore.acquire();
//	//					PrimeFindingWorker pfw = new PrimeFindingWorker(workerNum, numThreads, primes, semaphore, cancel, STOPPING_POINT, numsChecked);
////						PrimeFindingWorker pfw = new PrimeFindingWorker(workerNum, num_threads, primes, semaphore, STOPPING_POINT, numsChecked);
//						PrimeFindingWorker pfw = new PrimeFindingWorker(semaphore);
//						new Thread(pfw).start();
//						new Thread(new ReportWriter()).start();
//					}
//					
//					int returnedWorkers = 0;
//					
//					while (returnedWorkers < num_threads)
//					{
//						if (cancel) break;
//						semaphore.acquire();
//						returnedWorkers++;
//					}
//					
//					if (cancel) break;
//					doneFindingPrimes = true;
//					
////					long endTime = System.currentTimeMillis();
////					totalTime = endTime-startTime;
////					System.out.println(startTime);
////					System.out.println(totalTime);
////					int numFound = primes.size();
//					
////					System.out.println("Found "+ numPrimesFound + " primes in "+ totalTime + " ms.");
//					
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					cancelButton.doClick();
////					cancel = true;
////					System.exit(1);
//				}
//			}
//		}
//	}
//	
//	// gui prep
//	private JPanel bottomPanel()
//	{
//		JPanel panel = new JPanel();
//		panel.setLayout(new GridLayout(0,2,50,20));
////		JButton startButton = new JButton("Start");
////		JButton cancelButton = new JButton("Cancel");
//		cancelButton.setEnabled(false);
//		startButton.addActionListener(new ActionListener()
//		{
//			@Override
//			public void actionPerformed(ActionEvent e)
//			{
//				cancel = false;
//				startButton.setEnabled(false);
//				cancelButton.setEnabled(true);
//				System.out.println("111");
//				doneFindingPrimes = false;
//				threadNumBox.setEditable(false);
//				highNumBox.setEditable(false);
//				System.out.println("222");
////				int numThreads = Integer.valueOf(threadNumBox.getText());
//				num_threads = Integer.valueOf(threadNumBox.getText());
//				stopping_point = Integer.valueOf(highNumBox.getText());
//				System.out.println("333");
//				try
//				{
//					startTime = System.currentTimeMillis();
////					new Thread(new StartSlowStep(num_threads,stopping_point,cancel,primes)).start();
////					new Thread(new StartSlowStep(num_threads,stopping_point,primes)).start();
//					new Thread(new StartSlowStep()).start();
//				} 
//				catch (Exception e1) {
//					e1.printStackTrace();
//					System.exit(1);
//				}
//				System.out.println("444");
////				new Thread(new Reporter(primes)).start();
//
//				
//			}
//		});
//		cancelButton.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// cancel and reset gui
////				System.out.println("Canceling");
//				startButton.setEnabled(true);
//				cancelButton.setEnabled(false);
//				cancel = true;
//				
//				if (totalTime == 0)
//				{
//					long endTime = System.currentTimeMillis();
//					totalTime = endTime-startTime;
//				}
//				
//				// post final report
//				if (doneFindingPrimes) report = "Finished finding primes below " + highNumBox.getText() + "\n";
//				else report = "Did not finish run\n";
//				numPrimesFound = primes.size();
//				report += "Primes found: " + numPrimesFound +"\n"
//						+ "Time passed: " + totalTime/1000. + " seconds\n";
//				
//				try
//				{
//					reportBox.setText(report);
////					SwingUtilities.invokeAndWait(new Runnable()
////					{						
////						@Override
////						public void run() {
////							reportBox.setText(report);							
////						}
////					});
//				} catch (Exception e1)
//				{
//					e1.printStackTrace();
//					System.exit(1);
//				}
//				
//				// make stoppingPointBox and threadCountBox editable 
//				threadNumBox.setEditable(true);
//				highNumBox.setEditable(true);
//				startTime = 0;
//				totalTime = 0;
//			}
//		});
//		panel.add(cancelButton);
//		panel.add(startButton);
//		return panel;
//	}
//	
//	private JPanel topPanel()
//	{
//		JPanel panel = new JPanel();
//		panel.setLayout(new GridLayout(0,2,80,4));
//		JTextField threadDirections = new JTextField("Desired number of threads");
//		JTextField highNumDirections = new JTextField("Pick a high number");
//		JTextField threadNumBox = new JTextField("5");
//		JTextField highNumBox = new JTextField("20000");
//		
//		threadDirections.setEditable(false);
//		highNumDirections.setEditable(false);
//		
//		panel.add(threadDirections);
//		panel.add(highNumDirections);
//		panel.add(threadNumBox);
//		panel.add(highNumBox);
//		return panel;
//	}
//	
//	private JPanel middlePanel()
//	{
//		JPanel panel = new JPanel();
//		panel.setLayout(new GridLayout(1,1));
////		JTextArea reportBox = new JTextArea("\n\tReset the above parameters or hit Start");
//		reportBox.setEditable(false);
//		reportBox.setLineWrap(true);
//		reportBox.setWrapStyleWord(true);
//		panel.add(reportBox);
//		return panel;
//	}
//	
//	// constructor
//	public SlowGUI()
//	{
//		super("Prime Finder");
//		System.out.println("Running GUI");
//		setSize(400, 180);
//		setLocationRelativeTo(null);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		getContentPane().setLayout(new BorderLayout());
//		getContentPane().add(bottomPanel(),BorderLayout.SOUTH);
//		getContentPane().add(topPanel(),BorderLayout.NORTH);
//		getContentPane().add(middlePanel(),BorderLayout.CENTER);
//		getRootPane().setDefaultButton(startButton);
//		validate();
//		setVisible(true);
//	}
//	
//	
//	// slowStep class
//	public static class PrimeFindingWorker implements Runnable
//	{
//		private volatile static int NUM_WORKERS;
//		private volatile int worker_num;
////		private volatile static Set<Integer> primes;
//		private final Semaphore semaphore;
////		private volatile boolean cancel;
//		private final int STARTING_POINT;
//		private volatile static long STOPPING_POINT;
////		private volatile static Set<Integer> nums_checked;
//		private int lastNumAdded = 1;
//		
////		public PrimeFindingWorker(int worker_num, int NUM_WORKERS, Set<Integer> primes,Semaphore semaphore, boolean cancel, long STOPPING_POINT, Set<Integer> nums_checked)
////		public PrimeFindingWorker(int worker_num, int NUM_WORKERS, Set<Integer> primes,Semaphore semaphore, long STOPPING_POINT, Set<Integer> nums_checked)
//		public PrimeFindingWorker(Semaphore semaphore)
//		{
//			this.worker_num = worker_num;
////			PrimeFindingWork
//			this.semaphore = semaphore;
////			this.cancel = cancel;
//			PrimeFindingWorker.STOPPING_POINT = STOPPING_POINT;
//			this.STARTING_POINT = 2 + worker_num;
////			PrimeFindingWorker.nums_checked = nums_checked;
//		}
//				
//		@Override
//		public void run() 
//		{
//			try {				
//				System.out.println("Starting worker " + worker_num);
//				
//				Set<Integer> numsBelowHalf = new HashSet<Integer>();
//				int numToCheck=STARTING_POINT;
//				
//				while (numToCheck<STOPPING_POINT)
//				{
////					System.out.println("Worker " + WORKER_NUM +": checking "+numToCheck);
//
//					// cancel if supposed to
//					if (cancel) {
//						System.out.println("Worker "+ worker_num + " canceled");
//						break;
//					}
//					
//					// guilty until proven innocent
//					boolean numIsPrime = true;
//					
//					// only need to divide by numbers less than half the size of numToCheck
//					int halfWayPoint = numToCheck/2;
//					for (int n=lastNumAdded+1; n<=halfWayPoint; n++)
//					{
//						numsBelowHalf.add(n);
//					}
//					lastNumAdded = halfWayPoint;
////					System.out.println("Worker " + worker_num + " looking for " + numToCheck);					
//					
//					// if not every number between 2 and 1/2*numToCheck has been checked for prime, wait till present
//					while(true)
//					{
////						System.out.println("Worker " + worker_num + ": 1st check: "+nums_checked.containsAll(numsBelowHalf));
//						if (nums_checked.containsAll(numsBelowHalf)) break;
////						System.out.println("Worker "+ worker_num + "past 1st break for " + numToCheck);
//						if (cancel) {
//							System.out.println("Worker "+ worker_num + " canceled");
//							break;}
////						System.out.println("Worker "+ worker_num + "past 2nd break for " + numToCheck);
////						System.out.println("In while loop: Worker " + WORKER_NUM + " Waiting for "+numToCheck + " & " + halfWayPoint + 
////								"\n\tNUMS_CHECKED Worker " + WORKER_NUM + " " + NUMS_CHECKED + 
////								"\n\tnumsBelowHalf Worker " + WORKER_NUM + " " + numsBelowHalf);
//						Thread.sleep(100);
//					}
//					
//					if (cancel) break;
//					
//					// check if number is prime by checking its remainder for all known primes that are less than half of numToCheck
//					for (int prime : primes)
//					{
//						if (prime>halfWayPoint) continue;
//						
//						else if (numToCheck%prime==0)
//							{
//							numIsPrime = false;
//							break;
//							}
//					}
//					// if none 
//					if (numIsPrime) {
//						primes.add(numToCheck);
//						//System.out.println("Worker " + worker_num + " Found prime: "+ numToCheck);					
//					}
//					
//					// mark as checked
//					nums_checked.add(numToCheck);
//					
//					// Each worker jumps to its next number by adding the number of workers
//					numToCheck+=NUM_WORKERS;
//				}
//				
//				// release semaphore if step completes
//				semaphore.release();
//				System.out.println("Worker " + worker_num + " done");
//				
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				cancel = true;
//			} finally 
//			{	
//				cancelButton.doClick();
////				try
////				{
////					SwingUtilities.invokeAndWait(new Runnable()
////					{						
////						@Override
////						public void run() {
////							cancelButton.doClick();							
////						}
////					});
////				} catch (Exception e)
////				{
////					// TODO Auto-generated catch block
////					e.printStackTrace();	
////				}
//			}
//		}
//	}
//	
//
//	
//
//	
//	
//
//	public static void main(String[] args)
//	{
//		// run gui
//		new SlowGUI();
//	}
//
//
//	
//}