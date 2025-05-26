package org.somik.quick_share.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.somik.quick_share.dto.MessageBoxDTO;
import org.somik.quick_share.dto.ResponseDTO;
import org.somik.quick_share.entity.Message;
import org.somik.quick_share.entity.MessageBox;
import org.somik.quick_share.repo.MessageBoxRepo;
import org.somik.quick_share.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class MessageBoxServiceImpl implements MessageBoxService {
    private static final Logger LOG = Logger.getLogger(MessageBoxService.class.getName());

    private static final String[] bannedNames = { "admin", "owner", "support", "login", "site", "quickshare" };

    @Autowired
    MessageBoxRepo messageBoxRepo;

    @Override
    public ResponseDTO createMessageBox(String msgBoxName, String msgBoxPass, String creatorIp) {
        if (msgBoxName == null || msgBoxName.length() < 3 || isNameBanned(msgBoxName)) {
            LOG.warning(String.format("Message box name [%s] not acceptable.", msgBoxName));
            return new ResponseDTO("FAIL", null, "Message box name not acceptable.");
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
    public ResponseDTO openMessageBox(String msgBoxName, String msgBoxPass, User user) {
        boolean isAdmin = false;
        String quickshareUser = System.getenv("QUICKSHARE_USER");
        if (user != null && user.getUsername().length() > 0 && user.getUsername().equals(quickshareUser)) {
            isAdmin = true;
        }

        MessageBox messageBox = findMessageBox(msgBoxName, msgBoxPass, isAdmin);

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
        if (msgBoxName == null || msgBoxName.length() < 3 || isNameBanned(msgBoxName)) {
            LOG.warning(String.format("Message box name [%s] not acceptable.", msgBoxName));
            return new ResponseDTO("FAIL", null, "Message box name not acceptable.");
        }

        MessageBox messageBox = findMessageBox(msgBoxName, msgBoxPass);
        if (messageBox == null) {
            LOG.info(String.format("Message box not found or %s password did not match", msgBoxName));
            return new ResponseDTO("FAIL", null,
                    "Message box does not exist or incorrect password.");
        } else {
            if (username == null || username.length() < 3 || isNameBanned(username)) {
                LOG.warning(String.format("Username [%s] not acceptable.", username));
                return new ResponseDTO("FAIL", convertMessageBoxToDto(messageBox), "Username not acceptable.");
            } else if (message == null || message.length() < 5 || message.length() > 50000) {
                LOG.warning(String.format("Message [%s] not acceptable.", message));
                return new ResponseDTO("FAIL", convertMessageBoxToDto(messageBox), "Message size not acceptable.");
            } else if (expiry <= 0 || expiry > 5259600) {
                LOG.warning(String.format("Expiry of %d mins not acceptable.", expiry));
                return new ResponseDTO("FAIL", convertMessageBoxToDto(messageBox), "Invalid expiry time.");
            }
            LocalDateTime expiryTime = LocalDateTime.now().plus(expiry, ChronoUnit.MINUTES)
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

    @Override
    public ResponseDTO deleteMessageBox(String msgBoxName, String msgBoxPass) {
        MessageBox messageBox = findMessageBox(msgBoxName, msgBoxPass);
        if (messageBox == null) {
            LOG.info(String.format("Message box not found or %s password did not match", msgBoxName));
            return new ResponseDTO("FAIL", null, "Message box does not exist or incorrect password.");
        }
        LOG.info(String.format("Deleting message box [%s] with %d messages.",
                messageBox.getName(), messageBox.getMessageList().size()));
        messageBoxRepo.delete(messageBox);

        return new ResponseDTO("OK", convertMessageBoxToDto(messageBox), "Message box deleted successfully.");
    }

    @Override
    public List<MessageBoxDTO> getAllMessageBoxes() {
        List<MessageBox> messageBoxList = messageBoxRepo.findAllMessageBoxes();
        if (messageBoxList == null || messageBoxList.size() == 0) {
            LOG.info("No message boxes found.");
            return null;
        }
        LOG.info(String.format("Found %d message boxes.", messageBoxList.size()));
        List<MessageBoxDTO> messageBoxDTOList = new ArrayList<>();
        for (MessageBox messageBox : messageBoxList) {
            MessageBoxDTO messageBoxDTO = new MessageBoxDTO();
            messageBoxDTO.setName(messageBox.getName());
            messageBoxDTOList.add(messageBoxDTO);
        }
        return messageBoxDTOList;
    }

    @Override
    public void deleteExpiredMessages() {
        LocalDateTime now = LocalDateTime.now();
        List<MessageBox> mesageBoxList = messageBoxRepo.findAllMessageBoxes();
        for (MessageBox messageBox : mesageBoxList) {

            List<Message> messageList = messageBox.getMessageList();

            if (messageList == null || mesageBoxList.size() == 0) {
                LOG.info("Skipping");
                continue;
            }

            int count = 0;
            Iterator<Message> messageIterator = messageList.iterator();
            while (messageIterator.hasNext()) {
                Message message = messageIterator.next();
                if (now.isAfter(message.getExpiry())) {
                    messageIterator.remove();
                    count++;
                }
            }
            messageBoxRepo.save(messageBox);
            LOG.info(String.format("From message box %s deleted %d expired messages.", messageBox.getName(), count));
        }
    }

    private MessageBox findMessageBox(String msgBoxName, String msgBoxPass) {
        return findMessageBox(msgBoxName, msgBoxPass, false);
    }

    private MessageBox findMessageBox(String msgBoxName, String msgBoxPass, boolean isAdmin) {
        List<MessageBox> messageBoxList = messageBoxRepo.findByName(msgBoxName);
        if (messageBoxList != null && messageBoxList.size() > 0) {

            // Only open the first message box found
            MessageBox messageBox = messageBoxList.get(0);
            if (messageBox.getIsLocked()) {
                if (isAdmin) {
                    LOG.info(String.format("[Admin] Locked message box %s found.", msgBoxName));
                    return messageBox;
                } else if (CommonUtils.isPasswordCorrect(messageBox.getPassword(), msgBoxPass)) {
                    LOG.info(String.format("Locked message box %s found.", msgBoxName));
                    return messageBox;
                } else {
                    LOG.info(String.format("Message box %s found but password did not match.", msgBoxName));
                    return null;
                }
            } else {
                LOG.info(String.format("Unlocked message box %s found.", msgBoxName));
                return messageBox;
            }

        }
        LOG.info(String.format("Message box %s not found.", msgBoxName));
        return null;
    }

    private MessageBoxDTO convertMessageBoxToDto(MessageBox messageBox) {
        if (messageBox == null)
            return null;
        MessageBoxDTO messageBoxDTO = new MessageBoxDTO();
        messageBoxDTO.setName(messageBox.getName());
        messageBoxDTO.convertMessageListToDto(messageBox.getMessageList());
        return messageBoxDTO;
    }

    private boolean isNameBanned(String name) {
        for (int i = 0; i < bannedNames.length; i++) {
            if (name.toLowerCase().contains(bannedNames[i].toLowerCase()))
                return true;
        }
        return false;
    }

}
