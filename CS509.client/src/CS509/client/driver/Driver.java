/**
 * 
 */

package CS509.client.driver;
//------------
import java.util.Scanner;
import java.text.ParseException;
import java.util.List;
//------------
import CS509.client.dao.ServerInterface;
import CS509.client.flight.Flight;
import CS509.client.flight.Flights;
import CS509.client.airport.Airports;
import CS509.client.flight.Sort;
import CS509.client.driver.FlightSearch;

/**
 * @author gokul
 *
 */
public class Driver {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		ServerInterface resSys = new ServerInterface();
		//------------
		Scanner s = new Scanner(System.in);
		String team = "TeamC";
		System.out.println("departAirportCode:");	
	    String departure = s.nextLine().toUpperCase();
		System.out.println("departTime (YYYY_MM_DD) B/W. 2023_05_06 and 2023_05_31:");
		String time = s.nextLine();
		System.out.println("arrivalAiportCode:");
		String arrival = s.nextLine().toUpperCase();
		System.out.println("Trip Type (oneway, round):");
		String trip = s.nextLine().toUpperCase();
		System.out.println("Seating Class (Coach, FirstClass): ");
		String sclass = s.nextLine().toUpperCase();
		System.out.println("Number of Seats :");
		int seats = s.nextInt();
		s.nextLine(); 
		//When s.nextInt() is called, it reads only the integer value and leaves the newline character in the input buffer.
		//When s.nextLine() is called after s.nextInt(), it reads the newline character and stops, resulting in no input being 
		//taken from the user. To resolve this issue, an extra s.nextLine() call after s.nextInt() is added to consume the 
		//newline character from the input buffer.

		boolean coach = true; //1 for coach and 0 for FirstClass
		// Try to get a list of airports
		String xmlAirport = resSys.getAirports(team);
		Airports airports = new Airports();
		airports.addAll(xmlAirport);
		if (sclass.equals("FIRSTCLASS")) {
			coach = false;			
		}
		if (trip.equals("ONEWAY")) { //if oneway, the search is done for fights on the first day 
			searchFlight(team,departure,arrival,time,seats,coach);}
		//if roundtrip, the search is done for fights on the first day and a new arrival day is obtained, which is used by the search flight 
		//function to find the rturn flights.
		else if(trip.equals("ROUND")){ 
			System.out.println("ArrivalTime (YYYY_MM_DD) B/W. 2023_05_06 and 2023_05_31: ");
			String arrtime = s.nextLine();			
			searchFlight(team,departure,arrival,time,seats,coach);
			System.out.println("********************************************************************************************");
			System.out.println("Return Trip");
			searchFlight(team,arrival,departure,arrtime,seats,coach);			
			
		}
		
		
	}
	public static void searchFlight(String team, String departure,String arrival, String time,int seats, boolean coach) throws ParseException {
// ----------------------------------------WITHOUT ANY LAYOVERS-----------------------------------------
		System.out.println("Without Layovers");
		List<Flights> flightli = FlightSearch.searchFlightsWithNoStop(team,departure,arrival,time,seats, coach);
		FlightSearch.display(flightli);
//		
// ------------------------------------------WITH  ONE  LAYOVERS----------------------------------------
		System.out.println("With One Layover");
		List<Flights> flightlis = FlightSearch.searchFlightsWithOneStop(team,departure,arrival,time,seats,coach);
		FlightSearch.display(flightlis);
//		
// ------------------------------------------WITH  TWO  LAYOVERS----------------------------------------
		System.out.println("With Two Layover");
		List<Flights> flightlist = FlightSearch.searchFlightsWithTwoStop(team,departure,arrival,time,seats,coach);
		FlightSearch.display(flightlist);

	}
}	
		
	// 	//try to reserve a coach seat on one of the flights
	// 	Flight flight = flights.get(0);
	// 	String flightNumber = flight.getmNumber();
	// 	int seatsReservedStart = flight.getmSeatsCoach();
		
	// 	String xmlReservation = "<Flights>"
	// 			+ "<Flight number=\"" + flightNumber + " seating=\"Coach\" />"
	// 			+ "</Flights>";
		
		
	// 	// Try to lock the database, purchase ticket and unlock database
	// 	resSys.lock("WorldPlaneInc");
	// 	resSys.buyTickets("WorldPlaneInc", xmlReservation);
	// 	resSys.unlock("WorldPlaneInc");
		
	// 	// Verify the operation worked
	// 	xmlFlights = resSys.getFlights("WorldPlaneInc", "BOS", "2016_05_10");
	// 	// System.out.println(xmlFlights);
	// 	flights.clear();
	// 	flights.addAll(xmlFlights);
		
	// 	// Find the flight number just updated
	// 	int seatsReservedEnd = seatsReservedStart;
	// 	for (Flight f : flights) {
	// 		String tmpFlightNumber = f.getmNumber();
	// 		if (tmpFlightNumber.equals(flightNumber)) {
	// 			seatsReservedEnd = f.getmSeatsCoach();
	// 			break;
	// 		}
	// 	}
	// 	if (seatsReservedEnd == (seatsReservedStart + 1)) {
	// 		System.out.println("Seat Reserved Successfully");
	// 	} else {
	// 		System.out.println("Reservation Failed");
	// 	}
		