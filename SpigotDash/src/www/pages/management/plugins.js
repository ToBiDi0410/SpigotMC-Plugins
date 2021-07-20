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

    if (JSONMatches(newPlugins, JSON.parse(plugins))) return;

    var container = document.querySelector(".pluginContainer")
    container.innerHTML = "";

    plugins = JSON.stringify(newPlugins);

    JSON.parse(plugins).forEach((elem) => {
        elem.enabled_icon = elem.enabled ? "done" : "highlight_off";
        elem.enabled_text = elem.enabled ? "loaded" : "disabled";
        elem.enabled_toggle_text = !elem.enabled ? "Enable" : "Disable";
        elem.enabled_toggle_onoff = !elem.enabled ? "on" : "off";
        if (elem.authors != null) {
            elem.authors = elem.authors.join(",").substring(0, 40);
        }
        var html = PLUGIN_BOX_TEMPLATE;
        container.innerHTML += replaceObjectKeyInString(elem, html);
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

var plugins = [11];