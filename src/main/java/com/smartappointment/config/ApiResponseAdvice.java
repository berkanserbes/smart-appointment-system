package com.smartappointment.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.smartappointment.dto.common.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice(basePackages = "com.smartappointment.controller")
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (body instanceof ApiResponse<?>) {
            return body;
        }

        int statusCode = resolveStatusCode(response);

        if (statusCode == HttpServletResponse.SC_NO_CONTENT) {
            return null;
        }

        if (body instanceof String) {
            return body;
        }

        String message = statusCode >= 200 && statusCode < 300
                ? "Request processed successfully"
                : "Request completed";

        return ApiResponse.success(statusCode, message, body, request.getURI().getPath());
    }

    private int resolveStatusCode(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse servletResponse) {
            return servletResponse.getServletResponse().getStatus();
        }
        return HttpServletResponse.SC_OK;
    }
}
