package CS509.client.driver;

import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import CS509.client.flight.Flights;

public class Trips extends ArrayList<Trip>{
	
	private static final long serialVersionUID = 1L;
	
	public boolean addAll(List<Flights> LoF) {
		
		boolean collectionUpdated = false;
		
		for (int i = 0; i < LoF.size(); i++) {
			Trip trip = null;
			
			try {
				trip = new Trip(LoF.get(i));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if (trip.isValid()) {
				this.add(trip);
				collectionUpdated = true;
			}
		}
		
		return collectionUpdated;
	}
	
	Comparator<Trip> compareByPrice = new Comparator<Trip>(){
		@Override
		public int compare(Trip t1, Trip t2) {
			return t1.getPrice(Trip.seatType.COACH).compareTo(t2.getPrice(Trip.seatType.COACH));
		}
	};

	
	public boolean sortByDeparture() {
		return true;
	}
	
	public boolean sortByArrival() {
		return true;
	}
	
	public boolean sortByLengthOfTrip() {
		return true;
	}
	
	public boolean displayAll(Trip.seatType seatType) {
		int tripListSize = this.size();
		
		if(tripListSize > 0) {
			for (int i = 0; i < tripListSize; i++) {
			    System.out.println("("+i+")"+"*****************************************************************************************");
			    this.get(i).displayTrip(seatType);
			}
			System.out.println("********************************************************************************************");
		}
		else {
			System.out.println("No Flights in this List");
		}
		return true;
	}

}
