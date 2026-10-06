import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private JLabel temperatureLabel;
    private JLabel rainfallLabel;
    private JLabel waterLevelLabel;
    private JLabel windSpeedLabel;
    private JLabel riskScoreLabel;
    private JLabel riskLevelLabel;
    private JLabel alertLabel;

    public Dashboard(
            double temperature,
            double rainfall,
            double waterLevel,
            double windSpeed,
            String risk,
            int riskScore) {

        setTitle("Disaster Early-Warning System");

        setSize(700, 550);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());


        // =========================
        // HEADER
        // =========================

        JLabel title = new JLabel(
                "DISASTER EARLY-WARNING SYSTEM",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 10, 20, 10
                )
        );

        add(title, BorderLayout.NORTH);


        // =========================
        // SENSOR PANEL
        // =========================

        JPanel sensorPanel = new JPanel(
                new GridLayout(4, 2, 15, 15)
        );

        sensorPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 40, 20, 40
                )
        );


        sensorPanel.add(
                createLabel("Temperature")
        );

        temperatureLabel =
                createValueLabel(
                        temperature + " °C"
                );

        sensorPanel.add(temperatureLabel);


        sensorPanel.add(
                createLabel("Rainfall")
        );

        rainfallLabel =
                createValueLabel(
                        rainfall + " mm"
                );

        sensorPanel.add(rainfallLabel);


        sensorPanel.add(
                createLabel("Water Level")
        );

        waterLevelLabel =
                createValueLabel(
                        waterLevel + " %"
                );

        sensorPanel.add(waterLevelLabel);


        sensorPanel.add(
                createLabel("Wind Speed")
        );

        windSpeedLabel =
                createValueLabel(
                        windSpeed + " km/h"
                );

        sensorPanel.add(windSpeedLabel);


        add(sensorPanel, BorderLayout.CENTER);


        // =========================
        // RESULT PANEL
        // =========================

        JPanel resultPanel = new JPanel(
                new GridLayout(3, 1, 10, 10)
        );

        riskScoreLabel = new JLabel(
                "Risk Score: " + riskScore,
                SwingConstants.CENTER
        );

        riskScoreLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );


        riskLevelLabel = new JLabel(
                "Risk Level: " + risk,
                SwingConstants.CENTER
        );

        riskLevelLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );


        alertLabel = new JLabel(
                getAlertMessage(risk),
                SwingConstants.CENTER
        );

        alertLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );


        resultPanel.add(riskScoreLabel);
        resultPanel.add(riskLevelLabel);
        resultPanel.add(alertLabel);


        add(resultPanel, BorderLayout.SOUTH);


        // Set risk display
        setRiskAppearance(risk);


        setVisible(true);
    }


    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        return label;
    }


    private JLabel createValueLabel(String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font("Arial", Font.PLAIN, 18)
        );

        return label;
    }


    private String getAlertMessage(String risk) {

        switch (risk) {

            case "LOW":
                return "✓ NORMAL CONDITIONS";

            case "MEDIUM":
                return "⚠ MODERATE RISK";

            case "HIGH":
                return "⚠ HIGH DISASTER RISK";

            case "DANGER":
                return "🚨 EMERGENCY ALERT";

            default:
                return "UNKNOWN RISK";
        }
    }


    private void setRiskAppearance(String risk) {

        if (risk.equals("LOW")) {

            riskLevelLabel.setForeground(
                    new Color(0, 128, 0)
            );

            alertLabel.setForeground(
                    new Color(0, 128, 0)
            );

        } else if (risk.equals("MEDIUM")) {

            riskLevelLabel.setForeground(
                    Color.ORANGE
            );

            alertLabel.setForeground(
                    Color.ORANGE
            );

        } else if (risk.equals("HIGH")) {

            riskLevelLabel.setForeground(
                    Color.RED
            );

            alertLabel.setForeground(
                    Color.RED
            );

        } else if (risk.equals("DANGER")) {

            riskLevelLabel.setForeground(
                    Color.RED
            );

            alertLabel.setForeground(
                    Color.RED
            );
        }
    }
}