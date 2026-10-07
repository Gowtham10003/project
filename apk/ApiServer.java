import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiServer {

    private static final AtomicInteger scenarioIndex =
            new AtomicInteger(0);


    public static void main(String[] args)
            throws Exception {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );


        // STATUS API

        server.createContext(
                "/api/status",
                ApiServer::handleStatus
        );


        // HEALTH API

        server.createContext(
                "/api/health",
                ApiServer::handleHealth
        );


        server.setExecutor(null);

        server.start();


        System.out.println(
                "\n======================================"
        );

        System.out.println(
                " DISASTER EARLY-WARNING API SERVER"
        );

        System.out.println(
                "======================================"
        );

        System.out.println(
                "Server running at:"
        );

        System.out.println(
                "http://localhost:8080"
        );

        System.out.println();

        System.out.println(
                "Dashboard API:"
        );

        System.out.println(
                "http://localhost:8080/api/status"
        );

        System.out.println();

        System.out.println(
                "Demo sequence:"
        );

        System.out.println(
                "LOW -> MEDIUM -> HIGH -> DANGER"
        );
    }


    private static void handleStatus(
            HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"error\":\"Method not allowed\"}"
            );

            return;
        }


        int index =
                scenarioIndex.getAndIncrement()
                        % 4;


        Sensor sensor =
                createDemoSensor(index);


        DisasterAgent agent =
                new DisasterAgent(sensor);


        // Java rule engine

        agent.applyRules();


        // Export CSV

        try {

            agent.exportData();

        } catch (Exception e) {

            System.out.println(
                    "CSV export error: "
                    + e.getMessage()
            );
        }


        // Python classifier

        String pythonRisk =
                sensor.getRiskLevel();


        try {

            pythonRisk =
                    agent.runPythonClassifier();

        } catch (Exception e) {

            System.out.println(
                    "Python classifier error: "
                    + e.getMessage()
            );
        }


        // Use Python result

        sensor.setRiskLevel(
                pythonRisk
        );


        agent.generateAlert(
                pythonRisk
        );


        String json =
                createJson(
                        sensor,
                        index
                );


        sendResponse(
                exchange,
                200,
                json
        );
    }


    private static Sensor createDemoSensor(
            int scenario) {


        switch (scenario) {

            // ---------------------------
            // LOW
            // ---------------------------

            case 0:

                return new Sensor(
                        "DEMO-01",
                        "Main Road",
                        30.0,
                        0.0,
                        15.0
                );


            // ---------------------------
            // MEDIUM
            // ---------------------------

            case 1:

                return new Sensor(
                        "DEMO-02",
                        "Village Area",
                        38.0,
                        15.0,
                        45.0
                );


            // ---------------------------
            // HIGH
            // ---------------------------

            case 2:

                return new Sensor(
                        "DEMO-03",
                        "River Area",
                        45.0,
                        30.0,
                        80.0
                );


            // ---------------------------
            // DANGER
            // ---------------------------

            case 3:

                return new Sensor(
                        "DEMO-04",
                        "Flood Zone",
                        52.0,
                        60.0,
                        110.0
                );


            default:

                return new Sensor(
                        "DEMO-01",
                        "Main Road",
                        30.0,
                        0.0,
                        15.0
                );
        }
    }


    private static String createJson(
            Sensor sensor,
            int scenario) {


        return "{"

                + "\"status\":\"success\","

                + "\"mode\":\"DEMO\","

                + "\"scenario\":"
                + (scenario + 1)
                + ","

                + "\"sensorId\":\""
                + escape(sensor.getId())
                + "\","

                + "\"location\":\""
                + escape(sensor.getLocation())
                + "\","

                + "\"source\":\"Simulated Data\","

                + "\"temperature\":"
                + sensor.getTemperature()
                + ","

                + "\"rainfall\":"
                + sensor.getRainfall()
                + ","

                + "\"windSpeed\":"
                + sensor.getWindSpeed()
                + ","

                + "\"riskScore\":"
                + sensor.getRiskScore()
                + ","

                + "\"risk\":\""
                + escape(sensor.getRiskLevel())
                + "\","

                + "\"hazard\":\""
                + escape(sensor.getHazard())
                + "\""

                + "}";
    }


    private static void handleHealth(
            HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);


        String json =
                "{"
                + "\"status\":\"online\","
                + "\"mode\":\"DEMO\","
                + "\"service\":\"Disaster Early-Warning Agent\""
                + "}";


        sendResponse(
                exchange,
                200,
                json
        );
    }


    private static void addCorsHeaders(
            HttpExchange exchange) {

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Origin",
                        "*"
                );

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Methods",
                        "GET, OPTIONS"
                );

        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Headers",
                        "Content-Type"
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );
    }


    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response)
            throws IOException {


        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );


        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(bytes);
        }
    }


    private static String escape(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}