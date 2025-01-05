package org.somik.quick_share.repo;

import org.somik.quick_share.entity.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepo extends CrudRepository<Message, Integer> {

}
