package com.beaconhill.s3cp;

import org.junit.Test;

public class CmdLineTest {

	@Test
	public void testRun() {
		 String[] args = { "/home/brad/tmp/s3cp-test/fileA.txt",  "s3://beaconhill/test/s3cptest/fileA.txt" };
		 CmdLine.main(args);
	}

}
