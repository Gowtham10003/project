"""
Standalone Python rule classifier for the Disaster Early-Warning Agent.
Input: one JSON object on stdin.
Output: one JSON object on stdout.
The Java demo currently applies equivalent rules in DisasterAgent.java so the
dashboard works without external dependencies. This script is included for
the Java/Python integration stage.
"""
import json
import sys

def classify(d):
    temp = float(d.get("temperature", 0))
    rain = float(d.get("rainfall", 0))
    wind = float(d.get("windSpeed", 0))
    humidity = float(d.get("humidity", 50))
    water = float(d.get("waterLevel", 0))
    score = 0
    factors, hazards = [], []

    if rain >= 50:
        score += 3; factors.append("Very heavy rainfall")
    elif rain >= 25:
        score += 2; factors.append("Heavy rainfall")
    elif rain >= 10:
        score += 1; factors.append("Moderate rainfall")

    if water >= 4:
        score += 3; factors.append("High water level")
    elif water >= 2.5:
        score += 2; factors.append("Elevated water level")
    if rain >= 25 and water >= 2.5:
        hazards.append("Possible flood")

    if temp >= 40:
        score += 3; factors.append("Extreme temperature")
    elif temp >= 35:
        score += 1; factors.append("High temperature")
    if temp >= 40 and humidity >= 60:
        hazards.append("Possible heat stress / heatwave")

    if wind >= 80:
        score += 3; factors.append("Very strong wind")
    elif wind >= 50:
        score += 2; factors.append("Strong wind")
    elif wind >= 35:
        score += 1; factors.append("Elevated wind speed")
    if wind >= 50 and rain >= 20:
        hazards.append("Possible severe storm")

    if temp >= 35 and humidity <= 25 and rain < 5:
        score += 2
        factors.append("Hot and dry conditions")
        hazards.append("Wildfire-favorable conditions")

    score = min(score, 10)
    risk = "DANGER" if score >= 8 else "HIGH" if score >= 5 else "MEDIUM" if score >= 2 else "LOW"
    return {
        "risk": risk,
        "riskScore": score,
        "possibleDisaster": ", ".join(hazards) if hazards else "No specific hazard identified",
        "explanation": ", ".join(factors) if factors else "No major warning indicators"
    }

if __name__ == "__main__":
    payload = json.load(sys.stdin)
    print(json.dumps(classify(payload)))
