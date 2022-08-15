package ru.yandex.practicum.stellaburgers.api.model;

public class ChangeUserDataResponse {
    private boolean success;
    private User user;

    public ChangeUserDataResponse(Boolean success, User user) {
        this.success = success;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }
}
