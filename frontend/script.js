// ==========================================
// DISASTER EARLY-WARNING AGENT
// FRONTEND CONTROLLER
// ==========================================


// ==========================================
// CONFIGURATION
// ==========================================

const API_URL =
    "http://localhost:8080/api/status";

const UPDATE_INTERVAL =
    5000;


// ==========================================
// STATE
// ==========================================

let monitoring = false;

let monitoringInterval = null;

let requestInProgress = false;

let historyData = [];

const MAX_HISTORY = 10;


// ==========================================
// DOM ELEMENTS
// ==========================================

const startBtn =
    document.getElementById("startBtn");

const stopBtn =
    document.getElementById("stopBtn");

const resetBtn =
    document.getElementById("resetBtn");


const temperature =
    document.getElementById("temperature");

const rainfall =
    document.getElementById("rainfall");

const windSpeed =
    document.getElementById("windSpeed");


const riskLevel =
    document.getElementById("riskLevel");

const riskScore =
    document.getElementById("riskScore");

const riskProgress =
    document.getElementById("riskProgress");

const riskDescription =
    document.getElementById(
        "riskDescription"
    );


const sensorId =
    document.getElementById("sensorId");

const sensorLocation =
    document.getElementById(
        "sensorLocation"
    );


const scenarioNumber =
    document.getElementById(
        "scenarioNumber"
    );


const dataSource =
    document.getElementById(
        "dataSource"
    );


const systemStatus =
    document.getElementById(
        "systemStatus"
    );


const systemAlert =
    document.getElementById(
        "systemAlert"
    );


const hazardValue =
    document.getElementById(
        "hazardValue"
    );


const hazardStatus =
    document.getElementById(
        "hazardStatus"
    );


const riskIcon =
    document.getElementById(
        "riskIcon"
    );


const lastUpdate =
    document.getElementById(
        "lastUpdate"
    );


const historyBody =
    document.getElementById(
        "historyBody"
    );


const currentTime =
    document.getElementById(
        "currentTime"
    );


// ==========================================
// START MONITORING
// ==========================================

function startMonitoring() {

    if (monitoring) {
        return;
    }


    monitoring = true;


    startBtn.disabled = true;

    stopBtn.disabled = false;


    setSystemOnline();


    showAlert(
        "normal",
        "Monitoring started. Demo sensor data will update every 5 seconds."
    );


    // Get first data immediately

    fetchSensorData();


    // Then update every 5 seconds

    monitoringInterval =
        setInterval(
            fetchSensorData,
            UPDATE_INTERVAL
        );
}


// ==========================================
// STOP MONITORING
// ==========================================

function stopMonitoring() {

    monitoring = false;


    if (monitoringInterval) {

        clearInterval(
            monitoringInterval
        );

        monitoringInterval = null;
    }


    startBtn.disabled = false;

    stopBtn.disabled = true;


    systemStatus.textContent =
        "● SYSTEM PAUSED";

    systemStatus.className =
        "system-status offline";


    showAlert(
        "warning",
        "Monitoring stopped by user."
    );
}


// ==========================================
// FETCH DATA
// ==========================================

async function fetchSensorData() {

    if (
        requestInProgress ||
        !monitoring
    ) {
        return;
    }


    requestInProgress = true;


    try {

        const response =
            await fetch(
                API_URL +
                "?t=" +
                Date.now()
            );


        if (!response.ok) {

            throw new Error(
                "API returned HTTP " +
                response.status
            );
        }


        const data =
            await response.json();


        updateDashboard(data);


    } catch (error) {

        console.error(
            "Dashboard API error:",
            error
        );


        setSystemOffline();


        showAlert(
            "danger",
            "Unable to connect to Java API. Make sure the Java server is running on port 8080."
        );


    } finally {

        requestInProgress = false;
    }
}


// ==========================================
// UPDATE DASHBOARD
// ==========================================

