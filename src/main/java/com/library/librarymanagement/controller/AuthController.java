package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.LoginRequest;
import com.library.librarymanagement.dto.RegisterRequest;
import com.library.librarymanagement.dto.UserResponse;
import com.library.librarymanagement.entity.User;
import com.library.librarymanagement.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setStudentClass(user.getStudentClass());
        resp.setSection(user.getSection());
        resp.setRollNo(user.getRollNo());
        resp.setUsername(user.getUsername());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request,
                                              HttpSession session,
                                              HttpServletRequest httpRequest,
                                              HttpServletResponse httpResponse) {
        User user = authService.login(request);
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setStudentClass(user.getStudentClass());
        resp.setSection(user.getSection());
        resp.setRollNo(user.getRollNo());
        resp.setUsername(user.getUsername());
        session.setAttribute("userId", user.getId());

        // Attach cross-site compatible session cookie
        boolean isHttps = isRequestHttps(httpRequest);
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
                .from("JSESSIONID", session.getId())
                .path("/")
                .httpOnly(true);

        if (isHttps) {
            cookieBuilder.secure(true).sameSite("None");
        } else {
            cookieBuilder.sameSite("Lax");
        }
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(HttpSession session,
                                           HttpServletRequest httpRequest,
                                           HttpServletResponse httpResponse) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        User user = authService.getUserById(userId);
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setName(user.getName());
        resp.setStudentClass(user.getStudentClass());
        resp.setSection(user.getSection());
        resp.setRollNo(user.getRollNo());
        resp.setUsername(user.getUsername());

        // Refresh session cookie header
        boolean isHttps = isRequestHttps(httpRequest);
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
                .from("JSESSIONID", session.getId())
                .path("/")
                .httpOnly(true);

        if (isHttps) {
            cookieBuilder.secure(true).sameSite("None");
        } else {
            cookieBuilder.sameSite("Lax");
        }
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session,
                                       HttpServletRequest httpRequest,
                                       HttpServletResponse httpResponse) {
        session.invalidate();

        boolean isHttps = isRequestHttps(httpRequest);
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
                .from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true);

        if (isHttps) {
            cookieBuilder.secure(true).sameSite("None");
        } else {
            cookieBuilder.sameSite("Lax");
        }
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());

        return ResponseEntity.ok().build();
    }

    private boolean isRequestHttps(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        return request.isSecure() ||
                "https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto")) ||
                "https".equalsIgnoreCase(request.getHeader("x-forwarded-proto")) ||
                (origin != null && origin.toLowerCase().startsWith("https://"));
    }
}
