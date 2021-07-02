var API_URL = "./api";

function transformDate(date) {
    return new Intl.DateTimeFormat('de-DE', { hour: '2-digit', minute: '2-digit' }).format(date);
    return date.getHours() + ":" + date.getMinutes();
}

function refreshSections(id) {
    var secs = document.querySelectorAll(".section");
    secs.forEach((elem) => {
        if (elem.id != ("sec_" + id)) {
            elem.classList.add("section_disabled")
            elem.classList.remove("section_enabled")
            return;
        }
        elem.classList.remove("section_disabled")
        elem.classList.add("section_enabled")

    })
}

function setOffline(off) {
    if (off == true) {
        console.log("Recieved an hint, that the server is offline...");
        document.getElementById("offlinewarn").classList.add("shown");
    } else {
        document.getElementById("offlinewarn").classList.remove("shown");
    }
}

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
            window.location.href = "./login.html";
        }

        setOffline(false);
        return data;
    } catch (err) {
        setOffline(true);
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

document.addEventListener('DOMContentLoaded', () => {

    // Get all "navbar-burger" elements
    const $navbarBurgers = Array.prototype.slice.call(document.querySelectorAll('.navbar-burger'), 0);

    // Check if there are any navbar burgers
    if ($navbarBurgers.length > 0) {

        // Add a click event on each of them
        $navbarBurgers.forEach(el => {
            el.addEventListener('click', () => {

                // Get the target from the "data-target" attribute
                const target = el.dataset.target;
                const $target = document.getElementById(target);

                // Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
                el.classList.toggle('is-active');
                $target.classList.toggle('is-active');

            });
        });
    }

});