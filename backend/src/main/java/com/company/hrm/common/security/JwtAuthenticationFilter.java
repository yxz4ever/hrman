package com.company.hrm.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String token = getTokenFromRequest(request);
        
        logger.debug("JWT Filter - Request URI: " + requestUri);
        logger.debug("JWT Filter - Token present: " + (token != null && !token.isEmpty()));

        try {
            // 验证 Token
            if (StringUtils.hasText(token)) {
                logger.debug("JWT Filter - Token is not empty, checking expiration...");
                
                if (jwtTokenUtil.isTokenExpired(token)) {
                    logger.warn("JWT Filter - Token has expired for URI: " + requestUri);
                } else {
                    String username = jwtTokenUtil.getUsernameFromToken(token);
                    logger.debug("JWT Filter - Username from token: " + username);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        logger.debug("JWT Filter - Loading user details for: " + username);
                        
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        logger.debug("JWT Filter - User details loaded successfully");

                        // 验证 Token 有效性
                        if (jwtTokenUtil.validateToken(token, username)) {
                            logger.debug("JWT Filter - Token validation successful, setting authentication");
                            
                            // 创建认证对象
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // 设置到 Security 上下文中
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("JWT Filter - Authentication set for URI: " + requestUri);
                        } else {
                            logger.warn("JWT Filter - Token validation failed for username: " + username);
                        }
                    } else {
                        logger.debug("JWT Filter - Skipping authentication (username null or already authenticated)");
                    }
                }
            } else {
                logger.debug("JWT Filter - No token in request header for URI: " + requestUri);
            }
        } catch (Exception e) {
            logger.error("JWT Filter - Exception while setting user authentication: " + e.getMessage() + " for URI: " + requestUri, e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
