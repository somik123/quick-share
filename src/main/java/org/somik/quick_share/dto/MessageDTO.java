package org.somik.quick_share.dto;

public class MessageDTO {
    private String creatorHash;
    private String data;
    private String creatorName;
    private String created;
    private String expiry;
    private String deleteCode;

    public MessageDTO() {
    }

    public MessageDTO(String data, String creatorName, String creatorHash, String created, String expiry,
            String deleteCode) {
        this.data = data;
        this.creatorName = creatorName;
        this.creatorHash = creatorHash;
        this.created = created;
        this.expiry = expiry;
        this.deleteCode = deleteCode;
    }

    public String getCreatorHash() {
        return this.creatorHash;
    }

    public void setCreatorHash(String creatorHash) {
        this.creatorHash = creatorHash;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCreatorName() {
        return this.creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreated() {
        return this.created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getExpiry() {
        return this.expiry;
    }

    public void setExpiry(String expiry) {
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
        return String.format(
                "{ creatorHash=%s, data='%s', creatorName='%s', created='%s', expiry='%s', deleteCode='%s'}",
                getCreatorHash(), getData(), getCreatorName(), getCreated(), getExpiry(), getDeleteCode());
    }

}
