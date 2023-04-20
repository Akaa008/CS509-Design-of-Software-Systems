/**
 * 
 */
package CS509.client.flight;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
//----------------------
// import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
//----------------------
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class holds values pertaining to an aggregate flights. The aggregate is implemented
 * as an ArrayList. Flights can be populated from XML string returned from CS509 server.
 * 
 * @author benelson
 * @version 1
 * @since 2016-02-24
 *
 */
public class Flights extends ArrayList <Flight> {

	private static final long serialVersionUID = 1L;

	// public static void sortByArrivalAirport(List<String> flights) {
	// 	Comparator<String> comparator = new Comparator<String>() {
	// 		public int compare(String o1, String o2) {
	// 			String arr1 = o1.split(",")[2];
	// 			String arr2 = o2.split(",")[2];
	// 			return arr1.compareTo(arr2);
	// 		}
	// 	};
	// 	Collections.sort(flights, comparator);
	// }
	
	// public String filterByArrival(String arr, List<String> flights) {
	// 	List<String> res = new ArrayList<>();
	// 	for (String flight : flights) {
	// 		String[] flightData = flight.split(",");
	// 		String arrAirportCode = flightData[2];
	// 		if (arrAirportCode.equals(arr)) {
	// 			res.add(flight);
	// 		}
	// 	}
	// 	return String.join("\n", res);
	// }
	// public void sortByArrivalAirport(){
	// 	Comparator<Flight> comparator = new Comparator<Flight>(){
	// 			public int compare(Flight o1, Flight o2) {
	// 		return o1.getmCodeArrival().compareTo(o2.getmCodeArrival());
	// 			}
	// 	};
				
	//    Collections.sort(this, comparator);
	// }
	// public Flight filterByArrival(String arr, Flights flights){
	// 	Flights res= new Flights();
	// 	for(Flight flight: flights){
	// 		if(flight.getmCodeArrival().equals(arr)){
	// 			res.add(flight);
	// 		}
	// 	}
	// 	return res.toString();
	// }
	public boolean addAll (String xmlFlights) {
		
		boolean collectionUpdated = false;
		
		// Load the XML string into a DOM tree for ease of processing
		// then iterate over all nodes adding each flight to our collection
		Document docFlights = buildDomDoc (xmlFlights);
		NodeList nodesFlights = docFlights.getElementsByTagName("Flight");
		
		for (int i = 0; i < nodesFlights.getLength(); i++) {
			Element elementFlight = (Element) nodesFlights.item(i);
			Flight flight = buildFlight (elementFlight);
			
			if (flight.isValid()) {
				this.add(flight);
				collectionUpdated = true;
			}
		}
		
		return collectionUpdated;
	}
	//---------------------------
	public static Flights addAllf (String xmlFlights) throws NullPointerException {
		Flights flights = new Flights();
		
		
		// Load the XML string into a DOM tree for ease of processing
		// then iterate over all nodes adding each flight to our collection
		Document docFlights = buildDomDoc (xmlFlights);
		NodeList nodesFlights = docFlights.getElementsByTagName("Flight");
		
		for (int i = 0; i < nodesFlights.getLength(); i++) {
			Element elementFlight = (Element) nodesFlights.item(i);
			Flight flight = buildFlight (elementFlight);
			
			flights.add(flight);
		}
		
		return flights;
	}

	//--------------------------
	
	/**
	 * Builds a DOM tree form an XML string
	 * 
	 * Parses the XML file and returns a DOM tree that can be processed
	 * 
	 * @param xmlString XML String containing set of objects
	 * @return DOM tree from parsed XML or null if exception is caught
	 */
	static private Document buildDomDoc (String xmlString) {
		/**
		 * load the xml string into a DOM document and return the Document
		 */
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			/*InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xmlString));
			
			return docBuilder.parse(inputSource);*/
			return docBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		catch (SAXException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a FLight object form a DOM node
	 * 
	 * Processes a DOM Node that describes a Flight and creates a Flight object from the information
	 * @param nodeFlight is a DOM Node describing an Airport
	 * @return Flight object created from the DOM Node representation of the Flight
	 * 
	 * @preconditions nodeFlight is of format specified by CS509 server API
	 */
	static private Flight buildFlight (Node nodeFlight) {
		/**
		 * flight will be instantiated after attributes are parsed from XML node
		 */
		Flight flight = null;

		String airplane;
		String flightTime;
		String number;
		String codeDepart;
		String timeDepart;
		String codeArrival;
		String timeArrival;
		String priceFirstclass;
		int seatsFirstclass;
		String priceCoach;
		int seatsCoach;
	
		
		// The flight element has attributes of Airplane, FlightTime and [Flight]Number
		Element elementFlight = (Element) nodeFlight;
		airplane = elementFlight.getAttributeNode("Airplane").getValue();
		flightTime = elementFlight.getAttributeNode("FlightTime").getValue();
		number = elementFlight.getAttributeNode("Number").getValue();
		
		// The Departure and Arrival are child elements each with Code and Time children
		Element elementDeparture;
		Element elementArrival;
		Element elementCode;
		Element elementTime;
		
		elementDeparture = (Element)elementFlight.getElementsByTagName("Departure").item(0);
		elementCode = (Element)elementDeparture.getElementsByTagName("Code").item(0);
		elementTime = (Element)elementDeparture.getElementsByTagName("Time").item(0);
		codeDepart = getCharacterDataFromElement(elementCode);
		timeDepart = getCharacterDataFromElement(elementTime);
		
		elementArrival = (Element)elementFlight.getElementsByTagName("Arrival").item(0);
		elementCode = (Element)elementArrival.getElementsByTagName("Code").item(0);
		elementTime = (Element)elementArrival.getElementsByTagName("Time").item(0);
		codeArrival = getCharacterDataFromElement(elementCode);
		timeArrival = getCharacterDataFromElement(elementTime);

		//Seating is child element with children of FirstClass and Coach
		Element elementSeating;
		Element elementFirstclass;
		Element elementCoach;
		
		elementSeating = (Element)elementFlight.getElementsByTagName("Seating").item(0);
		elementFirstclass = (Element)elementSeating.getElementsByTagName("FirstClass").item(0);
		elementCoach = (Element)elementSeating.getElementsByTagName("Coach").item(0);
		
		priceFirstclass = elementFirstclass.getAttributeNode("Price").getValue();
		seatsFirstclass = Integer.parseInt(getCharacterDataFromElement(elementFirstclass));
		
		priceCoach = elementCoach.getAttributeNode("Price").getValue();
		seatsCoach = Integer.parseInt(getCharacterDataFromElement(elementCoach));
		
		flight = new Flight (airplane, flightTime, number, codeDepart, timeDepart, 
				codeArrival, timeArrival, priceFirstclass, seatsFirstclass, priceCoach, seatsCoach);

		return flight;
	}
	
	/**
	 * Retrieve character data from an element if it exists
	 * 
	 * @param e is the DOM Element to retrieve character data from
	 * @return the character data as String [possibly empty String]
	 */
	private static String getCharacterDataFromElement (Element e) {
		Node child = e.getFirstChild();
	    if (child instanceof CharacterData) {
	        CharacterData cd = (CharacterData) child;
	        return cd.getData();
	      }
	      return "";
	}
	@Override
	public String toString() {
		String builtList = new String();
		Iterator<Flight> iter = this.iterator();
		
		if(!iter.hasNext()) {
			return "Looks like there are no flights available.\n"
					+ "Perhaps you could try summoning a dragon to fly you there instead?";
		}
		
		while(iter.hasNext()) {
			builtList += iter.next().toString() + "\r\n";
		}
		return builtList;
	}

}
