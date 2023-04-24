// Load the Visualization API and the corechart package.
google.charts.load('current', {'packages': ['corechart']});

// Set a callback to run when the Google Visualization API is loaded.
google.charts.setOnLoadCallback(prepareAndDrawChart);

// redraw on resize
$(window).resize(drawChart);


// humid and temperature chart
let humid_temp_data;
let humid_temp_chart;

// prepare data
const humid_temp_data_array = [['created', 'temperature', 'humidity']];
for (entry of data) {
    humid_temp_data_array.push([entry.created, entry.temperature, entry.relativeHumidity]);
}

// Set chart options
const humid_temp_options = {
    backgroundColor: 'transparent',
    enableInteractivity: true,
    title: 'Soil Data',
    titleTextStyle: {color: '#787878'},
    hAxis: {
        title: 'created',
        titleTextStyle: {
            color: '#787878'
        },
        format: 'HH:mm',
        gridlines: {
            count: 10,
            color: '#787878'
        }
    },
    vAxis: {minValue: 0, color: '#787878'},
    colors: ['red', 'blue'],
    curveType: 'function',
    legend: {
        position: 'bottom'
    }
};

// moisture level chart
let moistureLevel_data;
let moistureLevel_chart;

// prepare data
const moistureLevel_data_array = [['created', 'moistureLevel']];
for (entry of data) {
    moistureLevel_data_array.push([entry.created, entry.moistureLevel]);
}

// Set chart options
const moistureLevel_options = {
    backgroundColor: 'transparent',
    enableInteractivity: true,
    title: 'Soil Data',
    titleTextStyle: {
        color: '#787878'
    },
    hAxis: {
        title: 'created',
        titleTextStyle: {
            color: '#787878'
        },
        format: 'HH:mm',
        gridlines: {
            count: 10,
            color: '#787878'
        }
    },
    colors: ['blue'],
    curveType: 'function',
    legend: {
        position: 'bottom',
        color: '#787878'
    }
};


// Callback that creates and populates a data table,
// instantiates the pie chart, passes in the data and
// draws it.
function prepareAndDrawChart() {

    humid_temp_data = google.visualization.arrayToDataTable(humid_temp_data_array);
    // Instantiate and draw our chart, passing in some options.
    humid_temp_chart = new google.visualization.LineChart(document.getElementById('temp_humid_div'));


    moistureLevel_data = google.visualization.arrayToDataTable(moistureLevel_data_array);
    // Instantiate and draw our chart, passing in some options.
    moistureLevel_chart = new google.visualization.LineChart(document.getElementById('moisture_div'));

    drawChart();
}

/*
 * Draw the charts data and options into the chart objects to display them on the page (in the divs).
 */
function drawChart() {
    moistureLevel_chart.draw(moistureLevel_data, moistureLevel_options);
    humid_temp_chart.draw(humid_temp_data, humid_temp_options);
}