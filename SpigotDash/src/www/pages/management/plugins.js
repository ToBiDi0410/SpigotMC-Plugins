var PLUGIN_BOX_TEMPLATE = '\
<div class="box">\
    <div class="plugin-title">%NAME%</div>\
    <div class="plugin-authors">from %AUTHORS%</div>\
    <div class="plugin-state %ENABLED%"><span class="material-icons-outlined">%ENABLED_ICON%</span> (%ENABLED_TEXT%)</div>\
    <div class="plugin-version">%VERSION%</div>\
    <div class="plugin-description">%DESCRIPTION%</div>\
    <a class="plugin-website" href="%WEBSITE%">%WEBSITE%</a>\
    <div class="plugin-toggle button is-warning" data-plugin="%NAME%" onclick="togglePlugin(this);"><span class="material-icons-outlined">toggle_%ENABLED_TOGGLE_ONOFF%</span> %ENABLED_TOGGLE_TEXT%</div>\
</div>\
';

function initPage() {
    curr_task = updatePlugins;
}

async function updatePlugins() {
    var newPlugins = await getDataFromAPI({ method: "GET_PLUGINS" });

    if (JSONMatches(newPlugins, plugins)) return;

    var container = document.querySelector(".pluginContainer")
    container.innerHTML = "";

    plugins = newPlugins;
    pluginIDS = [];

    plugins.forEach((elem) => {
        elem = getIndependentObject(elem);
        elem.enabled_icon = elem.enabled ? "done" : "highlight_off";
        elem.enabled_text = elem.enabled ? "loaded" : "disabled";
        elem.enabled_toggle_text = !elem.enabled ? "Enable" : "Disable";
        elem.enabled_toggle_onoff = !elem.enabled ? "on" : "off";
        if (elem.authors != null) {
            elem.authors = elem.authors.join(",").substring(0, 40);
        }
        var html = PLUGIN_BOX_TEMPLATE;
        container.innerHTML += replaceObjectKeyInString(elem, html);

        pluginIDS.push(elem.file.replace(".SpigotDashDownload", "").replace(".jar", ""));
    });
}

async function togglePlugin(elem) {
    elem.setAttribute("disabled", true);
    elem.classList.add("is-loading");

    var pl = elem.getAttribute("data-plugin");

    var data = await getDataFromAPI({ method: "TOGGLE_PLUGIN", plugin: pl });

    if (data == "SUCCESS") {
        elem.removeAttribute("disabled");
        updatePlugins();
    } else {
        elem.classList.add("is-danger");
        elem.innerHTML = "Failed";
    }

    elem.classList.remove("is-loading");


}

var currentQuery = "";
var latestUpdateQuery = "NULL";
var pluginIDS = [94842];

async function togglePluginInstallDialogue() {
    latestUpdateQuery = "NULL";
    currentQuery = "";
    await updateOrOpenDialogue();

    while (Swal.isVisible() && Swal.getTitle().textContent == "Install Plugins") {
        updateOrOpenDialogue();
        await timer(1000);
    }
}

async function updateOrOpenDialogue() {
    if (currentQuery == latestUpdateQuery) {
        return;
    }

    latestUpdateQuery = currentQuery;

    if (currentQuery != "") {
        var data = await fetch("https://api.spiget.org/v2/search/resources/" + currentQuery + "");
        data = await data.json();

    } else {
        var data = await fetch("https://api.spiget.org/v2/resources?sort=-updateDate");
        data = await data.json();
    }

    var entrys_html = "";

    data.forEach((elem) => {

        var insertElem = {};
        insertElem["SUPPORTED_VERSIONS"] = elem.testedVersions.join(", ");
        insertElem["ID"] = elem.id;
        insertElem["NAME"] = elem.name;
        insertElem["LATEST_VERSION"] = elem.version.id;
        insertElem["TAG"] = elem.tag;
        insertElem["BTN_ATTRIBS"] = "";
        insertElem["BTN_TEXT"] = "+ Install";
        insertElem["WARNINGS"] = "";

        if (elem.file.type != ".jar") {
            insertElem["BTN_TEXT"] = '<a class="has-text-danger">Unsupported Format</a>';
            insertElem["BTN_ATTRIBS"] = "disabled";
        }

        if (pluginIDS.includes("" + elem.id)) {
            insertElem["BTN_ATTRIBS"] = "disabled";
            insertElem["BTN_TEXT"] = '<span class="material-icons-outlined pr-1">done</span>Installed';
        }

        var html = replaceObjectKeyInString(insertElem, PLUGINS_INSTALL_DIALOGUE_ENTRY);
        entrys_html += html;
    });

    var html = PLUGINS_INSTALL_DIALOGUE.replace("%ENTRYS%", entrys_html).replace("%CURRENT_SEARCH%", currentQuery);

    if (Swal.isVisible() && Swal.getTitle().textContent == "Install Plugins") {
        Swal.getHtmlContainer().innerHTML = html;
    } else {
        Swal.fire({
            title: "Install Plugins",
            html: html,
            width: "90vw"
        });
    }
}

async function installPluginButtonClick(elem) {
    if (elem.hasAttribute("disabled")) return;

    var id = elem.getAttribute("data-id");

    var data = await getDataFromAPI({ method: "INSTALL_PLUGIN", id: id });
    console.log(data);

    elem.classList.add("is-loading");
    elem.setAttribute("disabled", true);

    await timer(1000);

    elem.classList.remove("is-loading");
    elem.innerHTML = '<span class="material-icons-outlined pr-1">done</span>Installed';
}

var PLUGINS_INSTALL_DIALOGUE = '<div class="w-100">\
<div class="field has-addons w-100" style="justify-content: center;">\
    <div class="control">\
        <input class="input is-primary" type="text" placeholder="Name" value="%CURRENT_SEARCH%">\
    </div>\
    <div class="control">\
        <a class="button is-success" onclick="currentQuery = this.parentElement.parentElement.querySelector(\'input\').value; updateOrOpenDialogue();">?</a>\
    </div>\
</div>\
</div>\
\
<div class="pluginInstallList">\
%ENTRYS%\
</div>'

var PLUGINS_INSTALL_DIALOGUE_ENTRY = '\
<div class="card m-2" style="overflow: hidden;">\
    <div class="card-content">\
        <div class="media">\
            <div class="media-left">\
                <figure class="image is-48x48">\
                    <img src="https://api.spiget.org/v2/resources/%ID%/icon/data" alt="Placeholder image">\
                </figure>\
            </div>\
            <div class="media-content">\
                <p class="title is-4">%NAME%</p>\
                <p class="subtitle is-7">%LATEST_VERSION%</p>\
            </div>\
        </div>\
\
        <div class="content">\
            %TAG%\
            <br>%WARNINGS%\
        </div>\
    </div>\
    <footer class="card-footer p-2">\
        <div class="level" style="width: 100%;">\
            <div class="level-left">\
                <div class="has-text-danger">Supports:</div>\
                <div class="has-text-secondary pl-1 pr-1">%SUPPORTED_VERSIONS%</div>\
            </div>\
\
            <div class="level-right">\
                <div class="button is-success align-left" data-id="%ID%" %BTN_ATTRIBS% onclick="installPluginButtonClick(this);">%BTN_TEXT%</div>\
            </div>\
    </div>\
</footer>\
</div>';

var plugins = [11];