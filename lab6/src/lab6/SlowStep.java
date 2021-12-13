package lab6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class SlowStep
{
	private static boolean needToCancel(boolean cancel, int workerNum)
	{
		if (cancel) {
			System.out.println("Worker "+ workerNum + " canceled");
			return true;}
		else return false;
	}
	
	public static class PrimeFindingWorker implements Runnable
	{
		private volatile static int NUM_WORKERS;
		private volatile int worker_num;
		private volatile static ConcurrentHashMap<Integer,Boolean> primes;
		private final Semaphore semaphore;
		protected volatile static boolean cancel = false;
		private final int STARTING_POINT;
		private volatile static long STOPPING_POINT;
		private volatile static Set<Integer> nums_checked;
		private int lastNumAdded = 1;
		private int iterationNum = 0;
		
		public PrimeFindingWorker(int worker_num, int NUM_WORKERS, ConcurrentHashMap<Integer,Boolean> primes,Semaphore semaphore, long STOPPING_POINT, Set<Integer> nums_checked) throws InterruptedException
		{
			this.worker_num = worker_num;
			PrimeFindingWorker.NUM_WORKERS = NUM_WORKERS;
			PrimeFindingWorker.primes = primes;
			this.semaphore = semaphore;
			PrimeFindingWorker.STOPPING_POINT = STOPPING_POINT;
			this.STARTING_POINT = 2 + worker_num;
			PrimeFindingWorker.nums_checked = nums_checked;
		}
				
		@Override
		public void run() {			
			try {
				System.out.println("Starting worker " + worker_num + " in SlowStep. Going to " + STOPPING_POINT);
				
				Set<Integer> numsBelowHalf = new HashSet<Integer>();
				PrimeFindingWorker.cancel = false; // reset
				int numToCheck=STARTING_POINT;
				
				while ((numToCheck<=STOPPING_POINT) & (! needToCancel(cancel,worker_num)))
				{
//					System.out.println("Worker " + worker_num +": checking "+numToCheck + " of " + STOPPING_POINT);
					
					// only need to divide by numbers less than half the size of numToCheck
					int halfWayPoint = numToCheck/2;
					for (int n=lastNumAdded+1; n<=halfWayPoint; n++)
					{
						numsBelowHalf.add(n);
					}
					lastNumAdded = halfWayPoint;
					
					// if not every number between 2 and 1/2*numToCheck has been checked for prime (by any worker), wait till present in nums_checked
					while((! nums_checked.containsAll(numsBelowHalf)) & (! needToCancel(cancel,worker_num)))
					{
//						System.out.println("Worker " + worker_num + " waiting for other workers to catch up");
						Thread.sleep(1);
					}
					
					// guilty until proven innocent
					boolean numIsPrime = true;
					
					// check if number is prime by checking its remainder for all known primes that are less than half of numToCheck
					for (int prime : primes.keySet())
					{
						// skip math on too big primes (might be no faster...)
						if (prime>halfWayPoint) continue;
						// prime check -- break if any factors found
						else if (numToCheck%prime==0) {numIsPrime = false; break;}
					}
					// add prime number to set of primes
					if (numIsPrime) {primes.put(numToCheck,true);}
					
					// mark as checked
					nums_checked.add(numToCheck);
					
					// Iterate by adding the number of workers
					numToCheck+=NUM_WORKERS;
					
//					System.out.println("Worker " + worker_num + " finished iteration "+iterationNum);
					iterationNum++;
				}				
			} catch (Exception e) {
				e.printStackTrace();
				cancel = true;
				System.exit(1);
			} finally {
				System.out.println("Worker " + worker_num + " releasing semaphore, numWorkers: "+NUM_WORKERS);
				semaphore.release();
			}
		}
		
		protected void endWorker()
		{
			cancel = true;
		}
	}
	

	public static void main(String[] args) throws Exception
	{
		
	//	int nw = 10;
		int[] numbers = {1,5};
		List<Integer> numWorkerList = new ArrayList<Integer>();
		for (int n : numbers) numWorkerList.add(n);
		
		long stop = 20000;
		ConcurrentHashMap<Integer,Boolean> p = new ConcurrentHashMap<Integer,Boolean>();
		Set<Integer> numChecked = new HashSet<Integer>();
		Map<Integer,Double> times = new HashMap<Integer,Double>();
		
		
		for (int nw : numbers)
		{
			Semaphore semaphore = new Semaphore(nw);
			long startTime = System.currentTimeMillis();
			
			System.out.println("New run, " + nw + " thread(s)");
			
			for (int w=0; w<nw; w++)
			{
				semaphore.acquire();
				PrimeFindingWorker pfw = new PrimeFindingWorker(w, nw, p, semaphore, stop, numChecked);
				new Thread(pfw).start();
			}
			
			int returnedWorkers = 0;
			
			while (returnedWorkers < nw)
			{
				semaphore.acquire();
				returnedWorkers++;
			}
			long endTime = System.currentTimeMillis();
			
			int numFound = p.size();
			System.out.println("Found " + numFound + " primes");
			
			times.put(nw, (endTime-startTime)/1000.);
		}
		
		// loop through times and report speedup
		System.out.println("num_threads\tTime (sec)");
		double[] timeArray = new double[2];
		int i = 0;
		for (int nw : numbers)
		{
			System.out.println(nw + "\t\t" + times.get(nw));
			timeArray[i++] = times.get(nw);
		}
		double ratio = timeArray[0]/timeArray[1]; 
		System.out.println("ratio (single:multi):\t" + ratio);
	}

}
