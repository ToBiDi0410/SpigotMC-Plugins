var PLAYER_ROW_TEMPLATE = '<div class="playerrow row box">\
<img src="https://crafatar.com/renders/body/%BODY_UUID%?scale=2" class="playerrow_head">\
<div class="playerrow_infos">\
    <div class="playerrow_name title">%DISPLAYNAME%</div>\
    <div class="playerrow_uuid">%UUID%</div>\
    <div class="playerrow_location">X: %LOCX% | Y: %LOCY% | Z: %LOCZ% in %WORLD%</div>\
    <div class="playerrow_online">Ontime: %ONTIME%</div>\
</div>\
</div>';

var current_players = {};

async function updatePlayers() {
    try {
        var data = await getJSONDataFromAPI("GET_PLAYERS");
        if (!isEquivalent(current_players, data) && data.length != 0) {
            document.querySelector(".playerrow_container").innerHTML = "";
            data.forEach((elem) => {
                html = PLAYER_ROW_TEMPLATE;
                html = html.replace("%BODY_UUID%", elem.UUID);
                html = html.replace("%UUID%", elem.UUID);
                html = html.replace("%DISPLAYNAME%", minecraftStringToHTMLString(elem.Displayname));
                html = html.replace("%LOCX%", elem.Location.X);
                html = html.replace("%LOCY%", elem.Location.Y);
                html = html.replace("%LOCZ%", elem.Location.Z);
                html = html.replace("%WORLD%", elem.Location.WORLD.toLocaleUpperCase());
                html = html.replace("%ONTIME%", msToTime(Date.now() - elem.JOINTIME));
                document.querySelector(".playerrow_container").innerHTML += html;
            });

            current_players = data;
        }
        if (data.length == 0) {
            document.querySelector(".playerrow_container").innerHTML = "No Players online";
        }
    } catch (err) {
        console.log(err);
    }
    setTimeout(updatePlayers, 10000);
}

function isEquivalent(a, b) {
    //FROM: http://adripofjavascript.com/blog/drips/object-equality-in-javascript.html#:~:text=Here%20is%20a%20very%20basic,are%20not%20equivalent%20if%20(aProps.
    // Create arrays of property names
    var aProps = Object.getOwnPropertyNames(a);
    var bProps = Object.getOwnPropertyNames(b);

    if (aProps.length != bProps.length) {
        return false;
    }

    for (var i = 0; i < aProps.length; i++) {
        var propName = aProps[i];
        if (a[propName] !== b[propName]) {
            return false;
        }
    }
    return true;
}

updatePlayers();