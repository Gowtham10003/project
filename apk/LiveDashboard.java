import javax.swing.*;
import java.awt.*;

public class LiveDashboard extends JFrame {

    private JLabel sensorId;
    private JLabel location;

    private JLabel temperature;
    private JLabel rainfall;
    private JLabel waterLevel;
    private JLabel windSpeed;
    private JLabel riskScore;
    private JLabel riskLevel;
    private JLabel statusLabel;
    private JLabel alert;

    private JButton startButton;
    private JButton stopButton;
    private JButton resetButton;

    public LiveDashboard() {

        setTitle("Disaster Early-Warning System");
        setSize(780, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // ==============================
        // TITLE
        // ==============================

        JLabel title = new JLabel(
                "DISASTER EARLY-WARNING SYSTEM",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 10, 10, 10
                )
        );

        add(title, BorderLayout.NORTH);


        // ==============================
        // DATA PANEL
        // ==============================

        JPanel panel = new JPanel(
                new GridLayout(9, 2, 15, 12)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 60, 15, 60
                )
        );


        panel.add(new JLabel("Sensor ID:"));
        sensorId = new JLabel("--");
        panel.add(sensorId);


        panel.add(new JLabel("Location:"));
        location = new JLabel("--");
        panel.add(location);


        panel.add(new JLabel("Temperature:"));
        temperature = new JLabel("-- °C");
        panel.add(temperature);


        panel.add(new JLabel("Rainfall:"));
        rainfall = new JLabel("-- mm");
        panel.add(rainfall);


        panel.add(new JLabel("Water Level:"));
        waterLevel = new JLabel("-- %");
        panel.add(waterLevel);


        panel.add(new JLabel("Wind Speed:"));
        windSpeed = new JLabel("-- km/h");
        panel.add(windSpeed);


        panel.add(new JLabel("Risk Score:"));
        riskScore = new JLabel("--");
        panel.add(riskScore);


        panel.add(new JLabel("Risk Level:"));
        riskLevel = new JLabel("--");
        panel.add(riskLevel);


        panel.add(new JLabel("Monitoring Status:"));
        statusLabel = new JLabel("STOPPED");
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel);


        for (Component c : panel.getComponents()) {
            c.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            17
                    )
            );
        }


        add(panel, BorderLayout.CENTER);


        // ==============================
        // BUTTONS
        // ==============================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER,
                        15,
                        10
                )
        );


        startButton = new JButton("▶ START");
        stopButton = new JButton("■ STOP");
        resetButton = new JButton("↻ RESET");


        startButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        stopButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        resetButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );


        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);


        stopButton.setEnabled(false);


        // ==============================
        // ALERT
        // ==============================

        alert = new JLabel(
                "Waiting for sensor data...",
                SwingConstants.CENTER
        );

        alert.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        21
                )
        );

        alert.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 15, 10
                )
        );


        JPanel bottomPanel = new JPanel(
                new BorderLayout()
        );

        bottomPanel.add(
                buttonPanel,
                BorderLayout.NORTH
        );

        bottomPanel.add(
                alert,
                BorderLayout.SOUTH
        );


        add(
                bottomPanel,
                BorderLayout.SOUTH
        );


        // ==============================
        // BUTTON EVENTS
        // ==============================

        startButton.addActionListener(e -> {

            setMonitoringStatus(true);

            Main.startMonitoring();
        });


        stopButton.addActionListener(e -> {

            setMonitoringStatus(false);

            Main.stopMonitoring();
        });


        resetButton.addActionListener(e -> {

            Main.stopMonitoring();

            resetDashboard();

            setMonitoringStatus(false);
        });


        setVisible(true);
    }


    // ==============================
    // START / STOP STATUS
    // ==============================

    public void setMonitoringStatus(
            boolean running) {

        if (running) {

            statusLabel.setText("RUNNING");

            statusLabel.setForeground(
                    new Color(0, 128, 0)
            );

            startButton.setEnabled(false);

            stopButton.setEnabled(true);

        } else {

            statusLabel.setText("STOPPED");

            statusLabel.setForeground(
                    Color.RED
            );

            startButton.setEnabled(true);

            stopButton.setEnabled(false);
        }
    }


    // ==============================
    // UPDATE DASHBOARD
    // ==============================

    public void updateData(
            String id,
            String sensorLocation,
            double temp,
            double rain,
            double water,
            double wind,
            int score,
            String risk) {


        sensorId.setText(id);

        location.setText(sensorLocation);


        temperature.setText(
                String.format(
                        "%.1f °C",
                        temp
                )
        );


        rainfall.setText(
                String.format(
                        "%.1f mm",
                        rain
                )
        );


        waterLevel.setText(
                String.format(
                        "%.1f %%",
                        water
                )
        );


        windSpeed.setText(
                String.format(
                        "%.1f km/h",
                        wind
                )
        );


        riskScore.setText(
                String.valueOf(score)
        );


        riskLevel.setText(risk);


        // ==============================
        // LOW
        // ==============================

        if (risk.equals("LOW")) {

            riskLevel.setForeground(
                    new Color(0, 128, 0)
            );

            alert.setForeground(
                    new Color(0, 128, 0)
            );

            alert.setText(
                    "✓ NORMAL CONDITIONS"
            );
        }


        // ==============================
        // MEDIUM
        // ==============================

        else if (risk.equals("MEDIUM")) {

            riskLevel.setForeground(
                    Color.ORANGE
            );

            alert.setForeground(
                    Color.ORANGE
            );

            alert.setText(
                    "⚠ MODERATE DISASTER RISK"
            );
        }


        // ==============================
        // HIGH
        // ==============================

        else if (risk.equals("HIGH")) {

            riskLevel.setForeground(
                    Color.RED
            );

            alert.setForeground(
                    Color.RED
            );

            alert.setText(
                    "⚠ HIGH DISASTER RISK"
            );
        }


        // ==============================
        // DANGER
        // ==============================

        else if (risk.equals("DANGER")) {

            riskLevel.setForeground(
                    Color.RED
            );

            alert.setForeground(
                    Color.RED
            );

            alert.setText(
                    "🚨 EMERGENCY ALERT"
            );


            JOptionPane.showMessageDialog(
                    this,
                    "EMERGENCY ALERT!\n\n"
                    + "Sensor: " + id + "\n"
                    + "Location: "
                    + sensorLocation
                    + "\n\n"
                    + "Immediate disaster "
                    + "response required!",
                    "DANGER",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // ==============================
    // RESET
    // ==============================

    public void resetDashboard() {

        sensorId.setText("--");
        location.setText("--");

        temperature.setText("-- °C");
        rainfall.setText("-- mm");
        waterLevel.setText("-- %");
        windSpeed.setText("-- km/h");

        riskScore.setText("--");
        riskLevel.setText("--");

        riskLevel.setForeground(Color.BLACK);

        alert.setText(
                "Waiting for sensor data..."
        );

        alert.setForeground(Color.BLACK);
    }
}