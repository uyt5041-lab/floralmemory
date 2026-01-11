package com.bit.floralmemory.dto.ordering;

@SuppressWarnings("unused")
public class OrderingRunResponse {
    private Long orderingRunId;

    public OrderingRunResponse() {
    }

    public OrderingRunResponse(Long orderingRunId) {
        this.orderingRunId = orderingRunId;
    }

    public static OrderingRunResponseBuilder builder() {
        return new OrderingRunResponseBuilder();
    }

    public Long getOrderingRunId() {
        return orderingRunId;
    }

    public void setOrderingRunId(Long orderingRunId) {
        this.orderingRunId = orderingRunId;
    }

    public static class OrderingRunResponseBuilder {
        private Long orderingRunId;

        OrderingRunResponseBuilder() {
        }

        public OrderingRunResponseBuilder orderingRunId(Long orderingRunId) {
            this.orderingRunId = orderingRunId;
            return this;
        }

        public OrderingRunResponse build() {
            return new OrderingRunResponse(orderingRunId);
        }
    }
}
