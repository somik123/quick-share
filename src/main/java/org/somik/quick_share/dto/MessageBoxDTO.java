package org.somik.quick_share.dto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.somik.quick_share.entity.Message;
import org.somik.quick_share.utils.PrettyTime;

public class MessageBoxDTO {

    private String name;
    private List<MessageDTO> messageList;

    public MessageBoxDTO() {
    }

    public MessageBoxDTO(String name, List<MessageDTO> messageList) {
        this.name = name;
        this.messageList = messageList;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MessageDTO> getMessageList() {
        return this.messageList;
    }

    public void setMessageList(List<MessageDTO> messageList) {
        this.messageList = messageList;
    }

    public void convertMessageListToDto(List<Message> messageList) {
        List<MessageDTO> messageDtoList = new ArrayList<>();
        if (messageList != null && messageList.size() > 0) {
            LocalDateTime now = LocalDateTime.now();

            for (Message message : messageList) {
                long created = ChronoUnit.MILLIS.between(now, message.getCreated());
                long expiry = ChronoUnit.MILLIS.between(now, message.getExpiry());

                MessageDTO messageDTO = new MessageDTO(message.getData(), message.getCreatorName(),
                        message.getCreatorHash(), PrettyTime.toDuration(created), PrettyTime.toDuration(expiry),
                        message.getDeleteCode());
                messageDtoList.add(messageDTO);
            }
        }
        this.messageList = messageDtoList;

    }

    @Override
    public String toString() {
        return String.format("{ name='%s', messageList='%s'}", getName(), getMessageList());
    }

}
