package com.hotwheels.dealer.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ClienteIdSessionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Solo procesa si el usuario está autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            HttpSession session = request.getSession();
            if (session.getAttribute("clienteId") == null) {
                // Asigna un clienteId basado en el nombre de usuario
                String username = authentication.getName();

                if ("user".equals(username)) {
                    session.setAttribute("clienteId", 1L);
                    session.setAttribute("clienteNombre", "Usuario Regular");
                    System.out.println("ClienteIdSessionFilter: Asignado clienteId 1 para usuario 'user'");
                } else if ("admin".equals(username)) {
                    session.setAttribute("clienteId", 2L);
                    session.setAttribute("clienteNombre", "Administrador");
                    System.out.println("ClienteIdSessionFilter: Asignado clienteId 2 para usuario 'admin'");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}