package demo.usercart.model;

import java.util.*;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtility {
    private static final String SECRET = "MySecretKey";

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1天
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            String name= Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            if(name!=null)
            	   return true;
            else
            	   return false;
        } catch (Exception e) {
        	    System.out.println("validateToken error "+e.getMessage());
            return false;
        }
    }
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}

