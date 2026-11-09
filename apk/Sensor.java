public class Sensor {
    private final String id;
    private final String location;
    private final double temperature;
    private final double rainfall;
    private final double windSpeed;
    private final double humidity;
    private final double waterLevel;

    public Sensor(String id, String location, double temperature,
                  double rainfall, double windSpeed,
                  double humidity, double waterLevel) {
        this.id = id;
        this.location = location;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.waterLevel = waterLevel;
    }

    public String getId() { return id; }
    public String getLocation() { return location; }
    public double getTemperature() { return temperature; }
    public double getRainfall() { return rainfall; }
    public double getWindSpeed() { return windSpeed; }
    public double getHumidity() { return humidity; }
    public double getWaterLevel() { return waterLevel; }
}
