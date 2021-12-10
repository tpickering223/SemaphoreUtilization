import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
// import java.util.concurrent.Semaphore;

public class RaceConditionDemo {
	static final int BUFFER_SIZE = 100;
	static final int MAX_ITERATIONS = 50;
	static Random rand = new Random();
	static boolean active = true;
	static int[] buffer = new int[BUFFER_SIZE];
	static int available_index = 0;
	static int available_out = 0;
	static Scanner kb = new Scanner(System.in);
//	static Semaphore emptySlots = new Semaphore(BUFFER_SIZE);
//	static Semaphore fullSlots = new Semaphore(0);
	static boolean consumerisAlive = true;
	
	public static void main(String[] args) {
		System.out.println("Starting threads...if successful and no race conditions are being hit, the user may enter \"stop\" to end the program");
		
		Thread producerThread = new Thread(() -> producer());
		
		Thread consumerThread = new Thread(() -> consumer());
		
	
		
		try {
			producerThread.start();
			consumerThread.start();
	
			
			producerThread.join();
			consumerThread.join();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void producer() {
		int count = 0;
		while(active && consumerisAlive && count != MAX_ITERATIONS) {
			int k1 = rand.nextInt() % BUFFER_SIZE;
			if(k1 < 0 && k1 != Integer.MIN_VALUE) {
				k1 = Math.abs(k1);
			}
			if(k1 == Integer.MIN_VALUE) {
				k1 = Math.abs(k1 + 1);
			}
			System.out.println("Producer iterations: " + k1);

			for(int i = 0; i <= k1; i++) {
					buffer[(available_index) % BUFFER_SIZE] += 1;
					available_index = (available_index + 1) % BUFFER_SIZE;	

			}
				
			
			
			System.out.println("Producer at iteration: " + count);
			System.out.println("Producer at index: " + available_index);
			//System.out.println("Printing Producer results:");
			System.out.println(Arrays.toString(buffer));
			count++;
			// mutex.unlock();
			// System.out.println("Producer unlocked mutex");
			
			
			try {
				Thread.sleep((long)(rand.nextInt(1000) + 500));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void consumer() {
		int count = 0;
		while(active && count != MAX_ITERATIONS) {
			//System.out.println("In consumer thread");

			try {
				Thread.sleep((long)(rand.nextInt(1000) + 100));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			
			int k2 = rand.nextInt() % BUFFER_SIZE;
			if(k2 < 0 && k2 != Integer.MIN_VALUE) {
				k2 = Math.abs(k2);
			}
			if(k2 == Integer.MIN_VALUE) {
				k2 = Math.abs(k2 + 1);
			}
			// mutex.lock();
			// System.out.println("Consumer thread obtained mutex lock.");
			for(int i = 0; i <= k2; i++) {
					int data = buffer[(available_out) % BUFFER_SIZE];
					
					buffer[(available_out) % BUFFER_SIZE] = 0;
					if(data > 1) {
						System.out.println("Buffer value at index " + ((available_out + k2) % BUFFER_SIZE) + ": " + data);
						consumerisAlive = false;
						throw new RuntimeException("Race condition detected in consumer thread.\nData\nOut Index: " + available_out + "\nFor index: " + i + "\nBuffer Index: " + (available_out % BUFFER_SIZE));
					}
					available_out = (available_out + 1) % BUFFER_SIZE;
			}
			
			count++;
			System.out.println("Consumer at iteration: " + count);
			// mutex.unlock();
			// System.out.println("Consumer unlocked mutex");
		}
		System.out.println("----------CONSUMER THREAD FINISHED--------");
	}
	
	
}
