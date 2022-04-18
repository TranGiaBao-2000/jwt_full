package com.example.jwt.jwt;

import com.example.jwt.exception.exceptions.InValidTokenException;
import com.example.jwt.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class MyFilter extends OncePerRequestFilter {

    @Autowired
    private TokenHandler tokenHandler;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ArrayList<String> ignoreCheckToken = new ArrayList<>();
        ignoreCheckToken.add("/noauthen");
        ignoreCheckToken.add("/login");

        String uri = request.getRequestURI();
        if(ignoreCheckToken.contains(uri)){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String tokenFromFrontEnd = getTokenFromFrontEnd(request);

            // token ko đúng
            if(tokenFromFrontEnd == null || !tokenHandler.validateToken(tokenFromFrontEnd)){
                throw new InValidTokenException("Your Token is Invalid");
            }

            // xuống đc tới đây là token chuẩn rồi
            // lấy id user có trong token vì token này mình đang quy định là lưu id
            long id = tokenHandler.getUserIdByToken(tokenFromFrontEnd);

            // từ id lấy đc user có id này
            UserDetails userDetails = userService.loadUserById(id);
            if(userDetails !=null){
                System.out.println(userDetails.getAuthorities().toArray()[0].toString());
                UsernamePasswordAuthenticationToken
                        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        }catch(Exception exception){
            log.error("Fail authen: {}",exception.getMessage());
        }

    }

    public String getTokenFromFrontEnd(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token !=null && token.startsWith("Bearer ")){
            return token.substring(7);
        }
        return null;
    }
}
