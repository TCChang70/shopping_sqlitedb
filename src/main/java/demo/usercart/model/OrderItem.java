package demo.usercart.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orderitems")
@Data
public class OrderItem {
    @Id   
	@GeneratedValue(strategy = GenerationType.IDENTITY)   
    private int id;
    private int pid;
    private String productTitle;
    private int productPrice;
    private int quantity;
    
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName="id")
    private Order order;

    // getters, setters
}

