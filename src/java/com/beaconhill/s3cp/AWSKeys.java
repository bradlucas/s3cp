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

import org.apache.log4j.Logger;

import java.util.Properties;
import java.io.File;
import java.io.InputStream;

/**
 * A single point of reference for my AWS credentials.
 * 
 * @author Brad Lucas <brad@beaconhill.com>
 * 
 */
public class AWSKeys {

	String accessKey = "";

	String secretKey = "";

	private static Logger log = Logger.getLogger(AWSKeys.class);

	/**
	 * 
	 * @see http://www.ibm.com/developerworks/java/library/j-dcl.html
	 * @return
	 */
	public static AWSKeys getInstance(String accountName) {
		AWSKeys keys = new AWSKeys();
		keys.loadFromSystem(accountName);
		return keys;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void loadFromSystem(String accountName) {
		String propFile = System.getenv().get("HOME");

		// The name of Windows' env for the home directory is a little different.
		if (propFile == null) {
      String propDrive = System.getenv().get("HOMEDRIVE");
			propFile  = System.getenv().get("HOMEPATH");

      if (propDrive != null && (propFile.charAt(0) == '\\' || propFile.charAt(0) == '/'))
        propFile = propDrive + propFile;
		}
		
		propFile += "/.s3cp/s3cp.properties";
				
		System.out.println(propFile);
		if (propFile != null) {
			File f = new File(propFile);
			if (f.isFile()) {
				loadFromPropertiesFile(propFile, accountName);
			} else {
				propFile = null;
				System.err
						.println("ERROR: no $HOME/.s3cp/s3cp.properties file found!");
				System.exit(1);
			}
		}
	}

	private void loadFromPropertiesFile(String propertiesFile, String accountName) {
		String msg ="";
		boolean error = false;
		try {
			Properties prop = new Properties();
			InputStream is = new java.io.FileInputStream(propertiesFile);
			prop.load(is);
			if (accountName != null && !accountName.isEmpty()) {
				accessKey = prop.getProperty(accountName + ".s3.accessKey");
				secretKey = prop.getProperty(accountName + ".s3.secretKey");
				if (accessKey == null || secretKey == null) {
					msg = "Error retrieving " + accountName + ".s3.accessKey and " + accountName + ".s3.secretKey settings from " + propertiesFile;
					error = true;
				}
			} else {
				accessKey = prop.getProperty("s3.accessKey");
				secretKey = prop.getProperty("s3.secretKey");
				if (accessKey == null || secretKey == null) {
					msg = "Error retrieving s3.accessKey and s3.secretKey settings from " + propertiesFile;
					error = true;
				}
			}
		} catch (Exception e) {
			System.err.println("Error opening properties file: "
					+ propertiesFile);
			throw new RuntimeException("Can't continue");
		}
		if (error) {
			throw new RuntimeException(msg);
		}
	}

	public void dp() {
			System.out.println(accessKey);
			System.out.println(secretKey);
	}
}
