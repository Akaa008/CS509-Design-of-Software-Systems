/**
 * 
 */
package CS509.client.airplane;

/**
 * This class stores information about an airplane. This class presents the
 * same information as received from the CS509 server after conversion from XML strings
 * to java primitives. Each data attribute is accessible via getters and setters
 *   
 * @author blake
 * @version 1.1
 * @since 2016-02-24
 *
 */
public class Airplane {

	/**
	 * Data attributes for an airplane as defined by the server Airplane XML
	 */
	private String mManufacturer;
	private String mModel;
	private int mFirstClassSeats;
	private int mCoachSeats;
	
	
	/**
	 * default constructor
	 * 
	 * The default ctor initializes the object instance to default / invalid values.
	 * The setter methods must be called to set each member attribute to a valid value.
	 */
	public Airplane () {
		mManufacturer = "";
		mModel = "";
		mFirstClassSeats = -1;
		mCoachSeats = -1;
	}
	
	/**
	 * constructor with all required field values supplied
	 * 
	 * This constructor will create a valid Airplane object
	 * 
	 * @param manufacturer is the manufacturer of the airplane
	 * @param model is the model of the airplane
	 * @param firstClassSeats is the number of first class seats available on the model
	 * @param coachSeats is the number of coach seats available on the model
	 */
	public Airplane (String manufacturer, String model, int firstClassSeats, int coachSeats) {
		mManufacturer = manufacturer;
		mModel = model;
		mFirstClassSeats = firstClassSeats;
		mCoachSeats = coachSeats;
	}

	/**
	 * set the manufacturer of the airplane
	 * 
	 * @param manufacturer is the manufacturer of the airplane
	 */
	public void manufacturer (String manufacturer) {
		mManufacturer = manufacturer;
	}
	/**
	 * get the manufacturer of the airplane
	 * 
	 * @return the manufacturer of the airplane
	 */
	public String manufacturer () {
		return mManufacturer;
	}
	
	/**
	 * set the model of the airplane
	 * 
	 * @param model is the model of the airplane
	 */
	public void model (String model) {
		mModel = model;
	}
	
	/**
	 * get the model of the airplane
	 * 
	 * @return the model of the airplane
	 */
	public String model () {
		return mModel;
	}
	
	/**
	 * set the number of first class seats on the model
	 * 
	 * @param firstClassSeats is the number of first class seats available on the model
	 */
	public void firstClassSeats (int firstClassSeats) {
		mFirstClassSeats = firstClassSeats;
	}
	
	/**
	 * get the number of first class seats on the model
	 * 
	 * @return the number of first class seats on the model
	 */
	public int firstClassSeats () {
		return mFirstClassSeats;
	}
	
	/** 
	 * set the number of coach seats on the model
	 * 
	 * @param coachSeats is the number of coach seats available on the model
	 */
	public void coachSeats (int coachSeats) {
		mCoachSeats = coachSeats;
	}
	
	/**
	 * get the number of coach seats on the model
	 * 
	 * @return the number of coach seats on the model
	 */
	public int coachSeats () {
		return mCoachSeats;
	}
	
}

