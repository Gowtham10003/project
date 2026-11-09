
/* =====================================================
   DISASTER EARLY-WARNING AGENT
   Frontend controller for three sensor locations
   ===================================================== */

const API_URL = "http://localhost:8080/api/status";
const UPDATE_INTERVAL = 5000;

let monitoring = false;
let monitoringInterval = null;
let requestInProgress = false;
let historyData = [];
let activityData = [];

const MAX_HISTORY = 12;
const MAX_ACTIVITY = 8;

const $ = (id) => document.getElementById(id);

/* ---------------- DOM ELEMENTS ---------------- */

const startBtn = $("startBtn");
const stopBtn = $("stopBtn");
const resetBtn = $("resetBtn");

const systemStatus = $("systemStatus");
const lastUpdate = $("lastUpdate");

const alertBanner = $("alertBanner");
const alertTitle = $("alertTitle");
const alertMessage = $("alertMessage");

const locationsGrid = $("locationsGrid");
const overallRisk = $("overallRisk");
const averageScore = $("averageScore");
const hazardCount = $("hazardCount");

const bfsOrder = $("bfsOrder");
const historyBody = $("historyBody");
const activityList = $("activityList");

/* ---------------- EVENT LISTENERS ---------------- */

startBtn?.addEventListener("click", startMonitoring);
stopBtn?.addEventListener("click", stopMonitoring);
resetBtn?.addEventListener("click", resetDashboard);

if (stopBtn) stopBtn.disabled = true;

/* ---------------- START MONITORING ---------------- */

function startMonitoring() {
    if (monitoring) return;

    monitoring = true;

    if (startBtn) startBtn.disabled = true;
    if (stopBtn) stopBtn.disabled = false;

    setSystemStatus(true);

    showAlert(
        "normal",
        "Monitoring started. Three simulated locations update every 5 seconds."
    );

    addActivity("Monitoring started in demo mode.");

    fetchSensorData();

    monitoringInterval = setInterval(
        fetchSensorData,
        UPDATE_INTERVAL
    );
}

/* ---------------- STOP MONITORING ---------------- */

function stopMonitoring() {
    monitoring = false;

    if (monitoringInterval !== null) {
        clearInterval(monitoringInterval);
        monitoringInterval = null;
    }

    if (startBtn) startBtn.disabled = false;
    if (stopBtn) stopBtn.disabled = true;

    setSystemStatus(false);

    showAlert(
        "warning",
        "Monitoring paused. No new readings will be requested."
    );

    addActivity("Monitoring paused by user.");
}

/* ---------------- RESET DASHBOARD ---------------- */

function resetDashboard() {
    stopMonitoring();

    historyData = [];
    activityData = [];

    if (locationsGrid) {
        locationsGrid.innerHTML =
            '<div class="empty-state">' +
            'Press Start Monitoring to load the three locations.' +
            '</div>';
    }

    if (overallRisk) {
        overallRisk.textContent = "WAITING";
        overallRisk.className = "";
    }

    if (averageScore) averageScore.textContent = "0 / 10";
    if (hazardCount) hazardCount.textContent = "0";

    if (lastUpdate) lastUpdate.textContent = "Not yet updated";

    if (historyBody) {
        historyBody.innerHTML =
            '<tr><td colspan="4">No history yet.</td></tr>';
    }

    if (activityList) {
        activityList.innerHTML =
            "<li>Monitoring has not started.</li>";
    }

    if (alertBanner) {
        alertBanner.className = "alert-banner hidden";
    }

    ["nodeS1", "nodeS2", "nodeS3"].forEach((id) => {
        const node = $(id);

        if (node) {
            node.setAttribute("class", "node low");
        }
    });

    addActivity("Dashboard reset.");
}

/* ---------------- SYSTEM STATUS ---------------- */

function setSystemStatus(online) {
    if (!systemStatus) return;

    systemStatus.textContent = online
        ? "SYSTEM ONLINE"
        : "SYSTEM PAUSED";

    systemStatus.className =
        "status-pill " + (online ? "online" : "offline");
}

/* ---------------- FETCH SENSOR DATA ---------------- */

