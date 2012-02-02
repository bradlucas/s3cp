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

import org.jets3t.service.model.S3Object;

/**
 * 
 * 
 * @author Brad Lucas <brad@beaconhill.com>
 *
 */
public class S3 {

	protected String accountName;

	public S3(String accountName) {
		this.accountName = accountName;
	}

	private S3() {
		// empty
	}

	public void getFile(String uri, String localFilename) {
		getFile(S3FileInfo.getS3FileInfo(uri), localFilename);
	}

	public void putFile(String localFilename, String uri) {
		putFileEx(S3FileInfo.getS3FileInfo(uri), localFilename);
	}

	private void deleteFile(String uri) {
		// deleteFileEx(S3FileInfo.getS3FileInfo(uri));
		throw new RuntimeException("No yet supported, tested");
	}

	private void getFile(S3FileInfo fi, String localFilename) {
		System.out.println("ACCOUNTNAME: " + accountName);
		AWSUtility aws = AWSUtility.getAWSConnection(accountName);
		System.out.println("" + fi.bucket + " , " + fi.path + " " + fi.filename);
		
		
		S3Object s3object = aws.getObject(fi.bucket, fi.path + fi.filename);
		if (s3object != null) {
			if (localFilename != null && !localFilename.equals("")) {
				aws.saveObjectToFile(s3object, localFilename);
			} else {
				aws.saveObjectToFile(s3object, fi.filename);
			}
		} else {
			System.out.println("s3object is null");
			System.out.println("Could not find " + fi + ", " + localFilename);
		}
	}

	private void putFileEx(S3FileInfo fi, String localFilename) {
		AWSUtility aws = AWSUtility.getAWSConnection(accountName);

		// put local file into directory
		if (fi.isDirectory()) {
			aws.putFile(removeTrailingSlash(fi.bucket + "/" + fi.path), "", localFilename);
		} else {
			aws.putFile(removeTrailingSlash(fi.bucket + "/" + fi.path), fi.filename, localFilename);
		}
	}

	private String removeTrailingSlash(String path) {
		String rtn = path;
		int slashIndex = path.lastIndexOf('/');
		int len = path.length();
		if (slashIndex == len - 1) {
			rtn = path.substring(0, slashIndex);
		}
		return rtn;
	}

	private void deleteFileEx(S3FileInfo fi) {
		AWSUtility aws = AWSUtility.getAWSConnection(accountName);

		aws.deleteFile(removeTrailingSlash(fi.bucket + "/" + fi.path), fi.filename);
	}

}
