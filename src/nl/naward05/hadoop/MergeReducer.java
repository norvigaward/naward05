package nl.naward05.hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MergeReducer extends Reducer<Text, Text, Text, LongWritable> {
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, LongWritable>.Context context)
					throws IOException, InterruptedException {
		String country = null;
		List<String> songs = new ArrayList<String>();
		
		for (Text val : values) {
			String valueStr = val.toString();
			if(valueStr.startsWith("COUNTRY#")) {
				country = valueStr.substring(8);
			} else {
				songs.add(valueStr);
			}
		}
		
		if(country == null) {
			context.write(new Text("ERROR: " + key.toString() + " has no country"), new LongWritable(1));
		} else {
			for(String song : songs) {
				context.write(new Text(country + "\t" + song), new LongWritable(1));
			}
		}
	}
}
