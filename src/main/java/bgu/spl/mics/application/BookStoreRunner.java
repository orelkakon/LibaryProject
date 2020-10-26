package bgu.spl.mics.application;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.APIService;
import bgu.spl.mics.application.services.InventoryService;
import bgu.spl.mics.application.services.LogisticsService;
import bgu.spl.mics.application.services.ResourceService;
import bgu.spl.mics.application.services.SellingService;
import bgu.spl.mics.application.services.TimeService;

/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String JsonFile;
		//File names from inputs:
		String CustomersFile;
		String BooksFile;
		String ReceiptsFile;
		String MoneyRegisterFile;
		JsonFile = args[0];
		CustomersFile = args[1];
		BooksFile = args[2];
		ReceiptsFile = args[3];
		MoneyRegisterFile = args[4];

		// Instance class
		Inventory inv = Inventory.getInstance();
		ResourcesHolder RH = ResourcesHolder.getInstance();
		MoneyRegister MoneyR = MoneyRegister.getInstance();
		FileReader reader = null;
		try {
			reader = new FileReader(JsonFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		JsonParser parser = new JsonParser();
		Object parse1 = parser.parse(reader);
		JsonObject json = (JsonObject) parse1;	
		JsonArray initBooks = (JsonArray) json.get("initialInventory");
		BookInventoryInfo[] booksLoad = BookStoreRunner.invInit(initBooks);
		inv.load(booksLoad); // load the books from jsonFile   //first load
		JsonArray resources = (JsonArray) json.get("initialResources");
		JsonArray veichelsLoad = (((JsonObject)resources.get(0)).get("vehicles")).getAsJsonArray();
		DeliveryVehicle[] vehicles = BookStoreRunner.vechilesInit(veichelsLoad); 
		RH.load(vehicles); //second load
		JsonElement serv = json.get("services");;
		Time timeInfo = new Time(((JsonObject)((JsonObject) serv).get("time")).get("speed").getAsInt(),((JsonObject)((JsonObject) serv).get("time")).get("duration").getAsInt() );
		// services Number:
		int SellingNum = ((JsonObject) serv).get("selling").getAsInt();
		int inventoryServiceNum = ((JsonObject) serv).get("inventoryService").getAsInt();
		int logisticsNum = ((JsonObject) serv).get("logistics").getAsInt();
		int resourcesServiceNum = ((JsonObject) serv).get("resourcesService").getAsInt();
		// customers:
		JsonArray customers = (((JsonObject) serv).get("customers")).getAsJsonArray();
		Customer[] CustList = Cust(customers,json);
		// build the services:
		CountDownLatch CDL = new CountDownLatch(SellingNum+inventoryServiceNum+logisticsNum+resourcesServiceNum);
		CountDownLatch CDL2 = new CountDownLatch(SellingNum+inventoryServiceNum+logisticsNum+resourcesServiceNum+1);
		for(int i=0;i<CustList.length;i++) {
			Thread t = new Thread(new APIService(CustList[i].OrderSchedule,CustList[i],i+1+"",CDL,CDL2));
			t.start();
		}
		for(int i=0;i<SellingNum;i++) {
			Thread t = new Thread(new SellingService(CDL,CDL2));
			t.start();
		}
		for(int i=0;i<inventoryServiceNum;i++) {
			Thread t = new Thread(new InventoryService(CDL,CDL2));
			t.start();
		}
		for(int i=0;i<logisticsNum;i++) {
			Thread t = new Thread(new LogisticsService(CDL,CDL2));
			t.start();
		}
		for(int i=0;i<resourcesServiceNum;i++) {
			Thread t = new Thread(new ResourceService(CDL,CDL2));
			t.start();
		}
		try {
			CDL.await();
			Thread time = new Thread(new TimeService(timeInfo.getSpeed(), timeInfo.getduration(),CDL2));
			time.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
		
		
		// after all run of main
		try {
			CDL2.await();
			printCustomers(CustomersFile,CustList);
			inv.printInventoryToFile(BooksFile);
			MoneyR.printOrderReceipts(ReceiptsFile);
			printMoneyReg(MoneyRegisterFile);

			input.close();
		}catch(InterruptedException e) {
			// TODO Auto-generated catch block
		}
		
	}

	private static void printMoneyReg(String filename) {
		try {
			FileOutputStream fout = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(MoneyRegister.getInstance());
			oos.close();
		} catch (Exception e) {

		}
	}
	private static void printCustomers(String filename, Customer[] CustList) {
		HashMap<Integer , Customer> cust = new HashMap<Integer , Customer>();
		for(int i=0;i<CustList.length;i++) {
			cust.put(CustList[i].getId(), CustList[i]);
		}
		try {
			FileOutputStream fout = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(cust);
			oos.close();
		} catch (Exception e) {

		} 
	}
	private static Customer[] Cust(JsonArray customers,JsonObject json) {
		Customer[] retCust = new Customer[customers.size()];
		int x = 0;
		for(JsonElement Cu : customers) {
			Customer Temp = new Customer (((JsonObject) Cu).get("id").getAsInt(), ((JsonObject) Cu).get("name").getAsString(),((JsonObject) Cu).get("address").getAsString(),((JsonObject) Cu).get("distance").getAsInt(),((JsonObject)((JsonObject) Cu).get("creditCard")).get("number").getAsInt(), ((JsonObject)((JsonObject) Cu).get("creditCard")).get("amount").getAsInt());
			JsonArray OrDe = ((JsonObject) Cu).get("orderSchedule").getAsJsonArray();
			OrderSchedule[] OS = new OrderSchedule[OrDe.size()];
			int i = 0;
			for(JsonElement Os : OrDe) {
				OS[i] = new OrderSchedule( ((JsonObject) Os).get("bookTitle").getAsString(), ((JsonObject) Os).get("tick").getAsInt() );
				i++;		
			}
			Temp.loadOS(OS);
			retCust[x] = Temp;
			x++;
		}
		return retCust;
	}
	private static DeliveryVehicle[] vechilesInit(JsonArray veichelsLoad) {
		DeliveryVehicle[] retVeichle = new DeliveryVehicle[veichelsLoad.size()];
		int x = 0;
		for (JsonElement veichle : veichelsLoad) {
			DeliveryVehicle Temp = new DeliveryVehicle(((JsonObject) veichle).get("license").getAsInt(),((JsonObject) veichle).get("speed").getAsInt() );
			retVeichle[x] = Temp;
			x++;
		}
		return retVeichle;
	}
	private static BookInventoryInfo[] invInit(JsonArray inv) {
		BookInventoryInfo[] retInv = new BookInventoryInfo[inv.size()];
		int x = 0;
		for (JsonElement book : inv) {
			BookInventoryInfo Temp = new BookInventoryInfo(((JsonObject) book).get("bookTitle").getAsString(), ((JsonObject) book).get("amount").getAsInt(), ((JsonObject) book).get("price").getAsInt());
			retInv[x] = Temp;
			x++;
		}
		return retInv;
	}

}
