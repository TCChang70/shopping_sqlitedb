package demo.usercart.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.usercart.model.*;
import java.util.*;
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins="*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;
	@Autowired
	JwtUtility JwtUtil;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order, @RequestHeader("Authorization") String token) {
        String username = JwtUtil.extractUsername(token.replace("Bearer ", ""));
        if (username == null)
               return ResponseEntity.status(401).build();
        order.setOrderTime(LocalDateTime.now());
        for (OrderItem item : order.getItems()) {
            item.setOrder(order);
        }

       // return ResponseEntity.ok(orderRepo.save(order));
        Order rs=orderRepo.save(order);
        for (OrderItem item : order.getItems()) {
            item.setOrder(null);
        }
        return ResponseEntity.ok(rs);
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable String username) {
    	List<Order> data=orderRepo.findByUsername(username);
    	data.forEach(o->o.setItems(null));
        return ResponseEntity.ok(data);
    }
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
    	List<Order> orders=orderRepo.findAll();
    	orders.replaceAll(o->{
    		o.setItems(null);
    		return o;
    	});
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/orderid/{orderid}")
    public ResponseEntity<Order> getOrdersById(@PathVariable("orderid") long orderid) {
    	Order order=orderRepo.findById(orderid).orElse(null);
    	if(order!=null)
    	  order.setItems(null);
        return ResponseEntity.ok(order);
    }
}

