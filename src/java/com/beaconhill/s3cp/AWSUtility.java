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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.jets3t.service.Constants;
import org.jets3t.service.Jets3tProperties;
import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.ServiceUtils;

/**
 * A single class to access the S3 Storage system.
 * 
 * @author Brad Lucas <brad@beaconhill.com>
 * 
 */
public class AWSUtility {

	private AWSCredentials credentials;

	private S3Service service;

	private static Logger log = Logger.getLogger(AWSUtility.class);

	public static AWSUtility getAWSConnection(String accountName) {
		AWSKeys keys = AWSKeys.getInstance(accountName);
		return AWSUtility.getAWSConnection(keys);
	}
	
	private static AWSUtility getAWSConnection(AWSKeys keys) {
		AWSUtility aws = null;
		try {
			aws = new AWSUtility(keys);
		} catch (Exception ex) {
			aws = null;
			log.debug("Error creating AWS Connection : " + ex.getMessage());
		}
		return aws;
	}

	private AWSUtility() {
		// empty
	}

	private AWSUtility(AWSKeys keys) throws Exception {
		this(keys.getAccessKey(), keys.getSecretKey());
	}

	private AWSUtility(String accessKey, String secretKey) throws Exception {
		credentials = new AWSCredentials(accessKey, secretKey);
		initService();
	}

	private void initService() throws Exception {
		assert (credentials != null);
		if (credentials == null) {
			throw new IllegalStateException("Credentials not setup prior to initService call");
		}

		try {
			// check if we have proxy username/password
			String proxyUsername = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
					.getStringProperty("httpclient.proxy-username", "");
			String proxyPassword = Jets3tProperties.getInstance(Constants.JETS3T_PROPERTIES_FILENAME)
					.getStringProperty("httpclient.proxy-password", "");
			// if (proxyUsername.length()>0) {
			// //service = new RestS3Service(credentials);
			// ProxyCredentials creds = new ProxyCredentials(proxyUsername,
			// proxyPassword);
			// service = new RestS3Service(credentials, "Firefox", creds);
			// } else {
			service = new RestS3Service(credentials);
			// }
		} catch (S3ServiceException serviceException) {
			log.debug(serviceException.getMessage());
		}
	}

	public List<S3Bucket> getBuckets() {
		S3Bucket[] buckets = null;
		try {
			buckets = service.listAllBuckets();

		} catch (S3ServiceException servEx) {
			log.debug(servEx.getMessage());
			log.debug(servEx.getS3ErrorCode());
			log.debug(servEx.getS3ErrorMessage());
		}
		if (buckets == null) {
			throw new RuntimeException("No buckets found, network issue");
		}
		return Arrays.asList(buckets);
	}

	public List<String> getBucketNames() {
		List<S3Bucket> buckets = getBuckets();
		ArrayList<String> list = new ArrayList<String>();
		if (buckets != null) {
			for (S3Bucket bucket : buckets) {
				list.add(bucket.getName());
			}
		}
		return list;
	}

	public S3Bucket getBucket(String bucketName) {
		S3Bucket bucket = null;
		try {
			boolean exists = service.isBucketAccessible(bucketName);
			if (exists) {
				bucket = new S3Bucket(bucketName);
			}
		} catch (S3ServiceException serviceException) {
			log.debug(serviceException.getMessage());
			log.debug(serviceException.getS3ErrorCode());
			log.debug(serviceException.getS3ErrorMessage());
		} catch (ServiceException ex) {
		    log.debug(ex.getMessage());
		}
		return bucket;
	}

	public List<S3Object> getObjects(String bucketName) {
		S3Bucket bucket = new S3Bucket(bucketName);
		return getObjects(bucket);
	}

	public List<S3Object> getObjects(S3Bucket bucket) {
		S3Object[] objects = null;
		if (bucket != null) {
			try {
				objects = service.listObjects(bucket);
			} catch (S3ServiceException serviceException) {
				log.debug(serviceException.getMessage());
				log.debug(serviceException.getS3ErrorCode());
				log.debug(serviceException.getS3ErrorMessage());
			}
		}
		return Arrays.asList(objects);
	}

	/**
	 * Given a bucketName return it's contents as a list of keys
	 * 
	 * @param bucketName
	 * @return
	 */
	public List<String> getBucketKeys(String bucketName) {
		List<S3Bucket> buckets = getBuckets();
		List<S3Object> objects = null;
		if (buckets != null) {
			for (S3Bucket bucket : buckets) {
				if (bucketName.equals(bucket.getName())) {
					objects = getObjects(bucket);
					break;
				}
			}
		}
		ArrayList<String> list = new ArrayList<String>();
		if (objects != null) {
			for (S3Object object : objects) {
				list.add(object.getKey());
			}
		}
		return list;
	}

	public S3Object getObject(String bucketName, String objectKey) {
		S3Bucket bucket = getBucket(bucketName);
		S3Object obj = null;
		if (bucket != null) {
			try {
				System.out.println( "getting object " + bucketName + " - " + objectKey );
				obj = service.getObject(bucketName, objectKey);
				
			} catch (S3ServiceException serviceException) {
				log.debug(serviceException.getMessage());
				log.debug(serviceException.getS3ErrorCode());
				log.debug(serviceException.getS3ErrorMessage());
			}
		}
		return obj;
	}

