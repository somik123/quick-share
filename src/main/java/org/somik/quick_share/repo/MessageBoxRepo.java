package org.somik.quick_share.repo;

import java.util.List;

import org.somik.quick_share.entity.MessageBox;
import org.springframework.data.repository.CrudRepository;

public interface MessageBoxRepo extends CrudRepository<MessageBox, Integer> {
    List<MessageBox> findByName(String name);
}
