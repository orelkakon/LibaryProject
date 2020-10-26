package bgu.spl.mics.application.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TermBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int tick = 1;
	private int speed;
	private int duration;
	private Object lock;
	private final CountDownLatch latch2;
	
	
	public TimeService(int speed, int duration,CountDownLatch latch) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		this.lock = new Object();
		this.latch2 = latch;
	}

	@Override
	protected void initialize() {
		
		this.subscribeBroadcast(TermBroadcast.class, (event)->{
			latch2.countDown();
			this.terminate();
		
		});
		TimerTask task = new TimerTask(){
			public void run(){
				if(tick<=duration){
					sendBroadcast(new TickBroadcast(tick));
					tick++;
				}
				else{
					synchronized(lock){
						sendBroadcast((Broadcast) new TermBroadcast());
						lock.notifyAll();
						
					}
				}
			}
		};
			
		Timer t = new Timer();
		t.scheduleAtFixedRate(task, 0, speed);
		synchronized(lock){
			while(tick<duration)	
				try{
					lock.wait();
				}catch(InterruptedException e){}
			t.cancel();
		}
	}

}
