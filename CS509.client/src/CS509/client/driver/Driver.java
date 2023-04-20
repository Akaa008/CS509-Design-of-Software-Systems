package CS509.client.driver;

import CS509.client.dao.ServerInterface;
import CS509.client.driver.reservationBuilder.flDirection;
import CS509.client.flight.*;
import CS509.client.airport.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;

import CS509.client.airport.*;

/**
 * @author Gokul
 * @author Akaash
 * @author Evan
 * @author Joe
 */
public class Driver {
	public enum State{
		WELCOME,
		BOOK_A_FLIGHT,
		GET_DEP_AIRPORT,
		GET_DEP_DATES,
		GET_ARR_AIRPORT,
		GET_ARR_DATES,
		GET_ROUND_TRIP,
		GET_CLASS,
		SEARCH,
		DISPLAY_DEP_FLIGHTS,
		SELECT_DEP_FLIGHTS,
		DISPLAY_ARR_FLIGHTS,
		SELECT_ARR_FLIGHTS,
		REVIEW_TRIP,
		RESERVE_FLIGHTS,
		CONFIRM_RESERVATION,
		EXIT_APP
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ServerInterface resSys = new ServerInterface();
		reservationBuilder resBuild = new reservationBuilder();
		TimeConversion converter = new TimeConversion("timezone.csv");
		Scanner s = new Scanner(System.in);
		String team = "TeamC";
		State uiState = State.WELCOME;
		String stringIn = new String();
		Airports airports = new Airports();
		boolean justSorted = false;
		//------------

		boolean appOn = true;
		
