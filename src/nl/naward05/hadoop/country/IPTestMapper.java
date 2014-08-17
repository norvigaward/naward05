
package nl.naward05.hadoop.country;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.net.URI;


import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jwat.common.HttpHeader;
import org.jwat.common.Payload;
import org.jwat.common.Uri;
import org.jwat.warc.WarcRecord;

/**
 * Map function that from a WarcRecord (warc) parses the headers' content and extracts the country where the server resides. The resulting key, values: country, 1.  
 * 
 * @author mathijs.kattenberg@surfsara.nl
 */
class IPTestMapper extends Mapper<LongWritable, WarcRecord, Text, LongWritable> {
	private static final Logger logger = Logger.getLogger(IPTestMapper.class);
	private final Set<String> invalidTLDs = new TreeSet<String>();

	private static enum Counters {
		CURRENT_RECORD, NUM_JSON_RECORDS
	}
	
	public IPTestMapper() {
		// From: http://en.wikipedia.org/wiki/GccTLD
		// + ly + eu
		invalidTLDs.addAll(Arrays.asList(new String[] { "ad", "as", "bz", "cc",
				"cd", "cl", "dj", "eu", "fm", "io", "la", "ly", "me", "ms",
				"nu", "sc", "sr", "su", "tv", "tk", "ws" }));
	}

	

	@Override
	public void map(LongWritable key, WarcRecord value, Context context) throws IOException, InterruptedException {
		context.setStatus(Counters.CURRENT_RECORD + ": " + key.get());
		context.write(new Text("Total records"), new LongWritable(1));
		
		try {
			// Only try to parse json content
			if ("application/json".equals(value.header.contentTypeStr)) {
				context.write(new Text("JSON records"), new LongWritable(1));
				context.getCounter(Counters.NUM_JSON_RECORDS).increment(1);
				// Get the json payload
				Payload payload = value.getPayload();
				if (payload == null) {
					context.write(new Text("Empty payload"), new LongWritable(1));
					// NOP
				} else {
					String warcContent = IOUtils.toString(payload.getInputStreamComplete());
					JSONObject json = new JSONObject(warcContent);
					context.write(new Text("JSON parsable"), new LongWritable(1));

					String warc_type = json.getJSONObject("Envelope").getJSONObject("WARC-Header-Metadata").getString("WARC-Type");
					context.write(new Text("warc_type parsable"), new LongWritable(1));
					if(!warc_type.equals("response")) {
						// Not relevant, we only use responses
						context.write(new Text("non-response"), new LongWritable(1));
						return;
					}

					String warc_record_id = json.getJSONObject("Envelope").getJSONObject("WARC-Header-Metadata").getString("WARC-Record-ID");
					context.write(new Text("warc_record_id parsable"), new LongWritable(1));
					String country = null;

					if(value.header.warcTargetUriUri != null) {
						String host = value.header.warcTargetUriUri.getHost();
						String tld = host.substring(host.lastIndexOf('.') + 1);
						if (tld.length() == 2 && tld.matches("[a-z]{2}")){
							if(!invalidTLDs.contains(tld)) {
								context.write(new Text("tld"), new LongWritable(1));
								country = tld;
							}
						}
					}

					if(country == null) {
						context.write(new Text("no tld"), new LongWritable(1));
						country = "";
					}
					try {
						String IP = json.getJSONObject("Envelope").getJSONObject("WARC-Header-Metadata").getString("WARC-IP-Address");
						if(IP.contains(":")) {
							context.write(new Text("IPv6"+country), new LongWritable(1));
						} else {
							context.write(new Text("IPv4"+country), new LongWritable(1));
						}
					} catch (JSONException e) {
						// Not the JSON we were looking for.. 
						logger.error(e);
						context.write(new Text("No WARC-IP-Address"), new LongWritable(1));
					}
				}
			}
		} catch(JSONException e) {
			logger.warn("Decoding JSON for key " + key.get() + " failed", e);
			context.write(new Text("Decoding JSON error"), new LongWritable(1));
		}
	}
}