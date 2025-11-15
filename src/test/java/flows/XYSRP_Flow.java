package flows;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;

import FlyModules.BrowserContants;
import FlyModules.Flynas;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pageObjects.BaseClass;
import pageObjects.Database;

public class XYSRP_Flow {
	static WebDriver driver;
	boolean status;
	private Database PnrDetails;
	static String strDate;
	public static String FlynasURL;



	@Test(priority = 1)
	public void test() throws Exception {

	    setRestAssuredBaseURI();
	    RequestSpecification request = RestAssured.given();
	    request.header("Content-Type", "application/json");

	    Response response = request.get("/Getroutesbyairline?airline=xy&days=11&skipdays=1&orderby=asc");
	    String s = response.body().asString();

	    int statusCode = response.getStatusCode();
	    System.out.println("The status code received: " + statusCode);

	    // Calculate departure date +5 days
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.DATE, 5);
	    SimpleDateFormat sdfURL = new SimpleDateFormat("yyyy-MM-dd");
	    String DepartDate = sdfURL.format(cal.getTime());

	    Gson gson = new Gson();
	    Database[] databaseArray = gson.fromJson(s, Database[].class);
	    List<Database> databaseList = Arrays.asList(databaseArray);

	    // --- Unique route filtering ---
	    Map<String, Database> uniqueRoutes = new LinkedHashMap<>();
	    for (Database data : databaseList) {
	        String routeKey = data.From + "-" + data.To;
	        if (!uniqueRoutes.containsKey(routeKey)) {
	            uniqueRoutes.put(routeKey, data);
	        }
	    }

	    // Print unique routes
	    for (Database data : uniqueRoutes.values()) {
	        System.out.println(data.From + " - " + data.To);
	    }

	    int maxRoutes = 10;
	    int count = 0;

	    for (Database data : uniqueRoutes.values()) {
	        if (count >= maxRoutes) {
	            break;
	        }

	        try {
	            // Replace airport mappings
	            if ("CAI".equals(data.To)) data.To = "EG1";
	            else if ("CAI".equals(data.From)) data.From = "EG1";

	            if ("DXB".equals(data.To)) data.To = "AE1";
	            else if ("DXB".equals(data.From)) data.From = "AE1";

	            // Convert date from "dd MMM yyyy" → "yyyy-MM-dd"
	            Date depDate = new SimpleDateFormat("dd MMM yyyy").parse(data.DepartureDate);
	            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	            String strDate = formatter.format(depDate);

	            //System.out.println("strDate : " + strDate);

	            FlynasURL ="https://booking.flynas.com/#/booking/search-redirect?origin=" + data.From + "&destination=" + data.To +"&currency=SAR&departureDate=" + DepartDate + "&flightMode=oneway&adultCount=1&childCount=0&infantCount=0";

	            System.out.println("API URL " + FlynasURL);

	            PnrDetails = data;

	            FirefoxOptions options = new FirefoxOptions();
	            options.addPreference("layout.css.devPixelsPerPx", "0.3");
	            options.addPreference("permissions.default.image", 2);
	            options.addArguments("--headless");

	            driver = new FirefoxDriver(options);
	            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
	            driver.manage().deleteAllCookies();

	            driver.get(FlynasURL);

	            new BaseClass(driver);
	            Flynas.FlightDetails(driver, PnrDetails);

	            driver.quit();

	            count++;  // Increase ONLY after successful processing

	        } catch (Exception e) {
	            System.out.println("Error processing route: " + data.From + "-" + data.To);
	            e.printStackTrace();
	        }
	    }

