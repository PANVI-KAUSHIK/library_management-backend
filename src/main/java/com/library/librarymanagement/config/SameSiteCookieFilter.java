package com.library.librarymanagement.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
            public void addCookie(Cookie cookie) {
                if (cookie != null && "JSESSIONID".equalsIgnoreCase(cookie.getName())) {
                    if (isRequestHttps(request)) {
                        cookie.setSecure(true);
                        cookie.setAttribute("SameSite", "None");
                    }
                }
                super.addCookie(cookie);
            }

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

        // Post-filter check: Ensure any Set-Cookie headers added directly by the container have SameSite=None; Secure
        if (isRequestHttps(request)) {
            Collection<String> headers = response.getHeaders("Set-Cookie");
            if (headers != null && !headers.isEmpty()) {
                List<String> updated = new ArrayList<>();
                boolean changed = false;
                for (String header : headers) {
                    String formatted = formatCookie(request, header);
                    if (!formatted.equals(header)) {
                        changed = true;
                    }
                    updated.add(formatted);
                }
                if (changed) {
                    response.setHeader("Set-Cookie", updated.get(0));
                    for (int i = 1; i < updated.size(); i++) {
                        response.addHeader("Set-Cookie", updated.get(i));
                    }
                }
            }
        }
    }

    private boolean isRequestHttps(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return request.isSecure() ||
                "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto")) ||
                "https".equalsIgnoreCase(request.getHeader("x-forwarded-proto")) ||
                (origin != null && origin.toLowerCase().startsWith("https://"));
    }

    private String formatCookie(HttpServletRequest request, String cookieHeader) {
        if (cookieHeader == null) {
            return null;
        }

        if (isRequestHttps(request) && cookieHeader.contains("JSESSIONID")) {
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
