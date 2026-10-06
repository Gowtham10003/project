# Disaster Early-Warning Agent

## How to Run the Project

### 1. Clone the Repository

```bash
git clone https://github.com/Gowtham10003/project.git
cd project
```

### 2. Open the Backend Folder

```bash
cd apk
```

The `apk` folder contains the Java backend, Python risk classifier, sensor classes, and API server.

### 3. Check Requirements

Make sure Java and Python are installed.

```bash
java -version
javac -version
python --version
```

### 4. Compile the Java Files

```bash
javac *.java
```

### 5. Start the Java API Server

```bash
java ApiServer
```

The Java API server runs at:

```text
http://localhost:8080
```

You can test the API using:

```text
http://localhost:8080/api/health
```

and:

```text
http://localhost:8080/api/status
```

Keep this terminal running.

### 6. Start the Frontend

Open a second terminal.

Go to the frontend folder:

```bash
cd ../frontend
```

Start the frontend using Python:

```bash
python -m http.server 5500
```

### 7. Open the Dashboard

Open a web browser and visit:

```text
http://localhost:5500
```

The Disaster Early-Warning dashboard will now be displayed.

---

# Project Flow

```text
                    START
                      |
                      v
             +------------------+
             | Simulated Sensors |
             | S1, S2, S3       |
             +--------+---------+
                      |
                      v
             +------------------+
             | Sensor Network   |
             | Graph + BFS      |
             +--------+---------+
                      |
                      v
             +------------------+
             | Sensor Readings  |
             |                  |
             | Temperature      |
             | Rainfall         |
             | Water Level      |
             | Wind Speed       |
             +--------+---------+
                      |
                      v
             +------------------+
             | Find Critical    |
             | Sensor           |
             +--------+---------+
                      |
                      v
             +------------------+
             | Java Rule Engine |
             | Threshold Check  |
             +--------+---------+
                      |
                      v
             +------------------+
             | sensor_data.csv  |
             | Java -> Python   |
             +--------+---------+
                      |
                      v
             +------------------+
             | Python Risk      |
             | Classifier       |
             +--------+---------+
                      |
                      v
             +------------------+
             | Calculate Risk   |
             | Score            |
             +--------+---------+
                      |
                      v
             +------------------+
             | Risk Level       |
             | LOW / MEDIUM     |
             | HIGH / DANGER    |
             +--------+---------+
                      |
                      v
             +------------------+
             | Alert System     |
             +--------+---------+
                      |
                      v
             +------------------+
             | Java API Server  |
             | /api/status      |
             +--------+---------+
                      |
                      | JSON
                      v
             +------------------+
             | Web Dashboard    |
             | HTML/CSS/JS      |
             +------------------+
```

## Detailed Flow

### 1. Sensor Simulation

The Java backend generates simulated environmental readings for multiple sensors.

The monitored parameters are:

- Temperature
- Rainfall
- Water Level
- Wind Speed

> Physical sensors are not currently connected. The project uses simulated sensor data.

### 2. Sensor Network

The sensors are represented as a graph.

Example:

```text
S1 -------- S2 -------- S3
```

Breadth-First Search (BFS) is used to traverse the sensor network.

### 3. Critical Sensor Selection

The system calculates a risk score for each simulated sensor and selects the sensor with the highest risk score as the critical sensor.

### 4. Java Rule Engine

The Java rule engine checks environmental values against predefined thresholds.

Examples:

```text
High Temperature
Heavy Rainfall
Critical Water Level
Very High Wind Speed
```

### 5. CSV Data Export

The selected sensor data is written to:

```text
sensor_data.csv
```

This file acts as the data exchange mechanism between Java and Python.

### 6. Python Risk Classification

The Java application executes:

```text
risk_classifier.py
```

Python reads the sensor data from the CSV file, calculates the risk score, and determines the overall risk level.

The possible risk levels are:

```text
LOW
MEDIUM
HIGH
DANGER
```

Python returns the result to Java in the form:

```text
RISK=HIGH
```

### 7. Alert Generation

Java receives the risk level from Python and generates the corresponding alert.

```text
LOW     -> Normal
MEDIUM  -> Moderate disaster risk
HIGH    -> High disaster risk warning
DANGER  -> Emergency alert
```

### 8. API Communication

`ApiServer.java` exposes the processed sensor information through:

```text
GET /api/status
```

The API returns JSON data containing:

- Sensor ID
- Location
- Temperature
- Rainfall
- Water Level
- Wind Speed
- Risk Score
- Risk Level

### 9. Web Dashboard

The frontend is built using:

```text
HTML + CSS + JavaScript
```

JavaScript requests data from the Java API and displays the current sensor information and risk status on the dashboard.

## Complete Data Flow

```text
Simulated Sensor Data
        |
        v
   Java Sensor
        |
        v
 Sensor Network + BFS
        |
        v
Critical Sensor Selection
        |
        v
 Java Rule Engine
        |
        v
 sensor_data.csv
        |
        v
 Python Classifier
        |
        v
 Risk Score + Risk Level
        |
        v
 Java Alert System
        |
        v
 Java API Server
        |
        v
 JSON Response
        |
        v
 Web Dashboard
```

## One-Line Project Flow

```text
Simulated Sensors -> Java -> Rule Engine -> CSV -> Python -> Risk Level -> Alert -> API -> Web Dashboard
```
