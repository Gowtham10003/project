const API_URL =
    "http://localhost:8080/api/status";


let monitoring = false;

let monitoringInterval = null;


/* =========================
   START MONITORING
========================= */

function startMonitoring() {

    if (monitoring) {
        return;
    }


    monitoring = true;


    document.getElementById("startBtn")
        .disabled = true;


    document.getElementById("stopBtn")
        .disabled = false;


    setSystemStatus(true);


    showAlert(
        "Monitoring Started",
        "Java backend is providing simulated environmental data.",
        "normal"
    );


    // Get data immediately

    fetchSensorData();


    // Get new data every 5 seconds

    monitoringInterval =
        setInterval(
            fetchSensorData,
            5000
        );
}


/* =========================
   STOP MONITORING
========================= */

function stopMonitoring() {

    monitoring = false;


    if (monitoringInterval !== null) {

        clearInterval(
            monitoringInterval
        );

        monitoringInterval = null;
    }


    document.getElementById("startBtn")
        .disabled = false;


    document.getElementById("stopBtn")
        .disabled = true;


    setSystemStatus(false);


    showAlert(
        "Monitoring Stopped",
        "Environmental monitoring has been stopped.",
        "normal"
    );
}


/* =========================
   FETCH JAVA API
========================= */

async function fetchSensorData() {

    try {

        const response =
            await fetch(API_URL);


        if (!response.ok) {

            throw new Error(
                "API HTTP " +
                response.status
            );
        }


        const data =
            await response.json();


        console.log(
            "Java API:",
            data
        );


        updateDashboard(data);


        addHistory(data);


        setSystemStatus(true);

    }

    catch (error) {

        console.error(
            "Backend connection error:",
            error
        );


        setSystemStatus(false);


        showAlert(
            "Backend Connection Error",
            "Make sure ApiServer is running on port 8080.",
            "danger"
        );
    }
}


/* =========================
   UPDATE DASHBOARD
========================= */

function updateDashboard(data) {


    /* ENVIRONMENT */

    document.getElementById("temperature")
        .textContent =
        formatNumber(data.temperature)
        + " °C";


    document.getElementById("rainfall")
        .textContent =
        formatNumber(data.rainfall)
        + " mm";


    document.getElementById("waterLevel")
        .textContent =
        formatNumber(data.waterLevel)
        + " %";


    document.getElementById("windSpeed")
        .textContent =
        formatNumber(data.windSpeed)
        + " km/h";


    /* RISK */

    document.getElementById("riskScore")
        .textContent =
        data.riskScore;


    document.getElementById("riskLevel")
        .textContent =
        data.risk;


    updateRiskVisuals(
        data.risk,
        data.riskScore
    );


    /* SENSOR */

    document.getElementById("sensorId")
        .textContent =
        data.sensorId;


    document.getElementById("sensorLocation")
        .textContent =
        data.location;


    document.getElementById("lastUpdate")
        .textContent =
        new Date().toLocaleTimeString();


    /* CRITICAL SENSOR */

    document.getElementById("criticalStatus")
        .textContent =
        data.risk;


    /* NETWORK */

    updateNetwork(
        data.sensorId
    );


    /* ALERT */

    updateAlert(
        data
    );
}


/* =========================
   RISK VISUALS
========================= */

function updateRiskVisuals(
    risk,
    score
) {

    const circle =
        document.getElementById(
            "riskCircle"
        );


    circle.className =
        "risk-circle";


    circle.classList.add(
        risk.toLowerCase()
    );


    const progress =
        document.getElementById(
            "riskProgress"
        );


    // Maximum score = 12

    const percentage =
        Math.min(
            (score / 12) * 100,
            100
        );


    progress.style.width =
        percentage + "%";


    if (risk === "LOW") {

        progress.style.background =
            "#22c55e";

    }

    else if (risk === "MEDIUM") {

        progress.style.background =
            "#eab308";

    }

    else if (risk === "HIGH") {

        progress.style.background =
            "#f97316";

    }

    else {

        progress.style.background =
            "#ef4444";
    }


    const description =
        document.getElementById(
            "riskDescription"
        );


    if (risk === "LOW") {

        description.textContent =
            "Environmental conditions are currently within normal limits.";

    }

    else if (risk === "MEDIUM") {

        description.textContent =
            "Moderate risk detected. Continue monitoring conditions.";

    }

    else if (risk === "HIGH") {

        description.textContent =
            "High disaster risk detected. Attention is required.";

    }

    else {

        description.textContent =
            "Critical danger detected. Immediate response is required.";
    }
}


/* =========================
   SENSOR NETWORK
========================= */

function updateNetwork(
    criticalSensor
) {

    const nodes = [
        "nodeS1",
        "nodeS2",
        "nodeS3"
    ];


    nodes.forEach(
        id => {

            document
                .getElementById(id)
                .classList
                .remove("critical");

        }
    );


    const criticalNode =
        document.getElementById(
            "node" + criticalSensor
        );


    if (criticalNode) {

        criticalNode.classList.add(
            "critical"
        );
    }
}


