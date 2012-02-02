package com.beaconhill.s3cp;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ProcessorTest  {

	Processor p;
	
	@Before
	public void setup() {
		p = new Processor("TEST");
	}
	
	@Test
	public void testLocalFileExists() {
		String filename = "/home/brad/.bashrc";
		assertTrue(p.localFileExists(filename));
	}

	@Test public void testLocalFileExistsFalse() {
		String filename = "/home/brad/.bashrcnoexits";
		assertTrue(!p.localFileExists(filename));
	}
	
}
