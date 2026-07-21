package com.lorenzon.e_commerce_api.exceptions;

import com.lorenzon.e_commerce_api.dto.ProblemDetailsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ProblemDetailsDTO detailsDTO = new ProblemDetailsDTO(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden",
                "User does not have permission to access this resource",
                "https://e-commerce-api.com/errors/user-forbidden",
                request.getRequestURI()
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(detailsDTO)
        );
    }
}
