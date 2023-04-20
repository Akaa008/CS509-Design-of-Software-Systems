package CS509.client.flight;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
/**
 * @author Gokul
 *
 */

public class Sort extends ArrayList<Flight> {
	private static final long serialVersionUID = 1L;
	

	
	static public Flights filterByArrival(String arr, Flights flights){
		Flights res= new Flights();
		for(Flight flight: flights){
			if(flight.getmCodeArrival().equals(arr)){
				res.add(flight);
			}else if (flight.getmCodeDepart().equals(arr)) {
				System.out.println("We admire your love for your home airport, but unfortunately,\n"
						+ "it can't be both the departure and arrival. Please make a different selection.");
				break;
			}
		}
		return res;
	}
	
	
	
	}
	

