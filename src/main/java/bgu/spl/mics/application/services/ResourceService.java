package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TakeVehicleEvent;
import bgu.spl.mics.application.messages.TermBroadcast;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	
	private ResourcesHolder rH;
	private final CountDownLatch latch;
	private final CountDownLatch latch2;
	
	public ResourceService(CountDownLatch latch,CountDownLatch latch2) {
		super("ResourceService");
	
		rH = ResourcesHolder.getInstance();
		this.latch = latch;
		this.latch2 = latch2;
	}

	
	@Override
	protected void initialize() {
		this.subscribeBroadcast(TermBroadcast.class, (event)->{
			latch2.countDown();
			this.terminate();
		});
		this.subscribeEvent(ReleaseVehicleEvent.class, (event)->{
			rH = ResourcesHolder.getInstance();
			rH.releaseVehicle(event.getVehicle());
		});
		this.subscribeEvent(TakeVehicleEvent.class, (event)->{
			
			try {
				Future<?> f = rH.acquireVehicle();
				this.complete(event, f);
			} catch (InterruptedException e) {
				
			}
});
		latch.countDown();
		
	}

}
