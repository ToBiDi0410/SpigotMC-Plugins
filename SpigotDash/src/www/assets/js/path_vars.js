var url = window.location.href;
var RELATIVE_PATH = url.split("/")[0] + "//" + url.split("/")[2] + "/";
//RELATIVE_PATH = "http://localhost:9677/";
var API_URL = RELATIVE_PATH + "api";
var AUTH_URL = RELATIVE_PATH + "auth"