async function fetchSensorData() {
    if (requestInProgress || !monitoring) return;

    requestInProgress = true;

    try {
        const response = await fetch(
            API_URL + "?t=" + Date.now(),
            { cache: "no-store" }
        );

        if (!response.ok) {
            throw new Error("API HTTP error: " + response.status);
        }

        const data = await response.json();

        if (
            data.status !== "success" ||
            !Array.isArray(data.locations)
        ) {
            throw new Error(
                "Invalid API response. Expected a locations array."
            );
        }

        if (data.locations.length !== 3) {
            throw new Error(
                "Expected exactly three locations from the Java API."
            );
        }

        updateDashboard(data);
        setSystemStatus(true);

    } catch (error) {
        console.error("Monitoring API error:", error);

        setSystemStatus(false);

        showAlert(
            "danger",
            "Cannot connect to the Java API. Start the backend on port 8080 and try again."
        );

        addActivity("Failed to retrieve sensor data.");

    } finally {
        requestInProgress = false;
    }
}

/* ---------------- UPDATE DASHBOARD ---------------- */

function updateDashboard(data) {
    const locations = data.locations;

    renderLocations(locations);
    updateSummary(locations);
    updateNetwork(locations, data.bfsTraversal);
    recordHistory(locations);

    if (lastUpdate) {
        lastUpdate.textContent = new Date().toLocaleTimeString();
    }

    addActivity("Received environmental readings for three locations.");

    const riskyLocations = locations.filter(
        (location) =>
            ["HIGH", "DANGER"].includes(normalizeRisk(location.risk))
    );

    if (riskyLocations.length > 0) {
        const highest = riskyLocations.reduce((a, b) =>
            riskRank(b.risk) > riskRank(a.risk) ? b : a
        );

        showAlert(
            "danger",
            highest.location +
            ": " + normalizeRisk(highest.risk) +
            " risk. Possible hazard: " +
            (highest.possibleDisaster || "Unknown") +
            "."
        );

        addActivity(
            "Risk alert for " + highest.location +
            ": " + normalizeRisk(highest.risk) + "."
        );

    } else if (
        locations.some(
            (location) => normalizeRisk(location.risk) === "MEDIUM"
        )
    ) {
        showAlert(
            "warning",
            "Elevated environmental conditions detected. Monitor the affected locations."
        );

    } else {
        showAlert(
            "normal",
            "No major risk indicators in the current simulated readings."
        );
    }
}

/* ---------------- LOCATION CARDS ---------------- */

function renderLocations(locations) {
    if (!locationsGrid) return;

    locationsGrid.innerHTML = "";

    locations.forEach((location, index) => {
        const risk = normalizeRisk(location.risk);

        const card = document.createElement("article");
        card.className = "location-card";

        const header = document.createElement("div");
        header.className = "location-head";

        const number = document.createElement("span");
        number.className = "location-number";
        number.textContent = String(index + 1);

        const title = document.createElement("h3");
        title.textContent =
            location.location || "Location " + (index + 1);

        const badge = document.createElement("span");
        badge.className = "risk-badge risk-" + risk;
        badge.textContent = risk;

        header.append(number, title, badge);

        const body = document.createElement("div");
        body.className = "location-body";

        const metrics = document.createElement("div");
        metrics.className = "metrics";

        metrics.append(
            createMetric(
                "Temperature",
                formatNumber(location.temperature, 1) + " °C"
            ),
            createMetric(
                "Precipitation",
                formatNumber(location.rainfall, 1) + " mm"
            ),
            createMetric(
                "Wind speed",
                formatNumber(location.windSpeed, 1) + " km/h"
            ),
            createMetric(
                "Humidity",
                formatNumber(location.humidity, 0) + "%"
            ),
            createMetric(
                "Water level",
                formatNumber(location.waterLevel, 1) + " m"
            ),
            createMetric(
                "Risk score",
                formatNumber(location.riskScore, 0) + " / 10"
            )
        );

        const disasterBox = document.createElement("div");
        disasterBox.className = "disaster-box";

        const disasterTitle = document.createElement("strong");
        disasterTitle.textContent = "Possible disaster";

        const disasterText = document.createElement("span");
        disasterText.textContent =
            location.possibleDisaster ||
            "No specific hazard identified";

        disasterBox.append(disasterTitle, disasterText);

        const explanation = document.createElement("p");
        explanation.className = "explanation";

        const explanationTitle = document.createElement("strong");
        explanationTitle.textContent = "Contributing factors: ";

        explanation.append(
            explanationTitle,
            document.createTextNode(
                location.explanation || "No major warning indicators"
            )
        );

        body.append(metrics, disasterBox, explanation);
        card.append(header, body);
        locationsGrid.appendChild(card);
    });
}

/* ---------------- CREATE METRIC ---------------- */

function createMetric(label, value) {
    const metric = document.createElement("div");
    metric.className = "metric";

    const caption = document.createElement("span");
    caption.textContent = label;

    const number = document.createElement("strong");
    number.textContent = value;

    metric.append(caption, number);

    return metric;
}

