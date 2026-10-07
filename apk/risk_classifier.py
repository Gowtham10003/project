import csv


# ---------------------------------------
# READ SENSOR DATA
# ---------------------------------------

with open("sensor_data.csv", "r") as file:

    reader = csv.DictReader(file)

    data = next(reader)


temperature = float(
    data["temperature"]
)

rainfall = float(
    data["rainfall"]
)

wind_speed = float(
    data["windSpeed"]
)


# ---------------------------------------
# RISK SCORE
# ---------------------------------------

score = 0


# TEMPERATURE

if temperature >= 50:

    score += 3

elif temperature >= 40:

    score += 2

elif temperature >= 35:

    score += 1


# RAINFALL

if rainfall >= 50:

    score += 3

elif rainfall >= 20:

    score += 2

elif rainfall >= 10:

    score += 1


# WIND

if wind_speed >= 100:

    score += 3

elif wind_speed >= 70:

    score += 2

elif wind_speed >= 40:

    score += 1


# ---------------------------------------
# RISK LEVEL
# ---------------------------------------

if score <= 2:

    risk = "LOW"

elif score <= 5:

    risk = "MEDIUM"

elif score <= 8:

    risk = "HIGH"

else:

    risk = "DANGER"


# ---------------------------------------
# OUTPUT
# ---------------------------------------

print(
    "Temperature:",
    temperature
)

print(
    "Rainfall:",
    rainfall
)

print(
    "Wind Speed:",
    wind_speed
)

print(
    "Risk Score:",
    score
)

print(
    "RISK=" + risk
)