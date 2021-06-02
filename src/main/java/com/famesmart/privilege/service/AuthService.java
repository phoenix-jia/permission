package com.famesmart.privilege.service;

import com.famesmart.privilege.entity.bo.LoginBO;
import com.famesmart.privilege.entity.vo.TokenVO;
import com.famesmart.privilege.security.JwtTokenProvider;
import com.famesmart.privilege.security.UserDetailsCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AuthService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LoginRecordsService loginRecordsService;

    public TokenVO login(LoginBO loginBO, HttpServletRequest request)  {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginBO.getUsername(), loginBO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsCustom userDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
        TokenVO tokenVO = jwtTokenProvider.createToken(userDetailsCustom);
        loginRecordsService.addLoginRecord(userDetailsCustom.getUsername(), getRemoteIP(request));
        return tokenVO;
    }

    public TokenVO refresh(UserDetailsCustom userDetailsCustom) {
        return jwtTokenProvider.createToken(userDetailsCustom);
    }

    public String getRemoteIP(HttpServletRequest request) {
        return request.getHeader("x-forwarded-for") == null ?
                request.getRemoteAddr() : request.getHeader("x-forwarded-for");
    }
}
