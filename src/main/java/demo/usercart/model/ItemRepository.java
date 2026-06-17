package demo.usercart.model;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<OrderItem,Long> {
     public List<OrderItem> findByOrderId(long id);
}
