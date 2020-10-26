package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.Pair;
import bgu.spl.mics.application.messages.TakeBookFromInventoryEvent;
import bgu.spl.mics.application.messages.TermBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory;
	private final CountDownLatch latch;
	private final CountDownLatch latch2;

	public InventoryService(CountDownLatch latch,CountDownLatch latch2) {
		super("InventoryService");
		this.inventory = Inventory.getInstance();
		this.latch = latch;
		this.latch2 = latch2;
	}	
	

	
	@Override
	protected void initialize() {	
		
		this.subscribeBroadcast(TermBroadcast.class, (event)->{
			latch2.countDown();
			this.terminate();
			
		});
		
		this.subscribeEvent(TakeBookFromInventoryEvent.class, (event)->{
			
			        int price = inventory.checkAvailabiltyAndGetPrice(event.getTitle());
			        if(price>-1){
			 
			        	if(event.getAmount()>=price){
				        	OrderResult order = inventory.take(event.getTitle());
				        	Pair<OrderResult,Integer> p = new Pair<OrderResult,Integer>(order, price);
							this.complete(event, p);
				        }
			        	else { Pair<OrderResult,Integer> p = new Pair<OrderResult,Integer>(OrderResult.NOT_IN_STOCK, -1);
						this.complete(event, p);
			        	}
			        }
			        
		});
		latch.countDown();
	}

	
}
