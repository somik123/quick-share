package org.somik.quick_share.dto;

import java.util.ArrayList;
import java.util.List;

import org.somik.quick_share.entity.Message;

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
        for (Message message : messageList) {
            MessageDTO messageDTO = new MessageDTO(message.getMessage(), message.getCreatorName(), message.getCreated(),
                    message.getExpiry(), message.getDeleteCode());
            messageDtoList.add(messageDTO);
        }
        this.messageList = messageDtoList;

    }

    @Override
    public String toString() {
        return String.format("{ name='%s', messageList='%s'}", getName(), getMessageList());
    }

}
