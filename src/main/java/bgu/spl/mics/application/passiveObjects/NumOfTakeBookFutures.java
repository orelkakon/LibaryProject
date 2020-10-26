package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;

import bgu.spl.mics.Future;


public class NumOfTakeBookFutures {

private LinkedList<Future<?>> l = new LinkedList<Future<?>>();
	
	private static class SingletonHolder {
		private static NumOfTakeBookFutures instance = new NumOfTakeBookFutures();
	} 
	public NumOfTakeBookFutures(){
		
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static NumOfTakeBookFutures getInstance() {
		return SingletonHolder.instance;
	}
	
	public void addFuture(Future<?> f) {
		l.add(f);
		
	}
	public LinkedList<Future<?>> getListOfFutures(){
		return l;
	}
}
