package org.somik.quick_share.repo;

import java.util.List;

import org.somik.quick_share.entity.MessageBox;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MessageBoxRepo extends CrudRepository<MessageBox, Integer> {
    List<MessageBox> findByName(String name);

    @Query("SELECT m FROM MessageBox m")
    List<MessageBox> findAllMessageBoxes();
}
