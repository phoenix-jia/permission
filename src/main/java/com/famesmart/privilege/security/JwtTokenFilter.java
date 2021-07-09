package com.famesmart.privilege.security;

import com.famesmart.privilege.util.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest httpServletRequest,
                                  @NotNull HttpServletResponse httpServletResponse,
                                  @NotNull FilterChain filterChain)
          throws ServletException, IOException {
    String token = jwtTokenProvider.resolveToken(httpServletRequest);
    try {
      if (StringUtils.isNotBlank(token) && jwtTokenProvider.validateToken(token)) {
        Authentication auth = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    } catch (RuntimeException ex) {
      String contentType = "application/json; charset=UTF-8";
      httpServletResponse.setContentType(contentType);
      httpServletResponse.setStatus(403);
      httpServletResponse.getWriter().write(objectMapper.writeValueAsString(Result.error(ex.getMessage())));
      return;
    }

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

}
