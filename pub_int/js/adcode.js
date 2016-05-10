// window.onload = onLoadMakeAdCall(currentElement);
var currentElement = getCurrentScriptElement();
onLoadMakeAdCall(currentElement)();
// addLoadEvent(onLoadMakeAdCall(currentElement));

/*
function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
        window.onload = func;
    } else {
        window.onload = function() {
            if (oldonload) {
                oldonload();
            }
            func();
        }
    }
}
*/

function onLoadMakeAdCall(currentElement) {
    return function() {
        // var currentElement = getCurrentScriptElement();
        var loc = currentElement.src;
        var val = loc.split('/')[3];
        // alert(loc);
        if(val != "test") {
            // alert("outside test");
            makeAdCall(loc, currentElement, 6);
        } else {
            // alert("inside test");
            makeTestAdCall(currentElement, 4);
        }
    }
}

function makeTestAdCall(currentElement, pos) {
    var loc = window.location.href;
    var mod_loc = loc.replace("?siteid=", "");
    // alert("loc = " + mod_loc);
    makeAdCall(mod_loc, currentElement, pos);
}

function makeAdCall(loc, currentElement, pos) {
    var xml_http;      
    // alert("I am here");

    if (window.XMLHttpRequest)
    {// code for IE7+, Firefox, Chrome, Opera, Safari
        xml_http = new XMLHttpRequest();
    }
    else
    {// code for IE6, IE5
        xml_http = new ActiveXObject("Microsoft.XMLHTTP");
    }

    var site_id_value = loc.split('/')[pos];
    var num_ads = "1";
    var question_mark    = "?";
    var param_separator  = "&";
    var value_separator  = "=";

    var dsp_base_url     = "http://ads.mad.com/impdi/";

    var user_agent_param = "ua";
    var num_ads_param    = "nads";
    var site_param       = "site-id";
    var version              = "ver";
    var site_value       = site_id_value;
    var version_value        = "js_1";

    var user_agent_value = navigator.userAgent;
    var post_payload     = user_agent_param;
    post_payload = post_payload.concat(value_separator);
    post_payload = post_payload.concat(user_agent_value);
    post_payload = post_payload.concat(param_separator);
    post_payload = post_payload.concat(site_param);
    post_payload = post_payload.concat(value_separator);
    post_payload = post_payload.concat(site_value);
    post_payload = post_payload.concat(param_separator);
    post_payload = post_payload.concat(num_ads_param);
    post_payload = post_payload.concat(value_separator);
    post_payload = post_payload.concat(num_ads);
    post_payload = post_payload.concat(param_separator);
    post_payload = post_payload.concat(version);
    post_payload = post_payload.concat(value_separator);
    post_payload = post_payload.concat(version_value);
    xml_http.open("POST", dsp_base_url , true);
    xml_http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xml_http.onreadystatechange = fetchResponse(xml_http, currentElement);
    xml_http.send(post_payload);
}

function getCurrentScriptElement() {
    var scripts = document.getElementsByTagName('script');
    var currentNode = scripts[scripts.length - 1];
    // alert("number of scripts = " + scripts.length);
    return currentNode;
}

function fetchResponse(xml_http, currentElement) {
    return function() {
        var state  = xml_http.readyState;
        var result = xml_http.status;
        // alert(state);

        if (state == 4 && result == 200)
        {
            // alert("Inside state = 4");
            var res = xml_http.responseText;
            var div = document.createElement("div");
            div.innerHTML  = res;
            // var beforeNode = document.getElementById('bidsopt_js_adcode');
            var scriptParentNode = currentElement.parentNode;
            // alert("Parent node = ");
            // alert(scriptParentNode.hasAttributes());
            // alert(scriptParentNode.hasChildNodes());
            scriptParentNode.insertBefore(div, currentElement);
        }
    }
}

