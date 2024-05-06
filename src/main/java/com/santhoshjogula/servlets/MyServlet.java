package com.santhoshjogula.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

/**
 * This class extends HttpServlet to create a servlet that handles HTTP requests for weather data.
 * It uses the OpenWeatherMap API to fetch weather data for a specified city.
 */
@WebServlet("/weather")
public class MyServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private static String apikey;
    private static String apiBaseUrl;

    /**
     * Default constructor for the MyServlet class.
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * This method is called by the servlet container to indicate to a servlet that the servlet is being placed into service.
     * The servlet container calls the init method exactly once after instantiating the servlet.
     * The init method must complete successfully before the servlet can receive any requests.
     * <p>
     * In this implementation, the method retrieves the path to a configuration file from the context parameters,
     * ensures the configFile parameter is not null, and loads properties from the configuration file.
     * It then retrieves values from the properties object.
     *
     * @param config a ServletConfig object containing the servlet's configuration and initialization parameters
     * @throws ServletException if an exception has occurred that interferes with the servlet's normal operation
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Get the path to the configuration file from the context parameters
        String configFile = getServletContext().getInitParameter("configFile");

        // Ensure the configFile parameter is not null
        if (configFile == null) {
            throw new ServletException("Config file path is not specified in context parameters.");
        }

        // Load properties from the configuration file
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            // Ensure that the input stream is not null before loading properties
            if (input != null) {
                Properties properties = new Properties();
                properties.load(input);

                // Retrieve values from the properties object
                apikey = properties.getProperty("apiKey");
                apiBaseUrl = properties.getProperty("apiBaseUrl");

                // Use the retrieved values as needed
                System.out.println("API Key: " + apikey);
                System.out.println("API url: " + apiBaseUrl);
            } else {
                throw new ServletException("Config file not found: " + configFile);
            }
        } catch (IOException e) {
            throw new ServletException("Error loading configuration file", e);
        }
    }

    /**
     * Handles HTTP GET requests.
     * This method redirects the client to the index.html page.
     *
     * @param request  the HttpServletRequest object that contains the request the client made of the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the GET could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.html");
    }

    /**
     * Handles HTTP POST requests.
     * This method fetches weather data for a specified city from the OpenWeatherMap API and forwards the data to a JSP page for rendering.
     *
     * @param request  the HttpServletRequest object that contains the request the client made of the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the POST could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the POST request
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the city from the form input
        String city = request.getParameter("city");

        // Create the URL for the OpenWeatherMap API request
        String apiUrl = apiBaseUrl + city + "&appid=" + apikey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check the HTTP response code
            int responseCode = connection.getResponseCode();

            if (responseCode == 404) {
                // Set an attribute indicating the error
                //request.setAttribute("error", "Invalid city name. Please enter a valid city name.");
                request.setAttribute("errorMessage", "Invalid city name entered. Please enter another city name.");

                // Forward the request to the weather.jsp page for rendering
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return; // Exit the method to avoid further processing
            }

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            // System.out.println(reader);

            Scanner scanner = new Scanner(reader);
            StringBuilder responseContent = new StringBuilder();

            while (scanner.hasNext()) {
                responseContent.append(scanner.nextLine());
            }

            // System.out.println(responseContent);
            scanner.close();

            // Parse the JSON response to extract temperature, date, and humidity
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

            // Date & Time
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            String date = new Date(dateTimestamp).toString();

            // Temperature
            double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            int temperatureCelsius = (int) (temperatureKelvin - 273.15);

            // Humidity
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

            // Wind Speed
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

            // Weather Condition
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").toString();

            // Set the data as request attributes (for sending to the jsp page)
            request.setAttribute("date", date);
            System.out.println(date);
            request.setAttribute("city", city);
            System.out.println(city);
            request.setAttribute("temperature", temperatureCelsius);
            System.out.println(temperatureCelsius);
            request.setAttribute("weatherCondition", weatherCondition);
            System.out.println(weatherCondition);
            request.setAttribute("humidity", humidity);
            System.out.println(humidity);
            request.setAttribute("windSpeed", windSpeed);
            System.out.println(windSpeed);
            request.setAttribute("weatherData", responseContent.toString());

            // Close the connection
            connection.disconnect();

        } catch (FileNotFoundException e) {
            // Set an attribute indicating the error
            //request.setAttribute("error", "City not found. Please enter a valid city name.");
            request.setAttribute("errorMessage", "Invalid city name entered. Please enter another city name.");
            // Forward the request to the weather.jsp page for rendering
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}