	    System.out.println("Completed processing " + count + " routes.");
	}


	@Test(priority = 2)
	public void test2() throws Exception {

	    setRestAssuredBaseURI();
	    RequestSpecification request = RestAssured.given();
	    request.header("Content-Type", "application/json");

	    Response response = request.get("/Getroutesbyairline?airline=xy&days=22&skipdays=11&orderby=asc");
	    String s = response.body().asString();

	    int statusCode = response.getStatusCode();
	    System.out.println("The status code received: " + statusCode);

	    // Calculate departure date +5 days
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.DATE, 16);
	    SimpleDateFormat sdfURL = new SimpleDateFormat("yyyy-MM-dd");
	    String DepartDate = sdfURL.format(cal.getTime());

	    Gson gson = new Gson();
	    Database[] databaseArray = gson.fromJson(s, Database[].class);
	    List<Database> databaseList = Arrays.asList(databaseArray);

	    // --- Unique route filtering ---
	    Map<String, Database> uniqueRoutes = new LinkedHashMap<>();
	    for (Database data : databaseList) {
	        String routeKey = data.From + "-" + data.To;
	        if (!uniqueRoutes.containsKey(routeKey)) {
	            uniqueRoutes.put(routeKey, data);
	        }
	    }

	    // Print unique routes
	    for (Database data : uniqueRoutes.values()) {
	        System.out.println(data.From + " - " + data.To);
	    }

	    int maxRoutes = 10;
	    int count = 0;

	    for (Database data : uniqueRoutes.values()) {
	        if (count >= maxRoutes) {
	            break;
	        }

	        try {
	            // Replace airport mappings
	            if ("CAI".equals(data.To)) data.To = "EG1";
	            else if ("CAI".equals(data.From)) data.From = "EG1";

	            if ("DXB".equals(data.To)) data.To = "AE1";
	            else if ("DXB".equals(data.From)) data.From = "AE1";

	            // Convert date from "dd MMM yyyy" → "yyyy-MM-dd"
	            Date depDate = new SimpleDateFormat("dd MMM yyyy").parse(data.DepartureDate);
	            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	            String strDate = formatter.format(depDate);

	            System.out.println("strDate : " + strDate);

	            FlynasURL ="https://booking.flynas.com/#/booking/search-redirect?origin=" + data.From + "&destination=" + data.To +"&currency=SAR&departureDate=" + DepartDate + "&flightMode=oneway&adultCount=1&childCount=0&infantCount=0";

	            System.out.println("API URL " + FlynasURL);

	            PnrDetails = data;

	            FirefoxOptions options = new FirefoxOptions();
	            options.addPreference("layout.css.devPixelsPerPx", "0.3");
	            options.addPreference("permissions.default.image", 2);
	            options.addArguments("--headless");

	            driver = new FirefoxDriver(options);
	            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
	            driver.manage().deleteAllCookies();

	            driver.get(FlynasURL);

	            new BaseClass(driver);
	            Flynas.FlightDetails(driver, PnrDetails);

	            driver.quit();

	            count++;  // Increase ONLY after successful processing

	        } catch (Exception e) {
	            System.out.println("Error processing route: " + data.From + "-" + data.To);
	            e.printStackTrace();
	        }
	    }

	    System.out.println("Completed processing " + count + " routes.");
	}

	@Test(priority = 3)
	public void test3() throws Exception {

	    setRestAssuredBaseURI();
	    RequestSpecification request = RestAssured.given();
	    request.header("Content-Type", "application/json");

	    Response response = request.get("/Getroutesbyairline?airline=xy&days=31&skipdays=22&orderby=asc");
	    String s = response.body().asString();

	    int statusCode = response.getStatusCode();
	    System.out.println("The status code received: " + statusCode);

	    // Calculate departure date +5 days
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.DATE, 27);
	    SimpleDateFormat sdfURL = new SimpleDateFormat("yyyy-MM-dd");
	    String DepartDate = sdfURL.format(cal.getTime());

	    Gson gson = new Gson();
	    Database[] databaseArray = gson.fromJson(s, Database[].class);
	    List<Database> databaseList = Arrays.asList(databaseArray);

	    // --- Unique route filtering ---
	    Map<String, Database> uniqueRoutes = new LinkedHashMap<>();
	    for (Database data : databaseList) {
	        String routeKey = data.From + "-" + data.To;
	        if (!uniqueRoutes.containsKey(routeKey)) {
	            uniqueRoutes.put(routeKey, data);
	        }
	    }

	    // Print unique routes
	    for (Database data : uniqueRoutes.values()) {
	        System.out.println(data.From + " - " + data.To);
	    }

	    int maxRoutes = 10;
	    int count = 0;

	    for (Database data : uniqueRoutes.values()) {
	        if (count >= maxRoutes) {
	            break;
	        }

	        try {
	            // Replace airport mappings
	            if ("CAI".equals(data.To)) data.To = "EG1";
	            else if ("CAI".equals(data.From)) data.From = "EG1";

	            if ("DXB".equals(data.To)) data.To = "AE1";
	            else if ("DXB".equals(data.From)) data.From = "AE1";

	            // Convert date from "dd MMM yyyy" → "yyyy-MM-dd"
	            Date depDate = new SimpleDateFormat("dd MMM yyyy").parse(data.DepartureDate);
	            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	            String strDate = formatter.format(depDate);

	            System.out.println("strDate : " + strDate);

	            FlynasURL ="https://booking.flynas.com/#/booking/search-redirect?origin=" + data.From + "&destination=" + data.To +"&currency=SAR&departureDate=" + DepartDate + "&flightMode=oneway&adultCount=1&childCount=0&infantCount=0";

	            System.out.println("API URL " + FlynasURL);

	            PnrDetails = data;

	            FirefoxOptions options = new FirefoxOptions();
	            options.addPreference("layout.css.devPixelsPerPx", "0.3");
	            options.addPreference("permissions.default.image", 2);
	            options.addArguments("--headless");

	            driver = new FirefoxDriver(options);
	            driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
	            driver.manage().deleteAllCookies();

	            driver.get(FlynasURL);

	            new BaseClass(driver);
	            Flynas.FlightDetails(driver, PnrDetails);

	            driver.quit();

	            count++;  // Increase ONLY after successful processing

	        } catch (Exception e) {
	            System.out.println("Error processing route: " + data.From + "-" + data.To);
	            e.printStackTrace();
	        }
	    }

	    System.out.println("Completed processing " + count + " routes.");
	}



	@AfterMethod
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}

	private void setRestAssuredBaseURI() {
		if (BrowserContants.ENV.equals("PRD")) {
			RestAssured.baseURI = BrowserContants.PRD_API_URL;
			System.out.println(BrowserContants.PRD_API_URL);
		} else if (BrowserContants.ENV.equals("STG")) {
			RestAssured.baseURI = BrowserContants.STG_API_URL;
			System.out.println(BrowserContants.STG_API_URL);
		}
	}
}
