package com.bit.floralmemory.dto.common;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().success(true).data(data).error(null).build();
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .error(new ApiError(code, message))
                .build();
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class ApiError {
        private String code;
        private String message;
    }
}
