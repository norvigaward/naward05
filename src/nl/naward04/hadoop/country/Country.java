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
package nl.naward04.hadoop.country;

import nl.surfsara.warcutils.WarcInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.util.Tool;

/**
 * Tool implementation that runs a mapreduce job that extracts servertype strings from wat files. 
 * 
 * @author mathijs.kattenberg@surfsara.nl
 */
public class Country extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {

		Configuration conf = this.getConf();
		
		// Set compress type to compress BLOCKs (not RECORDs)
		// https://hadoop.apache.org/docs/r2.4.0/hadoop-mapreduce-client/hadoop-mapreduce-client-core/mapred-default.xml
		// http://hadoop.apache.org/docs/r2.4.0/api/org/apache/hadoop/io/SequenceFile.html
		conf.set(FileOutputFormat.COMPRESS_TYPE, "BLOCK");

		Job job = Job.getInstance(conf, "Find the country based on domain name or IP address.");
		job.setJarByClass(Country.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(CountryLookup.class);
		job.setInputFormatClass(WarcInputFormat.class);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		// Enable compression
		FileOutputFormat.setCompressOutput(job, true);
		FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);

		// Execute job and return status
		return job.waitForCompletion(true) ? 0 : 1;

	}
	
}
