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

    // https://stackoverflow.com/a/42353290
    let searchParams = new URLSearchParams(window.location.search)
    let end_date;

    if (!searchParams.has("end")) {
        end_date = new Date();
    } else {
        end_date = new Date(searchParams.get("end"));
    }
    end_date.setMinutes(end_date.getMinutes() - end_date.getTimezoneOffset());

    let start_date;
    if (!searchParams.has("start")) {
        start_date = new Date(end_date);
        start_date.setDate(start_date.getDate() - 1);
    } else {
        start_date = new Date(searchParams.get("start"));
    }


    // https://stackoverflow.com/a/60884408
    start_date.setMilliseconds(null);
    start_date.setSeconds(null);
    end_date.setMilliseconds(null);
    end_date.setSeconds(null);

    let max_date = new Date();
    max_date.setMinutes(max_date.getMinutes() - max_date.getTimezoneOffset());
    let max_date_string = max_date.toISOString().slice(0, -1);


    let start = $("#start");
    start.attr("max", max_date_string);
    start.val(start_date.toISOString().slice(0, -1));

    let end = $("#end");
    end.attr("max", max_date_string);
    end.val(end_date.toISOString().slice(0, -1));

    // load data from server
    $.ajax({
        url: `/data?start=${encodeURIComponent(start_date.toISOString().slice(0, -1))}&end=${encodeURIComponent(end_date.toISOString().slice(0, -1))}`,
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
    const moisture_chart = new Chart(moisture_ctx, {
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
                }],
        },
        options: options
    });


    const temp_humid_ctx = $("#temp_humid_chart");
    const temp_humid_chart = new Chart(temp_humid_ctx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [
                {
                    label: "Temperature",
                    data: data.map(d => d.temperature),
                    backgroundColor: "rgba(0, 0, 0, 0)",
                    borderColor: "rgba(255, 0, 0, 0.5)",
                },
                {
                    label: "Humidity",
                    data: data.map(d => d.relativeHumidity),
                    backgroundColor: "rgba(0, 0, 0, 0)",
                    borderColor: "rgba(0, 0, 255, 0.5)",
                    borderWidth: 2
                }
            ],
        },
        options: options
    });
}