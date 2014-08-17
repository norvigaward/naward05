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
package nl.surfsara.warcexamples.hadoop.rr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.jwat.common.Payload;
import org.jwat.warc.WarcRecord;


class RRMapper extends Mapper<LongWritable, WarcRecord, Text, Text> {
	private static final Logger logger = Logger.getLogger(RRMapper.class);
	
    private static final int CAPACITY = 10000000;
    private static Map<String,String[]> songMap= new HashMap<String,String[]>(CAPACITY);
	private static ExactDictionaryChunker dictionaryChunkerFT;
	private static List<String> results;
	
	private static enum Counters {
		CURRENT_RECORD, NUM_TEXT_RECORDS, NUM_NO_IP, NUM_SONGS_DETECTED
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		
        logger.info("Setup initialized");
        
		// SETUP
		// Build a song - artist(s) hashmap && Dictionary
		MapDictionary<String> dictionary = new MapDictionary<String>();
		
	    String song = "";  
	    String line = "";
	    
	    String recordArtistListURL = "/nl/surfsara/warcexamples/hadoop/rr/resources/recordartists.txt";

	    BufferedReader input = new BufferedReader(new InputStreamReader(RRMapper.class.getResourceAsStream(recordArtistListURL)));
	    
        while (null != (line = input.readLine())) {
            String[] splitLine= line.split(";",2);
            song = splitLine[0].trim();
            song = song.substring(0, Math.min(song.length(), 200));
            if(song.length()>2 && !songMap.containsKey(song)){
            	
	            dictionary.addEntry(new DictionaryEntry<String>(song.toLowerCase(),song));
            	String[] artists = splitLine[1].split("\t");
	            
	        	songMap.put(song, removeElements(artists));
	        	
            }    
        }
        input.close();

        logger.info("Dictionary build-up completed");
        
        // No substring matching and capital insensitive
	    dictionaryChunkerFT = new ExactDictionaryChunker(dictionary,
                                     IndoEuropeanTokenizerFactory.INSTANCE,
                                     false,false);
	    
        dictionary = null;
	    
        logger.info("ExactDictionaryChunker build-up completed");
		
	}

	@Override
	public void map(LongWritable key, WarcRecord value, Context context) throws IOException, InterruptedException {
		context.setStatus(Counters.CURRENT_RECORD + ": " + key.get());
		
		// Only process text/plain content
		if ("text/plain".equals(value.header.contentTypeStr) ) {
			context.getCounter(Counters.NUM_TEXT_RECORDS).increment(1);
			// Get the text payload
			Payload payload = value.getPayload();
			
			
			
			
			if (payload == null) {
				// NOP
			} else {

				String warcContent = IOUtils.toString(payload.getInputStreamComplete());
				
				if (warcContent == null && "".equals(warcContent)) {
					// NOP
				} else {
					
					// warcContent is the text content of a site. Do something with it!
					results = new ArrayList<String>();
					
					Chunking chunking = dictionaryChunkerFT.chunk(warcContent);
				    for (Chunk chunk : chunking.chunkSet()) {
			            int start = chunk.start();
			            int end = chunk.end();
			            String type = chunk.type();
			            String recording = warcContent.substring(start,end);
			            if(recording.length()>1){
			                String[] correspondingArtist = songMap.get(recording);
//			                
//			                System.out.println("Recording: "+recording);
//			                System.out.println(Arrays.toString(correspondingArtist));
			                
			                if(null != correspondingArtist && correspondingArtist.length>0){
			                    for(String artist : correspondingArtist){
			                    	
			                    	String contentSubstring = warcContent.substring(Math.max(0,start-300),start) + warcContent.substring(end, Math.min(end+300, warcContent.length()));
			                    	
			                    	// 
			                    	// contentSubstring.toLowerCase().contains(artist.toLowerCase()
			                    	// 		System.out.println(tmp.matches(".*\\bpro\\b.*"));
			                    	if(null != contentSubstring && null != artist){
			                    		if(contentSubstring.toLowerCase().contains(artist.toLowerCase())){
			                    			
			                    			contentSubstring = contentSubstring.toLowerCase().replaceAll("(\\r|\\n)", " ");
			                    			
			                    			if(contentSubstring.matches(".*\\b"+artist.toLowerCase()+"\\b.*")){
				                    			String resultKey = type+";"+artist;
					                    		
					                    		context.getCounter(Counters.NUM_SONGS_DETECTED).increment(1);
					                    		
					                            if(!results.contains(resultKey)){
//					                            	System.out.println(resultKey);
					                            	results.add(resultKey);
					                            	context.write(new Text(value.header.warcRefersToStr), new Text(resultKey));
					                            } 
			                    			} else {
			                    				logger.info(artist+", "+contentSubstring);
			                    			}

			                    		}
			                    		
			                    	}
			                    }
			                }
			            }
			        }
					
					
					
				}
			}
		}
	}
	
	public static String[] removeElements(String[] input) {
	    List<String> result = new LinkedList<String>();

	    for(String item : input)
	        if(item.length()>1)
	            result.add(item);

	    return (String[]) result.toArray(input);
	}

}
