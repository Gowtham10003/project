import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;

public class ApiServer {
    private static int tick = 0;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/status", ApiServer::handleStatus);
        server.createContext("/api/health", ApiServer::handleHealth);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Disaster Early-Warning API running at http://localhost:8080");
        System.out.println("Status endpoint: http://localhost:8080/api/status");
    }

    private static synchronized void handleStatus(HttpExchange ex) throws IOException {
        cors(ex);
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "{\"status\":\"error\",\"message\":\"GET only\"}");
            return;
        }

        int phase = tick++ % 4;
        List<Sensor> sensors = demoSensors(phase);
        List<Map<String, Object>> locations = new ArrayList<>();
        for (Sensor sensor : sensors) {
            Map<String, Object> row = DisasterAgent.analyze(sensor);
            locations.add(row);
        }

        SensorNetwork network = new SensorNetwork();
        for (Sensor s : sensors) network.addSensor(s.getId());
        network.connectSensors("S1", "S2");
        network.connectSensors("S1", "S3");
        network.connectSensors("S2", "S3");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("mode", "SIMULATED");
        response.put("scenario", phase + 1);
        response.put("updatedAt", new Date().toString());
        response.put("locations", locations);
        response.put("bfsTraversal", network.bfs("S1"));
        response.put("network", network.getGraph());

        String json = toJson(response);
        send(ex, 200, json);
    }

    private static List<Sensor> demoSensors(int phase) {
        // The four scenarios change on each API poll. All values are simulated.
        switch (phase) {
            case 1:
                return Arrays.asList(
                    new Sensor("S1", "Riverside Area", 34.0, 32.0, 28.0, 72.0, 2.8),
                    new Sensor("S2", "Town Center", 38.7, 18.6, 35.2, 48.0, 1.0),
                    new Sensor("S3", "Industrial Zone", 36.0, 25.0, 58.0, 65.0, 1.7));
            case 2:
                return Arrays.asList(
                    new Sensor("S1", "Riverside Area", 32.0, 62.0, 42.0, 78.0, 4.2),
                    new Sensor("S2", "Town Center", 41.5, 3.0, 20.0, 65.0, 0.8),
                    new Sensor("S3", "Industrial Zone", 39.0, 38.0, 82.0, 55.0, 2.0));
            case 3:
                return Arrays.asList(
                    new Sensor("S1", "Riverside Area", 33.0, 48.0, 55.0, 76.0, 3.6),
                    new Sensor("S2", "Town Center", 42.0, 0.0, 18.0, 22.0, 0.5),
                    new Sensor("S3", "Industrial Zone", 43.0, 52.0, 91.0, 68.0, 3.0));
            default:
                return Arrays.asList(
                    new Sensor("S1", "Riverside Area", 30.5, 4.0, 18.0, 52.0, 1.1),
                    new Sensor("S2", "Town Center", 32.4, 8.2, 22.5, 45.0, 0.8),
                    new Sensor("S3", "Industrial Zone", 31.0, 6.0, 25.0, 50.0, 1.0));
        }
    }

    private static void handleHealth(HttpExchange ex) throws IOException {
        cors(ex);
        send(ex, 200, "{\"status\":\"success\",\"service\":\"Disaster Early-Warning API\"}");
    }

    private static void cors(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
    }

    private static void send(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    // Small JSON serializer for the primitive/map/list data used by this API.
    private static String toJson(Object value) {
        if (value == null) return "null";
        if (value instanceof String || value instanceof Character) {
            return "\"" + escape(String.valueOf(value)) + "\"";
        }
        if (value instanceof Number || value instanceof Boolean) return value.toString();
        if (value instanceof Map<?, ?>) {
            StringJoiner join = new StringJoiner(",", "{", "}");
            for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
                join.add(toJson(String.valueOf(e.getKey())) + ":" + toJson(e.getValue()));
            }
            return join.toString();
        }
        if (value instanceof Iterable<?>) {
            StringJoiner join = new StringJoiner(",", "[", "]");
            for (Object item : (Iterable<?>) value) join.add(toJson(item));
            return join.toString();
        }
        return toJson(String.valueOf(value));
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
