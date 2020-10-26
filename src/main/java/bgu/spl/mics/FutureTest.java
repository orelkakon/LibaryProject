package bgu.spl.mics;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class FutureTest {

	@Test
	public void testGet() {
		Future<String> F = new Future<String>();
		F.resolve("x");
		try {
			assertEquals(F.get(),"x");
		} catch (InterruptedException e) {
		}
	}
	@Test
	public void testResolve() {
		Future<String> F = new Future<String>();
		F.resolve("Well");
		try {
			assertEquals(F.get(),"Well");
		} catch (InterruptedException e) {
		}
	}
	@Test
	public void testIsDone() {
		Future<String> F = new Future<String>();
		assertEquals(F.isDone(),false);
		F.resolve("isDone_Well");
		assertEquals(F.isDone(),true);		
	}
	@Test
	public void testGET() {
		Future<Boolean> F = new Future<Boolean>();
		assertNull(F.get(2,TimeUnit.SECONDS));
		F.resolve(true);
		assertEquals(F.get(1,TimeUnit.SECONDS),true);	
		}
}
