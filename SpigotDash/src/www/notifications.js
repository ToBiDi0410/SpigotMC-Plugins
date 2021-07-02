async function updateNotifications() {
    try {
        var data = await getJSONDataFromAPI("GET_NOTIFICATIONS");

        document.querySelector(".notifcations-container").innerHTML = "";
        for (key in data) {
            var notf = data[key];
            addNotificationIfNotPresent(key, notf);
        }
    } catch (err) {
        console.log(err);
    }
    setTimeout(updateNotifications, 10000);
}


async function addNotificationIfNotPresent(id, data) {
    var cont = document.querySelector(".notifcations-container");
    if (document.querySelector("#NOTIFICATION_" + id) == null) {
        classes = ["notification"];
        if (data.level == "WARNING") classes.push("is-warning")
        if (data.level == "ERROR") classes.push("is-danger")
        if (data.level == "INFO") classes.push("is-info")
        cont.innerHTML += '\
        <div class="' + classes.join(" ") + '">\
            <button class="delete"></button>\
            <div class="NOTIFICATION_TITLE">' + data.title + '</div>\
            <div class="NOTIFICATION_CONTENT">' + data.message + '</div>\
            <div class="NOTIFICATION_INITIATOR"><b>' + data.initiator + '</b> at ' + new Date(data.created).toLocaleString() + '</div>\
        </div>'
    }
}

updateNotifications();