package CS509.client.driver;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DecimalFormat;



import CS509.client.flight.Flight;
import CS509.client.flight.Flights;
import CS509.client.airport.TimeConversion;
import CS509.client.dao.ServerInterface;
import CS509.client.flight.Sort;
/**
 * @author gokul
 *
 */

/**
 * Searches and return the list of possible flights between the given airports without any layover
 * 
 * Returns the string of the possible flights
 */
public class FlightSearch{
	static List<Long> fduration = new ArrayList<Long>();
	static List<Long> lduration = new ArrayList<Long>();
	static List<Double> ticketprice = new ArrayList<Double>();
	
	public static List<Flights> searchFlightsWithNoStop(String team,String departure,String arrival,String time, int seats,boolean coach){
		fduration.clear();lduration.clear();ticketprice.clear();
		List<Flights> res=new ArrayList<Flights>();
		Flights xmlflights = getFlights(team,departure,arrival,time,true);
		xmlflights=Sort.filterByArrival(arrival, xmlflights);
		for (Flight f1: xmlflights) {
			if(seatType(f1,seats,coach)) {
				long fdur = Long.parseLong(f1.getmFlightTime());
				long ldur = 0;
				double p = 0.0;
				if(coach) {
					p = Double.parseDouble(f1.getmPriceCoach().replace(",", "").replace("$", ""));					
				}
				else {
					p = Double.parseDouble(f1.getmPriceFirstclass().replace(",", "").replace("$", ""));
				}
				Flights flight=new Flights();
				if (validDate(f1,time,departure)) {
				flight.add(f1);
				res.add(flight);
				fduration.add(fdur);
				lduration.add(ldur);
				ticketprice.add(p*seats);}}
		}
		System.out.println("res size:"+res.size());	
		return res;
		
	}
	/**
	 * Searches and return the list of possible flights between the given airports with one layover
	 * The layover threshold is set between 30 minutes and 6 hours
	 * Returns a List of Flights
	 */
	public static List<Flights> searchFlightsWithOneStop(String team,String departure,String arrival,String time,int seats,boolean coach) throws ParseException{
		fduration.clear();lduration.clear();ticketprice.clear();
		List<Flights> res=new ArrayList<Flights>();
		String nextday = nextDate(time);
		Flights flights1 = getFlights(team,departure,arrival,time,true);
		Flights flights2 = getFlights(team,departure,arrival,time,false);//false means search by departure
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm z");		
		for(Flight f1 : flights1){			
			for(Flight f2 :flights2){
				if(f1.getmCodeArrival().equals(f2.getmCodeDepart()) && validDate(f1,time,departure)){					
					Date date1 = sdf.parse(f1.getmTimeArrival());
					Date date2 = sdf.parse(f2.getmTimeDepart());					
					
						long diffInMilliSec = date2.getTime() - date1.getTime();
						long diffInMinutes = diffInMilliSec / (60 * 1000);
//						long diffInHours = diffInMilliSec / (60 * 60 * 1000);
//						The layover threshold is set between 30 minutes and 6 hours.
						if (diffInMinutes >= 30 && diffInMinutes <= 360 && seatType(f1,seats,coach) && seatType(f2,seats,coach)) {
							long fdur = Long.parseLong(f1.getmFlightTime())+Long.parseLong(f2.getmFlightTime())+diffInMinutes;
							long ldur = diffInMinutes;
							double p = 0.0;
							if(coach) {
								p = Double.parseDouble(f1.getmPriceCoach().replace(",", "").replace("$", "")) +
								    Double.parseDouble(f2.getmPriceCoach().replace(",", "").replace("$", ""));
							}
							else {
								p = Double.parseDouble(f1.getmPriceFirstclass().replace(",", "").replace("$", ""))+
									Double.parseDouble(f2.getmPriceFirstclass().replace(",", "").replace("$", ""));
							}
							Flights flight=new Flights();
							flight.add(f1);
							flight.add(f2);
							res.add(flight);
							fduration.add(fdur);
							lduration.add(ldur);
							ticketprice.add(p*seats);}
				}
			}
		}
		System.out.println("res size:"+res.size());		
		return res;		
	}
	
	/**
	 * Searches and return the list of possible flights between the given airports with two layovers on the same day
	 * The individual layover threshold is set between 30 minutes and 3 hours
	 * The total layover will be less than 6 hours 
	 * Returns a List of Flights
	 */

