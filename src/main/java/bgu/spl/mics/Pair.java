package bgu.spl.mics;

public class Pair<E,F> {
	private E first; //first member of pair
	private F second; //second member of pair

	public Pair(E first, F second) {
	    this.first = first;
	    this.second = second;
	}

	public void setFirst(E first) {
	    this.first = first;
	}
	public void setSecond(F second) {
	    this.second = second;
	}

	public E getFirst() {
	    return first;
	}
	public F getSecond() {
	    return second;
	}
}