function updateDashboard(data) {

    if (
        !data ||
        data.status !== "success"
    ) {
        return;
    }


    // -------------------------------
    // ENVIRONMENT
    // -------------------------------

    temperature.textContent =
        Number(data.temperature)
            .toFixed(1) +
        " °C";


    rainfall.textContent =
        Number(data.rainfall)
            .toFixed(1) +
        " mm";


    windSpeed.textContent =
        Number(data.windSpeed)
            .toFixed(1) +
        " km/h";


    // -------------------------------
    // SENSOR
    // -------------------------------

    sensorId.textContent =
        data.sensorId;


    sensorLocation.textContent =
        data.location;


    // -------------------------------
    // SCENARIO
    // -------------------------------

    scenarioNumber.textContent =
        "Scenario " +
        data.scenario;


    dataSource.textContent =
        data.source ||
        "SIMULATED DATA";


    // -------------------------------
    // RISK
    // -------------------------------

    const risk =
        String(data.risk)
            .toUpperCase();


    const score =
        Number(data.riskScore);


    riskLevel.textContent =
        risk;


    riskScore.textContent =
        score;


    updateRiskVisual(
        risk,
        score
    );


    // -------------------------------
    // HAZARD
    // -------------------------------

    updateHazard(
        data.hazard,
        risk
    );


    // -------------------------------
    // ALERT
    // -------------------------------

    updateAlert(risk);


    // -------------------------------
    // TIME
    // -------------------------------

    const now =
        new Date();


    lastUpdate.textContent =
        "Last update: " +
        now.toLocaleTimeString();


    // -------------------------------
    // HISTORY
    // -------------------------------

    addHistory(data);
}


// ==========================================
// RISK VISUAL
// ==========================================

function updateRiskVisual(
    risk,
    score
) {

    const percentage =
        Math.min(
            100,
            Math.max(
                0,
                (score / 9) * 100
            )
        );


    riskProgress.style.width =
        percentage + "%";


    // Remove previous classes

    riskLevel.classList.remove(
        "risk-low",
        "risk-medium",
        "risk-high",
        "risk-danger"
    );


    riskProgress.classList.remove(
        "risk-low",
        "risk-medium",
        "risk-high",
        "risk-danger"
    );


    switch (risk) {

        case "LOW":

            riskLevel.classList.add(
                "risk-low"
            );

            riskProgress.classList.add(
                "risk-low"
            );

            riskIcon.textContent =
                "✓";

            riskDescription.textContent =
                "Normal conditions";

            break;


        case "MEDIUM":

            riskLevel.classList.add(
                "risk-medium"
            );

            riskProgress.classList.add(
                "risk-medium"
            );

            riskIcon.textContent =
                "!";

            riskDescription.textContent =
                "Moderate risk";

            break;


        case "HIGH":

            riskLevel.classList.add(
                "risk-high"
            );

            riskProgress.classList.add(
                "risk-high"
            );

            riskIcon.textContent =
                "⚠";

            riskDescription.textContent =
                "High disaster risk";

            break;


        case "DANGER":

            riskLevel.classList.add(
                "risk-danger"
            );

            riskProgress.classList.add(
                "risk-danger"
            );

            riskIcon.textContent =
                "!!";

            riskDescription.textContent =
                "Emergency conditions";

            break;
    }
}


// ==========================================
// HAZARD
// ==========================================

function updateHazard(
    hazard,
    risk
) {

    hazardValue.textContent =
        hazard || "Normal";


    hazardStatus.classList.remove(
        "normal",
        "warning",
        "danger"
    );


    if (risk === "LOW") {

        hazardStatus.classList.add(
            "normal"
        );

        hazardStatus.textContent =
            "No immediate threat";

    } else if (
        risk === "MEDIUM"
    ) {

        hazardStatus.classList.add(
            "warning"
        );

        hazardStatus.textContent =
            "Monitor conditions";

    } else {

        hazardStatus.classList.add(
            "danger"
        );

        hazardStatus.textContent =
            "Immediate attention required";
    }
}


// ==========================================
// ALERT
// ==========================================

function updateAlert(risk) {

    switch (risk) {

        case "LOW":

            showAlert(
                "normal",
                "✓ NORMAL: Environmental conditions are within safe limits."
            );

            break;


        case "MEDIUM":

            showAlert(
                "warning",
                "⚠ WARNING: Moderate disaster risk detected. Continue monitoring."
            );

            break;


        case "HIGH":

            showAlert(
                "high",
                "⚠ HIGH RISK: Significant disaster risk detected."
            );

            break;


        case "DANGER":

            showAlert(
                "danger",
                "🚨 EMERGENCY: Dangerous environmental conditions detected. Immediate disaster response may be required."
            );

            break;
    }
}


// ==========================================
// SHOW ALERT
// ==========================================

