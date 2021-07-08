async function getJSONDataFromAPI(method) {
    try {
        var data = await fetch(API_URL, {
            "headers": {
                "accept": "*/*",
                "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7,nl;q=0.6",
                "cache-control": "no-cache",
                "content-type": "application/json;charset=UTF-8",
            },
            "body": "{\n    \"method\": \"" + method + "\"\n}",
            "method": "POST",
            "Cache-Control": "no-cache",

        });

        var code = data.status;
        data = await data.json();

        if (code == 401 && data == "ERR_REQUIRE_AUTH") {
            parent.window.location.href = "../../login.html";
        }

        try {
            parent.setOffline(false);
        } catch (err) {}
        return data;
    } catch (err) {
        try {
            parent.setOffline(true);
        } catch (err) {}
        throw err;
    }
}

function msToTime(s) {
    var ms = s % 1000;
    s = (s - ms) / 1000;
    var secs = s % 60;
    s = (s - secs) / 60;
    var mins = s % 60;
    var hrs = (s - mins) / 60;

    return hrs + 'h ' + mins + 'm ' + secs + 's'
}

String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};