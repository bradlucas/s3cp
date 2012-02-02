/*
 *    This file is part of s3cp.
 *
 *    s3cp is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    s3cp is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with s3cp.  If not, see <http:www.gnu.org/licenses/>.
 */
package com.beaconhill.s3cp;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * 
 * 
 * @author Brad Lucas <brad@beaconhill.com>
 *
 */
public class S3FileInfo {

	// s3://trading/ideas/ideas/trading-system.txt
	//
	// bucket 	- trading
	// object	- ideas/ideas/
	// filename - trading-system.txt

	public String bucket;
	public String path;
	public String filename = "";
	
	final static String S3_PREFIX = "s3://"; 
	final static String HTTP_PREFIX = "http://";
	
	boolean isDirectory() {
		return filename.equals("");
	}

	public String toString() {
		String rtn = "";
		rtn += "bucket : " + bucket + "\n";
		rtn += "object : " + path + "\n";
		rtn += "file   : " + filename;
		return rtn;
	}

	static public S3FileInfo getS3FileInfo(String uri) {
		S3FileInfo fi = new S3FileInfo();

		// if protocol is s3, replace with http else URL will complain
		String path = uri.replaceFirst(S3_PREFIX, HTTP_PREFIX);				// TODO - compare ignore case
		try {
			URL url = new URL(path);
			fi.bucket = url.getHost();
			String tmp = removePrefixSlash(url.getPath());
			if (hasTrailingSlash(tmp)) {
				fi.filename = "";
				fi.path = tmp;
			} else {
				fi.filename = justFile(tmp);
				fi.path = justPath(tmp);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fi;
	}
	
	static public boolean isS3FileUri(String uri) {
		boolean rtn = false;
		if (uri == null || uri.length() == 0) {
			return false;
		}
		if (uri.startsWith(S3_PREFIX)) {			// TODO - compare ignore case
			rtn = true;
		}
		return rtn;
	}

	static boolean hasTrailingSlash(String path) {
		boolean rtn = false;
		char c = path.charAt(path.length()-1);
		if (c == '/') {
			rtn = true;
		}
		return rtn;
	}
	
	private static String removePrefixSlash(String path) {
		int slashIndex = path.indexOf('/');
		return path.substring(slashIndex + 1);
	}

	/**
	 * When dealing with a URI representation for a S3 resource if the path has
	 * a trailing / we assume the URI is a 'directory' else it is a URI to a file.
	 * If a file return it else return "" for directory.
	 * 
	 * @param path
	 * @return a filename for a file or "" for a directory
	 */
	static String justFile(String path) {
		// if path ends with a / then we have a DIRECTORY uri return ""
		// else return the filename
		if (path == null || path.length() == 0) {
			return "";
		}
		int slashIndex = path.lastIndexOf('/');
		int len = path.length();
		if (slashIndex == len - 1) {
			return "";
		} else {
			String rtn = path.substring(slashIndex + 1);
			return rtn;
		}
	}
	
	static String justPath(String path) {
		// assume we've not found a trailing slash
		// return everything before the last slahs
		if (path == null || path.length() == 0) {
			return "";
		}
		int slashIndex = path.lastIndexOf('/');
		int len = path.length();
		if (slashIndex == len - 1) {
			return "";
		} else {
			String rtn = path.substring(0, slashIndex + 1);
			return rtn;
		}
		
	}
}
