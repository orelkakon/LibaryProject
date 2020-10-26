package bgu.spl.mics.application.passiveObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {

	//fields:
	public int Id;
	public String Name;
	public String Address;
	public int Distance;
	public int CreditCard;
	public List<OrderReceipt> OReceipt;
	public AtomicInteger AvailableCC;
	public OrderSchedule[] OrderSchedule;
	
	/**
	 * constructor
	 */
	public Customer(int Id ,String Name ,String Address ,int Distance ,int CreditCard, int StartMoney) {
		this.Id = Id;
		this.Name = Name;
		this.Address = Address;
		this.Distance = Distance;
		this.CreditCard = CreditCard;
		this.OReceipt = new ArrayList<OrderReceipt>();
		this.AvailableCC = new AtomicInteger(StartMoney);
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return Name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return Id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return Address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return Distance;
	}

	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return OReceipt;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return AvailableCC.intValue();
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return CreditCard;
	}

	public void loadOS(OrderSchedule[] O) {
		this.OrderSchedule = O;
	}
	
	public void setAvailableCreditAmount(AtomicInteger count){
		AvailableCC = count;
	}
	public void addReceipt(OrderReceipt o){
		OReceipt.add(o);
	}
}


















