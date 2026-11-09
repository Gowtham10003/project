import java.util.*;

public class DisasterAgent {
    public static Map<String, Object> analyze(Sensor s) {
        int score = 0;
        List<String> factors = new ArrayList<>();
        List<String> hazards = new ArrayList<>();

        // Rainfall and water-level indicators for flood risk.
        if (s.getRainfall() >= 50) {
            score += 3; factors.add("Very heavy rainfall");
        } else if (s.getRainfall() >= 25) {
            score += 2; factors.add("Heavy rainfall");
        } else if (s.getRainfall() >= 10) {
            score += 1; factors.add("Moderate rainfall");
        }

        if (s.getWaterLevel() >= 4.0) {
            score += 3; factors.add("High water level");
        } else if (s.getWaterLevel() >= 2.5) {
            score += 2; factors.add("Elevated water level");
        }

        if (s.getRainfall() >= 25 && s.getWaterLevel() >= 2.5) {
            hazards.add("Possible flood");
        }

        // Heat conditions.
        if (s.getTemperature() >= 40) {
            score += 3; factors.add("Extreme temperature");
        } else if (s.getTemperature() >= 35) {
            score += 1; factors.add("High temperature");
        }

        if (s.getTemperature() >= 40 && s.getHumidity() >= 60) {
            hazards.add("Possible heat stress / heatwave");
        }

        // Wind and storm conditions.
        if (s.getWindSpeed() >= 80) {
            score += 3; factors.add("Very strong wind");
        } else if (s.getWindSpeed() >= 50) {
            score += 2; factors.add("Strong wind");
        } else if (s.getWindSpeed() >= 35) {
            score += 1; factors.add("Elevated wind speed");
        }

        if (s.getWindSpeed() >= 50 && s.getRainfall() >= 20) {
            hazards.add("Possible severe storm");
        }

        // Dry, hot conditions are only an indicator of fire-favorable weather.
        if (s.getTemperature() >= 35 && s.getHumidity() <= 25
                && s.getRainfall() < 5) {
            hazards.add("Wildfire-favorable conditions");
            score += 2;
            factors.add("Hot and dry conditions");
        }

        score = Math.min(score, 10);
        String risk;
        if (score >= 8) risk = "DANGER";
        else if (score >= 5) risk = "HIGH";
        else if (score >= 2) risk = "MEDIUM";
        else risk = "LOW";

        if (hazards.isEmpty()) hazards.add("No specific hazard identified");
        if (factors.isEmpty()) factors.add("No major warning indicators");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sensorId", s.getId());
        result.put("location", s.getLocation());
        result.put("temperature", s.getTemperature());
        result.put("rainfall", s.getRainfall());
        result.put("windSpeed", s.getWindSpeed());
        result.put("humidity", s.getHumidity());
        result.put("waterLevel", s.getWaterLevel());
        result.put("risk", risk);
        result.put("riskScore", score);
        result.put("possibleDisaster", String.join(", ", hazards));
        result.put("explanation", String.join(", ", factors));
        result.put("source", "SIMULATED DEMO DATA");
        return result;
    }
}
