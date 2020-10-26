package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TakeVehicleEvent;
import bgu.spl.mics.application.messages.TermBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.NumOfFuture;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {


	private final CountDownLatch latch;
	private final CountDownLatch latch2;
	private int currentTick = 0; 
	private NumOfFuture numOfFutures = NumOfFuture.getInstance();

	public LogisticsService(CountDownLatch latch,CountDownLatch latch2) {
		super("LogisticsService");
		this.latch = latch;
		this.latch2 = latch2;
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TermBroadcast.class, (event)->{
			latch2.countDown();
			this.terminate();
			LinkedList<Future<Object>> l = numOfFutures.getListOfFutures();
			for(int i=0; i<l.size();i++){
				if(!l.get(i).isDone()){
					l.get(i).resolve(null);
				}
			}

		});
		this.subscribeBroadcast(TickBroadcast.class, (event)->{
			currentTick = event.getTick();
		});
		this.subscribeEvent(DeliveryEvent.class, (event)->{

			Future<Object> f = sendEvent(new TakeVehicleEvent());
			synchronized(f){
				while(!f.isDone()){
					try {
						f.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			Future<Object> fut=null;
			try {
				fut = (Future<Object>) f.get();
			} catch (InterruptedException e1) {
			}

			numOfFutures.addFuture(fut);
			try {
				if(fut.get()!=null)
					((DeliveryVehicle)fut.get()).deliver(event.getAddress(), event.getDis());
				
			} catch (InterruptedException e1) {
			}

			try {
				this.sendEvent(new ReleaseVehicleEvent((DeliveryVehicle)fut.get()));
			} catch (InterruptedException e) {
			}
		});
		
		latch.countDown();
	}

}
