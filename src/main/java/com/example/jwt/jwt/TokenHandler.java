package com.example.jwt.jwt;

import com.example.jwt.user.CustomerDetails;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class TokenHandler {

    private final String SECRET_KEY = "GiaBao";

    private final long JWT_EXPIRATION = 200000L;


    //genarate token cho user nào chưa có token
    public String generateToken(CustomerDetails customerDetails){
        Date now = new Date();
        Date expirationDate = new Date(now.getTime()+JWT_EXPIRATION);
        String token = Jwts.builder()
                .setSubject(Long.toString(customerDetails.getUser().getId()))
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        return token;
    }

    //lấy user id từ token
    public long getUserIdByToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    // check xem token của người dùng có chuẩn hay là ko
    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
