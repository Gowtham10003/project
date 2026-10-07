import java.util.*;

public class SensorNetwork {

    private Map<String, List<String>> graph;

    public SensorNetwork() {
        graph = new HashMap<>();
    }

    public void addSensor(String sensorId) {

        graph.putIfAbsent(
                sensorId,
                new ArrayList<>()
        );
    }

    public void connectSensors(
            String sensor1,
            String sensor2) {

        graph.putIfAbsent(
                sensor1,
                new ArrayList<>()
        );

        graph.putIfAbsent(
                sensor2,
                new ArrayList<>()
        );

        graph.get(sensor1).add(sensor2);
        graph.get(sensor2).add(sensor1);
    }

    public void displayNetwork() {

        System.out.println(
                "\n===== SENSOR NETWORK ====="
        );

        for (String sensor : graph.keySet()) {

            System.out.println(
                    sensor + " -> " +
                    graph.get(sensor)
            );
        }
    }

    public void BFS(String startSensor) {

        if (!graph.containsKey(startSensor)) {

            System.out.println(
                    "Sensor not found."
            );

            return;
        }

        Set<String> visited =
                new HashSet<>();

        Queue<String> queue =
                new LinkedList<>();

        queue.add(startSensor);
        visited.add(startSensor);

        System.out.println(
                "\nBFS Sensor Network Traversal:"
        );

        while (!queue.isEmpty()) {

            String current =
                    queue.poll();

            System.out.print(
                    current + " "
            );

            for (
                    String neighbour :
                    graph.get(current)
            ) {

                if (!visited.contains(neighbour)) {

                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        System.out.println();
    }
}