var currentElement = getCurrentScriptElement();


/**
 * @file postscribe
 * @description Asynchronously write javascript, even with document.write.
 * @version v2.0.6
 * @see {@link https://krux.github.io/postscribe}
 * @license MIT
 * @author Derek Brans
 * @copyright 2016 Krux Digital, Inc
 */
!function(t,e){"object"==typeof exports&&"object"==typeof module?module.exports=e():"function"==typeof define&&define.amd?define([],e):"object"==typeof exports?exports.postscribe=e():t.postscribe=e()}(this,function(){return function(t){function e(r){if(n[r])return n[r].exports;var o=n[r]={exports:{},id:r,loaded:!1};return t[r].call(o.exports,o,o.exports,e),o.loaded=!0,o.exports}var n={};return e.m=t,e.c=n,e.p="",e(0)}([function(t,e,n){"use strict";function r(t){return t&&t.__esModule?t:{"default":t}}var o=n(1),i=r(o);t.exports=i["default"]},function(t,e,n){"use strict";function r(t){if(t&&t.__esModule)return t;var e={};if(null!=t)for(var n in t)Object.prototype.hasOwnProperty.call(t,n)&&(e[n]=t[n]);return e["default"]=t,e}function o(t){return t&&t.__esModule?t:{"default":t}}function i(){}function s(){var t=m.shift();if(t){var e=h.last(t);e.afterDequeue(),t.stream=a.apply(void 0,t),e.afterStreamStart()}}function a(t,e,n){function r(t){t=n.beforeWrite(t),g.write(t),n.afterWrite(t)}g=new p["default"](t,n),g.id=y++,g.name=n.name||g.id,u.streams[g.name]=g;var o=t.ownerDocument,a={close:o.close,open:o.open,write:o.write,writeln:o.writeln};c(o,{close:i,open:i,write:function(){for(var t=arguments.length,e=Array(t),n=0;t>n;n++)e[n]=arguments[n];return r(e.join(""))},writeln:function(){for(var t=arguments.length,e=Array(t),n=0;t>n;n++)e[n]=arguments[n];return r(e.join("")+"\n")}});var f=g.win.onerror||i;return g.win.onerror=function(t,e,r){n.error({msg:t+" - "+e+": "+r}),f.apply(g.win,[t,e,r])},g.write(e,function(){c(o,a),g.win.onerror=f,n.done(),g=null,s()}),g}function u(t,e,n){if(h.isFunction(n))n={done:n};else if("clear"===n)return m=[],g=null,void(y=0);n=h.defaults(n,d),t=/^#/.test(t)?window.document.getElementById(t.substr(1)):t.jquery?t[0]:t;var r=[t,e,n];return t.postscribe={cancel:function(){r.stream?r.stream.abort():r[1]=i}},n.beforeEnqueue(r),m.push(r),g||s(),t.postscribe}e.__esModule=!0;var c=Object.assign||function(t){for(var e=1;e<arguments.length;e++){var n=arguments[e];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(t[r]=n[r])}return t};e["default"]=u;var f=n(2),p=o(f),l=n(4),h=r(l),d={afterAsync:i,afterDequeue:i,afterStreamStart:i,afterWrite:i,autoFix:!0,beforeEnqueue:i,beforeWriteToken:function(t){return t},beforeWrite:function(t){return t},done:i,error:function(t){throw t},releaseAsync:!1},y=0,m=[],g=null;c(u,{streams:{},queue:m,WriteStream:p["default"]})},function(t,e,n){"use strict";function r(t){if(t&&t.__esModule)return t;var e={};if(null!=t)for(var n in t)Object.prototype.hasOwnProperty.call(t,n)&&(e[n]=t[n]);return e["default"]=t,e}function o(t){return t&&t.__esModule?t:{"default":t}}function i(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}function s(t,e){var n=d+e,r=t.getAttribute(n);return l.existy(r)?String(r):r}function a(t,e){var n=arguments.length<=2||void 0===arguments[2]?null:arguments[2],r=d+e;l.existy(n)&&""!==n?t.setAttribute(r,n):t.removeAttribute(r)}e.__esModule=!0;var u=Object.assign||function(t){for(var e=1;e<arguments.length;e++){var n=arguments[e];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(t[r]=n[r])}return t},c=n(3),f=o(c),p=n(4),l=r(p),h=!1,d="data-ps-",y="ps-style",m="ps-script",g=function(){function t(e){var n=arguments.length<=1||void 0===arguments[1]?{}:arguments[1];i(this,t),this.root=e,this.options=n,this.doc=e.ownerDocument,this.win=this.doc.defaultView||this.doc.parentWindow,this.parser=new f["default"]("",{autoFix:n.autoFix}),this.actuals=[e],this.proxyHistory="",this.proxyRoot=this.doc.createElement(e.nodeName),this.scriptStack=[],this.writeQueue=[],a(this.proxyRoot,"proxyof",0)}return t.prototype.write=function(){var t;for((t=this.writeQueue).push.apply(t,arguments);!this.deferredRemote&&this.writeQueue.length;){var e=this.writeQueue.shift();l.isFunction(e)?this._callFunction(e):this._writeImpl(e)}},t.prototype._callFunction=function(t){var e={type:"function",value:t.name||t.toString()};this._onScriptStart(e),t.call(this.win,this.doc),this._onScriptDone(e)},t.prototype._writeImpl=function(t){this.parser.append(t);for(var e=void 0,n=void 0,r=void 0,o=[];(e=this.parser.readToken())&&!(n=l.isScript(e))&&!(r=l.isStyle(e));)e=this.options.beforeWriteToken(e),e&&o.push(e);o.length>0&&this._writeStaticTokens(o),n&&this._handleScriptToken(e),r&&this._handleStyleToken(e)},t.prototype._writeStaticTokens=function(t){var e=this._buildChunk(t);return e.actual?(e.html=this.proxyHistory+e.actual,this.proxyHistory+=e.proxy,this.proxyRoot.innerHTML=e.html,h&&(e.proxyInnerHTML=this.proxyRoot.innerHTML),this._walkChunk(),h&&(e.actualInnerHTML=this.root.innerHTML),e):null},t.prototype._buildChunk=function(t){for(var e=this.actuals.length,n=[],r=[],o=[],i=t.length,s=0;i>s;s++){var a=t[s],u=a.toString();if(n.push(u),a.attrs){if(!/^noscript$/i.test(a.tagName)){var c=e++;r.push(u.replace(/(\/?>)/," "+d+"id="+c+" $1")),a.attrs.id!==m&&a.attrs.id!==y&&o.push("atomicTag"===a.type?"":"<"+a.tagName+" "+d+"proxyof="+c+(a.unary?" />":">"))}}else r.push(u),o.push("endTag"===a.type?u:"")}return{tokens:t,raw:n.join(""),actual:r.join(""),proxy:o.join("")}},t.prototype._walkChunk=function(){for(var t=void 0,e=[this.proxyRoot];l.existy(t=e.shift());){var n=1===t.nodeType,r=n&&s(t,"proxyof");if(!r){n&&(this.actuals[s(t,"id")]=t,a(t,"id"));var o=t.parentNode&&s(t.parentNode,"proxyof");o&&this.actuals[o].appendChild(t)}e.unshift.apply(e,l.toArray(t.childNodes))}},t.prototype._handleScriptToken=function(t){var e=this,n=this.parser.clear();n&&this.writeQueue.unshift(n),t.src=t.attrs.src||t.attrs.SRC,t=this.options.beforeWriteToken(t),t&&(t.src&&this.scriptStack.length?this.deferredRemote=t:this._onScriptStart(t),this._writeScriptToken(t,function(){e._onScriptDone(t)}))},t.prototype._handleStyleToken=function(t){var e=this.parser.clear();e&&this.writeQueue.unshift(e),t.type=t.attrs.type||t.attrs.TYPE||"text/css",t=this.options.beforeWriteToken(t),t&&this._writeStyleToken(t),e&&this.write()},t.prototype._writeStyleToken=function(t){var e=this._buildStyle(t);this._insertCursor(e,y),t.content&&(e.styleSheet&&!e.sheet?e.styleSheet.cssText=t.content:e.appendChild(this.doc.createTextNode(t.content)))},t.prototype._buildStyle=function(t){var e=this.doc.createElement(t.tagName);return e.setAttribute("type",t.type),l.eachKey(t.attrs,function(t,n){e.setAttribute(t,n)}),e},t.prototype._insertCursor=function(t,e){this._writeImpl('<span id="'+e+'"/>');var n=this.doc.getElementById(e);n&&n.parentNode.replaceChild(t,n)},t.prototype._onScriptStart=function(t){t.outerWrites=this.writeQueue,this.writeQueue=[],this.scriptStack.unshift(t)},t.prototype._onScriptDone=function(t){return t!==this.scriptStack[0]?void this.options.error({message:"Bad script nesting or script finished twice"}):(this.scriptStack.shift(),this.write.apply(this,t.outerWrites),void(!this.scriptStack.length&&this.deferredRemote&&(this._onScriptStart(this.deferredRemote),this.deferredRemote=null)))},t.prototype._writeScriptToken=function(t,e){var n=this._buildScript(t),r=this._shouldRelease(n),o=this.options.afterAsync;t.src&&(n.src=t.src,this._scriptLoadHandler(n,r?o:function(){e(),o()}));try{this._insertCursor(n,m),n.src&&!r||e()}catch(i){this.options.error(i),e()}},t.prototype._buildScript=function(t){var e=this.doc.createElement(t.tagName);return l.eachKey(t.attrs,function(t,n){e.setAttribute(t,n)}),t.content&&(e.text=t.content),e},t.prototype._scriptLoadHandler=function(t,e){function n(){t=t.onload=t.onreadystatechange=t.onerror=null}function r(){n(),e()}function o(t){n(),i(t),e()}var i=this.options.error;u(t,{onload:function(){return r()},onreadystatechange:function(){/^(loaded|complete)$/.test(t.readyState)&&r()},onerror:function(){return o({message:"remote script failed "+t.src})}})},t.prototype._shouldRelease=function(t){var e=/^script$/i.test(t.nodeName);return!e||!!(this.options.releaseAsync&&t.src&&t.hasAttribute("async"))},t}();e["default"]=g},function(t,e,n){!function(e,n){t.exports=n()}(this,function(){return function(t){function e(r){if(n[r])return n[r].exports;var o=n[r]={exports:{},id:r,loaded:!1};return t[r].call(o.exports,o,o.exports,e),o.loaded=!0,o.exports}var n={};return e.m=t,e.c=n,e.p="",e(0)}([function(t,e,n){"use strict";function r(t){return t&&t.__esModule?t:{"default":t}}var o=n(1),i=r(o);t.exports=i["default"]},function(t,e,n){"use strict";function r(t){return t&&t.__esModule?t:{"default":t}}function o(t){if(t&&t.__esModule)return t;var e={};if(null!=t)for(var n in t)Object.prototype.hasOwnProperty.call(t,n)&&(e[n]=t[n]);return e["default"]=t,e}function i(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}e.__esModule=!0;var s=n(2),a=o(s),u=n(3),c=o(u),f=n(6),p=r(f),l=n(5),h={comment:/^<!--/,endTag:/^<\//,atomicTag:/^<\s*(script|style|noscript|iframe|textarea)[\s\/>]/i,startTag:/^</,chars:/^[^<]/},d=function(){function t(){var e=this,n=arguments.length<=0||void 0===arguments[0]?"":arguments[0],r=arguments.length<=1||void 0===arguments[1]?{}:arguments[1];i(this,t),this.stream=n;var o=!1,s={};for(var u in a)a.hasOwnProperty(u)&&(r.autoFix&&(s[u+"Fix"]=!0),o=o||s[u+"Fix"]);o?(this._readToken=(0,p["default"])(this,s,function(){return e._readTokenImpl()}),this._peekToken=(0,p["default"])(this,s,function(){return e._peekTokenImpl()})):(this._readToken=this._readTokenImpl,this._peekToken=this._peekTokenImpl)}return t.prototype.append=function(t){this.stream+=t},t.prototype.prepend=function(t){this.stream=t+this.stream},t.prototype._readTokenImpl=function(){var t=this._peekTokenImpl();return t?(this.stream=this.stream.slice(t.length),t):void 0},t.prototype._peekTokenImpl=function(){for(var t in h)if(h.hasOwnProperty(t)&&h[t].test(this.stream)){var e=c[t](this.stream);if(e)return"startTag"===e.type&&/script|style/i.test(e.tagName)?null:(e.text=this.stream.substr(0,e.length),e)}},t.prototype.peekToken=function(){return this._peekToken()},t.prototype.readToken=function(){return this._readToken()},t.prototype.readTokens=function(t){for(var e=void 0;e=this.readToken();)if(t[e.type]&&t[e.type](e)===!1)return},t.prototype.clear=function(){var t=this.stream;return this.stream="",t},t.prototype.rest=function(){return this.stream},t}();e["default"]=d,d.tokenToString=function(t){return t.toString()},d.escapeAttributes=function(t){var e={};for(var n in t)t.hasOwnProperty(n)&&(e[n]=(0,l.escapeQuotes)(t[n],null));return e},d.supports=a;for(var y in a)a.hasOwnProperty(y)&&(d.browserHasFlaw=d.browserHasFlaw||!a[y]&&y)},function(t,e){"use strict";e.__esModule=!0;var n=!1,r=!1,o=window.document.createElement("div");try{var i="<P><I></P></I>";o.innerHTML=i,e.tagSoup=n=o.innerHTML!==i}catch(s){e.tagSoup=n=!1}try{o.innerHTML="<P><i><P></P></i></P>",e.selfClose=r=2===o.childNodes.length}catch(s){e.selfClose=r=!1}o=null,e.tagSoup=n,e.selfClose=r},function(t,e,n){"use strict";function r(t){var e=t.indexOf("-->");return e>=0?new c.CommentToken(t.substr(4,e-1),e+3):void 0}function o(t){var e=t.indexOf("<");return new c.CharsToken(e>=0?e:t.length)}function i(t){var e=t.indexOf(">");if(-1!==e){var n=t.match(f.startTag);if(n){var r=function(){var t={},e={},r=n[2];return n[2].replace(f.attr,function(n,o){arguments[2]||arguments[3]||arguments[4]||arguments[5]?arguments[5]?(t[arguments[5]]="",e[arguments[5]]=!0):t[o]=arguments[2]||arguments[3]||arguments[4]||f.fillAttr.test(o)&&o||"":t[o]="",r=r.replace(n,"")}),{v:new c.StartTagToken(n[1],n[0].length,t,e,!!n[3],r.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,""))}}();if("object"===("undefined"==typeof r?"undefined":u(r)))return r.v}}}function s(t){var e=i(t);if(e){var n=t.slice(e.length);if(n.match(new RegExp("</\\s*"+e.tagName+"\\s*>","i"))){var r=n.match(new RegExp("([\\s\\S]*?)</\\s*"+e.tagName+"\\s*>","i"));if(r)return new c.AtomicTagToken(e.tagName,r[0].length+e.length,e.attrs,e.booleanAttrs,r[1])}}}function a(t){var e=t.match(f.endTag);return e?new c.EndTagToken(e[1],e[0].length):void 0}e.__esModule=!0;var u="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(t){return typeof t}:function(t){return t&&"function"==typeof Symbol&&t.constructor===Symbol?"symbol":typeof t};e.comment=r,e.chars=o,e.startTag=i,e.atomicTag=s,e.endTag=a;var c=n(4),f={startTag:/^<([\-A-Za-z0-9_]+)((?:\s+[\w\-]+(?:\s*=?\s*(?:(?:"[^"]*")|(?:'[^']*')|[^>\s]+))?)*)\s*(\/?)>/,endTag:/^<\/([\-A-Za-z0-9_]+)[^>]*>/,attr:/(?:([\-A-Za-z0-9_]+)\s*=\s*(?:(?:"((?:\\.|[^"])*)")|(?:'((?:\\.|[^'])*)')|([^>\s]+)))|(?:([\-A-Za-z0-9_]+)(\s|$)+)/g,fillAttr:/^(checked|compact|declare|defer|disabled|ismap|multiple|nohref|noresize|noshade|nowrap|readonly|selected)$/i}},function(t,e,n){"use strict";function r(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}e.__esModule=!0,e.EndTagToken=e.AtomicTagToken=e.StartTagToken=e.TagToken=e.CharsToken=e.CommentToken=e.Token=void 0;var o=n(5),i=(e.Token=function s(t,e){r(this,s),this.type=t,this.length=e,this.text=""},e.CommentToken=function(){function t(e,n){r(this,t),this.type="comment",this.length=n||(e?e.length:0),this.text="",this.content=e}return t.prototype.toString=function(){return"<!--"+this.content},t}(),e.CharsToken=function(){function t(e){r(this,t),this.type="chars",this.length=e,this.text=""}return t.prototype.toString=function(){return this.text},t}(),e.TagToken=function(){function t(e,n,o,i,s){r(this,t),this.type=e,this.length=o,this.text="",this.tagName=n,this.attrs=i,this.booleanAttrs=s,this.unary=!1,this.html5Unary=!1}return t.formatTag=function(t){var e=arguments.length<=1||void 0===arguments[1]?null:arguments[1],n="<"+t.tagName;for(var r in t.attrs)if(t.attrs.hasOwnProperty(r)){n+=" "+r;var i=t.attrs[r];"undefined"!=typeof t.booleanAttrs&&"undefined"!=typeof t.booleanAttrs[r]||(n+='="'+(0,o.escapeQuotes)(i)+'"')}return t.rest&&(n+=" "+t.rest),n+=t.unary&&!t.html5Unary?"/>":">",void 0!==e&&null!==e&&(n+=e+"</"+t.tagName+">"),n},t}());e.StartTagToken=function(){function t(e,n,o,i,s,a){r(this,t),this.type="startTag",this.length=n,this.text="",this.tagName=e,this.attrs=o,this.booleanAttrs=i,this.html5Unary=!1,this.unary=s,this.rest=a}return t.prototype.toString=function(){return i.formatTag(this)},t}(),e.AtomicTagToken=function(){function t(e,n,o,i,s){r(this,t),this.type="atomicTag",this.length=n,this.text="",this.tagName=e,this.attrs=o,this.booleanAttrs=i,this.unary=!1,this.html5Unary=!1,this.content=s}return t.prototype.toString=function(){return i.formatTag(this,this.content)},t}(),e.EndTagToken=function(){function t(e,n){r(this,t),this.type="endTag",this.length=n,this.text="",this.tagName=e}return t.prototype.toString=function(){return"</"+this.tagName+">"},t}()},function(t,e){"use strict";function n(t){var e=arguments.length<=1||void 0===arguments[1]?"":arguments[1];return t?t.replace(/([^"]*)"/g,function(t,e){return/\\/.test(e)?e+'"':e+'\\"'}):e}e.__esModule=!0,e.escapeQuotes=n},function(t,e){"use strict";function n(t){return t&&"startTag"===t.type&&(t.unary=a.test(t.tagName)||t.unary,t.html5Unary=!/\/>$/.test(t.text)),t}function r(t,e){var r=t.stream,o=n(e());return t.stream=r,o}function o(t,e){var n=e.pop();t.prepend("</"+n.tagName+">")}function i(){var t=[];return t.last=function(){return this[this.length-1]},t.lastTagNameEq=function(t){var e=this.last();return e&&e.tagName&&e.tagName.toUpperCase()===t.toUpperCase()},t.containsTagName=function(t){for(var e,n=0;e=this[n];n++)if(e.tagName===t)return!0;return!1},t}function s(t,e,s){function a(){var e=r(t,s);e&&f[e.type]&&f[e.type](e)}var c=i(),f={startTag:function(n){var r=n.tagName;"TR"===r.toUpperCase()&&c.lastTagNameEq("TABLE")?(t.prepend("<TBODY>"),a()):e.selfCloseFix&&u.test(r)&&c.containsTagName(r)?c.lastTagNameEq(r)?o(t,c):(t.prepend("</"+n.tagName+">"),a()):n.unary||c.push(n)},endTag:function(n){var r=c.last();r?e.tagSoupFix&&!c.lastTagNameEq(n.tagName)?o(t,c):c.pop():e.tagSoupFix&&(s(),a())}};return function(){return a(),n(s())}}e.__esModule=!0,e["default"]=s;var a=/^(AREA|BASE|BASEFONT|BR|COL|FRAME|HR|IMG|INPUT|ISINDEX|LINK|META|PARAM|EMBED)$/i,u=/^(COLGROUP|DD|DT|LI|OPTIONS|P|TD|TFOOT|TH|THEAD|TR)$/i}])})},function(t,e){"use strict";function n(t){return void 0!==t&&null!==t}function r(t){return"function"==typeof t}function o(t,e,n){var r=void 0,o=t&&t.length||0;for(r=0;o>r;r++)e.call(n,t[r],r)}function i(t,e,n){for(var r in t)t.hasOwnProperty(r)&&e.call(n,r,t[r])}function s(t,e){return t=t||{},i(e,function(e,r){n(t[e])||(t[e]=r)}),t}function a(t){try{return Array.prototype.slice.call(t)}catch(e){var n=function(){var e=[];return o(t,function(t){e.push(t)}),{v:e}}();if("object"===("undefined"==typeof n?"undefined":l(n)))return n.v}}function u(t){return t[t.length-1]}function c(t,e){return t&&("startTag"===t.type||"atomicTag"===t.type)&&"tagName"in t?!!~t.tagName.toLowerCase().indexOf(e):!1}function f(t){return c(t,"script")}function p(t){return c(t,"style")}e.__esModule=!0;var l="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(t){return typeof t}:function(t){return t&&"function"==typeof Symbol&&t.constructor===Symbol?"symbol":typeof t};e.existy=n,e.isFunction=r,e.each=o,e.eachKey=i,e.defaults=s,e.toArray=a,e.last=u,e.isTag=c,e.isScript=f,e.isStyle=p}])});

onLoadMakeAdCall(currentElement)();

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

    var req_param_values = loc.split('/'); 
    var site_id_value    = req_param_values[pos];
    var num_ads = "1";
    var question_mark    = "?";
    var param_separator  = "&";
    var value_separator  = "=";

    var dsp_base_url     = "http://ads.mad.com/impdi/";

    var user_agent_param = "ua";
    var num_ads_param    = "nads";
    var site_param       = "site-id";
    var version          = "ver";
    var site_value       = site_id_value;
    var version_value    = "js_1";
    var height_param     = "h";
    var width_param      = "w";
  
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

    if(req_param_values.length >= (pos + 2) )
    {
        var width_value  = req_param_values[pos + 1];
        var height_value = req_param_values[pos + 2];
        post_payload     = post_payload.concat(param_separator);
        post_payload     = post_payload.concat(width_param);
        post_payload     = post_payload.concat(value_separator);
        post_payload     = post_payload.concat(width_value);
        post_payload     = post_payload.concat(param_separator);
        post_payload     = post_payload.concat(height_param);
        post_payload     = post_payload.concat(value_separator);
        post_payload     = post_payload.concat(height_value);
    }

    var exactbanner_size_param       = "eb";
    if(req_param_values.length >= (pos + 3) )
    {
        var exactbanner_value  = req_param_values[pos + 3];
        post_payload     = post_payload.concat(param_separator);
        post_payload     = post_payload.concat(exactbanner_size_param);
        post_payload     = post_payload.concat(value_separator);
        post_payload     = post_payload.concat(exactbanner_value);
    }

    
    xml_http.open("POST", dsp_base_url , true);
    xml_http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    var currentElementParent = currentElement.parentNode;
    currentElementParent.removeChild(currentElement);
    xml_http.onreadystatechange = fetchResponse(xml_http, currentElement, currentElementParent);
    xml_http.send(post_payload);
}

function getCurrentScriptElement() {
    var scripts = document.getElementsByTagName('script');
    var currentNode = scripts[scripts.length - 1];
    // alert("number of scripts = " + scripts.length);
    return currentNode;
}

function fetchResponse(xml_http, currentElement, currentElementParent) {
    return function() {
        var milliseconds = (new Date()).getTime();
        var state  = xml_http.readyState;
        var result = xml_http.status;
        // alert(state);

        if (state == 4 && result == 200) {
            // alert("Inside state = 4");
            var res = xml_http.responseText;
            var div = document.createElement("div");
            div.innerHTML  = res;
            var divChildren = div.children;

            var actualDiv = document.createElement("div");
            actualDiv.id = "fd_ad_"+milliseconds;
            currentElementParent.appendChild(actualDiv);

            for(i = 0; i < divChildren.length; i++) {
                var scriptSource = null;
                var childNode = divChildren[i];
                // div.removeChild(childNode);
                if(childNode.tagName.toLowerCase() == "script") {
                    // alert("src = " + childNode.getAttribute("src") + "\nText = " + childNode.text);
                    scriptSource = childNode.getAttribute("src");
                    if(scriptSource != null) {
                        postscribe("#fd_ad_"+milliseconds, '<script src=' + scriptSource + '><\/script>');
                    } else {
                        postscribe("#fd_ad_"+milliseconds, '<script>' + childNode.text + '<\/script>');
                    }
                } else {
                    actualDiv.appendChild(childNode);
                }
            }
        }
    }
}

