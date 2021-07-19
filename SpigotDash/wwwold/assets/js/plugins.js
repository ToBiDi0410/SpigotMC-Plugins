var CARD_TEMPLATE = '\
<div class="card is-fullwidth">\
    <header class="card-header">\
    <p class="card-header-title">\
        <b>%NAME%</b>\
        <k>%VERSION%</k>\
    </p>\
    <a class="card-header-icon card-toggle">\
        <span class="material-icons-outlined">\
            expand_more\
        </span>\
    </a>\
    </header>\
    <div class="card-content is-hidden">\
        <div class="content">\
            <img class="image" alt="Logo" src="ERR" style="display: none"></img>\
            <a><b>Name:</b> %NAME%</a>\
            <br>\
            <a><b>Enabled:</b> %ENABLED%</a>\
            <br>\
            <a><b>Version:</b> %VERSION% (API: %API_VERSION%)</a>\
            <br>\
            <a><b>Authors:</b> %AUTHORS%</a>\
            <br>\
            <a><b>Website:</b> <a href="%WEBSITE%">%WEBSITE%</a></a>\
            <br>\
            <a><b>Integration:</b> %INTEGRATED%</a>\
            <br><br>\
            %TOGGLE_STATE%\
        </div>\
    </div>\
</div>';

//            <button class="button is-danger" data-id="%NAME%" onclick="uninstallPlugin(this);"><span class="material-icons-outlined icon-centered">delete</span>Delete</button>\


var OLD_DATA = null;

document.addEventListener('DOMContentLoaded', addEventListenersToToggles);

function addEventListenersToToggles() {
    let cardToggles = document.getElementsByClassName('card-toggle');
    for (let i = 0; i < cardToggles.length; i++) {
        cardToggles[i].onclick = function(e) {
            e.currentTarget.parentElement.parentElement.childNodes[3].classList.toggle('is-hidden');
        };
    }
}

async function reloadPlugins() {
    var data = await getJSONDataFromAPI("GET_PLUGINS");

    if (JSON.stringify(OLD_DATA) != JSON.stringify(data)) {
        var container = document.querySelector(".container");
        container.innerHTML = "";
        data.forEach((elem) => {
            var html = CARD_TEMPLATE;

            html = html.replaceAll("%NAME%", elem.name);
            html = html.replaceAll("%VERSION%", elem.version);
            html = html.replaceAll("%AUTHORS%", elem.authors);
            html = html.replaceAll("%WEBSITE%", elem.website);
            html = html.replaceAll("%API_VERSION%", elem.apiversion);
            html = html.replaceAll("%ENABLED%", elem.enabled ? '<a class="has-text-success">Loaded</a>' : '<a class="has-text-danger">Disabled</a>');
            html = html.replaceAll("%INTEGRATED%", elem.known ? '<a class="has-text-success">Loaded</a>' : '<a class="has-text-danger">Not available</a>');
            if (elem.known) {
                html = html.replace('<img alt="Logo" class="image" src="ERR" style="display: none"></img>', '<img class="image" src="' + elem.img + '"></img>');
            }

            if (elem.enabled) {
                html = html.replace("%TOGGLE_STATE%", '<button data-toggle="' + elem.name + '" onclick="togglePluginState(\'' + elem.name + '\');" class="button is-danger">Disable</button>');
            } else {
                html = html.replace("%TOGGLE_STATE%", '<button data-toggle="' + elem.name + '" onclick="togglePluginState(\'' + elem.name + '\');" class="button is-success">Enable</button>');
            }

            html = html.replaceAll("undefined", "<k>Missing</k>");

            if (elem.name != "SpigotDash" || true) {
                container.innerHTML += html;
            }
        });

        addEventListenersToToggles();
        OLD_DATA = data;

    }
}

async function uninstallPlugin(elem) {
    var id = elem.getAttribute("data-id");
    var res = await swal.fire({
        title: 'Are you sure?',
        html: "Do you want to delete this Plugin and all of it´s data?<br>This cannot be undone!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes',
        closeOnClickOutside: false
    });

    if (res.isConfirmed) {
        swal.fire({
            title: 'Deleting...',
            text: 'Please wait a second...',
            closeOnClickOutside: false
        });
        swal.showLoading();

        var err = true;
        var err_details = "ERR: Unknown";

        try {
            var data = await fetch(API_URL, {
                "headers": {
                    "accept": "*/*",
                    "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7,nl;q=0.6",
                    "cache-control": "no-cache",
                    "content-type": "application/json;charset=UTF-8",
                },
                "body": '{\n    "method": "DELETE_PLUGIN", "plugin": "' + id + '"\n}',
                "method": "POST",
                "Cache-Control": "no-cache"

            });

            if (data.status != 200) {
                err_details = data.statusText;
            } else {
                err = false;
            }
        } catch (err) {
            err_details = err;
        }

        if (!err) {
            swal.fire({
                title: "Deleted",
                text: "The Plugin and all of it´s files has been deleted!",
                icon: "success"
            });
        } else {
            swal.fire({
                title: "Failed",
                text: "The Process could not be completed! Some files might be deleted and some not! Please check this manually.",
                icon: "error"
            });

            swal.showValidationMessage(err_details);
        }


        swal.close
    }
}

function reloadPluginsTask() {
    try {
        reloadPlugins();
    } catch (err) {
        console.log(err);
    }

    setTimeout(reloadPluginsTask, 10000);
}


async function togglePluginState(name) {
    var elem = document.querySelector("button[data-toggle='" + name + "']");
    elem.classList.add("is-loading");
    var data = await fetch(API_URL, {
        "headers": {
            "accept": "*/*",
            "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7,nl;q=0.6",
            "cache-control": "no-cache",
            "content-type": "application/json;charset=UTF-8",
        },
        "body": '{\n    "method": "TOGGLE_PLUGIN", "plugin": "' + name + '"\n}',
        "method": "POST",
        "Cache-Control": "no-cache"

    });

    elem.classList.remove("is-loading");

    reloadPlugins();
}

async function installPluginsDialogue(elem) {
    elem.classList.add("is-loading");

    var dialogue = document.querySelector(".PLUGIN_INSTALLER_DIALOGUE");
    dialogue.classList.remove("hidden");

    elem.classList.remove("is-loading");
}

reloadPluginsTask();