package CS509.client.airport;
//
//import CS509.client.airport.Airport;

import java.io.IOException;
//import com.github.joostvdg.timezone.mapper.TimeZoneMapper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
/**
 * @author Gokul
 *
 */

/**
 * Gets the GMT time and returns the Local time at that airport. Uses timezonedb.com api once to save all the time zones and GMT offsets in timezone_data.csv file. 
 * Uses timezone_data.csv for all the calculations
 * Returns the string of local time
 */

public class TimeConversion {
    
    private static final String API_URL = "http://api.timezonedb.com/v2.1/get-time-zone?key=<API_KEY>&format=json&by=position&lat=%f&lng=%f";
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm z");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm ");
    private static long lastApiCallTime = 0;
    private static final String CSV_FILE_PATH = "timezone_data.csv";
    
    private final static Map<String, String[]> airportData = new HashMap<>();
/**
 * 
 * @param csvFilePath
 * @throws IOException
 */
    public TimeConversion(String csvFilePath) throws IOException {
        // Read CSV file and populate airportData map
        BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",");
            airportData.put(fields[0], new String[]{fields[1], fields[2]});
        }
        reader.close();
    }
/**
 * 
 * @param gmtTime
 * @param airportCode
 * @return Time in zone local to the airport in yyyy MMM dd HH:mm zzz format.
 * @throws DateTimeParseException
 */
    public static String convertToTimeZone(String gmtTime, String airportCode) throws DateTimeParseException {
        // Parse GMT time string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm zzz");
        LocalDateTime gmtDateTime = LocalDateTime.parse(gmtTime, formatter);

        // Get airport data from map
        String[] airportInfo = airportData.get(airportCode);
        if (airportInfo == null) {
            throw new IllegalArgumentException("Unknown airport code: " + airportCode);
        }
        int gmtOffset = Integer.parseInt(airportInfo[0]);
        String nextAbbreviation = airportInfo[1];

        // Convert GMT time to local time
        LocalDateTime inputDateTime = LocalDateTime.parse(gmtTime, INPUT_FORMATTER);
		ZonedDateTime inputZonedDateTime = inputDateTime.atZone(ZoneOffset.UTC);
		ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(gmtOffset / 3600, Math.abs(gmtOffset % 3600) / 60);
		ZonedDateTime outputZonedDateTime = inputZonedDateTime.withZoneSameInstant(zoneOffset);
		String outputTime = outputZonedDateTime.format(OUTPUT_FORMATTER)  + nextAbbreviation;
        return outputTime;
    }
    /**
     * Calculates the start of the day and returns Date and Time for the given date.
     * @param date in yyyy_MM_dd format
     * @return date in yyyy_MM_dd HH:mm format with HH:mm being 00:00
     */
    public static String startofDay(String date) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
    	LocalDate localDate = LocalDate.parse(date, formatter);
    	LocalDateTime gmtDateTime = localDate.atStartOfDay();
    	String formattedDate = gmtDateTime.format(DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm"));
 	
    	return formattedDate;
    }
    /**
     * Calculates the end of the day and returns Date and Time for the given date.
     * @param date in yyyy_MM_dd format
     * @return date in yyyy_MM_dd HH:mm format with HH:mm being 23:59
     */
    public static String endofDay(String date) {
    	String startofdate = startofDay(date);
    	LocalDateTime dateTime = LocalDateTime.parse(startofdate, DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm"));
        LocalDateTime minusOneMinute = dateTime.plusHours(24).minusMinutes(1);
        String endofDay = minusOneMinute.format(DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm"));
    	return endofDay;
    }
/**
 * Converts time local to the airport to GMT
 * @param localTime
 * @param airportCode
 * @return date in yyyy_MM_dd HH:mm GMT format
 * @throws DateTimeParseException
 */
    public static String convertToGMT(String localTime, String airportCode) throws DateTimeParseException {
        // Parse GMT time string
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm");
    	LocalDateTime localDateTime = LocalDateTime.parse(localTime, formatter);

    	// Get airport data from map
    	String[] airportInfo = airportData.get(airportCode);
    	if (airportInfo == null) {
    	    throw new IllegalArgumentException("Unknown airport code: " + airportCode);
    	}
    	int gmtOffset = Integer.parseInt(airportInfo[0]);
    	String nextAbbreviation = airportInfo[1];

    	// Convert local time to GMT time
    	ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(gmtOffset / 3600, Math.abs(gmtOffset % 3600) / 60);
    	OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, zoneOffset);
    	ZonedDateTime gmtDateTime = offsetDateTime.toZonedDateTime().withZoneSameInstant(ZoneOffset.UTC);
    	String outputTime = gmtDateTime.format(formatter) + " GMT";
    	return outputTime;
    }
  /**
   * Checks if the given datetime is between two other datetimes. 
   * @param date
   * @param departTime
   * @param airportCode
   * @return True if the departTime is in the 24 hours of given date (yyyy_MM_dd).  
   */
		  
    public static boolean dateBetweentwoDates(String date,String departTime, String airportCode) {
    	LocalDateTime startofdayGMT = LocalDateTime.parse(convertToGMT(startofDay(date),airportCode),DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm zzz"));
    	LocalDateTime endofdayGMT = LocalDateTime.parse(convertToGMT(endofDay(date),airportCode),DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm zzz"));
    	LocalDateTime dateC =LocalDateTime.parse(departTime,DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm zzz"));
    	if (dateC.isAfter(startofdayGMT) && dateC.isBefore(endofdayGMT)) {
    	return true;
    	}
    	return false;
    }
    
    /**
     * Gets a date and return the date of next day
     * @param dateString
     * @return date of next day
     */
    public static String getNextDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        LocalDate nextDate = date.plusDays(1);
        return nextDate.format(formatter);
    }
    
    /**
     * Uses timezonedb api to store the gmtOffset, and the timezone abbreviation for all the available airports into timezone_data.csv file
     * @param airport
     */
    public static void getZones(Airports airport) {
    	try {
    		for (Airport a: airport) {
            // Check time elapsed since last API call
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastApiCallTime < 2000) {
                try {
                    Thread.sleep(2000 - (currentTime - lastApiCallTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lastApiCallTime = System.currentTimeMillis();

            // Get GMT offset and abbreviation for the given location and time
            String apiResponse = sendHttpRequest(String.format(API_URL, a.latitude(), a.longitude()));
            int gmtOffset = Integer.parseInt(parseApiResponse(apiResponse, "gmtOffset"));
            String nextAbbreviation = parseApiResponse(apiResponse, "abbreviation").replace("\"", "");
            writeToCSV(a.code(),gmtOffset, nextAbbreviation);
            }

    }catch (IOException e) {
            System.out.println( "Error: " + e.getMessage());}
        }
//    }

/**
 * 
 * @param apiResponse
 * @param key
 * @return
 */
    private static String parseApiResponse(String apiResponse, String key) {
        int offsetStartIndex = apiResponse.indexOf(key) + key.length() + 3;
        int offsetEndIndex = apiResponse.indexOf(",", offsetStartIndex);
        return apiResponse.substring(offsetStartIndex, offsetEndIndex);
    }
/**
 * 
 * @param apiUrl
 * @return
 * @throws IOException
 */
    private static String sendHttpRequest(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        String response = scanner.hasNext() ? scanner.next() : "";
        inputStream.close();
        connection.disconnect();
        return response;
    }
    /**
     * Writes to CSV file
     * @param code
     * @param gmtOffset
     * @param abbreviation
     * @throws IOException
     */
    private static void writeToCSV(String code,int gmtOffset, String abbreviation) throws IOException {
        FileWriter writer = new FileWriter("timezone.csv",true);
        writer.append(String.format("%s,%d,%s\n", code, gmtOffset, abbreviation));
        writer.close();
    }

    /**
     * Driver function for converting the time zones. Checks if the airport code is valid and subsequestly calls the 
     * convertToTimeZone function with given gmt time and airport code.
     * @param airport
     * @param time
     * @param code
     * @return returns output of convertToTimeZone which is Time in zone local to the airport in yyyy MMM dd HH:mm zzz format.
     */
	//===============================================================
    public static String loctime(Airports airport,String time, String code) {
        
        Airport a = airport.stream().filter(ap -> ap.code().equals(code)).findFirst().orElse(null);
        if (a == null) {
//            return "Airport code not found";
        }
        return convertToTimeZone(time,a.code());
        
    }
		
	
}