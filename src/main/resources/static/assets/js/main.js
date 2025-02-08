var interval = null;
var encryptedPasswords = new Map();
var msgHtml = "";

function createMessageBox() {
    let msgBox = document.getElementById("msgBoxNameTxt");
    let msgBoxName = msgBox.value.toLowerCase();
    let msgBoxPass = document.getElementById("msgBoxPassTxt").value;

    if (msgBoxName == "") {
        msgBoxName = "public";
    }
    msgBox.value = msgBoxName;

    let http = new XMLHttpRequest();
    http.open("POST", "/createMessageBox", true);
    http.setRequestHeader("Content-type", "application/json");
    let params = { "msgBoxName": msgBoxName, "msgBoxPass": msgBoxPass };
    http.send(JSON.stringify(params));
    http.onload = function () {
        showCards(http.responseText);
        pageLoadActions();
        updateUrlWithMsgBoxName(msgBoxName);
    }
    return false;
}


function openMessageBox() {
    let msgBox = document.getElementById("msgBoxNameTxt");
    let msgBoxName = msgBox.value.toLowerCase();
    let msgBoxPass = document.getElementById("msgBoxPassTxt").value;

    if (msgBoxName == "") {
        msgBoxName = "public";
    }
    msgBox.value = msgBoxName;

    let http = new XMLHttpRequest();
    http.open("POST", "/openMessageBox", true);
    http.setRequestHeader("Content-type", "application/json");
    let params = { "msgBoxName": msgBoxName, "msgBoxPass": msgBoxPass };
    http.send(JSON.stringify(params));
    http.onload = function () {
        if (http.responseText != msgHtml) {
            msgHtml = http.responseText;
            showCards(http.responseText);
            pageLoadActions();
            updateUrlWithMsgBoxName(msgBoxName)
        }
    }
    return false;
}


function firstLoadOpenMessageBox() {
    let msgBox = document.getElementById("msgBoxNameTxt");
    const urlParams = new URLSearchParams(window.location.search);
    const messageBoxName = urlParams.get("inbox");
    if (messageBoxName != null && messageBoxName != undefined && messageBoxName.length > 3) {
        msgBox.value = messageBoxName;
        openMessageBox();
    } else if (msgBox.value.length >= 3) {
        openMessageBox();
    }
}


function postMessage() {
    let msgBox = document.getElementById("msgBoxNameTxt");
    let msgBoxName = msgBox.value.toLowerCase();
    let msgBoxPass = document.getElementById("msgBoxPassTxt").value;

    let userBox = document.getElementById("usernameTxt");
    let username = userBox.value;
    let expiry = document.getElementById("messageExpirySel").value;
    let messageTxtArea = document.getElementById("messageTxtArea");
    let message = messageTxtArea.value;

    if (msgBoxName == "") {
        msgBoxName = "public";
    }
    msgBox.value = msgBoxName;

    if (username == "") {
        username = "Anonymous";
        userBox.value = username;
    }

    saveUsername(username);

    let http = new XMLHttpRequest();
    http.open("POST", "/postMessage", true);
    http.setRequestHeader("Content-type", "application/json");
    let params = {
        "msgBoxName": msgBoxName,
        "msgBoxPass": msgBoxPass,
        "username": username,
        "expiry": expiry,
        "message": message,
    };

    http.send(JSON.stringify(params));
    http.onload = function () {
        showCards(http.responseText);
        messageTxtArea.value = "";
        pageLoadActions();
    }
    return false;
}


function expandMessage(id) {
    let card_header = document.getElementById("card_header_" + id);
    let card_body = document.getElementById("card_" + id);
    let modal_header = document.getElementById("modalPopoutHeader");
    let modal_body = document.getElementById("modalPopoutBody");
    modal_header.innerHTML = card_header.innerHTML;
    modal_body.innerHTML = card_body.innerHTML;
}


function copyMessage(id) {
    let messageTxtArea = document.getElementById("messageTxtArea");
    let card_text = document.getElementById("raw_card_" + id).value;
    messageTxtArea.value = card_text;

}


