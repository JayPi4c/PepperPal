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

    // redraw on resize
    // $(window).resize(drawChart);
    // load data from server
    $.ajax({
        url: "/data",
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
        },elements: {
            point:{
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