package bgu.spl.mics.application;

public class Time {

	private int speed;
	private int duration;
	public Time(int speed,int duration) {
		this.speed = speed;
		this.duration = duration;
	}
	public int getSpeed() {
		return this.speed;
	}
	public int getduration() {
		return this.duration;
	}
}
