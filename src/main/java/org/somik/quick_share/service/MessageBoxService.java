package org.somik.quick_share.service;

import java.util.List;

import org.somik.quick_share.dto.MessageBoxDTO;
import org.somik.quick_share.dto.ResponseDTO;
import org.springframework.security.core.userdetails.User;

public interface MessageBoxService {
    ResponseDTO createMessageBox(String msgBoxName, String msgBoxPass, String creatorIp);

    ResponseDTO openMessageBox(String msgBoxName, String msgBoxPass, User user);

    ResponseDTO addMessageToBox(String msgBoxName, String msgBoxPass, String username, String message, int expiry,
            String creatorIp);

    ResponseDTO deleteMessageFromBox(String msgBoxName, String msgBoxPass, String messageDeleteCode);

    ResponseDTO deleteMessageBox(String msgBoxName, String msgBoxPass);

    List<MessageBoxDTO> getAllMessageBoxes();

    void deleteExpiredMessages();
}
