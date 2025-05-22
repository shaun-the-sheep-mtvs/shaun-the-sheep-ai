package org.mtvs.backend.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.auth.service.CustomUserDetailsService;
import org.mtvs.backend.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // 이해가 완전 안됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        // 클라이언트에서 Authorization: Bearer {token} 헤더를 보내지 않았음!
        // logger.info(authHeader); -> Bearer null
        // checklist 프론트 내
        //    fetch('http://localhost:8080/api/checklist', {
        //      //
        //      method: 'POST', << GET 방식으로 교체


        // 토큰이 유효하지 않음
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("token: " + token);


            if (jwtUtil.validateToken(token)) {
               Long userId = jwtUtil.getUserId(token);
                   // 인증 객체 생성 및 SecurityContext에 설정
//                userEmail이아니라 UserDetail을 저장하게 설정. 단 이생성자에 사용되는녀석은 manager나 provider에의해 구현된녀석이다 (인증토큰을만족하기위해)
//                        manager 구현할것 //provider
                System.out.println("userId = " + userId);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(userId));
                System.out.println("userDetails = " + userDetails);
                UsernamePasswordAuthenticationToken authToken =
                      new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList());
                //setAuthentication(Authentication 토큰)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}