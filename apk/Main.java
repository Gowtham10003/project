import javax.swing.*;
import java.util.Random;

public class Main {

    private static LiveDashboard dashboard;

    private static volatile boolean monitoring = false;

    private static Thread monitoringThread;


    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("   DISASTER EARLY-WARNING AGENT");
        System.out.println("======================================");


        // =========================================
        // CREATE SENSOR NETWORK
        // =========================================

        SensorNetwork network =
                new SensorNetwork();

        network.addSensor("S1");
        network.addSensor("S2");
        network.addSensor("S3");

        network.connectSensors("S1", "S2");
        network.connectSensors("S2", "S3");


        network.displayNetwork();

        network.BFS("S1");


        // =========================================
        // OPEN DASHBOARD
        // =========================================

        SwingUtilities.invokeLater(() -> {

            dashboard =
                    new LiveDashboard();

        });
    }


    // =========================================
    // START MONITORING
    // =========================================

    public static void startMonitoring() {

        // Prevent multiple monitoring threads

        if (monitoring) {
            return;
        }


        monitoring = true;


        System.out.println(
                "\nMonitoring started..."
        );


        monitoringThread =
                new Thread(() -> {

                    while (monitoring) {

                        try {

                            // Run one monitoring cycle

                            runMonitoringCycle();


                            // Wait 5 seconds

                            Thread.sleep(5000);


                        } catch (
                                InterruptedException e) {

                            Thread.currentThread()
                                    .interrupt();

                            break;


                        } catch (Exception e) {

                            System.out.println(
                                    "Monitoring error: "
                                            + e.getMessage()
                            );

                            e.printStackTrace();

                            break;
                        }
                    }
                });


        monitoringThread.start();
    }


    // =========================================
    // STOP MONITORING
    // =========================================

    public static void stopMonitoring() {

        monitoring = false;


        if (monitoringThread != null) {

            monitoringThread.interrupt();

        }


        System.out.println(
                "\nMonitoring stopped."
        );
    }


    // =========================================
    // ONE MONITORING CYCLE
    // =========================================

    private static void runMonitoringCycle()
            throws Exception {

        System.out.println(
                "\n======================================"
        );

        System.out.println(
                "       NEW SENSOR READINGS"
        );

        System.out.println(
                "======================================"
        );


        // =========================================
        // GENERATE SENSOR READINGS
        // =========================================

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


        // Display readings

        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);


        // =========================================
        // FIND CRITICAL SENSOR
        // =========================================

        Sensor criticalSensor =
                findCriticalSensor(
                        s1,
                        s2,
                        s3
                );


        System.out.println(
                "\nCritical Sensor: "
                        + criticalSensor.getId()
        );


        System.out.println(
                "Location: "
                        + criticalSensor.getLocation()
        );


        // =========================================
        // CREATE DISASTER AGENT
        // =========================================

        DisasterAgent agent =
                new DisasterAgent(
                        criticalSensor
                );


        // =========================================
        // JAVA RULE ENGINE
        // =========================================

        agent.applyRules();


        // =========================================
        // EXPORT SENSOR DATA
        // =========================================

        agent.exportData();


        // =========================================
        // RUN PYTHON CLASSIFIER
        // =========================================

        String risk =
                agent.runPythonClassifier();


        // =========================================
        // CALCULATE RISK SCORE
        // =========================================

        int score =
                calculateRiskScore(
                        criticalSensor
                );


        System.out.println(
                "\nRisk Score: "
                        + score
        );


        System.out.println(
                "Final Risk Level: "
                        + risk
        );


        // =========================================
        // GENERATE ALERT
        // =========================================

        agent.generateAlert(risk);


        // =========================================
        // UPDATE GUI
        // =========================================

        if (dashboard != null) {

            SwingUtilities.invokeLater(() -> {

                dashboard.updateData(

                        criticalSensor.getId(),

                        criticalSensor.getLocation(),

                        criticalSensor.getTemperature(),

                        criticalSensor.getRainfall(),

                        criticalSensor.getWaterLevel(),

                        criticalSensor.getWindSpeed(),

                        score,

                        risk
                );

            });
        }
    }


    // =========================================
    // GENERATE SIMULATED SENSOR DATA
    // =========================================

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


    // =========================================
    // FIND HIGHEST-RISK SENSOR
    // =========================================

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


    // =========================================
    // CALCULATE RISK SCORE
    // =========================================

    private static int calculateRiskScore(
            Sensor sensor) {

        int score = 0;


        // -----------------------------------------
        // TEMPERATURE
        // -----------------------------------------

        if (sensor.getTemperature() >= 50) {

            score += 3;

        } else if (
                sensor.getTemperature() >= 40) {

            score += 2;

        } else if (
                sensor.getTemperature() >= 35) {

            score += 1;
        }


        // -----------------------------------------
        // RAINFALL
        // -----------------------------------------

        if (sensor.getRainfall() >= 200) {

            score += 3;

        } else if (
                sensor.getRainfall() >= 100) {

            score += 2;

        } else if (
                sensor.getRainfall() >= 50) {

            score += 1;
        }


        // -----------------------------------------
        // WATER LEVEL
        // -----------------------------------------

        if (sensor.getWaterLevel() >= 90) {

            score += 3;

        } else if (
                sensor.getWaterLevel() >= 70) {

            score += 2;

        } else if (
                sensor.getWaterLevel() >= 50) {

            score += 1;
        }


        // -----------------------------------------
        // WIND SPEED
        // -----------------------------------------

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
}