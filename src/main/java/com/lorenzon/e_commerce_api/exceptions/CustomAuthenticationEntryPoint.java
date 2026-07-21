package com.lorenzon.e_commerce_api.exceptions;

import com.lorenzon.e_commerce_api.dto.ProblemDetailsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ProblemDetailsDTO detailsDTO = new ProblemDetailsDTO(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized",
                "Unauthenticated user",
                "https://e-commerce-api.com/errors/user-unauthorized",
                request.getRequestURI()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(detailsDTO)
        );
    }
}
