var players = [{ Location: { x: 0, y: 0, z: 0, YAW: 0, PITCH: 0, WORLD: "TEST" } }];

async function initPage() {
    curr_task = updatePlayerList;
}

async function updatePlayerList() {
    var temp_players = await getDataFromAPI({ method: "GET_PLAYERS" });
    temp_players.forEach(generatePlayerEntry);
    if (JSONMatches(temp_players, players)) return;
    players = temp_players;


    var cont = document.querySelector(".players");

    if (players.length <= 0) {
        cont.innerHTML = '<a class="has-text-danger">There are currently no Players on this Server!';
        return;
    }

    if (cont.innerHTML.includes('There are currently no Players on this Server!')) {
        cont.innerHTML = "";
    }

    var DOMsToRemove = Array.from(cont.querySelectorAll(".player"));

    players.forEach((elem) => {
        var currDom = cont.querySelector(".player[data-uuid='" + elem.UUID + "']");

        if (currDom == null) {
            //IF PLAYER NOT IN LSIT
            var child = cont.appendChild(generatePlayerEntry(elem));
            child.addEventListener("click", function() {
                showInfos(this.getAttribute("data-uuid"));
            });
        } else {
            //IF PLAYER IN LIST, REMOVE OUT OF DELETION BECAUSE THE PLAYER IS ONLINE (PLAYERS CONTAINS HIM)
            DOMsToRemove = DOMsToRemove.filter(function(el) { return el != currDom });
        }
    });

    DOMsToRemove.forEach((elem) => { elem.remove() });
    return;
}

async function showInfos(uuid) {
    var player = players.find(function(elem) { return (elem.UUID == uuid); });
    var new_html = replaceObjectKeyInString(player, TEMPLATE_PLAYER_MENU);

    var menu = new smartMenu("PLAYERINFO", player.Name, player.Displayname);
    menu.open();
    menu.setHTML(new_html);
    menu.update();

    while (!menu.closed) {
        var thisPlayer = players.find(function(elem) { return (elem.UUID == player.UUID); });
        if (thisPlayer == null || thisPlayer == undefined) {
            menu.setHTML(menu.html + '<br><a class="has-text-danger"><b>The Player you are viewing left the Server, no more updates!</b></a>');
            break;
        }

        if (!JSONMatches(thisPlayer, player)) {
            menu.setHTML(replaceObjectKeyInString(thisPlayer, TEMPLATE_PLAYER_MENU));
            player = thisPlayer;
        }

        await timer(1000);
    }
};

async function sendMessageClick(uuid) {
    var res = await Swal.fire({
        title: "Send Ingame Message",
        text: "Color Codes with '&' are Supported (e.g. '&6')!",
        input: "text",
        confirmButtonText: "Send Message",
        showCancelButton: true,
        backdrop: true,
        allowOutsideClick: () => !Swal.isLoading(),
        preConfirm: async function(message) {
            var reqres = await getDataFromAPI({ method: "PLAYER_ACTION", action: "MESSAGE", message: message, player: uuid })
            if (reqres == "SUCCESS") {
                return true;
            } else {
                Swal.showValidationMessage("Failed: " + reqres);
            }
        }
    });

    if (res.isConfirmed) {
        Swal.fire({
            title: "Sent",
            text: "The Message was sent successfully!",
            icon: "success",
            timer: 2000,
            timerProgressBar: true
        });
    }
}

async function kickClick(uuid) {
    var res = await Swal.fire({
        title: "Kick Player",
        text: "Color Codes with '&' are Supported (e.g. '&6')!",
        input: "text",
        confirmButtonText: "Kick Player",
        showCancelButton: true,
        backdrop: true,
        allowOutsideClick: () => !Swal.isLoading(),
        preConfirm: async function(message) {
            var reqres = await getDataFromAPI({ method: "PLAYER_ACTION", action: "KICK", message: message, player: uuid })
            if (reqres == "SUCCESS") {
                return true;
            } else {
                Swal.showValidationMessage("Failed: " + reqres);
            }
        }
    });

    if (res.isConfirmed) {
        Swal.fire({
            title: "Kicked",
            text: "The Player has been kicked!",
            icon: "success",
            timer: 2000,
            timerProgressBar: true
        });
    }
}

var TEMPLATE_PLAYER_MENU = '\
<a><b>Displayname: </b>%DISPLAYNAME%<br>\
<a><b>UUID: </b>%UUID%<br><br>\
<a><b>Position: </b>X: %X%, Y: %Y%, Z: %Z% in %WORLD%<br>\
<a><b>Health: </b>%HEALTH%/%HEALTH_MAX%<br>\
<a><b>Food: </b>%FOOD%/20<br>\
<div class="button is-info m-1" onclick="sendMessageClick(this.getAttribute(\'data-uuid\'))" data-uuid="%UUID%">Message</div>\
<div class="button is-danger m-1" onclick="kickClick(this.getAttribute(\'data-uuid\'))" data-uuid="%UUID%">Kick</div>\
';