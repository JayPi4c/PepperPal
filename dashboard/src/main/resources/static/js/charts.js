$(document).ready(() => {
    // show loading symbol on ajax request
    $(document).on({
        ajaxStart: () => {
            $("body").addClass("loading");
        },
        ajaxStop: () => {
            $("body").removeClass("loading");
        }
    });

    // Format a Date for <input type="datetime-local">
    function formatDateTimeLocal(date) {
        const pad = n => String(n).padStart(2, "0");

        return `${date.getFullYear()}-${
            pad(date.getMonth() + 1)
        }-${
            pad(date.getDate())
        }T${
            pad(date.getHours())
        }:${
            pad(date.getMinutes())
        }`;
    }

    // https://stackoverflow.com/a/42353290
    let searchParams = new URLSearchParams(window.location.search);
    let end_date;

    if (!searchParams.has("end")) {
        end_date = new Date();
    } else {
        end_date = new Date(searchParams.get("end"));
    }

    let start_date;
    if (!searchParams.has("start")) {
        start_date = new Date(end_date);
        start_date.setDate(start_date.getDate() - 1);
    } else {
        start_date = new Date(searchParams.get("start"));
    }

    // Remove seconds and milliseconds
    // https://stackoverflow.com/a/60884408
    start_date.setMilliseconds(0);
    start_date.setSeconds(0);
    end_date.setMilliseconds(0);
    end_date.setSeconds(0);

    let max_date = new Date();
    max_date.setMilliseconds(0);
    max_date.setSeconds(0);

    let max_date_string = formatDateTimeLocal(max_date);

    let start = $("#start");
    start.attr("max", max_date_string);
    start.val(formatDateTimeLocal(start_date));

    let end = $("#end");
    end.attr("max", max_date_string);
    end.val(formatDateTimeLocal(end_date));

    // load data from server
    $.ajax({
        url: `/data?start=${encodeURIComponent(formatDateTimeLocal(start_date))}&end=${encodeURIComponent(formatDateTimeLocal(end_date))}`,
        type: "GET",
        success: result => {
            console.log(result);
            drawChart(result);
        },
        error: err => {
            console.log(err);
        }
    });
});

function drawChart(data) {
    const options = {
        scales: {
            x: {
                type: 'time',
                ticks: {
                    autoSkip: true,
                    maxTicksLimit: 20
                },
                time: {
                    unit: 'minute'
                }
            }
        }, elements: {
            point: {
                radius: 1
            }
        }
    };

    const moisture_ctx = $("#moisture_chart");
    let labels = data.map(d => new Date(d.created));
    new Chart(moisture_ctx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [
                {
                    label: "Moisture",
                    data: data.map(d => d.moistureLevel),
                    backgroundColor: "rgba(0, 0, 0, 0)",
                    borderColor: "rgba(0, 0, 255, 0.5)",
                    borderWidth: 2
                }]
        },
        options: options
    });


    const temp_humid_ctx = $("#temp_humid_chart");
    new Chart(temp_humid_ctx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [
                {
                    label: "Temperature",
                    data: data.map(d => d.temperature),
                    backgroundColor: "rgba(0, 0, 0, 0)",
                    borderColor: "rgba(255, 0, 0, 0.5)"
                },
                {
                    label: "Humidity",
                    data: data.map(d => d.relativeHumidity),
                    backgroundColor: "rgba(0, 0, 0, 0)",
                    borderColor: "rgba(0, 0, 255, 0.5)",
                    borderWidth: 2
                }
            ]
        },
        options: options
    });
}