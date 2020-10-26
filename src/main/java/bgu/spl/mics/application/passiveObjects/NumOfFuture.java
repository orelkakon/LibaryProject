package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;

import bgu.spl.mics.Future;


public class NumOfFuture {

	
	private LinkedList<Future<Object>> l = new LinkedList<Future<Object>>();
	
	private static class SingletonHolder {
		private static NumOfFuture instance = new NumOfFuture();
	} 
	public NumOfFuture(){
		
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static NumOfFuture getInstance() {
		return SingletonHolder.instance;
	}
	
	public void addFuture(Future<Object> fut) {
		l.add(fut);
		
	}
	public LinkedList<Future<Object>> getListOfFutures(){
		return l;
	}
	

}
