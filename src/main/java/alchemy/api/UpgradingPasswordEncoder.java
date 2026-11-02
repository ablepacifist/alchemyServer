package alchemy.api;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * BCrypt encoder that still accepts a legacy plaintext stored value.
 *
 * Some old player rows hold their password unhashed. A plain BCryptPasswordEncoder rejects those
 * (so the player simply cannot log in). This encoder verifies such a row once, in constant time,
 * and reports {@link #upgradeEncoding} = true so Spring Security replaces it with a BCrypt hash
 * right after the successful login. Nobody is locked out and nobody has to reset anything.
 * New passwords are always BCrypt.
 */
public class UpgradingPasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String stored) {
        if (rawPassword == null || stored == null) {
            return false;
        }
        if (isBcrypt(stored)) {
            return bcrypt.matches(rawPassword, stored);
        }
        return MessageDigest.isEqual(
                rawPassword.toString().getBytes(StandardCharsets.UTF_8),
                stored.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean upgradeEncoding(String stored) {
        return stored == null || !isBcrypt(stored) || bcrypt.upgradeEncoding(stored);
    }

    static boolean isBcrypt(String stored) {
        return stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$");
    }
}
