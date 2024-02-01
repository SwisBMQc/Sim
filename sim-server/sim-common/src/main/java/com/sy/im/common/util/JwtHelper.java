package com.sy.im.common.util;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

public class JwtHelper {

    // 生成的token字符串的有效时长 3天 (ms)
    private static long tokenExpiration = 3 * 24 * 60 * 60 * 1000;

    // 签名加密的密钥
    private static String tokenSignKey = "123456";

    // 根据用户id和用户名称生成token字符串
    public static String createToken(String username) {
        String token = Jwts.builder()
                .setSubject("AUTH-USER")    // 分类
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)) // 设置有效时间

                // 主体部分
                .claim("username", username)

                // 签名部分
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
//            e.printStackTrace();
            System.err.println("jwt校验失败");
            return "";
        }
    }

    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDcwMjAyNjMx0FEqLU4tykvMTQWqK0ktLlGqBQDWTmVANgAAAA.CBwJ4yeAANNUc2InzKznzSFfQHuRVzwfz16oOCqg0BEoCTp02njc_Xn8EZcuhej9BCS-ndWfKI4SdnSLHRnogQ";
        System.out.println(token.length());
    }
}