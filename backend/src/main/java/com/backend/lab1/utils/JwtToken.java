/*
package com.backend.lab1.utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtToken {
    private static final String SECRET_KEY = "ваш_секретный_ключ"; // Замените на ваш секретный ключ
    private static final long EXPIRATION_TIME = 3600000; // 1 час в миллисекундах

    public static String generateToken(String username, String password) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + EXPIRATION_TIME;
        Date exp = new Date(expMillis);

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY);

        return builder.compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}

*/
