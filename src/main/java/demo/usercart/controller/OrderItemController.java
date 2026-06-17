package demo.usercart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.usercart.model.*;

import java.util.*;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins="*")
public class OrderItemController {
	@Autowired
	ItemRepository itemsRepo;
    @GetMapping("/{orderid}")
	public ResponseEntity<List<OrderItem>> getByOrderId(@PathVariable("orderid") long orderid){
	   List<OrderItem> items=itemsRepo.findByOrderId(orderid);
	   for(OrderItem i : items)
	   {
		   i.setOrder(null);
	   }
	   return ResponseEntity.ok(items);
   }
}
