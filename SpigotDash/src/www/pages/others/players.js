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
    })
};

var PLAYER_DIALOGUE_HTML = '\
<a><b>Displayname: </b>%DISPLAYNAME%<br>\
<a><b>UUID: </b>%UUID%<br><br>\
<a><b>Position: </b>X: %X%, Y: %Y%, Z: %Z% in %WORLD%<br>\
<a><b>Health: </b>%HEALTH%/%HEALTH_MAX%<br>\
<a><b>Food: </b>%FOOD%/20<br>\
<a><b>Groups: </b>%GROUPS%<br><br>\
<div class="button is-info m-1">Message</div><br>\
<div class="button is-danger m-1">Ban</div>\
<div class="button is-danger m-1">Kick</div>\
<div class="button is-danger m-1">Remove from Whitelist</div>\
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