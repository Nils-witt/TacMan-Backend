package dev.nilswitt.webmap.security;

public interface PasswordResetEmailSender {
    void sendPasswordResetEmail(String toEmail, String displayName, String resetLink, long ttlMinutes);
}

