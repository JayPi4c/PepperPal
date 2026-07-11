// Global chart instances
let moistureChartInstance = null;
let tempHumidChartInstance = null;

document.addEventListener("DOMContentLoaded", () => {
    // --- UI Element References ---
    const htmlElement = document.documentElement;
    const form = document.getElementById("updateForm");
    const startInput = document.getElementById("start");
    const endInput = document.getElementById("end");
    const moistureWrapper = document.getElementById("moisture_wrapper");
    const toggleMoistureBtn = document.getElementById("toggleMoisture");
    const toggleThemeBtn = document.getElementById("toggleTheme");
    const loadingOverlay = document.getElementById("loadingOverlay");

    // --- Theme Management (Bootstrap 5 Native) ---
    const currentTheme = localStorage.getItem("theme") || "light";
    htmlElement.setAttribute("data-bs-theme", currentTheme);

    toggleThemeBtn.addEventListener("click", () => {
        const newTheme = htmlElement.getAttribute("data-bs-theme") === "dark" ? "light" : "dark";
        htmlElement.setAttribute("data-bs-theme", newTheme);
        localStorage.setItem("theme", newTheme);
        // Optional: Re-draw charts here if you want grid lines to dynamically change color
    });

    // --- Date Management ---
    const formatDateTimeLocal = (date) => {
        const pad = n => String(n).padStart(2, "0");
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    };

    const updateMaxDates = () => {
        const now = new Date();
        now.setMilliseconds(0);
        now.setSeconds(0);
        const maxString = formatDateTimeLocal(now);
        startInput.max = maxString;
        endInput.max = maxString;
    };

    const initDates = () => {
        const searchParams = new URLSearchParams(window.location.search);
        let endDate = searchParams.has("end") ? new Date(searchParams.get("end")) : new Date();
        let startDate = searchParams.has("start") ? new Date(searchParams.get("start")) : new Date(endDate);

        if (!searchParams.has("start")) {
            startDate.setDate(startDate.getDate() - 1);
        }

        startDate.setMilliseconds(0);
        startDate.setSeconds(0);
        endDate.setMilliseconds(0);
        endDate.setSeconds(0);

        updateMaxDates();
        startInput.value = formatDateTimeLocal(startDate);
        endInput.value = formatDateTimeLocal(endDate);
    };

    // --- Data Fetching & Loading State ---
    const fetchData = async (start, end) => {
        // Show Bootstrap loading overlay
        loadingOverlay.classList.remove("d-none");
        loadingOverlay.classList.add("d-flex");

        try {
            const response = await fetch(`/data?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`);
            if (!response.ok) throw new Error("Network response was not ok");

            const data = await response.json();
            drawCharts(data);
        } catch (error) {
            console.error("Failed to fetch data:", error);
            alert("Could not load chili data. Please try again.");
        } finally {
            // Hide Bootstrap loading overlay
            loadingOverlay.classList.remove("d-flex");
            loadingOverlay.classList.add("d-none");
        }
    };

    // --- Event Listeners ---
    form.addEventListener("submit", (e) => {
        e.preventDefault();
        updateMaxDates();
        fetchData(startInput.value, endInput.value);
    });

    toggleMoistureBtn.addEventListener("click", () => {
        // Toggle Bootstrap's display-none class
        moistureWrapper.classList.toggle("d-none");
    });

    startInput.addEventListener("click", updateMaxDates);
    endInput.addEventListener("click", updateMaxDates);

    // --- Initialization ---
    initDates();
    fetchData(startInput.value, endInput.value);
});

// --- Chart Rendering ---
function drawCharts(data) {
    const labels = data.map(d => new Date(d.created));

    const commonOptions = {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            x: {
                type: 'time',
                ticks: {autoSkip: true, maxTicksLimit: 20},
                time: {unit: 'minute'}
            }
        },
        elements: {point: {radius: 1}},
        interaction: {
            mode: 'index',
            intersect: false,
        }
    };

    if (moistureChartInstance) moistureChartInstance.destroy();
    if (tempHumidChartInstance) tempHumidChartInstance.destroy();

    const moistureCtx = document.getElementById("moisture_chart");
    moistureChartInstance = new Chart(moistureCtx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [{
                label: "Moisture Level",
                data: data.map(d => d.moistureLevel),
                backgroundColor: "rgba(13, 110, 253, 0.1)", // Bootstrap Primary Blue
                borderColor: "rgba(13, 110, 253, 1)",
                borderWidth: 2,
                fill: true
            }]
        },
        options: commonOptions
    });

    const tempHumidCtx = document.getElementById("temp_humid_chart");
    tempHumidChartInstance = new Chart(tempHumidCtx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [
                {
                    label: "Temperature (°C)",
                    data: data.map(d => d.temperature),
                    backgroundColor: "transparent",
                    borderColor: "rgba(220, 53, 69, 1)", // Bootstrap Danger Red
                    borderWidth: 2
                },
                {
                    label: "Humidity (%)",
                    data: data.map(d => d.relativeHumidity),
                    backgroundColor: "transparent",
                    borderColor: "rgba(25, 135, 84, 1)", // Bootstrap Success Green
                    borderWidth: 2
                }
            ]
        },
        options: commonOptions
    });
}