function deleteMessage(messageDeleteCode) {
    let msgBox = document.getElementById("msgBoxNameTxt");
    let msgBoxName = msgBox.value.toLowerCase();
    let msgBoxPass = document.getElementById("msgBoxPassTxt").value;

    if (msgBoxName == "") {
        msgBoxName = "public";
    }
    msgBox.value = msgBoxName;

    let http = new XMLHttpRequest();
    http.open("POST", "/deleteMessage", true);
    http.setRequestHeader("Content-type", "application/json");
    let params = { "msgBoxName": msgBoxName, "msgBoxPass": msgBoxPass, "messageDeleteCode": messageDeleteCode };
    http.send(JSON.stringify(params));
    http.onload = function () {
        showCards(http.responseText);
        pageLoadActions();
    }
    return false;
}


function lengthCounter() {
    let el = document.getElementById("count_message");
    let txtArea = document.getElementById("messageTxtArea");
    el.innerHTML = parseInt(txtArea.value.length / 1000) + "k / " + parseInt(txtArea.maxLength / 1000) + "k";
}


function linkify(inputText) {
    let replacedText, replacePattern1, replacePattern2, replacePattern3;

    //URLs starting with http://, https://, ftp:// or www
    replacePattern1 = /((?<!\])\b(?:https?|ftp):\/\/[^\s\/$.?#].[^\s]*\/?|\bwww\.[^\s\/$.?#].[^\s]*\/?)/gi;
    replacedText = inputText.replaceAll(replacePattern1, '<a href="$1" target="_blank">$1</a>');

    //Change email addresses to mailto:: links.
    replacePattern2 = /(([a-zA-Z0-9\-\_\.])+@[a-zA-Z\_]+?(\.[a-zA-Z]{2,6})+)/gim;
    replacedText = replacedText.replaceAll(replacePattern2, '<a href="mailto:$1">$1</a>');

    let src_arr = ["\n", "[b]", "[/b]", "[i]", "[/i]", "[img]", "[/img]", "[code]", "[/code]"];
    let rep_arr = ["<br>", "<strong>", "</strong>", "<em>", "</em>", "<img src=\"", "\" alt=\"image\" />", "<pre><code>", "</code></pre>"];
    for (let i = 0; i < src_arr.length; i++) {
        replacedText = replacedText.replaceAll(src_arr[i], rep_arr[i]);
    }

    return replacedText;
}


function processCardContents() {
    let el;
    for (let id = 1; id <= 100; id++) {
        el = document.getElementById("card_" + id);
        if (el == null) {
            return;
        }
        else {
            el.innerHTML = linkify(el.innerHTML);
            decryptMessageProcess(id);
            showExpandButton(id);
            generateAvatar(id);
        }
    }
}


function getInputSelection(el) {
    let start = 0, end = 0, normalizedValue, range, textInputRange, len, endRange;

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
    let sel = getInputSelection(el)
    let fullTxt = el.value;
    el.value = fullTxt.slice(0, sel.start) + tag0 + fullTxt.substring(sel.start, sel.end) + tag1 + fullTxt.slice(sel.end);
    lengthCounter();
}


function boldText() {
    let el = document.getElementById("messageTxtArea");
    replaceSelectedText(el, "[b]", "[/b]");
}


function italicText() {
    let el = document.getElementById("messageTxtArea");
    replaceSelectedText(el, "[i]", "[/i]");
}

function codeText() {
    let el = document.getElementById("messageTxtArea");
    replaceSelectedText(el, "[code]", "\n[/code]");
}


function attachImage() {
    document.getElementById("attach_image").click();
}


function uploadImage() {
    let fileShare = document.getElementById("imageshare_site_url").value;
    if (fileShare.length < 10 || fileShare.substring(0, 4) != "http") {
        alert("IMAGESHARE_SITE_FULL_URL not set.")
        return false;
    }
    // Remove trailing slash
    if (fileShare[fileShare.length - 1] == "/")
        fileShare = fileShare.substring(0, fileShare.length - 1);

    let file = document.getElementById("attach_image");

    if (!file.files[0])
        return;

    let form = document.getElementById("image_upload_form");
    let data = new FormData(form);

    let el = document.getElementById("messageTxtArea");
    let http = new XMLHttpRequest();
    http.open("POST", fileShare + "/index.php");

    http.send(data);
    http.onload = function () {
        if (http.responseText.length > 0) {
            let api_reply = JSON.parse(http.responseText);
            if (api_reply['status'] == "OK") {
                document.getElementById("result-error").style.display = "none";
                el.value += "[img]" + api_reply['url'] + "[/img]\n";
                lengthCounter();
            }
            else if (api_reply['status'] == "FAIL") {
                document.getElementById("result-error").innerHTML = '<div class="alert alert-danger" role="alert"><strong>ERROR:</strong> ' + api_reply['msg'] + '</div>';
                document.getElementById("result-error").style.display = "";
                document.getElementById('slider').classList.remove('collapse');
            }
            else
                alert("Image upload failed as server returned error.");
        }
        else
            alert("Image upload failed as failed contacting the server.");
    }
    return false;
}


function attachFile() {
    document.getElementById("attach_file").click();
}


function uploadFile() {
    let fileShare = document.getElementById("fileshare_site_url").value;
    let expiry = document.getElementById("messageExpirySel").value;

    if (fileShare.length < 10 || fileShare.substring(0, 4) != "http") {
        alert("FILESHARE_SITE_FULL_URL not set.")
        return false;
    }
    // Remove trailing slash
    if (fileShare[fileShare.length - 1] == "/")
        fileShare = fileShare.substring(0, fileShare.length - 1);

    // Set the expiry date in file upload form
    let fileExpiry = document.getElementById("file_expiry");
    fileExpiry.value = expiry;

    let file = document.getElementById("attach_file");
    if (!file.files[0])
        return;

    let form = document.getElementById("file_upload_form");
    let data = new FormData(form);

    let percent = document.getElementById("upload_btn");
    let originalHtml = percent.innerHTML;
    let el = document.getElementById("messageTxtArea");
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
            percent.innerHTML = originalHtml;
            let api_reply = JSON.parse(http.responseText);
            if (api_reply['status'] == "OK") {
                document.getElementById("result-error").style.display = "none";
                el.value += "Link: " + fileShare + api_reply['content']['url'] + "\n";
                el.value += "Delete: " + fileShare + api_reply['content']['deleteUrl'];
                lengthCounter();
            }
            else if (api_reply['status'] == "FAIL") {
                document.getElementById("result-error").innerHTML = '<div class="alert alert-danger" role="alert"><strong>ERROR:</strong> ' + api_reply['error'] + '</div>';
                document.getElementById("result-error").style.display = "";
                document.getElementById('slider').classList.remove('collapse');
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
    let cardDiv = document.getElementById("all_cards");
    if (cardDiv != null) {
        clearInterval(interval);
        interval = setInterval(function () {
            openMessageBox();
        }, 10 * 1000);

    }
    else {
        clearInterval(interval);
        interval = null;
    }
}


function updateUrlWithMsgBoxName(msgBoxName) {
    let pathname = new URL(location.href).pathname; // Extracts the path
    let name = pathname.split('/').pop();
    if (name != msgBoxName)
        window.history.replaceState({}, "", '/' + msgBoxName);
}


function showExpandButton(id) {
    let el = document.getElementById("card_" + id);
    let expandButton = document.getElementById("card_expand_" + id);
    if (el.scrollHeight > el.offsetHeight) {
        expandButton.style.display = "";
    }
}


function generateAvatar(id) {
    let el = document.getElementById("card_avatar_" + id);
    let hash = el.getAttribute('data-hash');
    let options = {
        size: 42,
        format: 'svg'
    };
    let data = new Identicon(hash, options).toString();
    el.src = "data:image/svg+xml;base64," + data
}


function saveUsername(username) {
    setCookie("username", username, 30);
}


function getUsername() {
    let el = document.getElementById("usernameTxt");
    let username = getCookie("username");
    // validation happens in getCookie method
    el.value = username;
}


function setCookie(key, value, expiryDays) {
    const d = new Date();
    d.setTime(d.getTime() + (expiryDays * 24 * 60 * 60 * 1000));
    let expires = "expires=" + d.toUTCString();
    document.cookie = key + "=" + value + ";" + expires + ";path=/;SameSite=Strict";
}


function getCookie(key) {
    let name = key + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}


function showCards(data) {
    let inputCard = document.getElementById("inputCard");
    inputCard.classList.remove('collapse');

    let divBox = document.getElementById('messageCards');
    divBox.innerHTML = data;
    divBox.classList.remove('collapse');
}