/* =========================
   ALERT
========================= */

function updateAlert(data) {

    if (data.risk === "LOW") {

        showAlert(
            "Normal Conditions",
            "No critical environmental condition detected.",
            "normal"
        );

    }

    else if (data.risk === "MEDIUM") {

        showAlert(
            "Moderate Risk Detected",
            "Monitoring should continue at " +
            data.location +
            ".",
            "medium"
        );

    }

    else if (data.risk === "HIGH") {

        showAlert(
            "HIGH DISASTER RISK",
            "High risk detected at " +
            data.location +
            ". Immediate attention is recommended.",
            "high"
        );

    }

    else {

        showAlert(
            "EMERGENCY — DANGER",
            "Immediate disaster response required at " +
            data.location +
            ".",
            "danger"
        );
    }
}


/* =========================
   ALERT BOX
========================= */

function showAlert(
    title,
    message,
    type
) {

    const box =
        document.getElementById(
            "alertBox"
        );


    let icon = "✓";


    if (type === "medium") {

        icon = "!";
    }

    else if (
        type === "high" ||
        type === "danger"
    ) {

        icon = "⚠";
    }


    box.className =
        "alert-box " + type;


    box.innerHTML = `

        <div class="alert-icon">
            ${icon}
        </div>

        <div>

            <strong>
                ${title}
            </strong>

            <p>
                ${message}
            </p>

        </div>

    `;
}


/* =========================
   HISTORY
========================= */

function addHistory(data) {

    const table =
        document.getElementById(
            "historyTable"
        );


    // Remove empty message

    const empty =
        table.querySelector(
            ".empty-history"
        );


    if (empty) {

        empty.parentElement.remove();
    }


    const row =
        document.createElement("tr");


    const riskClass =
        data.risk.toLowerCase();


    row.innerHTML = `

        <td>
            ${new Date().toLocaleTimeString()}
        </td>

        <td>
            <strong>
                ${data.sensorId}
            </strong>
        </td>

        <td>
            ${data.location}
        </td>

        <td>
            <strong>
                ${data.riskScore}
            </strong>
        </td>

        <td>

            <span class="table-risk ${riskClass}">
                ${data.risk}
            </span>

        </td>

    `;


    table.insertBefore(
        row,
        table.firstChild
    );


    // Keep latest 10 records

    while (
        table.children.length > 10
    ) {

        table.removeChild(
            table.lastChild
        );
    }
}


/* =========================
   SYSTEM STATUS
========================= */

function setSystemStatus(
    online
) {

    const dot =
        document.getElementById(
            "statusDot"
        );


    const systemStatus =
        document.getElementById(
            "systemStatus"
        );


    const sidebarDot =
        document.getElementById(
            "sidebarStatusDot"
        );


    const sidebarStatus =
        document.getElementById(
            "sidebarStatus"
        );


    if (online) {

        dot.style.background =
            "#22c55e";


        systemStatus.textContent =
            "SYSTEM ONLINE";


        sidebarDot.style.background =
            "#22c55e";


        sidebarStatus.textContent =
            "Backend Connected";

    }

    else {

        dot.style.background =
            "#ef4444";


        systemStatus.textContent =
            "SYSTEM OFFLINE";


        sidebarDot.style.background =
            "#ef4444";


        sidebarStatus.textContent =
            "Backend Offline";
    }
}


/* =========================
   RESET
========================= */

function resetDashboard() {

    stopMonitoring();


    document.getElementById(
        "temperature"
    ).textContent = "-- °C";


    document.getElementById(
        "rainfall"
    ).textContent = "-- mm";


    document.getElementById(
        "waterLevel"
    ).textContent = "-- %";


    document.getElementById(
        "windSpeed"
    ).textContent = "-- km/h";


    document.getElementById(
        "riskScore"
    ).textContent = "--";


    document.getElementById(
        "riskLevel"
    ).textContent =
        "WAITING";


    document.getElementById(
        "riskCircle"
    ).className =
        "risk-circle";


    document.getElementById(
        "riskProgress"
    ).style.width =
        "0%";


    document.getElementById(
        "sensorId"
    ).textContent =
        "--";


    document.getElementById(
        "sensorLocation"
    ).textContent =
        "No sensor selected";


    document.getElementById(
        "criticalStatus"
    ).textContent =
        "Waiting";


    document.getElementById(
        "lastUpdate"
    ).textContent =
        "--";


    document.getElementById(
        "historyTable"
    ).innerHTML = `

        <tr>

            <td
                colspan="5"
                class="empty-history"
            >
                No monitoring data yet.
            </td>

        </tr>

    `;


    updateNetwork("");


    showAlert(
        "System Ready",
        "Dashboard has been reset.",
        "normal"
    );
}


/* =========================
   FORMAT NUMBER
========================= */

function formatNumber(value) {

    return Number(value)
        .toFixed(1)
        .replace(".0", "");
}


/* =========================
   INITIAL STATUS
========================= */

setSystemStatus(false);