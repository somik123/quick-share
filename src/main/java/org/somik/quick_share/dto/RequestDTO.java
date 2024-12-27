package org.somik.quick_share.dto;

public class RequestDTO {
    private String msgBoxName;
    private String msgBoxPass;

    private String username;
    private String message;
    private int expiry;
    private String messageDeleteCode;

    public RequestDTO() {
    }

    public RequestDTO(String msgBoxName, String msgBoxPass, String username, String message, int expiry,
            String messageDeleteCode) {
        this.msgBoxName = msgBoxName;
        this.msgBoxPass = msgBoxPass;
        this.username = username;
        this.message = message;
        this.expiry = expiry;
        this.messageDeleteCode = messageDeleteCode;
    }

    public String getMsgBoxName() {
        return this.msgBoxName;
    }

    public void setMsgBoxName(String msgBoxName) {
        this.msgBoxName = msgBoxName;
    }

    public String getMsgBoxPass() {
        return this.msgBoxPass;
    }

    public void setMsgBoxPass(String msgBoxPass) {
        this.msgBoxPass = msgBoxPass;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getExpiry() {
        return this.expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }

    public String getMessageDeleteCode() {
        return this.messageDeleteCode;
    }

    public void setMessageDeleteCode(String messageDeleteCode) {
        this.messageDeleteCode = messageDeleteCode;
    }

    @Override
    public String toString() {
        return String.format(
                "{ msgBoxName='%s', msgBoxPass='%s', username='%s', message='%s', expiry='%d', messageDeleteCode='%s'}",
                getMsgBoxName(), getMsgBoxPass(), getUsername(), getMessage(), getExpiry(), getMessageDeleteCode());
    }

}
