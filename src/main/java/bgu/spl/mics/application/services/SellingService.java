package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TermBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TakeBookFromInventoryEvent;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.NumOfTakeBookFutures;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	
	private MoneyRegister mR;
	private int startTick = -1;
	private int currentTick;
	private final CountDownLatch latch;
	private final CountDownLatch latch2;
    private NumOfTakeBookFutures numOfTakeBookFutures = NumOfTakeBookFutures.getInstance();
	
	public SellingService(CountDownLatch latch,CountDownLatch latch2) {
		super("SellingService");
		MoneyRegister mR = MoneyRegister.getInstance();
		this.latch = latch;
		this.latch2 = latch2;
	}

@Override
	protected void initialize() {
		
		this.subscribeBroadcast(TickBroadcast.class, (event)->{
			if(startTick == -1){
				this.startTick = event.getTick();
			}
			currentTick = event.getTick();
		});
		
		this.subscribeBroadcast(TermBroadcast.class, (event)->{
			latch2.countDown();
			this.terminate();
			LinkedList<Future<?>> l = numOfTakeBookFutures.getListOfFutures();
			for(int i=0; i<l.size();i++){
				synchronized(l.get(i)) {
				if(!l.get(i).isDone()){
					l.get(i).resolve(null);
				}
				}
			}
		});
		
		this.subscribeEvent(BookOrderEvent.class, (event)->{
			Integer x = event.getCustumer().getAvailableCreditAmount();
			String s = event.getTitle();
			Customer cus = event.getCustumer();
			synchronized(event.getCustumer()){//we have to use synchronized here, otherwise customer can take a book from the inventory but because other order ended he left without enough money to buy the book.
				Future<?> f = sendEvent(new TakeBookFromInventoryEvent(cus,x,s));
				
				numOfTakeBookFutures.addFuture(f);
				synchronized(f){
					while(!f.isDone()){
						try {
							f.wait();
						} catch (InterruptedException e) {
						}
					}
				}
				try {
					if(f.get()!=null) {
					int y = (int) ((Pair<?, ?>)f.get()).getSecond();
					
						if((((Pair<?, ?>)f.get()).getFirst())==(OrderResult.SUCCESSFULLY_TAKEN)&&event.getCustumer().getAvailableCreditAmount()>y){
							Customer c = event.getCustumer();
						
							mR =  MoneyRegister.getInstance();
							mR.chargeCreditCard(c,y);	
							OrderReceipt order = new OrderReceipt(event.getOrderId(), event.getSeller(), event.getCustumer().getId(), event.getTitle(), y,currentTick,event.getTick(),startTick);
							this.complete(event, order);
							mR.file(order);
							event.getCustumer().addReceipt(order);
							
							this.sendEvent(new DeliveryEvent(event.getCustumer().getAddress(),event.getCustumer().getDistance()));
						}
						else{
							this.complete(event, null);
						}
					}
					
				}
			 catch (InterruptedException e) {
			}
		
			}    
		});
		latch.countDown();

	}

}