		/**
	 * put a file into S3 using bucketName which should look like a path. We call this method when we know 
	 * the remote file's name so we can overwrite it.
	 * 
	 * @param bucketName
	 * @param fileName
	 * @return
	 */
	public boolean putFile(String bucketName, String remoteFilename, String fileName) {
		boolean rtn = false;

		S3Bucket bucket = new S3Bucket(bucketName);

		// Create an S3Object based on a file, with Content-Length set
		// automatically and
		// Content-Type set based on the file's extension (using the Mimetypes
		// utility class)
		File fileData = new File(fileName);
		S3Object fileObject;
		try {
			fileObject = new S3Object(bucket, fileData);
			if (remoteFilename != null && remoteFilename.length() > 0 ) {    // remote file has a different name so use it
			    fileObject.setKey(remoteFilename);
			}
			rtn = putObject(bucket, fileObject);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (java.io.IOException ioEx) {
			ioEx.printStackTrace();
		}
		return rtn;
	}

	public boolean putFile(String bucketName, String fileName, String mimeType, byte[] data) {
		boolean rtn = false;

		S3Bucket bucket = new S3Bucket(bucketName);

		// Create an object containing a greeting string as input stream data.
		String greeting = "Hello World!";

		// 		S3Object helloWorldObject = new S3Object("HelloWorld2.txt");
		// 		ByteArrayInputStream greetingIS = new ByteArrayInputStream(greeting.getBytes());
		// 		helloWorldObject.setDataInputStream(greetingIS);
		// 		helloWorldObject.setContentLength(greetingIS.available());
		// 		helloWorldObject.setContentType("text/plain");

		// 		putObject(bucket, helloWorldObject);

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		S3Object s3Obj = new S3Object(bucket, fileName);
		s3Obj.setDataInputStream(bais);

		System.out.println("available : " + bais.available());
		System.out.println("length    : " + data.length);

		s3Obj.setContentLength(bais.available());
		s3Obj.setContentType(mimeType);
		try {
			s3Obj.setMd5Hash(ServiceUtils.computeMD5Hash(bais));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bais.reset();

		System.out.println("Putting data to ..." + bucket + " " + s3Obj);

		rtn = putObject(bucket, s3Obj);
		System.out.println("done");

		return rtn;
	}

	public boolean putObject(S3Bucket bucket, S3Object object) {
		boolean rtn = false;
		try {
			service.putObject(bucket, object);
			rtn = true;
		} catch (S3ServiceException e) {
			e.printStackTrace();
		}
		return rtn;
	}

	public boolean deleteFile(String bucketName, String fileName) {
		System.out.println("deleteFile ( " + bucketName + ", " + fileName + " )");
		boolean rtn = false;
		S3Bucket bucket = getBucket(bucketName);
		if (bucket != null) {
			S3Object obj = getObject(bucketName, fileName);
			if (obj != null) {
				System.out.println("Calling deleteObject");
				rtn = deleteObject(bucket, obj);
			} else {
				System.err.println("Could not find object for bucket : " + bucketName + ", filename : " + fileName);
			}
		} else {
			System.err.println("Error getting bucket for " + bucketName);
		}
		return rtn;
	}

	public boolean deleteObject(S3Bucket bucket, S3Object object) {
		boolean rtn = false;
		try {
			service.deleteObject(bucket, object.getKey());
			rtn = true;
		} catch (S3ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rtn;
	}

	private void writeObjectToOutputStream(String bucketName, String objectKey, OutputStream os) {
		S3Object object = getObject(bucketName, objectKey);
		writeObjectToOutputStream(object, os);
	}

	public void saveObjectToFile(S3Object object, String localFile) {
		try {
			FileOutputStream fout = new FileOutputStream(localFile);
			writeObjectToOutputStream(object, fout);
		} catch (FileNotFoundException fileNotFoundEx) {
			log.error(fileNotFoundEx.getMessage());
		}
	}

	public void saveObjectToFileInsideRootPath(S3Object object, String rootPath) {
		// build complete local path from rootPath and the object's key
		String fileName = rootPath + System.getProperty("file.separator") + object.getKey();
		log.debug("filename: " + fileName);
		saveObjectToFile(object, fileName);
	}

	public void writeObjectToOutputStream(S3Object object, OutputStream fout) {
		log.debug("file size: " + object.getContentLength());
		log.debug(" : " + object.getContentType());
		long cnt = 0;
		try {
			// FileOutputStream fout = new FileOutputStream(fileName);
			BufferedInputStream buf = new BufferedInputStream(object.getDataInputStream());

			byte[] buffer = new byte[1024];// byte buffer
			int bytesRead = 0;
			while (true) {
				bytesRead = buf.read(buffer, 0, 1024);
				// bytesRead returns the actual number of bytes read from
				// the stream. returns -1 when end of stream is detected
				if (bytesRead == -1)
					break;
				fout.write(buffer, 0, bytesRead);
				System.out.print(".");
				cnt += bytesRead;
			}
			System.out.println("");
			if (buf != null)
				buf.close();
			if (fout != null)
				fout.close();
			buf = null;
			fout = null;
		} catch (FileNotFoundException fileNotFoundEx) {
			log.error(fileNotFoundEx.getMessage());
		} catch (IOException ioException) {
			log.error(ioException.getMessage());
		} catch (S3ServiceException serviceException) {
			log.debug(serviceException.getMessage());
			log.debug(serviceException.getS3ErrorCode());
			log.debug(serviceException.getS3ErrorMessage());
		} catch (ServiceException ex) {
		    log.debug(ex.getMessage());
		}
		log.debug("saved : " + cnt);

	}

}
