import java.util.*;

public class SensorNetwork {
    private final Map<String, List<String>> graph = new LinkedHashMap<>();

    public void addSensor(String id) {
        graph.putIfAbsent(id, new ArrayList<>());
    }

    public void connectSensors(String a, String b) {
        addSensor(a);
        addSensor(b);
        if (!graph.get(a).contains(b)) graph.get(a).add(b);
        if (!graph.get(b).contains(a)) graph.get(b).add(a);
    }

    public List<String> bfs(String start) {
        List<String> order = new ArrayList<>();
        if (!graph.containsKey(start)) return order;
        Set<String> visited = new LinkedHashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            String current = queue.remove();
            order.add(current);
            for (String next : graph.get(current)) {
                if (visited.add(next)) queue.add(next);
            }
        }
        return order;
    }

    public Map<String, List<String>> getGraph() {
        return Collections.unmodifiableMap(graph);
    }
}
