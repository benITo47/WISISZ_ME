package me.wisisz.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.wisisz.service.JwtProvider;

@Component
public class JwtMiddleware extends OncePerRequestFilter {
    @Autowired
    JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        Boolean cond = path.startsWith("/api/me");
        if (cond) {
            String token = Optional.ofNullable(request.getHeader("Authorization"))
                    .filter(h -> h.startsWith("Bearer "))
                    .map(h -> h.substring(7))
                    .orElse(null);

            Integer personId = null;
            if (token != null && jwtProvider.isValid(token)) {
                try {
                    personId = Integer.valueOf(jwtProvider.getPersonId(token));
                } catch (Exception e) {
                }
                request.setAttribute("personId", personId);
            }

            if (personId == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Unauthorized\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
