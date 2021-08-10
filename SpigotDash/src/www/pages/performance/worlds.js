async function initPage() {
    curr_task = refreshWorlds;
}

async function refreshWorlds() {
    var worldListDom = document.querySelector(".worldlist");

    var data = await getDataFromAPI({ method: "GET_WORLDS" });
    worldListDom.innerHTML = "";

    for (const elem of data) {
        worldListDom.appendChild(generateWorldEntry(elem));
    }
}

async function openWorldMenu(worldname) {
    var menu = new smartMenu("WOLRD_INFO", worldname, worldname);
    menu.open();

    var data = await getDataFromAPI({ method: "GET_WORLD", world: worldname });
    console.log(data);

    var entitieList = document.querySelector(".entitieDropDownCont");
    entitieList.innerHTML = "";

    for (const [key, value] of Object.entries(data.Entities)) {
        entitieList.appendChild(generateWorldEntitieEntry(key, value));
    }
    //menu.setHTML(JSON.stringify(data));
}