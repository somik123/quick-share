package org.somik.quick_share.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.UUID;

import org.somik.quick_share.utils.CommonUtils;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Nonnull
    @Column(length = 55000)
    private String data;

    private String creatorName;
    private String creatorIp;
    private String creatorHash;
    private LocalDateTime created;
    private LocalDateTime expiry;
    private String deleteCode;

    public Message() {
    }

    public Message(String data, String creatorName, String creatorIp, LocalDateTime expiry) {
        this.data = data;

        this.creatorName = creatorName;
        this.creatorIp = creatorIp;
        this.creatorHash = CommonUtils.generateIdenticonHash(creatorIp + creatorName);

        this.created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.expiry = expiry;
        this.deleteCode = UUID.randomUUID().toString();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCreatorIp() {
        return this.creatorIp;
    }

    public void setCreatorIp(String creatorIp) {
        this.creatorIp = creatorIp;
    }

    public String getCreatorHash() {
        return this.creatorHash;
    }

    public void setCreatorHash(String creatorHash) {
        this.creatorHash = creatorHash;
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
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Message)) {
            return false;
        }
        Message message = (Message) o;
        return id == message.id && Objects.equals(data, message.data)
                && Objects.equals(creatorName, message.creatorName) && Objects.equals(creatorIp, message.creatorIp)
                && Objects.equals(creatorHash, message.creatorHash) && Objects.equals(created, message.created)
                && Objects.equals(expiry, message.expiry) && Objects.equals(deleteCode, message.deleteCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, creatorName, creatorIp, creatorHash, created, expiry, deleteCode);
    }

    @Override
    public String toString() {
        return String.format(
                "{ id='%d', message='%s', creatorName='%s', creatorIp='%s', creatorHash='%s', created='%s', expiry='%s', deleteCode='%s'}",
                getId(), getData(), getCreatorName(), getCreatorIp(), getCreatorHash(), getCreated(), getExpiry(),
                getDeleteCode());
    }

}
