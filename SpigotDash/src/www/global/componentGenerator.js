var HTMLParser = new DOMParser();

function isValid(key, value) {
    return !(key == "" || key == undefined || key == null || value == "" || value == undefined || value == null);
}

function getDOMObject(html) {
    return HTMLParser.parseFromString(html, 'text/html').body.firstChild;
}

// NOTIFICATION
var TEMPLATE_NOTIFICATION = '\
<div class="notification is-%LEVEL%" data-uuid=%UUID%>\
    <div class="NOTIFICATION_TITLE"><b>%TITLE%</b></div> \
    <div class="NOTIFICATION_CONTENT">%MESSAGE%</div> \
    <div class="NOTIFICATION_INITIATOR">\
        <div><b>%INITIATOR%</b> at %LOCALECREATED%</div>\
    </div>\
</div>\
';

function generateNotificationHTML(id, data) {
    if (!isValid(id, data)) return "";

    data.localecreated = new Date(data.created).toLocaleTimeString();
    data.uuid = id;
    data.level = data.level.toLowerCase();

    var html = replaceObjectKeyInString(data, TEMPLATE_NOTIFICATION);

    return html;
}

// LOG
var TEMPLATE_LOGLIST_ENTRY = '<li class="console_messagelist_entry">%MESSAGE%</li>';

function generateLogListEntry(message) {
    var html = TEMPLATE_LOGLIST_ENTRY;
    html = html.replace("%MESSAGE%", ansiStringToHTMLString(message));
    return getDOMObject(html);
}

//PLUGINS
var TEMPLATE_PLUGIN_ENTRY = '\
<div class="">\
    <div class="plugin-title">%NAME%</div>\
    <div class="plugin-authors">from %AUTHORS%</div>\
    <div class="plugin-state %ENABLED%"><span class="material-icons-outlined">%ENABLED_ICON%</span> (%ENABLED_TEXT%)</div>\
    <div class="plugin-version">%VERSION%</div>\
    <div class="plugin-description">%DESCRIPTION%</div>\
    <a class="plugin-website" href="%WEBSITE%">%WEBSITE%</a>\
    <div class="plugin-toggle button is-warning" data-plugin="%NAME%" onclick="togglePlugin(this);"><span class="material-icons-outlined">toggle_%ENABLED_TOGGLE_ONOFF%</span> %ENABLED_TOGGLE_TEXT%</div>\
</div>\
';

function generatePluginEntry(value) {
    var html = TEMPLATE_PLUGIN_ENTRY;

    value.enabled_icon = value.enabled ? "done" : "highlight_off";
    value.enabled_text = value.enabled ? "loaded" : "disabled";
    value.enabled_toggle_text = !value.enabled ? "Enable" : "Disable";
    value.enabled_toggle_onoff = !value.enabled ? "on" : "off";
    if (value.authors != null) {
        value.authors = value.authors.join(",").substring(0, 40);
    }

    html = replaceObjectKeyInString(value, html);
    return getDOMObject(html);
}

//PLAYERS

var TEMPLATE_PLAYER_ENTRY = '\
<div class="player" data-uuid="%UUID%">\
    <img class="head" src="https://crafatar.com/avatars/%UUID%">\
    <div class="details">\
        <div>%DISPLAYNAME%</div>\
        <div>%UUID%</div>\
    </div>\
</div>\
';

function generatePlayerEntry(value) {
    var html = TEMPLATE_PLAYER_ENTRY;

    value.x = value.Location.X;
    value.y = value.Location.Y;
    value.z = value.Location.Z;
    value.world = value.Location.WORLD;
    value.Displayname = ansiStringToHTMLString(value.Displayname);

    html = replaceObjectKeyInString(value, html);
    return getDOMObject(html);
}