function generateRandomString(length = 12) {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
}


async function deriveKey(keyStr, saltStr, iterations) {
    const baseKey = await window.crypto.subtle.importKey(
        "raw", new TextEncoder().encode(keyStr), "PBKDF2", false, ["deriveKey"]
    );
    const derivedKey = await window.crypto.subtle.deriveKey(
        {
            name: "PBKDF2",
            salt: new TextEncoder().encode(saltStr),
            iterations: iterations,
            hash: "SHA-256"
        },
        baseKey,
        {
            name: "AES-GCM",
            length: 256
        },
        false,
        ["encrypt", "decrypt"]
    );
    return derivedKey;
}


async function encrypt(data, pass, id, format = "hex") {
    const saltStr = generateRandomString();
    const iterations = Math.floor(Math.random() * (5000 - 1000 + 1)) + 1000;

    // Convert the Salt string to a Uint8Array
    const salt = new Uint8Array(new TextEncoder().encode(saltStr));

    // Derive the encryption key
    const key = await deriveKey(pass, salt, iterations);

    // Generate a random IV for encryption
    const iv = window.crypto.getRandomValues(new Uint8Array(12));

    // Encrypt the text using the specified IV
    const encrypted = await window.crypto.subtle.encrypt(
        { name: "AES-GCM", iv: iv }, key, new TextEncoder().encode(data)
    );

    // Combine the IV and encrypted data into a single array
    const encryptedArray = new Uint8Array(encrypted);
    const resultArray = new Uint8Array(12 + encryptedArray.length);
    resultArray.set(iv);
    resultArray.set(encryptedArray, 12);

    // Convert the result to the selected output format
    let result;
    if (format === 'hex') {
        result = Array.from(resultArray, b => b.toString(16).padStart(2, '0')).join('');
    } else {
        result = btoa(String.fromCharCode.apply(null, resultArray));
    }

    var output = JSON.stringify({ "data": result, "iter": iterations, "salt": saltStr }, null, 2);

    document.getElementById(id).value = "Encrypted message: \n" + btoa(output);
    lengthCounter();
}


async function decrypt(data, pass, saltStr, iterations, id, format = "hex") {

    let encryptedArray;
    if (format === 'hex') {
        // Check that the input data is a valid hexadecimal string
        if (!/^[0-9a-fA-F]+$/.test(data)) {
            console.error("Error: Invalid input data format.");
            document.getElementById("output").value = "Error: Invalid input data format.";
            return;
        }
        encryptedArray = new Uint8Array(data.match(/.{1,2}/g).map(byte => parseInt(byte, 16)));
    } else {
        encryptedArray = new Uint8Array(atob(data).split('').map(c => c.charCodeAt(0)));
    }

    // Check that the input data contains the IV and encrypted data
    if (encryptedArray.length < 28) {
        console.error("Error: Invalid input data length.");
        return;
    }

    const iv = encryptedArray.slice(0, 12);
    const encrypted = encryptedArray.slice(12);
    const salt = new Uint8Array(new TextEncoder().encode(saltStr));
    const key = await deriveKey(pass, salt, iterations);

    try {
        const decrypted = await window.crypto.subtle.decrypt(
            { name: "AES-GCM", iv: iv, tagLength: 128 }, key, encrypted
        );
        const decryptedText = new TextDecoder().decode(decrypted);

        document.getElementById(id).innerHTML = "Decrypted message:<br>\n" + linkify(decryptedText);
    } catch (err) {
        console.error(err);
        alert("Error: Invalid decryption key or tampered data.");
        encryptedPasswords.delete(id);
    }
}


function encryptText() {
    id = "messageTxtArea";
    let el = document.getElementById(id);
    let plainTxt = el.value;
    let passwd = prompt("Enter encryption password:");
    encrypt(plainTxt, passwd, id);
    return false;
}


function decryptMessage(id) {
    let card_id = "card_" + id;
    let passwd = prompt("Enter encryption password:");
    encryptedPasswords.set(card_id, passwd);
    decryptMessageProcess(id);
    return false;
}

function decryptMessageProcess(id) {
    let card_id = "card_" + id;
    let el_source = document.getElementById("raw_" + card_id);
    let el_dest = document.getElementById(card_id);

    if (encryptedPasswords.has(card_id)) {
        let passwd = encryptedPasswords.get(card_id);

        if (passwd.length == 0)
            return false;
        let parts = el_source.value.split("\n");
        let json = JSON.parse(atob(parts[1]));
    
        decrypt(json["data"], passwd, json["salt"], json["iter"], card_id);
    }
}


