package org.somik.quick_share.service;

import org.somik.quick_share.dto.ResponseDTO;

public interface MessageBoxService {
    ResponseDTO createMessageBox(String msgBoxName, String msgBoxPass, String creatorIp);

    ResponseDTO openMessageBox(String msgBoxName, String msgBoxPass);

    ResponseDTO addMessageToBox(String msgBoxName, String msgBoxPass, String username, String message, int expiry,
            String creatorIp);

    ResponseDTO deleteMessageFromBox(String msgBoxName, String msgBoxPass, String messageDeleteCode);

    void deleteExpiredMessages();
}
