$(document).ready(() => {
    // add a click event listener to the <a> element
    $("#makeRequest").click(() => {
        const start = $("#start").val();
        const end = $("#end").val();
        const url = `/?begin=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`;
        $(this).attr("href", url);
    });

    // https://stackoverflow.com/a/42353290
    let searchParams = new URLSearchParams(window.location.search)

    // https://stackoverflow.com/a/60884408
    let now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());

    /* remove second/millisecond if needed - credit ref. https://stackoverflow.com/questions/24468518/html5-input-datetime-local-default-value-of-today-and-current-time#comment112871765_60884408 */
    now.setMilliseconds(null)
    now.setSeconds(null)

    let date_string = now.toISOString().slice(0, -1);
    let start_date = new Date(now);
    start_date.setHours(start_date.getHours() - 8);
    let start_date_string = start_date.toISOString().slice(0, -1);

    let start = $("#start");
    start.attr("max", date_string);
    start.val(searchParams.has('begin') ? searchParams.get('begin') : start_date_string);

    let end = $("#end");
    end.attr("max", date_string);
    end.val(searchParams.has('end') ? searchParams.get('end') : date_string);
});