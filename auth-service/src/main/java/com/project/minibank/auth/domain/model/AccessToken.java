package com.project.minibank.auth.domain.model;

public record AccessToken(String value, String type, long expiresInSeconds) {
}
