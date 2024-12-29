var interval = null;
var encryptedPasswords = new Map();

function createMessageBox() {
    var msgBox = document.getElementById("msgBoxNameTxt");
    var msgBoxName = msgBox.value.toLowerCase();
    var msgBoxPass = document.getElementById("msgBoxPassTxt").value;

    if (msgBoxName == "") {
        msgBoxName = "public";
    }
    msgBox.value = msgBoxName;

    var divBox = document.getElementById('messageCards');

    var http = new XMLHttpRequest();
    http.open("POST", "/createMessageBox", true);
    http.setRequestHeader("Content-type", "application/json");
    var params = { "msgBoxName": msgBoxName, "msgBoxPass": msgBoxPass };
    http.send(JSON.stringify(params));
    http.onload = function () {
        divBox.innerHTML = http.responseText;
        divBox.classList.remove('closed');
        pageLoadActions();
    }
    return false;
}


function openMessageBox() {
    var msgBox = document.getElementById("msgBoxNameTxt");
    var msgBoxName = msgBox.value.toLowerCase();
    var msgBoxPass = document.getElementById("msgBoxPassTxt").value;

    if (msgBoxName == "") {
        msgBoxName = "public";
    }
    msgBox.value = msgBoxName;

    var divBox = document.getElementById('messageCards');

    var http = new XMLHttpRequest();
    http.open("POST", "/openMessageBox", true);
    http.setRequestHeader("Content-type", "application/json");
    var params = { "msgBoxName": msgBoxName, "msgBoxPass": msgBoxPass };
    http.send(JSON.stringify(params));
    http.onload = function () {
        divBox.innerHTML = http.responseText;
        divBox.classList.remove('closed');
        pageLoadActions();
    }
    return false;
}

function postMessage() {
    var msgBox = document.getElementById("msgBoxNameTxt");
    var msgBoxName = msgBox.value.toLowerCase();
    var msgBoxPass = document.getElementById("msgBoxPassTxt").value;

    var userBox = document.getElementById("usernameTxt");
    var username = userBox.value;
    var expiry = document.getElementById("messageExpirySel").value;
    var messageTxtArea = document.getElementById("messageTxtArea");
    var message = messageTxtArea.value;

    if (msgBoxName == "") {
        msgBoxName = "public";
    }
    msgBox.value = msgBoxName;

    if (username == "") {
        username = "Anonymous";
        userBox.value = username;
    }

    var divBox = document.getElementById('messageCards');

    var http = new XMLHttpRequest();
    http.open("POST", "/postMessage", true);
    http.setRequestHeader("Content-type", "application/json");
    var params = {
        "msgBoxName": msgBoxName,
        "msgBoxPass": msgBoxPass,
        "username": username,
        "expiry": expiry,
        "message": message,
    };

    http.send(JSON.stringify(params));
    http.onload = function () {
        divBox.innerHTML = http.responseText;
        divBox.classList.remove('closed');
        messageTxtArea.value = "";
        pageLoadActions();
    }
    return false;
}


function deleteMessage(messageDeleteCode) {
    var msgBox = document.getElementById("msgBoxNameTxt");
    var msgBoxName = msgBox.value.toLowerCase();
    var msgBoxPass = document.getElementById("msgBoxPassTxt").value;

    if (msgBoxName == "") {
        msgBoxName = "public";
    }
    msgBox.value = msgBoxName;

    var divBox = document.getElementById('messageCards');

    var http = new XMLHttpRequest();
    http.open("POST", "/deleteMessage", true);
    http.setRequestHeader("Content-type", "application/json");
    var params = { "msgBoxName": msgBoxName, "msgBoxPass": msgBoxPass, "messageDeleteCode": messageDeleteCode };
    http.send(JSON.stringify(params));
    http.onload = function () {
        divBox.innerHTML = http.responseText;
        divBox.classList.remove('closed');
        pageLoadActions();
    }
    return false;
}


