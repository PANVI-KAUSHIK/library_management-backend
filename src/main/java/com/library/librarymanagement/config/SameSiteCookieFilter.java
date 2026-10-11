package com.library.librarymanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Ensures session cookies (JSESSIONID) are configured with SameSite=None; Secure
 * when accessed over HTTPS (e.g., deployed in production across domains on Vercel & Railway),
 * while allowing standard cookie behavior for local HTTP development.
 */
@Component
public class SameSiteCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        HttpServletResponse wrappedResponse = new HttpServletResponseWrapper(response) {
            @Override
            public void addHeader(String name, String value) {
                if ("Set-Cookie".equalsIgnoreCase(name)) {
                    value = formatCookie(request, value);
                }
                super.addHeader(name, value);
            }

            @Override
            public void setHeader(String name, String value) {
                if ("Set-Cookie".equalsIgnoreCase(name)) {
                    value = formatCookie(request, value);
                }
                super.setHeader(name, value);
            }
        };

        filterChain.doFilter(request, wrappedResponse);
    }

    private String formatCookie(HttpServletRequest request, String cookieHeader) {
        if (cookieHeader == null) {
            return null;
        }

        String origin = request.getHeader("Origin");
        boolean isHttps = request.isSecure() ||
                "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto")) ||
                "https".equalsIgnoreCase(request.getHeader("x-forwarded-proto")) ||
                (origin != null && origin.toLowerCase().startsWith("https://"));

        // When served over HTTPS in production across origins, append SameSite=None; Secure
        if (isHttps && cookieHeader.contains("JSESSIONID")) {
            String lower = cookieHeader.toLowerCase();
            StringBuilder sb = new StringBuilder(cookieHeader);
            if (!lower.contains("samesite")) {
                sb.append("; SameSite=None");
            }
            if (!lower.contains("secure")) {
                sb.append("; Secure");
            }
            return sb.toString();
        }

        return cookieHeader;
    }
}
