async function initPage() {
    curr_task = updateLog;
}

async function executeCommand(elem) {
    var input = elem.parentElement.firstElementChild;
    var command = input.value;
    var btn = elem;

    input.setAttribute("disabled", "");
    btn.setAttribute("disabled", "");
    btn.classList.add("is-loading");

    var data = await getDataFromAPI({ method: "EXEC_COMMAND", command: command });

    input.removeAttribute("disabled");
    btn.removeAttribute("disabled");
    btn.classList.remove("is-loading");

    updateLog();
}

async function updateLog() {
    var data = await getDataFromAPI({ method: "GET_LOG" });
    var new_last_line = data[data.length - 1];

    if (new_last_line != last_line) {
        var messagelist = document.querySelector(".console_messagelist");
        messagelist.innerHTML = "";
        data.forEach((elem) => {
            messagelist.innerHTML += "<li>" + ansiStringToHTMLString(elem) + "</li>";
        });

        messagelist.scrollTop = messagelist.scrollHeight;
        last_line = new_last_line;
    }
}

var last_line = "";