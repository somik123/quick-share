package org.somik.quick_share.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Logger;

import org.somik.quick_share.dto.MessageBoxDTO;
import org.somik.quick_share.dto.ResponseDTO;
import org.somik.quick_share.entity.Message;
import org.somik.quick_share.entity.MessageBox;
import org.somik.quick_share.repo.MessageBoxRepo;
import org.somik.quick_share.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageBoxServiceImpl implements MessageBoxService {
    private static final Logger LOG = Logger.getLogger(MessageBoxService.class.getName());

    @Autowired
    MessageBoxRepo messageBoxRepo;

    @Override
    public ResponseDTO createMessageBox(String msgBoxName, String msgBoxPass, String creatorIp) {
        if (msgBoxName == null || msgBoxName.length() < 3) {
            LOG.warning(String.format("Message box name [%s] not acceptable.", msgBoxName));
            return new ResponseDTO("FAIL", null, "Message box name too small");
        }

        MessageBox checkMessageBox = findMessageBox(msgBoxName, msgBoxPass);
        if (checkMessageBox != null) {
            LOG.info(String.format("Message box %s already exists with that password", msgBoxName));
            return new ResponseDTO("FAIL", null, "Message box by that name already exists.");
        }

        MessageBox newMessageBox = new MessageBox(msgBoxName, creatorIp, msgBoxPass);
        LOG.info(String.format("New message box [%s] by %s",
                newMessageBox.getName(), newMessageBox.getCreatorIp()));
        messageBoxRepo.save(newMessageBox);
        return new ResponseDTO("OK", convertMessageBoxToDto(newMessageBox));
    }

    @Override
    public ResponseDTO openMessageBox(String msgBoxName, String msgBoxPass) {
        MessageBox messageBox = findMessageBox(msgBoxName, msgBoxPass);
        if (messageBox == null) {
            LOG.info(String.format("Message box not found or %s password did not match", msgBoxName));
            return new ResponseDTO("FAIL", null, "Message box does not exist or incorrect password.");
        } else {
            LOG.info(String.format("Opening message box [%s] with %d messages.",
                    messageBox.getName(), messageBox.getMessageList().size()));
            return new ResponseDTO("OK", convertMessageBoxToDto(messageBox));
        }

    }

    @Override
    public ResponseDTO addMessageToBox(String msgBoxName, String msgBoxPass, String username, String message,
            int expiry, String creatorIp) {
        if (msgBoxName == null || msgBoxName.length() < 3) {
            LOG.warning(String.format("Message box name [%s] not acceptable.", msgBoxName));
            return new ResponseDTO("FAIL", null, "Message box name too small");
        }

        MessageBox messageBox = findMessageBox(msgBoxName, msgBoxPass);
        if (messageBox == null) {
            LOG.info(String.format("Message box not found or %s password did not match", msgBoxName));
            return new ResponseDTO("FAIL", convertMessageBoxToDto(messageBox),
                    "Message box does not exist or incorrect password.");
        } else {
            if (username == null || username.length() < 3) {
                LOG.warning(String.format("Username [%s] not acceptable.", username));
                return new ResponseDTO("FAIL", convertMessageBoxToDto(messageBox), "Username too small");
            } else if (message == null || message.length() < 3) {
                LOG.warning(String.format("Message [%s] not acceptable.", message));
                return new ResponseDTO("FAIL", convertMessageBoxToDto(messageBox), "Message too small");
            }
            LocalDateTime expiryTime = LocalDateTime.now().plus(expiry, ChronoUnit.HOURS)
                    .truncatedTo(ChronoUnit.SECONDS);
            Message newMessage = new Message(message, username, creatorIp, expiryTime);
            LOG.info(String.format("Adding message:\n%s\nTo box: \n%s", newMessage.toString(), msgBoxName));
            messageBox.addMessage(newMessage);
            messageBoxRepo.save(messageBox);
            return new ResponseDTO("OK", convertMessageBoxToDto(messageBox));
        }
    }

    @Override
    public ResponseDTO deleteMessageFromBox(String msgBoxName, String msgBoxPass, String messageDeleteCode) {
        MessageBox messageBox = findMessageBox(msgBoxName, msgBoxPass);
        if (messageBox == null) {
            LOG.info(String.format("Message box not found or %s password did not match", msgBoxName));
            return new ResponseDTO("FAIL", null, "Message box does not exist or incorrect password.");
        }
        if (messageBox.getMessageList() != null) {
            for (Message message : messageBox.getMessageList()) {
                if (message.getDeleteCode().equals(messageDeleteCode)) {
                    messageBox.removeMessage(message);
                    messageBoxRepo.save(messageBox);
                    return new ResponseDTO("OK", convertMessageBoxToDto(messageBox));
                }
            }
        }
        return new ResponseDTO("FAIL", null, "Message does not exist or incorrect delete code.");
    }

    private MessageBox findMessageBox(String msgBoxName, String msgBoxPass) {
        List<MessageBox> messageBoxList = messageBoxRepo.findByName(msgBoxName);
        if (messageBoxList != null && messageBoxList.size() > 0) {
            for (MessageBox messageBox : messageBoxList) {
                if (messageBox.getIsLocked()) {
                    if (CommonUtils.isPasswordCorrect(messageBox.getPassword(), msgBoxPass)) {
                        LOG.info(String.format("Locked message box %s found.", msgBoxName));
                        return messageBox;
                    }
                } else {
                    LOG.info(String.format("Unlocked message box %s found.", msgBoxName));
                    return messageBox;
                }
            }
        }
        LOG.info(String.format("Message box %s not found.", msgBoxName));
        return null;
    }

    private MessageBoxDTO convertMessageBoxToDto(MessageBox messageBox) {
        MessageBoxDTO messageBoxDTO = new MessageBoxDTO();
        messageBoxDTO.setName(messageBox.getName());
        messageBoxDTO.convertMessageListToDto(messageBox.getMessageList());
        return messageBoxDTO;
    }
}
