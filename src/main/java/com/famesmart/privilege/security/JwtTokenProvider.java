package com.famesmart.privilege.security;

import com.famesmart.privilege.entity.vo.TokenVO;
import com.famesmart.privilege.service.UserDetailsCustomService;
import io.jsonwebtoken.*;
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
           "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC8myrua/tCHnHL" +
           "hHoOOLTZwjZpSJX9HF0ijk6nILNiJZHTPZIhkw9xzA11sFp1JnLyp88iIgWqjNty" +
           "45dvs3a4X/VkIX1Mu0OwmV7rlbs1MEHwi2PqbffSygaVPPYgmnb2C6SqYuz62Sf+" +
           "Jry9s7XH7AAUGk5VL4/fpE8nfZ6HrE0dqYVZCVSw3D4vNATWV20R4UkkS/+Tjo6X" +
           "Tn1RiqD9E//FeiE4tamf64D8sUs/sO19IxlIOKloHgktMfaG5nO3p9dVUBOKssBt" +
           "N8IgJRIb9ozqqM18bqWWubD/b2iKzq/Vdq7DKCEcZx1OzDt6mpb+XerDjRA7UJ+D" +
           "ap9g5hEVAgMBAAECggEBAKE29Tx1OoEq6tQuufH8EMJuGvplnCeNniKSh7Kd5heH" +
           "y51H7Ogb/0CjTgICK2Al7G10eT5Q3I0K9+29ks1dFqbFB0l+ws7a5yJGBvfStgHh" +
           "7QtC6rYRJ5ubKoNYZ3446mSoUSl/g9dr1Z6N2BlDSqfIF80XK0vvasicdzhnTSHj" +
           "8PAilamnKzUxc1ttRQl69AjoHqd2k3CBCD4b5KTTPEh44iO/mgdOOXbUb5fMYRaf" +
           "5RzN4Vl9j0b+0hmC/Zz6oTToGI/kKE4El4624h/1TrnpPde6NvyBx89BjDfILyjQ" +
           "NozKfezinYgYNg37zBgzqmJclAwPTzQH0var8chHMCECgYEA9N1OJvhor+9Y83G2" +
           "T+ZXEA6ttb2cdeGeFKpbkoUQYMi/Qc7ZGpM91pMhqyhyW2DX2K03+SC7l6ZU4gRe" +
           "mq+wMeho6SPQwxmpppsXkuWuZBN12QeLvVnXDXdmk9Qjr4RpP3M3neBg47DiYjYM" +
           "rK/e57PxqfRy+WFQce227449h4kCgYEAxS7oHzBMGM5qSaKFdbIz9/bIzqIaKad2" +
           "ihRXMyCjEFrEe15FPIVlP7iuJiXv5sbDyXAf7N6QFxsTfsyQXkt6SI6LSnyWFcSg" +
           "08uPtWHne+/tFBZ6i7cM38cjWhtgh5jhyc10m6KAHx9+wwwgf40ntsarO7zy37pn" +
           "iPdjVk6dzi0CgYEA4IYwTCGI9JG2cXs6HWdrO4kKFVRoq9qWNgLDdsVWcDiCztfZ" +
           "ULnkgOVmgOfUfB9/q+mhImm2OWbF/HzfNl2UZR7jW6uAl/TnD6K0afj7AD6r17ZI" +
           "lvZS78ElKLXjhuQWEZ0XSUdd8EnYOw+ubYXSEYl5NUq1Rh/KQL0k+eHmX2kCgYEA" +
           "gQLqrpVMBKLxGdJy3YnFxMQ7qC6ZTY2a2kbqsrH03pt0mH8ab7mbgDCWCLGjn+uG" +
           "aAKrcLBBuExn2ft/raJWlKhm7EpNbFqlG03Bbdxrt0RIam82qLkQfE1vjOICq1VU" +
           "zVNWqwmMKFDUAEur66hwRn7/w+shQU5VtWCfJvXy+9UCgYAvLuIM+KmTX9/IILGR" +
           "ZKWlZ6wp7SJc9Cb4OH5o4I9tgJ1QmCXi7y+t3QyIyHuKbMeNNxTEgjPBWEVuO2zS" +
           "2OMPvxMj3eBDVSsbhcCFBADgMSo3U3BDpnW/eNI9PRL3pQsLEI7ZyPL+6VnZBVtL" +
           "JNEC+SzjH0DI2TChD6FckQEEKg==";

  static final String pubKey =
          "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvJsq7mv7Qh5xy4R6Dji0" +
          "2cI2aUiV/RxdIo5OpyCzYiWR0z2SIZMPccwNdbBadSZy8qfPIiIFqozbcuOXb7N2" +
          "uF/1ZCF9TLtDsJle65W7NTBB8Itj6m330soGlTz2IJp29gukqmLs+tkn/ia8vbO1" +
          "x+wAFBpOVS+P36RPJ32eh6xNHamFWQlUsNw+LzQE1ldtEeFJJEv/k46Ol059UYqg" +
          "/RP/xXohOLWpn+uA/LFLP7DtfSMZSDipaB4JLTH2huZzt6fXVVATirLAbTfCICUS" +
          "G/aM6qjNfG6llrmw/29ois6v1XauwyghHGcdTsw7epqW/l3qw40QO1Cfg2qfYOYR" +
          "FQIDAQAB";

  @Resource
  private UserDetailsCustomService userDetailsCustomService;

  private PrivateKey privateKey;

  private PublicKey publicKey;

  @PostConstruct
  protected void init() throws NoSuchAlgorithmException, InvalidKeySpecException {

    PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(priKey));
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubKey));
    publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
  }

  public TokenVO createToken(UserDetailsCustom userDetailsCustom) {
    Date now = new Date();
    long validityInMilliseconds = 1800000L;
//    long validityInMilliseconds = 3153600000000L;
    Date validity = new Date(now.getTime() + validityInMilliseconds);
    Date validityRefresh = new Date(now.getTime() + validityInMilliseconds * 2);

    String token = Jwts.builder()
            //.setSubject(Integer.toString(userDetailsCustom.getId()))
            .claim("username", userDetailsCustom.getUsername())
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(privateKey)
            .compact();

    String refreshToken = Jwts.builder()
            //.setSubject(Integer.toString(userDetailsCustom.getId()))
            .claim("username", userDetailsCustom.getUsername())
            .setIssuedAt(now)
            .setExpiration(validityRefresh)
            .signWith(privateKey)
            .compact();

    return new TokenVO(token, refreshToken);
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = userDetailsCustomService.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUsername(String token) {
    return Jwts
            .parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("username", String.class);
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
