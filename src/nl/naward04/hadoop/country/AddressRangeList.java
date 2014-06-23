package nl.naward04.hadoop.country;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 *  Singleton-class that populates and maintains a list of IPv4-address ranges (AddressRange-class) and allows searching through the list using a 
 *  binary search. Requires a ;-delimited iplist.csv file in the path containing non-overlapping sorted IP-ranges.
 * 
 * @author Marc Hulsebosch
 *
 */
public class AddressRangeList {
	
	//The singleton instance
	private static AddressRangeList instance;
	
	private static final Logger logger = Logger.getLogger(AddressRangeList.class);
	
	/**
	 * The array containing the list of AddressRanges. Is sorted, as the ordering from the sorted CSV-file is maintained.
	 */
	protected AddressRange[] list;
	
	//Private constructor that populates the list.
	private AddressRangeList(){
		populate();
	}
	
	/**
	 * Method for retrieving the singleton-instance of this class.
	 * @return The instance of this class.
	 */
	public static AddressRangeList getInstance(){
		if (instance == null){
			instance = new AddressRangeList();
		}
		return instance;
	}
	
	/**
	 * Reads from iplist.csv and places all lines into the list in the form of a AddressRange.
	 */
	private void populate(){
		Scanner s = null;
		InputStream csvlist = getClass().getClassLoader().getResourceAsStream("iplist.csv");
		s = new Scanner(csvlist);
		ArrayList<AddressRange>  temp= new ArrayList<AddressRange> (100000);
		while (s.hasNextLine()){
			String[] split = s.nextLine().split(";");
			temp.add(new AddressRange(Long.parseLong(split[0]),Long.parseLong(split[1]), split[2]));
		}
		list = new AddressRange[temp.size()];
		list = temp.toArray(list);
		s.close();
	}
	
	
	/**
	 * Returns the country to which the range containing the address has been assigned as a two-letter string.
	 * Uses binary search to find the desired range or returns "Unknown". The latter happens if the IP-range is not present in MaxMind's database, or if the number provided doesn't represent a valid IP-address.
	 * For example, the webserver of utwente.nl:
	 * getCountry(130.89.3.249) would return "NL".
	 * @param address The address to be found, as a long.
	 * @return The country the IP-address belongs to, or "Unknown".
	 */
	public String getCountry(long address){
		int lower = 0;
		int upper = list.length-1;
		String ret = "Unknown";
		int middle = (lower + upper)/2;
		
		while( lower <= upper ){
			//If the range starts at a higher number than the address (list[middle].getFrom() > address)
	      if ( list[middle].compareTo(address) > 0 ) {
	        lower = middle + 1;
	      	middle = (lower + upper)/2;
	      //If the range ends at a lower  number than the address (list[middle].getTo() < address)
		  } else if ( list[middle].compareTo(address) < 0) { 
	       upper = middle -1;
	       middle = (lower + upper)/2;
	       //If both of those are false, the IP should be in this range
		  } else{
	    	 ret = list[middle].getCountry();
	    	 break;
	      }
		}
		return ret;
	}
	
	/**
	 * Method for converting an IP-address to a long.
	 * @param address The address to be converted
	 * @return The IP-address as a long.
	 */
	public static long convertAddressToLong(String address){
		String[] addr = address.split("\\.");
		long ret=0;
		ret=ret+ Long.parseLong(addr[0]) * 16777216;
		ret=ret+ Long.parseLong(addr[1]) * 65536;
		ret=ret+ Long.parseLong(addr[2]) * 256;
		ret=ret+ Long.parseLong(addr[3]);
		return ret;
	}
}
