package org.somik.quick_share.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import org.somik.quick_share.entity.Message;
import org.somik.quick_share.repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    Logger LOG = Logger.getLogger(MessageServiceImpl.class.getName());

    @Autowired
    MessageRepo messageRepo;

    @Override
    public void deleteExpiredMessages() {
        LocalDateTime now = LocalDateTime.now();
        List<Message> messageList = messageRepo.findAllMessages();
        int count = 0;
        for (Message message : messageList) {
            if (now.isAfter(message.getExpiry())) {
                messageRepo.delete(message);
                count++;
            }
        }
        LOG.info(String.format("Deleted %d of %d expired messages.", count, (messageList.size() - 1)));
    }

}
