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

import java.io.File;

import static com.beaconhill.s3cp.S3FileInfo.isS3FileUri;

/**
 * 
 * 
 * @author Brad Lucas <brad@beaconhill.com>
 *
 */
public class Processor {

	String accountName = "";
	S3 s3 = null;
	
	public Processor(String accountName) {
		this.accountName = accountName;
		this.s3 = new S3(this.accountName);
	}
	
	private Processor() {
		// empty
	}
	
	public void run(String fromPath, String toPath) {
		if (localFileExists(fromPath) && isS3FileUri(toPath)) {
			s3.putFile(fromPath, toPath);
			return;
		}

		if (isS3FileUri(fromPath) && !isS3FileUri(toPath)) {
			s3.getFile(fromPath, toPath);
			return;
		}

		if (localFileExists(fromPath) && !isS3FileUri(toPath)) {
			System.out.println("Use the 'cp' command to copy a local file to another local file");
			return;
		}

		if (isS3FileUri(fromPath) && isS3FileUri(toPath)) {
			System.out.println("Copying from one S3 path to another S3 path is not yet supported");
			return;
		}
		System.err.println("missing case for : " + fromPath + "   " + toPath);
	}

    /**
     * return true only if the file is local and it exists 
     */
    boolean localFileExists(String path) {
		boolean rtn = false;

		if (path == null || path.length() == 0) {
			rtn = false;
		}

		try {
			File f = new File(path);
			rtn = f.isFile();				// make sure file exists
		} catch (Exception e) {
			rtn = false;
		}
		return rtn;
    }

}