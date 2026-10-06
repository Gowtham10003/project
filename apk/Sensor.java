public class Sensor {

    private String id;
    private String location;
    private double temperature;
    private double rainfall;
    private double waterLevel;
    private double windSpeed;

    public Sensor(String id, String location,
                  double temperature,
                  double rainfall,
                  double waterLevel,
                  double windSpeed) {

        this.id = id;
        this.location = location;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.waterLevel = waterLevel;
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

    public double getWaterLevel() {
        return waterLevel;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    @Override
    public String toString() {

        return "Sensor ID: " + id +
                ", Location: " + location +
                ", Temperature: " + temperature +
                "°C, Rainfall: " + rainfall +
                "mm, Water Level: " + waterLevel +
                "%, Wind Speed: " + windSpeed +
                "km/h";
    }
}