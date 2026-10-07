import java.io.*;

public class DisasterAgent {

    private Sensor sensor;

    public DisasterAgent(Sensor sensor) {
        this.sensor = sensor;
    }

    public void applyRules() {

        System.out.println(
                "\n===== JAVA RULE ENGINE ====="
        );

        int score = 0;

        // TEMPERATURE RULES

        if (sensor.getTemperature() >= 50) {

            System.out.println(
                    "WARNING: Extremely high temperature"
            );

            score += 3;

        } else if (sensor.getTemperature() >= 40) {

            System.out.println(
                    "WARNING: High temperature"
            );

            score += 2;

        } else if (sensor.getTemperature() >= 35) {

            score += 1;
        }


        // RAINFALL RULES

        if (sensor.getRainfall() >= 50) {

            System.out.println(
                    "WARNING: Extremely heavy rainfall"
            );

            score += 3;

        } else if (sensor.getRainfall() >= 20) {

            System.out.println(
                    "WARNING: Heavy rainfall"
            );

            score += 2;

        } else if (sensor.getRainfall() >= 10) {

            score += 1;
        }


        // WIND RULES

        if (sensor.getWindSpeed() >= 100) {

            System.out.println(
                    "WARNING: Very high wind speed"
            );

            score += 3;

        } else if (sensor.getWindSpeed() >= 70) {

            System.out.println(
                    "WARNING: High wind speed"
            );

            score += 2;

        } else if (sensor.getWindSpeed() >= 40) {

            score += 1;
        }


        // RISK CLASSIFICATION

        String risk;

        if (score <= 2) {

            risk = "LOW";

        } else if (score <= 5) {

            risk = "MEDIUM";

        } else if (score <= 8) {

            risk = "HIGH";

        } else {

            risk = "DANGER";
        }


        String hazard =
                detectHazard(sensor);


        sensor.setRiskScore(score);
        sensor.setRiskLevel(risk);
        sensor.setHazard(hazard);


        System.out.println(
                "Java Risk Score: " + score
        );

        System.out.println(
                "Java Risk Level: " + risk
        );

        System.out.println(
                "Detected Hazard: " + hazard
        );
    }


    private String detectHazard(
            Sensor sensor) {

        boolean flood =
                sensor.getRainfall() >= 20;

        boolean heat =
                sensor.getTemperature() >= 40;

        boolean wind =
                sensor.getWindSpeed() >= 70;


        if (flood && heat && wind) {

            return "Multiple Hazards";

        } else if (flood) {

            return "Flood Risk";

        } else if (heat) {

            return "Extreme Heat";

        } else if (wind) {

            return "High Wind";

        } else {

            return "Normal";
        }
    }


    public void exportData()
            throws IOException {

        try (
                FileWriter writer =
                        new FileWriter(
                                "sensor_data.csv"
                        )
        ) {

            writer.write(
                    "temperature,rainfall,windSpeed\n"
            );

            writer.write(
                    sensor.getTemperature() +
                    "," +
                    sensor.getRainfall() +
                    "," +
                    sensor.getWindSpeed() +
                    "\n"
            );
        }

        System.out.println(
                "Sensor data exported to sensor_data.csv"
        );
    }


    public String runPythonClassifier()
            throws IOException,
            InterruptedException {

        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        "python",
                        "risk_classifier.py"
                );

        processBuilder.directory(
                new File(".")
        );

        processBuilder.redirectErrorStream(
                true
        );

        Process process =
                processBuilder.start();

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                process.getInputStream()
                        )
                );

        String line;

        String risk = "";

        while (
                (line = reader.readLine())
                        != null
        ) {

            System.out.println(
                    "Python: " + line
            );

            if (
                    line.startsWith("RISK=")
            ) {

                risk =
                        line.substring(5)
                                .trim();
            }
        }

        int exitCode =
                process.waitFor();


        if (exitCode != 0) {

            throw new IOException(
                    "Python classifier failed. Exit code: "
                    + exitCode
            );
        }


        if (risk.isEmpty()) {

            throw new IOException(
                    "Python classifier did not return a risk level."
            );
        }


        return risk;
    }


    public void generateAlert(
            String risk) {

        System.out.println(
                "\n===== ALERT SYSTEM ====="
        );

        switch (risk) {

            case "LOW":

                System.out.println(
                        "STATUS: Normal"
                );

                break;


            case "MEDIUM":

                System.out.println(
                        "WARNING: Moderate disaster risk."
                );

                break;


            case "HIGH":

                System.out.println(
                        "WARNING: High disaster risk!"
                );

                break;


            case "DANGER":

                System.out.println(
                        "!!! EMERGENCY ALERT !!!"
                );

                System.out.println(
                        "DANGER: Immediate disaster response required!"
                );

                break;


            default:

                System.out.println(
                        "Unknown risk level."
                );
        }
    }
}