	public static List<Flights> searchFlightsWithTwoStop(String team,String departure,String arrival,String time,int seats,boolean coach) throws ParseException{
		List<Flights> res=new ArrayList<Flights>();
		fduration.clear();lduration.clear();ticketprice.clear();
		Flights flights1 = getFlights(team,departure,arrival,time,true);
		Flights flights3 = getFlights(team,departure,arrival,time,false);//false means search by arrival
		for(Flight f1:flights1) {
			if(validDate(f1,time,departure)) {
			//Since it is set to true, arrival doesnt matter thus set to Dummy
			Flights flights2 = getFlights(team, f1.getmCodeArrival(), "Dummy", time, true);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm z");
			for(Flight f2 : flights2) {				
				for(Flight f3 :flights3){
					if(f2.getmCodeArrival().equals(f3.getmCodeDepart()) && !f1.getmCodeArrival().equals(f3.getmCodeArrival())){					
						Date date1 = sdf.parse(f1.getmTimeArrival());
						Date date2 = sdf.parse(f2.getmTimeDepart());
						Date date3 = sdf.parse(f2.getmTimeArrival());
						Date date4 = sdf.parse(f3.getmTimeDepart());					
						
							long diffInMilliSec1 = date2.getTime() - date1.getTime(); //Layover 1
							long diffInMilliSec2 = date4.getTime() - date3.getTime();// Layover 2
							long diffInMilliSecTot = diffInMilliSec1+diffInMilliSec2;
							long diffInMinutes1 = diffInMilliSec1 / (60 * 1000);
							long diffInMinutes2 = diffInMilliSec2 / (60 * 1000);
							long diffInMinutes = diffInMilliSecTot / (60 * 1000);
//							long diffInHours = diffInMilliSec / (60 * 60 * 1000);
//							The layover threshold is set between 30 minutes and 6 hours.
							if (diffInMinutes1 >= 30 && diffInMinutes1 <= 180 && diffInMinutes2 >= 30 && diffInMinutes2 <= 180 && diffInMinutes <= 360  && seatType(f1,seats,coach) && seatType(f2,seats,coach)&& seatType(f3,seats,coach)) {
								long fdur = Long.parseLong(f1.getmFlightTime())+Long.parseLong(f2.getmFlightTime())+Long.parseLong(f3.getmFlightTime())+diffInMinutes1+diffInMinutes2;
								long ldur = diffInMinutes;
								double p = 0.0;
								if(coach) {
									p = Double.parseDouble(f1.getmPriceCoach().replace(",", "").replace("$", ""))+
										Double.parseDouble(f2.getmPriceCoach().replace(",", "").replace("$", ""))+
										Double.parseDouble(f3.getmPriceCoach().replace(",", "").replace("$", ""));
								}
								else {
									p = Double.parseDouble(f1.getmPriceFirstclass().replace(",", "").replace("$", ""))+
										Double.parseDouble(f2.getmPriceFirstclass().replace(",", "").replace("$", ""))+
										Double.parseDouble(f3.getmPriceFirstclass().replace(",", "").replace("$", ""));
								}
								Flights flight=new Flights();
								flight.add(f1);
								flight.add(f2);
								flight.add(f3);
								res.add(flight);
								fduration.add(fdur);
								lduration.add(ldur);
								ticketprice.add(p*seats);}
					}
			}
		}
	}
}

		System.out.println("res size:"+res.size());		
		return res;
		
	}
	public static boolean seatType(Flight f,int seats,boolean coach) {
		if (coach){
			int fseat = f.getmSeatsCoach();
			if (fseat >= seats){
				return true;
			}
		}
		else if (!coach) {
			int fseat = f.getmSeatsFirstclass();
			if (fseat >= seats){
				return true;
			}
		}
		return false;
	}
	
	public static boolean validDate(Flight f, String date, String airportCode) {
		String departTime = f.getmTimeDepart();
		return TimeConversion.dateBetweentwoDates(date,departTime,airportCode);
		
	}
	public static String nextDate(String date) {
		return TimeConversion.getNextDate(date);
	}
	
	public static Flights getFlights(String team,String departure,String arrival,String time,boolean dep) {
		ServerInterface resSys = new ServerInterface();
		String nextday = nextDate(time);
		Flights flights1 = resSys.getFlights(team,departure,arrival,time,dep); //true means search by departure
		Flights flnextday = resSys.getFlights(team,departure,arrival,nextday,dep);
		flights1.addAll(flnextday);
		return flights1;
	}

	/**
	 * Calculates the Flight Duration and Layover time in Hours and Minutes and Displays it
	 * 
	 */
	public static void display(List<Flights> flightlist) {
		DecimalFormat df = new DecimalFormat("#.##");
		for (int i = 0; i < flightlist.size(); i++) {
		    Flights flight = flightlist.get(i);
		 // get the duration from the fduration list
		    long duration = fduration.get(i);
		    long fhours = duration / 60;
		    long fminutes = duration % 60;
		    long layduration = lduration.get(i);
		    long lhours = layduration / 60;
		    long lminutes = layduration % 60;
		    String tp = df.format(ticketprice.get(i));
		    System.out.println(flight.toString());
		    System.out.println("Total Ticket Price :$"+tp);
		    System.out.printf("Total Layover Time:" +lhours+" Hours "+ lminutes+" Minutes\n");
		    System.out.printf("Total Trip Duration:" +fhours+" Hours "+ fminutes+" Minutes\n");
		    System.out.println("********************************************************************************************");
		}
	}
}