/* ---------------- SUMMARY CARDS ---------------- */

function updateSummary(locations) {
    if (!locations.length) return;

    const highest = locations.reduce((a, b) =>
        riskRank(b.risk) > riskRank(a.risk) ? b : a
    );

    const totalScore = locations.reduce(
        (sum, location) => sum + Number(location.riskScore || 0),
        0
    );

    const average = totalScore / locations.length;

    const hazardLocations = locations.filter((location) => {
        const hazard = String(location.possibleDisaster || "");

        return (
            hazard !== "" &&
            !hazard.toLowerCase().includes("no specific hazard")
        );
    }).length;

    if (overallRisk) {
        overallRisk.textContent = normalizeRisk(highest.risk);
        overallRisk.className =
            "risk-" + normalizeRisk(highest.risk);
    }

    if (averageScore) {
        averageScore.textContent = average.toFixed(1) + " / 10";
    }

    if (hazardCount) {
        hazardCount.textContent = String(hazardLocations);
    }
}

/* ---------------- SENSOR GRAPH AND BFS ---------------- */

function updateNetwork(locations, traversal) {
    locations.forEach((location, index) => {
        const node = $("nodeS" + (index + 1));

        if (node) {
            node.setAttribute(
                "class",
                "node " + normalizeRisk(location.risk).toLowerCase()
            );
        }
    });

    if (bfsOrder) {
        bfsOrder.textContent = Array.isArray(traversal)
            ? traversal.join(" → ")
            : "S1 → S2 → S3";
    }
}

/* ---------------- MONITORING HISTORY ---------------- */

function recordHistory(locations) {
    const timestamp = new Date().toLocaleTimeString();

    locations.forEach((location) => {
        historyData.unshift({
            time: timestamp,
            location: location.location || "Unknown",
            risk: normalizeRisk(location.risk),
            hazard: location.possibleDisaster || "Not identified"
        });
    });

    historyData = historyData.slice(0, MAX_HISTORY);

    renderHistory();
}

function renderHistory() {
    if (!historyBody) return;

    historyBody.innerHTML = "";

    if (historyData.length === 0) {
        historyBody.innerHTML =
            '<tr><td colspan="4">No history yet.</td></tr>';
        return;
    }

    historyData.forEach((item) => {
        const row = document.createElement("tr");

        const timeCell = document.createElement("td");
        timeCell.textContent = item.time;

        const locationCell = document.createElement("td");
        locationCell.textContent = item.location;

        const riskCell = document.createElement("td");
        const badge = document.createElement("span");
        badge.className = "risk-badge risk-" + item.risk;
        badge.textContent = item.risk;
        riskCell.appendChild(badge);

        const hazardCell = document.createElement("td");
        hazardCell.textContent = item.hazard;

        row.append(timeCell, locationCell, riskCell, hazardCell);
        historyBody.appendChild(row);
    });
}

/* ---------------- SYSTEM ACTIVITY ---------------- */

function addActivity(message) {
    activityData.unshift(
        new Date().toLocaleTimeString() + " — " + message
    );

    activityData = activityData.slice(0, MAX_ACTIVITY);

    if (!activityList) return;

    activityList.innerHTML = "";

    activityData.forEach((activity) => {
        const item = document.createElement("li");
        item.textContent = activity;
        activityList.appendChild(item);
    });
}

/* ---------------- ALERT BANNER ---------------- */

function showAlert(type, message) {
    if (!alertBanner || !alertMessage || !alertTitle) return;

    alertBanner.className = "alert-banner " + type;

    if (type === "danger") {
        alertTitle.textContent = "RISK ALERT";
    } else if (type === "warning") {
        alertTitle.textContent = "MONITOR";
    } else {
        alertTitle.textContent = "SYSTEM STATUS";
    }

    alertMessage.textContent = message;
}

/* ---------------- UTILITIES ---------------- */

function normalizeRisk(value) {
    const risk = String(value || "LOW").toUpperCase();

    return ["LOW", "MEDIUM", "HIGH", "DANGER"].includes(risk)
        ? risk
        : "LOW";
}

function riskRank(value) {
    const ranks = {
        LOW: 0,
        MEDIUM: 1,
        HIGH: 2,
        DANGER: 3
    };

    return ranks[normalizeRisk(value)];
}

function formatNumber(value, decimals) {
    const number = Number(value);

    return Number.isFinite(number)
        ? number.toFixed(decimals)
        : "--";
}
