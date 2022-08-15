package ru.yandex.practicum.stellaburgers.api.model;

public class LoginUserResponse {
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private User user;

    public LoginUserResponse(Boolean success, String accessToken, String refreshToken, User user) {
        this.success = success;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }
}
