package zerobase.weather.error;

public class InvalidDate extends RuntimeException{
	private static final String MESSGAE ="invalid date";
	
	public InvalidDate() {
		super(MESSGAE);
	}
}
