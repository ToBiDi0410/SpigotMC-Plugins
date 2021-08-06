var currURLElem = document.querySelector("#currurl");
var currentMenu = null;
var contentContainer = null;

async function init() {
    document.querySelectorAll(".menu-list>li>a").forEach((elem) => {
        elem.addEventListener("click", function(event) {
            loadPage(event.target.getAttribute("data-url").replace(".html", ""));
        });
    });

    theme = await getDataFromAPI({ method: "THEME" });
    theme = "dark";

    console.log("[INDEX] Using Theme: " + theme);
    if (theme == "dark") {
        loadCSSIfExists("./global/other-license/bulmaswatch.min.css", document.head);
        loadCSSIfExists("./global/other-license/sweet_dark.css", document.head);
        smartMenuHelpers.DARK_MODE = true;
    }

    addNewTask("heightFillClass", heightFillRestClass, 1000);

    loadPage("./pages/overview/overview");
    //loadPage("./pages/management/plugins");

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

    var pageName = url.split("/").latest().capitalizeFirstLetter();

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
        contentContainer.innerHTML = '<a class="has-text-danger">Page load failed! Is the Server online?</a>';
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