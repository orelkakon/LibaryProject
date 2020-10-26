package bgu.spl.mics;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private LinkedList<MicroService> microServices;
	private ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>> listOfRegisterMicroPerEvent;
	private ConcurrentHashMap<MicroService, ArrayBlockingQueue<Message>> listQueue;
	private LinkedList<Pair<Event<?>,Future<?>>> FutureForEvent;
	private int roundRobin;
	private Object o = new Object();

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}    
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	public MessageBusImpl(){
		this.microServices = new LinkedList<MicroService>();
		this.listOfRegisterMicroPerEvent = new ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>>();
		this.listQueue = new ConcurrentHashMap<MicroService, ArrayBlockingQueue<Message>>();
		this.FutureForEvent = new LinkedList<Pair<Event<?>,Future<?>>>();
		roundRobin = 0;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		microServices.add(m);
		boolean isFound = false;
		for(int i = 0; i< listOfRegisterMicroPerEvent.size() && !isFound; i++){
			if(listOfRegisterMicroPerEvent.containsKey(type)){
				isFound = true;
				listOfRegisterMicroPerEvent.get(type).add(m);
			}	
		}
		if(!isFound){
			LinkedList<MicroService> l = new LinkedList<MicroService>();
			l.add(m);
			listOfRegisterMicroPerEvent.put(type, l);
		}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		microServices.add(m);
		boolean isFound = false;
		for(int i = 0; i< listOfRegisterMicroPerEvent.size() && !isFound; i++){
			if(listOfRegisterMicroPerEvent.containsKey(type)){
				isFound = true;
				listOfRegisterMicroPerEvent.get(type).add(m);
			}	
		}
		if(!isFound){
			LinkedList<MicroService> l = new LinkedList<MicroService>();
			l.add(m);
			listOfRegisterMicroPerEvent.put(type, l);
		}


	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void complete(Event<T> e, T result) {
		for(int i=0; i<FutureForEvent.size();i++){
			if(FutureForEvent.get(i).getFirst()==e){
				((Future<Object>) FutureForEvent.get(i).getSecond()).resolve(result);

			}		
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for(int i = 0; i<listOfRegisterMicroPerEvent.get(b.getClass()).size() ; i++){
			if(listQueue.get(listOfRegisterMicroPerEvent.get(b.getClass()).get(i))!=null)
			listQueue.get(listOfRegisterMicroPerEvent.get(b.getClass()).get(i)).add(b);
		}


	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized(o) {
			Future<T> f = new Future<T>();
			FutureForEvent.add(new Pair<Event<?>,Future<?>>(e,(Future<?>) f));


			try{
				if (roundRobin > listOfRegisterMicroPerEvent.get(e.getClass()).size()-1){
					roundRobin = 0;
				}
                if(listQueue.get(listOfRegisterMicroPerEvent.get(e.getClass()).get(roundRobin))!=null) {
				listQueue.get(listOfRegisterMicroPerEvent.get(e.getClass()).get(roundRobin)).put(e);
				roundRobin++;
                }

				
			}
			catch(InterruptedException ex){

			}

			return  f;
		}
	}



	@Override
	public void register(MicroService m) {
		ArrayBlockingQueue<Message> q = new ArrayBlockingQueue<Message>(100000);
		listQueue.put(m, q);


	}

	@Override
	public void unregister(MicroService m) {
		boolean isFound = false;
		ArrayBlockingQueue<Message> x = listQueue.get(m);
		for(int j = 0;j<x.size();j++){
			LinkedList<MicroService> l = listOfRegisterMicroPerEvent.get(x.toArray()[j]);
			if(l!=null) {
				for(int k = 0 ; k<l.size()&&!isFound;k++){
					if(m==l.get(k)){
						l.remove(k);
						isFound = true;
					}
				}
			}
		}
		listQueue.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return listQueue.get(m).take();
	}



}
