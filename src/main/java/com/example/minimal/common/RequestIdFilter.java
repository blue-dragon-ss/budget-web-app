package com.example.minimal.common;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.minimal.common.constants.ApiHeaders;
import com.example.minimal.common.constants.LogFields;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String rid = request.getHeader(ApiHeaders.REQUEST_ID);
        if (rid == null || rid.isBlank()) {
            rid = UUID.randomUUID().toString();
        }
        MDC.put(LogFields.RID, rid);
        response.setHeader(ApiHeaders.REQUEST_ID, rid);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(LogFields.RID);
        }
    }
}