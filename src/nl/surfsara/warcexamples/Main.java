/**
 * Copyright 2014 SURFsara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.surfsara.warcexamples;

import java.util.Arrays;

import nl.naward04.hadoop.country.Country;
import nl.naward05.hadoop.MergeFiles;
import nl.naward05.hadoop.SumTool;
import nl.naward05.hadoop.country.CountCountries;
import nl.naward05.hadoop.country.IPTestTool;
import nl.surfsara.warcexamples.hadoop.ldps.LDPS;
import nl.surfsara.warcexamples.hadoop.rr.RR;
import nl.surfsara.warcexamples.hadoop.test.TestTool;
import nl.surfsara.warcexamples.hadoop.warc.Hrefs;
import nl.surfsara.warcexamples.hadoop.wat.ServerType;
import nl.surfsara.warcexamples.hadoop.wet.NER;
import nl.surfsara.warcexamples.hdfs.Headers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

/**
 * Main entry point for the warcexamples. 
 * 
 * @author mathijs.kattenberg@surfsara.nl
 */
public class Main {
	public enum Programs {
		NER("ner", "Perform named entity recognition on wet (extracted text) files."),
		LDPS("ldps", "Perform langauge detection on wet (extracted text) files."),
		RR("rr", "Record recognizer."),
		COUNTRY("country", "Country recognizer."),
		COUNTCOUNTRIES("countcountries", "Country counter."),
		MERGE("merge", "Merge country and song files"),
		SUM("sum", "Sum sequence file"),
		TEST("test", "Test tool."),
		IPTEST("iptest", "IP test tool."),
		SERVERTYPE("servertype", "Extract server type from wat (metadata) files."),
		HREF("href", "Extract links from http responses in warc (full crawl output) files."),
		HEADERS("headers", "Dumps all headers from a file (this is not a mapreduce job).");

		private final String name;
		private final String description;

		private Programs(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}
	}

	public static void main(String[] args) {
		int retval = 0;
		boolean showUsage = false;
		if(args.length <= 0) {
			showUsage();
			System.exit(0);
		}
		String tool = args[0];
		String[] toolArgs = Arrays.copyOfRange(args, 1, args.length);
		try {
			if (Programs.NER.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new NER(), toolArgs);
			} else if (Programs.LDPS.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new LDPS(), toolArgs);
			} else if (Programs.RR.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new RR(), toolArgs);
			} else if (Programs.COUNTRY.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new Country(), toolArgs);
			} else if (Programs.COUNTCOUNTRIES.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new CountCountries(), toolArgs);
			} else if (Programs.MERGE.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new MergeFiles(), toolArgs);
			} else if (Programs.SUM.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new SumTool(), toolArgs);
			} else if (Programs.TEST.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new TestTool(), toolArgs);
			} else if (Programs.IPTEST.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new IPTestTool(), toolArgs);
			} else if (Programs.SERVERTYPE.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new ServerType(), toolArgs);
			} else if (Programs.HREF.getName().equals(tool)) {
				retval = ToolRunner.run(new Configuration(), new Hrefs(), toolArgs);
			} else if (Programs.HEADERS.getName().equals(tool)) {
				Headers h = new Headers(args[1]);
				h.run();
			}
			if (showUsage) {
				showUsage();
			}
		} catch (Exception e) {
			showErrorAndExit(e);
		}
		System.exit(retval);
	}

	private static void showErrorAndExit(Exception e) {
		System.out.println("Something didn't quite work like expected: [" + e.getMessage() + "]");
		showUsage();
		System.exit(1);
	}

	private static void showUsage() {
		System.out.println("An example program must be given as the first argument.");
		System.out.println("Valid program names are:");
		for (Programs prog : Programs.values()) {
			System.out.println(" " + prog.getName() + ": " + prog.getDescription());
		}
	}
}