function lengthCounter() {
    var el = document.getElementById("count_message");
    var txtArea = document.getElementById("messageTxtArea");
    el.innerHTML = txtArea.value.length + " / " + txtArea.maxLength;
}


function linkify(inputText) {
    var replacedText, replacePattern1, replacePattern2, replacePattern3;

    //URLs starting with http://, https://, or ftp://
    replacePattern1 = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
    replacedText = inputText.replaceAll(replacePattern1, '<a href="$1" target="_blank">$1</a>');

    //URLs starting with "www." (without // before it, or it'd re-link the ones done above).
    replacePattern2 = /(^|[^\/])(www\.[\S]+(\b|$))/gim;
    replacedText = replacedText.replaceAll(replacePattern2, '$1<a href="http://$2" target="_blank">$2</a>');

    //Change email addresses to mailto:: links.
    replacePattern3 = /(([a-zA-Z0-9\-\_\.])+@[a-zA-Z\_]+?(\.[a-zA-Z]{2,6})+)/gim;
    replacedText = replacedText.replaceAll(replacePattern3, '<a href="mailto:$1">$1</a>');

    var src_arr = ["\n", "[b]", "[/b]", "[i]", "[/i]"];
    var rep_arr = ["<br>", "<strong>", "</strong>", "<em>", "</em>"];
    for (var i = 0; i < src_arr.length; i++) {
        replacedText = replacedText.replaceAll(src_arr[i], rep_arr[i]);
    }

    return replacedText;
}

function processCardContents() {
    var el;
    for (var id = 1; id <= 100; id++) {
        el = document.getElementById("card_" + id);
        if (el == null) {
            return;
        }
        else {
            el.innerHTML = linkify(el.innerHTML);
            decryptMessageProcess(id);
        }
    }
}

function getInputSelection(el) {
    var start = 0, end = 0, normalizedValue, range, textInputRange, len, endRange;

    if (typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") {
        start = el.selectionStart;
        end = el.selectionEnd;
    } else {
        range = document.selection.createRange();

        if (range && range.parentElement() == el) {
            len = el.value.length;
            normalizedValue = el.value.replace(/\r\n/g, "\n");

            // Create a working TextRange that lives only in the input
            textInputRange = el.createTextRange();
            textInputRange.moveToBookmark(range.getBookmark());

            // Check if the start and end of the selection are at the very end
            // of the input, since moveStart/moveEnd doesn't return what we want
            // in those cases
            endRange = el.createTextRange();
            endRange.collapse(false);

            if (textInputRange.compareEndPoints("StartToEnd", endRange) > -1) {
                start = end = len;
            } else {
                start = -textInputRange.moveStart("character", -len);
                start += normalizedValue.slice(0, start).split("\n").length - 1;

                if (textInputRange.compareEndPoints("EndToEnd", endRange) > -1) {
                    end = len;
                } else {
                    end = -textInputRange.moveEnd("character", -len);
                    end += normalizedValue.slice(0, end).split("\n").length - 1;
                }
            }
        }
    }

    return {
        start: start,
        end: end
    };
}

function replaceSelectedText(el, tag0, tag1) {
    var sel = getInputSelection(el)
    var fullTxt = el.value;
    el.value = fullTxt.slice(0, sel.start) + tag0 + fullTxt.substring(sel.start, sel.end) + tag1 + fullTxt.slice(sel.end);
    lengthCounter();
}

function boldText() {
    var el = document.getElementById("messageTxtArea");
    replaceSelectedText(el, "[b]", "[/b]");
}

function italicText() {
    var el = document.getElementById("messageTxtArea");
    replaceSelectedText(el, "[i]", "[/i]");
}

function encryptText() {
    var el = document.getElementById("messageTxtArea");
    var plainTxt = el.value;
    var hashedPwd = sha256(prompt("Enter encryption password:"));
    var encryptedTxt = encrypt(plainTxt, hashedPwd, true);
    var json = JSON.stringify(encryptedTxt);
    el.value = "Encoded: \n" + btoa(json);
    lengthCounter();
}

