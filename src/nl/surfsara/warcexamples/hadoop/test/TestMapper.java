
package nl.surfsara.warcexamples.hadoop.test;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.jwat.warc.WarcRecord;

class TestMapper extends Mapper<LongWritable, WarcRecord, Text, Text> {
	private static final Logger logger = Logger.getLogger(TestMapper.class);

	private static enum Counters {
		CURRENT_RECORD, NUM_JSON_RECORDS
	}
	
	@Override
	public void map(LongWritable key, WarcRecord value, Context context) throws IOException, InterruptedException {
		context.setStatus(Counters.CURRENT_RECORD + ": " + key.get());
		
		context.write(new Text("Test 1"), new Text(Long.toString(key.get())));
		context.write(new Text("Test 2"), new Text(Long.toString(key.get())));
		context.write(new Text("Test 3"), new Text(Long.toString(key.get())));
		context.write(new Text("Test 4"), new Text(Long.toString(key.get())));
		context.write(new Text("Test 5"), new Text(Long.toString(key.get())));
	}
}