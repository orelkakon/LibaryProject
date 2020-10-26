package bgu.spl.mics.application.passiveObjects;
import static org.junit.Assert.*;
import org.junit.Test;

public class InventoryTest {

	
	@Test
	public void testLoad() {
		Inventory INV = Inventory.getInstance();
		BookInventoryInfo T1= new BookInventoryInfo("Harry Poter",5,60);
		BookInventoryInfo T2= new BookInventoryInfo("SuperMan",3,45);
		BookInventoryInfo[] arr = {T1,T2};
		INV.load(arr);
		assertEquals("Harry Poter" , INV.take(arr[0].getBookTitle())); 
		assertEquals("SuperMan" , INV.take(arr[1].getBookTitle())); 
		INV = null;
	}
	@Test
	public void testTake() {
		Inventory  INV = Inventory.getInstance();
		BookInventoryInfo T1= new BookInventoryInfo("Harry Poter",5,60);
		BookInventoryInfo[] arr = {T1};
		INV.load(arr);
		assertEquals(OrderResult.SUCCESSFULLY_TAKEN , INV.take("Harry Poter"));
		assertEquals(OrderResult.NOT_IN_STOCK , INV.take("BatMan"));
		INV = null;
	}
	@Test
	public void testCheckAvailabiltyAndGetPrice() {
		Inventory  INV = Inventory.getInstance();
		BookInventoryInfo T1= new BookInventoryInfo("Harry Poter",5,60);
		BookInventoryInfo[] arr = {T1};
		INV.load(arr);
		assertEquals(60 , INV.checkAvailabiltyAndGetPrice("Harry Poter"));
		assertEquals(-1 , INV.checkAvailabiltyAndGetPrice("BatMan"));
		INV = null;
	}
	
}
