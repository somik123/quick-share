package org.somik.quick_share.dto;

import java.time.LocalDateTime;

public class MessageDTO {
    private String message;
    private String creatorName;
    private LocalDateTime created;
    private LocalDateTime expiry;
    private String deleteCode;

    public MessageDTO() {
    }

    public MessageDTO(String message, String creatorName, LocalDateTime created, LocalDateTime expiry,
            String deleteCode) {
        this.message = message;
        this.creatorName = creatorName;
        this.created = created;
        this.expiry = expiry;
        this.deleteCode = deleteCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getExpiry() {
        return this.expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }

    public String getDeleteCode() {
        return this.deleteCode;
    }

    public void setDeleteCode(String deleteCode) {
        this.deleteCode = deleteCode;
    }

    @Override
    public String toString() {
        return String.format("{ message='%s', creatorName='%s', created='%s', expiry='%s', deleteCode='%s'}",
                getMessage(), getCreatorName(), getCreated(), getExpiry(), getDeleteCode());
    }

}
