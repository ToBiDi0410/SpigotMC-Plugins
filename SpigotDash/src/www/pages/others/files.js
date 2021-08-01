var curr_path = "./";

var FILE_TEMPLATE_FOLDER = '<tr onclick="fileListClickEntry(this);" data-relative-path="%RELPATH%">\
    <th><span class="material-icons-outlined">folder</span></th >\
    <td>%NAME%</td>\
    <td>%MODF%</td>\
    <tr>';

var FILE_TEMPLATE_FILE = '<tr onclick="fileListClickEntry(this);" data-relative-path="%RELPATH%">\
    <th><span class="material-icons-outlined">insert_drive_file</span></th >\
    <td>%NAME%</td>\
    <td>%MODF%</td>\
    <tr>';

var FILE_TEMPLATE_BACK = '<tr onclick="goBackPath();">\
    <th><span class="material-icons-outlined">undo</span></th >\
    <td>Previous Folder</td>\
    <td></td>\
    <tr>';

async function refreshFiles() {
    var files = await getDataFromAPI({ method: "GET_FILES_IN_PATH", path: curr_path });
    files = files.sort((a, b) => (a.DIR == false) ? 1 : -1);
    var table_body = document.getElementsByTagName("tbody")[0];
    table_body.innerHTML = "";

    files.forEach((elem) => {
        table_body.innerHTML += getHTMLForFile(elem);
    });

    if (curr_path != "./") table_body.innerHTML += FILE_TEMPLATE_BACK;

    return files;
}

function initPage() {
    refreshFiles();
}

function getHTMLForFile(FILE_OBJ) {
    var html = "";
    if (FILE_OBJ.DIR == true) {
        html = FILE_TEMPLATE_FOLDER;
        FILE_OBJ.NAME += "/";
    } else {
        html = FILE_TEMPLATE_FILE;
    }

    html = html.replace("%NAME%", FILE_OBJ.NAME);
    html = html.replace("%MODF%", (new Date(FILE_OBJ.LAST_CHANGED)).toLocaleString());
    html = html.replace("%RELPATH%", curr_path + FILE_OBJ.NAME);

    return html;
}

async function fileListClickEntry(elem) {
    var path = elem.getAttribute("data-relative-path");
    var path_chars = path.split("");

    if (path_chars[path_chars.length - 1] == "/") {
        curr_path = path;
        refreshFiles();
    } else {
        openFileInViewer(path);
    }
}

async function openFileInViewer(path) {
    toggleFileViewer(false);

    var content_obj = document.querySelector(".fileview_content");
    var title_obj = document.querySelector(".fileview_title");
    var err_obj = document.querySelector(".fileview_error");
    var extension = path.split(".")[path.split(".").length - 1];

    try {

        if (!(path.includes("yml") || path.includes("yaml") || path.includes("json") || path.includes("txt") || path.includes("propertities"))) {
            throw new Error("File Format not Supported");
        }

        var data = await fetch(API_URL, {
            "headers": {
                "accept": "*/*",
                "accept-language": "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7,nl;q=0.6",
                "cache-control": "no-cache",
                "content-type": "application/json;charset=UTF-8",
            },
            "body": '{\n    "method": "GET_FILE_WITH_PATH", "path": "' + path + '"\n}',
            "method": "POST",
            "Cache-Control": "no-cache"

        });

        if (data.status != 200) {
            throw new Error(await data.text());
        }

        var text = await data.text();
        content_obj.innerHTML = text;
        extension = extension.replace("yml", "yaml");
        content_obj.classList = "fileview_content language-" + extension;
        content_obj.parentElement.classList.remove("hidden");
        err_obj.classList.add("hidden");
        hljs.highlightAll();
    } catch (err) {
        content_obj.parentElement.classList.add("hidden");
        err_obj.classList.remove("hidden");
        err_obj.querySelector(".fileview_error_code").innerHTML = err.toString();
    }

    title_obj.innerHTML = path.split("/")[path.split("/").length - 1];
    toggleFileViewer(true);

}

function goBackPath() {
    if (curr_path.split("/").length < 2) return;
    curr_path = curr_path.split("/")[0];
    if (curr_path == ".") curr_path = "./";
    refreshFiles();
}

function toggleFileViewer(state) {
    var fileviewer = document.querySelector(".fileview");
    if (!state) {
        fileviewer.classList.remove("shown")
        fileviewer.classList.add("hidden");
    } else {
        fileviewer.classList.add("shown")
        fileviewer.classList.remove("hidden");
    }
}