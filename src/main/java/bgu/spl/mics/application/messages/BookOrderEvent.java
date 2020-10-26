package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event<Object> {

	private Customer cust;
	private String bookTitle;
	private int orderId;
	private String seller;
	private int orderTick;
	
	public BookOrderEvent(Customer cust, String bookTitle, int orderId, String seller, int orderTick) {
		this.cust = cust;
		this.bookTitle = bookTitle;
		this.orderId = orderId;
		this.seller = seller;
		this.orderTick = orderTick;
	}
	public Customer getCustumer(){
		return cust;
	}
	public String getTitle(){
		return bookTitle;
	}
	public int getOrderId(){
		return orderId;
	}
	public String getSeller(){
		return seller;
	}
	public int getTick(){
		return orderTick;
	}

}
