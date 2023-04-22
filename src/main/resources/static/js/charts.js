// Load the Visualization API and the corechart package.
google.charts.load('current', {'packages': ['corechart']});

// Set a callback to run when the Google Visualization API is loaded.
google.charts.setOnLoadCallback(drawChart);

$(window).resize(drawChart);

// Callback that creates and populates a data table,
// instantiates the pie chart, passes in the data and
// draws it.
function drawChart() {

    const humid_temp_data = google.visualization.arrayToDataTable(humid_temp_data_array);

    // Set chart options
    const humid_temp_options = {
        enableInteractivity: true,
        title: 'Soil Data',
        hAxis: {
            title: 'created',
            titleTextStyle: {
                color: '#333'
            },
            format: 'HH:mm',
            gridlines: {
                count: 10
            }
        },
        vAxis: {minValue: 0},
        colors: ['red', 'blue'],
        curveType: 'function',
        legend: {position: 'bottom'}
    };

    const moistureLevel_data = google.visualization.arrayToDataTable(moistureLevel_data_array);
    // Instantiate and draw our chart, passing in some options.
    const humid_temp_chart = new google.visualization.LineChart(document.getElementById('temp_humid_div'));
    humid_temp_chart.draw(humid_temp_data, humid_temp_options);

    // Set chart options
    const moistureLevel_options = {
        enableInteractivity: true,
        title: 'Soil Data',
        hAxis: {
            title: 'created',
            titleTextStyle: {
                color: '#333'
            },
            format: 'HH:mm',
            gridlines: {
                count: 10
            }
        },
        colors: ['blue'],
        curveType: 'function',
        legend: {position: 'bottom'}
    };


    // Instantiate and draw our chart, passing in some options.
    const moistureLevel_chart = new google.visualization.LineChart(document.getElementById('moisture_div'));
    moistureLevel_chart.draw(moistureLevel_data, moistureLevel_options);

}