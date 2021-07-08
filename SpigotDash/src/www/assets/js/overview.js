async function updateNotifications() {
    try {
        var data = await getJSONDataFromAPI("GET_NOTIFICATIONS");

        document.querySelector(".notifcations-container").innerHTML = "";
        for (key in data) {
            var notf = data[key];
            addNotificationIfNotPresent(key, notf);
        }

        if (data.length < 1) {
            document.querySelector(".notifcations-container").innerHTML = "No Notifications available";
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
        <div class="' + classes.join(" ") + '" data-uuid="' + data.uuid + '">\
            <button class="delete" onclick="closeNotification(this);"></button>\
            <div class="NOTIFICATION_TITLE">' + data.title + '</div>\
            <div class="NOTIFICATION_CONTENT">' + data.message + '</div>\
            <div class="NOTIFICATION_INITIATOR"><b>' + data.initiator + '</b> at ' + new Date(data.created).toLocaleString() + '</div>\
        </div>'
    }
}

async function closeNotification(btn_elem) {
    var notification = btn_elem.parentElement;
    var uuid = notification.getAttribute("data-uuid");

    var data = await fetch(API_URL, {
        "headers": {
            "accept": "*/*",
            "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7,nl;q=0.6",
            "cache-control": "no-cache",
            "content-type": "application/json;charset=UTF-8",
        },
        "body": '{\n    "method": "REMOVED_NOTIFICATION", "uuid": "' + uuid + '"\n}',
        "method": "POST",
        "Cache-Control": "no-cache"

    });

    notification.remove();
}

updateNotifications();