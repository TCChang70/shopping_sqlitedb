package demo.usercart.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="productswithimage")
public class Product {
	
	@Id
	int id;
	@Column(length=255)
	String title;
	
	@Column(length=2048)
	String description;
	
	@Column(length=255)
	String category;	
	
	double price;
	
	@Column(length=255)
	String image;
	
	//@Lob
	//@Column(name = "picture", length = Integer.MAX_VALUE, nullable = true)
	//private byte[] picture;
	
	int rating_id;
	
    // 新增其他欄位如需

    // getters 和 setters
}


