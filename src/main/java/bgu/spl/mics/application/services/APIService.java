package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TermBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private String seller;
	private Customer cust;
	private OrderSchedule[] OrderSchedules; 
	private int currentTick;
	private int k=0;
	private final CountDownLatch latch;
	private final CountDownLatch latch2;
	
	
	public APIService(OrderSchedule[] OrderSchedules, Customer cust,String seller, CountDownLatch latch,CountDownLatch latch2) {
		super("APIService");
		this.OrderSchedules = OrderSchedules;
		this.cust = cust;
		this.seller = seller; 
		this.latch = latch;
		this.latch2 = latch2;
		
	}

	@Override
	protected void initialize() {
		
		OrderSchedule tmp;
		for (int i=0; i<OrderSchedules.length-1; i++){
			for (int j=0; j<OrderSchedules.length-1; j++){
				if (OrderSchedules[j].getTick() > OrderSchedules[j+1].getTick()) {
				tmp = OrderSchedules[j];
				OrderSchedules[j] = OrderSchedules[j+1];
				OrderSchedules[j+1] = tmp;

				}
			}
		}
		this.subscribeBroadcast(TermBroadcast.class, (event)->{
			latch2.countDown();
			this.terminate();
			
		});
		
		this.subscribeBroadcast(TickBroadcast.class, (event)->{
			currentTick = event.getTick();	
			while(k<OrderSchedules.length&&currentTick == OrderSchedules[k].getTick()){
			  this.sendEvent(new BookOrderEvent(cust, OrderSchedules[k].getBookTitle(), 1, seller, currentTick ));
				k++;
				
			}
				
		});
		latch.countDown();
		
		
	}

}
