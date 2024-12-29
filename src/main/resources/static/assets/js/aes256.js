function encrypt(clearTextData, hashedPwd, useIV) {
    var iv = null;
    if (useIV) {
        let randomBytes = CryptoJS.lib.WordArray.random(128 / 8).toString();
        iv = CryptoJS.enc.Hex.parse(randomBytes);
        // old method (wrong) which used first 16 bytes of key (hashed pwd)
        // CryptoJS.enc.Hex.parse(key);
        message = CryptoJS.AES.encrypt(clearTextData, CryptoJS.enc.Hex.parse(hashedPwd), { iv: iv });
        iv = message.iv;
    }
    else {
        message = CryptoJS.AES.encrypt(clearTextData, hashedPwd);
    }
    return { "data": message.toString(), "iv": iv };
}

function decrypt(encryptedData, hashedPwd, iv) {
    let code;
    if (iv != null) {
        code = CryptoJS.AES.decrypt(encryptedData, CryptoJS.enc.Hex.parse(hashedPwd), { iv: iv });
    }
    else {
        code = CryptoJS.AES.decrypt(encryptedData, hashedPwd);
    }
    let decryptedMessage = "";
    if (code.sigBytes < 0) {
        decryptedMessage = `Couldn't decrypt! It is probable that an incorrect password was used.`;
        throw new Error(decryptedMessage);
    }
    decryptedMessage = code.toString(CryptoJS.enc.Utf8);
    return decryptedMessage;
}

function generateHmac(hashedPwd, encryptedData, iv) {
    let hmac = sha256.hmac(`${iv}:${encryptedData}`, hashedPwd.toString());
    return hmac;
}

function validateMac(hashedPwd, encryptedData, iv, hmac) {
    let mac = sha256.hmac(`${iv}:${encryptedData}`, hashedPwd.toString());
    return (mac == hmac);
}

// Necessary steps
// ## 1. Generate random IV
// ## 2. Encrypt Data using clearText, AES256 & random IV
// ## 3. Add IV as part of message to user (IV can be known by all readers)
// ## 4. Hmac the entire message (IV & enrypted text) so the end user can know that the IV has not been changed.
