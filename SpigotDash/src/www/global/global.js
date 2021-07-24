var curr_task = null;

function replaceObjectKeyInString(object, string) {
    return replaceObjectKeyInStringWithChar(object, string, "%");
}

function replaceObjectKeyInStringWithChar(object, string, char) {
    try {
        object.x = object.Location.X;
        object.y = object.Location.Y;
        object.z = object.Location.Z;
        object.world = object.Location.WORLD;
    } catch (err) {}


    for (var [key, value] of Object.entries(object)) {
        if (value == null || value == undefined) {
            value = "";
        }
        string = string.replaceAll(char + key.toUpperCase() + char, value.toString());
    }

    return string;
}

function insertObjectIntoHTML(object, tree) {
    for (var [key, value] of Object.entries(object)) {
        tree.querySelectorAll("*[data-apiobj='" + key.toUpperCase() + "'").forEach((elem) => {
            elem.innerHTML = value;
        })
    }
}

function JSONMatches(objectone, objecttwo) {
    var objoneJSON = JSON.stringify(objectone);
    var objtwoJSON = JSON.stringify(objecttwo);
    return (objoneJSON == objtwoJSON);
}

async function getDataFromAPI(body) {
    try {
        var data = await fetch(API_URL, {
            method: "POST",
            mode: "cors",
            cache: "no-cache",
            redirect: "follow",
            body: JSON.stringify(body)
        });
        showOffline(false);

        if (data.status == 401) {
            loginRequired();
            return null;
        }

        data = await data.json();
        return data;
    } catch (err) {
        showOffline(true);
        return null;
    }
}

function getIndependentObject(obj) {
    return JSON.parse(JSON.stringify(obj));
}

function showOffline(state) {
    if (state == false) {
        if (Swal.isVisible() && Swal.getTitle().textContent == "Reconnecting...") Swal.close();
        return;
    }

    if (state == true) {
        if (Swal.isVisible() && Swal.getTitle().textContent == "Reconnecting...") return;
        Swal.fire({
            title: "Reconnecting...",
            text: "Oh no, the Server seems to be offline! We will try reconnecting for you...",
            showConfirmButton: false,
            allowOutsideClick: false,
            allowEscapeKey: false
        });
        Swal.showLoading();
        return;
    }
}

function loginRequired() {
    Swal.fire({
        title: "Login required",
        html: "You need to be logged in to use this Page!<br>Redirecting you to the Login page...",
        showCancelButton: false,
        showConfirmButton: false,
        allowOutsideClick: false,
        allowEscapeKey: false
    });

    Swal.showLoading();

    setTimeout(function() {
        window.location.href = "./login.html";
    }, 1000);
}

function loadJSIfExists(url, container) {
    try {
        var s = document.createElement("script");
        s.type = "text/javascript";
        s.src = url;
        s.onload = function() {
            try { initPage(); } catch (err) {
                console.warn("[LOADER] The Page loaded doesn´t provide a valid Javascript file!");
            }
        }
        var appendedjs = container.append(s);
        return true;
    } catch (err) {
        return false;
    }
}

async function loadCSSIfExists(url, container) {
    try {
        var csst = await fetch(url);
        csst = await csst.text();
        container.innerHTML += '<style>' + csst + '</style>';
        return true;
    } catch (err) {
        return false;
    }
}

String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};

var theme = (document.head.innerHTML.includes("bulmaswatch") ? "dark" : "light");
var API_URL = "./api";

const timer = function(time) {
    return new Promise((resolve, reject) => {
        setTimeout(resolve, time);
    });
}