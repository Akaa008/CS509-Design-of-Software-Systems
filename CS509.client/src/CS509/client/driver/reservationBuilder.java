package CS509.client.driver;
import CS509.client.flight.*;
import CS509.client.dao.*;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
/**
 * 
 * @author Joe
 * @author Gokul
 *
 */
public class reservationBuilder {
	
	public enum flDirection{
		INBOUND,
		OUTBOUND;
	}
	
	String team = "TeamC";//OOF check this constant.
	
	List<Flights> onewayFlights = new ArrayList<Flights>();
	List<Flights> DepartureFlights = new ArrayList<Flights>();
	List<Flights> ArrivalFlights = new ArrayList<Flights>();
	
	Trips outboundTrips = new Trips();
	Trips inboundTrips = new Trips();
	
	Trip outboundTrip;
	Trip inboundTrip;
	
//	Calendar cal = GregorianCalendar.getInstance();
	
	@SuppressWarnings("deprecation")
	Date earliestDate = new Date(2023,05,06);
	@SuppressWarnings("deprecation")
	Date latestDate = new Date(2023,05,31);
	
	ArrayList<Date> outboundDates = new ArrayList<Date>();
	ArrayList<Date> inboundDates = new ArrayList<Date>();
	
	//We should have proper data types here...
	String departureCode = new String();
	String arrivalCode = new String();
	String departureDate = new String();
	String arrivalDate = new String();
	String tripType = new String();
	String seatClass = new String();
	Boolean coach = false;
	Boolean firstClass = false;
	Boolean roundTrip = false;
	Boolean oneWay = false;
	int seats = 1; //does the professor ask for multiple seat reservation ability?
	private static final DecimalFormat df = new DecimalFormat("0.00");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm z");
	/**
	 * Searches flights 
	 * @throws ParseException
	 */
	public void searchFlights() throws ParseException {
		//Have to add handling of MULTIPLE departure dates (windows)
				DepartureFlights.addAll(FlightSearch.searchFlightsWithNoStop(team,departureCode,arrivalCode,departureDate,seats, coach));
				DepartureFlights.addAll(FlightSearch.searchFlightsWithOneStop(team,departureCode,arrivalCode,departureDate,seats, coach));
				DepartureFlights.addAll(FlightSearch.searchFlightsWithTwoStop(team,departureCode,arrivalCode,departureDate,seats, coach));
				
				if(roundTrip) {
					ArrivalFlights.addAll(FlightSearch.searchFlightsWithNoStop(team,arrivalCode,departureCode,arrivalDate,seats, coach));
					ArrivalFlights.addAll(FlightSearch.searchFlightsWithOneStop(team,arrivalCode,departureCode,arrivalDate,seats, coach));
					ArrivalFlights.addAll(FlightSearch.searchFlightsWithTwoStop(team,arrivalCode,departureCode,arrivalDate,seats, coach));
//					System.out.println(ArrivalFlights.size());
				}
			}
	/**
	 * 
	 */
	public void makeTrips() {
		try {
			this.outboundTrips.addAll(this.DepartureFlights);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.roundTrip) {
			this.inboundTrips.addAll(this.ArrivalFlights);
		}
	}
	/**
	 * Displays Departure Flights
	 */
	public void showDepartureFlights() {
		Trip.seatType seatType;
		
		if(this.coach) {
			seatType = Trip.seatType.COACH;
		}
		else {
			seatType = Trip.seatType.FIRST;
		}
		outboundTrips.displayAll(seatType);
	}
	/**
	 * Displays Arrival Flights.
	 */
	public void showArrivalFlights() {
		Trip.seatType seatType;
		
		if(this.coach) {
			seatType = Trip.seatType.COACH;
		}
		else {
			seatType = Trip.seatType.FIRST;
		}
		inboundTrips.displayAll(seatType);
	}
	/**
	 * 
	 * @param index
	 */
	public void selectDeparture(Integer index) {
		this.outboundTrip = outboundTrips.get(index);
	}
	/**
	 * 
	 * @param index
	 */
	public void selectArrival(Integer index) {
		this.inboundTrip = inboundTrips.get(index);
	}
/**
 * 
 * @param departureCode
 */
	public void setDepartureCode(String departureCode) {
		this.departureCode = departureCode;
	}
/**
 * 
 * @param arrivalCode
 */
	public void setArrivalCode(String arrivalCode) {
		this.arrivalCode = arrivalCode;
	}
	/**
	 * 
	 * @param departureDate
	 */
	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}
	/**
	 * 
	 * @param arrivalDate
	 */
	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	/**
	 * 
	 * @param tripType
	 */
	public void setTripType(String tripType) {
		this.tripType = tripType;
		if (tripType.equalsIgnoreCase("oneway")) {
			this.roundTrip = false;
			this.oneWay = true;
		} else if(tripType.equalsIgnoreCase("round")) {
			this.oneWay = false;
			this.roundTrip = true;
		}
		else {
			System.out.println("Caught Error in tripType.");
			this.oneWay = false;
			this.roundTrip = false;
		}
	}
	/**
	 * 
	 * @param seatClass
	 */
	public void setSeatClass(String seatClass) {
		this.seatClass = seatClass;
		if (seatClass.equalsIgnoreCase("firstclass")) {
			this.coach = false;
			this.firstClass = true;
		} else if(seatClass.equalsIgnoreCase("coach")) {
			this.firstClass = false;
			this.coach = true;
		}
		else {
			System.out.println("Caught Error in Seat Class.");
			this.firstClass = false;
			this.coach = false;
		}
	}
	/**
	 * 
	 */
	public void flushReservation() {
		this.DepartureFlights.removeAll(DepartureFlights);
		this.ArrivalFlights.removeAll(ArrivalFlights);
		this.outboundTrips.removeAll(outboundTrips);
		this.inboundTrips.removeAll(inboundTrips);
	}
	/**
	 * 
	 */
	public void displayReservation() {
		Trip.seatType seatType;
		double reservationCost = 0d;
		
		if(this.coach) {
			seatType = Trip.seatType.COACH;
		}
		else {
			seatType = Trip.seatType.FIRST;
		}
		
		System.out.println("********************************** PENDING RESERVATION *************************************");
		System.out.println("********************************************************************************************");
		if(this.outboundTrips.size() > 1) {
		System.out.println("**********************************   OUTBOUND FLIGHTS  *************************************");
		}else {
		System.out.println("**********************************   OUTBOUND FLIGHT   *************************************");	
		
		}
		
		this.outboundTrip.displayTrip(seatType);
		reservationCost += this.outboundTrip.getPrice(seatType);
		
		if(this.roundTrip) {
			if(this.inboundTrips.size() > 1) {
				System.out.println("**********************************   INBOUND FLIGHTS   *************************************");
				}else {
				System.out.println("**********************************   INBOUND FLIGHT    *************************************");	
				}
			this.inboundTrip.displayTrip(seatType);
			reservationCost += this.inboundTrip.getPrice(seatType);
		}
		System.out.println("******************************* PENDING RESERVATION ABOVE ***********************************");
		System.out.println("******************************* TOTAL COST: $" + df.format(reservationCost)+" ***************************************");
		System.out.println("*********************************************************************************************");
	}
