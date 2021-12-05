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
				// do the slow step
//				Thread.sleep(NUM_WORKERS*1000);
//				System.out.println("Sleeping "+ NUM_WORKERS + " seconds.");
				
				System.out.println("Starting worker " + worker_num + " in SlowStep");
				
				Set<Integer> numsBelowHalf = new HashSet<Integer>();
				int numToCheck=STARTING_POINT;
				
				while (numToCheck<=STOPPING_POINT)
				{
					System.out.println("Worker " + worker_num +": checking "+numToCheck + " of " + STOPPING_POINT);
//					System.out.println("Cancel " + cancel);
//					System.out.println("size " + primes.size());

					// cancel if supposed to
					if (needToCancel(cancel,worker_num)) break;
					System.out.println("Worker " + worker_num + " past break 1, "+numToCheck);
					
					// guilty until proven innocent
					boolean numIsPrime = true;
					
					// only need to divide by numbers less than half the size of numToCheck
					int halfWayPoint = numToCheck/2;
					for (int n=lastNumAdded+1; n<=halfWayPoint; n++)
					{
						numsBelowHalf.add(n);
					}
					lastNumAdded = halfWayPoint;
//					System.out.println("Worker " + worker_num + " looking for " + numToCheck);					
					
					// if not every number between 2 and 1/2*numToCheck has been checked for prime, wait till present
					while(true)
					{
//						System.out.println("1st check"+nums_checked.containsAll(numsBelowHalf));
						if (nums_checked.containsAll(numsBelowHalf)) break;
//						System.out.println("Worker "+ worker_num + "past 1st break for " + numToCheck);
						if (needToCancel(cancel,worker_num)) break;
						System.out.println("Worker " + worker_num + " past break 2, "+numToCheck);
//						System.out.println("Worker "+ worker_num + "past 2nd break for " + numToCheck);
//						System.out.println("In while loop: Worker " + WORKER_NUM + " Waiting for "+numToCheck + " & " + halfWayPoint + 
//								"\n\tNUMS_CHECKED Worker " + WORKER_NUM + " " + NUMS_CHECKED + 
//								"\n\tnumsBelowHalf Worker " + WORKER_NUM + " " + numsBelowHalf);
						Thread.sleep(100);
					}
					if (needToCancel(cancel,worker_num)) break;
					System.out.println("Worker " + worker_num + " past break 3, "+numToCheck);
					
					// check if number is prime by checking its remainder for all known primes that are less than half of numToCheck
					for (int prime : primes.keySet())
					{
						if (needToCancel(cancel,worker_num)) break;
						if (prime>halfWayPoint) continue;
						
						else if (numToCheck%prime==0)
							{
							numIsPrime = false;
							break;
							}
					}
					System.out.println("Worker " + worker_num + " past break 4, "+numToCheck);
					// if none 
					if (numIsPrime) {
						primes.put(numToCheck,true);
//						System.out.println("Worker " + worker_num + " Found prime: "+ numToCheck);					
					}
					System.out.println("Worker " + worker_num + " past break 5, "+numToCheck);
					
					// mark as checked
					nums_checked.add(numToCheck);
					
					// Each worker jumps to its next number by adding the number of workers
					numToCheck+=NUM_WORKERS;
					
					Thread.sleep(1000l);
					System.out.println("Worker " + worker_num + " past break 1, "+numToCheck);
				}
				System.out.println("Worker " + worker_num + " done");
				
			} catch (Exception e) {
				e.printStackTrace();
				cancel = true;
				System.exit(1);
			} finally {
				System.out.println("Worker " + worker_num + " releasing semaphore, "+NUM_WORKERS);
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
		int[] numbers = {1,4};
		List<Integer> numWorkerList = new ArrayList<Integer>();
		for (int n : numbers) numWorkerList.add(n);
		
		long stop = 10000;
		ConcurrentHashMap<Integer,Boolean> p = new ConcurrentHashMap<Integer,Boolean>();
		Set<Integer> nc = new HashSet<Integer>();
		Map<Integer,Long> times = new HashMap<Integer,Long>();
		
		
		for (int nw : numbers)
		{
			Semaphore semaphore = new Semaphore(nw);
			long startTime = System.currentTimeMillis();
			
			for (int w=0; w<nw; w++)
			{
				semaphore.acquire();
				PrimeFindingWorker pfw = new PrimeFindingWorker(w, nw, p, semaphore, stop, nc);
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
			
		//	System.out.println("All workers acquired");
		//	Thread.sleep(num);
			times.put(nw, endTime-startTime);
		}
		
		// loop through times and report speedup
		System.out.println("num_threads\tTime (sec)");
		long[] timeArray = new long[2];
		int i = 0;
		for (int nw : numbers)
		{
			System.out.println(nw + "\t\t" + times.get(nw));
			timeArray[i] = times.get(nw);
			i++;
		}
		long diff = timeArray[0]-timeArray[1]; 
		System.out.println("difference\t" + diff);
	}

}
