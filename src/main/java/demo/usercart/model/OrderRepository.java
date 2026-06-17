package demo.usercart.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface OrderRepository extends JpaRepository<Order,Long>{
	List<Order> findByUsername(String username);
}