//	==============================================================
	/**
	 * 
	 * @return
	 */
	public boolean reserve() {
//		tRIPS SEEMS TO BE A LIST, SO USE FOR LOOP FOR EVERY FLIGHT IN TRIP, RUN THE RESERVATION CODE. 
		boolean complete = false;
		boolean completereturn=true;
		Flights flights1 = this.outboundTrip.getTripFlights();
		
		ServerInterface resSys = new ServerInterface();
		if(reserveFlight(flights1, this.seatClass)) {
			complete = true;
		}
		
		if(this.roundTrip) {
			//book flights in inbound
			completereturn = false;
			Flights flights2 = this.inboundTrip.getTripFlights();
			if(reserveFlight(flights2, this.seatClass)) {
				completereturn = true;
			}
		
		}
		return (complete && completereturn);
	}
	
	/**
	 * 
	 * @param fl
	 */
//	====================================================================
	public void sortByPrice(flDirection fl) {
		Trip.seatType seatType;
		if(this.coach) {
			seatType = Trip.seatType.COACH;
		}
		else {
			seatType = Trip.seatType.FIRST;
		}
		
		Comparator<Trip> compareByPrice = new Comparator<Trip>(){
			@Override
			public int compare(Trip t1, Trip t2) {
				return t1.getPrice(seatType).compareTo(t2.getPrice(seatType));
			}
		};
		
		if(fl == flDirection.INBOUND) {
			inboundTrips.sort(compareByPrice);
		}else if (fl == flDirection.OUTBOUND) {
			outboundTrips.sort(compareByPrice);
		}
		else {
			System.out.println("Not sorted oof");
		}

	}
	/**
	 * 
	 * @param fl
	 */
	public void sortByDepartureDate(flDirection fl) {
		Comparator<Trip> compareByDepartureDate = new Comparator<Trip>(){
			@Override
			public int compare(Trip t1, Trip t2) {
				return t1.getDepartureDate().compareTo(t2.getDepartureDate());
			}
		};
		
		if(fl == flDirection.INBOUND) {
			inboundTrips.sort(compareByDepartureDate);
		}else if (fl == flDirection.OUTBOUND) {
			outboundTrips.sort(compareByDepartureDate);
		}
		else {
			System.out.println("Not sorted oof");
		}
		
	}
	/**
	 * 
	 * @param fl
	 */
	public void sortByArrivalDate(flDirection fl) {
		Comparator<Trip> compareByArrivalDate = new Comparator<Trip>(){
			@Override
			public int compare(Trip t1, Trip t2) {
				return t1.getArrivalDate().compareTo(t2.getArrivalDate());
			}
		};
		
		if(fl == flDirection.INBOUND) {
			inboundTrips.sort(compareByArrivalDate);
		}else if (fl == flDirection.OUTBOUND) {
			outboundTrips.sort(compareByArrivalDate);
		}
		else {
			System.out.println("Not sorted oof");
		}
	}
	/**
	 * 
	 * @param fl
	 */
	public void sortByTripLength(flDirection fl) {
		Comparator<Trip> compareByTripLength = new Comparator<Trip>(){
			@Override
			public int compare(Trip t1, Trip t2) {
				return t1.getTripLength().compareTo(t2.getTripLength());
			}
		};
		
		if(fl == flDirection.INBOUND) {
			inboundTrips.sort(compareByTripLength);
		}else if (fl == flDirection.OUTBOUND) {
			outboundTrips.sort(compareByTripLength);
		}
		else {
			System.out.println("Not sorted oof");
		}
	}
	/**
	 * 
	 * @param in
	 * @return
	 */
	public boolean validDate(String in) {
		Date checkDate = new Date();
		return true;
//		try {
//			checkDate = sdf.parse(in);
//		} catch (ParseException e) {
//			return false;
//		}
//		
//		if(checkDate.before(earliestDate)) {
//			System.out.println("Date is too early!");
//			return false;
//		}else if(checkDate.after(latestDate)) {
//			System.out.println("Date is too late!");
//			return false;
//		}else {
//			return true;
//		}
	}
	/**
	 * Creates the XML to be used to reserve flights
	 * @param flightList
	 * @param seatClass
	 * @return xml in the following format
	 * <pre>{@code
	 * <Flights>
         <Flight number=DDDDD seating=SEAT_TYPE/>
	        <Flight number=DDDDD seating=SEAT_TYPE/>
     </Flights>
	 * }</pre>
	 */
	public String getXML(ArrayList<Flight> flightList, String seatClass) {
		 
     
        String head = "<Flights>";
        String end = "</Flights>";
        String middle = "";
        String SEAT_TYPE = "";

        if(seatClass.equalsIgnoreCase("coach")) {
            SEAT_TYPE = "Coach";
        } else if (seatClass.equalsIgnoreCase("firstClass")) {
            SEAT_TYPE = "FirstClass";
        }

        for(Flight f:flightList){
            String number = f.getmNumber();
            middle += "<Flight number=\"" + number + "\" seating=\"" + SEAT_TYPE + "\"/>";
        }
        String res = head + middle + end;
        return res;
    }
	/**
	 * Reserves multiple flights simultaneously
	 * @param flightList
	 * @param seatClass
	 * @return true if reserved succesfully
	 */
	public boolean reserveFlight(ArrayList<Flight> flightList, String seatClass) {
        boolean isLocked = false;
        boolean isReserved = false;
        boolean isUnlocked = true;
        String xmlFlights;

        // lock server
        isLocked = ServerInterface.lock("TeamC");

        // if server locked
        // update server
        if (isLocked) {
            isUnlocked = false;
            xmlFlights = getXML(flightList, seatClass);
            isReserved = ServerInterface.buyTickets("TeamC", xmlFlights);

            // if reservation is successful
            // unlock server
            if (isReserved) {
                isUnlocked = ServerInterface.unlock("TeamC");
               
            }
            // if not, also unlock, and ask to try again.
            else {
                isUnlocked = ServerInterface.unlock("TeamC");
                System.out.println("Please try again.");
                
            }

        }
        return isReserved;
    }
}
