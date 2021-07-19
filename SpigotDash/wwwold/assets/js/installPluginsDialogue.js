var NEWST_PLUGINS = null;

var PLUGIN_INSTALLER_LI = '\
<li class="pluginli">\
    <div class="box">\
        <div class="pluginli_icon">\
            <img src="%IMG%">\
        </div>\
        <div class="pluginli_desc">\
            <a class="plugintitle" href="https://spigotmc.org">%NAME%</a>\
            <div class="pluginauth">by %AUTHOR%</div>\
            <div class="plugindesc">%TAG%</div>\
            <div data-id="%ID%" class="button is-success" onclick="installPlugin(this);">+ Install</div>\
        </div>\
    </div>\
</li>'

async function init() {
    sendDateIntoDialogue(await getPluginsJSON(""));
}

async function installPlugin(button) {
    button.classList.add("is-loading");
    var id = button.getAttribute("data-id");

    var data = await fetch(API_URL, {
        "headers": {
            "accept": "*/*",
            "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7,nl;q=0.6",
            "cache-control": "no-cache",
            "content-type": "application/json;charset=UTF-8",
        },
        "body": '{\n    "method": "INSTALL_PLUGIN", "id": "' + id + '"\n}',
        "method": "POST",
        "Cache-Control": "no-cache"

    });

    if (data.status != 200) {
        button.classList.remove("is-loading");
        button.classList.add("is-danger");
        button.innerHTML = '<span class="material-icons-outlined">error_outline</span>';
    } else {
        button.classList.remove("is-loading");
        button.innerHTML = '<span class="material-icons-outlined">done</span>';
    }

    button.setAttribute("disabled", null);

}

async function sendDateIntoDialogue(json) {
    try {
        console.log(json);
        var curr_plugins = await getJSONDataFromAPI("GET_PLUGIN_FILES");
        var list = document.querySelector(".plugininstall_list");
        var prog_html = '<progress class="progress is-danger" max="100">30%</progress>';
        list.innerHTML = prog_html;

        var i = 0;
        while (i < json.length) {
            var elem = json[i];
            var html = PLUGIN_INSTALLER_LI;

            html = html.replaceAll("%ID%", elem.id);
            html = html.replaceAll("%NAME%", elem.name);
            html = html.replaceAll("%TAG%", elem.tag);

            var author = await getAuthorByID(elem.author.id);
            if (author != null) {
                html = html.replaceAll("%AUTHOR%", author.name);
            } else {
                html = html.replaceAll("%AUTHOR%", "UNKNOWN");
            }

            if (elem.icon.url != "") {
                html = html.replaceAll("%IMG%", "data:image/png;base64," + elem.icon.data);
            } else {
                html = html.replaceAll("%IMG%", "../img/PLUGININSTALLER_NOICON.png");
            }

            if (includesSimilar(curr_plugins, elem.name.split(" ")[0] + ".jar")) {
                html = html.replace('class="button is-success" onclick="installPlugin(this);">+ Install</div>', 'class="button is-warning" disabled onclick="installPlugin(this);">Installed</div>');;
            }

            list.innerHTML += html;
            i++;
        }

        list.innerHTML = list.innerHTML.replace(prog_html, "");
    } catch (err) {
        console.log(err);
        setTimeout(init, 1000);
    }
}

async function updateRefresher() {
    try {
        NEWST_PLUGINS = await (await fetch("https://api.spiget.org/v2/resources?sort=-updateDate")).json();
    } catch (err) {}
    setTimeout(updateRefresher, 1000 * 60);
}

async function getAuthorByID(id) {
    try {
        return (await (await fetchWithTimeout("https://api.spiget.org/v2/authors/" + id, { timeout: 5000 })).json());
    } catch (err) {
        return null;
    }
}

async function getPluginsJSON(query) {
    try {
        if (query == "" || query == null) {
            while (NEWST_PLUGINS == null) {
                await timer(1000);
            }
            return NEWST_PLUGINS;
        } else {
            query = query.replaceAll(" ", "");
            return (await (await fetch("https://api.spiget.org/v2/search/resources/" + query + "?sort=-downloads")).json());
        }
    } catch (err) {
        return null;
    }
}

var last_input = 0;
var task_waiting = false;
async function newInput(elem) {
    last_input = Date.now();

    if (task_waiting == false) {
        task_waiting = true;
        while ((last_input + 2000) > Date.now()) {
            await timer(500);
        }

        elem.setAttribute("disabled", null);
        elem.classList.add("is-loading");

        await sendDateIntoDialogue(await getPluginsJSON(elem.value));

        elem.removeAttribute("disabled");
        elem.classList.remove("is-loading");

        task_waiting = false;
    }

}

updateRefresher();

document.addEventListener("DOMContentLoaded", init);