function showAlert(
    type,
    message
) {

    systemAlert.className =
        "system-alert " +
        type;


    systemAlert.textContent =
        message;
}


// ==========================================
// HISTORY
// ==========================================

function addHistory(data) {

    const now =
        new Date();


    const item = {

        time:
            now.toLocaleTimeString(),

        scenario:
            data.scenario,

        sensor:
            data.sensorId,

        temperature:
            Number(data.temperature),

        rainfall:
            Number(data.rainfall),

        wind:
            Number(data.windSpeed),

        score:
            Number(data.riskScore),

        risk:
            String(data.risk)
                .toUpperCase()
    };


    historyData.unshift(item);


    if (
        historyData.length >
        MAX_HISTORY
    ) {

        historyData =
            historyData.slice(
                0,
                MAX_HISTORY
            );
    }


    renderHistory();
}


// ==========================================
// RENDER HISTORY
// ==========================================

function renderHistory() {

    if (
        historyData.length === 0
    ) {

        historyBody.innerHTML = `

            <tr>

                <td
                    colspan="8"
                    class="empty-row"
                >
                    No monitoring data yet.
                </td>

            </tr>

        `;

        return;
    }


    historyBody.innerHTML =
        historyData.map(
            item => `

                <tr>

                    <td>
                        ${item.time}
                    </td>

                    <td>
                        #${item.scenario}
                    </td>

                    <td>
                        ${item.sensor}
                    </td>

                    <td>
                        ${item.temperature.toFixed(1)} °C
                    </td>

                    <td>
                        ${item.rainfall.toFixed(1)} mm
                    </td>

                    <td>
                        ${item.wind.toFixed(1)} km/h
                    </td>

                    <td>
                        ${item.score}/9
                    </td>

                    <td>
                        <span
                            class="table-risk ${getRiskClass(item.risk)}"
                        >
                            ${item.risk}
                        </span>
                    </td>

                </tr>

            `
        ).join("");
}


// ==========================================
// RISK CLASS
// ==========================================

function getRiskClass(risk) {

    switch (risk) {

        case "LOW":
            return "low";

        case "MEDIUM":
            return "medium";

        case "HIGH":
            return "high";

        case "DANGER":
            return "danger";

        default:
            return "low";
    }
}


// ==========================================
// SYSTEM ONLINE
// ==========================================

function setSystemOnline() {

    systemStatus.textContent =
        "● SYSTEM ONLINE";

    systemStatus.className =
        "system-status online";
}


// ==========================================
// SYSTEM OFFLINE
// ==========================================

function setSystemOffline() {

    systemStatus.textContent =
        "● SYSTEM OFFLINE";

    systemStatus.className =
        "system-status offline";
}


// ==========================================
// RESET
// ==========================================

function resetDashboard() {

    stopMonitoring();


    historyData = [];


    temperature.textContent =
        "-- °C";


    rainfall.textContent =
        "-- mm";


    windSpeed.textContent =
        "-- km/h";


    riskLevel.textContent =
        "READY";


    riskLevel.classList.remove(
        "risk-low",
        "risk-medium",
        "risk-high",
        "risk-danger"
    );


    riskScore.textContent =
        "--";


    riskProgress.style.width =
        "0%";


    riskDescription.textContent =
        "Waiting for monitoring...";


    riskIcon.textContent =
        "?";


    sensorId.textContent =
        "--";


    sensorLocation.textContent =
        "Waiting for data";


    scenarioNumber.textContent =
        "Waiting";


    dataSource.textContent =
        "SIMULATED DATA";


    hazardValue.textContent =
        "Normal";


    hazardStatus.className =
        "hazard-status normal";


    hazardStatus.textContent =
        "No immediate threat";


    lastUpdate.textContent =
        "Last update: --";


    systemAlert.className =
        "system-alert hidden";


    renderHistory();
}


// ==========================================
// CLOCK
// ==========================================

function updateClock() {

    const now =
        new Date();


    currentTime.textContent =
        now.toLocaleString();
}


setInterval(
    updateClock,
    1000
);


updateClock();


// ==========================================
// BUTTON EVENTS
// ==========================================

startBtn.addEventListener(
    "click",
    startMonitoring
);


stopBtn.addEventListener(
    "click",
    stopMonitoring
);


resetBtn.addEventListener(
    "click",
    resetDashboard
);


// ==========================================
// INITIAL STATE
// ==========================================

renderHistory();