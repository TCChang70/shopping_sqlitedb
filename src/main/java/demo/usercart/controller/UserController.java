package demo.usercart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import demo.usercart.model.JwtUtility;
import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin // 允許跨來源請求
public class UserController {
    List<Map<String, String>> users=new ArrayList<>();
    
    @Autowired
    private JwtUtility jwtUtil;

    public UserController() {
    	   users.add(Map.of("admin", "1234"));
    	   users.add(Map.of("guest", "1234"));
    	   users.add(Map.of("mary", "1234"));
    	   users.add(Map.of("george", "1234"));
    	   users.add(Map.of("john", "1234"));
    }
    @GetMapping("/login")
    public ModelAndView showLogin() {
    	    return new ModelAndView("usercart");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) {
        String username = payload.get("username");
        String password = payload.get("password");
        Map<String, String> u1=users.stream().filter(m->password.equals(m.get(username))).findAny().orElse(null);
        if (u1!=null) {
        	    session.setAttribute("loginname", username);
        	    String token = jwtUtil.generateToken(username);
        	    System.out.println(username+" 登入成功");
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("message", "帳號或密碼錯誤"));
        }
    }
}

