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

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Nonnull
    @Column(length = 2000)
    private String message;

    private String creatorName;
    private String creatorIp;
    private LocalDateTime created;
    private LocalDateTime expiry;
    private String deleteCode;


    public Message() {
    }

    public Message(String message, String creatorName, String creatorIp, LocalDateTime expiry) {
        this.message = message;
        this.creatorName = creatorName;
        this.creatorIp = creatorIp;
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

    public String getCreatorIp() {
        return this.creatorIp;
    }

    public void setCreatorIp(String creatorIp) {
        this.creatorIp = creatorIp;
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
        return id == message.id && Objects.equals(deleteCode, message.deleteCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, creatorName, creatorIp, created, expiry, deleteCode);
    }

    @Override
    public String toString() {
        return String.format("{ id='%d', message='%s', creatorName='%s', creatorIp='%s', created='%s', expiry='%s', deleteCode='%s'}", getId(), getMessage(), getCreatorName(), getCreatorIp(), getCreated(), getExpiry(), getDeleteCode());
    }
    

}
