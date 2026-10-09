# Disaster Early-Warning Agent

## 1. How to Run

### Step 1: Clone the Repository

Open PowerShell or Command Prompt:

```powershell
git clone https://github.com/Gowtham10003/project.git
cd project
```

If the project is inside the `DWA` folder:

```powershell
cd DWA
```

### Step 2: Requirements

Make sure the following are installed:

- Java JDK
- Python 3
- Git
- Web browser

Check the installations:

```powershell
java -version
javac -version
python --version
git --version
```

### Step 3: Run the Java Backend

Open PowerShell:

```powershell
cd apk
javac *.java
java Main
```

The Java backend starts the HTTP API server.

API endpoint:

```text
http://localhost:8080/api/status
```

Keep this terminal running.

### Step 4: Run the Frontend

Open a **second PowerShell window** and go to the frontend folder:

```powershell
cd frontend
python -m http.server 5500
```

Open the dashboard:

```text
http://localhost:5500
```

### Step 5: Use the Dashboard

After opening the dashboard:

1. Java provides the monitoring data.
2. Temperature, rainfall and wind speed are displayed.
3. The risk score and risk level are displayed.
4. The dashboard refreshes automatically every 5 seconds.
5. A warning is shown when the system detects a high-risk condition.

> **Note:** The project uses simulated/demo environmental data. Physical sensors are not required.

---

## 2. Project Overview

The **Disaster Early-Warning Agent** is a Java and Python based demonstration system for monitoring environmental conditions and identifying possible disaster risk.

The project combines:

- Java sensor modeling
- Sensor-network graph
- Rule-based processing
- Python risk classification
- CSV data exchange
- Java HTTP API
- HTML/CSS/JavaScript dashboard

---

## 3. Project Flow

```text
Simulated Environmental Data
            ↓
       Java Sensors
            ↓
    Sensor Network Graph
            ↓
     Java Rule Engine
            ↓
      sensor_data.csv
            ↓
   Python Risk Classifier
            ↓
      Risk Score/Level
            ↓
       Java API Server
            ↓
      Web Dashboard
            ↓
        Warning
```

---

## 4. How It Works

### Java

Java manages the sensor information and disaster-monitoring logic.

It:

- Creates sensor data.
- Represents the sensor network.
- Applies rule-based processing.
- Exports data to CSV.
- Runs the HTTP API server.

### Python

Python acts as the risk classifier.

It reads the sensor data and calculates an overall risk score.

The result is classified as:

```text
LOW
MEDIUM
HIGH
DANGER
```

### Web Dashboard

The frontend is built using:

- HTML
- CSS
- JavaScript

It communicates with the Java API and displays the latest monitoring information.

The dashboard automatically refreshes every **5 seconds**.

---

## 5. Project Structure

```text
DWA/
├── apk/
│   ├── ApiServer.java
│   ├── DisasterAgent.java
│   ├── Main.java
│   ├── Sensor.java
│   ├── SensorNetwork.java
│   ├── risk_classifier.py
│   └── sensor_data.csv
│
└── frontend/
    ├── index.html
    ├── style.css
    └── script.js
```

---

## 6. Main Features

- Environmental sensor monitoring
- Sensor-network graph representation
- Rule-based risk analysis
- Java and Python integration
- CSV-based data exchange
- Automatic risk classification
- HTTP API
- Web-based monitoring dashboard
- Risk alerts
- Monitoring history
- Simulated data for demonstration

---

## 7. Important Note

This is an **academic demonstration/prototype**.

The project currently uses simulated environmental data instead of physical IoT sensors or live external data.

The same architecture can later be connected to real sensors and live data sources.
