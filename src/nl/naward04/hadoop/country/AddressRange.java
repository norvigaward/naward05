package nl.naward04.hadoop.country;

public class AddressRange implements Comparable<AddressRange> {
	
	private long from;
	private long to;
	private String country;
	
	public AddressRange(long from, long to, String country){
		this.from 		= 	from;
		this.to 		=	to;
		this.country	=	country;
	}

	public long getFrom() {
		return from;
	}

	public long getTo() {
		return to;
	}

	public String getCountry() {
		return country;
	}

	@Override
	/**
	 * Method for comparing to AddressRanges. This method assumes that ranges do not overlap, as it checks the starting point of ranges.
	 */
	public int compareTo(AddressRange supplied) {
		return ((Long) this.getFrom()).compareTo(supplied.getFrom());
	}
	
	public long compareTo(Long address){
		long ret=0;
		if (address < from){
			ret = -1;
		} else if (address > to){
			ret = 1;
		}
		return ret;
		
	}
	
	public boolean contains(long address){
		return (address >= from && address <= to);
	}
	
	

	
	

}
