package org.somik.quick_share.entity;

import java.util.ArrayList;
import java.util.List;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.Objects;

import org.somik.quick_share.utils.CommonUtils;

@Entity
public class MessageBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String creatorIp;

    private boolean isLocked;
    private String password;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "messagebox_id", nullable = true)
    private List<Message> messageList;

    public MessageBox() {
    }

    public MessageBox(String name, String creatorIp, String password) {
        this.name = name;
        this.creatorIp = creatorIp;
        this.isLocked = password.length() > 0 ? true : false;
        this.password = CommonUtils.encryptPassword(password);
        this.messageList = new ArrayList<Message>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorIp() {
        return this.creatorIp;
    }

    public void setCreatorIp(String creatorIp) {
        this.creatorIp = creatorIp;
    }

    public boolean isIsLocked() {
        return this.isLocked;
    }

    public boolean getIsLocked() {
        return this.isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = CommonUtils.encryptPassword(password);
    }

    public List<Message> getMessageList() {
        return this.messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void addMessage(Message message) {
        this.messageList.add(message);
    }

    public void removeMessage(Message message) {
        this.messageList.remove(message);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MessageBox)) {
            return false;
        }
        MessageBox messageBox = (MessageBox) o;
        return id == messageBox.id && Objects.equals(name, messageBox.name)
                && Objects.equals(creatorIp, messageBox.creatorIp) && isLocked == messageBox.isLocked
                && Objects.equals(password, messageBox.password) && Objects.equals(messageList, messageBox.messageList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creatorIp, isLocked, password, messageList);
    }

    @Override
    public String toString() {
        return String.format("{ id='%d', name='%s', creator='%s', isLocked='%s', password='%s', messageList='%s'}",
                getId(), getName(), getCreatorIp(), isIsLocked(), getPassword(), getMessageList());
    }
}
