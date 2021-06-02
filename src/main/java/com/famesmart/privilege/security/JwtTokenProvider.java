package com.famesmart.privilege.security;

import com.famesmart.privilege.entity.vo.TokenVO;
import com.famesmart.privilege.service.UserDetailsCustomService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

   static final String priKey =
           "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgi8avbVYsK0mK6jkT" +
           "nfdSXAd3YE7KHCSBiAtODHe66vmhRANCAATp8v7SNNjlvTs/366CUUR5Aasjk3BD" +
           "RPQk/KE8d3NNpsVCVt/1CrvHaAy2LxZiMei/ecf/GAsP8q6mob0MK8uE";


   static final String pubKey =
           "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE6fL+0jTY5b07P9+uglFEeQGrI5Nw" +
           "Q0T0JPyhPHdzTabFQlbf9Qq7x2gMti8WYjHov3nH/xgLD/KupqG9DCvLhA==";

  @Resource
  private UserDetailsCustomService userDetailsCustomService;

  private PrivateKey privateKey;

  private PublicKey publicKey;

  @PostConstruct
  protected void init() throws NoSuchAlgorithmException, InvalidKeySpecException {

    PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(priKey));
    KeyFactory keyFactory = KeyFactory.getInstance("EC");
    privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey));
    publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
  }

  public TokenVO createToken(UserDetailsCustom userDetailsCustom) {
    Date now = new Date();
    long validityInMilliseconds = 1800000L;
//    long validityInMilliseconds = 3214080000L;
    Date validity = new Date(now.getTime() + validityInMilliseconds);
    Date validityRefresh = new Date(now.getTime() + validityInMilliseconds * 2);

    String token = Jwts.builder()
            .setSubject(Integer.toString(userDetailsCustom.getId()))
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(privateKey)
            .compact();

    String refreshToken = Jwts.builder()
            .setSubject(Integer.toString(userDetailsCustom.getId()))
            .setIssuedAt(now)
            .setExpiration(validityRefresh)
            .signWith(privateKey)
            .compact();

    return new TokenVO(token, refreshToken);
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = userDetailsCustomService.loadUserById(getId(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getId(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

  public String resolveToken(HttpServletRequest req) {
    String token = req.getHeader("token");
    return StringUtils.isNotBlank(token) ? token : null;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
      return true;
    } catch (SecurityException ex) {
      throw new RuntimeException("invalid jwt signature");
    } catch (MalformedJwtException ex) {
      throw new RuntimeException("invalid jwt token");
    } catch (ExpiredJwtException ex) {
      throw new RuntimeException("expired jwt token");
    } catch (UnsupportedJwtException ex) {
      throw new RuntimeException("unsupported jwt token");
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException("jwt claims string is empty");
    }
  }
}
