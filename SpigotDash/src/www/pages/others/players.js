var players = [11];

async function initPage() {
    registerClickListeners();
    curr_task = updatePlayerList;
}

function registerClickListeners() {
    document.querySelectorAll(".player>.head").forEach((elem) => {
        elem.setAttribute("onclick", 'showInfos(this.getAttribute("data-array-index"));');
    });
}

async function updatePlayerList() {
    var temp_players = await getDataFromAPI({ method: "GET_PLAYERS" });
    if (JSONMatches(temp_players, players)) return;

    players = temp_players;

    var cont = document.querySelector(".players");
    cont.innerHTML = "";

    if (players.length <= 0) {
        cont.innerHTML = '<a class="has-text-danger">There are currently no Players on this Server!';
        return;
    }

    players.forEach((elem) => {
        cont.innerHTML += replaceObjectKeyInString(elem, PLAYER_BOX_HTML);
    });

    registerClickListeners();

    return;
}

async function showInfos(arrayIndex) {
    var player = players[arrayIndex];

    var new_html = replaceObjectKeyInString(player, PLAYER_DIALOGUE_HTML);

    Swal.fire({
        title: "Player Information",
        html: new_html,
        showConfirmButton: true,
        showAbortButton: false
    });

    while (Swal.isVisible() && Swal.getTitle() == "Player Information") {
        var thisPlayer = players.find(function(elem) { return (elem.uuid == player.uuid); });
        if (thisPlayer == null || thisPlayer == undefined) {
            Swal.getHtmlContainer().innerHTML += '<br><a class="has-text-danger">The Player left the Server!</a>';
            break;
        }

        Swal.getHtmlContainer().innerHTML = replaceObjectKeyInString(thisPlayer, PLAYER_DIALOGUE_HTML);
        await timer(1000);
    }
};

async function sendMessageClick(uuid) {
    var res = await Swal.fire({
        title: "Send Ingame Message",
        text: "Color Codes with '&' are Supported (e.g. '&6')!",
        input: "text",
        inputLable: "Message",
        confirmButtonText: "Send Message",
        showCancelButton: true,
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
        inputLable: "Reason",
        confirmButtonText: "Kick Player",
        showCancelButton: true,
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



var PLAYER_DIALOGUE_HTML = '\
<a><b>Displayname: </b>%DISPLAYNAME%<br>\
<a><b>UUID: </b>%UUID%<br><br>\
<a><b>Position: </b>X: %X%, Y: %Y%, Z: %Z% in %WORLD%<br>\
<a><b>Health: </b>%HEALTH%/%HEALTH_MAX%<br>\
<a><b>Food: </b>%FOOD%/20<br>\
<a><b>Groups: </b>%GROUPS%<br><br>\
<div class="button is-info m-1" onclick="sendMessageClick(this.getAttribute(\'data-uuid\'))" data-uuid="%UUID%">Message</div>\
<div class="button is-danger m-1" onclick="kickClick(this.getAttribute(\'data-uuid\'))" data-uuid="%UUID%">Kick</div>\
';

var PLAYER_BOX_HTML = '\
<div class="box player">\
    <img class="head" data-array-index=0 src="https://crafatar.com/avatars/7bdb8815-02f7-4817-8fae-2b9225b7a598">\
    <div class="details">\
        <div>%DISPLAYNAME%</div>\
        <div>%UUID%</div>\
    </div>\
</div>\
';