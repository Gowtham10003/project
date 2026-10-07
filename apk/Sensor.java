public class Sensor {

    private String id;
    private String location;

    private double temperature;
    private double rainfall;
    private double windSpeed;

    private int riskScore;
    private String riskLevel;
    private String hazard;

    public Sensor(
            String id,
            String location,
            double temperature,
            double rainfall,
            double windSpeed) {

        this.id = id;
        this.location = location;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getRainfall() {
        return rainfall;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getHazard() {
        return hazard;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setHazard(String hazard) {
        this.hazard = hazard;
    }

    @Override
    public String toString() {

        return "Location: " + location +
                ", Temperature: " + temperature + " °C" +
                ", Rainfall: " + rainfall + " mm" +
                ", Wind Speed: " + windSpeed + " km/h" +
                ", Risk: " + riskLevel +
                ", Score: " + riskScore +
                ", Hazard: " + hazard;
    }
}