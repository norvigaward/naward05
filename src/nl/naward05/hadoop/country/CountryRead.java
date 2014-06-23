
package nl.naward05.hadoop.country;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

/**
 * Map function that from a WarcRecord (warc) parses the headers' content and extracts the country where the server resides. The resulting key, values: country, 1.  
 * 
 * @author mathijs.kattenberg@surfsara.nl
 */
class CountryRead extends Mapper<Text, Text, Text, LongWritable> {
	private static final Logger logger = Logger.getLogger(CountryRead.class);

	private static enum Counters {
		CURRENT_RECORD
	}
	
	@Override
	public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
		context.setStatus(Counters.CURRENT_RECORD + ": " + key);
		
		String valueStr = value.toString();
		
		if(valueStr.startsWith("COUNTRY#")) {
			String country = valueStr.substring(8);
			context.write(new Text(country), new LongWritable(1));
		}
	}
}