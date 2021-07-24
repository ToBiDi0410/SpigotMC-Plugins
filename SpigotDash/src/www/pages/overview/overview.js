async function initPage() {
    curr_task = dataRefreshTask;
}

async function dataRefreshTask() {
    try {
        var curr = await getDataFromAPI({ method: "GET_OVERVIEW" });
        insertObjectIntoHTML(curr, contentContainer);

        var cont = document.querySelector(".notificationContainer");
        console.log()
        if (curr.notifications.length < 1) {
            cont.innerHTML = '<a class="has-text-danger">No Notifications</a>';
        } else {
            cont.innerHTML = "";
        }

        for (const [key, value] of Object.entries(curr.notifications)) {
            addNotificationIfNotPresent(key, value);
        }
    } catch (err) {}

}

async function addNotificationIfNotPresent(id, data) {
    var cont = document.querySelector(".notificationContainer");
    if (document.querySelector(".notification[data-uuid='" + data.uuid + "']") == null) {
        classes = ["notification"];
        if (data.level == "WARNING") classes.push("is-warning")
        if (data.level == "ERROR") classes.push("is-danger")
        if (data.level == "INFO") classes.push("is-info")
        cont.innerHTML += '\
        <div class="' + classes.join(" ") + '" data-uuid="' + data.uuid + '">\
            <button class="delete" onclick="closeNotification(this);"></button>\
            <div class="NOTIFICATION_TITLE">' + data.title + '</div>\
            <div class="NOTIFICATION_CONTENT">' + data.message + '</div>\
            <div class="NOTIFICATION_INITIATOR"><b>' + data.initiator + '</b> at ' + new Date(data.created).toLocaleString() + '</div>\
        </div>'
    }
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