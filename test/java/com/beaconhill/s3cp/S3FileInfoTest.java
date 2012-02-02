package com.beaconhill.s3cp;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import static com.beaconhill.s3cp.S3FileInfo.*;

public class S3FileInfoTest {

	@Test
	public void testGetS3FileInfo() {

		out("s3://bucket/bar\n\n" + getS3FileInfo("s3://bucket/bar") + "\n");
		out("s3://bucket/bar/\n\n" + getS3FileInfo("s3://bucket/bar/") + "\n");
		out("s3://bucket/bar/baz/filename.txt\n\n" + getS3FileInfo("s3://bucket/bar/baz/filename.txt") + "\n");
		
		out("s3://beaconhill/test/s3cp/fileA.txt\n\n" + getS3FileInfo("s3://beaconhill/test/s3cp/fileA.txt") + "\n");
		out("s3://beaconhill/test/s3cp/\n\n" + getS3FileInfo("s3://beaconhill/test/s3cp/") + "\n");  
		
		out("s3://beaconhill/test/s3cp/fileC.txt\n\n" + getS3FileInfo("s3://beaconhill/test/s3cp/fileC.txt") + "\n");
		
	}

	@Test
	public void directoryTest() {
		S3FileInfo fi = getS3FileInfo("s3://beaconhill/test/s3cp/");
		assertTrue(hasTrailingSlash(fi.path));
	}
	
	@Test
	public void directoryTestFalse() {
		S3FileInfo fi = getS3FileInfo("s3://beaconhill/test/s3cp/filename.txt");
		System.out.println(fi.path);
		assertFalse(hasTrailingSlash(fi.path));
	}

	
	public void testRemovePrefixSlash() {
		fail("Not yet implemented");
	}

	public void testRemoveTrailingSlash() {
		fail("Not yet implemented");
	}

	@Test
	public void testJustFile() {
		assertEquals("foo", justFile("/foo"));
		assertEquals("", justFile("/foo/"));
	}

	static void out(String s) {
		System.out.println(s);
	}
	
	@Test
	public void testUrl() {
		String uri = "http://beaconhill/test/s3cp/fileC.txt";
		
		try {
			URL url = new URL(uri);
			out("Host: " + url.getHost());
			String path = url.getPath();
			String file = "";
			if (hasTrailingSlash(path)) {
				out("Directory");
			} else {
				file = justFile(path);
				path = justPath(path);
			}
			out("Path: " + path);
			out("File: " + file);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void testUrl2() {
		String uri = "http://beaconhill/test/s3cp/";
		
		try {
			URL url = new URL(uri);
			out("Host: " + url.getHost());
			String path = url.getPath();
			String file = "";
			if (hasTrailingSlash(path)) {
				out("Directory");
			} else {
				file = justFile(path);
				path = justPath(path);
			}
			out("Path: " + path);
			out("File: " + file);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
