package ru.yandex.practicum.stellaburgers.api.model;

public class CreateOrderResponse {
    private boolean success;
    private String name;
    OrderResponse order;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderResponse getOrder() {
        return order;
    }

    public void setOrderResponse(OrderResponse order) {
        this.order = order;
    }
}
