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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Usage:
 * 
 * s3cp local-file s3f
 *
 * Copy to S3 [PUT]
 *
 *   s3cp local-file s3://bucket/object[/]
 *
 *   If object has a trailing slash it will be assumed to mean a directory and the local-file's filename will be appended to object.
 *
 * Copy from S3 [GET]
 *
 *   s3cp s3://bucket/object [local-file]
 *
 *   If local-file is not present a filename from object will be used to create the file in the current directory.
 *
 * @author Brad Lucas <brad@beaconhill.com>
 *
 */
public class CmdLine {

	public static void main(String[] args) {
		CmdLine c = new CmdLine();
		c.run(args);
	}

	public void run(String[] args) {
		Options options = buildOptions();
		CommandLine cmd = null;
		boolean process = true;
		String accountName = "";
		if (args != null && args.length > 0) {
			cmd = parseCmdLine(options, args);
			if (cmd != null) {
				if (cmd.hasOption("h")) {
					usage(options);
					process = false;
				}
				if (cmd.hasOption("a")) {
					accountName = cmd.getOptionValue("a");
				}
			}
			if (process) {
				String[] params = cmd.getArgs();

				if (params != null && params.length > 0) {
					String p1 = params[0];
					String p2 = "";
					if (params.length > 1) {
						p2 = params[1];
					}
					if (p1.equals(".")) p1 = "";  // . is the same as blank for local file
					if (p2.equals(".")) p2 = "";
					Processor p = new Processor(accountName);
					p.run(p1, p2);
				} else {
					usage(options);
				}
			}
		} else {
			usage(options);
		}
	}

	Options buildOptions() {
		Options options = new Options();

		Option helpOption = OptionBuilder.withDescription("Help").withLongOpt(
				"help").isRequired(false).create("h");

		Option accountNameOption = OptionBuilder.hasArg().withDescription(
		"Account Name [OPTIONAL]").withLongOpt("account-name").isRequired(
		false).create("a");

		options.addOption(helpOption);
		options.addOption(accountNameOption);
		return options;
	}

	void usage(Options options) {
		// automatically generate the help statemente
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("s3cp  FILE1 FILE2", options);
	}

	CommandLine parseCmdLine(Options options, String[] args) {
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (MissingOptionException missingEx) {
			System.err.println(missingEx.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cmd;
	}

}
