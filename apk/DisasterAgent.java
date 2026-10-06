import java.io.*;

public class DisasterAgent {

    private Sensor sensor;


    public DisasterAgent(Sensor sensor) {

        this.sensor = sensor;
    }


    // JAVA RULE ENGINE

    public void applyRules() {

        System.out.println(
                "\n===== JAVA RULE ENGINE ====="
        );


        boolean danger = false;


        if (sensor.getTemperature() > 50) {

            System.out.println(
                    "WARNING: Extremely high temperature"
            );

            danger = true;
        }


        if (sensor.getRainfall() > 200) {

            System.out.println(
                    "WARNING: Heavy rainfall"
            );

            danger = true;
        }


        if (sensor.getWaterLevel() > 90) {

            System.out.println(
                    "WARNING: Critical water level"
            );

            danger = true;
        }


        if (sensor.getWindSpeed() > 100) {

            System.out.println(
                    "WARNING: Very high wind speed"
            );

            danger = true;
        }


        if (!danger) {

            System.out.println(
                    "No critical rule detected."
            );
        }
    }


    // EXPORT SENSOR DATA

    public void exportData()
            throws IOException {

        FileWriter writer =
                new FileWriter(
                        "sensor_data.csv"
                );


        writer.write(
                "temperature,rainfall,waterLevel,windSpeed\n"
        );


        writer.write(
                sensor.getTemperature()
                        + ","
                        + sensor.getRainfall()
                        + ","
                        + sensor.getWaterLevel()
                        + ","
                        + sensor.getWindSpeed()
                        + "\n"
        );


        writer.close();


        System.out.println(
                "\nSensor data exported to sensor_data.csv"
        );
    }


    // RUN PYTHON CLASSIFIER

    public String runPythonClassifier()
            throws IOException,
            InterruptedException {

        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        "python",
                        "risk_classifier.py"
                );


        // Make sure Python runs
        // inside the apk project directory

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


        String result;

        String risk = "";


        while (
                (result = reader.readLine())
                        != null
        ) {

            System.out.println(
                    "Python: " + result
            );


            if (
                    result.startsWith(
                            "RISK="
                    )
            ) {

                risk =
                        result.substring(5)
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


    // GENERATE ALERT

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