var currURLElem = document.querySelector("#currurl");
var currentMenu = null;
var contentContainer = null;

var PAGE_NAMES = {
    overview: "%T%OVERVIEW%T%",
    graphs: "%T%GRAPHS%T%",
    worlds: "%T%WORLDS%T%",
    console: "%T%CONSOLE%T%",
    controls: "%T%CONTROLS%T%",
    plugins: "%T%PLUGINS%T%",
    players: "%T%PLAYERS%T%",
    files: "%T%FILES%T%"
}

async function init() {
    try {
        await customEncryptor.init();
    } catch (err) {
        console.warn("[SECURITY] Failed to setup Encryptor!");
        console.error(err);
        Swal.fire({
            icon: "warning",
            title: "Encryption",
            html: "There was a Problem setting up Encryption for this connection!",
            allowClickOutside: false
        });
    }


    document.querySelectorAll(".menu-list>li>a").forEach((elem) => {
        elem.addEventListener("click", function(event) {
            loadPage(event.target.getAttribute("data-url").replace(".html", ""));
        });
    });

    theme = await getDataFromAPI({ method: "THEME" });

    console.log("[INDEX] Using Theme: " + theme);

    addNewTask("heightFillClass", heightFillRestClass, 1000);
    addNewTask("NOTIFICATIONS", refreshNotifications, 2000);

    loadPage("./pages/overview/overview");
    //loadPage("./pages/performance/worlds");

}

async function loadPage(url) {
    console.log("[LOADER] Loading Page '" + url + "'...");

    if (smartMenuHelpers.getConstructedByID(url) != null && !smartMenuHelpers.getConstructedByID(url).closed) {
        console.warn("[LOADER] Page already loaded");
        return;
    }

    curr_task = null;
    initPage = null;

    await smartMenuHelpers.closeAll();

    var pageName = PAGE_NAMES[url.split("/").latest().toLowerCase()];

    var menu = new smartMenu(url, pageName, pageName);
    menu.open();
    contentContainer = menu.getContentDOM();

    try {
        var html = url + ".html";
        var css = url + ".css";
        var js = url + ".js";

        var htmlt = await fetch(html);
        htmlt = await htmlt.text();
        contentContainer.innerHTML += htmlt;

        await loadCSSIfExists(css, contentContainer);
        loadJSIfExists(js, contentContainer);

        contentContainer.innerHTML = contentContainer.innerHTML.replace('<progress class="progress is-small is-primary" max="100">15%</progress>', '');
        hightlightMenu(html);
        heightFillRestClass();

        //MANAGE DATAREFRESH TASKS
        await stopTask("dataRefresher");
        await timer(100);
        addNewTask("dataRefresher", function() {
            if (curr_task == null) return;
            curr_task();
        }, 5000);
    } catch (err) {
        console.error("[LOADER] Page load failed: ");
        console.error(err);
        contentContainer.innerHTML = '<a class="has-text-danger">%T%PAGE_LOAD_FAILED%T%</a>';
    }

}

async function hightlightMenu(htmlurl) {
    document.querySelectorAll(".menu-list>li>a").forEach((elem) => {
        elem.classList.remove("is-active");
    });

    document.querySelectorAll(".menu-list>li>a[data-url='" + htmlurl + "']").forEach((elem) => {
        elem.classList.add("is-active");
    });
}

document.addEventListener("DOMContentLoaded", init);