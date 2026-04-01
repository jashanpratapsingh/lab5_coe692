package com.mycompany.lab5.auth.business;

import com.mycompany.lab5.auth.helper.LoginResponse;
import com.mycompany.lab5.auth.persistence.UserStore;
import com.mycompany.lab5.security.JwtUtil;

public class AuthManager {
    private final UserStore store = new UserStore();

    public LoginResponse login(String username, String password) {
        if (store.validate(username, password)) {
            String token = JwtUtil.createToken(username);
            return new LoginResponse(true, token, "Login successful");
        }
        return new LoginResponse(false, "", "Invalid credentials");
    }

    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);
    }
}
