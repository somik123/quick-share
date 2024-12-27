package org.somik.quick_share.repo;

import java.util.List;

import org.somik.quick_share.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepo extends CrudRepository<Message, Integer> {
    @Query("SELECT m FROM Message m")
    List<Message> findAllMessages();
}
