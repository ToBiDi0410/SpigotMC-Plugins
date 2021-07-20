var currURLElem = document.querySelector("#currurl");
var contentContainer = document.querySelector("#pagecontent");

async function init() {
    document.querySelectorAll(".menu-list>li>a").forEach((elem) => {
        elem.addEventListener("click", function(event) {
            loadPage(event.target.getAttribute("data-url").replace(".html", ""));
        });
    });

    theme = await getDataFromAPI({ method: "THEME" });

    console.log("[INDEX] Using Theme: " + theme);
    if (theme == "dark") {
        loadCSSIfExists("https://unpkg.com/bulmaswatch/darkly/bulmaswatch.min.css", document.head);
        loadCSSIfExists("https://cdn.jsdelivr.net/npm/@sweetalert2/theme-dark@3/dark.css", document.head);
    }

    loadPage("./pages/management/plugins");
}

async function loadPage(url) {
    var currURL = currURLElem.getAttribute("data-page");

    initPage = null;
    curr_task = null;
    if (currURL == url) {
        return;
    }

    console.log("[LOADER] Loading Page: " + url);
    contentContainer.innerHTML = '<progress class="progress is-small is-primary" max="100">15%</progress>';
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
        currURLElem.setAttribute("data-page", url);

        await stopTask("dataRefresher");

        await timer(100);
        addNewTask("dataRefresher", function() {
            if (curr_task == null) return;
            curr_task();
        }, 5000);
    } catch (err) {
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