function decryptMessage(id) {
    var card_id = "card_" + id;
    var hashedPwd = sha256(prompt("Enter encryption password:"));
    encryptedPasswords.set(card_id, hashedPwd);
    decryptMessageProcess(id);
    return false;
}

function decryptMessageProcess(id) {
    var card_id = "card_" + id;
    var el_source = document.getElementById("raw_" + card_id);
    var el_dest = document.getElementById(card_id);

    if (encryptedPasswords.has(card_id)) {
        var hashedPwd = encryptedPasswords.get(card_id);

        if (hashedPwd.length <= 5)
            return false;
        var parts = el_source.value.split("\n");
        var json = JSON.parse(atob(parts[1]));

        try {
            var decoded = "Decoded message:\n" + decrypt(json["data"], hashedPwd, json["iv"]);
            el_dest.innerHTML = linkify(decoded);
        } catch (error) {
            console.error(error);
            encryptedPasswords.delete(card_id);
        }
    }
}


function attachFile() {
    document.getElementById("file").click();
}

function uploadFile() {
    var fileShare = document.getElementById("fileshare_site_url").value;
    if (fileShare.length < 10 || fileShare.substring(0, 4) != "http") {
        alert("FILESHARE_SITE_FULL_URL not set.")
        return false;
    }
    fileShare = fileShare.substring(0, fileShare.length - 1);

    var file = document.getElementById("file");

    if (!file.files[0])
        return;

    let form = document.querySelector("form");
    let data = new FormData(form);

    let percent = document.getElementById("upload_btn");
    var el = document.getElementById("messageTxtArea");
    let http = new XMLHttpRequest();
    http.open("POST", fileShare + "/file/upload");

    http.upload.addEventListener("progress", ({ loaded, total }) => {
        let fileLoaded = Math.floor((loaded / total) * 100);
        let fileTotal = Math.floor(total / 1000);
        let fileSize;
        (fileTotal < 1024) ? fileSize = fileTotal + " KB" : fileSize = (loaded / (1024 * 1024)).toFixed(2) + " MB";
        percent.innerHTML = fileLoaded + "%";
        if (loaded == total) {
            percent.innerHTML = "...";
        }
    });

    http.send(data);
    http.onload = function () {
        if (http.responseText.length > 0) {
            percent.innerHTML = "&#x1F517;";
            var api_reply = JSON.parse(http.responseText);
            console.log(api_reply);
            if (api_reply['status'] == "OK") {
                document.getElementById("result-error").style.display = "none";
                el.value += "Link: " + fileShare + api_reply['content']['url'] + "\n";
                el.value += "Delete: " + fileShare + api_reply['content']['deleteUrl'];
                lengthCounter();
            }
            else if (api_reply['status'] == "FAIL") {
                document.getElementById("result-error").innerHTML = '<div class="alert alert-danger" role="alert"><strong>ERROR:</strong> ' + api_reply['error'] + '</div>';
                document.getElementById("result-error").style.display = "";
                document.getElementById('slider').classList.remove('closed');
            }
            else
                alert("File upload failed as server returned error.");
        }
        else
            alert("File upload failed as failed contacting the server.");
    }
    return false;
}

function pageLoadActions() {
    // Linkify all raw text
    processCardContents();
    // Length counter for text area
    lengthCounter();

    // Reload every 5 seconds
    var cardDiv = document.getElementById("all_cards");
    console.log(cardDiv);
    if (cardDiv != null) {
        if (interval == null) {
            interval = setInterval(function () {
                openMessageBox();
            }, 10 * 1000);
        }
    }
    else {
        clearInterval(interval);
        interval = null;
    }
}

function firstLoadOpenMessageBox() {
    var msgBox = document.getElementById("msgBoxNameTxt");
    const urlParams = new URLSearchParams(window.location.search);
    const messageBoxName = urlParams.get("inbox");
    if (messageBoxName != null && messageBoxName != undefined && messageBoxName.length > 3) {
        msgBox.value = messageBoxName;
        openMessageBox();
    } else if (msgBox.value.length > 3) {
        openMessageBox();
    }
}