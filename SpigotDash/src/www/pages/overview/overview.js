var dataMan;

async function initPage() {
    curr_task = updaterTask;
}

async function updaterTask() {
    var data = await getDataFromAPI({ method: "GET_OVERVIEW" });
    data.notificationHTML = "";

    for (const [key, value] of Object.entries(data.notifications)) {
        data.notificationHTML += generateNotificationHTML(key, value);
    }

    insertObjectIntoHTML(data, document.body);
}

async function closeNotification(elem) {
    var id = elem.parentElement.getAttribute("data-uuid");
    var res = await getDataFromAPI({ method: "NOTIFICATION_CLOSED", uuid: id });

    if (res == "REMOVED") {
        elem.parentElement.remove();
    } else {
        elem.parentElement.innerHTML += "<div>Failed to close this Notification! Sorry :(</div>"
    }
}