		while(appOn) {
		switch(uiState) {
			case WELCOME:
				System.out.println("Welcome to Team C's Application!");
				System.out.println("Enter X at any point to exit the application, and R to return to welcome.");
				//Ideally build a "nextState()" method.
				uiState = State.BOOK_A_FLIGHT;
				break;
				
			case BOOK_A_FLIGHT:
				System.out.println("Would you like to build a flight?");
				System.out.println("Please enter Y or N for selection.");
				stringIn = s.nextLine().toUpperCase();
					if(stringIn.equalsIgnoreCase("Y")) {
						uiState = State.GET_DEP_AIRPORT;
					}else if(stringIn.equalsIgnoreCase("N")) {
						uiState = State.EXIT_APP;
					}else if(stringIn.equalsIgnoreCase("X")) {
						uiState = State.EXIT_APP;
					}else if(stringIn.equalsIgnoreCase("R")) {
						uiState = State.WELCOME;
					}else {
						System.out.println("Improperly formed input. Please review the directions.");
					}
				break;
				
			case GET_DEP_AIRPORT:
				System.out.println("Finding Airports available...:");
				String xmlAirport = resSys.getAirports(team);
				airports.addAll(xmlAirport);
				Flight.setmAirports(airports);
				System.out.println("Found Aiports!");
				System.out.println("Ready for input. Enter 3-character departure Airport code:");	
				stringIn = s.nextLine().toUpperCase();
					if(airports.containsCode(stringIn)) {
						resBuild.setDepartureCode(stringIn);
						uiState = State.GET_DEP_DATES;
					}else if(stringIn.equalsIgnoreCase("X")) {
						uiState = State.EXIT_APP;
					}else if(stringIn.equalsIgnoreCase("R")) {
						uiState = State.WELCOME;
					}else {
						System.out.println("Improperly formed input. Please review the directions.");
					}
				break;
				
			case GET_DEP_DATES:
				System.out.println("departTime (YYYY_MM_DD) B/W. 2023_05_06 and 2023_05_31:");
				stringIn = s.nextLine();
				//Here is where we can introduce the selection for multiple dates!!!!!!!!!!!
				//Return to state with added wording.
				//Also add handling on the input code.
					if(resBuild.validDate(stringIn)) {
						resBuild.setDepartureDate(stringIn);
						uiState = State.GET_ARR_AIRPORT;
					}else if(stringIn.equalsIgnoreCase("X")) {
						uiState = State.EXIT_APP;
					}else if(stringIn.equalsIgnoreCase("R")) {
						uiState = State.WELCOME;
					}else {
						System.out.println("Improperly formed input. Please review the directions.");
					}
				break;
				
			case GET_ARR_AIRPORT:
				System.out.println("arrivalAiportCode:");
				stringIn = s.nextLine().toUpperCase();
					if(airports.containsCode(stringIn)) {
						resBuild.setArrivalCode(stringIn);
						uiState = State.GET_ROUND_TRIP;
					}else if(stringIn.equalsIgnoreCase("X")) {
						uiState = State.EXIT_APP;
					}else if(stringIn.equalsIgnoreCase("R")) {
						uiState = State.WELCOME;
					}else {
						System.out.println("Improperly formed input. Please review the directions.");
					}
				break;
				
			case GET_ARR_DATES:
				System.out.println("ArrivalTime (YYYY_MM_DD) B/W. 2023_05_06 and 2023_05_31: ");
				stringIn = s.nextLine();
					if(resBuild.validDate(stringIn)) {
						resBuild.setArrivalDate(stringIn);
						System.out.println("Would you like to add another date to search?");
						stringIn = s.nextLine();
						uiState = State.GET_CLASS;
					}else if(stringIn.equalsIgnoreCase("X")) {
						uiState = State.EXIT_APP;
					}else if(stringIn.equalsIgnoreCase("R")) {
						uiState = State.WELCOME;
					}else {
						System.out.println("Improperly formed input. Please review the directions.");
					}
				//Same note as above in GET_DEP_DATES state. Implement multiple dates of arrival/departure.
				break;
				
			case GET_ROUND_TRIP:
				System.out.println("Trip Type (oneway, round):");
				stringIn = s.nextLine().toUpperCase();
				resBuild.setTripType(stringIn);
					if(resBuild.oneWay) {
						uiState = State.GET_CLASS;
					}else if(resBuild.roundTrip) {
						uiState = State.GET_ARR_DATES;
					}else if(stringIn.equalsIgnoreCase("X")) {
						uiState = State.EXIT_APP;
					}else if(stringIn.equalsIgnoreCase("R")) {
						uiState = State.WELCOME;
					}else {
						System.out.println("Improperly formed input. Please review the directions.");
					}
				break;
				
			case GET_CLASS:
				System.out.println("Seating Class (Coach, FirstClass): ");
				stringIn = s.nextLine().toUpperCase();
				resBuild.setSeatClass(stringIn);
				if(resBuild.coach || resBuild.firstClass) {
					uiState = State.SEARCH;
				}else if(stringIn.equalsIgnoreCase("X")) {
					uiState = State.EXIT_APP;
				}else if(stringIn.equalsIgnoreCase("R")) {
					uiState = State.WELCOME;
				}else {
					System.out.println("Improperly formed input. Please review the directions.");
				}
				break;
				
			case SEARCH:
				System.out.println("Searching for flights now!");
				resBuild.flushReservation();
				try {
					resBuild.searchFlights();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				resBuild.makeTrips();
				uiState = State.DISPLAY_DEP_FLIGHTS;
				break;
				
			case DISPLAY_DEP_FLIGHTS:
				System.out.println("Here are your available DEPARTURE flights:");
				resBuild.showDepartureFlights();
				uiState = State.SELECT_DEP_FLIGHTS;
				break;
				
			case SELECT_DEP_FLIGHTS:
				if(justSorted) {
					System.out.println("");
					System.out.println("Flights have been sorted!");
					System.out.println("");
					justSorted = false;
				}
				
				System.out.println("Please review the departure flights above, and make a selection using the (#).");
				System.out.println("Otherwise, request to sort by:");
				System.out.println("Price: P");
				System.out.println("Departure Time: D");
				System.out.println("Arrival Time: A");
				System.out.println("Length of Trip: L");
				
				stringIn = s.nextLine();
				Integer index = 0;
				Boolean validIndex = false;
				
				try {
					index = Integer.parseInt(stringIn);
					validIndex = true;
				}
				catch(NumberFormatException e){
					validIndex = false;
				}
				
				if(validIndex) {
					resBuild.selectDeparture(index);
					if(resBuild.roundTrip) {
					uiState = State.DISPLAY_ARR_FLIGHTS;
					}else if(true){
						uiState = State.REVIEW_TRIP;
					}
				}else if(stringIn.equalsIgnoreCase("P")) {
					uiState = State.DISPLAY_DEP_FLIGHTS;
					justSorted = true;
					resBuild.sortByPrice(flDirection.OUTBOUND);
				}else if(stringIn.equalsIgnoreCase("D")) {
					uiState = State.DISPLAY_DEP_FLIGHTS;
					justSorted = true;
					resBuild.sortByDepartureDate(flDirection.OUTBOUND);
				}else if(stringIn.equalsIgnoreCase("A")) {
					uiState = State.DISPLAY_DEP_FLIGHTS;
					justSorted = true;
					resBuild.sortByArrivalDate(flDirection.OUTBOUND);
				}else if(stringIn.equalsIgnoreCase("L")) {
					uiState = State.DISPLAY_DEP_FLIGHTS;
					justSorted = true;
					resBuild.sortByTripLength(flDirection.OUTBOUND);
				}else if(stringIn.equalsIgnoreCase("X")) {
					uiState = State.EXIT_APP;
				}else if(stringIn.equalsIgnoreCase("R")) {
					uiState = State.WELCOME;
				}else {
					System.out.println("Improperly formed input. Please review the directions.");
				}
				break;
				
			case DISPLAY_ARR_FLIGHTS:
				System.out.println("Here are your available ARRIVAL flights:");
				resBuild.showArrivalFlights();
				uiState = State.SELECT_ARR_FLIGHTS;
				break;
				
			case SELECT_ARR_FLIGHTS:
				if(justSorted) {
					System.out.println("");
					System.out.println("Flights have been sorted!");
					System.out.println("");
					justSorted = false;
				}
				System.out.println("Please review the arrival flights above, and make a selection using the (#).");
				System.out.println("Otherwise, request to sort by:");
				System.out.println("Price: P");
				System.out.println("Departure Time: D");
				System.out.println("Arrival Time: A");
				System.out.println("Length of Trip: L");
				
				stringIn = s.nextLine();
				index = 0;
				validIndex = false;
				
				try {
					index = Integer.parseInt(stringIn);
					validIndex = true;
				}
				catch(NumberFormatException e){
					validIndex = false;
				}
				
				if(validIndex) {
					resBuild.selectArrival(index);
					uiState = State.REVIEW_TRIP;
				}else if(stringIn.equalsIgnoreCase("P")) {
					uiState = State.DISPLAY_ARR_FLIGHTS;
					justSorted = true;
					resBuild.sortByPrice(flDirection.INBOUND);
				}else if(stringIn.equalsIgnoreCase("D")) {
					uiState = State.DISPLAY_ARR_FLIGHTS;
					justSorted = true;
					resBuild.sortByDepartureDate(flDirection.INBOUND);
				}else if(stringIn.equalsIgnoreCase("A")) {
					uiState = State.DISPLAY_ARR_FLIGHTS;
					justSorted = true;
					resBuild.sortByArrivalDate(flDirection.INBOUND);
				}else if(stringIn.equalsIgnoreCase("L")) {
					uiState = State.DISPLAY_ARR_FLIGHTS;
					justSorted = true;
					resBuild.sortByTripLength(flDirection.INBOUND);
				}else if(stringIn.equalsIgnoreCase("X")) {
					uiState = State.EXIT_APP;
				}else if(stringIn.equalsIgnoreCase("R")) {
					uiState = State.WELCOME;
				}else {
					System.out.println("Improperly formed input. Please review the directions.");
				}
				break;
				
			case REVIEW_TRIP:
				System.out.println("Please review your pending reservation below!");
				resBuild.displayReservation();
				uiState = State.RESERVE_FLIGHTS;
				break;
				
			case RESERVE_FLIGHTS:
				System.out.println("");
				System.out.println("Would you like to confirm this reservation?");
				System.out.println("Please enter Y to reserve, N to return to search results, R to return to welcome, and X to exit.");
				
				stringIn = s.nextLine();
				if(stringIn.equalsIgnoreCase("Y")) {
					uiState = State.CONFIRM_RESERVATION;
				}else if(stringIn.equalsIgnoreCase("N")) {
					uiState = State.DISPLAY_DEP_FLIGHTS;
				}else if(stringIn.equalsIgnoreCase("X")) {
					uiState = State.EXIT_APP;
				}else if(stringIn.equalsIgnoreCase("R")) {
					uiState = State.WELCOME;
				}else {
					System.out.println("Improperly formed input. Please review the directions.");
				}
				break;
				
			case CONFIRM_RESERVATION:
				if(resBuild.reserve()) {
//					System.out.println("");
					System.out.println("Reservation confirmed!");
					System.out.println("Please enter R to return to welcome, or X to exit.");
				}
				else {
					System.out.println("Reservation failed!");
					System.out.println("Please choose a different flight");
					System.out.println("Please enter R to return to welcome, or X to exit.");
				}
				stringIn = s.nextLine();
				if(stringIn.equalsIgnoreCase("X")) {
					uiState = State.EXIT_APP;
				}else if(stringIn.equalsIgnoreCase("R")) {
					uiState = State.WELCOME;
				}else {
					System.out.println("Improperly formed input. Please review the directions.");
				}
				break;
				
			case EXIT_APP:
				System.out.println("Thank you for using Team C's Flight Application, Goodbye!");
				appOn = false;
				break;
				
			default:
				System.out.println("Default'd, how'd you mess that up?");
				break;
			}
		}
		s.close();
	}

}

///**
// * 
// */
//
//package CS509.client.driver;
////------------
//import java.util.Scanner;
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.List;
////------------
//import CS509.client.dao.ServerInterface;
//import CS509.client.flight.Flight;
//import CS509.client.flight.Flights;
//import CS509.client.airport.Airports;
//import CS509.client.flight.Sort;
//import CS509.client.driver.FlightSearch;
//import CS509.client.airport.TimeConversion;
//
///**
// * @author gokul
// *
// */
//public class Driver {
//
//	/**
//	 * @param args
//	 * @throws ParseException 
//	 * @throws IOException 
//	 */
//	public static void main(String[] args) throws ParseException, IOException {
//		ServerInterface resSys = new ServerInterface();
//		TimeConversion converter = new TimeConversion("timezone.csv");
//		//------------
//		Scanner s = new Scanner(System.in);
//		String team = "TeamC";
//		
//		String xmlAirport = resSys.getAirports(team);
//		Airports airports = new Airports();
//		airports.addAll(xmlAirport);
////		TimeConversion.getZones(airports);
////		System.out.println(TimeConversion.loctime(airports,"2023 May 06 14:44 GMT","HNL"));
//		Flight.setmAirports(airports);
////		//=====================================================
//		System.out.println("departAirportCode:");	
//	    String departure = s.nextLine().toUpperCase();
//		System.out.println("departTime (YYYY_MM_DD) B/W. 2023_05_06 and 2023_05_31:");
//		String time = s.nextLine();
//		System.out.println("arrivalAiportCode:");
//		String arrival = s.nextLine().toUpperCase();
//		System.out.println("Trip Type (O:oneway, R:round):");
//		String trip = s.nextLine().toUpperCase();
//		System.out.println("Seating Class (Coach, FirstClass): ");
//		String sclass = s.nextLine().toUpperCase();
//		System.out.println("Number of Seats :");
//		int seats = s.nextInt();
//		s.nextLine(); 
////		//=======================================================
//
//		//When s.nextInt() is called, it reads only the integer value and leaves the newline character in the input buffer.
//		//When s.nextLine() is called after s.nextInt(), it reads the newline character and stops, resulting in no input being 
//		//taken from the user. To resolve this issue, an extra s.nextLine() call after s.nextInt() is added to consume the 
//		//newline character from the input buffer.
////====================
//		boolean coach = true; //1 for coach and 0 for FirstClass
//		// Try to get a list of airports
////		==================================
//		if (sclass.equals("FIRSTCLASS")) {
//			coach = false;			
//		}
//		if (trip.equals("O")) { //if oneway, the search is done for fights on the first day 
//			searchFlight(team,departure,arrival,time,seats,coach);}
//		//if roundtrip, the search is done for fights on the first day and a new arrival day is obtained, which is used by the search flight 
//		//function to find the rturn flights.
//		else if(trip.equals("R")){ 
//			System.out.println("ArrivalTime (YYYY_MM_DD) B/W. 2023_05_06 and 2023_05_31: ");
//			String arrtime = s.nextLine();			
//			searchFlight(team,departure,arrival,time,seats,coach);
//			System.out.println("********************************************************************************************");
//			System.out.println("Return Trip");
//			searchFlight(team,arrival,departure,arrtime,seats,coach);			
//			
//		}
//		
//		
//		
//		
//	}
//	public static void searchFlight(String team, String departure,String arrival, String time,int seats, boolean coach) throws ParseException {
//// ----------------------------------------WITHOUT ANY LAYOVERS-----------------------------------------
//		System.out.println("Without Layovers");
//		List<Flights> flightli = FlightSearch.searchFlightsWithNoStop(team,departure,arrival,time,seats, coach);
//		FlightSearch.display(flightli);
//		
//// ------------------------------------------WITH  ONE  LAYOVERS----------------------------------------s
//		System.out.println("With One Layover");
//		List<Flights> flightlis = FlightSearch.searchFlightsWithOneStop(team,departure,arrival,time,seats,coach);
//		FlightSearch.display(flightlis);
////		
//// ------------------------------------------WITH  TWO  LAYOVERS----------------------------------------
//		System.out.println("With Two Layover");
//		List<Flights> flightlist = FlightSearch.searchFlightsWithTwoStop(team,departure,arrival,time,seats,coach);
//		FlightSearch.display(flightlist);
//
////	=================================
//	
//		
//	// 	//try to reserve a coach seat on one of the flights
////	 	Flight flight = flights.get(0);
//	 	String flightNumber = "27165";//flight.getmNumber();
//	 	int seatsReservedStart = 62;//flight.getmSeatsCoach();
//////	========================	
//	 	String xmlReservation = "<Flights>"
//	 			+ "<Flight number=\"27165\"" + " seating=\"Coach\" />"
//	 			+ "</Flights>";
//		
//	 	ServerInterface resSys = new ServerInterface();
//	// 	// Try to lock the database, purchase ticket and unlock database
//	 	resSys.lock("TeamC");
//	 	resSys.buyTickets("TeamC", xmlReservation);
//	 	resSys.unlock("TeamC");
//////	==========================
//	// 	// Verify the operation worked
//	 	Flights xmlFlights = resSys.getFlights(team,departure,arrival,time,true);
////	 	// System.out.println(xmlFlights);
////	 	flights.clear();
////	 	flights.addAll(xmlFlights);
////	
////	 	// Find the flight number just updated
//	 	int seatsReservedEnd = seatsReservedStart;
//	 	for (Flight f : xmlFlights) {
//	 		String tmpFlightNumber = f.getmNumber();
//	 		if (tmpFlightNumber.equals(flightNumber)) {
//	 			seatsReservedEnd = f.getmSeatsCoach();
//	 			break;
//	 		}
//	 	}
//	 	if (seatsReservedEnd == (seatsReservedStart + 1)) {
//	 		System.out.println("Seat Reserved Successfully");
//	 	} else {
//	 		System.out.println("Reservation Failed");
//	 	}
//}}
//		