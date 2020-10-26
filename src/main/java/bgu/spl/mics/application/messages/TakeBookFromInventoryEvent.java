package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class TakeBookFromInventoryEvent implements Event<Object> {

	private Customer cust;
	private int amountOfCredits;
	private String title;
	public TakeBookFromInventoryEvent(Customer cust, int amount, String title) {
		this.cust = cust;
		this.amountOfCredits = amount;
		this.title = title;
	}
	public int getAmount(){
		return this.amountOfCredits;
	}
	public String getTitle(){
		return title;
	}
	public Customer getCustomer(){
		return cust;
	}

}
