package com.mycompany.lab5.auth.helper;

public class LoginResponse {
    private boolean success;
    private String token;
    private String sessionMarker;
    private String message;

    public LoginResponse() {}
    public LoginResponse(boolean success, String token, String sessionMarker, String message) {
        this.success = success; this.token = token; this.sessionMarker = sessionMarker; this.message = message;
    }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getSessionMarker() { return sessionMarker; }
    public void setSessionMarker(String sessionMarker) { this.sessionMarker = sessionMarker; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
