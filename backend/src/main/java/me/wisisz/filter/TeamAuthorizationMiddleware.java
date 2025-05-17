package me.wisisz.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.wisisz.service.TeamService;

@Order(2)
@Component
public class TeamAuthorizationMiddleware extends OncePerRequestFilter {

    @Autowired
    TeamService teamService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/api/me/teams/")) {
            // /api/me/team/{teamId}/...
            String[] segments = path.split("/");
            Integer teamId = null;
            try {
                teamId = Integer.parseInt(segments[4]);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Invalid team ID\"}");
                return;
            }

            Integer personId = (Integer) request.getAttribute("personId");
            if (personId == null || !teamService.isPersonInTeam(personId, teamId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"message\":\"Unauthorized\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
