async function initPage() {
    curr_task = dataRefreshTask;
}

async function dataRefreshTask() {
    try {
        var curr = await getDataFromAPI({ method: "GET_OVERVIEW" });
        insertObjectIntoHTML(curr, contentContainer);

        var cont = document.querySelector(".notificationContainer");
        cont.innerHTML = "";

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