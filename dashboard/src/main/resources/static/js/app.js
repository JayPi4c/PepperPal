// Global references for ApexCharts instances
let moistureChart = null;
let tempHumidChart = null;

document.addEventListener("DOMContentLoaded", () => {
    const htmlElement = document.documentElement;
    const form = document.getElementById("updateForm");
    const startInput = document.getElementById("start");
    const endInput = document.getElementById("end");
    const moistureWrapper = document.getElementById("moisture_wrapper");
    const toggleMoistureBtn = document.getElementById("toggleMoisture");
    const toggleThemeBtn = document.getElementById("toggleTheme");
    const loadingOverlay = document.getElementById("loadingOverlay");

    // --- Theme Management ---
    const getTheme = () => localStorage.getItem("theme") || "light";
    htmlElement.setAttribute("data-bs-theme", getTheme());

    toggleThemeBtn.addEventListener("click", () => {
        const newTheme = htmlElement.getAttribute("data-bs-theme") === "dark" ? "light" : "dark";
        htmlElement.setAttribute("data-bs-theme", newTheme);
        localStorage.setItem("theme", newTheme);

        // ApexCharts has native theme support. We update it live here:
        const apexTheme = newTheme === "dark" ? "dark" : "light";
        if (moistureChart) moistureChart.updateOptions({theme: {mode: apexTheme}});
        if (tempHumidChart) tempHumidChart.updateOptions({theme: {mode: apexTheme}});
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

    // --- Data Fetching ---
    const fetchData = async (start, end) => {
        loadingOverlay.classList.remove("d-none");
        loadingOverlay.classList.add("d-flex");

        try {
            const response = await fetch(`/data?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`);
            if (!response.ok) throw new Error("Network response was not ok");

            const data = await response.json();
            renderCharts(data);
        } catch (error) {
            console.error("Failed to fetch data:", error);
            alert("Could not load chili data. Please try again.");
        } finally {
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
        moistureWrapper.classList.toggle("d-none");
    });

    startInput.addEventListener("click", updateMaxDates);
    endInput.addEventListener("click", updateMaxDates);

    // --- Initialization ---
    initDates();
    fetchData(startInput.value, endInput.value);
});

// --- ApexCharts Rendering ---
function renderCharts(data) {
    // Format data into [timestamp, value] tuples for ApexCharts time-series
    const moistureData = data.map(d => [new Date(d.created).getTime(), d.moistureLevel]);
    const tempData = data.map(d => [new Date(d.created).getTime(), d.temperature]);
    const humidData = data.map(d => [new Date(d.created).getTime(), d.relativeHumidity]);

    const currentTheme = localStorage.getItem("theme") === "dark" ? "dark" : "light";

    // 1. Moisture Chart
    const moistureOptions = {
        series: [{name: 'Moisture', data: moistureData}],
        chart: {
            type: 'area',
            height: '100%',
            background: 'transparent', // Let Bootstrap handle backgrounds
            toolbar: {show: true}, // Built-in zoom/pan tools
            animations: {enabled: true}
        },
        theme: {mode: currentTheme},
        colors: ['#0d6efd'], // Bootstrap Primary
        dataLabels: {enabled: false},
        stroke: {curve: 'smooth', width: 2},
        xaxis: {type: 'datetime'},
        yaxis: {title: {text: 'Moisture Level'}}
    };

    // 2. Temp & Humidity Chart (Multi-axis)
    const tempHumidOptions = {
        series: [
            {name: 'Temperature', type: 'line', data: tempData},
            {name: 'Humidity', type: 'line', data: humidData}
        ],
        chart: {
            height: '100%',
            background: 'transparent',
            toolbar: {show: true},
            animations: {enabled: true}
        },
        theme: {mode: currentTheme},
        colors: ['#dc3545', '#198754'], // Danger Red, Success Green
        dataLabels: {enabled: false},
        stroke: {curve: 'smooth', width: 2},
        xaxis: {type: 'datetime'},
        yaxis: [
            {
                title: {text: 'Temperature (°C)'},
                labels: {style: {colors: '#dc3545'}}
            },
            {
                opposite: true,
                title: {text: 'Humidity (%)'},
                labels: {style: {colors: '#198754'}}
            }
        ],
        tooltip: {shared: true, intersect: false}
    };

    // If charts already exist, gracefully update their data with animations
    if (moistureChart) {
        moistureChart.updateSeries([
            {
                name: 'Moisture',
                data: moistureData
            }
        ]);
    } else {
        moistureChart = new ApexCharts(document.querySelector("#moisture_chart"), moistureOptions);
        moistureChart.render();
    }

    if (tempHumidChart) {
        tempHumidChart.updateSeries([
            {
                name: 'Temperature',
                type: 'line',
                data: tempData
            },
            {
                name: 'Humidity',
                type: 'line',
                data: humidData
            }
        ]);
    } else {
        tempHumidChart = new ApexCharts(document.querySelector("#temp_humid_chart"), tempHumidOptions);
        tempHumidChart.render();
    }
}
