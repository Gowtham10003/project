import csv

# Read sensor data
with open("sensor_data.csv", "r") as file:

    reader = csv.DictReader(file)

    data = next(reader)

temperature = float(data["temperature"])
rainfall = float(data["rainfall"])
water_level = float(data["waterLevel"])
wind_speed = float(data["windSpeed"])


# Risk score
score = 0


# Temperature
if temperature >= 50:
    score += 3
elif temperature >= 40:
    score += 2
elif temperature >= 35:
    score += 1


# Rainfall
if rainfall >= 200:
    score += 3
elif rainfall >= 100:
    score += 2
elif rainfall >= 50:
    score += 1


# Water level
if water_level >= 90:
    score += 3
elif water_level >= 70:
    score += 2
elif water_level >= 50:
    score += 1


# Wind speed
if wind_speed >= 100:
    score += 3
elif wind_speed >= 70:
    score += 2
elif wind_speed >= 40:
    score += 1


# Final risk classification
if score <= 2:
    risk = "LOW"

elif score <= 5:
    risk = "MEDIUM"

elif score <= 8:
    risk = "HIGH"

else:
    risk = "DANGER"


print("Temperature:", temperature)
print("Rainfall:", rainfall)
print("Water Level:", water_level)
print("Wind Speed:", wind_speed)

print("Risk Score:", score)

print("RISK=" + risk)