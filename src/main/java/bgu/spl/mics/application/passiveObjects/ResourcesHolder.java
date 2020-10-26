package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.LinkedList;


import bgu.spl.mics.Future;


/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	LinkedList<Future<DeliveryVehicle>> listOfFuturesWithoutVehicle = new LinkedList<Future<DeliveryVehicle>>();
	ArrayList<DeliveryVehicle> cars = new ArrayList<DeliveryVehicle>();
	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	} 
	public ResourcesHolder(){
		
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
	 * @throws InterruptedException 
     */
	public Future<DeliveryVehicle> acquireVehicle() throws InterruptedException {
	
		
		Future<DeliveryVehicle> f = new Future<DeliveryVehicle>(); 
		if(cars.size()>0){
			synchronized(cars.get(0)){
				if(cars.size()>0){
				f.resolve(cars.get(0));
				cars.remove(0);
				}
			}
		}
		else{
			listOfFuturesWithoutVehicle.add(f);
		}
		return f;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		DeliveryVehicle[] deliveryCars = new DeliveryVehicle[cars.size()];
		for (int j=1; j<cars.size()-1; j++){
			deliveryCars[j] = cars.get(j);
		}
		cars.add(vehicle);
		if(listOfFuturesWithoutVehicle.size()>0){
			synchronized(listOfFuturesWithoutVehicle.get(0)) {
				if(!listOfFuturesWithoutVehicle.get(0).isDone())
				
			         listOfFuturesWithoutVehicle.get(0).resolve(vehicle);
		}
		}
		else{
			cars.add(vehicle);
			if(deliveryCars.length!=0) 
			   deliveryCars[0] = vehicle;
			else {
				deliveryCars = new DeliveryVehicle[1];
						deliveryCars[0] = vehicle;
			
			}
		}
		boolean isSort = false;
		DeliveryVehicle tmp = null;
			for (int j=0; j<deliveryCars.length-2&&!isSort; j++){
				if (deliveryCars[j].getSpeed() < deliveryCars[j+1].getSpeed()) {
				    tmp = deliveryCars[j];
				    deliveryCars[j] = deliveryCars[j+1];
				    deliveryCars[j+1] = tmp;
				}
				else
					isSort = true;
			}
			for (int j=0; j<deliveryCars.length-1&&!isSort; j++){
				cars.set(j, deliveryCars[j]);
			}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		DeliveryVehicle tmp;
		for (int i=0; i<vehicles.length-1; i++){
			for (int j=0; j<vehicles.length-1; j++){
				if (vehicles[j].getSpeed() < vehicles[j+1].getSpeed()) {
				tmp = vehicles[j];
				vehicles[j] = vehicles[j+1];
				vehicles[j+1] = tmp;
				}
			}
		}
		for(int i=0;i<vehicles.length;i++){
			cars.add(vehicles[i]);
		}
		
	}

}

