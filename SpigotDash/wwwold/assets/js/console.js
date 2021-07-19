var last_line = "";

async function updateLog() {
    try {
        var data = await getJSONDataFromAPI("GET_LOG");
        var new_last_line = data[data.length - 1];

        if (new_last_line != last_line) {
            var messagelist = document.querySelector(".console_messagelist");
            messagelist.innerHTML = "";
            data.forEach((elem) => {
                messagelist.innerHTML += "<li>" + ansiStringToHTMLString(elem) + "</li>";
            });

            messagelist.parentElement.scrollTop = messagelist.parentElement.scrollHeight;
            last_line = new_last_line;
        }
    } catch (err) {
        console.log(err);
    }

}

function updateLogTask() {
    updateLog();
    setTimeout(updateLogTask, 10000);
}

async function execConsoleCommand() {
    var input_cont = document.querySelector(".console_input_text");
    var btn = document.querySelector(".console_input_button");
    var input = input_cont.getElementsByTagName("input")[0];
    var command = input.value;

    input_cont.classList.add("is-loading");
    input.setAttribute("disabled", "");
    btn.setAttribute("disabled", "");

    var data = await fetch(API_URL, {
        "headers": {
            "accept": "*/*",
            "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7,nl;q=0.6",
            "cache-control": "no-cache",
            "content-type": "application/json;charset=UTF-8",
        },
        "body": '{\n    "method": "EXEC_COMMAND", "command": "' + command + '"\n}',
        "method": "POST",
        "Cache-Control": "no-cache"

    });

    input_cont.classList.remove("is-loading");
    input.removeAttribute("disabled", "");
    btn.removeAttribute("disabled", "");
    input.value = "";

    updateLog();
}

$('.console_input_text').on('keydown', function(evt) {
    if (evt.key === 'Tab' || evt.key === 'Enter') {
        if (evt.key === 'Tab') {

        }
        if (evt.key === 'Enter') {
            execConsoleCommand();
        }
        evt.preventDefault();
        $('input[name=btn]').trigger('click');
    }
});

updateLogTask();