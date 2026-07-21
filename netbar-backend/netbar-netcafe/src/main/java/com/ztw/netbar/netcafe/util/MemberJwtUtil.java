package com.ztw.netbar.netcafe.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 会员端 JWT 工具类（24小时有效，不存Redis）
 *
 * @author ruoyi
 */
public class MemberJwtUtil
{
    // 密钥（与 RuoYi 共用 token.secret）
    public static final String SECRET = "abcdefghijklmnopqrstuvwxyz";

    // 24小时（毫秒）
    public static final long EXPIRE_MILLIS = 24 * 60 * 60 * 1000L;

    /** 会员ID */
    public static final String CLAIM_MEMBER_ID = "memberId";
    /** 会员编号 */
    public static final String CLAIM_MEMBER_NO = "memberNo";
    /** 会员姓名 */
    public static final String CLAIM_NAME = "name";

    /**
     * 创建会员Token
     */
    public static String createToken(Long memberId, String memberNo, String name)
    {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_MILLIS);

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_MEMBER_ID, memberId);
        claims.put(CLAIM_MEMBER_NO, memberNo);
        claims.put(CLAIM_NAME, name);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(memberNo)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 解析Token
     */
    public static Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从Token获取会员ID
     */
    public static Long getMemberId(String token)
    {
        Claims claims = parseToken(token);
        Object memberId = claims.get(CLAIM_MEMBER_ID);
        if (memberId instanceof Integer)
        {
            return ((Integer) memberId).longValue();
        }
        return (Long) memberId;
    }

    /**
     * 验证Token是否有效
     */
    public static boolean validateToken(String token)
    {
        try
        {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
