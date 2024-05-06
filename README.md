# Weather App
Weather App using Java Servlets
# Project Summary

This project is a web application that uses the OpenWeatherMap API to fetch weather data for a specified city. The application is built using Java, JavaScript, and Maven.

## Technologies Used

1. **Java**: The main programming language used in this project. The application logic is written in Java, including the servlets that handle HTTP requests.

2. **JavaScript**: Used on the client-side for handling user interactions and possibly for making AJAX requests to the server.

3. **Maven**: A build automation tool used primarily for Java projects. It handles the project's build lifecycle, dependencies, and packaging.

4. **Java Servlets**: Used to handle HTTP requests and responses. The project includes a servlet (`MyServlet`) that fetches weather data from the OpenWeatherMap API and forwards the data to a JSP page for rendering.

5. **JavaServer Pages (JSP)**: Used to create dynamic web content. The servlet forwards the weather data to a JSP page, which renders the data in the user's browser.

6. **Gson**: A Java library used to convert Java Objects into their JSON representation and vice versa. It is used in this project to parse the JSON response from the OpenWeatherMap API.

7. **OpenWeatherMap API**: An API that provides weather data for cities around the world. The application sends a GET request to this API to fetch weather data for a specified city.

## Project Structure

The project follows a typical Maven project structure with source code in the `src/main/java` directory and resources in the `src/main/resources` directory. The web application files are located in the `src/main/webapp` directory.

The `MyServlet` class is the main servlet that handles requests to the `/weather` endpoint. It fetches weather data from the OpenWeatherMap API and forwards the data to a JSP page for rendering.

The `config.properties` file contains configuration data for the application, such as the API key and base URL for the OpenWeatherMap API.

The `web.xml` file is the deployment descriptor for the web application. It defines servlets, welcome files, and context parameters for the application.