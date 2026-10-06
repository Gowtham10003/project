import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ApiServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT),
                        0
                );

        server.createContext(
                "/api/status",
                ApiServer::handleStatus
        );

        server.createContext(
                "/api/health",
                ApiServer::handleHealth
        );

        server.start();

        System.out.println("======================================");
        System.out.println(" DISASTER EARLY-WARNING API SERVER");
        System.out.println("======================================");
        System.out.println(
                "Server running at: http://localhost:" + PORT
        );
        System.out.println(
                "API: http://localhost:" +
                        PORT +
                        "/api/status"
        );
    }


    // STATUS API
    private static void handleStatus(
            HttpExchange exchange) {

        try {

            // Allow browser frontend
            addCorsHeaders(exchange);

            // Handle browser preflight request
            if ("OPTIONS".equalsIgnoreCase(
                    exchange.getRequestMethod())) {

                exchange.sendResponseHeaders(
                        204,
                        -1
                );

                exchange.close();

                return;
            }


            if (!"GET".equalsIgnoreCase(
                    exchange.getRequestMethod())) {

                sendResponse(
                        exchange,
                        405,
                        "{\"error\":\"Method not allowed\"}"
                );

                return;
            }


            // Generate three simulated sensors

            Sensor s1 =
                    generateSensor(
                            "S1",
                            "Main Road"
                    );

            Sensor s2 =
                    generateSensor(
                            "S2",
                            "River Area"
                    );

            Sensor s3 =
                    generateSensor(
                            "S3",
                            "Village Area"
                    );


            // Find highest-risk sensor

            Sensor critical =
                    findCriticalSensor(
                            s1,
                            s2,
                            s3
                    );


            // Create Disaster Agent

            DisasterAgent agent =
                    new DisasterAgent(
                            critical
                    );


            // Java rule analysis

            agent.applyRules();


            // Export data

            agent.exportData();


            // Run Python

            String risk =
                    agent.runPythonClassifier();


            // Calculate score

            int score =
                    calculateRiskScore(
                            critical
                    );


            // Create JSON

            String json =
                    createJson(
                            critical,
                            score,
                            risk
                    );


            sendResponse(
                    exchange,
                    200,
                    json
            );

        }

        catch (Exception e) {

            e.printStackTrace();

            try {

                addCorsHeaders(exchange);

                sendResponse(
                        exchange,
                        500,
                        "{\"error\":\"" +
                                escapeJson(
                                        e.getMessage()
                                ) +
                                "\"}"
                );

            }

            catch (Exception ignored) {
            }
        }
    }


    // HEALTH API

    private static void handleHealth(
            HttpExchange exchange) {

        try {

            addCorsHeaders(exchange);

            String json =
                    "{\"status\":\"online\"}";

            sendResponse(
                    exchange,
                    200,
                    json
            );

        }

        catch (Exception e) {

            e.printStackTrace();
        }
    }


    // GENERATE SIMULATED SENSOR

    private static Sensor generateSensor(
            String id,
            String location) {

        Random random =
                new Random();

        double temperature =
                30 + random.nextInt(21);

        double rainfall =
                random.nextInt(251);

        double waterLevel =
                30 + random.nextInt(71);

        double windSpeed =
                20 + random.nextInt(101);


        return new Sensor(
                id,
                location,
                temperature,
                rainfall,
                waterLevel,
                windSpeed
        );
    }


    // FIND HIGHEST-RISK SENSOR

    private static Sensor findCriticalSensor(
            Sensor... sensors) {

        Sensor critical =
                sensors[0];

        int highestScore =
                calculateRiskScore(
                        critical
                );


        for (int i = 1;
             i < sensors.length;
             i++) {

            int score =
                    calculateRiskScore(
                            sensors[i]
                    );


            if (score > highestScore) {

                highestScore = score;

                critical = sensors[i];
            }
        }


        return critical;
    }


    // CALCULATE RISK SCORE

    private static int calculateRiskScore(
            Sensor sensor) {

        int score = 0;


        // Temperature

        if (sensor.getTemperature() >= 50) {

            score += 3;

        } else if (
                sensor.getTemperature() >= 40) {

            score += 2;

        } else if (
                sensor.getTemperature() >= 35) {

            score += 1;
        }


        // Rainfall

        if (sensor.getRainfall() >= 200) {

            score += 3;

        } else if (
                sensor.getRainfall() >= 100) {

            score += 2;

        } else if (
                sensor.getRainfall() >= 50) {

            score += 1;
        }


        // Water level

        if (sensor.getWaterLevel() >= 90) {

            score += 3;

        } else if (
                sensor.getWaterLevel() >= 70) {

            score += 2;

        } else if (
                sensor.getWaterLevel() >= 50) {

            score += 1;
        }


        // Wind speed

        if (sensor.getWindSpeed() >= 100) {

            score += 3;

        } else if (
                sensor.getWindSpeed() >= 70) {

            score += 2;

        } else if (
                sensor.getWindSpeed() >= 40) {

            score += 1;
        }


        return score;
    }


    // CREATE JSON RESPONSE

    private static String createJson(
            Sensor sensor,
            int score,
            String risk) {

        return "{"
                + "\"sensorId\":\""
                + escapeJson(sensor.getId())
                + "\","

                + "\"location\":\""
                + escapeJson(sensor.getLocation())
                + "\","

                + "\"temperature\":"
                + sensor.getTemperature()
                + ","

                + "\"rainfall\":"
                + sensor.getRainfall()
                + ","

                + "\"waterLevel\":"
                + sensor.getWaterLevel()
                + ","

                + "\"windSpeed\":"
                + sensor.getWindSpeed()
                + ","

                + "\"riskScore\":"
                + score
                + ","

                + "\"risk\":\""
                + escapeJson(risk)
                + "\""

                + "}";
    }


    // SEND HTTP RESPONSE

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response)
            throws IOException {

        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json; charset=UTF-8"
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


    // CORS

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
    }


    // ESCAPE JSON

    private static String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}