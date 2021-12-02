//package lab6;
//
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.FutureTask;
//
//public class TempCodeDump {
//	
//	
//	// instantiate variables
//	private volatile int numPrimesFound = 0;
//	
//	
//	// create slowStep
//	private static FutureTask<List<Integer>> primeFinder = new FutureTask<>(
//			new Callable<List<Integer>>() {
//				
//				private static final int howHigh;
//				
//				public primeFinder(int howHigh) {
//					this.howHigh = howHigh;
//				}
//				
//				@Override
//				public List<Integer> call() throws Exception{
//					System.out.println("Starting primeFinder");
//					
//					
//				}
//				
//			});
//	{
//		private static final int num;
//
//		public slowStep(int num) {
//			this.num = num;
//		}
//		public slowStep() {
//			this.num = 1000;
//		}
//	}
//
//}





//	//NOT AS CLASS - REMOVE?
//	private void startSlowStep(int numThreads,long stoppingPoint,boolean toCancel) throws Exception
//	{
//		int[] numbers = {1,4};
//		List<Integer> numWorkerList = new ArrayList<Integer>();
//		for (int n : numbers) numWorkerList.add(n);
//		
//		int stop = 10000;
//		Set<Integer> primes = ConcurrentHashMap.newKeySet();
//		Set<Integer> numsChecked = new HashSet<Integer>();
////		Map<Integer,Long> times = new HashMap<Integer,Long>();
//		
//		Semaphore semaphore = new Semaphore(numThreads);
//		long startTime = System.currentTimeMillis();
//		
//		for (int workerNum=0; workerNum<numThreads; workerNum++)
//		{
//			semaphore.acquire();
////			SlowStep ss = new SlowStep(workerNum, numThreads, primes, semaphore, toCancel, stop, numsChecked);
////			new Thread(ss).start();
//			PrimeFindingWorker pfw = ss.PrimeFindingWorker(workerNum, numThreads, primes, semaphore, toCancel, stoppingPoint, numsChecked);
//			new Thread(pfw).start();
//		}
//		
//		int returnedWorkers = 0;
//		
//		while (returnedWorkers < numThreads)
//		{
//			semaphore.acquire();
//			returnedWorkers++;
//		}
//		long endTime = System.currentTimeMillis();
//		
//		int numFound = p.size();
//		
//		long totalTime = endTime-startTime;
//	}